<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO TriplaRataPostale (issue #37): pagina dell'avviso con tre rate e i bollettini postali (da avviso-postale.xsl).
	Derivato dal template Jasper TriplaRataPostale.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/tripla; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoFop" exclude-result-prefixes="tj">

	<xsl:template name="TriplaRataPostale">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="allungamento-bollettino-tri-rata-postale" select="(ceiling(count(rata) div 1) * 160.0 - 10) * number((ceiling(count(rata) div 1) * 160.0 - 10) &gt; 0)"/>
		<xsl:variable name="allungamento-triplo-formato-postale" select="(ceiling(count(rata) div 3) * 242.0 - 10) * number((ceiling(count(rata) div 3) * 242.0 - 10) &gt; 0)"/>
		<xsl:call-template name="intestazione-pagopa"/>
		<fo:block-container absolute-position="absolute" left="505pt" top="20pt" width="70pt" height="70pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_px"/>
				<xsl:with-param name="larghezza" select="70"/>
				<xsl:with-param name="altezza" select="70"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="50pt" width="458pt" height="37pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="14"/>
						<xsl:with-param name="interlinea" select="10"/>
						<xsl:with-param name="larghezza" select="458"/>
						<xsl:with-param name="altezza" select="37"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:variable name="spostamento-bollettino-tri-rata-postale" select="$allungamento-triplo-formato-postale"/>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 1) * 595.0}pt" top="{104.0 + $spostamento-bollettino-tri-rata-postale + floor((position() - 1) div 1) * 160.0}pt" width="595pt" height="160pt">
				<fo:block/>
				<xsl:call-template name="BollettinoTriRataPostale">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
		<fo:block-container absolute-position="absolute" left="409pt" top="20pt" width="70pt" height="70pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="70"/>
				<xsl:with-param name="altezza" select="70"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="50pt" width="350pt" height="37pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="14"/>
						<xsl:with-param name="interlinea" select="10"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="37"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 3) * 198.0}pt" top="{94.0 + floor((position() - 1) div 3) * 242.0}pt" width="198pt" height="242pt">
				<fo:block/>
				<xsl:call-template name="TriploFormatoPostale">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
	</xsl:template>

</xsl:stylesheet>
