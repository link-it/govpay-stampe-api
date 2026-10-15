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
  dal bordo del blocco. K dipende dal file del font: 1,668 Regular, 1,709 SemiBold, 1,738 Bold,
  1,547 RobotoMono.

`jrxml2fo.py` imposta `line-height` uguale alla distanza di Jasper e un `padding-top` che riproduce
l'allineamento in alto, al centro o in basso. Il numero di righe lo ricava dagli a capo per i testi
fissi. Per i testi variabili lo legge da `righe=N` nelle personalizzazioni, e in sua assenza usa 1.

`fop.xconf` disattiva il kerning (`kerning="no"`) e l'elaborazione OpenType (`complex-scripts`), che
applicherebbe comunque il kerning delle tabelle GPOS: Jasper non ne usa.

**Immagini.** Gli SVG ricevono tutto il riquadro come area di disegno e si centrano da soli, come in
Jasper. Le immagini raster seguono `hAlign`/`vAlign`. Il blocco che le contiene ha corpo 0, perché
FOP non le allinei alla linea di base del testo.

**A capo dei testi fissi.** FOP spezza le righe con l'algoritmo di Knuth-Plass, che bilancia il paragrafo,
mentre Jasper riempie ogni riga finché c'è spazio. Per i testi fissi `jrxml2fo.py` calcola gli a capo come
Jasper con `SpezzaRighe.java`: `LineBreakMeasurer`, gli stessi file di font e la larghezza utile del
riquadro. Le righe vengono poi scritte esplicitamente con `wrap-option="no-wrap"`. Il convertitore
avvia una sola JVM per template e richiede `java` nel PATH.

Differenze note:
- Testi variabili (oggetto, nomi, indirizzi): gli a capo li sceglie FOP, quindi su testi lunghi una
  parola può andare a capo in un punto diverso da Jasper. Se il numero di righe non è quello indicato
  con `righe=N`, l'interlinea fissa differisce di qualche decimo di punto.
- Lo stemma dell'ente (immagine raster allineata a destra) è spostato di circa 0,7 pt per un diverso
  arrotondamento della scalatura.
- Il grassetto applicato con `<b>` a `TitilliumWeb-Bold`, che non ha una variante bold, è simulato da
  Jasper con un contorno ai caratteri. FOP non lo simula e usa lo stesso file: il testo è meno marcato,
  ma con la stessa larghezza e nella stessa posizione (per esempio "pagopa.gov.it"). `TitilliumWeb-Black`
  è stato provato e scartato perché troppo marcato.

## Verifica

`PrototipoFopTest` genera con FOP i PDF dei casi di riferimento, cioè gli stessi XML passati a Jasper.
I casi restano fuori dal repository e si indicano da riga di comando:

```bash
mvn test -Dtest=PrototipoFopTest \
  -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnica.json
```
