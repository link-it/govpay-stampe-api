<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/DoppiaRataV2.jrxml: non modificare a mano.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="DoppiaRataV2">
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="allungamento-6f98a22c" select="(ceiling(count(rata) div 4) * 211.0 - 212) * number((ceiling(count(rata) div 4) * 211.0 - 212) &gt; 0)"/>
		<xsl:variable name="allungamento-5cb760ca" select="(ceiling(count(rata) div 1) * 215.0 - 20) * number((ceiling(count(rata) div 1) * 215.0 - 20) &gt; 0)"/>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:instream-foreign-object content-width="34pt" content-height="34pt" scaling="non-uniform"><svg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 34.67 22.99'><defs><style>.cls-1{fill:#1d1d1b;}</style></defs><title>logo-pagopa</title><g id='Layer_2' data-name='Layer 2'><g id='Layer_1-2' data-name='Layer 1'><path class='cls-1' d='M24.43,6.89v2.9H22.3V0h3.92c1.26,0,1.9.34,1.91,1V5.79c0,.68-.62,1-1.85,1H24.43ZM26,1.13H24.42V5.67H26Z'/><path class='cls-1' d='M1.78,9.8v2.91H0V2.91H3.28c1.06,0,1.59.34,1.6,1V8.71c0,.67-.52,1-1.54,1H1.78ZM3.11,4.05H1.78V8.59H3.11Z'/><path class='cls-1' d='M10.36,9.72H7c-1,0-1.49-.34-1.49-1v-2C5.5,6,6,5.7,7,5.7H8.59V4.05H7.26V5H5.51v-1c0-.67.52-1,1.57-1l1.73,0q1.55,0,1.56,1ZM7.26,8.59H8.6V6.84H7.26Z'/><path class='cls-1' d='M14.24,9.72H12.68c-1,0-1.54-.34-1.54-1V3.93c0-.68.54-1,1.6-1H16v8.78c0,.68-.52,1-1.55,1H12.69c-1,0-1.55-.34-1.55-1v-1h1.77v.89h1.33V9.72ZM12.91,8.59h1.33V4.05H12.91Z'/><path class='cls-1' d='M21.5,8.71c0,.67-.53,1-1.59,1H18.19c-1,0-1.56-.34-1.56-1V3.93q0-1,1.56-1H20c1,0,1.54.34,1.54,1ZM19.74,4.05H18.4V8.59h1.33Z'/><path class='cls-1' d='M34.67,1.07c0-.68-.62-1-1.85-1H30.7q-1.86,0-1.86,1V9.81H31v-3h1.6v2.5a11.58,11.58,0,0,1-20.71,7.11l1.06-.81L8.66,12.76l.19,6,1.35-1.09A13.68,13.68,0,0,0,34.67,9.31ZM32.56,5.69H31V1.19h1.6Z'/></g></g></svg></fo:instream-foreign-object></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="59pt" top="24pt" width="311pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/avviso_pagamento, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="10"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="311"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
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
		<fo:block-container absolute-position="absolute" left="20pt" top="174pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="tronca" select="true()"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="334pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="286pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_cbill"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_cbill"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_cbill))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_cbill"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_cbill"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="159pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/destinatario"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/destinatario"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/destinatario))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/destinatario"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/destinatario"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="234pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/oggetto"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="191pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/ente_creditore"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/ente_creditore"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/ente_creditore))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="318pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="302pt" width="260pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="249pt" width="260pt" height="29pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="29"/>
					<xsl:with-param name="maxRighe" select="2"/>
					<xsl:with-param name="tronca" select="true()"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="206pt" width="260pt" height="27pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="260"/>
					<xsl:with-param name="altezza" select="27"/>
					<xsl:with-param name="maxRighe" select="2"/>
					<xsl:with-param name="tronca" select="true()"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{280.0 + ((position() - 1) mod 4) * 148.0}pt" top="{139.0 + floor((position() - 1) div 4) * 211.0}pt" width="148pt" height="211pt">
				<fo:block/>
				<xsl:call-template name="DoppioFormatoV2">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
		<xsl:variable name="spostamento-5cb760ca" select="$allungamento-6f98a22c"/>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 1) * 595.0}pt" top="{389.0 + $spostamento-5cb760ca + floor((position() - 1) div 1) * 215.0}pt" width="595pt" height="215pt">
				<fo:block/>
				<xsl:call-template name="BollettinoRataV2">
				</xsl:call-template>
			</fo:block-container>
		</xsl:for-each>
		<fo:block-container absolute-position="absolute" left="0pt" top="121pt" width="594pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/canali, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/canali))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/canali"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/canali"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="554"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="393pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="59pt" width="458pt" height="55pt">
				<fo:block font-family="TitilliumWeb" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/elenco_rate))"></xsl:when><xsl:otherwise><xsl:value-of select="rata/elenco_rate"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/elenco_rate)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))))"><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"><xsl:choose><xsl:when test="not(normalize-space(rata/elenco_rate_tra))"><xsl:text> - </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/elenco_rate_tra"><xsl:value-of select="rata/elenco_rate_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:text> - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(normalize-space(rata/elenco_rate_tra))) or (not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="rata/elenco_rate_tra"><xsl:value-of select="rata/elenco_rate_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/elenco_rate_tra)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; - &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="458"/>
						<xsl:with-param name="altezza" select="55"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="20pt" top="{361 + $allungamento-6f98a22c}pt" width="555pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="6"/>
					<xsl:with-param name="larghezza" select="555"/>
					<xsl:with-param name="altezza" select="24"/>
					<xsl:with-param name="maxRighe" select="2"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="59pt" width="350pt" height="55pt">
				<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/elenco_rate))"></xsl:when><xsl:otherwise><xsl:value-of select="rata/elenco_rate"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/elenco_rate)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))))"><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"><xsl:choose><xsl:when test="not(normalize-space(rata/elenco_rate_tra))"><xsl:text> - </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/elenco_rate_tra"><xsl:value-of select="rata/elenco_rate_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:text> - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(normalize-space(rata/elenco_rate_tra))) or (not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="rata/elenco_rate_tra"><xsl:value-of select="rata/elenco_rate_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/elenco_rate_tra)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; - &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="14"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="55"/>
						<xsl:with-param name="maxRighe" select="3"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
