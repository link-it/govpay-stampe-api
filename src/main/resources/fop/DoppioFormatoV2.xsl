<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO DoppioFormatoV2 (issue #37): riquadro di una rata su due colonne, avviso bilingue (da DoppiaRataV2.xsl).
	Derivato dal template Jasper DoppioFormatoV2.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento rata; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="DoppioFormatoV2">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="senza-importo" select="20 * number(not(normalize-space(format-number(number(importo), '#.##0,00', 'it'))))"/>
		<xsl:variable name="senza-codice-avviso" select="16 * number(not(normalize-space(codice_avviso)))"/>
		<fo:block-container absolute-position="absolute" left="11pt" top="15pt" width="94pt" height="94pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(qr_code_svg)"><fo:external-graphic content-width="94pt" content-height="94pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(numero_rata)">
			<fo:block-container absolute-position="absolute" left="0pt" top="3pt" width="160pt" height="18pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="numero_rata"><xsl:value-of select="numero_rata"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(numero_rata_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="numero_rata_tra"><xsl:value-of select="numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="140"/>
						<xsl:with-param name="altezza" select="18"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not(not(normalize-space(format-number(number(importo), '#.##0,00', 'it'))))">
			<fo:block-container absolute-position="absolute" left="0pt" top="125pt" width="148pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="format-number(number(importo), '#.##0,00', 'it')"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="128"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{179 - $senza-importo}pt" width="160pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_avviso))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_avviso"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_avviso"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="140"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(codice_avviso)))">
			<fo:block-container absolute-position="absolute" left="0pt" top="{195 - $senza-importo}pt" width="160pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="codice_avviso"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="140"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{147 - $senza-importo}pt" width="160pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(data))"><xsl:value-of select="scadenza"/></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/entro"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/entro"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/entro))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/entro"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/entro"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="140"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{163 - $senza-importo}pt" width="160pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(data))"><xsl:choose><xsl:when test="not(normalize-space(scadenza_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="scadenza_tra"><xsl:value-of select="scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:value-of select="data"/></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="140"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="106pt" width="160pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="3.254pt" wrap-option="no-wrap">Euro</fo:block>
		</fo:block-container>
	</xsl:template>

</xsl:stylesheet>
