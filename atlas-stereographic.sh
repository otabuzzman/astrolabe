# ./atlas-sterographic.sh
#

set -o errexit

atlas=`basename $0 .sh`

[ -f ${atlas}.pdf ] || {
	>&2 echo "make ${atlas}.pdf" ; exit 1
}

( exec >&2
sh prepAtlasPDF.sh ${atlas}.pdf 2 1 -northern
sh prepAtlasPDF.sh ${atlas}.pdf 4 3 -southern

sh prepAtlasIMG.sh ${atlas}-northern \
	${atlas}-northern-poster.pdf \
	${atlas}-northern-picker.pdf \
	65,105,225 686x686 .6122
# 686 ergeben sich aus chart size in Pixel mal chart view minus 4 Pixel, um Rahmen abzuschneiden.
# .6122 ergibt sich aus gewünschter Größe von 420 durch 686.
sh prepAtlasIMG.sh ${atlas}-southern \
	${atlas}-southern-poster.pdf \
	${atlas}-southern-picker.pdf \
	0,191,225 686x686 .6122 )

xsltproc Atlas.xsl ${atlas}-northern-picker.xml |\
>${atlas}-northern-picker.html LANG=de ./prepAtlasMAP.sh ${atlas}-northern.png 1.7357 "pdf/${atlas}-northern-page%03d.pdf"
# 1.7357 ergibt sich aus .6122 mal chart unit 2.834646 aus .prepferences.
xsltproc Atlas.xsl ${atlas}-southern-picker.xml |\
>${atlas}-southern-picker.html LANG=de ./prepAtlasMAP.sh ${atlas}-southern.png 1.7357 "pdf/${atlas}-southern-page%03d.pdf"

rm -f ${atlas}-northern-picker.xsl
>>${atlas}-northern-picker.xsl sh prepAtlasXSL.sh CircleParallel 0 360 -85 5 35 P0/theta/Rational/@value Top/theta/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{h&#x00f6;hebeide}}{{h&#x00f6;hegrd}}&#x00b0;{{h&#x00f6;hegrdmin}}&#x0027;\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"/></AnnotationStraight></Annotation>"
>>${atlas}-northern-picker.xsl sh prepAtlasXSL.sh CircleMeridian 89 -89 0 6 60 P1/phi/Rational/@value P2/phi/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{azimutmathe}}{{azimutstd}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"h\"/></Script><Script value=\"{{azimutstdmin}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"m\"/></Script></AnnotationStraight></Annotation>"

rm -f ${atlas}-southern-picker.xsl
>>${atlas}-southern-picker.xsl sh prepAtlasXSL.sh CircleParallel 0 360 85 -5 35 Top/theta/Rational/@value P0/theta/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{h&#x00f6;hebeide}}{{h&#x00f6;hegrd}}&#x00b0;{{h&#x00f6;hegrdmin}}&#x0027;\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"/></AnnotationStraight></Annotation>"
>>${atlas}-southern-picker.xsl sh prepAtlasXSL.sh CircleMeridian -89 89 0 6 60 P1/phi/Rational/@value P2/phi/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{azimutmathe}}{{azimutstd}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"h\"/></Script><Script value=\"{{azimutstdmin}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"m\"/></Script></AnnotationStraight></Annotation>"

>${atlas}-northern.xsl m4 ${atlas}-northern.m4
>${atlas}-southern.xsl m4 ${atlas}-southern.m4

>${atlas}-northern.xml xsltproc ${atlas}-northern.xsl ${atlas}-northern-picker.xml
>${atlas}-southern.xml xsltproc ${atlas}-southern.xsl ${atlas}-southern-picker.xml

>&2 echo "make ${atlas}-northern.pdf ${atlas}-southern.pdf"
>&2 echo "pdfsplit.sh 1 90 0 "${atlas}-northern-page%03d.pdf" ${atlas}-northern.pdf"
>&2 echo "pdfsplit.sh 1 90 0 "${atlas}-southern-page%03d.pdf" ${atlas}-southern.pdf"
