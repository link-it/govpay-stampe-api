<?xml version="1.0" encoding="UTF-8"?>
<!--
	Template di supporto comuni ai layout XSL-FO degli avvisi (issue #37).
	XSLT 1.0: e' sufficiente il processore del JDK.
-->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:tj="http://xml.apache.org/xalan/java/it.govpay.stampe.fop.TestoJasper" exclude-result-prefixes="tj">

	<!-- Formato numerico italiano, come DecimalFormat("#,###,##0.00", Locale.ITALY) dei template Jasper -->
	<xsl:decimal-format name="it" decimal-separator="," grouping-separator="."/>

	<xsl:template name="importo">
		<xsl:param name="valore"/>
		<xsl:value-of select="format-number(number($valore), '#.##0,00', 'it')"/>
	</xsl:template>

	<!--
		Testo variabile che va a capo come in Jasper, con le funzioni di TestoJasper. Si chiama dentro il fo:block
		del riquadro, con linefeed-treatment="preserve" e wrap-option="no-wrap": scrive line-height e padding-top
		del blocco, che dipendono dal numero di righe, e poi le righe.
		- testo: il valore; markup: none, styled o html (con markup le righe sono nodi XSL-FO, da TestoJasper.stile);
		- font, grassetto, corsivo, corpo, colore: il font del riquadro Jasper;
		- interlinea: lineSpacingSize con interlinea Fixed, 0 con Single;
		- larghezza: larghezza utile; altezza: altezza utile, senza i padding verticali; paddingSopra: padding superiore;
		- maxRighe: righe che stanno nel riquadro, 0 se il riquadro si allunga; tronca: troncamento a carattere;
		- allineamento: allineamento verticale (Top, Middle, Bottom);
		- limiteRighe: righe oltre le quali interlinea e padding restano quelli del limite.
	-->
	<xsl:template name="testo-a-capo">
		<xsl:param name="testo"/>
		<xsl:param name="markup" select="'none'"/>
		<xsl:param name="font"/>
		<xsl:param name="grassetto" select="false()"/>
		<xsl:param name="corsivo" select="false()"/>
		<xsl:param name="corpo"/>
		<xsl:param name="colore" select="''"/>
		<xsl:param name="interlinea" select="0"/>
		<xsl:param name="larghezza"/>
		<xsl:param name="altezza"/>
		<xsl:param name="paddingSopra" select="0"/>
		<xsl:param name="maxRighe" select="0"/>
		<xsl:param name="tronca" select="false()"/>
		<xsl:param name="allineamento" select="'Top'"/>
		<xsl:param name="limiteRighe" select="10 * number($maxRighe = 0) + ($maxRighe + (5 - $maxRighe) * number($maxRighe &lt; 5)) * number($maxRighe &gt; 0)"/>
		<xsl:variable name="righe">
			<xsl:choose>
				<xsl:when test="$markup = 'none'"><xsl:value-of select="tj:righe(string($testo), $font, $grassetto, $corsivo, $corpo, $larghezza, $maxRighe, $tronca)"/></xsl:when>
				<xsl:otherwise><xsl:copy-of select="tj:stile(string($testo), $markup, $font, $grassetto, $corsivo, $corpo, $colore, $larghezza, $maxRighe, $tronca)"/></xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:variable name="n" select="tj:numeroRighe(string($righe))"/>
		<xsl:attribute name="line-height"><xsl:value-of select="tj:interlinea($n, $limiteRighe, $font, $corpo, $interlinea)"/></xsl:attribute>
		<xsl:attribute name="padding-top"><xsl:value-of select="tj:paddingTesto($n, $limiteRighe, $font, $corpo, $interlinea, $allineamento, $altezza, $paddingSopra)"/></xsl:attribute>
		<xsl:copy-of select="$righe"/>
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
		Logo raster come Jasper (scaleImage RetainShape), dentro un riquadro larghezza x altezza: scalato per starci
		con le sue proporzioni, poi spostato secondo l'allineamento di uno spazio libero TRONCATO a punti interi.
		Le dimensioni in pixel ("larghezza altezza") le aggiunge PreparazioneAvviso nell'elemento <logo>_px
		(parametro px); senza, il logo si scala nel riquadro senza troncare. Logo vuoto: niente.
		- orizzontale: Left, Center o Right; verticale: Top, Middle o Bottom.
	-->
	<xsl:template name="logo">
		<xsl:param name="logo"/>
		<xsl:param name="px"/>
		<xsl:param name="larghezza"/>
		<xsl:param name="altezza"/>
		<xsl:param name="orizzontale" select="'Left'"/>
		<xsl:param name="verticale" select="'Top'"/>
		<xsl:variable name="sorgente">url(<xsl:call-template name="data-uri"><xsl:with-param name="valore" select="$logo"/></xsl:call-template>)</xsl:variable>
		<!-- font-size 0: senza, FOP allinea l'immagine alla linea di base del testo e la alza di qualche punto -->
		<fo:block font-size="0pt" line-height="0pt"><xsl:if test="normalize-space($logo)"><xsl:choose>
			<xsl:when test="normalize-space($px)">
				<xsl:variable name="W" select="$larghezza"/><xsl:variable name="H" select="$altezza"/>
				<xsl:variable name="iw" select="number(substring-before(normalize-space($px), ' '))"/>
				<xsl:variable name="ih" select="number(substring-after(normalize-space($px), ' '))"/>
				<xsl:variable name="sc"><xsl:choose><xsl:when test="$W div $iw &lt; $H div $ih"><xsl:value-of select="$W div $iw"/></xsl:when><xsl:otherwise><xsl:value-of select="$H div $ih"/></xsl:otherwise></xsl:choose></xsl:variable>
				<xsl:variable name="dx"><xsl:choose>
					<xsl:when test="$orizzontale = 'Center'"><xsl:value-of select="floor(($W - $iw * $sc) div 2)"/></xsl:when>
					<xsl:when test="$orizzontale = 'Right'"><xsl:value-of select="floor($W - $iw * $sc)"/></xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose></xsl:variable>
				<xsl:variable name="dy"><xsl:choose>
					<xsl:when test="$verticale = 'Middle'"><xsl:value-of select="floor(($H - $ih * $sc) div 2)"/></xsl:when>
					<xsl:when test="$verticale = 'Bottom'"><xsl:value-of select="floor($H - $ih * $sc)"/></xsl:when>
					<xsl:otherwise>0</xsl:otherwise>
				</xsl:choose></xsl:variable>
				<fo:block-container absolute-position="absolute" left="{$dx}pt" top="{$dy}pt" width="{$iw * $sc}pt" height="{$ih * $sc}pt">
					<fo:block font-size="0pt" line-height="0pt"><fo:external-graphic content-width="{$iw * $sc}pt" content-height="{$ih * $sc}pt" scaling="non-uniform" src="{$sorgente}"/></fo:block>
				</fo:block-container>
			</xsl:when>
			<xsl:otherwise><fo:external-graphic content-width="scale-to-fit" content-height="scale-to-fit" width="{$larghezza}pt" height="{$altezza}pt" scaling="uniform" src="{$sorgente}"/></xsl:otherwise>
		</xsl:choose></xsl:if></fo:block>
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

	<!-- entita' HTML nelle etichette, che Jasper interpreta nel markup styled: &#176; e' il simbolo di grado -->
	<xsl:template name="entita">
		<xsl:param name="testo"/>
		<xsl:choose>
			<xsl:when test="contains($testo, '&amp;#176;')">
				<xsl:value-of select="substring-before($testo, '&amp;#176;')"/>
				<xsl:text>°</xsl:text>
				<xsl:call-template name="entita"><xsl:with-param name="testo" select="substring-after($testo, '&amp;#176;')"/></xsl:call-template>
			</xsl:when>
			<xsl:otherwise><xsl:value-of select="$testo"/></xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<!-- parte N (1 o 2) di un'etichetta "*parte 1*parte 2", come split("\\*")[N] in Jasper -->
	<xsl:template name="parte-etichetta">
		<xsl:param name="etichetta"/>
		<xsl:param name="n"/>
		<xsl:variable name="dopo" select="substring-after($etichetta, '*')"/>
		<xsl:call-template name="entita">
			<xsl:with-param name="testo">
				<xsl:choose>
					<xsl:when test="$n = 1"><xsl:value-of select="substring-before($dopo, '*')"/></xsl:when>
					<xsl:otherwise><xsl:value-of select="substring-after($dopo, '*')"/></xsl:otherwise>
				</xsl:choose>
			</xsl:with-param>
		</xsl:call-template>
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
