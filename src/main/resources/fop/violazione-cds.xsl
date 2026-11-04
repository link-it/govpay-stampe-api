<?xml version="1.0" encoding="UTF-8"?>
<!--
	Avviso di violazione del codice della strada senza bollettino postale (modello v1) in XSL-FO (issue #37).
	Corrisponde ai template Jasper ViolazioneCDS.jrxml e Formato.jrxml: una pagina per ogni elemento
	pagine/singola, con il layout Sanzione se la rata ha importo ridotto e scontato. Il mapper produce
	solo questo caso; RidottoScontato (un solo importo) non e' migrato e il servizio lo stampa con Jasper.
	La violazione con bollettino postale usa invece avviso-postale.xsl.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:include href="comune.xsl"/>
	<xsl:include href="Sanzione.xsl"/>

	<xsl:template match="/ViolazioneInput">
		<fo:root>
			<fo:layout-master-set>
				<!-- A4 senza margini: i layout posizionano ogni elemento in punti dal bordo della pagina -->
				<fo:simple-page-master master-name="A4" page-width="595pt" page-height="842pt">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates select="pagine/singola"/>
		</fo:root>
	</xsl:template>

	<xsl:template match="pagine/singola[normalize-space(rata/importo_ridotto) and normalize-space(rata/importo_scontato)]">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="Sanzione"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/singola" priority="-1">
		<xsl:message terminate="yes">Layout RidottoScontato (un solo importo) non migrato a XSL-FO</xsl:message>
	</xsl:template>

</xsl:stylesheet>
