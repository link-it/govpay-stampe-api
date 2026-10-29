<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO DoppioFormatoPostale (issue #37): riquadro di una rata su due colonne, avviso postale (da DoppiaRataPostale.xsl).
	Derivato dal template Jasper DoppioFormatoPostale.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento rata; i dati comuni sono sotto /AvvisoPagamentoInput.
	Testi su due righe con corpi diversi (etichetta a 8 pt, valore in RobotoMono 12 pt): due blocchi, con il
	riquadro del testo di ciascuno dove Jasper mette la riga; la prima riga di Jasper e' alta quanto
	l'ascendente del suo corpo piu' la discendente del font del paragrafo (12 pt).
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="DoppioFormatoPostale">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="senza-numero-rata" select="20 * number(not(normalize-space(numero_rata)))"/>
		<xsl:variable name="senza-di-poste" select="20 * number(not(normalize-space(/AvvisoPagamentoInput/di_poste)))"/>
		<xsl:variable name="senza-di-poste-2" select="13 * number((not(normalize-space(/AvvisoPagamentoInput/di_poste))) and (not(normalize-space(/AvvisoPagamentoInput/di_poste))))"/>
		<xsl:variable name="senza-di-poste-3" select="20 * number((not(normalize-space(/AvvisoPagamentoInput/di_poste))) and (not(normalize-space(/AvvisoPagamentoInput/di_poste))))"/>
		<xsl:variable name="senza-ente-creditore" select="13 * number(not(normalize-space(/AvvisoPagamentoInput/ente_creditore)))"/>
		<xsl:variable name="senza-oggetto-del-pagamento" select="13 * number(not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento)))"/>
		<!-- intestazione "Rata N", vuota senza numero_rata: Jasper la rimuove (isRemoveLineWhenBlank) e il resto della rata sale -->
		<xsl:if test="not(not(normalize-space(numero_rata)))">
			<fo:block-container absolute-position="absolute" left="10pt" top="0pt" width="275pt" height="20pt" background-color="#EBEBEB">
				<fo:block font-family="TitilliumWeb-Bold" font-size="10pt" start-indent="10pt" end-indent="0pt" line-height="15.21pt" padding-top="3.65pt"><xsl:if test="normalize-space(numero_rata)">Rata <xsl:value-of select="numero_rata"/></xsl:if></fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="189pt" top="{20 - $senza-numero-rata}pt" width="96pt" height="96pt">
			<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space(qr_code_svg)"><fo:external-graphic content-width="96pt" content-height="96pt" scaling="non-uniform"><xsl:attribute name="src">url(<xsl:value-of select="qr_code_svg"/>)</xsl:attribute></fo:external-graphic></xsl:if></fo:block>
		</fo:block-container>
		<!-- "Euro" (8 pt) e importo in RobotoMono grassetto -->
		<fo:block-container absolute-position="absolute" left="0pt" top="{26 - $senza-numero-rata}pt" width="178pt" height="32pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.023pt"><fo:block font-size="8pt" line-height="10.13pt">Euro</fo:block><fo:block space-before="2.479pt" font-family="RobotoMono" font-weight="bold" line-height="15.828pt"><xsl:call-template name="importo"><xsl:with-param name="valore" select="importo"/></xsl:call-template></fo:block></fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="0pt" top="{179 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2 - $senza-di-poste-3}pt" width="285pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(/AvvisoPagamentoInput/ente_creditore)))">
			<fo:block-container absolute-position="absolute" left="0pt" top="{191 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2 - $senza-di-poste-3}pt" width="285pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="265"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{203 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2 - $senza-di-poste-3 - $senza-ente-creditore}pt" width="285pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<xsl:if test="not(not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento)))">
			<fo:block-container absolute-position="absolute" left="0pt" top="{215 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2 - $senza-di-poste-3 - $senza-ente-creditore}pt" width="285pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="265"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="0pt" top="{232 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2 - $senza-di-poste-3 - $senza-ente-creditore - $senza-oggetto-del-pagamento}pt" width="285pt" height="15pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="2.42pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app di pagamento o usa i dati sopra.</fo:block>
		</fo:block-container>
		<!-- "Entro il" + data oppure tipo + giorni della soglia, come sopra -->
		<fo:block-container absolute-position="absolute" left="0pt" top="56pt" width="178pt" height="32pt">
			<fo:block font-family="TitilliumWeb" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.023pt"><xsl:choose><xsl:when test="normalize-space(giorni)"><fo:block font-size="8pt" line-height="10.13pt"><xsl:value-of select="tipo"/></fo:block><fo:block space-before="2.479pt" font-family="RobotoMono" font-weight="bold" line-height="15.828pt"><xsl:call-template name="giorni"><xsl:with-param name="giorni" select="giorni"/></xsl:call-template></fo:block></xsl:when><xsl:when test="normalize-space(data)"><fo:block font-size="8pt" line-height="10.13pt">Entro il</fo:block><fo:block space-before="2.479pt" font-family="RobotoMono" font-weight="bold" line-height="15.828pt"><xsl:value-of select="data"/></fo:block></xsl:when></xsl:choose></fo:block>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="{106 - $senza-numero-rata}pt" width="189pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Codice Avviso</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="{118 - $senza-numero-rata}pt" width="189pt" height="20pt">
				<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="codice_avviso"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="169"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="{139 - $senza-numero-rata - $senza-di-poste}pt" width="189pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="20pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. Fiscale Ente</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="0pt" top="{151 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2}pt" width="189pt" height="20pt">
				<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="20pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cf_ente"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="169"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="199pt" top="{139 - $senza-numero-rata - $senza-di-poste}pt" width="89pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Cod. CBILL</fo:block>
			</fo:block-container>
		</xsl:if>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/di_poste)">
			<fo:block-container absolute-position="absolute" left="199pt" top="{151 - $senza-numero-rata - $senza-di-poste - $senza-di-poste-2}pt" width="89pt" height="20pt">
				<fo:block font-family="RobotoMono" font-size="12pt" font-weight="bold" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/cbill"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'RobotoMono'"/>
						<xsl:with-param name="grassetto" select="true()"/>
						<xsl:with-param name="corpo" select="12"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="89"/>
						<xsl:with-param name="altezza" select="20"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
