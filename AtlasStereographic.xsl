<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:output method="html" encoding="UTF-8"/>

	<xsl:template xmlns:ns="http://www.chartaecaeli.eu/astrolabe/model" match="AtlasStereographic">
		<xsl:value-of select="@chartpagerealx"/><xsl:text> </xsl:text>
		<xsl:value-of select="@chartpagerealy"/><xsl:text> </xsl:text>
		
		<xsl:text>&#xa;</xsl:text>

		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template xmlns:ns="http://www.chartaecaeli.eu/astrolabe/model" match="AtlasPage">
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
		
		<xsl:value-of select="(Origin/phi/HMS/@hrs*3600+Origin/phi/HMS/@min*60+Origin/phi/HMS/@sec) div 3600"/><xsl:text> </xsl:text>
		<xsl:value-of select="(Origin/theta/DMS/@deg*3600+Origin/theta/DMS/@min*60+Origin/theta/DMS/@sec) div 3600"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="Top/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Top/theta/Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="Bottom/phi/Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="Bottom/theta/Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="@scale"/>
		
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
