<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/BollettinoTriRataPostale.jrxml: non modificare a mano.
	Le parti scritte a mano sono in script/fop/personalizzazioni/BollettinoTriRataPostale.xml.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="BollettinoTriRataPostale">
		<!-- banda detail, y=0.0 -->
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="543pt" top="0pt" width="30pt" height="30pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="30pt" height="23.182pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="30pt" content-height="23.182pt" scaling="uniform" src="url(classpath:/fop/icone/forbici.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/di_poste))">
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="13pt" width="595pt" height="20pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="3.65pt" wrap-option="no-wrap">BOLLETTINO POSTALE PA</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="0pt" top="13pt" width="595pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="255pt" top="85pt" width="224pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="224"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="42pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-poste-italiane.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="199pt" top="41pt" width="216pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="justify" line-height="18.252pt" padding-top="1.4pt">sul C/C n. <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:value-of select="numero_cc_postale"/></fo:inline></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="130pt" width="149pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Codice Avviso</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="488pt" top="72pt" width="85pt" height="85pt">
				<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(data_matrix_svg)"><fo:external-graphic content-width="85pt" content-height="85pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="data_matrix_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="103pt" width="85pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="215pt" top="67pt" width="264pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/intestatario_conto_corrente_postale"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="264"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="361pt" top="130pt" width="118pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" text-align="start" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Codice Fiscale Ente</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="255pt" top="103pt" width="224pt" height="25pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="224"/>
						<xsl:with-param name="altezza" select="25"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="38pt" width="24pt" height="24pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="24pt" content-height="24pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-euro-bollettino.svg)"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="361pt" top="143pt" width="118pt" height="16pt">
				<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" text-align="start" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="118"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="14pt" width="230pt" height="19pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="15.21pt" padding-top="3.15pt" wrap-option="no-wrap">Banco<fo:inline font-family="TitilliumWeb-Bold">Posta</fo:inline></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="143pt" width="149pt" height="16pt">
				<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="codice_avviso_postale"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="149"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="319pt" top="143pt" width="42pt" height="16pt">
				<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" text-align="center" line-height="13.19pt" padding-top="1.16pt" wrap-option="no-wrap">P1</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="47pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-bollettino-postale.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="107pt" width="137pt" height="43pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.62pt" padding-top="1.778pt" wrap-option="no-wrap">Bollettino Postale pagabile in tutti gli <fo:block/>Uffici Postali e sui canali fisici o digitali <fo:block/>abilitati di Poste Italiane e dell’Ente <fo:block/>Creditore</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="426pt" top="41pt" width="169pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" end-indent="20pt" line-height="18.252pt" padding-top="1.774pt">Euro <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="importo"/></xsl:call-template></fo:inline></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="67pt" width="45pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Intestato a</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="170pt" top="85pt" width="85pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Destinatario dell’avviso</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="319pt" top="130pt" width="42pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" text-align="center" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Tipo</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="361pt" top="14pt" width="234pt" height="19pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="3.15pt" white-space-collapse="false"><xsl:choose><xsl:when test="normalize-space(numero_rata)">Rata <xsl:value-of select="numero_rata"/></xsl:when><xsl:otherwise>RATA UNICA </xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="normalize-space(giorni)"><xsl:value-of select="concat(' ', tipo, ' ')"/><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="giorni"/></xsl:call-template></xsl:when><xsl:when test="normalize-space(data)"> entro il <xsl:value-of select="data"/></xsl:when></xsl:choose></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="149pt" width="137pt" height="10pt">
				<fo:block font-family="TitilliumWeb" font-size="6pt" color="#CCCCCC" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="autorizzazione"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="6"/>
						<xsl:with-param name="colore" select="'#CCCCCC'"/>
						<xsl:with-param name="larghezza" select="137"/>
						<xsl:with-param name="altezza" select="10"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
