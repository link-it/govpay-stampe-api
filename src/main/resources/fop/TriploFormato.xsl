<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/TriploFormato.jrxml: non modificare a mano.
	Le parti scritte a mano sono in script/fop/personalizzazioni/TriploFormato.xml.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="TriploFormato">
		<xsl:param name="Multipla" select="'false'"/>
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="senza-numero-rata" select="20 * number(not(normalize-space(numero_rata)))"/>
		<xsl:variable name="senza-cf-ente-e-altri" select="19 * number((not(normalize-space(/AvvisoPagamentoInput/cf_ente))) and (not(normalize-space(/AvvisoPagamentoInput/cbill))))"/>
		<xsl:if test="not(not(normalize-space(numero_rata)))">
			<fo:block-container absolute-position="absolute" left="10pt" top="0pt" width="178pt" height="20pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="10pt" end-indent="0pt" line-height="15.21pt" padding-top="3.65pt"><xsl:if test="normalize-space(numero_rata)">Rata <xsl:value-of select="numero_rata"/></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="102pt" top="{21 - $senza-numero-rata}pt" width="85pt" height="85pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(qr_code_svg)"><fo:external-graphic content-width="85pt" content-height="85pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{89 - $senza-numero-rata}pt" width="194pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Codice Avviso</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{102 - $senza-numero-rata}pt" width="194pt" height="19pt">
			<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="codice_avviso"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="12"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="174"/>
					<xsl:with-param name="altezza" select="19"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{121 - $senza-numero-rata}pt" width="108pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/cf_ente))) and (not(normalize-space(/AvvisoPagamentoInput/cbill))))">
			<fo:block-container absolute-position="absolute" left="0pt" top="{133 - $senza-numero-rata}pt" width="108pt" height="19pt">
				<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="88"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="110pt" top="{121 - $senza-numero-rata}pt" width="86pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/cf_ente))) and (not(normalize-space(/AvvisoPagamentoInput/cbill))))">
			<fo:block-container absolute-position="absolute" left="110pt" top="{133 - $senza-numero-rata}pt" width="86pt" height="19pt">
				<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="86"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{69 - $senza-numero-rata}pt" width="97pt" height="20pt">
			<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" line-height="15.828pt" padding-top="1.392pt"><xsl:choose><xsl:when test="normalize-space(giorni)"><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="giorni"/></xsl:call-template></xsl:when><xsl:otherwise><xsl:value-of select="data"/></xsl:otherwise></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{37 - $senza-numero-rata}pt" width="110pt" height="20pt">
			<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" line-height="15.828pt" padding-top="1.392pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="importo"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{57 - $senza-numero-rata}pt" width="97pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt"><xsl:choose><xsl:when test="normalize-space(giorni)"><xsl:value-of select="tipo"/></xsl:when><xsl:when test="normalize-space(data)">Entro il</xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{25 - $senza-numero-rata}pt" width="97pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Euro</fo:block>
		</fo:block-container>
		<!-- banda detail, y=160.0 -->
		<xsl:variable name="con-di-poste" select="13 * number(not(not(normalize-space(/AvvisoPagamentoInput/di_poste))))"/>
		<xsl:variable name="con-di-poste-2" select="13 * number(not(not(normalize-space(/AvvisoPagamentoInput/di_poste))))"/>
		<xsl:variable name="senza-ente-creditore" select="13 * number(not(normalize-space(/AvvisoPagamentoInput/ente_creditore)))"/>
		<xsl:variable name="senza-oggetto-del-pagamento" select="13 * number(not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento)))"/>
		<xsl:if test="not($Multipla = 'true')">
			<xsl:if test="not(not(normalize-space(/AvvisoPagamentoInput/ente_creditore)))">
				<fo:block-container absolute-position="absolute" left="0pt" top="{196 - $con-di-poste - $con-di-poste-2}pt" width="194pt" height="13pt">
					<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
						<xsl:call-template name="testo-a-capo">
							<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
							<xsl:with-param name="markup" select="'styled'"/>
							<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
							<xsl:with-param name="corpo" select="8"/>
							<xsl:with-param name="interlinea" select="7"/>
							<xsl:with-param name="larghezza" select="174"/>
							<xsl:with-param name="altezza" select="13"/>
							<xsl:with-param name="maxRighe" select="1"/>
							<xsl:with-param name="tronca" select="true()"/>
						</xsl:call-template>
					</fo:block>
				</fo:block-container>
			</xsl:if>
			<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/di_poste))">
				<fo:block-container absolute-position="absolute" left="0pt" top="160pt" width="194pt" height="13pt">
					<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Destinatario</fo:block>
				</fo:block-container>
			</xsl:if>
			<xsl:if test="not(not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento)))">
				<fo:block-container absolute-position="absolute" left="0pt" top="{220 - $con-di-poste - $con-di-poste-2 - $senza-ente-creditore}pt" width="194pt" height="13pt">
					<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
						<xsl:call-template name="testo-a-capo">
							<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
							<xsl:with-param name="markup" select="'styled'"/>
							<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
							<xsl:with-param name="corpo" select="8"/>
							<xsl:with-param name="interlinea" select="7"/>
							<xsl:with-param name="larghezza" select="174"/>
							<xsl:with-param name="altezza" select="13"/>
							<xsl:with-param name="maxRighe" select="1"/>
							<xsl:with-param name="tronca" select="true()"/>
						</xsl:call-template>
					</fo:block>
				</fo:block-container>
			</xsl:if>
			<fo:block-container absolute-position="absolute" left="0pt" top="{235 - $con-di-poste - $con-di-poste-2 - $senza-ente-creditore - $senza-oggetto-del-pagamento}pt" width="205pt" height="15pt">
				<fo:block font-family="TitilliumWeb" font-size="7pt" start-indent="20pt" line-height="10.647pt" padding-top="3.055pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app o usa i dati sopra.</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="0pt" top="{208 - $con-di-poste - $con-di-poste-2 - $senza-ente-creditore}pt" width="194pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
			</fo:block-container>
			<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/di_poste))">
				<fo:block-container absolute-position="absolute" left="0pt" top="{172 - $con-di-poste}pt" width="194pt" height="13pt">
					<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
						<xsl:call-template name="testo-a-capo">
							<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
							<xsl:with-param name="markup" select="'styled'"/>
							<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
							<xsl:with-param name="corpo" select="8"/>
							<xsl:with-param name="interlinea" select="7"/>
							<xsl:with-param name="larghezza" select="174"/>
							<xsl:with-param name="altezza" select="13"/>
							<xsl:with-param name="maxRighe" select="1"/>
							<xsl:with-param name="tronca" select="true()"/>
						</xsl:call-template>
					</fo:block>
				</fo:block-container>
			</xsl:if>
			<fo:block-container absolute-position="absolute" left="0pt" top="{184 - $con-di-poste - $con-di-poste-2}pt" width="194pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Ente Creditore</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
