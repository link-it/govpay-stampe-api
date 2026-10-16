# Migrazione dei layout da JasperReports a XSL-FO

Strumenti per convertire i template Jasper (`src/main/jasper/*.jrxml`) nei layout XSL-FO usati da
Apache FOP (`src/main/resources/fop/*.xsl`), issue [#37](https://github.com/link-it/govpay-stampe-api/issues/37).

## Flusso

1. `jrxml2fo.py` converte un `.jrxml` nello scheletro di un template XSLT. In automatico, compresi gli
   a capo dei testi fissi calcolati come in Jasper:
   - posizioni e dimensioni degli elementi;
   - sfondi, padding, font e allineamenti;
   - testi fissi con markup, grafica SVG e loghi;
   - espressioni semplici (`$P{x}`, `$F{y}`, controllo di null) e condizioni comuni.

   Le altre espressioni diventano commenti `TODO` con il testo Groovy originale.
2. Le parti che il convertitore non traduce si scrivono in `personalizzazioni/<Template>.xml`, legate
   all'`uuid` dell'elemento nel `.jrxml`. Ogni voce è di uno di tre tipi:
   - `<elemento>`: sostituisce il contenuto di un testo o di un'immagine;
   - `<condizione>`: sostituisce la `printWhenExpression`;
   - `attributi="nome=valore;…"`: cambia gli attributi del blocco.
3. `genera-xsl.sh` rigenera tutti i layout migrati. I file in `src/main/resources/fop/` generati
   da qui non vanno modificati a mano.

Un layout è migrato quando il file generato non contiene più `TODO`.

## Taratura sui PDF di Jasper

La posizione del testo viene da un modello di Jasper e di FOP misurato su report di taratura. Le
combinazioni sono quelle usate nei template: font, corpo da 6 a 24 pt, interlinea `Single` o `Fixed`,
markup, allineamento verticale. Report e misure sono in `pdf-riferimento/taratura/`, che resta locale.

- **Jasper 6.21.5** usa le metriche AWT del font: ascendente A e discendente D per unità di corpo,
  1,133 e 0,388 per Titillium, 1,0479 e 0,2711 per RobotoMono.
  - La prima riga parte dal bordo superiore del riquadro.
  - La distanza tra le righe con `Single` è (A + D) × corpo.
  - Con `Fixed` F è F + D × corpo + (A × corpo − F) / n, dove n è il numero di righe del testo.
  - Il centrato e l'allineamento in basso si applicano all'altezza T = (A + D) × corpo + (n − 1) × distanza.
  - Markup e grassetto non cambiano la distanza.
- **FOP 2.11** distanzia le righe esattamente di `line-height` L e mette la prima riga a (L − K × corpo)/2
  dal bordo del blocco. K è misurato sulla posizione dell'inchiostro: 1,772 per tutte le varianti di
  Titillium, 1,551 per RobotoMono.
  - Le caselle delle parole di `pdftotext` non bastano: Jasper e FOP descrivono il font con ascendenti
    diverse, quindi due caselle allineate non garantiscono caratteri allineati.

`jrxml2fo.py` imposta `line-height` uguale alla distanza di Jasper e un `padding-top` che riproduce
l'allineamento in alto, al centro o in basso. Il numero di righe lo ricava dagli a capo per i testi
fissi. Per i testi variabili lo legge da `righe=N` nelle personalizzazioni, e in sua assenza usa 1.

`fop.xconf` disattiva il kerning (`kerning="no"`) e l'elaborazione OpenType (`complex-scripts`), che
applicherebbe comunque il kerning delle tabelle GPOS: Jasper non ne usa.

**Immagini.**
- **SVG:** ricevono tutto il riquadro come area di disegno e si centrano da soli, come in Jasper.
- **Raster (loghi):** Jasper le scala per stare nel riquadro mantenendo le proporzioni, poi le sposta
  secondo `hAlign`/`vAlign` di uno spazio libero troncato a punti interi. Il renderer aggiunge all'XML le
  dimensioni in pixel di ogni logo (`<logo>_px`) e il layout ripete lo stesso calcolo.
- **Blocco contenitore:** ha corpo 0, perché FOP non allinei l'immagine alla linea di base del testo.

**Righe con font diversi.** Se su una riga ci sono font con ascendenti diverse, per esempio Titillium e
RobotoMono, FOP abbassa la linea di base rispetto a Jasper. Va compensato nella personalizzazione,
come per "Euro 122,50" in `RataUnica` (0,48 pt).

**A capo dei testi fissi.** FOP spezza le righe con l'algoritmo di Knuth-Plass, che bilancia il paragrafo,
mentre Jasper riempie ogni riga finché c'è spazio. Per i testi fissi `jrxml2fo.py` calcola gli a capo come
Jasper con `SpezzaRighe.java`: `LineBreakMeasurer`, gli stessi file di font e la larghezza utile del
riquadro. Le righe vengono poi scritte esplicitamente con `wrap-option="no-wrap"`. Il convertitore
avvia una sola JVM per template e richiede `java` nel PATH.

Differenze note:
- Testi variabili (oggetto, nomi, indirizzi): gli a capo li sceglie FOP, quindi su testi lunghi una
  parola può andare a capo in un punto diverso da Jasper. Se il numero di righe non è quello indicato
  con `righe=N`, l'interlinea fissa differisce di qualche decimo di punto.
- Il grassetto applicato con `<b>` a `TitilliumWeb-Bold`, che non ha una variante bold, è simulato da
  Jasper con un contorno ai caratteri. FOP non lo simula e usa lo stesso file: il testo è meno marcato,
  ma con la stessa larghezza e nella stessa posizione (per esempio "pagopa.gov.it"). `TitilliumWeb-Black`
  è stato provato e scartato perché troppo marcato.

## Criteri di accettazione

Un layout migrato è accettato quando `verifica_pdf.py` passa su tutti i suoi casi di riferimento:

| Criterio | Soglia |
| --- | --- |
| Testo | stesse righe con le stesse parole, quindi stessi a capo |
| Inchiostro per riga | bordi superiore, inferiore, sinistro e destro entro 0,25 pt (misura a 600 dpi) |
| Raster | a 200 dpi con tolleranza di 1 pixel (circa 0,36 pt), al massimo 5 pixel fuori tolleranza, escluse le immagini e le differenze accettate indicate con `--escludi` |
| Immagini | riquadro dell'inchiostro entro 1 pt |
| QR code | letto dal PDF FOP, con il contenuto del `qr_code` dell'XML |
| DataMatrix (bollettino postale) | letto dal PDF FOP, con il contenuto del `data_matrix` dell'XML |

Differenze accettate in `RataUnica` e `RataUnicaPostale`: il grassetto simulato di "pagopa.gov.it" (`--escludi 480,250,100,22`).

Il DataMatrix del bollettino è generato da zxing, mentre Jasper usa barcode4j. Ha la stessa dimensione
(48×48) e lo stesso contenuto, ma il disegno dei moduli è diverso perché le due librerie scelgono una
codifica interna diversa. Per questo si confronta il contenuto letto, non il disegno.

```bash
python3 script/fop/verifica_pdf.py jasper.pdf fop.pdf --fo layout.fo --xml input.xml \
  --classpath "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)" --escludi 480,250,100,22
```

Il file FO (`layout.fo`) si ottiene dall'XML con `xsltproc src/main/resources/fop/avviso-semplice.xsl input.xml`
(`avviso-postale.xsl` per gli avvisi con bollettino postale).

**Bordi dei riquadri.** Jasper disegna la linea centrata sul bordo del riquadro. FOP disegna il bordo
superiore sotto il bordo, quindi il convertitore posiziona la linea sul bordo stesso.

## Verifica

`PrototipoFopTest` genera con FOP i PDF dei casi di riferimento, cioè gli stessi XML passati a Jasper.
I casi restano fuori dal repository e si indicano da riga di comando:

```bash
mvn test -Dtest=PrototipoFopTest \
  -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnica.json
# avvisi con bollettino postale
mvn test -Dtest=PrototipoFopTest -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnicaPostale.json -Dprototipo.foglio=avviso-postale.xsl
```
