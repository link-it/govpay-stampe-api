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


def interlinea_fissa(el):
    """lineSpacingSize con interlinea Fixed, 0 con Single: il parametro interlineaFissa di TestoJasper."""
    p = el.find(f'{J}textElement/{J}paragraph')
    return float(p.get('lineSpacingSize')) if p is not None and p.get('lineSpacing') == 'Fixed' and p.get('lineSpacingSize') else 0.0


def num_xpath(v):
    """Numero come letterale XPath, senza perdere cifre: 16 e non 16.0."""
    r = repr(float(v))
    return r[:-2] if r.endswith('.0') else r


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


def testo_costante(el):
    """Testo di un elemento che non dipende dai dati: il testo di uno staticText, o l'espressione di un
    textField fatta solo di stringhe letterali concatenate ("..." + "..."). None altrimenti."""
    if el.tag == f'{J}staticText':
        return el.findtext(f'{J}text') or ''
    if el.tag != f'{J}textField':
        return None
    espr = (el.findtext(f'{J}textFieldExpression') or '').strip()
    letterale = r'"((?:[^"\\]|\\.)*)"'
    if not re.fullmatch(rf'{letterale}(?:\s*\+\s*{letterale})*', espr, re.S):
        return None
    unisci = ''.join(m.group(1) for m in re.finditer(letterale, espr))
    return re.sub(r'\\(.)', lambda m: {'n': '\n', 't': '\t'}.get(m.group(1), m.group(1)), unisci)


def calcola_a_capo(radice):
    """Raccoglie i testi fissi del template e ne calcola gli a capo con SpezzaRighe.java in un solo avvio."""
    import subprocess
    from pathlib import Path
    richieste = []
    for el in radice.iter():
        if testo_costante(el) is None:
            continue
        re_el = el.find(f'{J}reportElement')
        te = el.find(f'{J}textElement')
        markup = (te.get('markup') if te is not None else None) or 'none'
        base = font_attrs(el)
        pad = padding_block(el, avvisa=False)
        larghezza = float(re_el.get('width')) - float(pad.get('start-indent', '0pt')[:-2]) - float(pad.get('end-indent', '0pt')[:-2])
        for k, par in enumerate(paragrafi(run_statici(testo_costante(el).strip('\n'), markup))):
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
    def __init__(self, campi, param_xpath, parametri=(), param_campi=None):
        self.campi = campi
        self.param_xpath = param_xpath.rstrip('/')
        # parametri del template XSLT: valori costanti passati dal report che lo richiama come sottoreport
        self.parametri = set(parametri)
        # parametri che sono campi del report principale, passati invariati lungo i sottoreport: XPath assoluto
        self.param_campi = param_campi or {}

    def p(self, nome):
        if nome in self.parametri:
            return f'${nome}'
        if nome in self.param_campi:
            return self.param_campi[nome]
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
    # importo: new DecimalFormat("#,###,##0.00[ suffisso]", Locale.ITALY).format(Double.valueOf($F{x}))
    m = re.fullmatch(r'\(*newjava\.text\.DecimalFormat\("#,###,##0\.00[^"]*",newDecimalFormatSymbols\(Locale\.ITALY\)\)'
                     r'\.format\(Double\.valueOf\(\$([PF])\{(\w+)\}\)\)\)*(?:\.toString\(\))?', e)
    if m:
        suffisso = re.search(r'DecimalFormat\(\s*"#,###,##0\.00([^"]*)"', espr).group(1)
        nodo = ctx.p(m.group(2)) if m.group(1) == 'P' else ctx.f(m.group(2))
        numero = f"format-number(number({nodo}), '#.##0,00', 'it')"
        return f"concat({numero}, '{suffisso}')" if suffisso else numero
    # "testo" + $F{x}
    m = re.fullmatch(r'"([^"\\]*)"\+\$([PF])\{(\w+)\}', e)
    if m:
        testo = re.search(r'"([^"\\]*)"', espr).group(1)
        return f"concat('{testo}', {ctx.p(m.group(3)) if m.group(2) == 'P' else ctx.f(m.group(3))})"
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
        return condizione_composta(espr, ctx) if None in parti else ' and '.join(f'({x})' for x in parti)
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
    # $F{x}.equals("V") / !$F{x}.equals("V")
    m = re.fullmatch(r'(!?)\$([PF])\{(\w+)\}\.equals\("([^"\']*)"\)', e)
    if m:
        t = f"{nodo(m.group(2), m.group(3))} = '{m.group(4)}'"
        return f'not({t})' if m.group(1) else t
    m = re.fullmatch(r'\$([PF])\{(\w+)\}==false', e)
    if m:
        return f"not({nodo(*m.groups())} = 'true')"
    m = re.fullmatch(r'\$([PF])\{(\w+)\}==true', e)
    if m:
        return f"{nodo(*m.groups())} = 'true'"
    return condizione_composta(espr, ctx)


# --- espressioni Java composte ------------------------------------------------------------------------
#
# Le espressioni dei testi bilingue compongono il valore con concatenazioni, ternari, controlli di null e
# markup nelle stringhe, per esempio $P{x} + ($P{y}.equals(null) ? "" : "<style forecolor=..> · " + $P{y} + "</style>").
# Qui un sottoinsieme di Java diventa XSLT: le condizioni un test XPath, i testi un frammento XSLT
# (xsl:choose per i ternari) che produce la stessa stringa, markup compreso, che Jasper interpreta dopo.

