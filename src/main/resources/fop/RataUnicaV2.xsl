<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO RataUnicaV2 (issue #37): pagina dell'avviso bilingue con la rata unica, con o senza bollettino postale (da avviso-bilingue.xsl).
	Derivato dal template Jasper RataUnicaV2.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/singola; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoFop" exclude-result-prefixes="tj">

	<xsl:template name="RataUnicaV2">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="senza-nome-cognome-destinatario-e-altri" select="20 * number((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))))"/>
		<xsl:variable name="senza-settore-ente-e-altri" select="30 * number((not(normalize-space(/AvvisoPagamentoInput/settore_ente))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_1))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_2))))"/>
		<xsl:variable name="senza-info-ente" select="34 * number(not(normalize-space(/AvvisoPagamentoInput/info_ente)))"/>
		<xsl:variable name="allungamento-cf-ente-testo"><xsl:variable name="valore"><xsl:value-of select="/AvvisoPagamentoInput/cf_ente"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'TitilliumWeb', false(), false(), 9, '', 64, 0, false())"/></xsl:variable><xsl:variable name="allungamento-cf-ente" select="tj:allungamento(string($allungamento-cf-ente-testo), 'TitilliumWeb', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-cf-destinatario-testo"><xsl:variable name="valore"><xsl:value-of select="/AvvisoPagamentoInput/cf_destinatario"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'TitilliumWeb', false(), false(), 9, '', 105, 0, false())"/></xsl:variable><xsl:variable name="allungamento-cf-destinatario" select="tj:allungamento(string($allungamento-cf-destinatario-testo), 'TitilliumWeb', 9, 0, 0, 18)"/>
		<xsl:variable name="allungamento-nome-cognome-destinatario" select="tj:allungamento(tj:righe(string(/AvvisoPagamentoInput/nome_cognome_destinatario), 'TitilliumWeb-Bold', false(), false(), 10, 271, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-ente-creditore" select="tj:allungamento(tj:righe(string(/AvvisoPagamentoInput/ente_creditore), 'TitilliumWeb-Bold', false(), false(), 10, 274, 0, false()), 'TitilliumWeb-Bold', 10, 7, 0, 20)"/>
		<xsl:variable name="allungamento-cf-ente-e-affiancati" select="(number($allungamento-cf-ente &gt; $allungamento-cf-destinatario) * $allungamento-cf-ente + number(not($allungamento-cf-ente &gt; $allungamento-cf-destinatario)) * $allungamento-cf-destinatario)"/>
		<xsl:variable name="allungamento-nome-cognome-destinatario-e-affiancati" select="(number($allungamento-nome-cognome-destinatario &gt; $allungamento-ente-creditore) * $allungamento-nome-cognome-destinatario + number(not($allungamento-nome-cognome-destinatario &gt; $allungamento-ente-creditore)) * $allungamento-ente-creditore)"/>
		<xsl:if test="(normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_1)) and (not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_2)))">
			<fo:block-container absolute-position="absolute" left="43pt" top="{296 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="250pt" height="100pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota_1"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota_1"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota_1))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota_1"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota_1"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="250"/>
						<xsl:with-param name="altezza" select="100"/>
						<xsl:with-param name="maxRighe" select="9"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(normalize-space(rata/qr_code))) or (not(normalize-space(/AvvisoPagamentoInput/poste)))">
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="543pt" top="{599 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="30pt" height="30pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="30pt" height="23.182pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="30pt" content-height="23.182pt" scaling="uniform" src="url(classpath:/fop/icone/forbici.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="20pt" top="21pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="34pt" content-height="34pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-pagopa-nero.svg)"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="59pt" top="25pt" width="311pt" height="16pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" color="#000000" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/avviso_pagamento, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="10"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/avviso_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="colore" select="'#000000'"/>
					<xsl:with-param name="larghezza" select="311"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="11pt" top="{451 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="88pt" height="88pt">
				<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_svg)"><fo:external-graphic content-width="88pt" content-height="88pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="490pt" top="21pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/logo_ente_secondario))">
			<fo:block-container absolute-position="absolute" left="20pt" top="59pt" width="458pt" height="42pt">
				<fo:block font-family="TitilliumWeb" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/numero_rata))"></xsl:when><xsl:otherwise><xsl:value-of select="rata/numero_rata"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/numero_rata)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))))"><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"><xsl:choose><xsl:when test="not(normalize-space(rata/numero_rata_tra))"><xsl:text> - </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/numero_rata_tra"><xsl:value-of select="rata/numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:text> - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(normalize-space(rata/numero_rata_tra))) or (not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="rata/numero_rata_tra"><xsl:value-of select="rata/numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/numero_rata_tra)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; - &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
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
		<fo:block-container absolute-position="absolute" left="20pt" top="{266 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="115pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="15pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="format-number(number(rata/importo), '#.##0,00', 'it')"/><xsl:text> Euro</xsl:text></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="15"/>
					<xsl:with-param name="larghezza" select="115"/>
					<xsl:with-param name="altezza" select="24"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="295pt" top="{248 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="278pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/paga_app, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/paga_app))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/paga_app"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/paga_app"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="270"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="111pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/ente_creditore, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/ente_creditore))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="276"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="296pt" top="111pt" width="299pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/destinatario_avviso, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/destinatario_avviso))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/destinatario_avviso"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/destinatario_avviso"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="271"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="220pt" top="111pt" width="74pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" start-indent="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="larghezza" select="64"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
					<xsl:with-param name="limiteRighe" select="5"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="470pt" top="111pt" width="105pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_destinatario"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="larghezza" select="105"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
					<xsl:with-param name="limiteRighe" select="5"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="295pt" top="{266 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="226pt" height="74pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/paga_app2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/paga_app2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/paga_app2))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/paga_app2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/paga_app2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="218"/>
					<xsl:with-param name="altezza" select="74"/>
					<xsl:with-param name="maxRighe" select="6"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="296pt" top="{341 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="278pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/paga_territorio, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/paga_territorio))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/paga_territorio"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/paga_territorio"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="270"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="296pt" top="{359 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="226pt" height="68pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/paga_territorio2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/paga_territorio2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/paga_territorio2))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/paga_territorio2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/paga_territorio2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="9"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="218"/>
					<xsl:with-param name="altezza" select="68"/>
					<xsl:with-param name="maxRighe" select="6"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="0pt" top="{433 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="296pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/canali, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/canali))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/canali"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/canali"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="256"/>
						<xsl:with-param name="altezza" select="18"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="295pt" top="{433 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="299pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="8pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/qr_code))"></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="not(normalize-space(rata/data))"><xsl:choose><xsl:when test="rata/scadenza"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(rata/scadenza_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/scadenza_tra"><xsl:value-of select="rata/scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="rata/scadenza"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(rata/scadenza_tra))"><xsl:text> </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/scadenza_tra"><xsl:value-of select="rata/scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; </xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="rata/data"><xsl:value-of select="rata/data"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="271"/>
						<xsl:with-param name="altezza" select="18"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{539 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="140pt" height="53pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb" size="9"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="6"/>
						<xsl:with-param name="larghezza" select="140"/>
						<xsl:with-param name="altezza" select="53"/>
						<xsl:with-param name="maxRighe" select="5"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{475 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="306pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="306"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="478pt" top="{460 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="97pt" height="29pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:text>Euro &lt;style isBold="true"&gt;</xsl:text><xsl:value-of select="format-number(number(rata/importo), '#.##0,00', 'it')"/><xsl:text>&lt;/style&gt;</xsl:text></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="97"/>
						<xsl:with-param name="altezza" select="29"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{460 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="305pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/destinatario"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/destinatario"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/destinatario))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/destinatario"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/destinatario"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="305"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{539 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="405pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="405"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="169pt" top="{561 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="108pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_cbill"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_cbill"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_cbill))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_cbill"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_cbill"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="108"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="169pt" top="{575 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="59pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="59"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="277pt" top="{575 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="128pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="rata/codice_avviso"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="128"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="277pt" top="{561 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="138pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_avviso))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_avviso"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_avviso"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="138"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="415pt" top="{561 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="160pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/codice_fiscale_ente"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="160"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="415pt" top="{575 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="160pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="160"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="0pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="575pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">BOLLETTINO POSTALE PA</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="0pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="575pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="199pt" top="{648 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="216pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="justify" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:text>sul C/C n. &lt;style isBold="true"&gt;</xsl:text><xsl:choose><xsl:when test="rata/numero_cc_postale"><xsl:value-of select="rata/numero_cc_postale"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;</xsl:text></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="216"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="478pt" top="{648 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="97pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:text>Euro &lt;style isBold="true"&gt;</xsl:text><xsl:value-of select="format-number(number(rata/importo), '#.##0,00', 'it')"/><xsl:text>&lt;/style&gt;</xsl:text></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="97"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Bottom'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="220pt" top="{701 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="262pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="262"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="170pt" top="{683 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="50pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/intestatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="50"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Bottom'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="170pt" top="{701 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="50pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="50"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Bottom'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="220pt" top="{683 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="262pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/intestatario_conto_corrente_postale"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="262"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="538pt" top="{284 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-digitali.svg)"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="538pt" top="{363 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-fisici.svg)"/></fo:block>
		</fo:block-container>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="345pt" top="{781 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="137pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="137"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="169pt" top="{767 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="140pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="140"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="169pt" top="{781 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="140pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="rata/codice_avviso_postale"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="140"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="310pt" top="{767 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="33pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="center" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/tipo"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="33"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="310pt" top="{781 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="33pt" height="16pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" text-align="center" line-height="15.21pt" padding-top="1.255pt" wrap-option="no-wrap">P1</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{727 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="137pt" height="48pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="10.541pt" padding-top="2.703pt" wrap-option="no-wrap">Bollettino Postale pagabile in tutti <fo:block/>gli Uffici Postali e sui canali fisici o <fo:block/>digitali abilitati di Poste Italiane e <fo:block/>dell’Ente Creditore</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{785 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="137pt" height="10pt">
				<fo:block font-family="TitilliumWeb" font-size="6pt" color="#CCCCCC" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="rata/autorizzazione"/>
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
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{649 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-poste-italiane.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="495pt" top="{713 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="80pt" height="80pt">
				<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/data_matrix_svg)"><fo:external-graphic content-width="80pt" content-height="80pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/data_matrix_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="170pt" top="{645 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="24pt" height="24pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="24pt" content-height="24pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-euro-bollettino.svg)"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{224 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="296pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/quanto_quando, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/quanto_quando))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/quanto_quando"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/quanto_quando"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="276"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="294pt" top="{224 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="300pt" height="18pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:value-of select="translate(/AvvisoPagamentoInput/etichette/italiano/come, 'abcdefghijklmnopqrstuvwxyzàèéìòùäöüčšž', 'ABCDEFGHIJKLMNOPQRSTUVWXYZÀÈÉÌÒÙÄÖÜČŠŽ')"/><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/come))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/come"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/come"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="272"/>
					<xsl:with-param name="altezza" select="18"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="490pt" top="{224 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="85pt" height="18pt">
			<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" start-indent="0pt" line-height="13.689pt" padding-top="3.285pt" wrap-option="no-wrap"><fo:inline font-weight="bold">www.pagopa.gov.it</fo:inline></fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{507 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="405pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="405"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{493 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="305pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/ente_creditore"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/ente_creditore"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/ente_creditore))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/ente_creditore"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="305"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not(not(normalize-space(/AvvisoPagamentoInput/info_ente)))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{186 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="275pt" height="34pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/info_ente"/>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="6"/>
						<xsl:with-param name="larghezza" select="275"/>
						<xsl:with-param name="altezza" select="34"/>
						<xsl:with-param name="maxRighe" select="3"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/settore_ente))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_1))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{156 - $senza-nome-cognome-destinatario-e-altri + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="274pt" height="30pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/settore_ente"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="274"/>
						<xsl:with-param name="altezza" select="30"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/settore_ente))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_1))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="296pt" top="{171 - $senza-nome-cognome-destinatario-e-altri + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="279pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/indirizzo_destinatario_2"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="271"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/settore_ente))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_1))) and (not(normalize-space(/AvvisoPagamentoInput/indirizzo_destinatario_2))))">
			<fo:block-container absolute-position="absolute" left="296pt" top="{156 - $senza-nome-cognome-destinatario-e-altri + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="279pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/indirizzo_destinatario_1"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="271"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))))">
			<fo:block-container absolute-position="absolute" left="296pt" top="{134 + $allungamento-cf-ente-e-affiancati}pt" width="279pt" height="20pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="271"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="limiteRighe" select="5"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{134 + $allungamento-cf-ente-e-affiancati}pt" width="274pt" height="20pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="274"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="limiteRighe" select="5"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(rata/qr_code)">
			<fo:block-container absolute-position="absolute" left="170pt" top="{525 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="305pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/oggetto"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="305"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="170pt" top="{720 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="50pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/descrizione"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="50"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Bottom'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="220pt" top="{720 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="262pt" height="40pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="262"/>
						<xsl:with-param name="altezza" select="40"/>
						<xsl:with-param name="maxRighe" select="3"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="{656 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-bollettino-postale.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="170pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="403pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">Banco<fo:inline font-weight="bold">Posta</fo:inline></fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="170pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="403pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="295pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="299pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/qr_code))"></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="not(normalize-space(rata/data))"><xsl:choose><xsl:when test="rata/scadenza"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(rata/scadenza_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/scadenza_tra"><xsl:value-of select="rata/scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="rata/scadenza"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(rata/scadenza_tra))"><xsl:text> </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/scadenza_tra"><xsl:value-of select="rata/scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; </xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="rata/data"><xsl:value-of select="rata/data"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="279"/>
						<xsl:with-param name="altezza" select="18"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="295pt" top="{613 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="299pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="140pt" top="{248 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="154pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/qr_code))"><xsl:choose><xsl:when test="not(normalize-space(rata/data))"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/prima_rata"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/prima_rata"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/prima_rata))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/prima_rata"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/prima_rata"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="not(normalize-space(rata/data))"><xsl:value-of select="rata/scadenza"/></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/entro"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/entro"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/entro))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/entro"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/entro"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="154"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{248 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="115pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/importo"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/importo"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/importo))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/importo"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/importo"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="115"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="140pt" top="{267 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="154pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="15pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="rata/data"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="15"/>
					<xsl:with-param name="larghezza" select="154"/>
					<xsl:with-param name="altezza" select="24"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="(not(not(normalize-space(rata/qr_code)))) and (not(not(normalize-space(/AvvisoPagamentoInput/poste))))">
			<fo:block-container absolute-position="absolute" left="345pt" top="{767 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="137pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="137"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="393pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="59pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/numero_rata))"></xsl:when><xsl:otherwise><xsl:value-of select="rata/numero_rata"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/numero_rata)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))))"><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"><xsl:choose><xsl:when test="not(normalize-space(rata/numero_rata_tra))"><xsl:text> - </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="rata/numero_rata_tra"><xsl:value-of select="rata/numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:text> - </xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/oggetto_del_pagamento"/></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(normalize-space(rata/numero_rata_tra))) or (not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento)))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="rata/numero_rata_tra"><xsl:value-of select="rata/numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="(not(not(normalize-space(rata/numero_rata_tra)))) and (not(not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))))"><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; - &lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/oggetto_del_pagamento"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
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
		<fo:block-container absolute-position="absolute" left="140pt" top="{268 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="154pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(rata/data))"><xsl:choose><xsl:when test="not(normalize-space(rata/scadenza_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt;</xsl:text><xsl:choose><xsl:when test="rata/scadenza_tra"><xsl:value-of select="rata/scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise></xsl:otherwise></xsl:choose></xsl:with-param>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="154"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="(normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_1)) and (normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_2))">
			<fo:block-container absolute-position="absolute" left="43pt" top="{296 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="250pt" height="34pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota_1"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota_1"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota_1))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota_1"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota_1"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="250"/>
						<xsl:with-param name="altezza" select="34"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_1)) and (normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_2)) and (normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_3))">
			<fo:block-container absolute-position="absolute" left="43pt" top="{397 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="250pt" height="30pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota_3"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota_3"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota_3))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota_3"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota_3"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="250"/>
						<xsl:with-param name="altezza" select="30"/>
						<xsl:with-param name="maxRighe" select="2"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_1)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{297 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="16pt" height="16pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="16pt" content-height="16pt" scaling="uniform" src="url(classpath:/fop/icone/informazioni-bilingue.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_3)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{398 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="16pt" height="16pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="16pt" content-height="16pt" scaling="uniform" src="url(classpath:/fop/icone/informazioni-bilingue.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="(normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_1)) and (normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_2))">
			<fo:block-container absolute-position="absolute" left="43pt" top="{331 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="250pt" height="65pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/italiano/nota_2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/nota_2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;br&gt;</xsl:text><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/nota_2))"></xsl:when><xsl:otherwise><xsl:text>&lt;span style="color: #999999;"&gt;</xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/nota_2"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/nota_2"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/span&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="250"/>
						<xsl:with-param name="altezza" select="65"/>
						<xsl:with-param name="maxRighe" select="5"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/etichette/italiano/nota_2)">
			<fo:block-container absolute-position="absolute" left="20pt" top="{332 - $senza-nome-cognome-destinatario-e-altri - $senza-settore-ente-e-altri - $senza-info-ente + $allungamento-cf-ente-e-affiancati + $allungamento-nome-cognome-destinatario-e-affiancati}pt" width="16pt" height="16pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="16pt" height="16pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="16pt" content-height="16pt" scaling="uniform" src="url(classpath:/fop/icone/informazioni-bilingue.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
