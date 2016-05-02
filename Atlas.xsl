<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:output method="html" encoding="UTF-8"/>

	<xsl:template xmlns:ns="http://www.chartacaeli.eu/astrolabe/model" match="Atlas">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template xmlns:ns="http://www.chartacaeli.eu/astrolabe/model" match="AtlasPage">
		<xsl:value-of select="@p0x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p0y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3y"/><xsl:text> </xsl:text>

		<xsl:value-of select="P0/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P0/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P1/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P1/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P2/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P2/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P3/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P3/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:choose>
			<xsl:when test="Center/phi/HMS/@neg='true'"><xsl:value-of select="-1*(Center/phi/HMS/@hrs*3600+Center/phi/HMS/@min*60+Center/phi/HMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/phi/HMS/@hrs*3600+Center/phi/HMS/@min*60+Center/phi/HMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:choose>
			<xsl:when test="Center/phi/DMS/@neg='true'"><xsl:value-of select="-1*(Center/theta/DMS/@deg*3600+Center/theta/DMS/@min*60+Center/theta/DMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/theta/DMS/@deg*3600+Center/theta/DMS/@min*60+Center/theta/DMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>

		<xsl:value-of select="Top/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Top/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="Bottom/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Bottom/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="@scale"/><xsl:text> </xsl:text>

		<xsl:value-of select="@num"/>

		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
