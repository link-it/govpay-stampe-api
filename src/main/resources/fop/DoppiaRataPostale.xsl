<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO DoppiaRataPostale (issue #37): pagina dell'avviso con due rate e i bollettini postali (da avviso-postale.xsl).
	Derivato dal template Jasper DoppiaRataPostale.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/doppia; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="DoppiaRataPostale">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="allungamento-doppio-formato-postale" select="(ceiling(count(rata) div 2) * 247.0 - 20) * number((ceiling(count(rata) div 2) * 247.0 - 20) &gt; 0)"/>
		<xsl:variable name="allungamento-bollettino-rata-postale" select="(ceiling(count(rata) div 1) * 223.0 - 20) * number((ceiling(count(rata) div 1) * 223.0 - 20) &gt; 0)"/>
		<xsl:call-template name="intestazione-pagopa"/>
		<fo:block-container absolute-position="absolute" left="490pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="458pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="458"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{1.0 + ((position() - 1) mod 2) * 297.0}pt" top="{110.0 + floor((position() - 1) div 2) * 247.0}pt" width="297pt" height="247pt">
				<fo:block/>
				<xsl:call-template name="DoppioFormatoPostale">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
		<xsl:variable name="spostamento-bollettino-rata-postale" select="$allungamento-doppio-formato-postale"/>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{1.0 + ((position() - 1) mod 1) * 595.0}pt" top="{134.0 + $spostamento-bollettino-rata-postale + floor((position() - 1) div 1) * 223.0}pt" width="595pt" height="223pt">
				<fo:block/>
				<xsl:call-template name="BollettinoRataPostale">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
		<fo:block-container absolute-position="absolute" left="394pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
