<?xml version="1.0" encoding="UTF-8"?>
<!--
	GENERATO da script/fop/jrxml2fo.py dal sorgente src/main/jasper/BollettinoRataV2.jrxml: non modificare a mano.
	Rigenerare con script/fop/genera-xsl.sh.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="BollettinoRataV2">
		<!-- banda detail, y=0.0 -->
		<xsl:if test="not(normalize-space(/AvvisoPagamentoInput/poste))">
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="15pt" width="595pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="20pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">BOLLETTINO POSTALE PA</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="0pt" top="15pt" width="595pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="216pt" top="103pt" width="262pt" height="14pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="46pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-poste-italiane.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="198pt" top="47pt" width="224pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="justify" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:text>sul C/C n. &lt;style isBold="true"&gt;</xsl:text><xsl:choose><xsl:when test="numero_cc_postale"><xsl:value-of select="numero_cc_postale"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;</xsl:text></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="224"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="166pt" top="170pt" width="165pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/codice_avviso"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="165"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="495pt" top="115pt" width="80pt" height="80pt">
				<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(data_matrix_svg)"><fo:external-graphic content-width="80pt" content-height="80pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="data_matrix_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="166pt" top="124pt" width="50pt" height="14pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="216pt" top="82pt" width="262pt" height="14pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="543pt" top="1pt" width="30pt" height="30pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="30pt" height="23.182pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="30pt" content-height="23.182pt" scaling="uniform" src="url(classpath:/fop/icone/forbici.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="361pt" top="170pt" width="116pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/codice_fiscale_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="116"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="216pt" top="124pt" width="262pt" height="40pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="167pt" top="44pt" width="24pt" height="24pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="24pt" content-height="24pt" scaling="non-uniform" src="url(classpath:/fop/icone/logo-euro-bollettino.svg)"/></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="361pt" top="184pt" width="116pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="end" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="116"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="167pt" top="15pt" width="408pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" start-indent="0pt" line-height="15.21pt" padding-top="2.65pt" wrap-option="no-wrap">Banco<fo:inline font-weight="bold">Posta</fo:inline></fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="167pt" top="15pt" width="408pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="166pt" top="184pt" width="165pt" height="16pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="codice_avviso_postale"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="165"/>
						<xsl:with-param name="altezza" select="16"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="331pt" top="184pt" width="30pt" height="16pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="9pt" text-align="center" line-height="13.689pt" padding-top="1.13pt" wrap-option="no-wrap">P1</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="53pt" width="128pt" height="75pt">
				<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="100pt" height="75pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="100pt" content-height="75pt" scaling="uniform" src="url(classpath:/fop/icone/logo-bollettino-postale.svg)"/></fo:block></fo:block-container></fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="129pt" width="137pt" height="50pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" line-height="11.291pt" padding-top="2.328pt" wrap-option="no-wrap">Bollettino Postale pagabile in tutti <fo:block/>gli Uffici Postali e sui canali fisici o <fo:block/>digitali abilitati di Poste Italiane e <fo:block/>dell’Ente Creditore</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="463pt" top="47pt" width="132pt" height="19pt">
				<fo:block font-family="TitilliumWeb" font-size="12pt" text-align="end" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:text>&lt;style isBold="true"&gt;Euro </xsl:text><xsl:value-of select="format-number(number(importo), '#.##0,00', 'it')"/><xsl:text>&lt;/style&gt;</xsl:text></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="larghezza" select="112"/>
						<xsl:with-param name="altezza" select="19"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Bottom'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="166pt" top="82pt" width="50pt" height="14pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="166pt" top="103pt" width="50pt" height="14pt">
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="331pt" top="170pt" width="30pt" height="14pt">
				<fo:block font-family="TitilliumWeb" font-size="9pt" text-align="center" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/etichette/italiano/tipo"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb'"/>
						<xsl:with-param name="corpo" select="9"/>
						<xsl:with-param name="larghezza" select="30"/>
						<xsl:with-param name="altezza" select="14"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="20pt" top="189pt" width="137pt" height="10pt">
				<fo:block font-family="TitilliumWeb" font-size="6pt" color="#CCCCCC" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="autorizzazione"/>
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
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/poste)">
			<fo:block-container absolute-position="absolute" left="245pt" top="15pt" width="350pt" height="18pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb" font-size="10pt" text-align="end" start-indent="0pt" end-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo"><xsl:choose><xsl:when test="not(normalize-space(numero_rata))"></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="numero_rata"><xsl:value-of select="numero_rata"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text> </xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(data))"><xsl:choose><xsl:when test="not(normalize-space(scadenza))"></xsl:when><xsl:otherwise><xsl:value-of select="scadenza"/></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/italiano/entro))"><xsl:text> </xsl:text></xsl:when><xsl:otherwise><xsl:value-of select="/AvvisoPagamentoInput/etichette/italiano/entro"/></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(numero_rata_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="numero_rata_tra"><xsl:value-of select="numero_rata_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(data))"><xsl:choose><xsl:when test="not(normalize-space(scadenza_tra))"></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; · </xsl:text><xsl:choose><xsl:when test="scadenza_tra"><xsl:value-of select="scadenza_tra"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt;</xsl:text></xsl:otherwise></xsl:choose></xsl:when><xsl:otherwise><xsl:choose><xsl:when test="not(normalize-space(/AvvisoPagamentoInput/etichette/traduzione/entro))"><xsl:text> </xsl:text></xsl:when><xsl:otherwise><xsl:text>&lt;font face="TitilliumWeb"&gt;&lt;style forecolor="#999999"&gt; </xsl:text><xsl:choose><xsl:when test="/AvvisoPagamentoInput/etichette/traduzione/entro"><xsl:value-of select="/AvvisoPagamentoInput/etichette/traduzione/entro"/></xsl:when><xsl:otherwise>null</xsl:otherwise></xsl:choose><xsl:text>&lt;/style&gt;&lt;/font&gt; </xsl:text></xsl:otherwise></xsl:choose></xsl:otherwise></xsl:choose><xsl:choose><xsl:when test="not(normalize-space(data))"></xsl:when><xsl:otherwise><xsl:value-of select="data"/></xsl:otherwise></xsl:choose></xsl:with-param>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="10"/>
						<xsl:with-param name="larghezza" select="330"/>
						<xsl:with-param name="altezza" select="18"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="allineamento" select="'Middle'"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
			<fo:block-container absolute-position="absolute" left="245pt" top="15pt" width="350pt" height="0pt" border-top="0.5pt solid #000000"><fo:block/></fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
