<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:output method="html" encoding="UTF-8"/>

	<xsl:template xmlns:ns="http://www.chartacaeli.eu/astrolabe/model" match="AtlasPage">
		<xsl:value-of select="@p0x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p0y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3y"/><xsl:text> </xsl:text>

		<xsl:value-of select="P0/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P0/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P1/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P1/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P2/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P2/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="P3/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="P3/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:choose>
			<xsl:when test="Center/lon/HMS/@neg='true'"><xsl:value-of select="-1*(Center/lon/HMS/@hrs*3600+Center/lon/HMS/@min*60+Center/lon/HMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/lon/HMS/@hrs*3600+Center/lon/HMS/@min*60+Center/lon/HMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>
		<xsl:choose>
			<xsl:when test="Center/lon/DMS/@neg='true'"><xsl:value-of select="-1*(Center/lat/DMS/@deg*3600+Center/lat/DMS/@min*60+Center/lat/DMS/@sec) div 3600"/></xsl:when>
			<xsl:otherwise><xsl:value-of select="(Center/lat/DMS/@deg*3600+Center/lat/DMS/@min*60+Center/lat/DMS/@sec) div 3600"/></xsl:otherwise>
		</xsl:choose>
		<xsl:text> </xsl:text>

		<xsl:value-of select="Top/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Top/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="Bottom/lon/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Bottom/lat/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="@scale"/><xsl:text> </xsl:text>

		<xsl:value-of select="@num"/>

		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
