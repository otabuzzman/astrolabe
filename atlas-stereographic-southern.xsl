<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="1.0"

	xmlns="http://www.chartacaeli.eu/astrolabe/model"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"

	exclude-result-prefixes="xsl fo">

	<xsl:output method="xml" encoding="UTF-8"/>

	<xsl:param name="epochY" select="2000"/>
	<xsl:param name="epochM" select="1"/>
	<xsl:param name="epochD" select="1"/>

	<xsl:param name="numoff" select="0"/>

	<xsl:template match="Atlas">
		<Astrolabe name="atlas-sterographic-southern">
		<xsl:attribute
			xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
			name="xsi:schemaLocation">http://www.chartacaeli.eu/astrolabe/model astrolabe.xsd</xsl:attribute>
			<Epoch>
				<Calendar>
					<YMD y="{$epochY}" m="{$epochM}" d="{$epochD}"/>
				</Calendar>
			</Epoch>

			<xsl:apply-templates/>
		</Astrolabe>
	</xsl:template>

	<xsl:template match="AtlasPage">
		<Chart>
			<ChartStereographic northern="false">
			<xsl:attribute name="scale"><xsl:value-of select="@scale"/></xsl:attribute>
				<ChartPage name="atlas" size="a3" view="82">
					<xsl:apply-templates select="Center"/>
				</ChartPage>
				<Oblique>
					<lon>
						<Rational value="0"/>
					</lon>
					<lat>
						<Rational value="-90"/>
					</lat>
				</Oblique>
				<Horizon>
					<HorizonEquatorial practicality="always">
						<!--$pagetitle-->
						<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
							<!--"CHARTA CAELI"-->
							<Script purpose="titlelettering" value="&#xf743;&#xf748;&#xf741;&#xf752;&#xf754;&#xf741; &#xf743;&#xf741;&#xf745;&#xf74c;&#xf749;"/>
							<Frame number="2" anchor="bottommiddle"/>
						</AnnotationStraight>
						<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
							<Script purpose="textlettering" value="Atlas zum Sternkatalog The Bright Star Catalogue (S&#x00fc;dhimmel)"/>
							<Frame number="5" anchor="middle"/>
						</AnnotationStraight>
						<!--$legendmagnitude-->
						<AnnotationStraight reverse="false" radiant="false" anchor="middleleft">
							<Script purpose="circlelettering" value="Helligkeit (Vmag):"/>
							<Script purpose="circlelettering" value=" -1(&#x2264;)"/>
							<Script purpose="4.98" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 0"/>
							<Script purpose="4.02" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 1"/>
							<Script purpose="3.24" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 2"/>
							<Script purpose="2.61" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 3"/>
							<Script purpose="2.11" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 4"/>
							<Script purpose="1.70" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 5"/>
							<Script purpose="1.37" value="&#xf811;"/>
							<Script purpose="circlelettering" value=" 6(&#x2265;)"/>
							<Script purpose="1.10" value="&#xf811;"/>
							<Frame number="14" anchor="middleleft"/>
						</AnnotationStraight>
						<!--$legendtopcon-->
						<AnnotationStraight reverse="false" radiant="false" anchor="topmiddle">
							<Script value="Anschlussseite {@tcp+$numoff}">
							<xsl:attribute name="purpose">
								<xsl:choose>
								<xsl:when test="@tcp+$numoff &#x003e; 0">diallettering</xsl:when>
								<xsl:otherwise>none</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Script>
							<Frame number="2" anchor="topmiddle"/>
						</AnnotationStraight>
						<!--$legendbotcon-->
						<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
							<Script value="Anschlussseite {@bcp+$numoff}">
							<xsl:attribute name="purpose">
								<xsl:choose>
								<xsl:when test="@bcp+$numoff &#x003e; 0">diallettering</xsl:when>
								<xsl:otherwise>none</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Script>
							<Frame number="20" anchor="topmiddle"/>
						</AnnotationStraight>
						<!--$center-->
						<AnnotationStraight reverse="false" radiant="false">
						<xsl:attribute name="anchor">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">middleright</xsl:when>
							<xsl:otherwise>middleleft</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="textlettering">
							<xsl:choose>
								<xsl:when test="Center/lon/HMS/@neg='true'"><xsl:attribute name="value"><xsl:value-of select="-1*Center/lon/HMS/@hrs"/></xsl:attribute></xsl:when>
								<xsl:otherwise><xsl:attribute name="value"><xsl:value-of select="Center/lon/HMS/@hrs"/></xsl:attribute></xsl:otherwise>
							</xsl:choose>
								<Superscript value="h"/>
							</Script>
							<Script purpose="textlettering" value="{Center/lon/HMS/@min}">
								<Superscript value="m"/>
							</Script>
							<Script purpose="textlettering" value=" (&#x03b1;)"/>
							<Frame anchor="topmiddle">
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">6</xsl:when>
								<xsl:otherwise>4</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<AnnotationStraight reverse="false" radiant="false">
						<xsl:attribute name="anchor">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">topright</xsl:when>
							<xsl:otherwise>topleft</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="textlettering">
							<xsl:attribute name="value">
								<xsl:choose>
								<xsl:when test="Center/lat/DMS/@neg='true'">-</xsl:when>
								<xsl:otherwise>+</xsl:otherwise>
								</xsl:choose>
								<xsl:value-of select="Center/lat/DMS/@deg"/>
							</xsl:attribute>
							</Script>
							<Script purpose="textlettering" value="&#x00b0;"/>
							<Script purpose="textlettering" value="{Center/lat/DMS/@min}"/>
							<Script purpose="textlettering" value="'"/>
							<Script purpose="textlettering" value=" (&#x03b4;)"/>
							<Frame anchor="topmiddle">
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">6</xsl:when>
								<xsl:otherwise>4</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<!--$atlasequinox-->
						<AnnotationStraight reverse="false" radiant="false">
						<xsl:attribute name="anchor">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">middleleft</xsl:when>
							<xsl:otherwise>middleright</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="textlettering" value="J2000.0"/>
							<Frame anchor="topmiddle">
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">4</xsl:when>
								<xsl:otherwise>6</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<!--AtlasPage.num-->
						<AnnotationStraight reverse="false" radiant="false" anchor="middle">
							<Script purpose="titlelettering" value="{@num+$numoff}"/>
							<Frame anchor="middle">
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">15</xsl:when>
								<xsl:otherwise>13</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<!--$catdatsource-->
						<AnnotationStraight radiant="true" anchor="bottomleft">
						<xsl:attribute name="reverse">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">false</xsl:when>
							<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="guidance" value="Sternkatalogquelle: ftp://cdsarc.u-strasbg.fr/pub/cats/V/50/catalog.dat.gz"/>
							<Frame>
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">7</xsl:when>
								<xsl:otherwise>9</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:attribute name="anchor">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">bottomright</xsl:when>
								<xsl:otherwise>topleft</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<!--$trademark-->
						<AnnotationStraight radiant="true" anchor="bottomright">
						<xsl:attribute name="reverse">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">false</xsl:when>
							<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="guidance" value="2010 CHARTA CAELI"/>
							<Frame>
							<xsl:attribute name="number">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">7</xsl:when>
								<xsl:otherwise>9</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							<xsl:attribute name="anchor">
								<xsl:choose>
								<xsl:when test="@num mod 2 &#x003e; 0">topright</xsl:when>
								<xsl:otherwise>bottomleft</xsl:otherwise>
								</xsl:choose>
							</xsl:attribute>
							</Frame>
						</AnnotationStraight>
						<!--$legendlcon-->
						<AnnotationStraight radiant="true">
						<xsl:attribute name="reverse">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">false</xsl:when>
							<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="anchor">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">topmiddle</xsl:when>
							<xsl:otherwise>bottommiddle</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="diallettering" value="Anschlussseite {@pcp+$numoff}"/>
							<Frame number="7" anchor="middleleft"/>
						</AnnotationStraight>
						<!--$legendrcon-->
						<AnnotationStraight radiant="true">
						<xsl:attribute name="reverse">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">false</xsl:when>
							<xsl:otherwise>true</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
						<xsl:attribute name="anchor">
							<xsl:choose>
							<xsl:when test="@num mod 2 &#x003e; 0">bottommiddle</xsl:when>
							<xsl:otherwise>topmiddle</xsl:otherwise>
							</xsl:choose>
						</xsl:attribute>
							<Script purpose="diallettering" value="Anschlussseite {@fcp+$numoff}"/>
							<Frame number="9" anchor="middleright"/>
						</AnnotationStraight>

						<xsl:apply-templates select="Picker"/>

						<!--ADC5050-->
						<Catalog>
							<!--ftp://cdsarc.u-strasbg.fr/pub/cats/V/50/catalog.dat.gz-->
							<!--file:///c:/users/jschuck/src/astrolabe/lib/cat/5050/catalog.dat.gz-->
							<CatalogADC5050 name="BSC" url="lib/cat/5050/catalog.dat.gz">
								<CatalogADC5050Record select="true">
									<Script purpose="{{Vmag&#x003c;-1?4.98:Vmag&#x003c;0?4.02:Vmag&#x003c;1?3.24:Vmag&#x003c;2?2.61:Vmag&#x003c;3?2.11:Vmag&#x003c;4?1.70:Vmag&#x003c;5?1.37:1.10}}" value="&#xf811;"/>
								</CatalogADC5050Record>
							</CatalogADC5050>
						</Catalog>
					</HorizonEquatorial>
				</Horizon>
			</ChartStereographic>
		</Chart>
	</xsl:template>

	<xsl:template match="Center">
		<xsl:element name="Center">
		<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Picker">
		<xsl:apply-templates/>
	</xsl:template>

	<xsl:template match="SpanMeridian">
		<Circle>
			<CircleMeridian importance="canonical">
				<Begin><Angle><Rational value="-89"/></Angle></Begin>
				<End><Angle><Rational value="89"/></Angle></End>
				<Annotation>
					<AnnotationStraight anchor="{{0&#x003e;endpunktY&#x0026;&#x0026;endpunktX/endpunktY&#x003c;0.707&#x0026;&#x0026;endpunktX/endpunktY&#x003e;=-0.707?&#x0022;topmiddle&#x0022;:endpunktY&#x003e;=0&#x0026;&#x0026;endpunktX/endpunktY&#x003c;0.707&#x0026;&#x0026;endpunktX/endpunktY&#x003e;=-0.707?&#x0022;bottommiddle&#x0022;:0&#x003e;endpunktX&#x0026;&#x0026;(endpunktX/endpunktY&#x003c;-0.707||endpunktX/endpunktY&#x003e;=0.707)?&#x0022;middleright&#x0022;:&#x0022;middleleft&#x0022;}}" reverse="false" radiant="0"><Script value="{{azimutstdneg~&#x0022;true&#x0022;?azimutstd*-1:azimutstd}}" purpose="{{endpunktX&#x003e;119.7||endpunktY&#x003e;170.2||endpunktX&#x003c;-119.7||endpunktY&#x003c;-170.2?&#x0022;guidance&#x0022;:&#x0022;none&#x0022;}}"><Superscript value="h"/></Script><Script value="{{azimutstdmin}}" purpose="{{endpunktX&#x003e;119.7||endpunktY&#x003e;170.2||endpunktX&#x003c;-119.7||endpunktY&#x003c;-170.2?&#x0022;guidance&#x0022;:&#x0022;none&#x0022;}}"><Superscript value="m"/></Script></AnnotationStraight>
				</Annotation>
				<Angle><xsl:apply-templates/></Angle>
			</CircleMeridian>
		</Circle>
	</xsl:template>

	<xsl:template match="SpanParallel">
		<Circle>
			<CircleParallel importance="canonical">
				<Begin><Angle><Rational value="0"/></Angle></Begin>
				<End><Angle><Rational value="360"/></Angle></End>
				<Annotation>
					<AnnotationStraight anchor="{{0&#x003e;endpunktY&#x0026;&#x0026;endpunktX/endpunktY&#x003c;0.707&#x0026;&#x0026;endpunktX/endpunktY&#x003e;=-0.707?&#x0022;topmiddle&#x0022;:endpunktY&#x003e;=0&#x0026;&#x0026;endpunktX/endpunktY&#x003c;0.707&#x0026;&#x0026;endpunktX/endpunktY&#x003e;=-0.707?&#x0022;bottommiddle&#x0022;:0&#x003e;endpunktX&#x0026;&#x0026;(endpunktX/endpunktY&#x003c;-0.707||endpunktX/endpunktY&#x003e;=0.707)?&#x0022;middleright&#x0022;:&#x0022;middleleft&#x0022;}}" reverse="false" radiant="0"><Script value="{{h&#x00f6;hegrdneg~&#x0022;true&#x0022;?&#x0022;-&#x0022;:&#x0022;+&#x0022;}}{{h&#x00f6;hegrd}}&#x00b0;{{h&#x00f6;hegrdmin}}&#x0027;" purpose="{{endpunktX&#x003e;119.7||endpunktY&#x003e;170.2||endpunktX&#x003c;-119.7||endpunktY&#x003c;-170.2?&#x0022;guidance&#x0022;:&#x0022;none&#x0022;}}"/></AnnotationStraight>
				</Annotation>
				<Angle><xsl:apply-templates/></Angle>
			</CircleParallel>
		</Circle>
	</xsl:template>

	<xsl:template match="lon">
		<xsl:element name="lon">
		<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="lat">
		<xsl:element name="lat">
		<xsl:apply-templates/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="DMS">
		<xsl:element name="DMS">
		<xsl:copy-of select="@*"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="HMS">
		<xsl:element name="HMS">
		<xsl:copy-of select="@*"/>
		</xsl:element>
	</xsl:template>

	<xsl:template match="Rational">
		<xsl:element name="Rational">
		<xsl:copy-of select="@*"/>
		</xsl:element>
	</xsl:template>

</xsl:stylesheet>
