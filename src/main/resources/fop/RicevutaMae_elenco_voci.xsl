<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/RicevutaMae_elenco_voci.jrxml: non modificare a mano.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="RicevutaMae_elenco_voci">
		<!-- banda detail, y=0.0 -->
		<xsl:variable name="allungamento-c6344c19" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 234.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-e88f55a7" select="tj:allungamento(tj:righe(string(id_riscossione), 'TitilliumWeb-Regular.ttf', 12.0, 185.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-05b5be5f" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 419.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(not(/root/versione_oggetto = 'SANP_230'))"/>
		<xsl:variable name="allungamento-c6344c19-gruppo" select="(number((number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) &gt; number($allungamento-05b5be5f)) * (number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) + number(not((number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) &gt; number($allungamento-05b5be5f))) * number($allungamento-05b5be5f))"/>
		<xsl:if test="/root/versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="250pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="0pt" end-indent="16pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 234.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 12, 8)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 12, 8, 'Top', 20, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="435pt" top="0pt" width="80pt" height="20pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(format-number(number(importo), '#.##0,00', 'it')), 'TitilliumWeb-Bold.ttf', 12.0, 80.0, 1, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 5, 'TitilliumWeb-Bold', 12, 8)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 5, 'TitilliumWeb-Bold', 12, 8, 'Top', 20, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
		</fo:block-container>
		<xsl:if test="/root/versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="250pt" top="0pt" width="185pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(id_riscossione), 'TitilliumWeb-Regular.ttf', 12.0, 185.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 12, 8)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 12, 8, 'Top', 20, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not(/root/versione_oggetto = 'SANP_230')">
			<fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="435pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="0pt" end-indent="16pt" linefeed-treatment="preserve" wrap-option="no-wrap"><xsl:variable name="testo-righe"><xsl:value-of select="tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 419.0, 0, false())"/></xsl:variable><xsl:variable name="righe" select="tj:numeroRighe(string($testo-righe))"/><xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($righe, 10, 'TitilliumWeb', 12, 8)"/></xsl:attribute><xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($righe, 10, 'TitilliumWeb', 12, 8, 'Top', 20, 0)"/></xsl:attribute><xsl:value-of select="$testo-righe"/></fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RicevutaMae_elenco_voci-altezza">
		<xsl:variable name="allungamento-c6344c19" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 234.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-e88f55a7" select="tj:allungamento(tj:righe(string(id_riscossione), 'TitilliumWeb-Regular.ttf', 12.0, 185.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-05b5be5f" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb-Regular.ttf', 12.0, 419.0, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(not(/root/versione_oggetto = 'SANP_230'))"/>
		<xsl:variable name="allungamento-c6344c19-gruppo" select="(number((number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) &gt; number($allungamento-05b5be5f)) * (number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) + number(not((number(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7)) * number($allungamento-c6344c19) + number(not(number($allungamento-c6344c19) &gt; number($allungamento-e88f55a7))) * number($allungamento-e88f55a7)) &gt; number($allungamento-05b5be5f))) * number($allungamento-05b5be5f))"/>
		<xsl:value-of select="27 + $allungamento-c6344c19-gruppo"/>
	</xsl:template>

</xsl:stylesheet>
