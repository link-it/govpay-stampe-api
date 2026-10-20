#!/usr/bin/env python3
"""
Converte un template JasperReports (.jrxml) nello scheletro di un template XSLT
che produce XSL-FO (issue #37).

Viene convertito in automatico cio' che e' geometria e stile: posizione e
dimensione degli elementi, sfondi, padding, font, allineamenti, interlinea,
testi fissi (anche con markup html/styled), grafica SVG scritta nelle
espressioni BatikRenderer, loghi in base64. Delle espressioni vengono tradotte
solo le forme semplici ($P{x}, $F{y} e il loro controllo di null); le altre
diventano un commento TODO con l'espressione originale, da completare a mano.

Uso:
  jrxml2fo.py TEMPLATE.jrxml [--nome NOME] [--offset-x N] [--offset-y N]
              [--param-xpath PREFISSO]

  --nome         nome del template XSLT generato (default: nome del report)
  --offset-x/y   origine del report nella pagina, oltre ai suoi margini (default 0)
  --param-xpath  XPath a cui sono legati i parametri $P{x} (default
                 /AvvisoPagamentoInput): nei template di stampe-api ogni
                 parametro del sottoreport e' il campo omonimo dell'input
  --personalizzazioni FILE
                 parti scritte a mano, legate all'uuid degli elementi Jasper:
                 <elemento uuid="..."> sostituisce il contenuto di un testo o di
                 un'immagine, <condizione uuid="..."> la printWhenExpression;
                 attributi="nome=valore;..." cambia gli attributi del blocco, e
                 righe=N indica il numero di righe di un testo variabile

Il file generato va su stdout.
"""

import argparse
import html
import os
import re
import sys
import xml.etree.ElementTree as ET

J = '{http://jasperreports.sourceforge.net/jasperreports}'
BANDE = ['background', 'title', 'pageHeader', 'columnHeader', 'detail',
         'columnFooter', 'pageFooter', 'lastPageFooter', 'summary', 'noData']

ALLINEAMENTO = {'Left': 'start', 'Center': 'center', 'Right': 'end', 'Justified': 'justify'}
VERTICALE = {'Top': 'before', 'Middle': 'center', 'Bottom': 'after'}

todo = []
personalizzazioni = {'elemento': {}, 'condizione': {}, 'attributi': {}}


def coppie(testo):
    """nome=valore;nome=valore: il ; separa solo se seguito da un nuovo nome= (i valori XPath possono
    contenere entita' come &lt;)."""
    return dict(kv.split('=', 1) for kv in re.split(r';(?=[\w-]+=)', testo) if kv.strip())


def carica_personalizzazioni(path):
    # lettura testuale: i frammenti vanno copiati cosi' come sono scritti, con i prefissi xsl: e fo:
    testo = open(path, encoding='utf-8').read()
    testo = re.sub(r'<!--.*?-->', '', testo, flags=re.S)
    # <elemento uuid="..." attributi="..."/> senza contenuto: cambia solo gli attributi del blocco
    for uuid, extra in re.findall(r'<elemento uuid="([^"]+)"([^>]*?)/>', testo):
        m = re.search(r'attributi="([^"]*)"', extra)
        if m:
            personalizzazioni['attributi'][uuid] = coppie(m.group(1))
    for tipo in ('elemento', 'condizione'):
        for uuid, extra, corpo in re.findall(r'<%s uuid="([^"]+)"([^>]*)(?<!/)>(.*?)</%s>' % (tipo, tipo), testo, re.S):
            personalizzazioni[tipo][uuid] = corpo.strip()
            # attributi="nome=valore;nome=valore": sostituiscono o aggiungono attributi del blocco di testo
            m = re.search(r'attributi="([^"]*)"', extra)
            if m:
                personalizzazioni['attributi'][uuid] = coppie(m.group(1))


def pt(v):
    v = float(v)
    return f'{int(v)}pt' if v == int(v) else f'{v}pt'


def font_attrs(el):
    f = el.find(f'{J}textElement/{J}font')
    nome, size, bold, italic = 'SansSerif', '10', False, False
    if f is not None:
        nome = f.get('fontName', nome)
        size = f.get('size', size)
        bold = f.get('isBold') == 'true'
        italic = f.get('isItalic') == 'true'
    a = {'font-family': nome, 'font-size': pt(size)}
    if bold:
        a['font-weight'] = 'bold'
    if italic:
        a['font-style'] = 'italic'
    return a


def famiglia(nome):
    return nome.split('-')[0]


# --- geometria del testo: modello di Jasper e di FOP misurato sui PDF (pdf-riferimento/taratura) ---
#
# Jasper (6.21.5, metriche AWT del font, ascendente A e discendente D per unita' di corpo):
#   - la prima riga parte dal bordo superiore del riquadro (allineamento in alto);
#   - distanza tra le righe p: interlinea Single -> (A + D) * corpo;
#     Fixed F -> F + D*corpo + (A*corpo - F) / n, con n numero di righe del testo;
#   - in verticale centra, o allinea in fondo, l'altezza T = (A + D)*corpo + (n - 1) * p.
# FOP 2.11: distanza tra le righe = line-height L; la prima riga sta a (L - K*corpo)/2 dal bordo
# del blocco. K e' misurato sulla posizione dell'inchiostro (pdf-riferimento/taratura/inchiostro),
# non sulle caselle di pdftotext: Jasper e FOP descrivono il font con ascendenti diverse, e caselle
# allineate non garantiscono caratteri allineati. Tutte le varianti di Titillium hanno lo stesso K.
# Imponendo L = p e un padding superiore si ottiene la stessa posizione di Jasper per ogni allineamento.
METRICHE_AWT = {'TitilliumWeb': (1.133, 0.388), 'RobotoMono': (1.0479, 0.2711), 'Roboto': (0.9277, 0.2441)}
K_FOP = {'TitilliumWeb-Regular.ttf': 1.772, 'TitilliumWeb-SemiBold.ttf': 1.772, 'TitilliumWeb-Bold.ttf': 1.772,
         'TitilliumWeb-Italic.ttf': 1.772, 'TitilliumWeb-SemiBoldItalic.ttf': 1.772,
         'RobotoMono-Regular.ttf': 1.551, 'RobotoMono-Bold.ttf': 1.551,
         'RobotoMono-Italic.ttf': 1.551, 'RobotoMono-BoldItalic.ttf': 1.551}


