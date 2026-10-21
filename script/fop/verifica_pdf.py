#!/usr/bin/env python3
"""
Verifica un PDF generato con FOP rispetto al PDF di riferimento generato con Jasper (issue #37).

Criteri di accettazione (ogni criterio deve passare, su ogni pagina):
  0. pagine     stesso numero di pagine
  1. testo      stesse righe con le stesse parole, quindi stessi a capo
  2. inchiostro per ogni riga di testo, la posizione dell'inchiostro (bordi superiore, inferiore,
                sinistro, destro) differisce al massimo di 0,25 pt; misurata a 600 dpi
  3. raster     a 200 dpi, con tolleranza di 1 pixel (circa 0,36 pt), al massimo 5 pixel scuri di
                uno dei due PDF senza corrispondente nell'altro (rumore di rasterizzazione), escluse
                le zone delle immagini
  4. immagini   per ogni immagine il riquadro dell'inchiostro differisce al massimo di 1 pt
  5. codici     tutti i qr_code e data_matrix dell'XML si leggono, in qualche pagina, nel PDF FOP
                (opzionale, con --xml e --classpath)

Le posizioni delle parole vengono da pdftotext, ma sono usate solo per trovare le righe: Jasper e
FOP descrivono le metriche del font in modo diverso, quindi lo scarto si misura sull'inchiostro.
Le zone delle immagini si ricavano dal file FO del layout: sono i block-container il cui blocco ha
corpo 0, come li genera jrxml2fo.py per immagini, QR code e DataMatrix. Le posizioni sono sommate
lungo i block-container annidati (le rate dei sottoreport) e ogni page-sequence e' una pagina.

Uso:
  verifica_pdf.py JASPER.pdf FOP.pdf --fo LAYOUT.fo [--xml INPUT.xml --classpath CP] [--escludi X,Y,W,H]...

  --classpath   classpath con zxing (per esempio da mvn dependency:build-classpath), per il criterio 5
  --escludi     zona in punti esclusa dal criterio raster: per le differenze accettate e documentate
                (per esempio il grassetto che Jasper simula su TitilliumWeb-Bold)

Richiede poppler-utils (pdftotext, pdftoppm) e Pillow. Esce con codice 1 se un criterio non passa.
"""

import argparse
import re
import subprocess
import sys
import tempfile
from collections import defaultdict
from pathlib import Path

from PIL import Image, ImageChops, ImageFilter

SOGLIA_INCHIOSTRO = 0.25   # pt
SOGLIA_IMMAGINI = 1.0      # pt
SOGLIA_RASTER = 5          # pixel isolati a 200 dpi
DPI_INCHIOSTRO = 600
DPI_RASTER = 200

LEGGI_CODICI = """
import java.io.File; import java.util.Map; import javax.imageio.ImageIO; import com.google.zxing.*;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource; import com.google.zxing.common.HybridBinarizer;
public class LeggiCodici { public static void main(String[] a) throws Exception {
  for (String f : a) {
    String letto = "";
    try { letto = new MultiFormatReader().decode(new BinaryBitmap(new HybridBinarizer(new BufferedImageLuminanceSource(ImageIO.read(new File(f))))),
        Map.of(DecodeHintType.TRY_HARDER, true, DecodeHintType.POSSIBLE_FORMATS, java.util.List.of(BarcodeFormat.QR_CODE, BarcodeFormat.DATA_MATRIX))).getText();
    } catch (NotFoundException e) { }
    System.out.print(java.util.Base64.getEncoder().encodeToString(letto.getBytes(java.nio.charset.StandardCharsets.UTF_8)) + "\\n"); } } }
"""


def pagine(pdf):
    out = subprocess.run(['pdfinfo', pdf], capture_output=True, text=True, check=True).stdout
    return int(re.search(r'^Pages:\s+(\d+)', out, re.M).group(1))


def parole(pdf, pagina):
    out = subprocess.run(['pdftotext', '-f', str(pagina), '-l', str(pagina), '-bbox', pdf, '-'], capture_output=True, text=True, check=True).stdout
    return [(m.group(5), *map(float, m.group(1, 2, 3, 4)))
            for m in re.finditer(r'<word xMin="([\d.]+)" yMin="([\d.]+)" xMax="([\d.]+)" yMax="([\d.]+)">([^<]*)</word>', out)]


