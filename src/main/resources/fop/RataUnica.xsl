<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/RataUnica.jrxml: non modificare a mano.
	Le parti scritte a mano sono in script/fop/personalizzazioni/RataUnica.xml.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="RataUnica">
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="allungamento-c4d1c1c7" select="tj:allungamento(tj:righe(string(/AvvisoPagamentoInput/nome_cognome_destinatario), 'TitilliumWeb-Bold', false(), false(), 14, 269, 0, false()), 'TitilliumWeb-Bold', 14, 7, 0, 22)"/>
		<xsl:variable name="allungamento-1916de52-testo"><xsl:variable name="valore"><xsl:value-of select="/AvvisoPagamentoInput/cf_ente"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 10, '', 121, 0, false())"/></xsl:variable><xsl:variable name="allungamento-1916de52" select="tj:allungamento(string($allungamento-1916de52-testo), 'RobotoMono', 10, 0, 0, 20)"/>
		<xsl:variable name="allungamento-e7d6ecaa" select="tj:allungamento(tj:righe(string(/AvvisoPagamentoInput/ente_creditore), 'TitilliumWeb-Bold', false(), false(), 14, 274, 0, false()), 'TitilliumWeb-Bold', 14, 7, 0, 36)"/>
		<xsl:variable name="allungamento-f78dda86-testo"><xsl:variable name="valore"><xsl:value-of select="/AvvisoPagamentoInput/cf_destinatario"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'html', 'RobotoMono', false(), false(), 10, '', 161, 0, false())"/></xsl:variable><xsl:variable name="allungamento-f78dda86" select="tj:allungamento(string($allungamento-f78dda86-testo), 'RobotoMono', 10, 0, 0, 20)"/>
		<xsl:variable name="allungamento-1916de52-gruppo" select="(number($allungamento-1916de52 &gt; $allungamento-f78dda86) * $allungamento-1916de52 + number(not($allungamento-1916de52 &gt; $allungamento-f78dda86)) * $allungamento-f78dda86)"/>
		<xsl:variable name="allungamento-c4d1c1c7-gruppo" select="(number($allungamento-c4d1c1c7 &gt; $allungamento-e7d6ecaa) * $allungamento-c4d1c1c7 + number(not($allungamento-c4d1c1c7 &gt; $allungamento-e7d6ecaa)) * $allungamento-e7d6ecaa)"/>
		<fo:block-container absolute-position="absolute" left="299pt" top="{253 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="296pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="8pt" end-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">DOVE PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="403pt" top="{253 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="173pt" height="20pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" text-align="end" start-indent="0pt" line-height="15.21pt" padding-top="3.65pt" wrap-option="no-wrap">Vai su <fo:inline font-weight="bold">pagopa.gov.it</fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{253 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="294pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">QUANTO E QUANDO PAGARE?</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="20pt" width="34pt" height="34pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="34pt" content-height="34pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-pagopa.svg)"/></fo:block>
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
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
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
		<fo:block-container absolute-position="absolute" left="299pt" top="109pt" width="297pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="8pt" end-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">DESTINATARIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{140 + $allungamento-1916de52-gruppo}pt" width="277pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="269"/>
					<xsl:with-param name="altezza" select="22"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{176 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="274pt" height="40pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/settore_ente"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="12"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="274"/>
					<xsl:with-param name="altezza" select="40"/>
					<xsl:with-param name="maxRighe" select="2"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{216 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="274pt" height="37pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt"><xsl:variable name="righe" select="1 + string-length(/AvvisoPagamentoInput/info_ente) - string-length(translate(/AvvisoPagamentoInput/info_ente, '&lt;', ''))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 8, 6)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 8, 6, 'Top', 37, 0)"/></xsl:attribute><xsl:call-template name="markup"><xsl:with-param name="testo" select="/AvvisoPagamentoInput/info_ente"/></xsl:call-template></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="109pt" width="294pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="157pt" top="109pt" width="137pt" height="20pt">
			<fo:block font-family="RobotoMono" font-size="10pt" text-align="end" start-indent="8pt" end-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="121"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{140 + $allungamento-1916de52-gruppo}pt" width="274pt" height="36pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="274"/>
					<xsl:with-param name="altezza" select="36"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{176 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="277pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/indirizzo_destinatario_1"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="12"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="269"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="404pt" top="109pt" width="171pt" height="20pt">
			<fo:block font-family="RobotoMono" font-size="10pt" text-align="end" start-indent="10pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_destinatario"/>
					<xsl:with-param name="markup" select="'html'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="161"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{196 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="277pt" height="20pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/indirizzo_destinatario_2"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="12"/>
					<xsl:with-param name="interlinea" select="7"/>
					<xsl:with-param name="larghezza" select="269"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="252pt" top="{561 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="149pt" height="16pt">
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
		<fo:block-container absolute-position="absolute" left="252pt" top="{547 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="149pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. Avviso</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="538pt" top="{351 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-fisici.svg)"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="401pt" top="{561 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="174pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'RobotoMono'"/>
					<xsl:with-param name="grassetto" select="true()"/>
					<xsl:with-param name="corpo" select="10"/>
					<xsl:with-param name="larghezza" select="174"/>
					<xsl:with-param name="altezza" select="16"/>
					<xsl:with-param name="maxRighe" select="1"/>
					<xsl:with-param name="allineamento" select="'Middle'"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{419 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="296pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" line-height="15.21pt" padding-top="3.65pt"><xsl:choose><xsl:when test="normalize-space(rata/numero_rata)"><xsl:value-of select="rata/numero_rata"/>° RATA</xsl:when><xsl:otherwise>Rata unica</xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="normalize-space(rata/giorni)"><xsl:value-of select="concat(' ', rata/tipo, ' ')"/><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="rata/giorni"/></xsl:call-template></xsl:when><xsl:when test="normalize-space(rata/data)"> entro il <xsl:value-of select="rata/data"/></xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{419 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="299pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">DATI PER IL PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="9pt" top="{445 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="105pt" height="105pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(rata/qr_code_svg)"><fo:external-graphic content-width="105pt" content-height="105pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="rata/qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{289 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="213pt" height="20pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="8pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">PAGA CON L'APP IO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{525 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
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
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true')">
			<fo:block-container absolute-position="absolute" left="20pt" top="{352 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="20pt" height="20pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="20pt" height="20pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="20pt" content-height="20pt" scaling="uniform" src="url(classpath:/fop/icone/informazioni.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="191pt" top="{512 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{289 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="149pt" height="42pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="24.336pt" padding-top="2.023pt"><fo:block font-size="8pt" line-height="10.13pt">Importo</fo:block><fo:block space-before="5.057pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo"/></xsl:call-template> Euro</fo:block></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{547 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="60pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{452 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{465 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
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
		<fo:block-container absolute-position="absolute" left="191pt" top="{495 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
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
		<fo:block-container absolute-position="absolute" left="386pt" top="{452 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="190pt" height="24pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" text-align="end" line-height="21.294pt" padding-top="1.277pt" white-space-collapse="false"><fo:inline font-size="10pt">Euro</fo:inline><xsl:text>  </xsl:text><fo:inline font-weight="bold" font-family="RobotoMono"><xsl:call-template name="importo"><xsl:with-param name="valore" select="rata/importo"/></xsl:call-template></fo:inline></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{561 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="60pt" height="16pt">
			<fo:block font-family="RobotoMono" font-size="10pt" font-weight="bold" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/>
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
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true') and not(normalize-space(/AvvisoPagamentoInput/informativa_importo_avviso))">
			<fo:block-container absolute-position="absolute" left="54pt" top="{351 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="232pt" height="32pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="10.125pt" padding-top="2.025pt" wrap-option="no-wrap">In fase di pagamento, se previsto dall’ente, l'importo potrebbe essere <fo:block/>aggiornato automaticamente e subire variazioni in diminuzione<fo:block/>(per sgravi, note di credito) o in aumento (per sanzioni, interessi, ecc.).</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="299pt" top="{349 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="213pt" height="20pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="8pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">PAGA SUL TERRITORIO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="538pt" top="{291 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="35pt" height="35pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="35pt" content-height="35pt" scaling="non-uniform" src="url(classpath:/fop/icone/canali-digitali.svg)"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="{547 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="136pt" height="26pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="10.636pt" padding-top="1.77pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di <fo:block/>pagamento o usa i dati accanto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{482 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="191pt" top="{289 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="103pt" height="42pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" line-height="24.336pt" padding-top="2.023pt"><xsl:choose><xsl:when test="normalize-space(rata/giorni)"><fo:block font-size="8pt" line-height="10.13pt"><xsl:value-of select="rata/tipo"/></fo:block><fo:block space-before="5.057pt"><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="rata/giorni"/></xsl:call-template></fo:block></xsl:when><xsl:when test="normalize-space(rata/data)"><fo:block font-size="8pt" line-height="10.13pt">entro il</fo:block><fo:block space-before="5.057pt"><xsl:value-of select="rata/data"/></fo:block></xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="401pt" top="{547 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="174pt" height="14pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" text-align="end" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{308 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="213pt" height="41pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="8pt" line-height="10.636pt" padding-top="1.77pt"><xsl:if test="normalize-space(/AvvisoPagamentoInput/del_tuo_ente)"><xsl:value-of select="concat('oppure sul sito ', /AvvisoPagamentoInput/del_tuo_ente, ', ')"/></xsl:if>dal tuo Home Banking, con la tua app di pagamento o con gli altri canali abilitati.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="299pt" top="{368 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="213pt" height="37pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="8pt" line-height="10.125pt" padding-top="2.025pt" wrap-option="no-wrap">presso Banche e Sportelli ATM, negli Uffici Postali e Punti <fo:block/>Postali, nei Bar, Edicole, Ricevitorie, Supermercati, <fo:block/>Tabaccherie e altri Esercenti Convenzionati.</fo:block>
		</fo:block-container>
		<xsl:if test="not(/AvvisoPagamentoInput/nascondi_informativa_importo_avviso = 'true') and normalize-space(/AvvisoPagamentoInput/informativa_importo_avviso)">
			<fo:block-container absolute-position="absolute" left="54pt" top="{351 + $allungamento-1916de52-gruppo + $allungamento-c4d1c1c7-gruppo}pt" width="232pt" height="40pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/informativa_importo_avviso"/>
						<xsl:with-param name="markup" select="'html'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="6"/>
						<xsl:with-param name="larghezza" select="232"/>
						<xsl:with-param name="altezza" select="40"/>
						<xsl:with-param name="maxRighe" select="4"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