class NonTradotta(Exception):
    pass


TOKEN = re.compile(r'\s*(?:(?P<str>"(?:[^"\\]|\\.)*")|(?P<ref>\$[PFV]\{\w+\})|(?P<num>\d+(?:\.\d+)?)'
                   r'|(?P<op>&&|\|\||==|!=|[!?:+().,])|(?P<id>[A-Za-z_][\w.]*))')


def token(espr):
    pos, out = 0, []
    espr = espr.strip()
    while pos < len(espr):
        m = TOKEN.match(espr, pos)
        if not m or m.end() == pos:
            raise NonTradotta(espr[pos:pos + 20])
        tipo = m.lastgroup
        out.append((tipo, m.group(tipo)))
        pos = m.end()
        while pos < len(espr) and espr[pos].isspace():
            pos += 1
    return out


class Parser:
    """Albero di un'espressione: ('lit', s), ('ref', '$P{x}'), ('null',), ('bool', b), ('num', n),
    ('concat', [..]), ('cond', c, a, b), ('not', e), ('and', a, b), ('or', a, b), ('isnull', e),
    ('eq', a, b), ('upper', e), ('fmt', suffisso, e)."""

    def __init__(self, espr):
        self.t = token(espr)
        self.i = 0

    def vedi(self, v=None):
        if self.i >= len(self.t):
            return None
        return self.t[self.i] if v is None or self.t[self.i][1] == v else None

    def prendi(self, v=None):
        t = self.vedi(v)
        if t is None:
            raise NonTradotta(f'atteso {v}')
        self.i += 1
        return t

    def tutto(self):
        e = self.ternario()
        if self.i != len(self.t):
            raise NonTradotta('resto')
        return e

    def ternario(self):
        c = self.o()
        if self.vedi('?'):
            self.prendi('?')
            a = self.ternario()
            self.prendi(':')
            return ('cond', c, a, self.ternario())
        return c

    def o(self):
        e = self.e()
        while self.vedi('||'):
            self.prendi()
            e = ('or', e, self.e())
        return e

    def e(self):
        e = self.uguale()
        while self.vedi('&&'):
            self.prendi()
            e = ('and', e, self.uguale())
        return e

    def uguale(self):
        e = self.somma()
        for op in ('==', '!='):
            if self.vedi(op):
                self.prendi()
                d = self.somma()
                r = ('eq', e, d)
                return ('not', r) if op == '!=' else r
        return e

    def somma(self):
        parti = [self.unario()]
        while self.vedi('+'):
            self.prendi()
            parti.append(self.unario())
        return parti[0] if len(parti) == 1 else ('concat', parti)

    def unario(self):
        if self.vedi('!'):
            self.prendi()
            return ('not', self.unario())
        return self.postfisso(self.primario())

    def argomenti(self):
        self.prendi('(')
        args = []
        while not self.vedi(')'):
            args.append(self.ternario())
            if self.vedi(','):
                self.prendi()
        self.prendi(')')
        return args

    def postfisso(self, e):
        while self.vedi('.'):
            self.prendi()
            metodo = self.prendi()[1]
            args = self.argomenti()
            if metodo == 'equals' and args == [('null',)]:
                e = ('isnull', e)
            elif metodo == 'equals' and len(args) == 1:
                e = ('eq', e, args[0])
            elif metodo == 'toUpperCase' and not args:
                e = ('upper', e)
            elif metodo in ('toString', 'trim') and not args:
                pass
            elif metodo == 'format' and e[0] == 'decimalformat':
                arg = args[0]
                if arg[0] == 'doublevalueof':
                    arg = arg[1]
                e = ('fmt', e[1], arg)
            else:
                raise NonTradotta(f'metodo {metodo}')
        return e

    def primario(self):
        tipo, v = self.prendi()
        if tipo == 'str':
            return ('lit', re.sub(r'\\(.)', r'\1', v[1:-1]))
        if tipo == 'ref':
            return ('ref', v)
        if tipo == 'num':
            return ('num', v)
        if v == '(':
            e = self.ternario()
            self.prendi(')')
            return e
        if v in ('true', 'false'):
            return ('bool', v == 'true')
        if v == 'null':
            return ('null',)
        if v == 'new':
            classe = self.prendi()[1]
            args = self.argomenti()
            if classe == 'Boolean' and len(args) == 1:
                return args[0]
            if classe in ('java.text.DecimalFormat', 'DecimalFormat') and args and args[0][0] == 'lit':
                m = re.fullmatch(r'#,###,##0\.00(.*)', args[0][1])
                if m:
                    return ('decimalformat', m.group(1))
            if classe == 'DecimalFormatSymbols':
                return ('locale',)
            raise NonTradotta(f'new {classe}')
        if v == 'Double.valueOf':
            return ('doublevalueof', self.argomenti()[0])
        if v == 'Locale.ITALY':
            return ('locale',)
        raise NonTradotta(v)


# toUpperCase di Java; la ß, che diventa "SS", resta com'e'
MINUSCOLE = 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž'
MAIUSCOLE = 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ'


