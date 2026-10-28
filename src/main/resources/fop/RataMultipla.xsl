<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/RataMultipla.jrxml: non modificare a mano.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="RataMultipla">
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="senza-nome-cognome-destinatario-e-altri" select="13 * number((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))"/>
		<xsl:variable name="senza-colonne" select="10 * number((not(@colonne = '2')) and (not(@colonne = '3')))"/>
		<xsl:variable name="allungamento-9af746ce" select="(ceiling(count(rata) div 2) * 165.0 - 10) * number((ceiling(count(rata) div 2) * 165.0 - 10) &gt; 0) * number(@colonne = '2')"/>
		<xsl:variable name="allungamento-ce204b7a" select="(ceiling(count(rata) div 3) * 160.0 - 10) * number((ceiling(count(rata) div 3) * 160.0 - 10) &gt; 0) * number(@colonne = '3')"/>
		<fo:block-container absolute-position="absolute" left="0pt" top="111pt" width="594pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">DATI PER IL PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="34pt" content-height="34pt" scaling="non-uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 34.67 22.99'><defs></defs><title>logo-pagopa</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path fill='#0066CC' d='M24.43,6.89v2.9H22.3V0h3.92c1.26,0,1.9.34,1.91,1V5.79c0,.68-.62,1-1.85,1H24.43ZM26,1.13H24.42V5.67H26Z'/><path fill='#0066CC' d='M1.78,9.8v2.91H0V2.91H3.28c1.06,0,1.59.34,1.6,1V8.71c0,.67-.52,1-1.54,1H1.78ZM3.11,4.05H1.78V8.59H3.11Z'/><path fill='#0066CC' d='M10.36,9.72H7c-1,0-1.49-.34-1.49-1v-2C5.5,6,6,5.7,7,5.7H8.59V4.05H7.26V5H5.51v-1c0-.67.52-1,1.57-1l1.73,0q1.55,0,1.56,1ZM7.26,8.59H8.6V6.84H7.26Z'/><path fill='#0066CC' d='M14.24,9.72H12.68c-1,0-1.54-.34-1.54-1V3.93c0-.68.54-1,1.6-1H16v8.78c0,.68-.52,1-1.55,1H12.69c-1,0-1.55-.34-1.55-1v-1h1.77v.89h1.33V9.72ZM12.91,8.59h1.33V4.05H12.91Z'/><path fill='#0066CC' d='M21.5,8.71c0,.67-.53,1-1.59,1H18.19c-1,0-1.56-.34-1.56-1V3.93q0-1,1.56-1H20c1,0,1.54.34,1.54,1ZM19.74,4.05H18.4V8.59h1.33Z'/><path fill='#0066CC' d='M34.67,1.07c0-.68-.62-1-1.85-1H30.7q-1.86,0-1.86,1V9.81H31v-3h1.6v2.5a11.58,11.58,0,0,1-20.71,7.11l1.06-.81L8.66,12.76l.19,6,1.35-1.09A13.68,13.68,0,0,0,34.67,9.31ZM32.56,5.69H31V1.19h1.6Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="54pt" top="24pt" width="150pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="5pt" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">AVVISO DI PAGAMENTO</fo:block>
		</fo:block-container>
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
		<fo:block-container absolute-position="absolute" left="54pt" top="{192 - $senza-nome-cognome-destinatario-e-altri}pt" width="489pt" height="15pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app o usa i dati sotto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="144pt" width="136pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="157pt" width="136pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="136"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="156pt" top="144pt" width="164pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="156pt" top="157pt" width="164pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="164"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="320pt" top="144pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="320pt" top="157pt" width="255pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="255"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="20pt" top="{189 - $senza-nome-cognome-destinatario-e-altri}pt" width="20pt" height="20pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="20pt" height="20pt"><fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="20pt" content-height="20pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' height='24px' viewBox='0 0 24 24' width='24px' fill='#000000'><path d='M0 0h24v24H0V0z' fill='none'/><path d='M11 7h2v2h-2zm0 4h2v6h-2zm1-9C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z'/></svg></fo:instream-foreign-object></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<xsl:if test="@colonne = '2'">
			<xsl:variable name="spostamento-9af746ce" select="-$senza-nome-cognome-destinatario-e-altri"/>
			<xsl:for-each select="rata">
				<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 2) * 297.0}pt" top="{221.0 + $spostamento-9af746ce + floor((position() - 1) div 2) * 165.0}pt" width="297pt" height="165pt">
					<fo:block/>
					<xsl:call-template name="DoppioFormato">
						<xsl:with-param name="Multipla" select="'true'"/>
					</xsl:call-template>
				</fo:block-container>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="@colonne = '3'">
			<xsl:variable name="spostamento-ce204b7a" select="-$senza-nome-cognome-destinatario-e-altri"/>
			<xsl:for-each select="rata">
				<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 3) * 198.0}pt" top="{221.0 + $spostamento-ce204b7a + floor((position() - 1) div 3) * 160.0}pt" width="198pt" height="160pt">
					<fo:block/>
					<xsl:call-template name="TriploFormato">
						<xsl:with-param name="Multipla" select="'true'"/>
					</xsl:call-template>
				</fo:block-container>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