def geometria_testo(el, blocco, n, altezza):
    """line-height e padding-top che riproducono in FOP la posizione delle righe di Jasper."""
    corpo = float(blocco['font-size'][:-2])
    ff = file_font(blocco, {})
    metriche = METRICHE_AWT.get(famiglia(blocco['font-family']))
    if ff is None or metriche is None or ff[0] not in K_FOP:
        todo.append(f"geometria non tarata per {blocco['font-family']}")
        return {}
    a, d = metriche[0] * corpo, metriche[1] * corpo
    p = el.find(f'{J}textElement/{J}paragraph')
    if p is not None and p.get('lineSpacing') == 'Fixed' and p.get('lineSpacingSize'):
        f = float(p.get('lineSpacingSize'))
        passo = f + d + (a - f) / n
    else:
        passo = a + d
    totale = a + d + (n - 1) * passo
    te = el.find(f'{J}textElement')
    verticale = (te.get('verticalAlignment') if te is not None else None) or 'Top'
    scarto = {'Top': 0.0, 'Middle': (altezza - totale) / 2, 'Bottom': altezza - totale}.get(verticale, 0.0)
    padding = scarto + (K_FOP[ff[0]] * corpo - passo) / 2
    # un testo piu' alto del riquadro farebbe un padding negativo, che FO non ammette
    return {'line-height': pt(round(passo, 3)), 'padding-top': pt(round(max(padding, 0), 3))}


def attrs(d):
    return ''.join(f' {k}="{html.escape(str(v), quote=True)}"' for k, v in d.items())


# --- markup html/styled di Jasper -> inline XSL-FO -----------------------------

def markup_statico(testo, tipo):
    """Converte <b>, <br/>, <style size= isBold=>, <font face=> in fo:inline."""
    if tipo not in ('html', 'styled'):
        return html.escape(testo, quote=False)
    out, pila = [], []
    for tok in re.split(r'(<[^>]+>)', testo):
        if not tok:
            continue
        m = re.fullmatch(r'<\s*(/?)\s*(\w+)([^>]*?)(/?)>', tok)
        if not m:
            out.append(html.escape(html.unescape(tok), quote=False))
            continue
        chiusura, tag, resto, vuoto = m.group(1), m.group(2).lower(), m.group(3), m.group(4)
        if tag == 'br':
            out.append('<fo:block/>')
            continue
        if chiusura:
            if pila:
                pila.pop()
                out.append('</fo:inline>')
            continue
        a = {}
        if tag == 'b':
            a['font-weight'] = 'bold'
        elif tag == 'i':
            a['font-style'] = 'italic'
        elif tag in ('style', 'font'):
            for k, v in re.findall(r'(\w+)\s*=\s*\\?"([^"\\]*)\\?"', resto):
                if k == 'size':
                    a['font-size'] = pt(v)
                elif k == 'isBold' and v == 'true':
                    a['font-weight'] = 'bold'
                elif k == 'face':
                    a['font-family'] = v
                elif k == 'forecolor':
                    a['color'] = v
        out.append(f'<fo:inline{attrs(a)}>')
        pila.append(tag)
        if vuoto:
            pila.pop()
            out.append('</fo:inline>')
    out.extend('</fo:inline>' for _ in pila)
    return ''.join(out)


def run_statici(testo, tipo):
    """Testo fisso -> lista di ('t', testo, attributi inline) e ('br',) per gli a capo."""
    if tipo not in ('html', 'styled'):
        out = []
        for i, parte in enumerate(testo.split('\n')):
            if i:
                out.append(('br',))
            out.append(('t', parte, {}))
        return out
    out, pila = [], []
    for tok in re.split(r'(<[^>]+>)', testo):
        if not tok:
            continue
        m = re.fullmatch(r'<\s*(/?)\s*(\w+)([^>]*?)(/?)>', tok)
        if not m:
            correnti = {}
            for a in pila:
                correnti.update(a)
            for i, parte in enumerate(html.unescape(tok).split('\n')):
                if i:
                    out.append(('br',))
                out.append(('t', parte, dict(correnti)))
            continue
        chiusura, tag, resto, vuoto = m.group(1), m.group(2).lower(), m.group(3), m.group(4)
        if tag == 'br':
            out.append(('br',))
            continue
        if chiusura:
            if pila:
                pila.pop()
            continue
        a = {}
        if tag == 'b':
            a['font-weight'] = 'bold'
        elif tag == 'i':
            a['font-style'] = 'italic'
        elif tag in ('style', 'font'):
            for k, v in re.findall(r'(\w+)\s*=\s*\\?"([^"\\]*)\\?"', resto):
                if k == 'size':
                    a['font-size'] = pt(v)
                elif k == 'isBold' and v == 'true':
                    a['font-weight'] = 'bold'
                elif k == 'face':
                    a['font-family'] = v
                elif k == 'forecolor':
                    a['color'] = v
        if not vuoto:
            pila.append(a)
    return out


# famiglia Jasper (fonts/fontsfamily*.xml) -> file per (grassetto, corsivo)
FILE_FONT = {
    'TitilliumWeb': {(False, False): 'TitilliumWeb-Regular.ttf', (True, False): 'TitilliumWeb-SemiBold.ttf',
                     (False, True): 'TitilliumWeb-Italic.ttf', (True, True): 'TitilliumWeb-SemiBoldItalic.ttf'},
    'TitilliumWeb-Bold': {(False, False): 'TitilliumWeb-Bold.ttf'},
    'TitilliumWeb-Black': {(False, False): 'TitilliumWeb-Black.ttf'},
    'RobotoMono': {(False, False): 'RobotoMono-Regular.ttf', (True, False): 'RobotoMono-Bold.ttf',
                   (False, True): 'RobotoMono-Italic.ttf', (True, True): 'RobotoMono-BoldItalic.ttf'},
    'Roboto': {(False, False): 'Roboto-Regular.ttf', (True, False): 'Roboto-Bold.ttf'},
    'Roboto-Bold': {(False, False): 'Roboto-Bold.ttf'},
}


def file_font(base, a):
    fam = a.get('font-family', base['font-family'])
    grassetto = a.get('font-weight', base.get('font-weight')) == 'bold'
    corsivo = a.get('font-style', base.get('font-style')) == 'italic'
    varianti = FILE_FONT.get(fam)
    if not varianti:
        return None
    # una famiglia senza la variante richiesta usa la normale, come il font extension di Jasper
    return (varianti.get((grassetto, corsivo)) or varianti.get((grassetto, False))
            or varianti[(False, False)], float(a.get('font-size', base['font-size'])[:-2]))


def paragrafi(run):
    correnti = [[]]
    for x in run:
        if x[0] == 'br':
            correnti.append([])
        else:
            correnti[-1].append(x)
    return correnti


# uuid del testo fisso -> posizioni di a capo per paragrafo, calcolate come in Jasper
a_capo = {}


