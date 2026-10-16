#!/usr/bin/env python3
"""
Verifica un PDF generato con FOP rispetto al PDF di riferimento generato con Jasper (issue #37).

Criteri di accettazione (ogni criterio deve passare):
  1. testo      stesse righe con le stesse parole, quindi stessi a capo
  2. inchiostro per ogni riga di testo, la posizione dell'inchiostro (bordi superiore, inferiore,
                sinistro, destro) differisce al massimo di 0,25 pt; misurata a 600 dpi
  3. raster     a 200 dpi, con tolleranza di 1 pixel (circa 0,36 pt), al massimo 5 pixel scuri di
                uno dei due PDF senza corrispondente nell'altro (rumore di rasterizzazione), escluse
                le zone delle immagini
  4. immagini   per ogni immagine il riquadro dell'inchiostro differisce al massimo di 1 pt
  5. codici     QR code e DataMatrix del PDF FOP si leggono e contengono il qr_code e il data_matrix
                dell'XML (opzionale, con --xml e --classpath)

Le posizioni delle parole vengono da pdftotext, ma sono usate solo per trovare le righe: Jasper e
FOP descrivono le metriche del font in modo diverso, quindi lo scarto si misura sull'inchiostro.
Le zone delle immagini si ricavano dal file FO del layout: sono i block-container il cui blocco ha
corpo 0, come li genera jrxml2fo.py per immagini, QR code e DataMatrix.

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


def parole(pdf):
    out = subprocess.run(['pdftotext', '-bbox', pdf, '-'], capture_output=True, text=True, check=True).stdout
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
            # parole distanti piu' di 3 corpi: elementi diversi sulla stessa riga
            if w[1] - corrente[-1][3] > 3 * (corrente[-1][4] - corrente[-1][2]):
                risultato.append(corrente); corrente = []
            corrente.append(w)
        risultato.append(corrente)
    return [(' '.join(w[0] for w in r), min(w[1] for w in r), min(w[2] for w in r),
             max(w[3] for w in r), max(w[4] for w in r)) for r in risultato]


def zone_immagini(fo):
    testo = Path(fo).read_text(encoding='utf-8')
    zone = []
    for m in re.finditer(r'<fo:block-container[^>]*?left="([\d.]+)pt" top="([\d.]+)pt" width="([\d.]+)pt" height="([\d.]+)pt"[^>]*>\s*<fo:block([^>]*)>', testo):
        if 'font-size="0pt"' in m.group(5):
            zone.append(tuple(map(float, m.group(1, 2, 3, 4))))
    return zone


def raster(pdf, dpi, cartella, nome):
    base = Path(cartella) / nome
    subprocess.run(['pdftoppm', '-r', str(dpi), '-gray', '-png', '-singlefile', pdf, str(base)], check=True)
    # pdftoppm scrive comunque PNG RGB: conversione in scala di grigi
    return Image.open(f'{base}.png').convert('L')


def bordo_inchiostro(im, k, x, y, w, h, soglia=128):
    b = im.crop((int(x * k), int(y * k), int((x + w) * k), int((y + h) * k))).point(lambda v: 255 if v < soglia else 0).getbbox()
    return None if b is None else (x + b[0] / k, y + b[1] / k, x + b[2] / k, y + b[3] / k)


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('jasper'); ap.add_argument('fop')
    ap.add_argument('--fo', required=True); ap.add_argument('--xml'); ap.add_argument('--classpath')
    ap.add_argument('--escludi', action='append', default=[])
    ap.add_argument('--dettagli', type=int, default=0, help='mostra comunque le N righe con lo scarto maggiore')
    a = ap.parse_args()
    esiti = {}
    tmp = tempfile.mkdtemp()

    # 1. testo
    rj, rf = righe(parole(a.jasper)), righe(parole(a.fop))
    testi_f = defaultdict(list)
    for r in rf:
        testi_f[r[0]].append(r)
    mancanti = [r[0] for r in rj if not testi_f.get(r[0])]
    in_piu = [r[0] for r in rf if r[0] not in {x[0] for x in rj}]
    esiti['testo'] = (not mancanti and not in_piu, f'{len(rj)} righe' + (f', diverse: {(mancanti + in_piu)[:3]}' if mancanti or in_piu else ''))

    # 2. inchiostro per riga
    k = DPI_INCHIOSTRO / 72
    ij, if_ = raster(a.jasper, DPI_INCHIOSTRO, tmp, 'j'), raster(a.fop, DPI_INCHIOSTRO, tmp, 'f')
    peggiori = []
    for testo, x0, y0, x1, y1 in rj:
        cand = testi_f.get(testo)
        if not cand:
            continue
        # area della riga di Jasper, allargata: comprende anche l'inchiostro di FOP se spostato
        bj = bordo_inchiostro(ij, k, x0 - 1, y0 - 1.5, x1 - x0 + 2, y1 - y0 + 3)
        bf = bordo_inchiostro(if_, k, x0 - 1, y0 - 1.5, x1 - x0 + 2, y1 - y0 + 3)
        if bj and bf:
            scarto = max(abs(p - q) for p, q in zip(bj, bf))
            peggiori.append((scarto, testo, tuple(round(q - p, 2) for p, q in zip(bj, bf))))
    peggiori.sort(reverse=True)
    massimo = peggiori[0][0] if peggiori else 0
    esiti['inchiostro'] = (massimo <= SOGLIA_INCHIOSTRO, f'scarto massimo {massimo:.2f} pt' +
                           ''.join(f'\n      {s:.2f} pt {t[:40]!r} (sinistra, alto, destra, basso: {d})' for s, t, d in peggiori[:max(3, a.dettagli)] if s > SOGLIA_INCHIOSTRO or a.dettagli))

    # 3. raster con tolleranza, fuori dalle immagini
    zone = zone_immagini(a.fo)
    k2 = DPI_RASTER / 72
    rj2 = raster(a.jasper, DPI_RASTER, tmp, 'j2').point(lambda v: 255 if v < 160 else 0)
    rf2 = raster(a.fop, DPI_RASTER, tmp, 'f2').point(lambda v: 255 if v < 160 else 0)
    diff = ImageChops.lighter(ImageChops.subtract(rj2, rf2.filter(ImageFilter.MaxFilter(3))),
                              ImageChops.subtract(rf2, rj2.filter(ImageFilter.MaxFilter(3))))
    escluse = [tuple(map(float, e.split(','))) for e in a.escludi]
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
    ij3, if3 = raster(a.jasper, 300, tmp, 'j3'), raster(a.fop, 300, tmp, 'f3')
    for x, y, w, h in zone:
        bj = bordo_inchiostro(ij3, k3, x - 2, y - 2, w + 4, h + 4, 200)
        bf = bordo_inchiostro(if3, k3, x - 2, y - 2, w + 4, h + 4, 200)
        if bj and bf:
            scarti.append((max(abs(p - q) for p, q in zip(bj, bf)), (x, y)))
    massimo = max((s for s, _ in scarti), default=0)
    esiti['immagini'] = (massimo <= SOGLIA_IMMAGINI, f'{len(scarti)} immagini, scarto massimo {massimo:.2f} pt' +
                         ''.join(f'\n      {s:.2f} pt immagine in ({p[0]:.0f}, {p[1]:.0f})' for s, p in scarti if s > SOGLIA_IMMAGINI))

    # 5. QR code e DataMatrix: ogni riquadro quadrato viene letto come QR o DataMatrix
    if a.xml and a.classpath:
        import base64, html as h
        xml = Path(a.xml).read_text(encoding='utf-8')
        attesi = {n: h.unescape(m.group(1)) for n in ('qr_code', 'data_matrix') if (m := re.search(rf'<{n}>([^<]*)</{n}>', xml))}
        quadrati = [z for z in zone if abs(z[2] - z[3]) < 0.5 and z[2] >= 40]
        png = []
        for i, (x, y, w, h_) in enumerate(quadrati):
            f = Path(tmp) / f'codice{i}.png'
            if3.crop((int((x - 5) * k3), int((y - 5) * k3), int((x + w + 5) * k3), int((y + h_ + 5) * k3))).save(f); png.append(str(f))
        sorgente = Path(tmp) / 'LeggiCodici.java'; sorgente.write_text(LEGGI_CODICI)
        out = subprocess.run(['java', '-cp', a.classpath, str(sorgente)] + png, capture_output=True, text=True).stdout
        letti = [base64.b64decode(l).decode('utf-8') for l in out.split() if l]
        for nome, atteso in attesi.items():
            esiti[nome] = (atteso in letti, 'letto' if atteso in letti else f'non trovato tra {len(letti)} codici letti')

    ok = all(v[0] for v in esiti.values())
    for nome, (passa, dettaglio) in esiti.items():
        print(f"  {'OK  ' if passa else 'NO  '} {nome:10} {dettaglio}")
    print('ACCETTATO' if ok else 'NON ACCETTATO')
    sys.exit(0 if ok else 1)


if __name__ == '__main__':
    main()
