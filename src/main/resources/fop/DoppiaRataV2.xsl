<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO DoppiaRataV2 (issue #37): pagina dell'avviso bilingue con due rate (da avviso-bilingue.xsl).
	Derivato dal template Jasper DoppiaRataV2.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/doppia; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoFop" exclude-result-prefixes="tj">

	<xsl:template name="DoppiaRataV2">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="allungamento-doppio-formato-v2" select="(ceiling(count(rata) div 4) * 211.0 - 212) * number((ceiling(count(rata) div 4) * 211.0 - 212) &gt; 0)"/>
		<xsl:variable name="allungamento-bollettino-rata-v2" select="(ceiling(count(rata) div 1) * 215.0 - 20) * number((ceiling(count(rata) div 1) * 215.0 - 20) &gt; 0)"/>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="34pt" content-height="34pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-pagopa-nero.svg)"/></fo:block>
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
		<xsl:variable name="spostamento-bollettino-rata-v2" select="$allungamento-doppio-formato-v2"/>
		<xsl:for-each select="rata">
			<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 1) * 595.0}pt" top="{389.0 + $spostamento-bollettino-rata-v2 + floor((position() - 1) div 1) * 215.0}pt" width="595pt" height="215pt">
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
		<fo:block-container absolute-position="absolute" left="20pt" top="{361 + $allungamento-doppio-formato-v2}pt" width="555pt" height="24pt">
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