def calcola_a_capo(radice):
    """Raccoglie i testi fissi del template e ne calcola gli a capo con SpezzaRighe.java in un solo avvio."""
    import subprocess
    from pathlib import Path
    richieste = []
    for el in radice.iter(f'{J}staticText'):
        re_el = el.find(f'{J}reportElement')
        te = el.find(f'{J}textElement')
        markup = (te.get('markup') if te is not None else None) or 'none'
        base = font_attrs(el)
        pad = padding_block(el, avvisa=False)
        larghezza = float(re_el.get('width')) - float(pad.get('start-indent', '0pt')[:-2]) - float(pad.get('end-indent', '0pt')[:-2])
        for k, par in enumerate(paragrafi(run_statici((el.findtext(f'{J}text') or '').strip('\n'), markup))):
            parti = []
            for _, t, a in par:
                ff = file_font(base, a)
                if ff is None:
                    break
                parti.append(f'{ff[0]}\x1f{ff[1]}\x1f{t}')
            else:
                if parti:
                    richieste.append(f"{re_el.get('uuid')}#{k}\t{larghezza}\t" + '\x1e'.join(parti))
    if not richieste:
        return
    radice_repo = Path(__file__).resolve().parents[2]
    esito = subprocess.run(['java', '-Djava.awt.headless=true', str(Path(__file__).with_name('SpezzaRighe.java')),
                            str(radice_repo / 'src/main/resources/fonts')],
                           input='\n'.join(richieste) + '\n', capture_output=True, text=True, check=True)
    for riga in esito.stdout.splitlines():
        chiave, _, posizioni = riga.partition('\t')
        uuid, _, k = chiave.rpartition('#')
        a_capo.setdefault(uuid, {})[int(k)] = [int(p) for p in posizioni.split(',') if p]


def fo_statico(run, posizioni):
    """FO del testo fisso, con gli a capo di Jasper espliciti (posizioni: paragrafo -> offset)."""
    out = []
    for k, par in enumerate(paragrafi(run)):
        if k:
            out.append('<fo:block/>')
        tagli = set(posizioni.get(k, [])) if posizioni is not None else set()
        pos = 0
        for _, t, a in par:
            pezzi, inizio = [], 0
            for i in range(len(t)):
                if pos + i in tagli and i > inizio:
                    pezzi.append(t[inizio:i]); inizio = i
                    pezzi.append(None)
                elif pos + i in tagli:
                    pezzi.append(None)
            pezzi.append(t[inizio:])
            for pz in pezzi:
                if pz is None:
                    out.append('<fo:block/>')
                elif pz:
                    e = html.escape(pz, quote=False)
                    out.append(f'<fo:inline{attrs(a)}>{e}</fo:inline>' if a else e)
            pos += len(t)
    return ''.join(out)


# --- espressioni ---------------------------------------------------------------

class Contesto:
    def __init__(self, campi, param_xpath, parametri=()):
        self.campi = campi
        self.param_xpath = param_xpath.rstrip('/')
        # parametri del template XSLT: valori costanti passati dal report che lo richiama come sottoreport
        self.parametri = set(parametri)

    def p(self, nome):
        if nome in self.parametri:
            return f'${nome}'
        return f'{self.param_xpath}/{nome}'

    def f(self, nome):
        return self.campi.get(nome, nome)


def xpath_semplice(espr, ctx):
    """XPath del valore per le forme semplici, None altrimenti."""
    e = re.sub(r'\s+', '', espr)
    m = re.fullmatch(r'\$P\{(\w+)\}', e)
    if m:
        return ctx.p(m.group(1))
    m = re.fullmatch(r'\$F\{(\w+)\}', e)
    if m:
        return ctx.f(m.group(1))
    # ($P{x}.equals(null))?"":$P{x}   e   $P{x}.equals(null)?"":$P{x}
    m = re.fullmatch(r'\(?\$([PF])\{(\w+)\}\.equals\(null\)\)?\?"":\$\1\{\2\}', e)
    if m:
        return ctx.p(m.group(2)) if m.group(1) == 'P' else ctx.f(m.group(2))
    return None


# Variabili e parametri dei template con valore noto: nei report principali (AvvisoPagamento*.jrxml)
# totale_rate e totale_soglie sono campi count(//rata) e count(//rata/giorni) calcolati su tutto il
# documento e passati ai sottoreport; totaleRate e soloSoglie ne derivano.
NUMERI_NOTI = {'$V{totaleRate}': 'count(//rata)', '$P{totale_rate}': 'count(//rata)',
               '$P{totale_soglie}': 'count(//rata/giorni)'}
BOOLEANI_NOTI = {'$V{soloSoglie}': 'count(//rata/giorni) &gt; 0'}


def condizione(espr, ctx):
    """Traduce le printWhenExpression piu' comuni in un test XPath."""
    e = re.sub(r'\s+', '', espr)
    if '&&' in e and '(' not in e.replace('newBoolean(', '').replace('.equals(null)', '').replace(')', ''):
        parti = [condizione(x, ctx) for x in e.split('&&')]
        return None if None in parti else ' and '.join(f'({x})' for x in parti)
    m = re.fullmatch(r'(\$V\{\w+\})==(true|false)', e)
    if m and m.group(1) in BOOLEANI_NOTI:
        t = BOOLEANI_NOTI[m.group(1)]
        return t if m.group(2) == 'true' else f'not({t})'
    if e in BOOLEANI_NOTI:
        return BOOLEANI_NOTI[e]
    def nodo(t, n):
        return ctx.p(n) if t == 'P' else ctx.f(n)
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\.equals\(null\)\)', e)
    if m:
        return f'not(normalize-space({nodo(*m.groups())}))'
    m = re.fullmatch(r'newBoolean\(!\$([PF])\{(\w+)\}\.equals\(null\)\)', e)
    if m:
        return f'normalize-space({nodo(*m.groups())})'
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\.equals\(null\)\)==true', e)
    if m:
        return f'not(normalize-space({nodo(*m.groups())}))'
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\.equals\(null\)\)==false', e)
    if m:
        return f'normalize-space({nodo(*m.groups())})'
    # new Boolean($P{x}) == false / true: parametri booleani
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\)==(true|false)', e)
    if m:
        t = f"{nodo(m.group(1), m.group(2))} = 'true'"
        return t if m.group(3) == 'true' else f'not({t})'
    # new Boolean($F{colonne}.toString() == '2')
    m = re.fullmatch(r"newBoolean\(\$([PF])\{(\w+)\}\.toString\(\)=='([^']*)'\)", e)
    if m:
        return f"{nodo(m.group(1), m.group(2))} = '{m.group(3)}'"
    m = re.fullmatch(r'\$([PF])\{(\w+)\}==false', e)
    if m:
        return f"not({nodo(*m.groups())} = 'true')"
    m = re.fullmatch(r'\$([PF])\{(\w+)\}==true', e)
    if m:
        return f"{nodo(*m.groups())} = 'true'"
    return None


