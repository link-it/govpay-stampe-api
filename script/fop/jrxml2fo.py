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
                 un'immagine, <condizione uuid="..."> la printWhenExpression

Il file generato va su stdout.
"""

import argparse
import html
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


def carica_personalizzazioni(path):
    # lettura testuale: i frammenti vanno copiati cosi' come sono scritti, con i prefissi xsl: e fo:
    testo = open(path, encoding='utf-8').read()
    testo = re.sub(r'<!--.*?-->', '', testo, flags=re.S)
    for tipo in ('elemento', 'condizione'):
        for uuid, extra, corpo in re.findall(r'<%s uuid="([^"]+)"([^>]*)>(.*?)</%s>' % (tipo, tipo), testo, re.S):
            personalizzazioni[tipo][uuid] = corpo.strip()
            # attributi="nome=valore;nome=valore": sostituiscono o aggiungono attributi del blocco di testo
            m = re.search(r'attributi="([^"]*)"', extra)
            if m:
                personalizzazioni['attributi'][uuid] = dict(
                    kv.split('=', 1) for kv in m.group(1).split(';') if kv.strip())


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
    # Interlinea tarata sui PDF prodotti da Jasper: per Titillium la distanza tra le righe misurata
    # e' 1,266 volte il corpo, anche quando lineSpacing="Fixed" indica un valore piu' piccolo
    # (Jasper non scende sotto l'altezza utile del font). Un valore fisso piu' grande viene rispettato.
    corpo = float(size)
    interlinea = corpo * INTERLINEA.get(famiglia(nome), 1.2)
    p = el.find(f'{J}textElement/{J}paragraph')
    if p is not None and p.get('lineSpacing') == 'Fixed' and p.get('lineSpacingSize'):
        interlinea = max(interlinea, float(p.get('lineSpacingSize')))
    a['line-height'] = pt(round(interlinea, 2))
    return a


# famiglia di font -> rapporto interlinea/corpo misurato sui PDF di Jasper
INTERLINEA = {'TitilliumWeb': 1.266, 'RobotoMono': 1.266, 'Roboto': 1.266}
# spostamento verso il basso della prima riga, in frazione del corpo: Jasper allinea la prima linea
# di base all'ascendente del font, FOP la alza di meta' della differenza tra interlinea e altezza del font
ALLINEAMENTO_PRIMA_RIGA = {'TitilliumWeb': 0.217, 'RobotoMono': 0.0, 'Roboto': 0.217}


def famiglia(nome):
    return nome.split('-')[0]


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
    def __init__(self, campi, param_xpath):
        self.campi = campi
        self.param_xpath = param_xpath.rstrip('/')

    def p(self, nome):
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


def condizione(espr, ctx):
    """Traduce le printWhenExpression piu' comuni in un test XPath."""
    e = re.sub(r'\s+', '', espr)
    def nodo(t, n):
        return ctx.p(n) if t == 'P' else ctx.f(n)
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\.equals\(null\)\)', e)
    if m:
        return f'not(normalize-space({nodo(*m.groups())}))'
    m = re.fullmatch(r'newBoolean\(!\$([PF])\{(\w+)\}\.equals\(null\)\)', e)
    if m:
        return f'normalize-space({nodo(*m.groups())})'
    m = re.fullmatch(r'newBoolean\(\$([PF])\{(\w+)\}\.equals\(null\)\)==false', e)
    if m:
        return f'normalize-space({nodo(*m.groups())})'
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