def xpath_ref(ref, ctx):
    tipo, nome = ref[1], ref[3:-1]
    if ref in NUMERI_NOTI:
        return NUMERI_NOTI[ref]
    if tipo == 'P':
        return ctx.p(nome)
    if tipo == 'F':
        return ctx.f(nome)
    raise NonTradotta(ref)


def letterale_xpath(s):
    if "'" not in s:
        return f"'{html.escape(s, quote=False)}'"
    if '"' not in s:
        return '"' + html.escape(s, quote=False) + '"'
    raise NonTradotta('apici')


def booleano(n, ctx):
    """Test XPath di un'espressione booleana."""
    k = n[0]
    if k == 'isnull':
        return f'not(normalize-space({valore_xpath(n[1], ctx)}))'
    if k == 'not':
        return f'not({booleano(n[1], ctx)})'
    if k in ('and', 'or'):
        return f'({booleano(n[1], ctx)}) {k} ({booleano(n[2], ctx)})'
    if k == 'bool':
        return 'true()' if n[1] else 'false()'
    if k == 'eq':
        a, b = n[1], n[2]
        if b[0] == 'bool':
            return booleano(a, ctx) if b[1] else f'not({booleano(a, ctx)})'
        if b[0] == 'null':
            return booleano(('isnull', a), ctx)
        return f'{valore_xpath(a, ctx)} = {valore_xpath(b, ctx)}'
    if k == 'ref':
        if n[1] in BOOLEANI_NOTI:
            return BOOLEANI_NOTI[n[1]]
        # parametro booleano (per esempio poste)
        return f"{xpath_ref(n[1], ctx)} = 'true'"
    raise NonTradotta(f'condizione {k}')


def valore_xpath(n, ctx):
    """Espressione XPath di un valore semplice (riferimento, letterale, maiuscolo, importo)."""
    k = n[0]
    if k == 'ref':
        return xpath_ref(n[1], ctx)
    if k == 'lit':
        return letterale_xpath(n[1])
    if k == 'num':
        return n[1]
    if k == 'upper':
        return f"translate({valore_xpath(n[1], ctx)}, '{MINUSCOLE}', '{MAIUSCOLE}')"
    if k == 'fmt':
        numero = f"format-number(number({valore_xpath(n[2], ctx)}), '#.##0,00', 'it')"
        return f"concat({numero}, {letterale_xpath(n[1])})" if n[1] else numero
    if k == 'concat':
        return 'concat(' + ', '.join(valore_xpath(x, ctx) for x in n[1]) + ", '')"
    raise NonTradotta(f'valore {k}')


def frammento(n, ctx):
    """Frammento XSLT che produce il testo dell'espressione."""
    k = n[0]
    if k == 'lit':
        return f'<xsl:text>{html.escape(n[1], quote=False)}</xsl:text>' if n[1] else ''
    if k == 'null':
        return ''
    if k == 'concat':
        # come in Java, un valore assente concatenato a una stringa diventa "null": Jasper lo stampa
        return ''.join(f'<xsl:choose><xsl:when test="{valore_xpath(x, ctx)}"><xsl:value-of select="{valore_xpath(x, ctx)}"/>'
                       f'</xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose>' if x[0] == 'ref' else frammento(x, ctx)
                       for x in n[1])
    if k == 'cond':
        return (f'<xsl:choose><xsl:when test="{booleano(n[1], ctx)}">{frammento(n[2], ctx)}</xsl:when>'
                f'<xsl:otherwise>{frammento(n[3], ctx)}</xsl:otherwise></xsl:choose>')
    return f'<xsl:value-of select="{valore_xpath(n, ctx)}"/>'


def testo_composto(espr, ctx):
    """Frammento XSLT del valore di un'espressione composta, None se non e' traducibile."""
    try:
        return frammento(Parser(espr).tutto(), ctx)
    except (NonTradotta, IndexError, ValueError):
        return None


def condizione_composta(espr, ctx):
    """Test XPath di una condizione composta, None se non e' traducibile."""
    try:
        return booleano(Parser(espr).tutto(), ctx)
    except (NonTradotta, IndexError, ValueError):
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
SOVRAPPOSIZIONE = 1.0 # pt di sovrapposizione tra elementi che per le righe rimosse non contano
RIMOVIBILI = {}       # id(reportElement) -> condizione XPath di elemento vuoto
SPOSTAMENTO = None    # spostamento dell'elemento in conversione
DICHIARAZIONI = []    # variabili XSLT degli allungamenti della banda in conversione, da dichiarare in testa
ALLUNGAMENTO = ''     # espressione XPath dei punti di cui si allunga la banda in conversione
FLUSSO = False        # report in flusso (--flusso): i sottoreport vanno in pagina con i salti di pagina di FOP
ALTEZZA_BANDA = 0.0   # altezza dichiarata della banda in conversione


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


def figli(contenitore_xml):
    """Elementi di una banda o di un frame: il contenuto degli elementGroup (gruppi senza posizione propria,
    con le coordinate della banda) e' trattato come contenuto diretto."""
    for el in contenitore_xml:
        if el.tag == f'{J}elementGroup':
            yield from figli(el)
        else:
            yield el


