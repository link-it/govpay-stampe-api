#!/bin/bash
#
# Rigenera i layout XSL-FO in src/main/resources/fop/ dai sorgenti Jasper in src/main/jasper/,
# applicando le parti scritte a mano in script/fop/personalizzazioni/ (issue #37).
# Va eseguito dalla radice del repository dopo ogni modifica a un sorgente, a una personalizzazione
# o al convertitore.

set -euo pipefail
cd "$(dirname "$0")/../.."

genera() {
	local nome="$1"
	local personalizzazioni="script/fop/personalizzazioni/$nome.xml"
	local opzioni=()
	[ -f "$personalizzazioni" ] && opzioni+=(--personalizzazioni "$personalizzazioni")
	python3 script/fop/jrxml2fo.py "src/main/jasper/$nome.jrxml" "${opzioni[@]}" > "src/main/resources/fop/$nome.xsl"
	echo "generato src/main/resources/fop/$nome.xsl"
}

# layout migrati
genera RataUnica
genera RataUnicaPostale
