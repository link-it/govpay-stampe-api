<?xml version="1.0" encoding="UTF-8"?>
<!--
	Ricevuta telematica (modello v1) in XSL-FO (issue #37). Corrisponde ai template Jasper RicevutaMae.jrxml
	e RicevutaMae_elenco_voci.jrxml. RicevutaMae e' generato in flusso: le voci seguono nel flusso di FOP e,
	quando non stanno nella pagina, passano alla successiva come in Jasper.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:include href="comune.xsl"/>
	<xsl:include href="RicevutaMae.xsl"/>
	<xsl:include href="RicevutaMae_elenco_voci.xsl"/>

	<xsl:template match="/root">
		<fo:root>
			<fo:layout-master-set>
				<!-- A4 con i margini superiore e inferiore del report Jasper (40 pt) nella region-body: le posizioni
					verticali partono dal margine superiore, quelle orizzontali dal bordo della pagina -->
				<fo:simple-page-master master-name="A4" page-width="595pt" page-height="842pt">
					<fo:region-body margin-top="40pt" margin-bottom="40pt"/>
				</fo:simple-page-master>
			</fo:layout-master-set>
			<fo:page-sequence master-reference="A4">
				<fo:flow flow-name="xsl-region-body">
					<xsl:call-template name="RicevutaMae"/>
				</fo:flow>
			</fo:page-sequence>
		</fo:root>
	</xsl:template>

</xsl:stylesheet>
