<?xml version="1.0" encoding="UTF-8"?>

<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xmlns:fo="http://www.w3.org/1999/XSL/Format"
	xmlns:ns="http://www.chartaecaeli.eu/astrolabe/model"
	exclude-result-prefixes="ns">

	<xsl:output method="xml" encoding="UTF-8"/>

	<xsl:template match="/ns:Atlas">
	<Astrolabe xmlns="http://www.chartaecaeli.eu/astrolabe/model" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xsi:schemaLocation="http://www.chartaecaeli.eu/astrolabe/model
astrolabe.xsd">
		<Epoch>
			<Calendar>
				<YMD y="2010" m="1" d="1"/>
			</Calendar>
		</Epoch>
		<xsl:if test="$evenonright = 1">
			<xsl:apply-templates select="ns:AtlasPage">
				<xsl:with-param name="r" select="0"/>
				<xsl:with-param name="l" select="1"/>
			</xsl:apply-templates>
		</xsl:if>
		<xsl:if test="not($evenonright = 1)">
			<xsl:apply-templates select="ns:AtlasPage">
				<xsl:with-param name="r" select="1"/>
				<xsl:with-param name="l" select="0"/>
			</xsl:apply-templates>
		</xsl:if>
	</Astrolabe>
	</xsl:template>

	<xsl:template match="ns:AtlasPage">
		<xsl:param name="r"/>
		<xsl:param name="l"/>

		<xsl:variable name="num" select="$pageoffset+position()"/>

		<xsl:comment>page <xsl:value-of select="$num"/></xsl:comment>

	<Chart xmlns="http://www.chartaecaeli.eu/astrolabe/model">
		<ChartStereographic pagesize="a4" viewport="100" layout="atlaspage" scale="100" hemisphere="northern">
			<Origin>
				<phi>
					<DMS deg="0" min="0" sec="0.0"/>
				</phi>
				<theta>
					<DMS deg="90" min="0" sec="0.0"/>
				</theta>
			</Origin>
			<Horizon>
				<HorizonLocal practicality="always">
					<Latitude>
						<DMS deg="90" min="0" sec="0.0"/>
					</Latitude>
					<Circle>
						<CircleParallel importance="canonical">
							<Angle>
								<DMS deg="0" min="0" sec="0.0"/>
							</Angle>
							<Begin>
								<Immediate>
									<DMS deg="0" min="0" sec="0.00"/>
								</Immediate>
							</Begin>
							<End>
								<Immediate>
									<DMS deg="0" min="0" sec="0.01"/>
								</Immediate>
							</End>
							<!--$pagetitle-->
							<Annotation>
								<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
									<!--"CHARTAE CAELI"-->
									<Text purpose="titlelettering" value="&#xf743;&#xf748;&#xf741;&#xf752;&#xf754;&#xf741;&#xf745; &#xf743;&#xf741;&#xf745;&#xf74c;&#xf749;"/>
									<Frame number="2" anchor="bottommiddle"/>
								</AnnotationStraight>
							</Annotation>
							<Annotation>
								<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
									<Text purpose="textlettering" value="{$pagetitle}"/>
									<Frame number="5" anchor="middle"/>
								</AnnotationStraight>
							</Annotation>
							<!--$legendmagnitude-->
							<Annotation>
								<AnnotationStraight anchor="middleleft" reverse="false" radiant="false">
									<Text purpose="circlelettering" value="{$legendmagnitude}"/>
									<Text purpose="circlelettering" value=" -1 "/>
									<Text purpose="mag-1" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 0 "/>
									<Text purpose="mag0" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 1 "/>
									<Text purpose="mag1" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 2 "/>
									<Text purpose="mag2" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 3 "/>
									<Text purpose="mag3" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 4 "/>
									<Text purpose="mag4" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 5 "/>
									<Text purpose="mag5" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 6 "/>
									<Text purpose="mag6" value="&#xf811;"/>
									<Text purpose="circlelettering" value=" 7 "/>
									<Text purpose="mag7" value="&#xf811;"/>
									<Frame number="14" anchor="middleleft"/>
								</AnnotationStraight>
							</Annotation>
		<!--$legendtopcon-->
		<xsl:if test="@tcp > 0">
		<Annotation>
			<AnnotationStraight reverse="false" radiant="false" anchor="topmiddle">
				<Text purpose="diallettering" value="{$legendtopcon}"/>
				<Text purpose="diallettering" value="{$pageoffset+@tcp}"/>
				<Frame number="2" anchor="topmiddle"/>
			</AnnotationStraight>
		</Annotation>
		</xsl:if>
		<!--$legendbotcon-->
		<xsl:if test="@bcp > 0">
		<Annotation>
			<AnnotationStraight reverse="false" radiant="false" anchor="bottommiddle">
				<Text purpose="diallettering" value="{$legendbotcon}"/>
				<Text purpose="diallettering" value="{$pageoffset+@bcp}"/>
				<Frame number="20" anchor="topmiddle"/>
			</AnnotationStraight>
		</Annotation>
		</xsl:if>
		<!--right side-->
		<xsl:if test="$num mod 2 = $r">
			<xsl:apply-templates select="ns:Origin">
				<xsl:with-param name="anchor" select="'right'"/>
				<xsl:with-param name="number" select="6"/>
			</xsl:apply-templates>
			<!--$atlasequinox-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="false" anchor="middleleft">
					<Text purpose="textlettering" value="{$atlasequinox}"/>
					<Frame number="4" anchor="topmiddle"/>
				</AnnotationStraight>
			</Annotation>
			<!--AtlasPage.num-->
			<Annotation xmlns="http://www.chartaecaeli.eu/astrolabe/model">
				<AnnotationStraight reverse="false" radiant="false" anchor="middle">
					<Text purpose="titlelettering" value="{$num}"/>
					<Frame number="15" anchor="middle"/>
				</AnnotationStraight>
			</Annotation>
			<!--$catdatsource-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="true" anchor="bottomleft">
					<Text purpose="guidance" value="{$catdatsource}"/>
					<Frame number="7" anchor="bottomright"/>
				</AnnotationStraight>
			</Annotation>
			<!--$trademark-->
			<Annotation xmlns="http://www.chartaecaeli.eu/astrolabe/model">
				<AnnotationStraight reverse="false" radiant="true" anchor="bottomright">
					<Text purpose="guidance" value="{$trademark}"/>
					<Frame number="7" anchor="topright"/>
				</AnnotationStraight>
			</Annotation>
			<!--$legendlcon-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="true" anchor="topmiddle">
					<Text purpose="diallettering" value="{$legendlcon}"/>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg > ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@fcp}"/>
					</xsl:if>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg &lt; ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@pcp}"/>
					</xsl:if>
					<Frame number="7" anchor="middleleft"/>
				</AnnotationStraight>
			</Annotation>
			<!--$legendrcon-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="true" anchor="bottommiddle">
					<Text purpose="diallettering" value="{$legendrcon}"/>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg > ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@pcp}"/>
					</xsl:if>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg &lt; ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@fcp}"/>
					</xsl:if>
					<Frame number="9" anchor="middleright"/>
				</AnnotationStraight>
			</Annotation>
		</xsl:if>
		<!--left side-->
		<xsl:if test="$num mod 2 = $l">
			<xsl:apply-templates name="ns:Origin">
				<xsl:with-param name="anchor" select="'left'"/>
				<xsl:with-param name="number" select="4"/>
			</xsl:apply-templates>
			<!--$atlasequinox-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="false" anchor="middleright">
					<Text purpose="textlettering" value="{$atlasequinox}"/>
					<Frame number="6" anchor="topmiddle"/>
				</AnnotationStraight>
			</Annotation>
			<!--AtlasPage.num-->
			<Annotation>
				<AnnotationStraight reverse="false" radiant="false" anchor="middle">
					<Text purpose="titlelettering" value="{$num}"/>
					<Frame number="13" anchor="middle"/>
				</AnnotationStraight>
			</Annotation>
			<!--$catdatsource-->
			<Annotation>
				<AnnotationStraight reverse="true" radiant="true" anchor="bottomleft">
					<Text purpose="guidance" value="{$catdatsource}"/>
					<Frame number="9" anchor="topleft"/>
				</AnnotationStraight>
			</Annotation>
			<!--$trademark-->
			<Annotation>
				<AnnotationStraight reverse="true" radiant="true" anchor="bottomright">
					<Text purpose="guidance" value="{$trademark}"/>
					<Frame number="9" anchor="bottomleft"/>
				</AnnotationStraight>
			</Annotation>
			<!--$legendlcon-->
			<Annotation>
				<AnnotationStraight reverse="true" radiant="true" anchor="bottommiddle">
					<Text purpose="diallettering" value="{$legendlcon}"/>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg > ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@fcp}"/>
					</xsl:if>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg &lt; ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@pcp}"/>
					</xsl:if>
					<Frame number="7" anchor="middleleft"/>
				</AnnotationStraight>
			</Annotation>
			<!--$legendrcon-->
			<Annotation>
				<AnnotationStraight reverse="true" radiant="true" anchor="topmiddle">
					<Text purpose="diallettering" value="{$legendrcon}"/>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg > ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@pcp}"/>
					</xsl:if>
					<xsl:if test="ns:Origin/ns:theta/ns:DMS/@deg &lt; ns:P0/ns:theta/ns:Rational/@value">
						<Text purpose="diallettering" value="{$pageoffset+@fcp}"/>
					</xsl:if>
					<Frame number="9" anchor="middleright"/>
				</AnnotationStraight>
			</Annotation>
		</xsl:if>
						</CircleParallel>
					</Circle>
				</HorizonLocal>
			</Horizon>
		</ChartStereographic>
	</Chart>
	</xsl:template>

	<xsl:template match="ns:Origin">
		<xsl:param name="anchor"/>
		<xsl:param name="number"/>

	<Annotation xmlns="http://www.chartaecaeli.eu/astrolabe/model">
		<AnnotationStraight reverse="false" radiant="false" anchor="middle{$anchor}">
			<Text purpose="textlettering" value="{ns:phi/ns:HMS/@hrs}">
				<Superscript value="h"/>
			</Text>
			<Text purpose="textlettering" value="{ns:phi/ns:HMS/@min}">
				<Superscript value="m"/>
			</Text>
			<Text purpose="textlettering" value=" (&#x03b1;)"/>
			<Frame number="{$number}" anchor="topmiddle"/>
		</AnnotationStraight>
	</Annotation>
	<Annotation xmlns="http://www.chartaecaeli.eu/astrolabe/model">
		<AnnotationStraight reverse="false" radiant="false" anchor="top{$anchor}">
			<Text purpose="textlettering" value="{ns:theta/ns:DMS/@deg}"/>
			<Text purpose="textlettering" value="&#x00b0;"/>
			<Text purpose="textlettering" value="{ns:theta/ns:DMS/@min}"/>
			<Text purpose="textlettering" value="'"/>
			<Text purpose="textlettering" value=" (&#x03b4;)"/>
			<Frame number="{$number}" anchor="topmiddle"/>
		</AnnotationStraight>
	</Annotation>
	</xsl:template>
</xsl:stylesheet>