def analizza_banda(banda, ctx, base):
    """Calcola gli spostamenti degli elementi della banda; base e' lo spostamento della banda stessa (righe
    rimosse nelle bande precedenti). Restituisce lo spostamento della banda successiva."""
    elementi_banda = [(el, el.find(f'{J}reportElement')) for el in figli(banda) if el.find(f'{J}reportElement') is not None]
    num = lambda e, k: float(e.get(k, 0))
    # elementi che possono essere vuoti, raggruppati quando le loro fasce verticali si sovrappongono:
    # Jasper toglie una fascia solo se tutti gli elementi che la occupano sono vuoti, quindi un gruppo si
    # rimuove quando tutti i suoi elementi sono vuoti. Un elemento che non puo' essere vuoto e tocca il
    # gruppo lo rende non rimovibile.
    candidati = [(num(re_el, 'y'), num(re_el, 'y') + num(re_el, 'height'), c, re_el)
                 for el, re_el in elementi_banda if (c := condizione_vuoto(el, ctx))]
    # Una sovrapposizione di 1 pt non conta: nell'avviso bilingue senza indirizzo del destinatario Jasper
    # toglie la fascia degli indirizzi (136-167) anche se le informazioni dell'ente, stampate, partono da 166,
    # e le sposta in su con il resto
    gruppi = []
    for cand in sorted(candidati, key=lambda t: t[0]):
        if gruppi and cand[0] < max(t[1] for t in gruppi[-1]) - SOVRAPPOSIZIONE:
            gruppi[-1].append(cand)
        else:
            gruppi.append([cand])
    rimovibili = []
    for g in gruppi:
        membri = {id(t[3]) for t in g}
        inizio, fine = min(t[0] for t in g), max(t[1] for t in g)
        if any(id(o) not in membri and num(o, 'y') < fine - SOVRAPPOSIZIONE and num(o, 'y') + num(o, 'height') > inizio + SOVRAPPOSIZIONE
               for _, o in elementi_banda):
            continue
        # Jasper toglie l'altezza dell'elemento piu' alto del gruppo, non l'unione delle fasce: nella
        # violazione CdS senza indirizzo, settore ente (25 pt) e indirizzi (16 + 16 pt, sovrapposti di 1)
        # spostano il resto di 25 pt
        altezza = max(t[1] - t[0] for t in g)
        c = ' and '.join(f'({t[2]})' for t in g) if len(g) > 1 else g[0][2]
        rimovibili.append((inizio, altezza, c, fine))
        for t in g:
            RIMOVIBILI[id(t[3])] = c
    # sottoreport: Jasper li allunga fino al loro contenuto (righe di record per altezza del record), e gli
    # elementi Float sotto scendono di quanto l'altezza supera quella dichiarata
    allungati = []
    for el, re_el in elementi_banda:
        if el.tag == f'{J}subreport' and (dati := dati_sottoreport(el)) is not None:
            _, selezione, colonne, _, altezza, _ = dati
            pw = re_el.findtext(f'{J}printWhenExpression')
            c = (personalizzazioni['condizione'].get(re_el.get('uuid')) or condizione(pw, ctx)) if pw else None
            # Jasper non accorcia un sottoreport sotto l'altezza dichiarata: DoppioFormatoV2, alto 211 pt in un
            # riquadro di 212, non sposta in su gli elementi sotto
            contenuto = f'ceiling(count({selezione}) div {colonne}) * {altezza}'
            dichiarata = pt(num(re_el, "height"))[:-2]
            # massimo con zero senza confronti, perche' l'espressione finisce sia negli attributi sia nei select:
            # (x + |x|) / 2, con |x| ottenuto togliendo il segno
            x = f'({contenuto} - {dichiarata})'
            allungamento = f"(({x} + number(translate(string({x}), '-', ''))) div 2)"
            allungati.append((num(re_el, 'y') + num(re_el, 'height'), f'{allungamento} * number({c})' if c else allungamento, re_el))
    # testi che si allungano: raggruppati quando le loro fasce verticali si sovrappongono, perche' per gli
    # elementi affiancati conta il piu' alto; i gruppi uno sotto l'altro si sommano
    global ALLUNGAMENTO
    testi = []
    for el, re_el in elementi_banda:
        pw = re_el.findtext(f'{J}printWhenExpression')
        c = (personalizzazioni['condizione'].get(re_el.get('uuid')) or condizione(pw, ctx)) if pw else None
        if (v := allungamento_testo(el, ctx, c)) is not None:
            DICHIARAZIONI.append(v[1])
            testi.append((num(re_el, 'y'), num(re_el, 'y') + num(re_el, 'height'), f'number(${v[0]})', re_el))
    gruppi_testi = []
    for t in sorted(testi, key=lambda t: t[0]):
        if gruppi_testi and t[0] < max(u[1] for u in gruppi_testi[-1]):
            gruppi_testi[-1].append(t)
        else:
            gruppi_testi.append([t])
    for g in gruppi_testi:
        espr = g[0][2]
        for t in g[1:]:
            # massimo in XPath 1.0
            espr = f'(number({espr} &gt; {t[2]}) * {espr} + number(not({espr} &gt; {t[2]})) * {t[2]})'
        if len(g) > 1:
            nome = 'allungamento-' + g[0][3].get('uuid')[:8] + '-gruppo'
            DICHIARAZIONI.append(f'<xsl:variable name="{nome}" select="{espr}"/>')
            espr = f'${nome}'
        allungati.append((max(t[1] for t in g), espr, None))
    ALLUNGAMENTO = ' + '.join(espr for _, espr, r in allungati if r is None)
    for el, re_el in elementi_banda:
        termini = [base] if base else []
        if re_el.get('positionType') == 'Float':
            termini += [f'{pt(round(h, 3))[:-2]} * number({c})' for y, h, c, fine in rimovibili if fine - SOVRAPPOSIZIONE <= num(re_el, 'y')]
            termini += [f'-{a}' for fondo, a, r in allungati if r is not re_el and fondo <= num(re_el, 'y')]
        if termini:
            SPOSTAMENTI[id(re_el)] = ' + '.join(termini)
    termini = ([base] if base else []) + [f'{pt(round(h, 3))[:-2]} * number({c})' for _, h, c, _ in rimovibili]
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
    blocco.update(padding_block(el, avvisa=False))
    cont = contenitore(re_el, ox, oy)
    # padding verticale del riquadro: Jasper sposta il testo in basso e riduce l'altezza utile
    sopra, sotto = padding_verticale(el)
    altezza = float(re_el.get('height')) - sopra - sotto

    # numero di righe: per i testi fissi dagli a capo calcolati, per gli altri dall'indicazione
    # righe=N nelle personalizzazioni (default 1)
    extra = dict(personalizzazioni['attributi'].get(re_el.get('uuid'), {}))
    costante = testo_costante(el)
    righe_xpath = extra.pop('righe-xpath', None)
    righe = int(extra.pop('righe', 0))
    extra.pop('vuoto', None)
    # testo variabile: a capo calcolati a runtime come in Jasper (TestoJasper.righe). Con markup, solo se il
    # valore non contiene tag: altrimenti resta il template markup, con gli a capo scelti da FOP
    a_runtime = None
    if not righe_xpath and not righe:
        a_runtime = testo_a_runtime(el, ctx)
        if a_runtime:
            righe_xpath = 'tj:numeroRighe(string($testo-righe))'
    posizioni = a_capo.get(re_el.get('uuid')) if costante is not None else None
    if not righe:
        righe = sum(len(v) + 1 for v in posizioni.values()) if posizioni else 1
    blocco.update(geometria_testo(el, blocco, righe, altezza))
    if sopra and 'padding-top' in blocco and 'padding-top' not in extra:
        blocco['padding-top'] = pt(round(float(blocco['padding-top'][:-2]) + sopra, 3))
    blocco.update(extra)
    variabile = ''
    if righe_xpath:
        # numero di righe calcolato dal contenuto: interlinea e padding calcolati a runtime da TestoJasper.
        # La geometria vale fino a un limite di righe, almeno 5, le righe che stanno nel riquadro (10 se il
        # riquadro si allunga); oltre, resta quella del limite
        massimo_righe = righe_massime(el, blocco, altezza)
        ultimo = max(5, massimo_righe) if massimo_righe else (10 if el.get('isStretchWithOverflow') == 'true' else 5)
        te = el.find(f'{J}textElement')
        verticale = (te.get('verticalAlignment') if te is not None else None) or 'Top'
        font = f"'{blocco['font-family']}', {num_xpath(float(blocco['font-size'][:-2]))}, {num_xpath(interlinea_fissa(el))}"
        blocco.pop('line-height', None); blocco.pop('padding-top', None)
        variabile = (f'<xsl:variable name="righe" select="{righe_xpath}"/>'
                     f'<xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, {ultimo}, {font})"/></xsl:attribute>'
                     f'<xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, {ultimo}, {font}, '
                     f"'{verticale}', {num_xpath(altezza)}, {num_xpath(sopra)})\"/></xsl:attribute>")
    if a_runtime:
        xp, con_tag, scelta, _ = a_runtime
        variabile = f'<xsl:variable name="testo-righe">{scelta}</xsl:variable>' + variabile
        if xp == '$valore':
            # espressione composta: con markup le righe sono nodi XSL-FO
            corpo = '<xsl:copy-of select="$testo-righe"/>'
        elif markup == 'none':
            corpo = '<xsl:value-of select="$testo-righe"/>'
        else:
            corpo = (f'<xsl:choose><xsl:when test="{con_tag}"><xsl:call-template name="markup"><xsl:with-param name="testo" select="{xp}"/></xsl:call-template></xsl:when>'
                     f'<xsl:otherwise><xsl:value-of select="$testo-righe"/></xsl:otherwise></xsl:choose>')
        blocco['linefeed-treatment'] = 'preserve'
        blocco['wrap-option'] = 'no-wrap'
        if 'tj:stile' in scelta and blocco['font-family'].startswith('TitilliumWeb'):
            # come TestoJasper.stileBlocco: il blocco in TitilliumWeb normale, i tratti nel font del riquadro
            # sono fo:inline (FOP allarga la riga per un inline con capHeight maggiore di quella del blocco)
            blocco['font-family'] = 'TitilliumWeb'
            blocco.pop('font-weight', None)
            blocco.pop('font-style', None)
    if re_el.get('uuid') in personalizzazioni['elemento']:
        corpo = personalizzazioni['elemento'][re_el.get('uuid')]
    elif costante is not None:
        run = run_statici(costante.strip('\n'), markup)
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


