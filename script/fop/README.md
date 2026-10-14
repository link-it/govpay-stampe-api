# Migrazione dei layout da JasperReports a XSL-FO

Strumenti per convertire i template Jasper (`src/main/jasper/*.jrxml`) nei layout XSL-FO usati da
Apache FOP (`src/main/resources/fop/*.xsl`), issue [#37](https://github.com/link-it/govpay-stampe-api/issues/37).

## Flusso

1. `jrxml2fo.py` converte un `.jrxml` nello scheletro di un template XSLT. In automatico:
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

Il convertitore riproduce l'impaginazione di Jasper con parametri misurati sui PDF di riferimento:
- **Interlinea:** 1,266 volte il corpo per Titillium. Jasper non scende sotto questo valore anche
  quando il template indica un `lineSpacing="Fixed"` più piccolo.
- **Prima riga:** spostata in basso di 0,217 volte il corpo. Jasper allinea la prima linea di base
  all'ascendente del font, FOP la alza di metà della differenza tra interlinea e altezza del font.

Differenze note:
- FOP spezza le righe dei paragrafi con l'algoritmo di Knuth-Plass (righe bilanciate), mentre Jasper
  riempie ogni riga finché c'è spazio. Su testi lunghi una parola può andare a capo in un punto
  diverso.
- Il grassetto applicato con `<b>` a una famiglia che ha solo la variante normale (`TitilliumWeb-Bold`)
  è simulato da Jasper; FOP non lo simula.

## Verifica

`PrototipoFopTest` genera con FOP i PDF dei casi di riferimento, cioè gli stessi XML passati a Jasper.
I casi restano fuori dal repository e si indicano da riga di comando:

```bash
mvn test -Dtest=PrototipoFopTest \
  -Dprototipo.run=$PWD/pdf-riferimento/run-20261012 \
  -Dprototipo.casi=casi-prototipo-RataUnica.json
```
