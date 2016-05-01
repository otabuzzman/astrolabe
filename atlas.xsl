<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format">

	<xsl:output method="html" encoding="UTF-8"/>

	<xsl:template xmlns:ns="http://www.chartaecaeli.eu/astrolabe/model" match="/ns:Atlas">
		<xsl:value-of select="@pagesizex"/><xsl:text> </xsl:text>
		<xsl:value-of select="@pagesizey"/><xsl:text> </xsl:text>
		
		<xsl:text>&#xa;</xsl:text>

		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template xmlns:ns="http://www.chartaecaeli.eu/astrolabe/model" match="ns:AtlasPage">
		<xsl:value-of select="@p0x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p0y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p1y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p2y"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3x"/><xsl:text> </xsl:text>
		<xsl:value-of select="@p3y"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:P0/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:P0/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:P1/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:P1/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:P2/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:P2/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:P3/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:P3/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:Origin/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:Origin/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:Top/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:Top/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>
		
		<xsl:value-of select="ns:Bottom/ns:phi/ns:Rational/@value"/><xsl:text> </xsl:text>
		<xsl:value-of select="ns:Bottom/ns:theta/ns:Rational/@value"/><xsl:text> </xsl:text>

		<xsl:value-of select="@scale"/>
		
		<xsl:text>&#xa;</xsl:text>
	</xsl:template>

</xsl:stylesheet>