def testo_a_runtime(el, ctx):
    """Per un testo variabile con gli a capo calcolati a runtime come in Jasper: (XPath del valore, condizione
    XPath di valore con tag, istruzioni XSLT che producono il testo con gli a capo, espressione XPath del testo
    con gli a capo o None se servono le istruzioni). None se il testo non e' di questo tipo.
    - Senza markup: TestoJasper.righe sul valore, semplice o composto (testo_composto) in una variabile.
    - Con markup: TestoJasper.stile, che interpreta i tag come Jasper (anche gli a capo <br/>) e restituisce
      le righe come nodi XSL-FO."""
    re_el = el.find(f'{J}reportElement')
    te = el.find(f'{J}textElement')
    markup = (te.get('markup') if te is not None else None) or 'none'
    attr = personalizzazioni['attributi'].get(re_el.get('uuid'), {})
    if el.tag != f'{J}textField' or testo_costante(el) is not None or re_el.get('uuid') in personalizzazioni['elemento'] \
            or 'righe-xpath' in attr or 'righe' in attr or markup not in ('none', 'styled', 'html'):
        return None
    blocco = font_attrs(el)
    blocco.update(padding_block(el, avvisa=False))
    espr = el.findtext(f'{J}textFieldExpression') or ''
    xp = xpath_semplice(espr, ctx)
    composto = None if xp else testo_composto(espr, ctx)
    if xp and markup != 'none':
        # valore semplice con markup: i tag del valore si interpretano come per le espressioni composte
        composto = f'<xsl:value-of select="{xp}"/>'
    ff = file_font(blocco, {})
    if not (xp or composto) or not ff:
        return None
    sopra, sotto = padding_verticale(el)
    larghezza = float(re_el.get('width')) - float(blocco.get('start-indent', '0pt')[:-2]) - float(blocco.get('end-indent', '0pt')[:-2])
    massimo = righe_massime(el, blocco, float(re_el.get('height')) - sopra - sotto)
    proprieta = {p.get('name'): p.get('value') for p in re_el.findall(f'{J}property')}
    tronca = 'true()' if proprieta.get('net.sf.jasperreports.text.truncate.at.char') == 'true' else 'false()'
    if composto is not None and markup != 'none':
        colore = re_el.get('forecolor') or ''
        grassetto = 'true()' if blocco.get('font-weight') == 'bold' else 'false()'
        corsivo = 'true()' if blocco.get('font-style') == 'italic' else 'false()'
        chiamata = (f"tj:stile(string($valore), '{markup}', '{blocco['font-family']}', {grassetto}, {corsivo}, {ff[1]}, "
                    f"'{colore}', {round(larghezza, 3)}, {massimo}, {tronca})")
        scelta = f'<xsl:variable name="valore">{composto}</xsl:variable><xsl:copy-of select="{chiamata}"/>'
        return '$valore', 'false()', scelta, None
    if composto is not None:
        chiamata = f"tj:righe(string($valore), '{ff[0]}', {ff[1]}, {round(larghezza, 3)}, {massimo}, {tronca})"
        scelta = f'<xsl:variable name="valore">{composto}</xsl:variable><xsl:value-of select="{chiamata}"/>'
        return '$valore', 'false()', scelta, None
    chiamata = f"tj:righe(string({xp}), '{ff[0]}', {ff[1]}, {round(larghezza, 3)}, {massimo}, {tronca})"
    if markup == 'none':
        return xp, 'false()', f'<xsl:value-of select="{chiamata}"/>', chiamata
    # valore con tag: resta intero, e il blocco usa il template markup
    con_tag = f"contains({xp}, '&lt;')"
    scelta = (f'<xsl:choose><xsl:when test="{con_tag}"><xsl:value-of select="{xp}"/></xsl:when>'
              f'<xsl:otherwise><xsl:value-of select="{chiamata}"/></xsl:otherwise></xsl:choose>')
    return xp, con_tag, scelta, None


