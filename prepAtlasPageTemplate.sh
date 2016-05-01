# ./prepAtlasPageTemplate.sh <pageoffset> <evenonright> <atlas-xml>
#

atlaspage_off=$1 ; shift
atlaspage_eor=$1 ; shift
atlas_xml=$1 ; shift

xsltproc \
	--param pageoffset $atlaspage_off \
	--param evenonright $atlaspage_eor \
	--param pagetitle "'Atlas zum Sternkatalog The Bright Star Catalogue'" \
	--param legendmagnitude "'Helligkeit (Vmag):'" \
	--param atlasequinox "'J2000.0'" \
	--param catdatsource "'Sternkatalogquelle: ftp://adc.astro.umd.edu/pub/adc/archives/catalogs/5/5050/catalog.dat.gz'" \
	--param trademark "'2010 CHARTAE CAELI'" \
	--param legendtopcon "'Anschlussseite '" \
	--param legendbotcon "'Anschlussseite '" \
	--param legendlcon "'Anschlussseite '" \
	--param legendrcon "'Anschlussseite '" \
	atlaspage-template.xsl $atlas_xml