def svg_da_batik(espr):
    m = re.search(r'getInstanceFromText\("(.*)"\)\s*$', espr.strip(), re.S)
    if not m:
        return None
    s = m.group(1).replace('\\"', '"').replace('\\\\', '\\')
    try:
        ET.fromstring(s)
    except ET.ParseError:
        return None
    return s


# --- elementi ------------------------------------------------------------------

# --- righe rimosse (isRemoveLineWhenBlank) e posizione Float ----------------------------------------
#
# Jasper toglie un elemento vuoto con isRemoveLineWhenBlank (testo vuoto o printWhenExpression falsa)
# insieme alla sua fascia verticale, se nessun altro elemento della banda la occupa; gli elementi Float
# sotto di lui salgono della stessa altezza e la banda si accorcia. Qui lo spostamento diventa
# un'espressione XPath: y - somma(altezza * number(vuoto)) degli elementi rimovibili sopra.
# Le bande mantengono la loro altezza: le righe rimosse spostano solo gli elementi della stessa banda.

SPOSTAMENTI = {}      # id(reportElement) -> espressione XPath dello spostamento verso l'alto, in punti
RIMOVIBILI = {}       # id(reportElement) -> condizione XPath di elemento vuoto
SPOSTAMENTO = None    # spostamento dell'elemento in conversione


def condizione_vuoto(el, ctx):
    """Condizione XPath in cui l'elemento e' vuoto e Jasper lo rimuove con la sua riga, None se non si sa."""
    re_el = el.find(f'{J}reportElement')
    if re_el.get('isRemoveLineWhenBlank') != 'true':
        return None
    uuid = re_el.get('uuid')
    attr = personalizzazioni['attributi'].get(uuid, {})
    if 'vuoto' in attr:
        return attr['vuoto']
    pw = re_el.findtext(f'{J}printWhenExpression')
    if pw:
        c = personalizzazioni['condizione'].get(uuid) or condizione(pw, ctx)
        return f'not({c})' if c else None
    if el.tag == f'{J}textField' and uuid not in personalizzazioni['elemento']:
        xp = xpath_semplice(el.findtext(f'{J}textFieldExpression') or '', ctx)
        if xp:
            return f'not(normalize-space({xp}))'
    return None


def analizza_banda(banda, ctx, base):
    """Calcola gli spostamenti degli elementi della banda; base e' lo spostamento della banda stessa (righe
    rimosse nelle bande precedenti). Restituisce lo spostamento della banda successiva."""
    figli = [(el, el.find(f'{J}reportElement')) for el in banda if el.find(f'{J}reportElement') is not None]
    num = lambda e, k: float(e.get(k, 0))
    rimovibili = []
    for el, re_el in figli:
        c = condizione_vuoto(el, ctx)
        if not c:
            continue
        y, h = num(re_el, 'y'), num(re_el, 'height')
        if any(o is not re_el and num(o, 'y') < y + h and num(o, 'y') + num(o, 'height') > y for _, o in figli):
            continue  # fascia occupata anche da un altro elemento: Jasper non la rimuove
        rimovibili.append((y, h, c, re_el))
        RIMOVIBILI[id(re_el)] = c
    for el, re_el in figli:
        termini = [base] if base else []
        if re_el.get('positionType') == 'Float':
            termini += [f'{pt(h)[:-2]} * number({c})' for y, h, c, r in rimovibili if r is not re_el and y + h <= num(re_el, 'y')]
        if termini:
            SPOSTAMENTI[id(re_el)] = ' + '.join(termini)
    termini = ([base] if base else []) + [f'{pt(h)[:-2]} * number({c})' for _, h, c, _ in rimovibili]
    return ' + '.join(termini)


def top(y):
    """Attributo top: costante, oppure espressione se l'elemento si sposta per righe rimosse sopra di lui."""
    return f'{{{y} - ({SPOSTAMENTO})}}pt' if SPOSTAMENTO else pt(y)


def contenitore(re_el, ox, oy, extra=None):
    x = float(re_el.get('x', 0)) + ox
    y = float(re_el.get('y', 0)) + oy
    a = {'absolute-position': 'absolute', 'left': pt(x), 'top': top(y),
         'width': pt(re_el.get('width')), 'height': pt(re_el.get('height'))}
    if re_el.get('mode') == 'Opaque' and re_el.get('backcolor'):
        a['background-color'] = re_el.get('backcolor')
    if extra:
        a.update(extra)
    return a


def padding_block(el, avvisa=True):
    box = el.find(f'{J}box')
    a = {}
    if box is not None:
        pad = box.get('padding')
        sx = box.get('leftPadding', pad)
        dx = box.get('rightPadding', pad)
        if sx:
            a['start-indent'] = pt(sx)
        if dx:
            a['end-indent'] = pt(dx)
        for lato in ('topPadding', 'bottomPadding'):
            if avvisa and box.get(lato, pad) not in (None, '0'):
                todo.append(f'padding verticale non convertito ({lato}={box.get(lato, pad)})')
    return a