def contenitore(re_el, ox, oy, extra=None):
    x = float(re_el.get('x', 0)) + ox
    y = float(re_el.get('y', 0)) + oy
    a = {'absolute-position': 'absolute', 'left': pt(x), 'top': pt(y),
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
    verticale = VERTICALE.get(te.get('verticalAlignment') if te is not None else None, 'before')
    if verticale != 'before':
        cont['display-align'] = verticale
    else:
        spostamento = float(blocco['font-size'][:-2]) * ALLINEAMENTO_PRIMA_RIGA.get(famiglia(blocco['font-family']), 0)
        if spostamento:
            blocco['padding-top'] = pt(round(spostamento, 2))

    blocco.update(personalizzazioni['attributi'].get(re_el.get('uuid'), {}))
    if re_el.get('uuid') in personalizzazioni['elemento']:
        corpo = personalizzazioni['elemento'][re_el.get('uuid')]
    elif el.tag == f'{J}staticText':
        run = run_statici((el.findtext(f'{J}text') or '').strip('\n'), markup)
        posizioni = a_capo.get(re_el.get('uuid'))
        if posizioni is not None:
            # a capo calcolati come in Jasper: FOP non deve spezzare di nuovo le righe
            blocco['wrap-option'] = 'no-wrap'
        corpo = fo_statico(run, posizioni)
    else:
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
    out.append(f'{rientro}\t<fo:block{attrs(blocco)}>{corpo}</fo:block>')
    out.append(f'{rientro}</fo:block-container>')


def immagine(el, ox, oy, ctx, out, rientro):
    re_el = el.find(f'{J}reportElement')
    espr = el.findtext(f'{J}imageExpression') or ''
    w, h = pt(re_el.get('width')), pt(re_el.get('height'))
    grafica = {'content-width': w, 'content-height': h, 'scaling': 'uniform'}
    cont = contenitore(re_el, ox, oy)
    svg = svg_da_batik(espr)
    m_logo = re.search(r'decodeBase64\(\(\(\$P\{(\w+)\}\.contains', espr)
    out.append(f'{rientro}<fo:block-container{attrs(cont)}>')
    if re_el.get('uuid') in personalizzazioni['elemento']:
        out.append(f"{rientro}\t{personalizzazioni['elemento'][re_el.get('uuid')]}")
    elif svg:
        out.append(f'{rientro}\t<fo:block line-height="0"><fo:instream-foreign-object{attrs(grafica)}>{svg}</fo:instream-foreign-object></fo:block>')
    elif m_logo:
        xp = ctx.p(m_logo.group(1))
        # logo assente: Jasper lascia il riquadro vuoto (onErrorType Blank)
        out.append(f'{rientro}\t<fo:block line-height="0"><xsl:if test="normalize-space({xp})"><fo:external-graphic{attrs(grafica)}>'
                   f'<xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="{xp}"/></xsl:call-template>)</xsl:attribute>'
                   f'</fo:external-graphic></xsl:if></fo:block>')
    else:
        todo.append('immagine')
        out.append(f'{rientro}\t<!-- TODO immagine: {commento(espr)} -->')
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


def commento(s):
    s = re.sub(r'<svg.*?</svg>', '<svg…>', s, flags=re.S)
    return re.sub(r'\s+', ' ', s).replace('--', '- -').strip()


def elementi(contenitore_xml, ox, oy, ctx, out, rientro):
    for el in contenitore_xml:
        tag = el.tag.replace(J, '')
        re_el = el.find(f'{J}reportElement')
        if re_el is None:
            continue
        pw = re_el.findtext(f'{J}printWhenExpression')
        if pw:
            c = personalizzazioni['condizione'].get(re_el.get('uuid')) or condizione(pw, ctx)
            if c is None:
                todo.append('printWhenExpression')
                out.append(f'{rientro}<!-- TODO condizione: {commento(pw)} -->')
                c = 'true()'
            out.append(f'{rientro}<xsl:if test="{c}">')
            r2 = rientro + '\t'
        else:
            r2 = rientro
        if tag in ('staticText', 'textField'):
            testo(el, ox, oy, ctx, out, r2)
        elif tag == 'image':
            immagine(el, ox, oy, ctx, out, r2)
        elif tag in ('line', 'rectangle'):
            linea_o_rettangolo(el, ox, oy, out, r2)
        elif tag == 'frame':
            x = float(re_el.get('x', 0)) + ox
            y = float(re_el.get('y', 0)) + oy
            elementi(el, x, y, ctx, out, r2)
        else:
            todo.append(tag)
            out.append(f'{r2}<!-- TODO elemento {tag} x={re_el.get("x")} y={re_el.get("y")} -->')
        if pw:
            out.append(f'{rientro}</xsl:if>')


def main():
    ap = argparse.ArgumentParser()
    ap.add_argument('jrxml')
    ap.add_argument('--nome')
    ap.add_argument('--offset-x', type=float, default=0)
    ap.add_argument('--offset-y', type=float, default=0)
    ap.add_argument('--param-xpath', default='/AvvisoPagamentoInput')
    ap.add_argument('--personalizzazioni')
    a = ap.parse_args()
    if a.personalizzazioni:
        carica_personalizzazioni(a.personalizzazioni)

    r = ET.parse(a.jrxml).getroot()
    nome = a.nome or r.get('name')
    campi = {f.get('name'): (f.findtext(f'{J}fieldDescription') or f.get('name')).strip()
             for f in r.findall(f'{J}field')}
    ctx = Contesto(campi, a.param_xpath)

    calcola_a_capo(r)
    ox = a.offset_x + float(r.get('leftMargin', 0))
    oy = a.offset_y + float(r.get('topMargin', 0))
    out = []
    for banda in BANDE:
        for sezione in r.findall(f'{J}{banda}'):
            for b in sezione.iter(f'{J}band'):
                out.append(f'\t\t<!-- banda {banda}, y={oy - a.offset_y - float(r.get("topMargin", 0))} -->')
                elementi(b, ox, oy, ctx, out, '\t\t')
                oy += float(b.get('height', 0))

    print('<?xml version="1.0" encoding="UTF-8"?>')
    sorgente = a.jrxml.split('/')[-1]
    print('<!--')
    print(f'\tGENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/{sorgente}: non modificare a mano.')
    if a.personalizzazioni:
        print(f'\tLe parti scritte a mano sono in {a.personalizzazioni}.')
    print('\tRigenerare con script/fop/genera-xsl.sh.')
    print('-->')
    print('<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"')
    print('\txmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg">')
    print()
    print(f'\t<xsl:template name="{nome}">')
    print('\n'.join(out))
    print('\t</xsl:template>')
    print()
    print('</xsl:stylesheet>')

    if todo:
        from collections import Counter
        print('TODO: ' + ', '.join(f'{k} x{v}' for k, v in Counter(todo).items()), file=sys.stderr)


if __name__ == '__main__':
    main()
