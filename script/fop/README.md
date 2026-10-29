# Layout XSL-FO degli avvisi

Le stampe (avvisi, violazione CdS, ricevuta) sono layout XSL-FO in `src/main/resources/fop/`, resi in PDF
da Apache FOP. Sostituiscono i template JasperReports, issue [#37](https://github.com/link-it/govpay-stampe-api/issues/37),
e riproducono la loro impaginazione al punto.

## Struttura

| File | Contenuto |
| --- | --- |
| `avviso-semplice.xsl`, `avviso-postale.xsl`, `violazione-cds.xsl`, `ricevuta.xsl`, `avviso-bilingue.xsl` | fogli principali, uno per tipo di stampa: pagina A4 e scelta del layout per ogni pagina dell'XML |
| `RataUnica.xsl`, `DoppiaRata.xsl`, `TriplaRata.xsl`, `RataMultipla.xsl`, `*Postale.xsl`, `*V2.xsl`, `Sanzione.xsl`, `RicevutaMae*.xsl` | layout di una pagina, o del riquadro di una rata (`*Formato*`, `Bollettino*`); l'intestazione dice da dove sono chiamati e qual è il nodo di contesto |
| `comune.xsl` | template condivisi: `intestazione-pagopa`, `testo-a-capo`, `logo`, `importo`, `data-uri`, `markup`, `parte-etichetta`, `giorni` |
| `icone/*.svg` | icone (logo pagoPA, Poste, canali di pagamento, forbici, ...), lette da `classpath:/fop/icone/<nome>.svg` |
| `fop.xconf` | configurazione di FOP: font e opzioni di impaginazione |

`TestoJasper` (`it.govpay.stampe.fop`) contiene le funzioni di estensione Java usate dai fogli con il prefisso
`tj:`: a capo, interlinea, padding e allungamento dei testi, calcolati come in Jasper.

I layout sono stati derivati dai template Jasper (`.jrxml`) con un convertitore, rimosso dopo l'ultima
generazione. Sorgenti Jasper, convertitore (`script/fop/jrxml2fo.py`) e parti scritte a mano
(`script/fop/personalizzazioni/`) sono nella storia git: l'ultima generazione è il commit `95fc24e`.

## Come modificare un layout

Ogni elemento è un `fo:block-container` in posizione assoluta, con `left`, `top`, `width` e `height` in
punti, come nel template Jasper: dal bordo della pagina, oppure dal riquadro della rata per i layout delle
rate.

- **Testo fisso:** un `fo:block` con il testo. Gli a capo sono scritti esplicitamente, come li fa Jasper, con
  `wrap-option="no-wrap"`: FOP spezzerebbe le righe in modo diverso. Per un testo nuovo che deve andare a
  capo si può usare il template `testo-a-capo` con un valore costante (`select="'...'"`), che calcola gli a
  capo a runtime.
- **Testo variabile:** il blocco chiama `testo-a-capo` con il valore, il font e le dimensioni del riquadro;
  i parametri sono descritti in `comune.xsl`. Il template calcola righe, interlinea e padding come Jasper.
- **Logo:** template `logo`, con il logo, le sue dimensioni in pixel (`<logo>_px`), il riquadro e
  l'allineamento.
- **Icona:** `fo:external-graphic src="url(classpath:/fop/icone/<nome>.svg)"`. Un'icona nuova si aggiunge
  in `icone/`.
- **Righe rimosse e testi che si allungano:** le variabili in testa alla banda (`$senza-numero-rata`,
  `$allungamento-oggetto-del-pagamento`, ...) valgono i punti di cui si spostano gli elementi sotto;
  il `top` di un elemento è `{y - $senza-... + $allungamento-...}pt`. Un elemento nuovo sotto una riga
  rimovibile, o sotto un testo che si allunga, deve sommare le stesse variabili degli elementi vicini.

I fogli si possono applicare solo con il processore XSLT del JDK (XSLTC), quello di `MotoreFop`: le
funzioni `tj:` sono estensioni Java, che xsltproc e gli altri processori non hanno.

Dopo una modifica:
1. `mvn test -Dtest=RegressioneLayoutTest`: i casi il cui PDF è cambiato falliscono e lasciano area tree e
   PDF in `target/regressione-layout/` (vedi [Regressione dei layout](#regressione-dei-layout));
2. si controllano i PDF dei casi cambiati, e solo quelli: un caso cambiato che non c'entra con la modifica
   è un errore;
3. se la modifica è voluta, si aggiornano gli hash con `-Dregressione.aggiorna=true` e si committano
   insieme al layout.

## Regressione dei layout

`RegressioneLayoutTest` protegge i layout da modifiche non volute. Per ogni caso in
`src/test/resources/regressione-layout/<foglio>/` stampa l'XML con `<foglio>.xsl` e confronta l'hash
SHA-256 dell'area tree di FOP (la pagina impaginata: posizioni, testi, immagini) con quello in
`attesi.properties`. Se l'hash cambia, area tree e PDF del caso vanno in `target/regressione-layout/`.

- Gli XML vengono da `GeneraCasiRegressioneTest` (factory e mapper, come il controller), con i loghi
  sostituiti da `@logo`; si rigenerano solo su richiesta, perché la factory usa codici avviso casuali:
  `mvn test -Dtest=GeneraCasiRegressioneTest -Dregressione.genera=true`.
- Dopo una modifica voluta del layout, si aggiornano gli hash:
  `mvn test -Dtest=RegressioneLayoutTest -Dregressione.aggiorna=true`.

Per i casi di riferimento fuori dal repository (sotto), `PrototipoFopTest` scrive PDF, documento XSL-FO e,
con `-Dprototipo.areatree=true`, l'area tree di ogni caso in `target/prototipo-fop/`.

## Geometria del testo

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

I blocchi di testo hanno `line-height` uguale alla distanza di Jasper e un `padding-top` che riproduce
l'allineamento in alto, al centro o in basso: costanti per i testi fissi, calcolati da
`TestoJasper.interlinea` e `TestoJasper.paddingTesto` per i testi variabili.

`fop.xconf` disattiva il kerning (`kerning="no"`) e l'elaborazione OpenType (`complex-scripts`), che
applicherebbe comunque il kerning delle tabelle GPOS: Jasper non ne usa.

**Immagini.**
- **SVG:** ricevono tutto il riquadro come area di disegno e si centrano da soli, come in Jasper.
- **Raster (loghi):** Jasper le scala per stare nel riquadro mantenendo le proporzioni, poi le sposta
  secondo `hAlign`/`vAlign` di uno spazio libero troncato a punti interi. Il renderer aggiunge all'XML le
  dimensioni in pixel di ogni logo (`<logo>_px`) e il template `logo` ripete lo stesso calcolo.
- **Blocco contenitore:** ha corpo 0, perché FOP non allinei l'immagine alla linea di base del testo.

**Righe con font diversi.** Se su una riga ci sono font con ascendenti diverse, per esempio Titillium e
RobotoMono, FOP abbassa la linea di base rispetto a Jasper. Va compensato nel `padding-top` del blocco,
come per "Euro 122,50" in `RataUnica` (0,48 pt).

**A capo dei testi.** FOP spezza le righe con l'algoritmo di Knuth-Plass, che bilancia il paragrafo,
mentre Jasper riempie ogni riga finché c'è spazio. Per questo i testi fissi hanno gli a capo scritti, e i
testi variabili (oggetto, nomi, indirizzi) passano dal template `testo-a-capo`, che chiama
`TestoJasper.righe`: `LineBreakMeasurer` con il `FontRenderContext` di Jasper, gli stessi file di font e la
larghezza utile del riquadro.
- **Riquadri che non si allungano:** restano solo le righe che Jasper mostra. Ci stanno finché la prima
  riga, alta (A + D) × corpo, più le successive, alte `lineSpacingSize` + D × corpo con `Fixed`, stanno
  nell'altezza (parametro `maxRighe`). La regola è ricavata dai PDF di riferimento: titolo 16 pt in 42 pt,
  2 righe; oggetto del bollettino 9 pt in 24 pt, 1 riga.
- **Troncamento a carattere:** con `tronca`, l'ultima riga visibile è riempita carattere per carattere.
- **Testi con markup:** passano da `TestoJasper.stile`. La funzione interpreta i tag `style`, `font`, `b`,
  `i`, `span` e `br` come Jasper, spezza le righe con `LineBreakMeasurer` usando i font di ogni tratto
  (come il `ComplexTextLineWrapper` di Jasper) e restituisce le righe come nodi XSL-FO: testo e
  `fo:inline` per i tratti con uno stile diverso.
- **Interlinea e padding:** dipendono dal numero di righe. Oltre un limite di righe, almeno 5, le righe
  che stanno nel riquadro (10 per i riquadri che si allungano), la geometria resta quella del limite.

**Righe con stili diversi.** FOP allarga una riga quando un `fo:inline` usa un file di font con `capHeight`
maggiore di quella del blocco: TitilliumWeb-Regular 692, TitilliumWeb-Bold 680. Ascendente e discendente
sono gli stessi, e il `line-height` dell'inline non conta. La traduzione grigia in TitilliumWeb dentro un
titolo TitilliumWeb-Bold abbassava la riga di 0,24 pt e la successiva di 0,48 pt. Per i testi che passano
da `TestoJasper.stile` il blocco è quindi in TitilliumWeb normale, il Titillium con la `capHeight` maggiore,
e i tratti nel font del riquadro sono `fo:inline`: la posizione è la stessa del blocco nel font del riquadro.

**Righe rimosse.** Jasper toglie un elemento con `isRemoveLineWhenBlank` vuoto, o non stampato per la sua
condizione, insieme alla sua fascia verticale, se tutti gli elementi che la occupano sono vuoti. Gli
elementi rimovibili le cui fasce si sovrappongono formano un gruppo, che si toglie quando sono tutti vuoti.
Gli elementi `Float` sotto salgono dell'altezza del più alto del gruppo: nella violazione CdS senza
indirizzo, settore ente (25 pt) e indirizzi (16 + 16 pt) spostano il resto di 25 pt. Una sovrapposizione
di 1 pt non conta: nell'avviso bilingue senza indirizzo del destinatario Jasper toglie la fascia degli
indirizzi (136-167 pt) anche se le informazioni dell'ente, stampate, partono da 166 pt, e le sposta in su
con il resto. Nel foglio ogni gruppo è una variabile in testa alla banda, che vale l'altezza della riga se
il gruppo si toglie e 0 altrimenti, con un nome ricavato dai campi della condizione (`$senza-numero-rata`).
Le bande mantengono la loro altezza, come si vede nei PDF di riferimento dei sottoreport a colonne.

**Allungamenti.** Un riquadro di rate (sottoreport in Jasper) si allunga fino al suo contenuto e sposta in
basso gli elementi `Float` che lo seguono, ma non si accorcia sotto l'altezza dichiarata: `DoppioFormatoV2`,
alto 211 pt in un riquadro di 212, non sposta in su gli elementi sotto. Un testo variabile con
`isStretchWithOverflow` alza il riquadro fino al testo, come in Jasper.
- **Altezza:** arrotondata per eccesso, più i padding verticali. La prima riga è alta (A + D) × corpo, le
  successive `lineSpacingSize` + D × corpo con `Fixed` e (A + D) × corpo con `Single`. Misurata sui PDF
  della ricevuta: l'oggetto a 14 pt su 2 righe passa da 22 a 39 pt, una voce a 12 pt su 2 righe da 20 a 31 pt.
- **Testi affiancati:** i testi le cui fasce si sovrappongono formano un gruppo che si allunga quanto il
  più alto (`$allungamento-<campo>-e-affiancati`). I gruppi uno sotto l'altro si sommano.
- **Nel foglio:** l'allungamento di ogni testo è una variabile in testa alla banda, calcolata da
  `TestoJasper.allungamento`, e usata nel `top` degli elementi sotto.

**Report in flusso.** La ricevuta, che con molte voci va su più pagine, ha le posizioni relative alla
region-body, che ha i margini superiore e inferiore del report. Le voci entrano nel flusso di FOP, dopo
uno spazio alto quanto la loro posizione e dopo tutti gli elementi assoluti della banda, che altrimenti si
ancorerebbero all'ultima pagina. Ogni voce:
- **Altezza:** quella dichiarata; ci va una voce solo se c'è posto per questa altezza, altrimenti passa
  alla pagina successiva, che riparte dal margine superiore.
- **Allungamento:** viene dal template `RicevutaMae_elenco_voci-altezza` ed è uno spazio dopo la voce,
  scartato a fine pagina. Nei PDF di riferimento una voce allungata che finisce il testo sul margine
  inferiore chiude la pagina senza spostare la successiva.
- **Spazio sotto l'elenco:** quello della banda resta un blocco. Con le voci che riempiono la pagina fino
  al margine, Jasper aggiunge una pagina vuota, e FOP fa lo stesso.

**Bordi dei riquadri.** Jasper disegna la linea centrata sul bordo del riquadro. FOP disegna il bordo
superiore sotto il bordo, quindi la linea è posizionata sul bordo stesso.

`MotoreFop` alza i limiti di lunghezza delle espressioni XPath del JDK: i fogli sono interni, non arrivano
dall'esterno.

Differenze note rispetto a Jasper:
- Voce della ricevuta il cui testo allungato non sta nello spazio rimasto in fondo alla pagina: Jasper
  divide il testo tra le due pagine, FOP lo lascia sporgere nel margine inferiore. Non succede con i casi
  di riferimento.
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
- Il DataMatrix del bollettino è generato da zxing, mentre Jasper usa barcode4j. Ha la stessa dimensione
  (48×48) e lo stesso contenuto, ma il disegno dei moduli è diverso perché le due librerie scelgono una
  codifica interna diversa.

## Confronto con Jasper

`verifica_pdf.py` confronta il PDF FOP con quello Jasper dello stesso XML. Un caso è accettato quando:

| Criterio | Soglia |
| --- | --- |
| Testo | stesse righe con le stesse parole, quindi stessi a capo |
| Inchiostro per riga | bordi superiore, inferiore, sinistro e destro entro 0,25 pt (misura a 600 dpi) |
| Raster | a 200 dpi con tolleranza di 1 pixel (circa 0,36 pt), al massimo 5 pixel fuori tolleranza, escluse le immagini e le differenze accettate indicate con `--escludi`; sono scuri i pixel sotto 204, a metà tra il grigio delle traduzioni (#999999) e il bianco |
| Immagini | riquadro dell'inchiostro entro 1 pt |
| Pagine | stesso numero di pagine; gli altri criteri si applicano pagina per pagina |
| QR code | tutti i `qr_code` dell'XML letti dal PDF FOP, dal riquadro esatto di ogni codice a 600 dpi; se zxing non lo legge, riprova con un margine bianco, a scale minori e con `PURE_BARCODE` |
| DataMatrix (bollettino postale) | tutti i `data_matrix` dell'XML letti dal PDF FOP: si confronta il contenuto, non il disegno |

Differenze accettate in `RataUnica` e `RataUnicaPostale`: il grassetto simulato di "pagopa.gov.it" (`--escludi 480,250,100,22`).

```bash
python3 script/fop/verifica_pdf.py jasper.pdf fop.pdf --fo layout.fo --xml input.xml \
  --classpath "$(mvn -q dependency:build-classpath -Dmdep.outputFile=/dev/stdout)" --escludi 480,250,100,22
```

Il file FO (`layout.fo`) lo scrive `PrototipoFopTest` in `target/prototipo-fop/`, accanto al PDF di ogni
caso, con il foglio indicato da `-Dprototipo.foglio`.

### Casi di riferimento

I PDF Jasper dei layout a pagina singola vengono dalla testsuite del core (`pdf-riferimento/run-*`). Quelli
multi-rata, la ricevuta e l'avviso bilingue, che il core non produce, sono stati generati con
`GeneraRiferimentiJasperTest`. Il test costruiva le varianti (rate, soglie, pagine multiple, postale,
violazione CdS, ricevuta, bilingue in tedesco e sloveno) come il controller e salvava XML e PDF Jasper.
È stato rimosso con JasperReports: per rigenerare i riferimenti si usa il commit `c88aed7`, l'ultimo con Jasper:

```bash
mvn test -Dtest=GeneraRiferimentiJasperTest -Driferimenti.uscita=$PWD/pdf-riferimento/jasper-multirata
```

`PrototipoFopTest` genera con FOP i PDF dei casi di riferimento, cioè gli stessi XML passati a Jasper. I
casi restano fuori dal repository e si indicano da riga di comando:

```bash
mvn test -Dtest=PrototipoFopTest \
  -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnica.json
# avvisi con bollettino postale
mvn test -Dtest=PrototipoFopTest -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnicaPostale.json -Dprototipo.foglio=avviso-postale.xsl
```

## Motore nel servizio

`MotoreFop` è il componente Spring che stampa con FOP:
- **Bean, non XML:** riceve l'oggetto JAXB dell'avviso e lo passa all'XSLT con `JAXBSource`, senza serializzarlo.
- **Dati calcolati:** prima della stampa `PreparazioneAvviso` valorizza i campi opzionali dell'XSD che XSLT non può calcolare:
  - `qr_code_svg` e `data_matrix_svg` di ogni rata, nei modelli v1 e v2;
  - `logo_ente_px` e `logo_ente_secondario_px`, `logo_pagopa_px` per la ricevuta.
- **Cache:** `FopFactory` (configurazione, font e immagini) è unica; i fogli XSLT compilati sono in cache e si compilano all'avvio.
- **Classpath:** fogli, configurazione, font e icone vengono letti solo dal classpath (font e icone come `classpath:/...`), quindi funzionano da `target/classes`, dal jar eseguibile e dal war. Ogni altra risorsa esterna viene rifiutata.

I layout `RidottoScontato`, e postale con pagine multiple, non sono stati migrati da Jasper: il mapper
non li produce.

Test:
- `UC_12_MotoreFopTest`: motore usato, riconosciuto dal Producer del PDF;
- `MotoreFopCodiciTest`: QR code e DataMatrix letti dalla pagina resa da FOP come immagine;
- `TestoJasperGeometriaTest`: interlinea, padding e allungamento dei testi;
- `RegressioneLayoutTest`: regressione dei layout.