def righe(ws):
    """Parole raggruppate per riga: centro verticale entro 2,5 pt e vicine in orizzontale."""
    gruppi = []
    # centro verticale entro 2,5 pt: le caselle di font diversi sulla stessa riga hanno yMin diversi
    # (ogni font ha la sua ascendente), mentre due righe vere distano almeno 6 pt
    centro = lambda w: (w[2] + w[4]) / 2
    for w in sorted(ws, key=centro):
        if gruppi and abs(centro(w) - centro(gruppi[-1][0])) < 2.5:
            gruppi[-1].append(w)
        else:
            gruppi.append([w])
    risultato = []
    for g in gruppi:
        g.sort(key=lambda w: w[1])
        corrente = [g[0]]
        for w in g[1:]:
            # parole distanti piu' di 8 pt: elementi diversi sulla stessa riga. La soglia e' fissa perche'
            # l'altezza delle caselle dipende da come ogni PDF descrive il font; tra due parole dello
            # stesso testo c'e' uno spazio di 2-4 pt
            if w[1] - corrente[-1][3] > 8:
                risultato.append(corrente); corrente = []
            corrente.append(w)
        risultato.append(corrente)
    return [(' '.join(w[0] for w in r), min(w[1] for w in r), min(w[2] for w in r),
             max(w[3] for w in r), max(w[4] for w in r)) for r in risultato]


def zone_immagini(fo):
    """Zone delle immagini per pagina (una page-sequence per pagina), in punti dal bordo della pagina."""
    import xml.etree.ElementTree as ET
    FO = '{http://www.w3.org/1999/XSL/Format}'
    num = lambda v: float(v[:-2]) if v and v.endswith('pt') else 0.0
    risultato = []

    def visita(el, ox, oy, zone):
        for c in el:
            if c.tag == f'{FO}block-container' and c.get('absolute-position') == 'absolute':
                x, y = ox + num(c.get('left')), oy + num(c.get('top'))
                primo = next(iter(c), None)
                if primo is not None and primo.tag == f'{FO}block' and primo.get('font-size') == '0pt' \
                        and c.get('width') and c.get('height'):
                    zone.append((x, y, num(c.get('width')), num(c.get('height'))))
                    continue
                visita(c, x, y, zone)
            else:
                visita(c, ox, oy, zone)

    for ps in ET.parse(fo).getroot().iter(f'{FO}page-sequence'):
        zone = []
        visita(ps, 0.0, 0.0, zone)
        risultato.append(zone)
    return risultato


def raster(pdf, dpi, cartella, nome, pagina):
    base = Path(cartella) / f'{nome}-{pagina}'
    subprocess.run(['pdftoppm', '-r', str(dpi), '-f', str(pagina), '-l', str(pagina), '-gray', '-png', '-singlefile', pdf, str(base)], check=True)
    # pdftoppm scrive comunque PNG RGB: conversione in scala di grigi
    return Image.open(f'{base}.png').convert('L')


def bordo_inchiostro(im, k, x, y, w, h, soglia=128):
    b = im.crop((int(x * k), int(y * k), int((x + w) * k), int((y + h) * k))).point(lambda v: 255 if v < soglia else 0).getbbox()
    return None if b is None else (x + b[0] / k, y + b[1] / k, x + b[2] / k, y + b[3] / k)