def padding_verticale(el):
    box = el.find(f'{J}box')
    sopra = float((box.get('topPadding') or box.get('padding') or 0)) if box is not None else 0.0
    sotto = float((box.get('bottomPadding') or box.get('padding') or 0)) if box is not None else 0.0
    return sopra, sotto


def allungamento_testo(el, ctx, cond):
    """Testo che si allunga (isStretchWithOverflow) con gli a capo calcolati a runtime: (nome, dichiarazione)
    della variabile XSLT con i punti di cui Jasper allunga il riquadro, None se il testo non e' di questo tipo.
    Jasper alza il riquadro fino all'altezza del testo, arrotondata per eccesso, piu' i padding verticali:
    (A + D) x corpo per la prima riga e, per le successive, lineSpacingSize + D x corpo con Fixed e
    (A + D) x corpo con Single. Misurato sui PDF della ricevuta: oggetto 14 pt Fixed 12 su 2 righe, 39 pt
    (+17); voce 12 pt Fixed 8 su 2 righe, 31 pt (+11). cond e' la condizione di stampa dell'elemento."""
    re_el = el.find(f'{J}reportElement')
    if el.get('isStretchWithOverflow') != 'true' and el.get('textAdjust') != 'StretchHeight':
        return None
    runtime = testo_a_runtime(el, ctx)
    metriche = METRICHE_AWT.get(famiglia(font_attrs(el)['font-family']))
    if runtime is None or metriche is None:
        return None
    sopra, sotto = padding_verticale(el)
    nome = 'allungamento-' + re_el.get('uuid')[:8]
    parametri = (f"'{font_attrs(el)['font-family']}', {num_xpath(float(font_attrs(el)['font-size'][:-2]))}, "
                 f"{num_xpath(interlinea_fissa(el))}, {num_xpath(sopra + sotto)}, {num_xpath(float(re_el.get('height')))}")
    fattore = f' * number({cond})' if cond else ''
    if runtime[3]:
        return nome, f'<xsl:variable name="{nome}" select="tj:allungamento({runtime[3]}, {parametri}){fattore}"/>'
    return nome, (f'<xsl:variable name="{nome}"><xsl:variable name="t">{runtime[2]}</xsl:variable>'
                  f'<xsl:value-of select="tj:allungamento(string($t), {parametri}){fattore}"/></xsl:variable>')


