<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/RataUnica.jrxml: non modificare a mano.
	Le parti scritte a mano sono in script/fop/personalizzazioni/RataUnica.xml.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg">

	<xsl:template name="RataUnica">
		<!-- banda detail, y=0.0 -->
		<fo:block-container absolute-position="absolute" left="299pt" top="253pt" width="296pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="8pt" end-indent="20pt" wrap-option="no-wrap">DOVE PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="403pt" top="253pt" width="173pt" height="20pt" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" line-height="12.66pt" text-align="end" start-indent="0pt" wrap-option="no-wrap">Vai su <fo:inline font-weight="bold">pagopa.gov.it</fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="253pt" width="294pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="20pt" wrap-option="no-wrap">QUANTO E QUANDO PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block line-height="0"><fo:instream-foreign-object content-width="34pt" content-height="34pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 34.67 22.99'><defs></defs><title>logo-pagopa</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path fill='#0066CC' d='M24.43,6.89v2.9H22.3V0h3.92c1.26,0,1.9.34,1.91,1V5.79c0,.68-.62,1-1.85,1H24.43ZM26,1.13H24.42V5.67H26Z'/><path fill='#0066CC' d='M1.78,9.8v2.91H0V2.91H3.28c1.06,0,1.59.34,1.6,1V8.71c0,.67-.52,1-1.54,1H1.78ZM3.11,4.05H1.78V8.59H3.11Z'/><path fill='#0066CC' d='M10.36,9.72H7c-1,0-1.49-.34-1.49-1v-2C5.5,6,6,5.7,7,5.7H8.59V4.05H7.26V5H5.51v-1c0-.67.52-1,1.57-1l1.73,0q1.55,0,1.56,1ZM7.26,8.59H8.6V6.84H7.26Z'/><path fill='#0066CC' d='M14.24,9.72H12.68c-1,0-1.54-.34-1.54-1V3.93c0-.68.54-1,1.6-1H16v8.78c0,.68-.52,1-1.55,1H12.69c-1,0-1.55-.34-1.55-1v-1h1.77v.89h1.33V9.72ZM12.91,8.59h1.33V4.05H12.91Z'/><path fill='#0066CC' d='M21.5,8.71c0,.67-.53,1-1.59,1H18.19c-1,0-1.56-.34-1.56-1V3.93q0-1,1.56-1H20c1,0,1.54.34,1.54,1ZM19.74,4.05H18.4V8.59h1.33Z'/><path fill='#0066CC' d='M34.67,1.07c0-.68-.62-1-1.85-1H30.7q-1.86,0-1.86,1V9.81H31v-3h1.6v2.5a11.58,11.58,0,0,1-20.71,7.11l1.06-.81L8.66,12.76l.19,6,1.35-1.09A13.68,13.68,0,0,0,34.67,9.31ZM32.56,5.69H31V1.19h1.6Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="54pt" top="24pt" width="150pt" height="16pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" line-height="12.66pt" start-indent="5pt" padding-top="2.17pt" wrap-option="no-wrap">AVVISO DI PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="490pt" top="20pt" width="85pt" height="85pt">
			<fo:block line-height="0"><xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente)"><fo:external-graphic content-width="85pt" content-height="85pt" scaling="uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="/AvvisoPagamentoInput/logo_ente"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="458pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="20.26pt" padding-top="3.47pt"><xsl:value-of select="/AvvisoPagamentoInput/oggetto_del_pagamento"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="20.26pt" padding-top="3.47pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="393pt" top="20pt" width="85pt" height="85pt">
			<fo:block line-height="0"><xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)"><fo:external-graphic content-width="85pt" content-height="85pt" scaling="uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="/AvvisoPagamentoInput/logo_ente_secondario"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="109pt" width="297pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="8pt" end-indent="20pt" wrap-option="no-wrap">DESTINATARIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="140pt" width="277pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" line-height="17.72pt" start-indent="8pt" padding-top="3.04pt"><xsl:value-of select="/AvvisoPagamentoInput/nome_cognome_destinatario"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="176pt" width="274pt" height="40pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" line-height="15.19pt" padding-top="2.6pt"><xsl:value-of select="/AvvisoPagamentoInput/settore_ente"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="216pt" width="274pt" height="37pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" padding-top="1.74pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/info_ente"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="109pt" width="294pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="20pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="157pt" top="109pt" width="137pt" height="20pt" display-align="center">
			<fo:block font-family="RobotoMono" font-size="10pt" line-height="12.66pt" text-align="end" start-indent="8pt" end-indent="8pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="140pt" width="274pt" height="36pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" line-height="17.72pt" padding-top="3.04pt"><xsl:value-of select="/AvvisoPagamentoInput/ente_creditore"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="176pt" width="277pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" line-height="15.19pt" start-indent="8pt" padding-top="2.6pt"><xsl:value-of select="/AvvisoPagamentoInput/indirizzo_destinatario_1"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="404pt" top="109pt" width="171pt" height="20pt" display-align="center">
			<fo:block font-family="RobotoMono" font-size="10pt" line-height="12.66pt" text-align="end" start-indent="10pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_destinatario"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="196pt" width="277pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" line-height="15.19pt" start-indent="8pt" padding-top="2.6pt"><xsl:value-of select="/AvvisoPagamentoInput/indirizzo_destinatario_2"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="252pt" top="561pt" width="149pt" height="16pt" display-align="center">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" line-height="12.66pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="rata/codice_avviso"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="252pt" top="547pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" padding-top="1.74pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="538pt" top="351pt" width="35pt" height="35pt">
			<fo:block line-height="0"><fo:instream-foreign-object content-width="35pt" content-height="35pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 33.14 33.45'><title>canali-fisici</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path d='M24,6.47a12.68,12.68,0,0,1,1.06.88.67.67,0,0,0,.91-1,12.26,12.26,0,0,0-1.18-1A.67.67,0,1,0,24,6.47Z'/><path d='M5.73,14.34a3.52,3.52,0,1,0,3.51,3.52,3.52,3.52,0,0,0-3.51-3.52Zm0,5.7a2.18,2.18,0,1,1,2.18-2.18A2.19,2.19,0,0,1,5.73,20Z'/><path d='M20.1,5.73a3.51,3.51,0,1,0-3.51,3.51A3.51,3.51,0,0,0,20.1,5.73Zm-5.69,0a2.18,2.18,0,1,1,2.18,2.18,2.19,2.19,0,0,1-2.18-2.18Z'/><path d='M30.3,18.43a13.48,13.48,0,0,0,.13-1.9,13.75,13.75,0,0,0-2.83-8.4A.67.67,0,0,0,26.66,8a.66.66,0,0,0-.12.93,12.48,12.48,0,0,1,2.56,7.6c0,.45,0,.9-.07,1.35a5.82,5.82,0,0,0-1.62-.23,5.74,5.74,0,0,0-5.73,5.73A9.3,9.3,0,0,0,23,27.27a12.55,12.55,0,0,1-15.2-1.78c.32-.46.66-1,1-1.52,1.75-2.77,2.63-4.83,2.63-6.11a5.74,5.74,0,0,0-5.73-5.74,5.62,5.62,0,0,0-.92.08,12.58,12.58,0,0,1,6.06-6.83c0,.12,0,.24,0,.36,0,1.29.89,3.34,2.63,6.11,1.27,2,2.52,3.64,2.57,3.71a.67.67,0,0,0,1.06,0c.05-.07,1.3-1.7,2.57-3.71C21.43,9.07,22.32,7,22.32,5.73a5.73,5.73,0,0,0-11.1-2,13.93,13.93,0,0,0-8,9A5.74,5.74,0,0,0,0,17.86C0,19.14.89,21.2,2.64,24c1.26,2,2.51,3.63,2.56,3.7a.67.67,0,0,0,1.06,0s.33-.44.79-1.07A13.69,13.69,0,0,0,11,29.22a13.86,13.86,0,0,0,12.68-.79c.2.34.41.69.64,1.06,1.27,2,2.52,3.63,2.57,3.7a.67.67,0,0,0,1.06,0c0-.07,1.3-1.7,2.56-3.7,1.75-2.77,2.64-4.83,2.64-6.11a5.73,5.73,0,0,0-2.84-4.95ZM16.59,1.33A4.41,4.41,0,0,1,21,5.73c0,1.87-2.81,6.12-4.4,8.3-1.59-2.18-4.4-6.43-4.4-8.3a4.41,4.41,0,0,1,4.4-4.4ZM5.73,26.15c-1.59-2.18-4.4-6.42-4.4-8.29a4.4,4.4,0,0,1,8.8,0c0,1.87-2.81,6.11-4.4,8.29Zm21.68,5.52C25.82,29.5,23,25.25,23,23.38a4.4,4.4,0,0,1,8.8,0c0,1.87-2.81,6.12-4.4,8.29Z'/><path d='M27.41,19.86a3.52,3.52,0,1,0,3.51,3.52,3.52,3.52,0,0,0-3.51-3.52Zm0,5.7a2.18,2.18,0,1,1,2.18-2.18,2.19,2.19,0,0,1-2.18,2.18Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="401pt" top="561pt" width="174pt" height="16pt" display-align="center">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" line-height="12.66pt" text-align="end"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="419pt" width="296pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" line-height="12.66pt" text-align="end" start-indent="0pt" end-indent="20pt"><xsl:choose><xsl:when test="normalize-space(rata/numero_rata)"><xsl:value-of select="rata/numero_rata"/>° RATA</xsl:when><xsl:otherwise>Rata unica</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="normalize-space(rata/giorni)"><xsl:value-of select="concat(' ', rata/tipo, ' ')"/><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="rata/giorni"/></xsl:call-template></xsl:when><xsl:when test="normalize-space(rata/data)"> entro il <xsl:value-of select="rata/data"/></xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="419pt" width="299pt" height="20pt" background-color="#EBEBEB" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="20pt" wrap-option="no-wrap">DATI PER IL PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="9pt" top="445pt" width="105pt" height="105pt">
			<fo:block line-height="0"><fo:external-graphic content-width="105pt" content-height="105pt" scaling="uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_svg"/>)</xsl:attribute></fo:external-graphic></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="289pt" width="213pt" height="20pt" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="8pt" wrap-option="no-wrap">PAGA CON L'APP IO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="525pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="8pt" line-height="10.13pt" padding-top="1.74pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/></xsl:call-template></fo:block>
		</fo:block-container>
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true')">
			<fo:block-container absolute-position="absolute" left="20pt" top="352pt" width="20pt" height="20pt">
				<fo:block line-height="0"><fo:instream-foreign-object content-width="20pt" content-height="20pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' height='24px' viewBox='0 0 24 24' width='24px' fill='#000000'><path d='M0 0h24v24H0V0z' fill='none'/><path d='M11 7h2v2h-2zm0 4h2v6h-2zm1-9C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2zm0 18c-4.41 0-8-3.59-8-8s3.59-8 8-8 8 3.59 8 8-3.59 8-8 8z'/></svg></fo:instream-foreign-object></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="191pt" top="512pt" width="255pt" height="13pt" display-align="after">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="289pt" width="149pt" height="42pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="20.26pt" padding-top="1.74pt"><fo:block font-size="8pt" line-height="10.13pt">Importo</fo:block><fo:block space-before="7.2pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo"/></xsl:call-template> Euro</fo:block></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="547pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" padding-top="1.74pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="452pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" padding-top="1.74pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="465pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="8pt" line-height="10.13pt" start-indent="0pt" padding-top="1.74pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="495pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="8pt" line-height="10.13pt" start-indent="0pt" padding-top="1.74pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="386pt" top="452pt" width="190pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" line-height="17.72pt" text-align="end" padding-top="3.04pt"><fo:inline font-size="10pt">Euro</fo:inline>&#160;&#160;<fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="561pt" width="60pt" height="16pt" display-align="center">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" line-height="12.66pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/></xsl:call-template></fo:block>
		</fo:block-container>
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true') and not(normalize-space(/AvvisoPagamentoInput/informativa_importo_avviso))">
			<fo:block-container absolute-position="absolute" left="54pt" top="351pt" width="232pt" height="32pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" start-indent="0pt" padding-top="1.74pt" wrap-option="no-wrap">In fase di pagamento, se previsto dall’ente, l'importo potrebbe essere <fo:block/>aggiornato automaticamente e subire variazioni in diminuzione<fo:block/>(per sgravi, note di credito) o in aumento (per sanzioni, interessi, ecc.).</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="299pt" top="349pt" width="213pt" height="20pt" display-align="center">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" line-height="15.19pt" start-indent="8pt" wrap-option="no-wrap">PAGA SUL TERRITORIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="538pt" top="291pt" width="35pt" height="35pt">
			<fo:block line-height="0"><fo:instream-foreign-object content-width="35pt" content-height="35pt" scaling="uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 33.31 31.09'><defs><style>.cls-1{fill:#1d1d1b;}</style></defs><title>canali-digitali</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path class='cls-1' d='M31.65,15V2.78A2.79,2.79,0,0,0,28.87,0H4.44A2.78,2.78,0,0,0,1.67,2.78V11.1A1.67,1.67,0,0,0,0,12.77V29.43a1.67,1.67,0,0,0,1.67,1.66H13.88a1.67,1.67,0,0,0,1.67-1.66H20.2a1.65,1.65,0,0,0,1.32-.66,1.67,1.67,0,0,0,.29-1.45l-1.1-4h3.16v6.11a1.67,1.67,0,0,0,1.67,1.66h6.11a1.67,1.67,0,0,0,1.66-1.66V16.66A1.67,1.67,0,0,0,31.65,15Zm.55,1.67v1.11H25V16.66a.56.56,0,0,1,.56-.56h6.11A.56.56,0,0,1,32.2,16.66ZM25,18.88H32.2V27.2H25ZM2.78,2.78A1.66,1.66,0,0,1,4.44,1.11H28.87a1.67,1.67,0,0,1,1.67,1.67V15h-5a1.67,1.67,0,0,0-1.67,1.67v1.11H15.55v-5a1.67,1.67,0,0,0-1.67-1.67H2.78ZM14.44,27.2H1.11V15H14.44Zm-12.77-15H13.88a.56.56,0,0,1,.56.56v1.11H1.11V12.77A.56.56,0,0,1,1.67,12.21ZM13.88,30H1.67a.56.56,0,0,1-.56-.55V28.32H14.44v1.11A.56.56,0,0,1,13.88,30Zm6.76-1.88a.56.56,0,0,1-.44.22H15.55V27.2h5.07l.11.41A.54.54,0,0,1,20.64,28.1Zm-.32-2H15.55V23.32h4Zm-4.77-3.88V18.88h8.32v3.33ZM31.65,30H25.54a.56.56,0,0,1-.56-.55V28.32H32.2v1.11A.55.55,0,0,1,31.65,30Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="547pt" width="136pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" start-indent="0pt" padding-top="1.74pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="482pt" width="255pt" height="13pt" display-align="after">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="289pt" width="103pt" height="42pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="20.26pt" padding-top="1.74pt"><xsl:choose><xsl:when test="normalize-space(rata/giorni)"><fo:block font-size="8pt" line-height="10.13pt"><xsl:value-of select="rata/tipo"/></fo:block><fo:block space-before="7.2pt"><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="rata/giorni"/></xsl:call-template></fo:block></xsl:when><xsl:when test="normalize-space(rata/data)"><fo:block font-size="8pt" line-height="10.13pt">entro il</fo:block><fo:block space-before="7.2pt"><xsl:value-of select="rata/data"/></fo:block></xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="401pt" top="547pt" width="174pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" text-align="end" padding-top="1.74pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="308pt" width="213pt" height="41pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" start-indent="8pt" padding-top="1.74pt"><xsl:if test="normalize-space(/AvvisoPagamentoInput/del_tuo_ente)"><xsl:value-of select="concat('oppure sul sito ', /AvvisoPagamentoInput/del_tuo_ente, ', ')"/></xsl:if>dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="368pt" width="213pt" height="37pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" start-indent="8pt" padding-top="1.74pt" wrap-option="no-wrap">presso Banche e Sportelli ATM, negli Uffici Postali e Punti <fo:block/>Postali, nei Bar, Edicole, Ricevitorie, Supermercati, <fo:block/>Tabaccherie e altri Esercenti Convenzionati.</fo:block>
		</fo:block-container>
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true') and normalize-space(/AvvisoPagamentoInput/informativa_importo_avviso)">
			<fo:block-container absolute-position="absolute" left="54pt" top="351pt" width="232pt" height="40pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="10.13pt" padding-top="1.74pt"><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/informativa_importo_avviso"/></xsl:call-template></fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
