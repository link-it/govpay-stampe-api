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
		<xsl:variable name="senza-luogo-ente" select="22 * number(not(normalize-space(luogo_ente)))"/>
		<xsl:variable name="allungamento-266ec87d" select="(ceiling(count(elenco_voci/voce) div 1) * 27.0 - 39) * number((ceiling(count(elenco_voci/voce) div 1) * 27.0 - 39) &gt; 0)"/>
		<xsl:variable name="allungamento-6a9c8cc3" select="tj:allungamento(tj:righe(string(oggetto_del_pagamento), 'TitilliumWeb', false(), false(), 14, 515, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-c4fdde83" select="tj:allungamento(tj:righe(string(ente_denominazione), 'TitilliumWeb-Bold', false(), false(), 14, 515, 0, false()), 'TitilliumWeb-Bold', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-29219fc3" select="tj:allungamento(tj:righe(string(concat('codice fiscale: ', cf_ente)), 'TitilliumWeb', false(), false(), 14, 515, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-ded07971" select="tj:allungamento(tj:righe(string(indirizzo_ente), 'TitilliumWeb', false(), false(), 14, 515, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<xsl:variable name="allungamento-13f8bbeb" select="tj:allungamento(tj:righe(string(luogo_ente), 'TitilliumWeb', false(), false(), 14, 515, 0, false()), 'TitilliumWeb', 14, 12, 0, 22)"/>
		<fo:block-container absolute-position="absolute" left="40pt" top="{501 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Identificativo pagamento:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="106pt" width="282pt" height="37pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="24pt" line-height="36.504pt" padding-top="3.012pt" wrap-option="no-wrap">Ricevuta di pagamento</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="0pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="logo_pagopa"/>
				<xsl:with-param name="px" select="logo_pagopa_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="verticale" select="'Middle'"/>
			</xsl:call-template>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="470pt" top="0pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="logo_ente"/>
				<xsl:with-param name="px" select="logo_ente_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
				<xsl:with-param name="verticale" select="'Middle'"/>
			</xsl:call-template>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="157pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="oggetto_del_pagamento"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="515"/>
					<xsl:with-param name="altezza" select="22"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{202 + $allungamento-6a9c8cc3}pt" width="515pt" height="30pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#808080" line-height="21.294pt" padding-top="9.757pt" wrap-option="no-wrap">ENTE CREDITORE</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{202 + $allungamento-6a9c8cc3}pt" width="515pt" height="0pt" border-top="0.5pt solid #808080"><fo:block/></fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{248 + $allungamento-6a9c8cc3}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="ente_denominazione"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="515"/>
					<xsl:with-param name="altezza" select="22"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{270 + $allungamento-6a9c8cc3 + $allungamento-c4fdde83}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="concat('codice fiscale: ', cf_ente)"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="515"/>
					<xsl:with-param name="altezza" select="22"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{299 + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3}pt" width="515pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="indirizzo_ente"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="515"/>
					<xsl:with-param name="altezza" select="22"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(luogo_ente)))">
			<fo:block-container absolute-position="absolute" left="40pt" top="{321 + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971}pt" width="515pt" height="22pt">
				<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="luogo_ente"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="14"/>
						<xsl:with-param name="interlinea" select="12"/>
						<xsl:with-param name="larghezza" select="515"/>
						<xsl:with-param name="altezza" select="22"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="40pt" top="{369 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="515pt" height="30pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#808080" line-height="21.294pt" padding-top="9.757pt" wrap-option="no-wrap">INFORMAZIONI SUL PAGAMENTO</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{369 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="515pt" height="0pt" border-top="0.5pt solid #808080"><fo:block/></fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{457 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="istituto"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{413 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb-Bold" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="concat(format-number(number(importo), '#.##0,00', 'it'), ' Euro')"/>
					<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{501 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="CCP"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{479 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="IUV"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{523 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="dataOperazione"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{457 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Psp:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{479 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Identificativo versamento:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{523 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Data operazione:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{435 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="stato"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="40pt" top="{577 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="250pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Causale importi</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="versione_oggetto = 'SANP_230'">
			<fo:block-container absolute-position="absolute" left="290pt" top="{577 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="185pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">ID riscossione</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="475pt" top="{577 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="80pt" height="19pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" text-align="end" end-indent="0pt" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Importo</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{413 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Importo operazione:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{435 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Esito:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="40pt" top="{545 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="182pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" color="#8A8A8A" line-height="21.294pt" padding-top="1.757pt" wrap-option="no-wrap">Data applicativa:</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="222pt" top="{545 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="333pt" height="22pt">
			<fo:block font-family="TitilliumWeb" font-size="14pt" linefeed-treatment="preserve" wrap-option="no-wrap">
				<xsl:call-template name="testo-a-capo">
					<xsl:with-param name="testo" select="dataApplicativa"/>
					<xsl:with-param name="markup" select="'styled'"/>
					<xsl:with-param name="font" select="'TitilliumWeb'"/>
					<xsl:with-param name="corpo" select="14"/>
					<xsl:with-param name="interlinea" select="12"/>
					<xsl:with-param name="larghezza" select="333"/>
					<xsl:with-param name="altezza" select="22"/>
					<xsl:with-param name="maxRighe" select="1"/>
				</xsl:call-template>
			</fo:block>
		</fo:block-container>
		<xsl:if test="not(versione_oggetto = 'SANP_230')">
			<fo:block-container absolute-position="absolute" left="40pt" top="{577 - $senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb}pt" width="435pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" color="#808080" line-height="18.252pt" padding-top="1.506pt" wrap-option="no-wrap">Causale importi</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:variable name="spostamento-266ec87d" select="-$senza-luogo-ente + $allungamento-6a9c8cc3 + $allungamento-c4fdde83 + $allungamento-29219fc3 + $allungamento-ded07971 + $allungamento-13f8bbeb"/>
		<fo:block-container height="{600.0 + $spostamento-266ec87d}pt"><fo:block/></fo:block-container>
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
