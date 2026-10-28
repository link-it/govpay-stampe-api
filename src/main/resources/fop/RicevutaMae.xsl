<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/RicevutaMae.jrxml: non modificare a mano.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="RicevutaMae">
		<!-- banda detail, y=0 -->
		<xsl:variable name="allungamento-6a9c8cc3" select="tj:allungamento(tj:righe(string(oggetto_del_pagamento), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-c4fdde83" select="tj:allungamento(tj:righe(string(ente_denominazione), 'TitilliumWeb-Bold.ttf', 14.0, 515.0, 0, false()), 'TitilliumWeb-Bold', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-29219fc3" select="tj:allungamento(tj:righe(string(concat('codice fiscale: ', cf_ente)), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-ded07971" select="tj:allungamento(tj:righe(string(indirizzo_ente), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-13f8bbeb" select="tj:allungamento(tj:righe(string(luogo_ente), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<fo:block-container absolute-position="absolute" left="40pt" top="{501.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Identificativo pagamento:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="106pt" width="282pt" height="37pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="24pt" line-height="36.504pt" padding-top="3.012pt" wrap-option="no-wrap">Ricevuta di pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="0pt" width="85pt" height="85pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(logo_pagopa)"><xsl:choose>
				<xsl:when test="normalize-space(logo_pagopa_px)">
					<xsl:variable name="W" select="85.0"/><xsl:variable name="H" select="85.0"/>
					<xsl:variable name="iw" select="number(substring-before(normalize-space(logo_pagopa_px), ' '))"/>
					<xsl:variable name="ih" select="number(substring-after(normalize-space(logo_pagopa_px), ' '))"/>
					<xsl:variable name="sc"><xsl:choose><xsl:when test="$W div $iw &lt; $H div $ih"><xsl:value-of select="$W div $iw"/></xsl:when><xsl:otherwise><xsl:value-of select="$H div $ih"/></xsl:otherwise></xsl:choose></xsl:variable>
					<fo:block-container absolute-position="absolute" left="{0}pt" top="{floor(($H - $ih * $sc) div 2)}pt" width="{$iw * $sc}pt" height="{$ih * $sc}pt">
						<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="{$iw * $sc}pt" content-height="{$ih * $sc}pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="logo_pagopa"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></fo:block>
					</fo:block-container>
				</xsl:when>
				<xsl:otherwise><fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="85pt" height="85pt" scaling="uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="logo_pagopa"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></xsl:otherwise>
			</xsl:choose></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="470pt" top="0pt" width="85pt" height="85pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(logo_ente)"><xsl:choose>
				<xsl:when test="normalize-space(logo_ente_px)">
					<xsl:variable name="W" select="85.0"/><xsl:variable name="H" select="85.0"/>
					<xsl:variable name="iw" select="number(substring-before(normalize-space(logo_ente_px), ' '))"/>
					<xsl:variable name="ih" select="number(substring-after(normalize-space(logo_ente_px), ' '))"/>
					<xsl:variable name="sc"><xsl:choose><xsl:when test="$W div $iw &lt; $H div $ih"><xsl:value-of select="$W div $iw"/></xsl:when><xsl:otherwise><xsl:value-of select="$H div $ih"/></xsl:otherwise></xsl:choose></xsl:variable>
					<fo:block-container absolute-position="absolute" left="{floor($W - $iw * $sc)}pt" top="{floor(($H - $ih * $sc) div 2)}pt" width="{$iw * $sc}pt" height="{$ih * $sc}pt">
						<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="{$iw * $sc}pt" content-height="{$ih * $sc}pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="logo_ente"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></fo:block>
					</fo:block-container>
				</xsl:when>
				<xsl:otherwise><fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="85pt" height="85pt" scaling="uniform"><xsl:attribute name="src">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="logo_ente"/></xsl:call-template>)</xsl:attribute></fo:external-graphic></xsl:otherwise>
			</xsl:choose></xsl:if></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="157pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(oggetto_del_pagamento), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{202.0 - (-number($allungamento-6a9c8cc3))}pt" width="515pt" height="30pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#808080" line-height="21.294pt" padding-top="9.757pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{202.0 - (-number($allungamento-6a9c8cc3))}pt" width="515pt" height="0pt" border-top="0.5pt solid #808080"><fo:block/></fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{248.0 - (-number($allungamento-6a9c8cc3))}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(ente_denominazione), 'TitilliumWeb-Bold.ttf', 14.0, 515.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb-Bold', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb-Bold', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{270.0 - (-number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83))}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(concat('codice fiscale: ', cf_ente)), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{299.0 - (-number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3))}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(indirizzo_ente), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(luogo_ente)))">
			<fo:block-container absolute-position="absolute" left="40pt" top="{321.0 - (-number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971))}pt" width="515pt" height="22pt">
				<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(luogo_ente), 'TitilliumWeb-Regular.ttf', 14.0, 515.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="40pt" top="{369.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="515pt" height="30pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#808080" line-height="21.294pt" padding-top="9.757pt" wrap-option="no-wrap">INFORMAZIONI SUL PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{369.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="515pt" height="0pt" border-top="0.5pt solid #808080"><fo:block/></fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{457.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="istituto"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{413.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(concat(format-number(number(importo), '#.##0,00', 'it'), ' Euro')), 'TitilliumWeb-Bold.ttf', 14.0, 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb-Bold', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb-Bold', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{501.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="CCP"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{479.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="IUV"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{523.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="dataOperazione"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{457.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Psp:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{479.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Identificativo versamento:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{523.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Data operazione:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{435.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="stato"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<xsl:if test="versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="40pt" top="{577.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="250pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Causale importi</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="290pt" top="{577.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="185pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">ID riscossione</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="475pt" top="{577.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="80pt" height="19pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" text-align="end" end-indent="0pt" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Importo</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{413.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Importo operazione:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{435.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Esito:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{545.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Data applicativa:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{545.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:variable name="valore"><xsl:value-of select="dataApplicativa"/></xsl:variable><xsl:copy-of select="tj:stile(string($valore), 'styled', 'TitilliumWeb', false(), false(), 14.0, '', 333.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb', 14, 12)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb', 14, 12, 'Top', 22, 0)"/></xsl:attribute><xsl:copy-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<xsl:if test="not(versione_oggetto = 'SANP_230')">
			<fo:block-container absolute-position="absolute" left="40pt" top="{577.0 - (22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb))}pt" width="435pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Causale importi</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:variable name="spostamento-266ec87d" select="22 * number(not(normalize-space(luogo_ente))) + -number($allungamento-6a9c8cc3) + -number($allungamento-c4fdde83) + -number($allungamento-29219fc3) + -number($allungamento-ded07971) + -number($allungamento-13f8bbeb)"/>
		<fo:block-container height="{600.0 - $spostamento-266ec87d}pt"><fo:block/></fo:block-container>
		<xsl:for-each select="elenco_voci/voce">
			<xsl:variable name="altezza-record"><xsl:call-template name="RicevutaMae_elenco_voci-altezza"/></xsl:variable>
			<fo:block-container height="27pt" space-after="{$altezza-record - 27}pt" keep-together.within-page="always">
				<fo:block-container absolute-position="absolute" left="40pt" top="0pt" width="515pt" height="{$altezza-record}pt">
					<xsl:call-template name="RicevutaMae_elenco_voci">
					</xsl:call-template>
				</fo:block-container>
			</fo:block-container>
		</xsl:for-each>
		<fo:block-container height="2pt"><fo:block/></fo:block-container>
	</xsl:template>

</xsl:stylesheet>