def testo(el, ox, oy, ctx, out, rientro):
    re_el = el.find(f'{J}reportElement')
    te = el.find(f'{J}textElement')
    markup = (te.get('markup') if te is not None else None) or 'none'
    blocco = font_attrs(el)
    if re_el.get('forecolor'):
        blocco['color'] = re_el.get('forecolor')
    if te is not None and te.get('textAlignment'):
        blocco['text-align'] = ALLINEAMENTO.get(te.get('textAlignment'), 'start')
    blocco.update(padding_block(el))
    cont = contenitore(re_el, ox, oy)

    # numero di righe: per i testi fissi dagli a capo calcolati, per gli altri dall'indicazione
    # righe=N nelle personalizzazioni (default 1)
    extra = dict(personalizzazioni['attributi'].get(re_el.get('uuid'), {}))
    righe_xpath = extra.pop('righe-xpath', None)
    righe = int(extra.pop('righe', 0))
    extra.pop('vuoto', None)
    # testo variabile: a capo calcolati a runtime come in Jasper (TestoJasper.righe). Con markup, solo se il
    # valore non contiene tag: altrimenti resta il template markup, con gli a capo scelti da FOP
    a_runtime = None
    if el.tag == f'{J}textField' and re_el.get('uuid') not in personalizzazioni['elemento'] and not righe_xpath and not righe \
            and markup in ('none', 'styled', 'html'):
        xp = xpath_semplice(el.findtext(f'{J}textFieldExpression') or '', ctx)
        ff = file_font(blocco, {})
        if xp and ff:
            a_runtime = (xp, ff)
            righe_xpath = "1 + string-length($testo-righe) - string-length(translate($testo-righe, '&#10;', ''))"
    posizioni = a_capo.get(re_el.get('uuid')) if el.tag == f'{J}staticText' else None
    if not righe:
        righe = sum(len(v) + 1 for v in posizioni.values()) if posizioni else 1
    blocco.update(geometria_testo(el, blocco, righe, float(re_el.get('height'))))
    blocco.update(extra)
    variabile = ''
    if righe_xpath:
        # numero di righe calcolato dal contenuto: interlinea e padding per ogni caso, scelti in XSLT
        casi = {n: geometria_testo(el, blocco, n, float(re_el.get('height'))) for n in range(1, 6)}
        blocco.pop('line-height', None); blocco.pop('padding-top', None)
        for nome_attr in ('line-height', 'padding-top'):
            scelte = ''.join(f'<xsl:when test="$righe = {n}">{g[nome_attr]}</xsl:when>' for n, g in casi.items() if n < 5)
            variabile += f'<xsl:attribute name="{nome_attr}"><xsl:choose>{scelte}<xsl:otherwise>{casi[5][nome_attr]}</xsl:otherwise></xsl:choose></xsl:attribute>'
        variabile = f'<xsl:variable name="righe" select="{righe_xpath}"/>' + variabile
    if a_runtime:
        xp, ff = a_runtime
        larghezza = float(re_el.get('width')) - float(blocco.get('start-indent', '0pt')[:-2]) - float(blocco.get('end-indent', '0pt')[:-2])
        massimo = righe_massime(el, blocco, float(re_el.get('height')))
        proprieta = {p.get('name'): p.get('value') for p in re_el.findall(f'{J}property')}
        tronca = 'true()' if proprieta.get('net.sf.jasperreports.text.truncate.at.char') == 'true' else 'false()'
        # con xsltproc (solo per le verifiche) la funzione Java non c'e': il testo resta intero
        chiamata = f"tj:righe(string({xp}), '{ff[0]}', {ff[1]}, {round(larghezza, 3)}, {massimo}, {tronca})"
        con_tag = f"contains({xp}, '&lt;')" if markup != 'none' else 'false()'
        variabile = (f'<xsl:variable name="testo-righe"><xsl:choose><xsl:when test="function-available(\'tj:righe\') and not({con_tag})">'
                     f'<xsl:value-of select="{chiamata}"/></xsl:when><xsl:otherwise><xsl:value-of select="{xp}"/></xsl:otherwise></xsl:choose></xsl:variable>') + variabile
        if markup == 'none':
            corpo = '<xsl:value-of select="$testo-righe"/>'
        else:
            corpo = (f'<xsl:choose><xsl:when test="{con_tag}"><xsl:call-template name="markup"><xsl:with-param name="testo" select="{xp}"/></xsl:call-template></xsl:when>'
                     f'<xsl:otherwise><xsl:value-of select="$testo-righe"/></xsl:otherwise></xsl:choose>')
        blocco['linefeed-treatment'] = 'preserve'
        blocco['wrap-option'] = 'no-wrap'
    if re_el.get('uuid') in personalizzazioni['elemento']:
        corpo = personalizzazioni['elemento'][re_el.get('uuid')]
    elif el.tag == f'{J}staticText':
        run = run_statici((el.findtext(f'{J}text') or '').strip('\n'), markup)
        if posizioni is not None:
            # a capo calcolati come in Jasper: FOP non deve spezzare di nuovo le righe
            blocco['wrap-option'] = 'no-wrap'
        corpo = fo_statico(run, posizioni)
    elif not a_runtime:
        espr = el.findtext(f'{J}textFieldExpression') or ''
        xp = xpath_semplice(espr, ctx)
        if xp and markup in ('html', 'styled'):
            corpo = f'<xsl:call-template name="markup"><xsl:with-param name="testo" select="{xp}"/></xsl:call-template>'
        elif xp:
            corpo = f'<xsl:value-of select="{xp}"/>'
        else:
            todo.append('espressione di testo')
            corpo = f'<!-- TODO espressione ({markup}): {commento(espr)} -->'
    out.append(f'{rientro}<fo:block-container{attrs(cont)}>')
    out.append(f'{rientro}\t<fo:block{attrs(blocco)}>{variabile}{corpo}</fo:block>')
    out.append(f'{rientro}</fo:block-container>')


def righe_massime(el, blocco, altezza):
    """Righe che Jasper mostra in un riquadro che non si allunga: aggiunge righe finche' l'altezza della
    prima, (A + D) x corpo, piu' l'interlinea delle successive (lineSpacingSize con Fixed) sta nel riquadro.
    0 se il riquadro si allunga (isStretchWithOverflow)."""
    if el.get('isStretchWithOverflow') == 'true' or el.get('textAdjust') == 'StretchHeight':
        return 0
    metriche = METRICHE_AWT.get(famiglia(blocco['font-family']))
    corpo = float(blocco['font-size'][:-2])
    if metriche is None:
        return 0
    prima = (metriche[0] + metriche[1]) * corpo
    p = el.find(f'{J}textElement/{J}paragraph')
    successive = float(p.get('lineSpacingSize')) if p is not None and p.get('lineSpacing') == 'Fixed' and p.get('lineSpacingSize') else prima
    n = 1
    while prima + n * successive <= altezza + 0.01:
        n += 1
    return n


