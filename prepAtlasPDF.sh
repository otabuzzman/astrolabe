# ./prepAtlasPDF.sh <atlas pdf> <page no poster> <page no picker> [ <suffix atlas> <suffix poster> <suffix picker> ]
#

# Gebrauch:
# ./prepAtlasPDF.sh atlas-stereographic.pdf 2 1
#

### set -o errexit

iscygexe() {
	EXE=$1
	DLL=cygwin1.dll

	2>/dev/null test cygcheck $EXE | grep -q $DLL
}

export atlaspdf=$1 ; shift
export pagenoposter=$1 ; shift
export pagenopicker=$1 ; shift
export suffixatlas=$1 ; shift
export suffixposter=$1 ; shift
export suffixpicker=$1 ; shift

atlasbas=`basename $atlaspdf .pdf`

posterpdf=${atlasbas}${suffixatlas}${suffixposter:=-poster}.pdf
pickerpdf=${atlasbas}${suffixatlas}${suffixpicker:=-picker}.pdf

$GS -dFirstPage=$pagenoposter -dLastPage=$pagenoposter -sDEVICE=pdfwrite -o $posterpdf $atlaspdf
$GS -dFirstPage=$pagenopicker -dLastPage=$pagenopicker -sDEVICE=pdfwrite -o $pickerpdf $atlaspdf

if iscygexe $CONVERT ; then devnull=/dev/null ; else devnull=nul ; fi
posterdim=`$CONVERT -identify $posterpdf $devnull | awk '{print $3}' | sed 's,x, ,'`
pickerdim=`$CONVERT -identify $pickerpdf $devnull | awk '{print $3}' | sed 's,x, ,'`

set $posterdim $pickerdim
test $1 -eq $3 -a $2 -eq $4
