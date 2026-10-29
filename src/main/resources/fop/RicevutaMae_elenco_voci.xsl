<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO RicevutaMae_elenco_voci (issue #37): voce della ricevuta telematica (da RicevutaMae.xsl).
	Derivato dal template Jasper RicevutaMae_elenco_voci.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento elenco_voci/voce; i dati comuni sono sotto /root.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoFop" exclude-result-prefixes="tj">

	<xsl:template name="RicevutaMae_elenco_voci">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="allungamento-descrizione" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb', false(), false(), 12, 234, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-id-riscossione" select="tj:allungamento(tj:righe(string(id_riscossione), 'TitilliumWeb', false(), false(), 12, 185, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-descrizione-2" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb', false(), false(), 12, 419, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(not(/root/versione_oggetto = 'SANP_230'))"/>
		<xsl:variable name="allungamento-descrizione-e-affiancati" select="(number((number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) &gt; $allungamento-descrizione-2) * (number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) + number(not((number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) &gt; $allungamento-descrizione-2)) * $allungamento-descrizione-2)"/>
		<xsl:if test="/root/versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="250pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="0pt" end-indent="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="descrizione"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="8"/>
						<xsl:with-param name="larghezza" select="234"/>
						<xsl:with-param name="altezza" select="20"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="435pt" top="0pt" width="80pt" height="20pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="format-number(number(importo), '#.##0,00', 'it')"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="12"/>
					<xsl:with-param name="interlinea" select="8"/>
					<xsl:with-param name="larghezza" select="80"/>
					<xsl:with-param name="altezza" select="20"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="/root/versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="250pt" top="0pt" width="185pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="id_riscossione"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="8"/>
						<xsl:with-param name="larghezza" select="185"/>
						<xsl:with-param name="altezza" select="20"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="not(/root/versione_oggetto = 'SANP_230')">
			<fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="435pt" height="20pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="0pt" end-indent="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="descrizione"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="8"/>
						<xsl:with-param name="larghezza" select="419"/>
						<xsl:with-param name="altezza" select="20"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

	<xsl:template name="RicevutaMae_elenco_voci-altezza">
		<xsl:variable name="allungamento-descrizione" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb', false(), false(), 12, 234, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-id-riscossione" select="tj:allungamento(tj:righe(string(id_riscossione), 'TitilliumWeb', false(), false(), 12, 185, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(/root/versione_oggetto = 'SANP_230')"/>
		<xsl:variable name="allungamento-descrizione-2" select="tj:allungamento(tj:righe(string(descrizione), 'TitilliumWeb', false(), false(), 12, 419, 0, false()), 'TitilliumWeb', 12, 8, 0, 20) * number(not(/root/versione_oggetto = 'SANP_230'))"/>
		<xsl:variable name="allungamento-descrizione-e-affiancati" select="(number((number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) &gt; $allungamento-descrizione-2) * (number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) + number(not((number($allungamento-descrizione &gt; $allungamento-id-riscossione) * $allungamento-descrizione + number(not($allungamento-descrizione &gt; $allungamento-id-riscossione)) * $allungamento-id-riscossione) &gt; $allungamento-descrizione-2)) * $allungamento-descrizione-2)"/>
		<xsl:value-of select="27 + $allungamento-descrizione-e-affiancati"/>
	</xsl:template>

</xsl:stylesheet>
