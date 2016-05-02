# ./prepAtlas.sh <atlas origin name> <page no picker> <page no poster> <picker color> <atlas img size> <atlas page offset> <atlas page url directory> <atlas page url qualifier>
#

# Gebrauch:
# numoff=0
# PATH=<path to xmlstarlet>:<path to IM convert>:$PATH LANG=de ./prepAtlas.sh atlas-stereographic 1 2 65,105,225 420 $numoff pdf -page%03d
# numoff=`xml -N ns=http://www.chartacaeli.eu/astrolabe/model -t -v count(ns:Astrolabe/ns:Chart) atlas-stereographic-northern.xml`
# PATH=<path to xmlstarlet>:<path to IM convert>:$PATH LANG=de ./prepAtlas.sh atlas-stereographic 3 4 0,191,225 420 $numoff pdf -page%03d
#

set -o errexit

iscygexe() {
	EXE=$1
	DLL=cygwin1.dll

	2>/dev/null test cygcheck $EXE | grep -q $DLL
}

export aorigname=$1 ; shift
export pnopicker=$1 ; shift
export pnoposter=$1 ; shift
export colpicker=$1 ; shift
export aimedge=$1 ; shift
export apgoff=$1 ; shift
export apgurldict=$1 ; shift
export apgurlqual=$1

sufxpicker=_picker
sufxposter=_poster

astrolabexsd=astrolabe.xsd
astrolabeprf=astrolabe.preferences
test -f $astrolabexsd -a -s $astrolabexsd -a -r $astrolabexsd
test -f $astrolabeprf -a -s $astrolabeprf -a -r $astrolabeprf

atlasmapxsl=AtlasHTML.xsl
test -f $atlasmapxsl -a -s $atlasmapxsl -a -r $atlasmapxsl

#? Astrolabe@xmlns
ns=http://www.chartacaeli.eu/astrolabe/model

atlasoriginxml=${aorigname}.xml
atlasoriginpdf=${aorigname}.pdf
test -f $atlasoriginxml -a -s $atlasoriginxml -a -r $atlasoriginxml
test -f $atlasoriginpdf -a -s $atlasoriginpdf -a -r $atlasoriginpdf

pickerchart=`xml sel -N ns=$ns -t -v "name(ns:Astrolabe/ns:Chart[$pnopicker]/descendant::*)" $atlasoriginxml`
posterchart=`xml sel -N ns=$ns -t -v "name(ns:Astrolabe/ns:Chart[$pnoposter]/descendant::*)" $atlasoriginxml`
test $pickerchart = $posterchart
atlastargetdef=`xml select -N ns=$ns -t -v ns:Astrolabe/ns:Chart[$pnopicker]/ns:$pickerchart/ns:Atlas/@picker $atlasoriginxml`
test -f $atlastargetdef -a -s $atlastargetdef -a -r $atlastargetdef

prextmp=`mktemp -u _XXXX_`

atlastargetnam=`basename $atlastargetdef .xml`
atlastargettmp=${prextmp}${atlastargetnam}
atlastargetpng=${atlastargetnam}.png
atlastargetxsl=${atlastargetnam}.xsl
atlastargethtm=${atlastargettmp}.html
atlastargetxml=${atlastargettmp}.xml
atlastargetpdf=${atlastargettmp}.pdf
test -f $atlastargetxsl -a -s $atlastargetxsl -a -r $atlastargetxsl

atlaspickernam=${atlastargettmp}$sufxpicker
atlaspickerpdf=${atlaspickernam}.pdf
atlaspickerpng=${atlaspickernam}.png

atlasposternam=${atlastargettmp}$sufxposter
atlasposterpdf=${atlasposternam}.pdf
atlasposterpng=${atlasposternam}.png

$GS -q -dBATCH -dFirstPage=$pnopicker -dLastPage=$pnopicker -sDEVICE=pdfwrite -o $atlaspickerpdf $atlasoriginpdf
$GS -q -dBATCH -dFirstPage=$pnoposter -dLastPage=$pnoposter -sDEVICE=pdfwrite -o $atlasposterpdf $atlasoriginpdf
if iscygexe convert ; then devnull=/dev/null ; else devnull=nul ; fi
pickerxy=`convert -identify $atlaspickerpdf $devnull | awk '{print $3}' | sed 's,x, ,'`
posterxy=`convert -identify $atlasposterpdf $devnull | awk '{print $3}' | sed 's,x, ,'`
set $pickerxy $posterxy
test $1 -eq $3 -a $2 -eq $4

apgv=`xml select -N ns=$ns -t -v ns:Astrolabe/ns:Chart[$pnopicker]/ns:$pickerchart/ns:ChartPage/@view $atlasoriginxml`
apge=`echo "if($1>$2) ${2}*${apgv}/100-4 else ${1}*${apgv}/100-4" | bc`
convert $atlaspickerpdf $atlaspickerpng
convert $atlasposterpdf $atlasposterpng
convert $atlaspickerpng -gravity Center -crop ${apge}x${apge}+0+0 $atlaspickerpng
convert $atlasposterpng -gravity Center -crop ${apge}x${apge}+0+0 $atlasposterpng
convert $atlaspickerpng -fill "rgb($colpicker)" +opaque transparent - |\
convert $atlasposterpng - -composite - |\
convert - -resize ${aimedge}x${aimedge} $atlastargetpng

#? ${pickerchart}@name
psunit=`xml select -t -v "preferences/root/node[@name='astrolabe']/node[@name='"$pickerchart"']/map/entry[@key='unit']/@value" $astrolabeprf`
aimpgr=`echo "scale=4;${aimedge}/${apge}*$psunit" | bc -l`
xml transform $atlasmapxsl $atlastargetdef |\
>$atlastargethtm ./prepAtlasHTML.sh $atlastargetpng $aimpgr $apgurldict/${atlastargetnam}${apgurlqual}.pdf

>$atlastargetxml xml transform $atlastargetxsl -p numoff=$apgoff $atlastargetdef
xml validate -q --xsd $astrolabexsd $atlastargetxml

num=`xml select -N ns=$ns -t -v 'count(ns:Astrolabe/ns:Chart)' $atlastargetxml`
>&2 echo "*** make $atlastargetpdf"
>&2 echo "*** ./pdfsplit.sh 1 $num 0 $apgurldict/${atlastargetnam}${apgurlqual}.pdf $atlastargetpdf"
