# Migrazione dei layout da JasperReports a XSL-FO

Strumenti per convertire i template Jasper (`src/main/jasper/*.jrxml`) nei layout XSL-FO usati da
Apache FOP (`src/main/resources/fop/*.xsl`), issue [#37](https://github.com/link-it/govpay-stampe-api/issues/37).

## Flusso

1. `jrxml2fo.py` converte un `.jrxml` nello scheletro di un template XSLT. In automatico, compresi gli
   a capo dei testi fissi calcolati come in Jasper:
   - posizioni e dimensioni degli elementi;
   - sfondi, padding, font e allineamenti;
   - testi fissi con markup, grafica SVG e loghi;
   - espressioni semplici (`$P{x}`, `$F{y}`, controllo di null) e condizioni comuni, anche composte
     con `&&`, e le variabili `totaleRate` e `soloSoglie`;
   - sottoreport su sorgente XML (`subDataSource`): un riquadro per record, disposto nelle colonne del
     sottoreport, con il template generato dallo stesso sottoreport; i parametri costanti (per esempio
     `Multipla`) diventano `xsl:param` del template, da indicare con `--parametri`;
   - `elementGroup`, trattati come contenuto della banda;
   - textField con un testo costante (stringhe letterali concatenate), trattati come testi fissi;
   - importi formattati con `DecimalFormat("#,###,##0.00")` (anche con un suffisso, per esempio " Euro"),
     testi costanti concatenati a un campo e condizioni `$F{x}.equals("V")`;
   - espressioni Java composte, come le etichette bilingue: un sottoinsieme di Java (stringhe, `+`,
     ternari, `equals(null)`, `!`, `&&`, `||`, `toUpperCase()`, `DecimalFormat`) diventa un frammento
     XSLT che produce la stessa stringa di Jasper, markup compreso. Come in Java, un valore assente
     concatenato a una stringa diventa "null", che Jasper stampa.

   Le altre espressioni diventano commenti `TODO` con il testo Groovy originale.

   Opzioni per i sottoreport:
   - `--param-campi JRXML`: i parametri sono i campi omonimi del report indicato, passati invariati lungo
     i sottoreport. Per i layout bilingue `$P{codice_avviso_ita}` diventa
     `/AvvisoPagamentoInput/etichette/italiano/codice_avviso`, come in `AvvisoPagamentoV2.jrxml`.
   - `--nome`: nome del template XSLT. I report V2 hanno lo stesso nome interno dei v1 (`RataUnica`),
     quindi il template prende il nome del file.
2. Le parti che il convertitore non traduce si scrivono in `personalizzazioni/<Template>.xml`, legate
   all'`uuid` dell'elemento nel `.jrxml`. Ogni voce è di uno di tre tipi:
   - `<elemento>`: sostituisce il contenuto di un testo o di un'immagine;
   - `<condizione>`: sostituisce la `printWhenExpression`;
   - `attributi="nome=valore;…"`: cambia gli attributi del blocco. Tre nomi non sono attributi FO:
     `righe=N` (righe di un testo variabile, per l'interlinea fissa), `righe-xpath=XPATH` (le stesse,
     calcolate dal contenuto) e `vuoto=XPATH` (quando il testo è vuoto, per le righe rimosse).
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

**A capo dei testi variabili.** I testi variabili (oggetto, nomi, indirizzi) vanno a capo a runtime come
in Jasper: il foglio XSLT chiama `TestoJasper.righe`, una funzione di estensione Java con lo stesso
algoritmo di `SpezzaRighe.java`. Il numero di righe che ne risulta sceglie interlinea e padding.
- **Riquadri che non si allungano:** restano solo le righe che Jasper mostra. Ci stanno finché la prima
  riga, alta (A + D) × corpo, più le successive, alte `lineSpacingSize` + D × corpo con `Fixed`, stanno
  nell'altezza. La regola è ricavata dai PDF di riferimento: titolo 16 pt in 42 pt, 2 righe; oggetto del
  bollettino 9 pt in 24 pt, 1 riga.
- **Troncamento a carattere:** con `net.sf.jasperreports.text.truncate.at.char`, l'ultima riga visibile
  è riempita carattere per carattere.
- **Testi con markup:** passano da `TestoJasper.stile`. La funzione interpreta i tag `style`, `font`, `b`,
  `i`, `span` e `br` come Jasper, spezza le righe con `LineBreakMeasurer` usando i font di ogni tratto
  (come il `ComplexTextLineWrapper` di Jasper) e restituisce le righe come nodi XSL-FO: testo e
  `fo:inline` per i tratti con uno stile diverso.
- **Interlinea e padding:** dipendono dal numero di righe; il foglio ha un caso per ogni numero possibile,
  almeno 5, fino alle righe che stanno nel riquadro (10 per i riquadri che si allungano).

**Righe con stili diversi.** FOP allarga una riga quando un `fo:inline` usa un file di font con `capHeight`
maggiore di quella del blocco: TitilliumWeb-Regular 692, TitilliumWeb-Bold 680. Ascendente e discendente
sono gli stessi, e il `line-height` dell'inline non conta. La traduzione grigia in TitilliumWeb dentro un
titolo TitilliumWeb-Bold abbassava la riga di 0,24 pt e la successiva di 0,48 pt. Per i testi che passano
da `TestoJasper.stile` il blocco è quindi in TitilliumWeb normale, il Titillium con la `capHeight` maggiore,
e i tratti nel font del riquadro sono `fo:inline`: la posizione è la stessa del blocco nel font del riquadro.

`MotoreFop` usa il processore XSLT del JDK (XSLTC), che accetta le funzioni di estensione, senza i limiti
di lunghezza delle espressioni XPath: le posizioni calcolate li superano, e i fogli sono generati da qui. Con xsltproc,
usato solo per ricavare il file FO delle verifiche, la funzione non c'è e il testo resta intero.

**Righe rimosse e posizione Float.** Jasper toglie un elemento con `isRemoveLineWhenBlank` vuoto, o non
stampato per la sua condizione, insieme alla sua fascia verticale, se tutti gli elementi che la occupano
sono vuoti. Gli elementi rimovibili le cui fasce si sovrappongono formano un gruppo, che si toglie quando
sono tutti vuoti. Gli elementi `Float` sotto salgono dell'altezza del più alto del gruppo: nella violazione
CdS senza indirizzo, settore ente (25 pt) e indirizzi (16 + 16 pt) spostano il resto di 25 pt. Una
sovrapposizione di 1 pt non conta: nell'avviso bilingue senza indirizzo del destinatario Jasper toglie la
fascia degli indirizzi (136-167 pt) anche se le informazioni dell'ente, stampate, partono da 166 pt, e le
sposta in su con il resto. Allo stesso modo un sottoreport si allunga fino al suo contenuto e sposta in
basso gli elementi `Float` che lo seguono, ma non si accorcia sotto l'altezza dichiarata: `DoppioFormatoV2`,
alto 211 pt in un riquadro di 212, non sposta in su gli elementi sotto. Il convertitore traduce
entrambi in un'espressione XPath sul `top` degli elementi. Le bande mantengono la loro altezza, come si vede
nei PDF di riferimento dei sottoreport a colonne.

**Testi che si allungano.** Un testo variabile con `isStretchWithOverflow`, che va a capo a runtime, alza il
riquadro fino al testo e sposta in basso gli elementi `Float` sotto di lui, come in Jasper.
- **Altezza:** arrotondata per eccesso, più i padding verticali. La prima riga è alta (A + D) × corpo, le
  successive `lineSpacingSize` + D × corpo con `Fixed` e (A + D) × corpo con `Single`. Misurata sui PDF
  della ricevuta: l'oggetto a 14 pt su 2 righe passa da 22 a 39 pt, una voce a 12 pt su 2 righe da 20 a 31 pt.
- **Testi affiancati:** i testi le cui fasce si sovrappongono formano un gruppo che si allunga quanto il
  più alto. I gruppi uno sotto l'altro si sommano.
- **Nel foglio:** l'allungamento di ogni testo è una variabile XSLT dichiarata in testa alla banda e
  usata nel `top` degli elementi sotto.

**Report in flusso (`--flusso`).** Un report che va su più pagine, come la ricevuta con molte voci, si
genera con le posizioni relative alla region-body, che ha i margini superiore e inferiore del report. I
sottoreport entrano nel flusso di FOP, dopo uno spazio alto quanto la loro posizione e dopo tutti gli
elementi assoluti della banda, che altrimenti si ancorerebbero all'ultima pagina. Ogni record:
- **Altezza:** quella dichiarata; ci va un record solo se c'è posto per questa altezza, altrimenti passa
  alla pagina successiva, che riparte dal margine superiore.
- **Allungamento:** viene dal template `<nome>-altezza` del sottoreport (generato con `--altezza-record`) ed
  è uno spazio dopo il record, scartato a fine pagina. Nei PDF di riferimento una voce allungata che finisce
  il testo sul margine inferiore chiude la pagina senza spostare la successiva.
- **Spazio sotto il sottoreport:** quello della banda resta un blocco. Con le voci che riempiono la pagina
  fino al margine, Jasper aggiunge una pagina vuota, e FOP fa lo stesso.

Differenze note:
- Record in flusso il cui testo allungato non sta nello spazio rimasto in fondo alla pagina: Jasper divide
  il testo tra le due pagine, FOP lo lascia sporgere nel margine inferiore. Non succede con i casi di
  riferimento.
- Il grassetto applicato con `<b>` a `TitilliumWeb-Bold`, che non ha una variante bold, è simulato da
  Jasper con un contorno ai caratteri. FOP non lo simula e usa lo stesso file: il testo è meno marcato,
  ma con la stessa larghezza e nella stessa posizione (per esempio "pagopa.gov.it"). `TitilliumWeb-Black`
  è stato provato e scartato perché troppo marcato.
- Avviso bilingue con soglie e bollettino postale, senza rata unica: ci sono solo pagine doppie
  (`MonoBandV2`) e Jasper sovrappone la seconda pagina alla prima. FOP stampa le due pagine
  (`bilingue-postale-soglie-3-SL`, non accettato perché il riferimento è sbagliato).
- Testo grigio grande, come la traduzione nel titolo a 16 pt: Jasper e FOP incorporano il font in modo
  diverso e i bordi dei glifi risultano lievemente diversi nel raster, con le posizioni identiche entro
  0,005 pt. Su "(de)" sono 6 pixel, oltre la soglia di 5 (`bilingue-unica-de`, `bilingue-postale-unica-DE`).

## Criteri di accettazione

Un layout migrato è accettato quando `verifica_pdf.py` passa su tutti i suoi casi di riferimento:

| Criterio | Soglia |
| --- | --- |
| Testo | stesse righe con le stesse parole, quindi stessi a capo |
| Inchiostro per riga | bordi superiore, inferiore, sinistro e destro entro 0,25 pt (misura a 600 dpi) |
| Raster | a 200 dpi con tolleranza di 1 pixel (circa 0,36 pt), al massimo 5 pixel fuori tolleranza, escluse le immagini e le differenze accettate indicate con `--escludi`; sono scuri i pixel sotto 204, a metà tra il grigio delle traduzioni (#999999) e il bianco |
| Immagini | riquadro dell'inchiostro entro 1 pt |
| Pagine | stesso numero di pagine; gli altri criteri si applicano pagina per pagina |
| QR code | tutti i `qr_code` dell'XML letti dal PDF FOP, dal riquadro esatto di ogni codice a 600 dpi; se zxing non lo legge, riprova con un margine bianco, a scale minori e con `PURE_BARCODE` |
| DataMatrix (bollettino postale) | tutti i `data_matrix` dell'XML letti dal PDF FOP |

Differenze accettate in `RataUnica` e `RataUnicaPostale`: il grassetto simulato di "pagopa.gov.it" (`--escludi 480,250,100,22`).

Il DataMatrix del bollettino è generato da zxing, mentre Jasper usa barcode4j. Ha la stessa dimensione
(48×48) e lo stesso contenuto, ma il disegno dei moduli è diverso perché le due librerie scelgono una
codifica interna diversa. Per questo si confronta il contenuto letto, non il disegno.

```bash
python3 script/fop/verifica_pdf.py jasper.pdf fop.pdf --fo layout.fo --xml input.xml \
  --classpath "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)" --escludi 480,250,100,22
```

Il file FO (`layout.fo`) si ottiene dall'XML con `xsltproc src/main/resources/fop/avviso-semplice.xsl input.xml`
(`avviso-postale.xsl` per gli avvisi con bollettino postale, `ricevuta.xsl` per la ricevuta).

**Bordi dei riquadri.** Jasper disegna la linea centrata sul bordo del riquadro. FOP disegna il bordo
superiore sotto il bordo, quindi il convertitore posiziona la linea sul bordo stesso.

## Motore nel servizio

`MotoreFop` è il componente Spring che stampa con FOP:
- **Bean, non XML:** riceve l'oggetto JAXB dell'avviso e lo passa all'XSLT con `JAXBSource`, senza serializzarlo.
- **Dati calcolati:** prima della stampa `PreparazioneAvviso` valorizza i campi opzionali dell'XSD che XSLT non può calcolare:
  - `qr_code_svg` e `data_matrix_svg` di ogni rata, nei modelli v1 e v2;
  - `logo_ente_px` e `logo_ente_secondario_px`, `logo_pagopa_px` per la ricevuta.
- **Cache:** `FopFactory` (configurazione e font) è unica; i fogli XSLT compilati sono in cache, e quelli dei tipi configurati con `fop` si compilano all'avvio.
- **Classpath:** fogli, configurazione e font vengono letti solo dal classpath (i font come `classpath:/fonts/...`), quindi funzionano da `target/classes`, dal jar eseguibile e dal war. Ogni altra risorsa esterna viene rifiutata.

Il motore si sceglie per tipo di avviso con `stampe.motore.avviso-semplice`, `stampe.motore.avviso-postale`,
`stampe.motore.violazione-cds`, `stampe.motore.ricevuta` e `stampe.motore.avviso-bilingue` (`jasper` di
default). `LayoutMigrati` decide quali avvisi stampare con FOP: postale con pagine multiple e violazione
`RidottoScontato`, che il mapper non produce, restano su Jasper.

Test:
- `UC_12_MotoreFopTest`: motore usato, riconosciuto dal Producer del PDF;
- `MotoreFopCodiciTest`: QR code e DataMatrix letti dalla pagina resa da FOP come immagine.

## Casi di riferimento

I PDF Jasper dei layout a pagina singola vengono dalla testsuite del core (`pdf-riferimento/run-*`). Quelli
multi-rata, la ricevuta e l'avviso bilingue, che il core non produce, si generano con
`GeneraRiferimentiJasperTest`. Il test costruisce le varianti (rate, soglie, pagine multiple, postale,
violazione CdS, ricevuta, bilingue in tedesco e sloveno) come il controller e salva XML e PDF Jasper:

```bash
mvn test -Dtest=GeneraRiferimentiJasperTest -Driferimenti.uscita=$PWD/pdf-riferimento/jasper-multirata
```

## Verifica

`PrototipoFopTest` genera con FOP i PDF dei casi di riferimento, cioè gli stessi XML passati a Jasper, e
accanto il documento XSL-FO prodotto con le funzioni Java.
I casi restano fuori dal repository e si indicano da riga di comando:

```bash
mvn test -Dtest=PrototipoFopTest \
  -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnica.json
# avvisi con bollettino postale
mvn test -Dtest=PrototipoFopTest -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnicaPostale.json -Dprototipo.foglio=avviso-postale.xsl
```