def immagine(el, ox, oy, ctx, out, rientro):
    re_el = el.find(f'{J}reportElement')
    espr = el.findtext(f'{J}imageExpression') or ''
    m_qr = re.search(r'QRCodeWriter\(\)\.encode\(\s*\$F\{(\w+)\}', espr)
    if m_qr and re_el.get('uuid') not in personalizzazioni['elemento']:
        codice(re_el, ctx.f(m_qr.group(1)), ox, oy, out, rientro)
        return
    w, h = pt(re_el.get('width')), pt(re_el.get('height'))
    cont = contenitore(re_el, ox, oy)
    svg = svg_da_batik(espr)
    m_logo = re.search(r'decodeBase64\(\(\(\$P\{(\w+)\}\.contains', espr)
    # font-size 0: senza, FOP allinea l'immagine alla linea di base del testo e la alza di qualche punto
    blocco = {'font-size': '0pt', 'line-height': '0pt'}
    out.append(f'{rientro}<fo:block-container{attrs(cont)}>')
    if re_el.get('uuid') in personalizzazioni['elemento']:
        out.append(f"{rientro}\t{personalizzazioni['elemento'][re_el.get('uuid')]}")
    elif svg:
        apertura = re.search(r'<svg[^>]*>', svg).group(0)
        iw = re.search(r'''\swidth=['"]([\d.]+)''', apertura)
        ih = re.search(r'''\sheight=['"]([\d.]+)''', apertura)
        if iw and ih:
            # SVG con dimensioni proprie: Jasper lo tratta come un'immagine (RetainShape), adattata al
            # riquadro con le sue proporzioni e allineata secondo hAlign/vAlign, con lo spazio libero
            # troncato a punti interi come per le immagini raster
            bw, bh = float(re_el.get('width')), float(re_el.get('height'))
            sc = min(bw / float(iw.group(1)), bh / float(ih.group(1)))
            dw, dh = float(iw.group(1)) * sc, float(ih.group(1)) * sc
            dx = {'Center': (bw - dw) // 2, 'Right': (bw - dw) // 1}.get(el.get('hAlign'), 0)
            dy = {'Middle': (bh - dh) // 2, 'Bottom': (bh - dh) // 1}.get(el.get('vAlign'), 0)
            interno = {'absolute-position': 'absolute', 'left': pt(dx), 'top': pt(dy), 'width': pt(round(dw, 3)), 'height': pt(round(dh, 3))}
            grafica = {'content-width': pt(round(dw, 3)), 'content-height': pt(round(dh, 3)), 'scaling': 'uniform'}
            out.append(f'{rientro}\t<fo:block{attrs(blocco)}><fo:block-container{attrs(interno)}><fo:block{attrs(blocco)}>'
                       f'<fo:instream-foreign-object{attrs(grafica)}>{svg}</fo:instream-foreign-object></fo:block></fo:block-container></fo:block>')
        else:
            # SVG senza dimensioni: Jasper (BatikRenderer) gli passa tutto il riquadro come area di disegno,
            # ed e' l'SVG stesso (preserveAspectRatio) a centrare il disegno, indipendentemente da hAlign/vAlign
            grafica = {'content-width': w, 'content-height': h, 'scaling': 'non-uniform'}
            out.append(f'{rientro}\t<fo:block{attrs(blocco)}><fo:instream-foreign-object{attrs(grafica)}>{svg}</fo:instream-foreign-object></fo:block>')
    elif m_logo:
        xp = ctx.p(m_logo.group(1))
        lw, lh = float(re_el.get('width')), float(re_el.get('height'))
        # Immagine raster come Jasper (scaleImage RetainShape): scalata per stare nel riquadro, poi
        # spostata secondo hAlign/vAlign di uno spazio libero TRONCATO a punti interi. Le dimensioni in
        # pixel le aggiunge il renderer (elemento <logo>_px); senza, si scala nel riquadro senza troncare.
        oriz = {'Center': 'floor(($W - $iw * $sc) div 2)', 'Right': 'floor($W - $iw * $sc)'}.get(el.get('hAlign'), '0')
        vert = {'Middle': 'floor(($H - $ih * $sc) div 2)', 'Bottom': 'floor($H - $ih * $sc)'}.get(el.get('vAlign'), '0')
        sorgente = (f'<xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="{xp}"/>'
                    f'</xsl:call-template>)</xsl:attribute>')
        out.append(f"""{rientro}\t<fo:block{attrs(blocco)}><xsl:if test="normalize-space({xp})"><xsl:choose>
{rientro}\t\t<xsl:when test="normalize-space({xp}_px)">
{rientro}\t\t\t<xsl:variable name="W" select="{lw}"/><xsl:variable name="H" select="{lh}"/>
{rientro}\t\t\t<xsl:variable name="iw" select="number(substring-before(normalize-space({xp}_px), ' '))"/>
{rientro}\t\t\t<xsl:variable name="ih" select="number(substring-after(normalize-space({xp}_px), ' '))"/>
{rientro}\t\t\t<xsl:variable name="sc"><xsl:choose><xsl:when test="$W div $iw &lt; $H div $ih"><xsl:value-of select="$W div $iw"/></xsl:when><xsl:otherwise><xsl:value-of select="$H div $ih"/></xsl:otherwise></xsl:choose></xsl:variable>
{rientro}\t\t\t<fo:block-container absolute-position="absolute" left="{{{oriz}}}pt" top="{{{vert}}}pt" width="{{$iw * $sc}}pt" height="{{$ih * $sc}}pt">
{rientro}\t\t\t\t<fo:block{attrs(blocco)}><fo:external-graphic content-width="{{$iw * $sc}}pt" content-height="{{$ih * $sc}}pt" scaling="non-uniform">{sorgente}</fo:external-graphic></fo:block>
{rientro}\t\t\t</fo:block-container>
{rientro}\t\t</xsl:when>
{rientro}\t\t<xsl:otherwise><fo:external-graphic{attrs({'content-width': 'scale-to-fit', 'content-height': 'scale-to-fit', 'width': w, 'height': h, 'scaling': 'uniform'})}>{sorgente}</fo:external-graphic></xsl:otherwise>
{rientro}\t</xsl:choose></xsl:if></fo:block>""")
    else:
        todo.append('immagine')
        out.append(f'{rientro}\t<!-- TODO immagine: {commento(espr)} -->')
    out.append(f'{rientro}</fo:block-container>')


def codice(re_el, campo_xpath, ox, oy, out, rientro):
    """QR code o DataMatrix: SVG calcolato dal servizio (PreparazioneAvviso) nell'elemento <campo>_svg,
    disegnato su tutto il riquadro come fa Jasper."""
    w, h = pt(re_el.get('width')), pt(re_el.get('height'))
    cont = contenitore(re_el, ox, oy)
    grafica = {'content-width': w, 'content-height': h, 'scaling': 'non-uniform'}
    out.append(f'{rientro}<fo:block-container{attrs(cont)}>')
    out.append(f'{rientro}\t<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space({campo_xpath}_svg)"><fo:external-graphic{attrs(grafica)}>'
               f'<xsl:attribute name="src">url(<xsl:value-of select="{campo_xpath}_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>')
    out.append(f'{rientro}</fo:block-container>')


def linea_o_rettangolo(el, ox, oy, out, rientro):
    re_el = el.find(f'{J}reportElement')
    pen = el.find(f'{J}graphicElement/{J}pen')
    spessore = float(pen.get('lineWidth', '1')) if pen is not None else 1.0
    colore = (pen.get('lineColor') if pen is not None else None) or re_el.get('forecolor') or '#000000'
    if el.tag == f'{J}line':
        if spessore == 0:
            return  # distanziatore invisibile
        w, h = float(re_el.get('width')), float(re_el.get('height'))
        lato = 'border-top' if h <= 1 else 'border-left' if w <= 1 else None
        if lato is None:
            todo.append('linea diagonale')
            out.append(f'{rientro}<!-- TODO linea diagonale {w}x{h} -->')
            return
        stile = {'Dashed': 'dashed', 'Dotted': 'dotted'}.get(pen.get('lineStyle') if pen is not None else '', 'solid')
        cont = contenitore(re_el, ox, oy, {lato: f'{pt(spessore)} {stile} {colore}'})
    else:
        extra = {}
        if spessore > 0:
            extra['border'] = f'{pt(spessore)} solid {colore}'
        cont = contenitore(re_el, ox, oy, extra)
    out.append(f'{rientro}<fo:block-container{attrs(cont)}><fo:block/></fo:block-container>')


def bordi(el, ox, oy, out, rientro):
    box = el.find(f'{J}box')
    re_el = el.find(f'{J}reportElement')
    if box is None or re_el is None:
        return
    x = float(re_el.get('x', 0)) + ox; y = float(re_el.get('y', 0)) + oy
    w = float(re_el.get('width')); h = float(re_el.get('height'))
    comune = box.find(f'{J}pen')
    for lato, (lx, ly, lw, lh) in {'topPen': (x, y, w, 0), 'bottomPen': (x, y + h, w, 0),
                                   'leftPen': (x, y, 0, h), 'rightPen': (x + w, y, 0, h)}.items():
        pen = box.find(f'{J}{lato}')
        spessore = float((pen if pen is not None and pen.get('lineWidth') else comune).get('lineWidth', '0')) \
            if (pen is not None and pen.get('lineWidth')) or comune is not None else 0.0
        if spessore <= 0:
            continue
        colore = (pen.get('lineColor') if pen is not None else None) or (comune.get('lineColor') if comune is not None else None) or re_el.get('forecolor') or '#000000'
        stile = {'Dashed': 'dashed', 'Dotted': 'dotted'}.get((pen.get('lineStyle') if pen is not None else None) or '', 'solid')
        if lh == 0:
            # FOP disegna il bordo superiore sotto il bordo del riquadro: posizionato sul bordo, la linea
            # cade dove Jasper la disegna centrata
            a = {'absolute-position': 'absolute', 'left': pt(lx), 'top': top(ly), 'width': pt(lw), 'height': '0pt',
                 'border-top': f'{pt(spessore)} {stile} {colore}'}
        else:
            a = {'absolute-position': 'absolute', 'left': pt(round(lx - spessore / 2, 3)), 'top': top(ly), 'width': '0pt', 'height': pt(lh),
                 'border-left': f'{pt(spessore)} {stile} {colore}'}
        out.append(f'{rientro}<fo:block-container{attrs(a)}><fo:block/></fo:block-container>')


def valore_costante(espr):
    """Valore XPath di un'espressione Jasper costante (true, false, "testo", numero), None altrimenti."""
    e = re.sub(r'\s+', '', espr or '')
    m = re.fullmatch(r'(?:newBoolean\()?(true|false)\)?', e) or re.fullmatch(r'Boolean\.(TRUE|FALSE)', e)
    if m:
        return "'" + m.group(1).lower() + "'"
    m = re.fullmatch(r'"([^"]*)"', e)
    if m:
        return "'" + m.group(1) + "'"
    if re.fullmatch(r'-?\d+(\.\d+)?', e):
        return e
    return None


def sottoreport(el, ox, oy, ctx, out, rientro):
    """Sottoreport su una sorgente dati XML: un riquadro per record, disposto nelle colonne del sottoreport
    (printOrder Horizontal: i record riempiono le righe da sinistra a destra), con dentro il template
    generato dallo stesso sottoreport. I parametri passati come $P{x} non servono: il sottoreport legge
    gli stessi valori dalla radice dell'XML. Quelli costanti diventano parametri del template."""
    re_el = el.find(f'{J}reportElement')
    m_nome = re.fullmatch(r'\s*\$P\{(\w+)\}\s*', el.findtext(f'{J}subreportExpression') or '')
    m_ds = re.search(r'subDataSource\("([^"]+)"\)', el.findtext(f'{J}dataSourceExpression') or '')
    if not m_nome or not m_ds:
        todo.append('subreport')
        out.append(f'{rientro}<!-- TODO sottoreport: {commento(el.findtext(f"{J}subreportExpression") or "")} -->')
        return
    nome = m_nome.group(1)
    # subDataSource("/doppia/rata") parte dal record corrente, che ne e' la radice: dal contesto
    # XSLT (l'elemento doppia) basta il resto del percorso
    passi = m_ds.group(1).strip('/').split('/')
    selezione = '/'.join(passi[1:]) or '.'
    costanti = {}
    for sp in el.findall(f'{J}subreportParameter'):
        v = valore_costante(sp.findtext(f'{J}subreportParameterExpression'))
        if v is not None:
            costanti[sp.get('name')] = v
    sub = ET.parse(os.path.join(DIR_JRXML, nome + '.jrxml')).getroot()
    colonne = int(sub.get('columnCount', '1'))
    larghezza = float(sub.get('columnWidth'))
    # altezza di un record: bande di dettaglio stampate, con le condizioni sui parametri costanti
    altezza = 0.0
    for b in sub.findall(f'{J}detail/{J}band'):
        pw = b.findtext(f'{J}printWhenExpression')
        if pw and not valuta_condizione_costante(pw, sub, costanti):
            continue
        altezza += float(b.get('height', 0))
    x = float(re_el.get('x', 0)) + ox
    y = float(re_el.get('y', 0)) + oy
    out.append(f'{rientro}<xsl:for-each select="{selezione}">')
    out.append(f'{rientro}\t<fo:block-container absolute-position="absolute" left="{{{x} + ((position() - 1) mod {colonne}) * {larghezza}}}pt"'
               f' top="{{{y} + floor((position() - 1) div {colonne}) * {altezza}}}pt" width="{pt(larghezza)}" height="{pt(altezza)}">')
    out.append(f'{rientro}\t\t<xsl:call-template name="{nome}">')
    for k, v in costanti.items():
        out.append(f'{rientro}\t\t\t<xsl:with-param name="{k}" select="{v}"/>')
    out.append(f'{rientro}\t\t</xsl:call-template>')
    out.append(f'{rientro}\t</fo:block-container>')
    out.append(f'{rientro}</xsl:for-each>')


def valuta_condizione_costante(espr, sub, costanti):
    """Valuta in Python una condizione su un parametro booleano del sottoreport (new Boolean($P{x}) == true/false),
    con il valore passato dal report principale o il default del sottoreport."""
    e = re.sub(r'\s+', '', espr)
    m = re.fullmatch(r'newBoolean\(\$P\{(\w+)\}\)==(true|false)', e)
    if not m:
        todo.append('condizione di banda')
        return True
    v = costanti.get(m.group(1))
    if v is None:
        par = next((p for p in sub.findall(f'{J}parameter') if p.get('name') == m.group(1)), None)
        v = valore_costante(par.findtext(f'{J}defaultValueExpression') if par is not None else 'false') or "'false'"
    return (v == "'true'") == (m.group(2) == 'true')


def commento(s):
    s = re.sub(r'<svg.*?</svg>', '<svg…>', s, flags=re.S)
    return re.sub(r'\s+', ' ', s).replace('--', '- -').strip()


def elementi(contenitore_xml, ox, oy, ctx, out, rientro, ereditato=None):
    global SPOSTAMENTO
    for el in contenitore_xml:
        tag = el.tag.replace(J, '')
        re_el = el.find(f'{J}reportElement')
        if re_el is None:
            continue
        # gli elementi di un frame si spostano con il frame
        SPOSTAMENTO = ereditato if ereditato is not None else SPOSTAMENTI.get(id(re_el))
        pw = re_el.findtext(f'{J}printWhenExpression')
        if pw:
            c = personalizzazioni['condizione'].get(re_el.get('uuid')) or condizione(pw, ctx)
            if c is None:
                todo.append('printWhenExpression')
                out.append(f'{rientro}<!-- TODO condizione: {commento(pw)} -->')
                c = 'true()'
        elif id(re_el) in RIMOVIBILI:
            # elemento vuoto rimosso con la sua riga: non va disegnato nemmeno lo sfondo
            c = f'not({RIMOVIBILI[id(re_el)]})'
        else:
            c = None
        if c:
            out.append(f'{rientro}<xsl:if test="{c}">')
            r2 = rientro + '\t'
        else:
            r2 = rientro
        if tag in ('staticText', 'textField'):
            testo(el, ox, oy, ctx, out, r2)
            bordi(el, ox, oy, out, r2)
        elif tag == 'image':
            immagine(el, ox, oy, ctx, out, r2)
        elif tag in ('line', 'rectangle'):
            linea_o_rettangolo(el, ox, oy, out, r2)
        elif tag == 'componentElement' and el.find('{http://jasperreports.sourceforge.net/jasperreports/components}DataMatrix') is not None:
            dm = el.find('{http://jasperreports.sourceforge.net/jasperreports/components}DataMatrix')
            m = re.fullmatch(r'\s*\$F\{(\w+)\}\s*', dm.findtext('{http://jasperreports.sourceforge.net/jasperreports/components}codeExpression') or '')
            if m:
                codice(re_el, ctx.f(m.group(1)), ox, oy, out, r2)
            else:
                todo.append('DataMatrix')
                out.append(f'{r2}<!-- TODO DataMatrix con espressione non semplice -->')
        elif tag == 'subreport':
            sottoreport(el, ox, oy, ctx, out, r2)
        elif tag == 'frame':
            x = float(re_el.get('x', 0)) + ox
            y = float(re_el.get('y', 0)) + oy
            elementi(el, x, y, ctx, out, r2, SPOSTAMENTO or '')
            SPOSTAMENTO = None
        else:
            todo.append(tag)
            out.append(f'{r2}<!-- TODO elemento {tag} x={re_el.get("x")} y={re_el.get("y")} -->')
        if c:
            out.append(f'{rientro}</xsl:if>')


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('jrxml')
    ap.add_argument('--nome')
    ap.add_argument('--offset-x', type=float, default=0)
    ap.add_argument('--offset-y', type=float, default=0)
    ap.add_argument('--param-xpath', default='/AvvisoPagamentoInput')
    ap.add_argument('--personalizzazioni')
    ap.add_argument('--parametri', help='parametri ricevuti come costanti dal report principale, separati da virgola')
    a = ap.parse_args()
    global DIR_JRXML
    DIR_JRXML = os.path.dirname(os.path.abspath(a.jrxml))
    if a.personalizzazioni:
        carica_personalizzazioni(a.personalizzazioni)

    r = ET.parse(a.jrxml).getroot()
    nome = a.nome or r.get('name')
    campi = {f.get('name'): (f.findtext(f'{J}fieldDescription') or f.get('name')).strip()
             for f in r.findall(f'{J}field')}
    # parametri che il template riceve come costanti da chi lo richiama come sottoreport (per esempio
    # Multipla): tutti gli altri si leggono dalla radice dell'XML
    parametri = [x for x in (a.parametri or '').split(',') if x]
    ctx = Contesto(campi, a.param_xpath, parametri)

    calcola_a_capo(r)
    ox = a.offset_x + float(r.get('leftMargin', 0))
    oy = a.offset_y + float(r.get('topMargin', 0))
    out = []
    base = ''
    for banda in BANDE:
        for sezione in r.findall(f'{J}{banda}'):
            for b in sezione.iter(f'{J}band'):
                out.append(f'\t\t<!-- banda {banda}, y={oy - a.offset_y - float(r.get("topMargin", 0))} -->')
                pw = b.findtext(f'{J}printWhenExpression')
                c = condizione(pw, ctx) if pw else None
                successiva = analizza_banda(b, ctx, base)
                if pw and c is None:
                    todo.append('printWhenExpression di banda')
                    out.append(f'\t\t<!-- TODO condizione di banda: {commento(pw)} -->')
                if c:
                    # banda condizionata: solo l'ultima puo' mancare senza spostare le successive
                    out.append(f'\t\t<xsl:if test="{c}">')
                    elementi(b, ox, oy, ctx, out, '\t\t\t')
                    out.append('\t\t</xsl:if>')
                else:
                    elementi(b, ox, oy, ctx, out, '\t\t')
                oy += float(b.get('height', 0))
                # le righe rimosse non spostano le bande successive: nei sottoreport a colonne (printOrder
                # Horizontal) Jasper tiene l'altezza piena della banda, come si vede nei PDF di riferimento
                # delle soglie
                del successiva

    print('<?xml version="1.0" encoding="UTF-8"?>')
    sorgente = a.jrxml.split('/')[-1]
    print('<!--')
    print(f'\tGENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/{sorgente}: non modificare a mano.')
    if a.personalizzazioni:
        print(f'\tLe parti scritte a mano sono in {a.personalizzazioni}.')
    print('\tRigenerare con script/fop/genera-xsl.sh.')
    print('-->')
    print('<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"')
    print('\txmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"')
    print('\txmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">')
    print()
    print(f'\t<xsl:template name="{nome}">')
    for nome_par in parametri:
        par = next(p for p in r.findall(f'{J}parameter') if p.get('name') == nome_par)
        print(f'\t\t<xsl:param name="{nome_par}" select="{valore_costante(par.findtext(f"{J}defaultValueExpression")) or chr(39) + "false" + chr(39)}"/>')
    print('\n'.join(out))
    print('\t</xsl:template>')
    print()
    print('</xsl:stylesheet>')

    if todo:
        from collections import Counter
        print('TODO: ' + ', '.join(f'{k} x{v}' for k, v in Counter(todo).items()), file=sys.stderr)


if __name__ == '__main__':
    main()
