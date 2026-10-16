<?xml version="1.0" encoding="UTF-8"?>
<!--
	Avviso di pagamento semplice (modello v1, senza bollettino postale) in XSL-FO (issue #37).
	Corrisponde al template Jasper AvvisoPagamento.jrxml: una pagina per ogni elemento di pagine,
	con il layout scelto in base al tipo di pagina.

	PROTOTIPO: e' migrato solo il layout della pagina singola (RataUnica). Per gli altri tipi
	la trasformazione si interrompe con un errore esplicito.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:include href="comune.xsl"/>
	<xsl:include href="RataUnica.xsl"/>

	<xsl:template match="/AvvisoPagamentoInput">
		<fo:root>
			<fo:layout-master-set>
				<!-- A4 senza margini: i layout posizionano ogni elemento in punti dal bordo della pagina -->
				<fo:simple-page-master master-name="A4" page-width="595pt" page-height="842pt">
					<fo:region-body/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<xsl:apply-templates select="pagine/*"/>
		</fo:root>
	</xsl:template>

	<xsl:template match="pagine/singola">
		<fo:page-sequence master-reference="A4">
			<fo:flow flow-name="xsl-region-body">
				<xsl:call-template name="RataUnica"/>
			</fo:flow>
		</fo:page-sequence>
	</xsl:template>

	<xsl:template match="pagine/*" priority="-1">
		<xsl:message terminate="yes">Layout '<xsl:value-of select="local-name()"/>' non ancora migrato a XSL-FO</xsl:message>
	</xsl:template>

</xsl:stylesheet>
