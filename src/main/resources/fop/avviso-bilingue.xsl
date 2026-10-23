<?xml version="1.0" encoding="UTF-8"?>
<!--
	Avviso di pagamento bilingue (modello v2, con e senza bollettino postale) in XSL-FO (issue #37).
	Corrisponde ai template Jasper AvvisoPagamentoV2.jrxml, MonoBandV2.jrxml e DualBandV2.jrxml: una pagina
	per ogni elemento di pagine, prima le singole e poi le doppie. Il bollettino postale e' nei layout stessi,
	stampato quando poste e' valorizzato.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:include href="comune.xsl"/>
	<xsl:include href="RataUnicaV2.xsl"/>
	<xsl:include href="DoppiaRataV2.xsl"/>
	<xsl:include href="DoppioFormatoV2.xsl"/>
	<xsl:include href="BollettinoRataV2.xsl"/>

	<xsl:template match="/AvvisoPagamentoInput">
		<fo:root>
			<fo:layout-master-set>
				<!-- A4 senza margini: i layout posizionano ogni elemento in punti dal bordo della pagina -->
				<fo:simple-page-master master-name="A4" page-width="595pt" page-height="842pt">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates select="pagine/singola"/>
			<xsl:apply-templates select="pagine/doppia"/>
		</fo:root>
	</xsl:template>

	<xsl:template match="pagine/singola">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="RataUnicaV2"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/doppia">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="DoppiaRataV2"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

</xsl:stylesheet>