def verifica_pagina(a, pagina, zone, escluse, tmp):
    esiti = {}

    # 1. testo
    rj, rf = righe(parole(a.jasper, pagina)), righe(parole(a.fop, pagina))
    testi_f = defaultdict(list)
    for r in rf:
        testi_f[r[0]].append(r)
    mancanti = [r[0] for r in rj if not testi_f.get(r[0])]
    in_piu = [r[0] for r in rf if r[0] not in {x[0] for x in rj}]
    esiti['testo'] = (not mancanti and not in_piu, f'{len(rj)} righe' + (f', diverse: {(mancanti + in_piu)[:3]}' if mancanti or in_piu else ''))

    # 2. inchiostro per riga
    k = DPI_INCHIOSTRO / 72
    ij, if_ = raster(a.jasper, DPI_INCHIOSTRO, tmp, 'j', pagina), raster(a.fop, DPI_INCHIOSTRO, tmp, 'f', pagina)
    peggiori = []
    for testo, x0, y0, x1, y1 in rj:
        cand = testi_f.get(testo)
        if not cand:
            continue
        # area della riga di Jasper, allargata di 1,5 pt per comprendere anche l'inchiostro di FOP se
        # spostato, ma non oltre meta' strada dalle righe sopra e sotto che la sovrappongono in orizzontale:
        # con l'interlinea stretta l'area toccherebbe le ascendenti della riga sotto, e uno scarto di un
        # pixel di quella riga cambierebbe la misura di questa
        alto, basso = y0 - 1.5, y1 + 1.5
        for _, a0, b0, a1, b1 in rj:
            if a0 < x1 and a1 > x0 and (b0, b1) != (y0, y1):
                if y0 < b0 < basso:
                    basso = min(basso, (y1 + b0) / 2 + 0.5)
                if alto < b1 < y1 and b0 < y0:
                    alto = max(alto, (b1 + y0) / 2 - 0.5)
        bj = bordo_inchiostro(ij, k, x0 - 1, alto, x1 - x0 + 2, basso - alto)
        bf = bordo_inchiostro(if_, k, x0 - 1, alto, x1 - x0 + 2, basso - alto)
        if bj and bf:
            scarto = max(abs(p - q) for p, q in zip(bj, bf))
            peggiori.append((scarto, testo, tuple(round(q - p, 2) for p, q in zip(bj, bf))))
    peggiori.sort(reverse=True)
    massimo = peggiori[0][0] if peggiori else 0
    esiti['inchiostro'] = (massimo <= SOGLIA_INCHIOSTRO, f'scarto massimo {massimo:.2f} pt' +
                           ''.join(f'\n      {s:.2f} pt {t[:40]!r} (sinistra, alto, destra, basso: {d})' for s, t, d in peggiori[:max(3, a.dettagli)] if s > SOGLIA_INCHIOSTRO or a.dettagli))

    # 3. raster con tolleranza, fuori dalle immagini
    k2 = DPI_RASTER / 72
    rj2 = raster(a.jasper, DPI_RASTER, tmp, 'j2', pagina).point(lambda v: 255 if v < 160 else 0)
    rf2 = raster(a.fop, DPI_RASTER, tmp, 'f2', pagina).point(lambda v: 255 if v < 160 else 0)
    diff = ImageChops.lighter(ImageChops.subtract(rj2, rf2.filter(ImageFilter.MaxFilter(3))),
                              ImageChops.subtract(rf2, rj2.filter(ImageFilter.MaxFilter(3))))
    for x, y, w, h in zone + escluse:
        diff.paste(0, (int(x * k2) - 2, int(y * k2) - 2, int((x + w) * k2) + 2, int((y + h) * k2) + 2))
    fuori = sum(diff.histogram()[1:])
    dettaglio = f'{fuori} pixel fuori tolleranza (escluse {len(zone)} immagini' + (f' e {len(escluse)} zone indicate)' if escluse else ')')
    if fuori:
        # zone di 10 pt con piu' pixel fuori tolleranza, per la diagnosi
        px, (larg, alt) = diff.load(), diff.size
        conta = defaultdict(int)
        for yy in range(alt):
            for xx in range(larg):
                if px[xx, yy]:
                    conta[(int(xx / k2) // 10 * 10, int(yy / k2) // 10 * 10)] += 1
        dettaglio += ''.join(f'\n      {n} pixel in x {x}-{x + 10} pt, y {y}-{y + 10} pt'
                             for (x, y), n in sorted(conta.items(), key=lambda kv: -kv[1])[:3])
    esiti['raster'] = (fuori <= SOGLIA_RASTER, dettaglio)

    # 4. immagini
    scarti = []
    k3 = 300 / 72
    ij3, if3 = raster(a.jasper, 300, tmp, 'j3', pagina), raster(a.fop, 300, tmp, 'f3', pagina)
    for x, y, w, h in zone:
        bj = bordo_inchiostro(ij3, k3, x - 2, y - 2, w + 4, h + 4, 200)
        bf = bordo_inchiostro(if3, k3, x - 2, y - 2, w + 4, h + 4, 200)
        if bj and bf:
            scarti.append((max(abs(p - q) for p, q in zip(bj, bf)), (x, y)))
    massimo = max((s for s, _ in scarti), default=0)
    esiti['immagini'] = (massimo <= SOGLIA_IMMAGINI, f'{len(scarti)} immagini, scarto massimo {massimo:.2f} pt' +
                         ''.join(f'\n      {s:.2f} pt immagine in ({p[0]:.0f}, {p[1]:.0f})' for s, p in scarti if s > SOGLIA_IMMAGINI))

    # riquadri quadrati (QR code e DataMatrix) ritagliati dalla pagina FOP, letti tutti insieme alla fine
    png = []
    for i, (x, y, w, h_) in enumerate(z for z in zone if abs(z[2] - z[3]) < 0.5 and z[2] >= 40):
        f = Path(tmp) / f'codice{pagina}-{i}.png'
        if3.crop((int((x - 5) * k3), int((y - 5) * k3), int((x + w + 5) * k3), int((y + h_ + 5) * k3))).save(f); png.append(str(f))
    misure = {'inchiostro': peggiori[0][0] if peggiori else 0, 'raster': fuori, 'immagini': max((s for s, _ in scarti), default=0), 'codici': png}
    return esiti, misure


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('jasper'); ap.add_argument('fop')
    ap.add_argument('--fo', required=True); ap.add_argument('--xml'); ap.add_argument('--classpath')
    ap.add_argument('--escludi', action='append', default=[])
    ap.add_argument('--dettagli', type=int, default=0, help='mostra comunque le N righe con lo scarto maggiore')
    a = ap.parse_args()
    esiti = {}
    tmp = tempfile.mkdtemp()
    n_j, n_f = pagine(a.jasper), pagine(a.fop)
    esiti['pagine'] = (n_j == n_f, f'{n_j}' + ('' if n_j == n_f else f' in Jasper, {n_f} in FOP'))
    zone_fo = zone_immagini(a.fo)
    escluse = [tuple(map(float, e.split(','))) for e in a.escludi]
    per_pagina = [verifica_pagina(a, p, zone_fo[p - 1] if p - 1 < len(zone_fo) else [], escluse, tmp)
                  for p in range(1, min(n_j, n_f) + 1)]
    # esito di ogni criterio: il peggiore tra le pagine, con il numero di pagina se sono piu' di una
    for nome in per_pagina[0][0] if per_pagina else []:
        falliti = [(p, e[nome]) for p, (e, _) in enumerate(per_pagina, 1) if not e[nome][0]]
        if falliti:
            esiti[nome] = (False, '; '.join((f'pagina {p}: ' if len(per_pagina) > 1 else '') + d for p, (_, d) in falliti))
        else:
            esiti[nome] = (True, per_pagina[0][0][nome][1] if len(per_pagina) == 1 else
                           f'{len(per_pagina)} pagine, ' + per_pagina[max(range(len(per_pagina)), key=lambda i: per_pagina[i][1].get(nome, 0))][0][nome][1])

    # 5. QR code e DataMatrix: ogni riquadro quadrato di ogni pagina viene letto come QR o DataMatrix
    if a.xml and a.classpath:
        import base64, html as h
        xml = Path(a.xml).read_text(encoding='utf-8')
        png = [f for _, f_ in per_pagina for f in f_.get('codici', [])]
        sorgente = Path(tmp) / 'LeggiCodici.java'; sorgente.write_text(LEGGI_CODICI)
        out = subprocess.run(['java', '-cp', a.classpath, str(sorgente)] + png, capture_output=True, text=True).stdout
        letti = {base64.b64decode(l).decode('utf-8') for l in out.split() if l}
        for nome in ('qr_code', 'data_matrix'):
            attesi = {h.unescape(v) for v in re.findall(rf'<{nome}>([^<]*)</{nome}>', xml)}
            if attesi:
                mancanti = attesi - letti
                esiti[nome] = (not mancanti, f'{len(attesi)} letti' if not mancanti else f'{len(mancanti)} di {len(attesi)} non letti')

    ok = all(v[0] for v in esiti.values())
    for nome, (passa, dettaglio) in esiti.items():
        print(f"  {'OK  ' if passa else 'NO  '} {nome:10} {dettaglio}")
    print('ACCETTATO' if ok else 'NON ACCETTATO')
    sys.exit(0 if ok else 1)


if __name__ == '__main__':
    main()