def righe_massime(el, blocco, altezza):
    """Righe che Jasper mostra in un riquadro che non si allunga: aggiunge righe finche' la prima, alta
    (A + D) x corpo, piu' le successive, alte lineSpacingSize + D x corpo con Fixed e (A + D) x corpo con
    Single, stanno nel riquadro. Regola ricavata dai PDF di riferimento: titolo 16 pt Fixed 11 in 42 pt,
    2 righe; titolo 14 pt Fixed 11 in 42 pt, 2 righe; oggetto 9 pt Fixed 7 in 24 pt, 1 riga.
    0 se il riquadro si allunga (isStretchWithOverflow)."""
    if el.get('isStretchWithOverflow') == 'true' or el.get('textAdjust') == 'StretchHeight':
        return 0
    metriche = METRICHE_AWT.get(famiglia(blocco['font-family']))
    corpo = float(blocco['font-size'][:-2])
    if metriche is None:
        return 0
    prima = (metriche[0] + metriche[1]) * corpo
    p = el.find(f'{J}textElement/{J}paragraph')
    fissa = p is not None and p.get('lineSpacing') == 'Fixed' and p.get('lineSpacingSize')
    successive = float(p.get('lineSpacingSize')) + metriche[1] * corpo if fissa else prima
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
    m_logo = re.search(r'decodeBase64\(\(\(\$([PF])\{(\w+)\}\.contains', espr)
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
        xp = ctx.p(m_logo.group(2)) if m_logo.group(1) == 'P' else ctx.f(m_logo.group(2))
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
    dati = dati_sottoreport(el)
    if dati is None:
        todo.append('subreport')
        out.append(f'{rientro}<!-- TODO sottoreport: {commento(el.findtext(f"{J}subreportExpression") or "")} -->')
        return
    nome, selezione, colonne, larghezza, altezza, costanti = dati
    x = float(re_el.get('x', 0)) + ox
    y = float(re_el.get('y', 0)) + oy
    sopra = ''
    if SPOSTAMENTO:
        # lo spostamento si calcola nel contesto del report, prima di entrare nei record
        variabile = 'spostamento-' + re_el.get('uuid', 'sottoreport')[:8]
        out.append(f'{rientro}<xsl:variable name="{variabile}" select="{SPOSTAMENTO}"/>')
        sopra = f' - ${variabile}'
    if FLUSSO:
        # report in flusso: il sottoreport parte dopo uno spazio alto quanto la sua posizione, e i record
        # seguono nel flusso, uno per riga. Come in Jasper, un record va nella pagina se ci sta la sua altezza
        # dichiarata, altrimenti passa alla successiva; l'allungamento calcolato dal template <nome>-altezza
        # del sottoreport (generato con --altezza-record) e' uno spazio dopo il record, che a fine pagina si
        # scarta: nei PDF di riferimento la voce allungata che finisce il testo sul margine inferiore chiude
        # la pagina e la successiva riparte dal margine. Lo spazio della banda sotto il sottoreport resta
        # invece un blocco: con le voci che riempiono la pagina fino al margine, Jasper aggiunge una pagina.
        if colonne != 1:
            todo.append('sottoreport a piu\' colonne in flusso')
        out.append(f'{rientro}<fo:block-container height="{{{y}{sopra}}}pt"><fo:block/></fo:block-container>')
        out.append(f'{rientro}<xsl:for-each select="{selezione}">')
        out.append(f'{rientro}\t<xsl:variable name="altezza-record"><xsl:call-template name="{nome}-altezza"/></xsl:variable>')
        out.append(f'{rientro}\t<fo:block-container height="{pt(altezza)}" space-after="{{$altezza-record - {altezza:g}}}pt" keep-together.within-page="always">')
        out.append(f'{rientro}\t\t<fo:block-container absolute-position="absolute" left="{pt(x)}" top="0pt" width="{pt(larghezza)}" height="{{$altezza-record}}pt">')
        out.append(f'{rientro}\t\t\t<xsl:call-template name="{nome}">')
        for k, v in costanti.items():
            out.append(f'{rientro}\t\t\t\t<xsl:with-param name="{k}" select="{v}"/>')
        out.append(f'{rientro}\t\t\t</xsl:call-template>')
        out.append(f'{rientro}\t\t</fo:block-container>')
        out.append(f'{rientro}\t</fo:block-container>')
        out.append(f'{rientro}</xsl:for-each>')
        coda = ALTEZZA_BANDA - float(re_el.get('y', 0)) - float(re_el.get('height', 0))
        if coda > 0:
            out.append(f'{rientro}<fo:block-container height="{pt(coda)}"><fo:block/></fo:block-container>')
        return
    out.append(f'{rientro}<xsl:for-each select="{selezione}">')
    out.append(f'{rientro}\t<fo:block-container absolute-position="absolute" left="{{{x} + ((position() - 1) mod {colonne}) * {larghezza}}}pt"'
               f' top="{{{y}{sopra} + floor((position() - 1) div {colonne}) * {altezza}}}pt" width="{pt(larghezza)}" height="{pt(altezza)}">')
    # un blocco vuoto: il record puo' non stampare nulla (per esempio il bollettino senza poste), e FOP
    # vuole almeno un blocco nel riquadro
    out.append(f'{rientro}\t\t<fo:block/>')
    out.append(f'{rientro}\t\t<xsl:call-template name="{nome}">')
    for k, v in costanti.items():
        out.append(f'{rientro}\t\t\t<xsl:with-param name="{k}" select="{v}"/>')
    out.append(f'{rientro}\t\t</xsl:call-template>')
    out.append(f'{rientro}\t</fo:block-container>')
    out.append(f'{rientro}</xsl:for-each>')


