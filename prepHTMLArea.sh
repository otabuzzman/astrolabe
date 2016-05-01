# ./prepHTMLArea.sh <img-src> <area-href> [ <chart-picker-ratio> ]
#

# Gebrauch:
# xsltproc <xsl> <xml> | [ LANG=<country> ] ./prepHTMLArea.sh <img-src> <area-href> [ <pagesize-chart> <pagesize-picker> ]
#
# xsltproc AtlasStereographic.xsl AtlasStereographic-northern.xml |\
#	LANG=de ./prepHTMLArea.sh atlas-stereographic-northern.png "atlaspage-stereographic-northern-%03d.pdf"
#

export img_src=$1 ; shift
export area_href=$1 ; shift

_img_src=`convert -verbose $img_src /dev/null | head -1 | awk '{print $3}' | sed 's,x, ,'`
set $_img_src
export img_src_x=$1
export img_src_y=$2

gawk '
function discreteDMS( dms ) {
	d=dms<0?(dms-.000001)*-1:dms+.000001 ;
	ms=d-sprintf("%i",d) ;
	m=sprintf("%i",60*ms) ;
	s=sprintf("%.2f",60*(60*ms-m)) ;

	return sprintf("%s %s %s %s",d,m,s,dms<0?-1:1) ;
}

function discreteHMS( hms ) {
	h=(hms<0?(hms-.000001)*-1:hms+.000001)/15. ;
	ms=h-sprintf("%i",h) ;
	m=sprintf("%i",60*ms) ;
	s=sprintf("%.2f",60*(60*ms-m)) ;

	return sprintf("%s %s %s %s",h,m,s,hms<0?-1:1) ;
}

BEGIN {
	file=sprintf("chartacaeli_%s.properties",ENVIRON["LANG"])
	if(!(system(sprintf("test -f %s",file))==0)) {
		file="chartacaeli.properties" ;
	}
	while(getline<file) {
		split($0,av,/[ 	=]*=[ 	=]*/) ;
		messages[av[1]]=av[2] ;
	}

	ximg=ENVIRON["img_src_x"] ;
	yimg=ENVIRON["img_src_y"] ;

	xcpr=-1 ;
	ycpr=-1 ;

	print sprintf("<map id=\"%s\" name=\"%s\">",ENVIRON["img_src"],ENVIRON["img_src"]) ;
}

END {
	print "</map>" ;
}

# $1  : chartpagerealx (pdf)
# $2  : chartpagerealy (pdf)

NF==2 {
	xcpr=$1 ;
	ycpr=$2 ;

	r=yimg/ycpr ;
	r=r*297/210 ; # pagesize-chart=a3, pagesize-picker=a4
}

# $1  : p0xy[0]
# $2  : p0xy[1]
# $3  : p1xy[0]
# $4  : p1xy[1]
# $5  : p2xy[0]
# $6  : p2xy[1]
# $7  : p3xy[0]
# $8  : p3xy[1]
# $9  : p0eq[0]
# $10 : p0eq[1]
# $11 : p1eq[0]
# $12 : p1eq[1]
# $13 : p2eq[0]
# $14 : p2eq[1]
# $15 : p3eq[0]
# $16 : p3eq[1]
# $17 : oeq[0]
# $18 : oeq[1]
# $19 : teq[0]
# $20 : teq[1]
# $21 : beq[0]
# $22 : beq[1]
# $23 : scale
# $24 : num

NF==24 {
	split(discreteDMS($22),deb) ;
	split(discreteDMS($12),det) ;
	split(discreteHMS($9),ran) ;
	split(discreteHMS($15),raf) ;
	title=sprintf(messages["html.home.atlas.area.title"],
		deb[4]<0?deb[1]*-1:deb[1],deb[2],det[4]<0?det[1]*-1:det[1],det[2],
		ran[4]<0?ran[1]*-1:ran[1],ran[2],raf[4]<0?raf[1]*-1:raf[1],raf[2]) ;

	v0x=ximg/2.0 ;
	v0y=yimg/2.0 ;

	vp0x=$1*r ;
	vp0y=$2*r*-1 ;	
	vp0ax=v0x+vp0x ;
	vp0ay=v0y+vp0y ;

	vp1x=$3*r ;
	vp1y=$4*r*-1 ;	
	vp1ax=v0x+vp1x ;
	vp1ay=v0y+vp1y ;

	vp2x=$5*r ;
	vp2y=$6*r*-1 ;	
	vp2ax=v0x+vp2x ;
	vp2ay=v0y+vp2y ;

	vp3x=$7*r ;
	vp3y=$8*r*-1 ;	
	vp3ax=v0x+vp3x ;
	vp3ay=v0y+vp3y ;

	area_href=sprintf(ENVIRON["area_href"],$24) ;

	print sprintf("<area class=\"noborder\" title=\"%s\" shape=\"poly\" coords=\"%i,%i,%i,%i,%i,%i,%i,%i,%i,%i\" alt=\"\" href=\"%s\"/>",
		title,vp0ax,vp0ay,vp1ax,vp1ay,vp2ax,vp2ay,vp3ax,vp3ay,vp0ax,vp0ay,area_href) ;
}
'
