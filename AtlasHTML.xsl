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

		<xsl:value-of select="P0/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P0/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P1/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P1/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P2/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P2/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P3/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P3/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:choose>
			<xsl:when test="Center/deviation/HMS/@neg='true'"><xsl:value-of select="-1*(Center/deviation/HMS/@hrs*3600+Center/deviation/HMS/@min*60+Center/deviation/HMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/deviation/HMS/@hrs*3600+Center/deviation/HMS/@min*60+Center/deviation/HMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:choose>
			<xsl:when test="Center/deviation/DMS/@neg='true'"><xsl:value-of select="-1*(Center/elevation/DMS/@deg*3600+Center/elevation/DMS/@min*60+Center/elevation/DMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/elevation/DMS/@deg*3600+Center/elevation/DMS/@min*60+Center/elevation/DMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>

		<xsl:value-of select="Top/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Top/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="Bottom/deviation/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Bottom/elevation/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="@scale"/><xsl:text> </xsl:text>

		<xsl:value-of select="@num"/>

		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