def dati_sottoreport(el):
    """(nome, selezione XSLT dei record, colonne, larghezza colonna, altezza record, parametri costanti) di un
    sottoreport su sorgente XML, None se non e' di questo tipo."""
    m_nome = re.fullmatch(r'\s*(?:\$P\{(\w+)\}|"(\w+)\.jasper")\s*', el.findtext(f'{J}subreportExpression') or '')
    m_ds = re.search(r'subDataSource\("([^"]+)"\)', el.findtext(f'{J}dataSourceExpression') or '')
    if not m_nome or not m_ds:
        return None
    nome = m_nome.group(1) or m_nome.group(2)
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
    return nome, selezione, colonne, larghezza, altezza, costanti


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
    # in flusso i sottoreport vanno per ultimi: gli elementi assoluti si ancorano al punto del flusso in cui
    # sono scritti, e dopo i record finirebbero nell'ultima pagina
    for el in sorted(figli(contenitore_xml), key=lambda e: FLUSSO and e.tag == f'{J}subreport'):
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
    ap.add_argument('--param-campi', metavar='JRXML',
                    help='report principale i cui campi arrivano al sottoreport come parametri omonimi: $P{x} '
                         'diventa l\'XPath assoluto del campo x (per esempio le etichette di AvvisoPagamentoV2)')
    ap.add_argument('--flusso', action='store_true',
                    help='report che va su piu\' pagine: posizioni relative alla region-body, che ha i margini '
                         'del report, e sottoreport nel flusso di FOP')
    ap.add_argument('--altezza-record', action='store_true',
                    help='genera anche il template <nome>-altezza, con l\'altezza di un record allungato dai suoi testi')
    a = ap.parse_args()
    global DIR_JRXML, FLUSSO, ALTEZZA_BANDA
    FLUSSO = a.flusso
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
    param_campi = {}
    if a.param_campi:
        principale = ET.parse(a.param_campi).getroot()
        radice = (principale.findtext(f'{J}queryString') or '').strip().rstrip('/')
        for f in principale.findall(f'{J}field'):
            d = (f.findtext(f'{J}fieldDescription') or f.get('name')).strip()
            param_campi[f.get('name')] = d if d.startswith('/') else f'{radice}/{d}'
    ctx = Contesto(campi, a.param_xpath, parametri, param_campi)

    calcola_a_capo(r)
    ox = a.offset_x + float(r.get('leftMargin', 0))
    # in flusso la region-body ha i margini superiore e inferiore del report: le posizioni partono da li'
    oy = a.offset_y + (0 if a.flusso else float(r.get('topMargin', 0)))
    out = []
    base = ''
    altezza_record = []
    for banda in BANDE:
        for sezione in r.findall(f'{J}{banda}'):
            for b in sezione.iter(f'{J}band'):
                out.append(f'\t\t<!-- banda {banda}, y={oy - a.offset_y - (0 if a.flusso else float(r.get("topMargin", 0)))} -->')
                pw = b.findtext(f'{J}printWhenExpression')
                c = condizione(pw, ctx) if pw else None
                DICHIARAZIONI.clear()
                ALTEZZA_BANDA = float(b.get('height', 0))
                successiva = analizza_banda(b, ctx, base)
                out.extend(f'\t\t{d}' for d in DICHIARAZIONI)
                if banda == 'detail':
                    altezza_record.extend(DICHIARAZIONI)
                    altezza_record.append(f'{float(b.get("height", 0)):g}' + (f' + {ALLUNGAMENTO}' if ALLUNGAMENTO else ''))
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
    if a.altezza_record:
        # altezza di un record: bande di dettaglio, ciascuna allungata dai suoi testi
        print()
        print(f'\t<xsl:template name="{nome}-altezza">')
        for d in altezza_record:
            if d.startswith('<'):
                print(f'\t\t{d}')
        print(f'\t\t<xsl:value-of select="{" + ".join(d for d in altezza_record if not d.startswith("<"))}"/>')
        print('\t</xsl:template>')
    print()
    print('</xsl:stylesheet>')

    if todo:
        from collections import Counter
        print('TODO: ' + ', '.join(f'{k} x{v}' for k, v in Counter(todo).items()), file=sys.stderr)


if __name__ == '__main__':
    main()
