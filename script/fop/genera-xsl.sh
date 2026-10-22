#!/bin/bash
#
# Rigenera i layout XSL-FO in src/main/resources/fop/ dai sorgenti Jasper in src/main/jasper/,
# applicando le parti scritte a mano in script/fop/personalizzazioni/ (issue #37).
# Va eseguito dalla radice del repository dopo ogni modifica a un sorgente, a una personalizzazione
# o al convertitore.

set -euo pipefail
cd "$(dirname "$0")/../.."

# genera NOME [opzioni di jrxml2fo.py]
genera() {
	local nome="$1"
	shift
	local personalizzazioni="script/fop/personalizzazioni/$nome.xml"
	local opzioni=("$@")
	[ -f "$personalizzazioni" ] && opzioni+=(--personalizzazioni "$personalizzazioni")
	python3 script/fop/jrxml2fo.py "src/main/jasper/$nome.jrxml" "${opzioni[@]}" > "src/main/resources/fop/$nome.xsl"
	echo "generato src/main/resources/fop/$nome.xsl"
}

# layout migrati
genera RataUnica
genera RataUnicaPostale
genera DoppiaRata
genera TriplaRata
genera RataMultipla --nome RataMultipla
# sottoreport di una rata, a 2 e 3 colonne: Multipla nasconde la seconda banda nelle pagine multiple
genera DoppioFormato --parametri Multipla
genera TriploFormato --parametri Multipla
genera DoppiaRataPostale
genera TriplaRataPostale
genera DoppioFormatoPostale
genera TriploFormatoPostale
genera BollettinoRataPostale
genera BollettinoTriRataPostale
# violazione del codice della strada: i parametri sono sotto la radice ViolazioneInput
genera Sanzione --param-xpath /ViolazioneInput
# ricevuta telematica: i parametri sono sotto la radice root
genera RicevutaMae --param-xpath /root --flusso
genera RicevutaMae_elenco_voci --param-xpath /root --altezza-record
