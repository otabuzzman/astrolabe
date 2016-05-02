# ./prepAtlasXSL.sh <circle model> <circle begin> <circle end> <circle angle start> <circle angle interval> <circle angle number> <check angle low> <check angle high> [ <annotation model> ]
#

# Gebrauch:
# CircleParallel northern
# ./prepAtlasXSL.sh CircleParallel 0 360 -85 5 35 P0/theta/Rational/@value Top/theta/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{h&#x00f6;hebeide}}{{h&#x00f6;hegrd}}&#x00b0;{{h&#x00f6;hegrdmin}}&#x0027;\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"/></AnnotationStraight></Annotation>"
# CircleMeridian northern
# ./prepAtlasXSL.sh CircleMeridian 89 -89 0 6 60 P1/phi/Rational/@value P2/phi/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{azimutmathe}}{{azimutstd}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"h\"/></Script><Script value=\"{{azimutstdmin}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"m\"/></Script></AnnotationStraight></Annotation>"
# CircleParallel southern
# ./prepAtlasXSL.sh CircleParallel 0 360 85 -5 35 Top/theta/Rational/@value P0/theta/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{h&#x00f6;hebeide}}{{h&#x00f6;hegrd}}&#x00b0;{{h&#x00f6;hegrdmin}}&#x0027;\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"/></AnnotationStraight></Annotation>"
# CircleMeridian southern
# ./prepAtlasXSL.sh CircleMeridian -89 89 0 6 60 P1/phi/Rational/@value P2/phi/Rational/@value "<Annotation><AnnotationStraight anchor=\"{{aktuellerpunktrand==1?&#x0022;bottommiddle&#x0022;:aktuellerpunktrand==2?&#x0022;middleleft&#x0022;:aktuellerpunktrand==3?&#x0022;topmiddle&#x0022;:&#x0022;middleright&#x0022;}}\" reverse=\"false\" radiant=\"0\"><Script value=\"{{azimutmathe}}{{azimutstd}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"h\"/></Script><Script value=\"{{azimutstdmin}}\" purpose=\"{{aktuellerpunktrand==0?&#x0022;none&#x0022;:&#x0022;guidance&#x0022;}}\"><Superscript value=\"m\"/></Script></AnnotationStraight></Annotation>"
#

set -o errexit

export cmodel=$1 ; shift

export cbeg=$1 ; shift
export cend=$1 ; shift

export cangs=$1 ; shift
export cangi=$1 ; shift
export cangn=$1 ; shift

export chkal=$1 ; shift
export chkah=$1 ; shift

export amodel=$1 ; shift

defimp=canonical

case $cmodel in
	CircleParallel)
		echo	"<!--$cangn CircleParallel-->"
		for a in `echo "for(t=0;t<$cangn;t++){print ${cangs}+${cangi}*t;print \"\n\"}"|bc -l` ; do
			echo -n	"<xsl:if test=\"$a &#x003e;= $chkal and $a &#x003c;= $chkah\">"
			echo -n		"<Circle>"
			echo -n			"<$cmodel importance=\"$defimp\">"
			echo -n				"<Begin><Angle><Rational value=\"$cbeg\"></Rational></Angle></Begin>"
			echo -n				"<End><Angle><Rational value=\"$cend\"></Rational></Angle></End>"
			echo -n				"${amodel}"
			echo -n				"<Angle><Rational value=\"$a\"></Rational></Angle>"
			echo -n			"</$cmodel>"
			echo -n		"</Circle>"
			echo	"</xsl:if>"
		done
		;;
	CircleMeridian)
		echo	"<!--$cangn CircleMeridian-->"
		for a in `echo "for(t=0;t<$cangn;t++){print ${cangs}+${cangi}*t;print \"\n\"}"|bc -l` ; do
			echo -n "<xsl:choose>"
			echo -n		"<xsl:when test=\"$chkal &#x003e; $chkah\">"
			echo -n			"<xsl:if test=\"$a &#x003e;= $chkal or $a &#x003c;= $chkah or 360 - $chkal + $chkah &#x003e; 180\">"
			echo -n				"<Circle>"
			echo -n					"<$cmodel importance=\"$defimp\">"
			echo -n						"<Begin><Angle><Rational value=\"$cbeg\"></Rational></Angle></Begin>"
			echo -n						"<End><Angle><Rational value=\"$cend\"></Rational></Angle></End>"
			echo -n						"${amodel}"
			echo -n						"<Angle><Rational value=\"$a\"></Rational></Angle>"
			echo -n					"</$cmodel>"
			echo -n				"</Circle>"
			echo -n			"</xsl:if>"
			echo -n		"</xsl:when>"
			echo -n		"<xsl:otherwise>"
			echo -n			"<xsl:if test=\"($a &#x003e;= $chkal and $a &#x003c;= $chkah) or $chkah - $chkal &#x003e; 180\">"
			echo -n				"<Circle>"
			echo -n					"<$cmodel importance=\"$defimp\">"
			echo -n						"<Begin><Angle><Rational value=\"$cbeg\"></Rational></Angle></Begin>"
			echo -n						"<End><Angle><Rational value=\"$cend\"></Rational></Angle></End>"
			echo -n						"${amodel}"
			echo -n						"<Angle><Rational value=\"$a\"></Rational></Angle>"
			echo -n					"</$cmodel>"
			echo -n				"</Circle>"
			echo -n			"</xsl:if>"
			echo -n		"</xsl:otherwise>"
			echo	"</xsl:choose>"
		done
		;;
	*)
		exit 1
		;;
esac
