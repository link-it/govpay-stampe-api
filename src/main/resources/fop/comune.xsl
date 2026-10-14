<?xml version="1.0" encoding="UTF-8"?>
<!--
	Template di supporto comuni ai layout XSL-FO degli avvisi (issue #37).
	XSLT 1.0: e' sufficiente il processore del JDK.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<!-- Formato numerico italiano, come DecimalFormat("#,###,##0.00", Locale.ITALY) dei template Jasper -->
	<xsl:decimal-format name="it" decimal-separator="," grouping-separator="."/>

	<xsl:template name="importo">
		<xsl:param name="valore"/>
		<xsl:value-of select="format-number(number($valore), '#.##0,00', 'it')"/>
	</xsl:template>

	<!--
		Un logo arriva come URI data: completo oppure come solo contenuto base64:
		nel secondo caso si assume PNG, come fa il template Jasper che decodifica la parte dopo la virgola.
	-->
	<xsl:template name="data-uri">
		<xsl:param name="valore"/>
		<xsl:variable name="v" select="normalize-space($valore)"/>
		<xsl:choose>
			<xsl:when test="contains($v, ',')"><xsl:value-of select="$v"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="concat('data:image/png;base64,', $v)"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!--
		Markup html/styled di Jasper in un valore dinamico (per esempio info_ente, che il mapper
		compone con <br/>): gestiti gli a capo <br/> e <br>, e il grassetto <b>...</b>.
	-->
	<xsl:template name="markup">
		<xsl:param name="testo"/>
		<xsl:choose>
			<xsl:when test="contains($testo, '&lt;br/&gt;')">
				<xsl:call-template name="markup-grassetto"><xsl:with-param name="testo" select="substring-before($testo, '&lt;br/&gt;')"/></xsl:call-template>
				<fo:block/>
				<xsl:call-template name="markup"><xsl:with-param name="testo" select="substring-after($testo, '&lt;br/&gt;')"/></xsl:call-template>
			</xsl:when>
			<xsl:when test="contains($testo, '&lt;br&gt;')">
				<xsl:call-template name="markup-grassetto"><xsl:with-param name="testo" select="substring-before($testo, '&lt;br&gt;')"/></xsl:call-template>
				<fo:block/>
				<xsl:call-template name="markup"><xsl:with-param name="testo" select="substring-after($testo, '&lt;br&gt;')"/></xsl:call-template>
			</xsl:when>
			<xsl:otherwise>
				<xsl:call-template name="markup-grassetto"><xsl:with-param name="testo" select="$testo"/></xsl:call-template>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="markup-grassetto">
		<xsl:param name="testo"/>
		<xsl:choose>
			<xsl:when test="contains($testo, '&lt;b&gt;') and contains(substring-after($testo, '&lt;b&gt;'), '&lt;/b&gt;')">
				<xsl:value-of select="substring-before($testo, '&lt;b&gt;')"/>
				<fo:inline font-weight="bold"><xsl:value-of select="substring-before(substring-after($testo, '&lt;b&gt;'), '&lt;/b&gt;')"/></fo:inline>
				<xsl:call-template name="markup-grassetto"><xsl:with-param name="testo" select="substring-after(substring-after($testo, '&lt;b&gt;'), '&lt;/b&gt;')"/></xsl:call-template>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="$testo"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- "1 giorno" / "N giorni", come nelle espressioni Jasper delle soglie -->
	<xsl:template name="giorni">
		<xsl:param name="giorni"/>
		<xsl:value-of select="$giorni"/>
		<xsl:choose>
			<xsl:when test="string($giorni) = '1'"> giorno</xsl:when>
			<xsl:otherwise> giorni</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

</xsl:stylesheet>
