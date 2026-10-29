<?xml version="1.0" encoding="UTF-8"?>
<!--
	Layout XSL-FO RataMultipla (issue #37): pagina dell'avviso con piu' di tre rate, a due o tre colonne (da avviso-semplice.xsl).
	Derivato dal template Jasper RataMultipla.jrxml. Posizioni in punti, come in Jasper (pagina o riquadro della rata):
	vedi script/fop/README.md per come modificarlo e verificarlo.
	Contesto dei percorsi relativi: l'elemento pagine/multipla; i dati comuni sono sotto /AvvisoPagamentoInput.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format" xmlns:svg="http://www.w3.org/2000/svg"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<xsl:template name="RataMultipla">
		<!-- banda detail del report Jasper, da y = 0 pt -->
		<xsl:variable name="senza-nome-cognome-destinatario-e-altri" select="13 * number((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))"/>
		<xsl:variable name="senza-colonne" select="10 * number((not(@colonne = '2')) and (not(@colonne = '3')))"/>
		<xsl:variable name="allungamento-doppio-formato" select="(ceiling(count(rata) div 2) * 165.0 - 10) * number((ceiling(count(rata) div 2) * 165.0 - 10) &gt; 0) * number(@colonne = '2')"/>
		<xsl:variable name="allungamento-triplo-formato" select="(ceiling(count(rata) div 3) * 160.0 - 10) * number((ceiling(count(rata) div 3) * 160.0 - 10) &gt; 0) * number(@colonne = '3')"/>
		<fo:block-container absolute-position="absolute" left="0pt" top="111pt" width="594pt" height="20pt" background-color="#EBEBEB">
			<fo:block font-family="TitilliumWeb-Bold" font-size="12pt" start-indent="20pt" line-height="18.252pt" padding-top="2.38pt" wrap-option="no-wrap">DATI PER IL PAGAMENTO</fo:block>
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
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="394pt" top="20pt" width="85pt" height="85pt">
			<xsl:call-template name="logo">
				<xsl:with-param name="logo" select="/AvvisoPagamentoInput/logo_ente_secondario"/>
				<xsl:with-param name="px" select="/AvvisoPagamentoInput/logo_ente_secondario_px"/>
				<xsl:with-param name="larghezza" select="85"/>
				<xsl:with-param name="altezza" select="85"/>
				<xsl:with-param name="orizzontale" select="'Right'"/>
			</xsl:call-template>
		</fo:block-container>
		<xsl:if test="normalize-space(/AvvisoPagamentoInput/logo_ente_secondario)">
			<fo:block-container absolute-position="absolute" left="20pt" top="58pt" width="350pt" height="42pt">
				<fo:block font-family="TitilliumWeb-Bold" font-size="16pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/oggetto_del_pagamento"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="16"/>
						<xsl:with-param name="interlinea" select="11"/>
						<xsl:with-param name="larghezza" select="350"/>
						<xsl:with-param name="altezza" select="42"/>
						<xsl:with-param name="maxRighe" select="2"/>
						<xsl:with-param name="tronca" select="true()"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="54pt" top="{192 - $senza-nome-cognome-destinatario-e-altri}pt" width="489pt" height="15pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Inquadra il <fo:inline font-weight="bold">codice QR</fo:inline> con la tua app o usa i dati sotto.</fo:block>
		</fo:block-container>
		<fo:block-container absolute-position="absolute" left="20pt" top="144pt" width="136pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.004pt" wrap-option="no-wrap">Destinatario</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="20pt" top="157pt" width="136pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/nome_cognome_destinatario"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="136"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="156pt" top="144pt" width="164pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Ente Creditore</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="156pt" top="157pt" width="164pt" height="13pt">
				<fo:block font-family="TitilliumWeb" font-size="8pt" start-indent="0pt" linefeed-treatment="preserve" wrap-option="no-wrap">
					<xsl:call-template name="testo-a-capo">
						<xsl:with-param name="testo" select="/AvvisoPagamentoInput/ente_creditore"/>
						<xsl:with-param name="markup" select="'styled'"/>
						<xsl:with-param name="font" select="'TitilliumWeb-Bold'"/>
						<xsl:with-param name="corpo" select="8"/>
						<xsl:with-param name="interlinea" select="7"/>
						<xsl:with-param name="larghezza" select="164"/>
						<xsl:with-param name="altezza" select="13"/>
						<xsl:with-param name="maxRighe" select="1"/>
					</xsl:call-template>
				</fo:block>
			</fo:block-container>
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="320pt" top="144pt" width="255pt" height="13pt">
			<fo:block font-family="TitilliumWeb" font-size="8pt" line-height="12.168pt" padding-top="1.836pt" wrap-option="no-wrap">Oggetto del pagamento</fo:block>
		</fo:block-container>
		<xsl:if test="not((not(normalize-space(/AvvisoPagamentoInput/nome_cognome_destinatario))) and (not(normalize-space(/AvvisoPagamentoInput/ente_creditore))) and (not(normalize-space(/AvvisoPagamentoInput/oggetto_del_pagamento))))">
			<fo:block-container absolute-position="absolute" left="320pt" top="157pt" width="255pt" height="13pt">
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
		</xsl:if>
		<fo:block-container absolute-position="absolute" left="20pt" top="{189 - $senza-nome-cognome-destinatario-e-altri}pt" width="20pt" height="20pt">
			<fo:block font-size="0pt" line-height="0pt"><fo:block-container absolute-position="absolute" left="0pt" top="0pt" width="20pt" height="20pt"><fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="20pt" content-height="20pt" scaling="uniform" src="url(classpath:/fop/icone/informazioni.svg)"/></fo:block></fo:block-container></fo:block>
		</fo:block-container>
		<xsl:if test="@colonne = '2'">
			<xsl:variable name="spostamento-doppio-formato" select="-$senza-nome-cognome-destinatario-e-altri"/>
			<xsl:for-each select="rata">
				<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 2) * 297.0}pt" top="{221.0 + $spostamento-doppio-formato + floor((position() - 1) div 2) * 165.0}pt" width="297pt" height="165pt">
					<fo:block/>
					<xsl:call-template name="DoppioFormato">
						<xsl:with-param name="Multipla" select="'true'"/>
					</xsl:call-template>
				</fo:block-container>
			</xsl:for-each>
		</xsl:if>
		<xsl:if test="@colonne = '3'">
			<xsl:variable name="spostamento-triplo-formato" select="-$senza-nome-cognome-destinatario-e-altri"/>
			<xsl:for-each select="rata">
				<fo:block-container absolute-position="absolute" left="{0.0 + ((position() - 1) mod 3) * 198.0}pt" top="{221.0 + $spostamento-triplo-formato + floor((position() - 1) div 3) * 160.0}pt" width="198pt" height="160pt">
					<fo:block/>
					<xsl:call-template name="TriploFormato">
						<xsl:with-param name="Multipla" select="'true'"/>
					</xsl:call-template>
				</fo:block-container>
			</xsl:for-each>
		</xsl:if>
	</xsl:template>

</xsl:stylesheet>
