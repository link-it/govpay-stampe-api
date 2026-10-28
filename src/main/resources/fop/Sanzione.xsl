<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/Sanzione.jrxml: non modificare a mano.
	Le parti scritte a mano sono in script/fop/personalizzazioni/Sanzione.xml.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="Sanzione">
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="senza-ente-creditore-e-altri" select="20 * number((not(normalize-space(/ViolazioneInput/ente_creditore))) and (not(normalize-space(/ViolazioneInput/nome_cognome_destinatario))))"/>
		<xsl:variable name="senza-settore-ente-e-altri" select="25 * number((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))"/>
		<xsl:variable name="senza-info-ente" select="34 * number(not(normalize-space(/ViolazioneInput/info_ente)))"/>
		<xsl:variable name="allungamento-0d083ca2-testo"><xsl:variable name="valore"><xsl:value-of select="/ViolazioneInput/cf_destinatario"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 9, '', 162, 0, false())"/></xsl:variable><xsl:variable name="allungamento-0d083ca2" select="tj:allungamento(string($allungamento-0d083ca2-testo), 'RobotoMono', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-d1e5cae3" select="tj:allungamento(tj:righe(string(/ViolazioneInput/ente_creditore), 'TitilliumWeb-Bold', false(), false(), 10, 274, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-37d6964a-testo"><xsl:variable name="valore"><xsl:value-of select="/ViolazioneInput/cf_ente"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 9, '', 121, 0, false())"/></xsl:variable><xsl:variable name="allungamento-37d6964a" select="tj:allungamento(string($allungamento-37d6964a-testo), 'RobotoMono', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-fe15c28a" select="tj:allungamento(tj:righe(string(/ViolazioneInput/nome_cognome_destinatario), 'TitilliumWeb-Bold', false(), false(), 10, 269, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-0d083ca2-gruppo" select="(number($allungamento-0d083ca2 &gt; $allungamento-37d6964a) * $allungamento-0d083ca2 + number(not($allungamento-0d083ca2 &gt; $allungamento-37d6964a)) * $allungamento-37d6964a)"/>
		<xsl:variable name="allungamento-d1e5cae3-gruppo" select="(number($allungamento-d1e5cae3 &gt; $allungamento-fe15c28a) * $allungamento-d1e5cae3 + number(not($allungamento-d1e5cae3 &gt; $allungamento-fe15c28a)) * $allungamento-fe15c28a)"/>
		<fo:block-container absolute-position="absolute" left="251pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="149pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="rata/codice_avviso_2"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="149"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="251pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="107pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cf_ente"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="107"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="295pt" top="{648 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="299pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt"><fo:inline font-family="TitilliumWeb-Bold">Se paghi <xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><xsl:text> </xsl:text><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{741 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{677 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{691 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/nome_cognome_destinatario"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{723 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/ente_creditore"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="445pt" top="{677 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="129pt" height="25pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" line-height="18.252pt" padding-top="1.026pt">Euro <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_ridotto"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="60pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cbill"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="60"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="19pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="160pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="12.545pt" padding-top="2.588pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{709 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="107pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{648 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">IMPORTO RIDOTTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="8pt" top="{667 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="116pt" height="116pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_2_svg)"><fo:external-graphic content-width="116pt" content-height="116pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_2_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{755 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/oggetto_del_pagamento"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="24"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{499 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{531 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="295pt" top="{470 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="299pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt"><fo:inline font-family="TitilliumWeb-Bold">Se paghi <xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><xsl:text> </xsl:text><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{577 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/oggetto_del_pagamento"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="24"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="445pt" top="{499 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="129pt" height="25pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" line-height="18.252pt" padding-top="1.026pt">Euro <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_scontato"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{470 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">IMPORTO SCONTATO DEL 30%</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{563 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{513 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/nome_cognome_destinatario"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="8pt" top="{489 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="116pt" height="116pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_svg)"><fo:external-graphic content-width="116pt" content-height="116pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{545 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="255pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/ente_creditore"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="255"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="107pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="60pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cbill"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="60"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="107pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cf_ente"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="107"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="19pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="160pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="12.545pt" padding-top="2.588pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="251pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="149pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="rata/codice_avviso"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="149"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="251pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(/ViolazioneInput/info_ente)))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{189 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="275pt" height="34pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt"><xsl:variable name="righe" select="1 + string-length(/ViolazioneInput/info_ente) - string-length(translate(/ViolazioneInput/info_ente, '&lt;', ''))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 9, 6)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 9, 6, 'Top', 34, 0)"/></xsl:attribute><xsl:call-template name="markup"><xsl:with-param name="testo" select="/ViolazioneInput/info_ente"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="157pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="137pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="0pt" end-indent="5pt" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">Importo ridotto</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="43pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="516pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" end-indent="0pt" line-height="12.168pt" padding-top="2.92pt" wrap-option="no-wrap">Scegli l'importo corretto per i termini previsti, altrimenti riceverai altre richieste di pagamento per le somme residue ed eventuali somme aggiuntive.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="235pt" height="18pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">PAGA CON L'APP IO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="105pt" width="294pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="105pt" width="297pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">DESTINATARIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="136pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="0pt" end-indent="5pt" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">Importo scontato del 30%</fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(/ViolazioneInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/oggetto_del_pagamento"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="14"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="298pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">DOVE PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="54pt" top="24pt" width="150pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="5pt" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">AVVISO DI PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{271 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="235pt" height="44pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" line-height="11.591pt" padding-top="2.179pt"><xsl:if test="normalize-space(/ViolazioneInput/del_tuo_ente)"><xsl:value-of select="concat('oppure sul sito ', /ViolazioneInput/del_tuo_ente, ', ')"/></xsl:if>dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="34pt" content-height="34pt" scaling="non-uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 34.67 22.99'><defs></defs><title>logo-pagopa</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path fill='#0066CC' d='M24.43,6.89v2.9H22.3V0h3.92c1.26,0,1.9.34,1.91,1V5.79c0,.68-.62,1-1.85,1H24.43ZM26,1.13H24.42V5.67H26Z'/><path fill='#0066CC' d='M1.78,9.8v2.91H0V2.91H3.28c1.06,0,1.59.34,1.6,1V8.71c0,.67-.52,1-1.54,1H1.78ZM3.11,4.05H1.78V8.59H3.11Z'/><path fill='#0066CC' d='M10.36,9.72H7c-1,0-1.49-.34-1.49-1v-2C5.5,6,6,5.7,7,5.7H8.59V4.05H7.26V5H5.51v-1c0-.67.52-1,1.57-1l1.73,0q1.55,0,1.56,1ZM7.26,8.59H8.6V6.84H7.26Z'/><path fill='#0066CC' d='M14.24,9.72H12.68c-1,0-1.54-.34-1.54-1V3.93c0-.68.54-1,1.6-1H16v8.78c0,.68-.52,1-1.55,1H12.69c-1,0-1.55-.34-1.55-1v-1h1.77v.89h1.33V9.72ZM12.91,8.59h1.33V4.05H12.91Z'/><path fill='#0066CC' d='M21.5,8.71c0,.67-.53,1-1.59,1H18.19c-1,0-1.56-.34-1.56-1V3.93q0-1,1.56-1H20c1,0,1.54.34,1.54,1ZM19.74,4.05H18.4V8.59h1.33Z'/><path fill='#0066CC' d='M34.67,1.07c0-.68-.62-1-1.85-1H30.7q-1.86,0-1.86,1V9.81H31v-3h1.6v2.5a11.58,11.58,0,0,1-20.71,7.11l1.06-.81L8.66,12.76l.19,6,1.35-1.09A13.68,13.68,0,0,0,34.67,9.31ZM32.56,5.69H31V1.19h1.6Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{377 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="11pt" height="11pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="11pt" height="11pt"><fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="11pt" content-height="11pt" scaling="uniform"><svg width='11' height='11' viewBox='0 0 11 11' fill='none' xmlns='http://www.w3.org/2000/svg'><g clip-path='url(#clip0_1:960)'><path d='M8.28948 2.496H2.68422C2.44683 2.496 2.25439 2.68813 2.25439 2.92514C2.25439 3.16215 2.44683 3.35428 2.68422 3.35428H8.28948C8.52687 3.35428 8.71931 3.16215 8.71931 2.92514C8.71931 2.68813 8.52687 2.496 8.28948 2.496Z' fill='black'/><path d='M8.28948 4.36145H2.68422C2.44683 4.36145 2.25439 4.55358 2.25439 4.79059C2.25439 5.0276 2.44683 5.21973 2.68422 5.21973H8.28948C8.52687 5.21973 8.71931 5.0276 8.71931 4.79059C8.71931 4.55358 8.52687 4.36145 8.28948 4.36145Z' fill='black'/><path d='M4.78071 6.1481H2.68422C2.44683 6.1481 2.25439 6.34023 2.25439 6.57724C2.25439 6.81425 2.44683 7.00638 2.68422 7.00638H4.78071C5.0181 7.00638 5.21054 6.81425 5.21054 6.57724C5.21054 6.34023 5.0181 6.1481 4.78071 6.1481Z' fill='black'/><path d='M9.28076 8.5672e-08H1.71935C1.26156 -0.000142768 0.821845 0.178371 0.494066 0.497446C0.166287 0.816522 -0.0235894 1.25087 -0.0350342 1.70779V9.27468C-0.0350342 9.73923 0.149803 10.1848 0.478814 10.5132C0.807825 10.8417 1.25406 11.0263 1.71935 11.0263H8.0176L10.9825 8.09235V1.75159C10.9827 1.296 10.8051 0.858247 10.4874 0.53123C10.1697 0.204212 9.73687 0.0136677 9.28076 8.5672e-08ZM8.22812 9.8352V8.59156C8.22812 8.50097 8.26417 8.41408 8.32832 8.35003C8.39248 8.28597 8.4795 8.24999 8.57023 8.24999H9.82462L8.22812 9.8352ZM8.57023 7.56687C8.29722 7.56686 8.03526 7.67453 7.8414 7.86645C7.64753 8.05837 7.53745 8.31899 7.53514 8.59156V10.1505H1.71935C1.48671 10.1505 1.26359 10.0582 1.09908 9.89395C0.934578 9.7297 0.842159 9.50695 0.842159 9.27468V1.70779C0.842159 1.47551 0.934578 1.25276 1.09908 1.08852C1.26359 0.924274 1.48671 0.83199 1.71935 0.83199H9.28076C9.5134 0.83199 9.73652 0.924274 9.90103 1.08852C10.0655 1.25276 10.1579 1.47551 10.1579 1.70779V7.56687H8.57023Z' fill='black'/></g><defs><clipPath id='clip0_1:960'><rect width='11' height='11' fill='white'/></clipPath></defs></svg></fo:instream-foreign-object></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{341 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="11pt" height="11pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="11pt" height="11pt"><fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="11pt" content-height="11pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' x='0px' y='0px' width='11px' height='11px' viewBox='0 0 11 11' enable-background='new 0 0 11 11' xml:space='preserve'><path d='M11,7.133c0,0.237-0.192,0.43-0.43,0.43s-0.43-0.192-0.43-0.43V2.578c0-0.474-0.385-0.859-0.859-0.859H8.723v0.43	c0,0.237-0.192,0.43-0.43,0.43c-0.237,0-0.43-0.192-0.43-0.43v-0.43H5.908v0.43c0,0.237-0.192,0.43-0.43,0.43	c-0.237,0-0.43-0.192-0.43-0.43v-0.43H3.115v0.43c0,0.237-0.192,0.43-0.43,0.43c-0.237,0-0.43-0.192-0.43-0.43v-0.43H1.719	c-0.474,0-0.859,0.386-0.859,0.859v6.703c0,0.474,0.386,0.859,0.859,0.859h7.562c0.474,0,0.859-0.385,0.859-0.859	c0-0.237,0.192-0.43,0.43-0.43S11,9.044,11,9.281C11,10.229,10.229,11,9.281,11H1.719C0.771,11,0,10.229,0,9.281V2.578	C0,1.63,0.771,0.859,1.719,0.859h0.537V0.43C2.256,0.192,2.448,0,2.686,0c0.237,0,0.43,0.192,0.43,0.43v0.43h1.934V0.43	C5.049,0.192,5.241,0,5.479,0c0.237,0,0.43,0.192,0.43,0.43v0.43h1.955V0.43C7.863,0.192,8.056,0,8.293,0	c0.237,0,0.43,0.192,0.43,0.43v0.43h0.559C10.229,0.859,11,1.63,11,2.578V7.133z M8.293,4.941c0.237,0,0.43-0.192,0.43-0.43	c0-0.237-0.192-0.43-0.43-0.43c-0.237,0-0.43,0.192-0.43,0.43C7.863,4.749,8.056,4.941,8.293,4.941z M5.994,7.82	c0,0.474,0.386,0.859,0.859,0.859h1.01c0.474,0,0.859-0.386,0.859-0.859v-1.01c0-0.474-0.386-0.859-0.859-0.859h-1.01	c-0.474,0-0.859,0.386-0.859,0.859V7.82z M6.854,6.811h1.01l0.001,1.01l-0.001,0h-1.01V6.811z M6.854,4.512	c0,0.237-0.192,0.43-0.43,0.43s-0.43-0.192-0.43-0.43c0-0.237,0.192-0.43,0.43-0.43S6.854,4.274,6.854,4.512z M3.115,8.25	c0,0.237-0.192,0.43-0.43,0.43s-0.43-0.192-0.43-0.43s0.192-0.43,0.43-0.43S3.115,8.013,3.115,8.25z M2.686,4.941	c0.237,0,0.43-0.192,0.43-0.43c0-0.237-0.192-0.43-0.43-0.43s-0.43,0.192-0.43,0.43C2.256,4.749,2.448,4.941,2.686,4.941z	 M3.115,6.381c0,0.237-0.192,0.43-0.43,0.43s-0.43-0.192-0.43-0.43s0.192-0.43,0.43-0.43S3.115,6.144,3.115,6.381z M4.555,6.811	c0.237,0,0.43-0.192,0.43-0.43s-0.192-0.43-0.43-0.43s-0.43,0.192-0.43,0.43S4.317,6.811,4.555,6.811z M4.984,4.512	c0,0.237-0.192,0.43-0.43,0.43s-0.43-0.192-0.43-0.43c0-0.237,0.192-0.43,0.43-0.43S4.984,4.274,4.984,4.512z M4.555,8.68	c0.237,0,0.43-0.192,0.43-0.43s-0.192-0.43-0.43-0.43s-0.43,0.192-0.43,0.43S4.317,8.68,4.555,8.68z'/></svg></fo:instream-foreign-object></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="43pt" top="{374 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="251pt" height="55pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.117pt" padding-top="2.03pt" wrap-option="no-wrap"><fo:inline font-family="TitilliumWeb-Bold">Cosa succede in caso di giacenza?</fo:inline><fo:block/>La notifica corrisponde al giorno del ritiro, se questo avviene entro 10 <fo:block/>giorni dalla data di deposito (la data più vecchia sull'avviso di giacenza) Se <fo:block/>non ritiri il verbale entro i 10 giorni, calcola le scadenze iniziando a contare <fo:block/>dall’undicesimo giorno.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="559pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="16pt" height="16pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="16pt" content-height="16pt" scaling="uniform"><svg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'><path fill-rule='evenodd' clip-rule='evenodd' d='M4.93236 8.34956L5.43907 8.83888L7.43383 10.9984L7.43383 10.9983L5.43916 8.83888L4.93245 8.34956C4.77071 8.19337 4.57234 8.11219 4.37506 8.11228C4.57231 8.11222 4.77065 8.19339 4.93236 8.34956ZM8.1682 13.1263C8.4997 13.1257 8.8288 12.9702 9.12938 12.659L12.3624 9.31109C12.6653 8.99741 12.6689 8.52291 12.3431 8.20835C12.2089 8.07876 12.0465 8.0072 11.8803 7.99276C12.0464 8.00721 12.2089 8.07878 12.343 8.20835C12.6688 8.52291 12.6652 8.99741 12.3623 9.31109L9.1293 12.659C8.82874 12.9702 8.49967 13.1256 8.1682 13.1263ZM8.85681 10.9734L8.8779 7.76474L8.83183 4.10641C8.83414 3.8988 8.742 3.71656 8.59723 3.57676C8.42238 3.40791 8.21079 3.31914 8.0021 3.31964C8.21076 3.31917 8.42232 3.40793 8.59714 3.57676C8.74192 3.71656 8.83405 3.8988 8.83175 4.10641L8.87781 7.76474L8.85672 10.9735L8.85681 10.9734ZM7.65835 11.5362L7.48884 7.78621L7.42514 4.13689C7.42236 3.97787 7.48067 3.8257 7.60208 3.69999C7.84027 3.45333 8.18764 3.45934 8.45821 3.72063C8.57088 3.82943 8.63329 3.9612 8.6317 4.10419L8.6778 7.76534L8.65314 11.519L10.9111 8.88055L11.3962 8.37818C11.6313 8.13481 11.973 8.129 12.2041 8.35221C12.4484 8.58808 12.4466 8.93589 12.2184 9.17215L8.98543 12.52C8.71554 12.7995 8.43863 12.9229 8.17709 12.9263C7.91518 12.9297 7.63333 12.8132 7.35315 12.5426L4.00528 9.30959C3.75986 9.07259 3.74617 8.73107 3.97911 8.48985C4.21006 8.25069 4.54587 8.25436 4.79343 8.49343L5.29603 8.97878L7.65835 11.5362Z' fill='black'/></svg></fo:instream-foreign-object></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{269 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="136pt" height="25pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="15pt" end-indent="5pt" line-height="22.815pt" padding-top="1.883pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_scontato"/></xsl:call-template> Euro</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="540pt" top="{324 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="35pt" content-height="35pt" scaling="non-uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 33.14 33.45'><title>canali-fisici</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path d='M24,6.47a12.68,12.68,0,0,1,1.06.88.67.67,0,0,0,.91-1,12.26,12.26,0,0,0-1.18-1A.67.67,0,1,0,24,6.47Z'/><path d='M5.73,14.34a3.52,3.52,0,1,0,3.51,3.52,3.52,3.52,0,0,0-3.51-3.52Zm0,5.7a2.18,2.18,0,1,1,2.18-2.18A2.19,2.19,0,0,1,5.73,20Z'/><path d='M20.1,5.73a3.51,3.51,0,1,0-3.51,3.51A3.51,3.51,0,0,0,20.1,5.73Zm-5.69,0a2.18,2.18,0,1,1,2.18,2.18,2.19,2.19,0,0,1-2.18-2.18Z'/><path d='M30.3,18.43a13.48,13.48,0,0,0,.13-1.9,13.75,13.75,0,0,0-2.83-8.4A.67.67,0,0,0,26.66,8a.66.66,0,0,0-.12.93,12.48,12.48,0,0,1,2.56,7.6c0,.45,0,.9-.07,1.35a5.82,5.82,0,0,0-1.62-.23,5.74,5.74,0,0,0-5.73,5.73A9.3,9.3,0,0,0,23,27.27a12.55,12.55,0,0,1-15.2-1.78c.32-.46.66-1,1-1.52,1.75-2.77,2.63-4.83,2.63-6.11a5.74,5.74,0,0,0-5.73-5.74,5.62,5.62,0,0,0-.92.08,12.58,12.58,0,0,1,6.06-6.83c0,.12,0,.24,0,.36,0,1.29.89,3.34,2.63,6.11,1.27,2,2.52,3.64,2.57,3.71a.67.67,0,0,0,1.06,0c.05-.07,1.3-1.7,2.57-3.71C21.43,9.07,22.32,7,22.32,5.73a5.73,5.73,0,0,0-11.1-2,13.93,13.93,0,0,0-8,9A5.74,5.74,0,0,0,0,17.86C0,19.14.89,21.2,2.64,24c1.26,2,2.51,3.63,2.56,3.7a.67.67,0,0,0,1.06,0s.33-.44.79-1.07A13.69,13.69,0,0,0,11,29.22a13.86,13.86,0,0,0,12.68-.79c.2.34.41.69.64,1.06,1.27,2,2.52,3.63,2.57,3.7a.67.67,0,0,0,1.06,0c0-.07,1.3-1.7,2.56-3.7,1.75-2.77,2.64-4.83,2.64-6.11a5.73,5.73,0,0,0-2.84-4.95ZM16.59,1.33A4.41,4.41,0,0,1,21,5.73c0,1.87-2.81,6.12-4.4,8.3-1.59-2.18-4.4-6.43-4.4-8.3a4.41,4.41,0,0,1,4.4-4.4ZM5.73,26.15c-1.59-2.18-4.4-6.42-4.4-8.29a4.4,4.4,0,0,1,8.8,0c0,1.87-2.81,6.11-4.4,8.29Zm21.68,5.52C25.82,29.5,23,25.25,23,23.38a4.4,4.4,0,0,1,8.8,0c0,1.87-2.81,6.12-4.4,8.29Z'/><path d='M27.41,19.86a3.52,3.52,0,1,0,3.51,3.52,3.52,3.52,0,0,0-3.51-3.52Zm0,5.7a2.18,2.18,0,1,1,2.18-2.18,2.19,2.19,0,0,1-2.18,2.18Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="298pt" top="{171 - $senza-ente-creditore-e-altri + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="277pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/indirizzo_destinatario_2"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="269"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="157pt" top="{269 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="137pt" height="25pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="15pt" end-indent="5pt" line-height="22.815pt" padding-top="1.883pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_ridotto"/></xsl:call-template> Euro</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="394pt" top="19pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/ViolazioneInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/ViolazioneInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="277pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" start-indent="0pt" line-height="13.689pt" padding-top="3.285pt" wrap-option="no-wrap">Vai su <fo:inline font-weight="bold">pagopa.gov.it</fo:inline></fo:block>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/ViolazioneInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="458pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="458"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="540pt" top="{258 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="35pt" content-height="35pt" scaling="non-uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 33.31 31.09'><defs><style>.cls-1{fill:#1d1d1b;}</style></defs><title>canali-digitali</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path class='cls-1' d='M31.65,15V2.78A2.79,2.79,0,0,0,28.87,0H4.44A2.78,2.78,0,0,0,1.67,2.78V11.1A1.67,1.67,0,0,0,0,12.77V29.43a1.67,1.67,0,0,0,1.67,1.66H13.88a1.67,1.67,0,0,0,1.67-1.66H20.2a1.65,1.65,0,0,0,1.32-.66,1.67,1.67,0,0,0,.29-1.45l-1.1-4h3.16v6.11a1.67,1.67,0,0,0,1.67,1.66h6.11a1.67,1.67,0,0,0,1.66-1.66V16.66A1.67,1.67,0,0,0,31.65,15Zm.55,1.67v1.11H25V16.66a.56.56,0,0,1,.56-.56h6.11A.56.56,0,0,1,32.2,16.66ZM25,18.88H32.2V27.2H25ZM2.78,2.78A1.66,1.66,0,0,1,4.44,1.11H28.87a1.67,1.67,0,0,1,1.67,1.67V15h-5a1.67,1.67,0,0,0-1.67,1.67v1.11H15.55v-5a1.67,1.67,0,0,0-1.67-1.67H2.78ZM14.44,27.2H1.11V15H14.44Zm-12.77-15H13.88a.56.56,0,0,1,.56.56v1.11H1.11V12.77A.56.56,0,0,1,1.67,12.21ZM13.88,30H1.67a.56.56,0,0,1-.56-.55V28.32H14.44v1.11A.56.56,0,0,1,13.88,30Zm6.76-1.88a.56.56,0,0,1-.44.22H15.55V27.2h5.07l.11.41A.54.54,0,0,1,20.64,28.1Zm-.32-2H15.55V23.32h4Zm-4.77-3.88V18.88h8.32v3.33ZM31.65,30H25.54a.56.56,0,0,1-.56-.55V28.32H32.2v1.11A.55.55,0,0,1,31.65,30Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="403pt" top="105pt" width="172pt" height="18pt">
			<fo:block font-family="RobotoMono" font-size="9pt" text-align="end" start-indent="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cf_destinatario"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="larghezza" select="162"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/ente_creditore))) and (not(normalize-space(/ViolazioneInput/nome_cognome_destinatario))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{128 + $allungamento-0d083ca2-gruppo}pt" width="274pt" height="20pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/ente_creditore"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="274"/>
						<xsl:with-param name="altezza" select="20"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/ViolazioneInput/scadenza_ridotto)">
			<fo:block-container absolute-position="absolute" left="157pt" top="{293 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="137pt" height="30pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" end-indent="5pt" line-height="11.591pt" padding-top="2.179pt"><fo:inline font-family="TitilliumWeb-Bold"><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><fo:block/><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="157pt" top="105pt" width="137pt" height="18pt">
			<fo:block font-family="RobotoMono" font-size="9pt" text-align="end" start-indent="8pt" end-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/ViolazioneInput/cf_ente"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="larghezza" select="121"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="490pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/ViolazioneInput/logo_ente"/>
				<xsl:with-param name="px" select="/ViolazioneInput/logo_ente_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="43pt" top="{338 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="251pt" height="32pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.459pt" padding-top="1.859pt" wrap-option="no-wrap"><fo:inline font-family="TitilliumWeb-Bold">Come si calcolano le scadenze?</fo:inline><fo:block/>Il giorno della notifica non si conta. Se la scadenza è di domenica (o <fo:block/>festivo), puoi pagare anche il primo giorno feriale successivo.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="16pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="16pt" height="16pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="16pt" content-height="16pt" scaling="uniform"><svg width='16' height='16' viewBox='0 0 16 16' fill='none' xmlns='http://www.w3.org/2000/svg'><path fill-rule='evenodd' clip-rule='evenodd' d='M4.93236 8.34956L5.43907 8.83888L7.43383 10.9984L7.43383 10.9983L5.43916 8.83888L4.93245 8.34956C4.77071 8.19337 4.57234 8.11219 4.37506 8.11228C4.57231 8.11222 4.77065 8.19339 4.93236 8.34956ZM8.1682 13.1263C8.4997 13.1257 8.8288 12.9702 9.12938 12.659L12.3624 9.31109C12.6653 8.99741 12.6689 8.52291 12.3431 8.20835C12.2089 8.07876 12.0465 8.0072 11.8803 7.99276C12.0464 8.00721 12.2089 8.07878 12.343 8.20835C12.6688 8.52291 12.6652 8.99741 12.3623 9.31109L9.1293 12.659C8.82874 12.9702 8.49967 13.1256 8.1682 13.1263ZM8.85681 10.9734L8.8779 7.76474L8.83183 4.10641C8.83414 3.8988 8.742 3.71656 8.59723 3.57676C8.42238 3.40791 8.21079 3.31914 8.0021 3.31964C8.21076 3.31917 8.42232 3.40793 8.59714 3.57676C8.74192 3.71656 8.83405 3.8988 8.83175 4.10641L8.87781 7.76474L8.85672 10.9735L8.85681 10.9734ZM7.65835 11.5362L7.48884 7.78621L7.42514 4.13689C7.42236 3.97787 7.48067 3.8257 7.60208 3.69999C7.84027 3.45333 8.18764 3.45934 8.45821 3.72063C8.57088 3.82943 8.63329 3.9612 8.6317 4.10419L8.6778 7.76534L8.65314 11.519L10.9111 8.88055L11.3962 8.37818C11.6313 8.13481 11.973 8.129 12.2041 8.35221C12.4484 8.58808 12.4466 8.93589 12.2184 9.17215L8.98543 12.52C8.71554 12.7995 8.43863 12.9229 8.17709 12.9263C7.91518 12.9297 7.63333 12.8132 7.35315 12.5426L4.00528 9.30959C3.75986 9.07259 3.74617 8.73107 3.97911 8.48985C4.21006 8.25069 4.54587 8.25436 4.79343 8.49343L5.29603 8.97878L7.65835 11.5362Z' fill='black'/></svg></fo:instream-foreign-object></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{332 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="235pt" height="52pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" line-height="10.891pt" padding-top="2.529pt" wrap-option="no-wrap">presso Banche e Sportelli ATM, negli Uffici Postali e Punti <fo:block/>Postali, nei Bar, Edicole, Ricevitorie, Supermercati, <fo:block/>Tabaccherie e altri Esercenti Convenzionati.</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/ente_creditore))) and (not(normalize-space(/ViolazioneInput/nome_cognome_destinatario))))">
			<fo:block-container absolute-position="absolute" left="298pt" top="{128 + $allungamento-0d083ca2-gruppo}pt" width="277pt" height="20pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/nome_cognome_destinatario"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="269"/>
						<xsl:with-param name="altezza" select="20"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/ViolazioneInput/scadenza_scontato)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{293 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="136pt" height="30pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" end-indent="5pt" line-height="11.591pt" padding-top="2.179pt"><fo:inline font-family="TitilliumWeb-Bold"><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><fo:block/><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{156 - $senza-ente-creditore-e-altri + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="274pt" height="25pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/settore_ente"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="274"/>
						<xsl:with-param name="altezza" select="25"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="298pt" top="{156 - $senza-ente-creditore-e-altri + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="277pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/ViolazioneInput/indirizzo_destinatario_1"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="269"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="294pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">QUANTO E QUANDO PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{315 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-0d083ca2-gruppo + $allungamento-d1e5cae3-gruppo}pt" width="235pt" height="18pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">PAGA SUL TERRITORIO</fo:block>
		</fo:block-container>
	</xsl:template>

</xsl:stylesheet>
