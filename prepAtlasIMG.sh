# ./prepAtlasIMG.sh <atlas> <poster pdf> <picker pdf> <picker color> <atlas img size> <atlas img scale>
#

# Gebrauch:
# ./prepAtlasIMG.sh atlas-stereographic-northern \
# 	atlas-stereographic-northern-poster.pdf \
# 	atlas-stereographic-northern-picker.pdf \
# 	255,255,255 686x686 .6122
#

### set -o errexit

iscygexe() {
	EXE=$1
	DLL=cygwin1.dll

	2>/dev/null test cygcheck $EXE | grep -q $DLL
}

export atlas=$1 ; shift
export posterpdf=$1 ; shift
export pickerpdf=$1 ; shift
export pickercolor=$1 ; shift
export imgsize=$1 ; shift
export imgscale=$1 ; shift

atlaspng=${atlas}.png

posterbas=`basename $posterpdf .pdf`
posterpng=${posterbas}.png
pickerbas=`basename $pickerpdf .pdf`
pickerpng=${pickerbas}.png

set `echo $imgsize | sed 's,x, ,'`
imgx=$1 ; shift
imgy=$1 ; shift
scalex=`echo "scale=4;${imgx}*$imgscale" | bc`
scaley=`echo "scale=4;${imgy}*$imgscale" | bc`

$CONVERT $posterpdf $posterpng
$CONVERT $pickerpdf $pickerpng

$CONVERT $posterpng -gravity Center -crop ${imgx}x${imgy}+0+0 $posterpng
$CONVERT $posterpng -resize ${scalex}x${scaley} $posterpng

$CONVERT $pickerpng -fill "rgb($pickercolor)" +opaque transparent $pickerpng
$CONVERT $pickerpng -gravity Center -crop ${imgx}x${imgy}+0+0 $pickerpng
$CONVERT $pickerpng -resize ${scalex}x${scaley} $pickerpng

$CONVERT $posterpng $pickerpng -composite $atlaspng
