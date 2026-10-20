<?xml version="1.0" encoding="UTF-8"?>
<!--
	Avviso di pagamento semplice (modello v1, senza bollettino postale) in XSL-FO (issue #37).
	Corrisponde al template Jasper AvvisoPagamento.jrxml: una pagina per ogni elemento di pagine,
	con il layout scelto in base al tipo di pagina.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:include href="comune.xsl"/>
	<xsl:include href="RataUnica.xsl"/>
	<xsl:include href="DoppiaRata.xsl"/>
	<xsl:include href="TriplaRata.xsl"/>
	<xsl:include href="RataMultipla.xsl"/>
	<xsl:include href="DoppioFormato.xsl"/>
	<xsl:include href="TriploFormato.xsl"/>

	<xsl:template match="/AvvisoPagamentoInput">
		<fo:root>
			<fo:layout-master-set>
				<!-- A4 senza margini: i layout posizionano ogni elemento in punti dal bordo della pagina -->
				<fo:simple-page-master master-name="A4" page-width="595pt" page-height="842pt">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<!-- come le bande di AvvisoPagamento.jrxml: prima le pagine singole, poi doppie, triple e multiple -->
			<xsl:apply-templates select="pagine/singola"/>
			<xsl:apply-templates select="pagine/doppia"/>
			<xsl:apply-templates select="pagine/tripla"/>
			<xsl:apply-templates select="pagine/multipla"/>
		</fo:root>
	</xsl:template>

	<xsl:template match="pagine/singola">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="RataUnica"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/doppia">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="DoppiaRata"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/tripla">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="TriplaRata"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/multipla">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="RataMultipla"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

</xsl:stylesheet>
