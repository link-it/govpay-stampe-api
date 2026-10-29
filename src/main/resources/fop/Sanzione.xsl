<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO Sanzione (issue #37): pagina della violazione del codice della strada, importo ridotto e scontato (da violazione-cds.xsl).
	Derivato dal template Jasper Sanzione.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/singola; i dati comuni sono sotto /ViolazioneInput.
	Le etichette scadenza_ridotto e scadenza_scontato hanno la forma "*parte 1*parte 2".
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="Sanzione">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="senza-ente-creditore-e-altri" select="20 * number((not(normalize-space(/ViolazioneInput/ente_creditore))) and (not(normalize-space(/ViolazioneInput/nome_cognome_destinatario))))"/>
		<xsl:variable name="senza-settore-ente-e-altri" select="25 * number((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))"/>
		<xsl:variable name="senza-info-ente" select="34 * number(not(normalize-space(/ViolazioneInput/info_ente)))"/>
		<xsl:variable name="allungamento-cf-destinatario-testo"><xsl:variable name="valore"><xsl:value-of select="/ViolazioneInput/cf_destinatario"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 9, '', 162, 0, false())"/></xsl:variable><xsl:variable name="allungamento-cf-destinatario" select="tj:allungamento(string($allungamento-cf-destinatario-testo), 'RobotoMono', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-ente-creditore" select="tj:allungamento(tj:righe(string(/ViolazioneInput/ente_creditore), 'TitilliumWeb-Bold', false(), false(), 10, 274, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-cf-ente-testo"><xsl:variable name="valore"><xsl:value-of select="/ViolazioneInput/cf_ente"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 9, '', 121, 0, false())"/></xsl:variable><xsl:variable name="allungamento-cf-ente" select="tj:allungamento(string($allungamento-cf-ente-testo), 'RobotoMono', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-nome-cognome-destinatario" select="tj:allungamento(tj:righe(string(/ViolazioneInput/nome_cognome_destinatario), 'TitilliumWeb-Bold', false(), false(), 10, 269, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-cf-destinatario-e-affiancati" select="(number($allungamento-cf-destinatario &gt; $allungamento-cf-ente) * $allungamento-cf-destinatario + number(not($allungamento-cf-destinatario &gt; $allungamento-cf-ente)) * $allungamento-cf-ente)"/>
		<xsl:variable name="allungamento-ente-creditore-e-affiancati" select="(number($allungamento-ente-creditore &gt; $allungamento-nome-cognome-destinatario) * $allungamento-ente-creditore + number(not($allungamento-ente-creditore &gt; $allungamento-nome-cognome-destinatario)) * $allungamento-nome-cognome-destinatario)"/>
		<fo:block-container absolute-position="absolute" left="251pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="149pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="251pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="107pt" height="16pt">
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
		<!-- bollettino importo ridotto: "Se paghi <parte 1>" in grassetto + parte 2 -->
		<fo:block-container absolute-position="absolute" left="295pt" top="{648 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="299pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt"><fo:inline font-family="TitilliumWeb-Bold">Se paghi <xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><xsl:text> </xsl:text><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_ridotto"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{741 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{677 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{691 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="18pt">
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
		<fo:block-container absolute-position="absolute" left="190pt" top="{723 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="18pt">
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
		<!-- "Euro " + importo in RobotoMono grassetto: riga con due font, padding ridotto di 0,48 pt -->
		<fo:block-container absolute-position="absolute" left="445pt" top="{677 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="129pt" height="25pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" line-height="18.252pt" padding-top="1.026pt">Euro <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_ridotto"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{798 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="60pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="19pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="160pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="12.545pt" padding-top="2.588pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{709 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="467pt" top="{784 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="107pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{648 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">IMPORTO RIDOTTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="8pt" top="{667 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="116pt" height="116pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_2_svg)"><fo:external-graphic content-width="116pt" content-height="116pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_2_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{755 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="24pt">
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
		<fo:block-container absolute-position="absolute" left="190pt" top="{499 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{531 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<!-- bollettino importo scontato -->
		<fo:block-container absolute-position="absolute" left="295pt" top="{470 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="299pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt"><fo:inline font-family="TitilliumWeb-Bold">Se paghi <xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><xsl:text> </xsl:text><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{577 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="24pt">
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
		<fo:block-container absolute-position="absolute" left="445pt" top="{499 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="129pt" height="25pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" line-height="18.252pt" padding-top="1.026pt">Euro <fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_scontato"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{470 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">IMPORTO SCONTATO DEL 30%</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{563 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.441pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{513 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="18pt">
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
		<fo:block-container absolute-position="absolute" left="8pt" top="{489 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="116pt" height="116pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_svg)"><fo:external-graphic content-width="116pt" content-height="116pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{545 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="255pt" height="18pt">
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
		<fo:block-container absolute-position="absolute" left="467pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="107pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="190pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="60pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="467pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="107pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="190pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="19pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="160pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="12.545pt" padding-top="2.588pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="251pt" top="{620 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="149pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="251pt" top="{606 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<!-- informazioni dell'ente (sito, telefono, PEC): righe separate da <br/>, contate per l'interlinea fissa di Jasper -->
		<xsl:if test="not(not(normalize-space(/ViolazioneInput/info_ente)))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{189 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="275pt" height="34pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt"><xsl:variable name="righe" select="1 + string-length(/ViolazioneInput/info_ente) - string-length(translate(/ViolazioneInput/info_ente, '&lt;', ''))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 9, 6)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 9, 6, 'Top', 34, 0)"/></xsl:attribute><xsl:call-template name="markup"><xsl:with-param name="testo" select="/ViolazioneInput/info_ente"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="157pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="137pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="0pt" end-indent="5pt" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">Importo ridotto</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="43pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="516pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" end-indent="0pt" line-height="12.168pt" padding-top="2.92pt" wrap-option="no-wrap">Scegli l'importo corretto per i termini previsti, altrimenti riceverai altre richieste di pagamento per le somme residue ed eventuali somme aggiuntive.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="235pt" height="18pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">PAGA CON L'APP IO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="105pt" width="294pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="105pt" width="297pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">DESTINATARIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{254 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="136pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="298pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" end-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">DOVE PAGARE?</fo:block>
		</fo:block-container>
		<xsl:call-template name="intestazione-pagopa"/>
		<!-- "oppure sul sito <del_tuo_ente>, " solo se valorizzato -->
		<fo:block-container absolute-position="absolute" left="298pt" top="{271 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="235pt" height="44pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" line-height="11.591pt" padding-top="2.179pt"><xsl:if test="normalize-space(/ViolazioneInput/del_tuo_ente)"><xsl:value-of select="concat('oppure sul sito ', /ViolazioneInput/del_tuo_ente, ', ')"/></xsl:if>dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{377 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="11pt" height="11pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="11pt" height="11pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="11pt" content-height="11pt" scaling="uniform" src="url(classpath:/fop/icone/nota.svg)"/></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{341 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="11pt" height="11pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="11pt" height="11pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="11pt" content-height="11pt" scaling="uniform" src="url(classpath:/fop/icone/calendario.svg)"/></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="43pt" top="{374 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="251pt" height="55pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.117pt" padding-top="2.03pt" wrap-option="no-wrap"><fo:inline font-family="TitilliumWeb-Bold">Cosa succede in caso di giacenza?</fo:inline><fo:block/>La notifica corrisponde al giorno del ritiro, se questo avviene entro 10 <fo:block/>giorni dalla data di deposito (la data più vecchia sull'avviso di giacenza) Se <fo:block/>non ritiri il verbale entro i 10 giorni, calcola le scadenze iniziando a contare <fo:block/>dall’undicesimo giorno.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="559pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="16pt" height="16pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="16pt" content-height="16pt" scaling="uniform" src="url(classpath:/fop/icone/freccia-giu.svg)"/></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<!-- importi in evidenza: importo formattato + " Euro" -->
		<fo:block-container absolute-position="absolute" left="20pt" top="{269 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="136pt" height="25pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="15pt" end-indent="5pt" line-height="22.815pt" padding-top="1.883pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo_scontato"/></xsl:call-template> Euro</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="540pt" top="{324 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-fisici.svg)"/></fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="298pt" top="{171 - $senza-ente-creditore-e-altri + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="277pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="157pt" top="{269 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="137pt" height="25pt">
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
		<fo:block-container absolute-position="absolute" left="298pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="277pt" height="18pt">
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
		<fo:block-container absolute-position="absolute" left="540pt" top="{258 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-digitali.svg)"/></fo:block>
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
			<fo:block-container absolute-position="absolute" left="20pt" top="{128 + $allungamento-cf-destinatario-e-affiancati}pt" width="274pt" height="20pt">
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
			<fo:block-container absolute-position="absolute" left="157pt" top="{293 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="137pt" height="30pt">
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
		<fo:block-container absolute-position="absolute" left="43pt" top="{338 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="251pt" height="32pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.459pt" padding-top="1.859pt" wrap-option="no-wrap"><fo:inline font-family="TitilliumWeb-Bold">Come si calcolano le scadenze?</fo:inline><fo:block/>Il giorno della notifica non si conta. Se la scadenza è di domenica (o <fo:block/>festivo), puoi pagare anche il primo giorno feriale successivo.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="16pt" top="{443 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="16pt" height="16pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="16pt" content-height="16pt" scaling="uniform" src="url(classpath:/fop/icone/freccia-giu.svg)"/></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{332 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="235pt" height="52pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" line-height="10.891pt" padding-top="2.529pt" wrap-option="no-wrap">presso Banche e Sportelli ATM, negli Uffici Postali e Punti <fo:block/>Postali, nei Bar, Edicole, Ricevitorie, Supermercati, <fo:block/>Tabaccherie e altri Esercenti Convenzionati.</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/ente_creditore))) and (not(normalize-space(/ViolazioneInput/nome_cognome_destinatario))))">
			<fo:block-container absolute-position="absolute" left="298pt" top="{128 + $allungamento-cf-destinatario-e-affiancati}pt" width="277pt" height="20pt">
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
		<!-- scadenze sotto gli importi: parte 1 in grassetto, a capo, parte 2 -->
		<xsl:if test="normalize-space(/ViolazioneInput/scadenza_scontato)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{293 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="136pt" height="30pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" end-indent="5pt" line-height="11.591pt" padding-top="2.179pt"><fo:inline font-family="TitilliumWeb-Bold"><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="1"/></xsl:call-template></fo:inline><fo:block/><xsl:call-template name="parte-etichetta"><xsl:with-param name="etichetta" select="/ViolazioneInput/scadenza_scontato"/><xsl:with-param name="n" select="2"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/ViolazioneInput/settore_ente))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_1))) and (not(normalize-space(/ViolazioneInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{156 - $senza-ente-creditore-e-altri + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="274pt" height="25pt">
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
			<fo:block-container absolute-position="absolute" left="298pt" top="{156 - $senza-ente-creditore-e-altri + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="277pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="0pt" top="{231 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="294pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">QUANTO E QUANDO PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="298pt" top="{315 - $senza-ente-creditore-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-destinatario-e-affiancati + $allungamento-ente-creditore-e-affiancati}pt" width="235pt" height="18pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">PAGA SUL TERRITORIO</fo:block>
		</fo:block-container>
	</xsl:template>

</xsl:stylesheet>
