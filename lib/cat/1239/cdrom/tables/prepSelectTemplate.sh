# ./prepSelectTemplate.sh <cat>
#

# cat==1: 1259
# cat==5: 5050
#
cat=$1 ; shift

gawk -v "cat=${cat:=1}" 'BEGIN{FS="|" ;
	greek["alf"]="&#x03b1;" ;
	greek["bet"]="&#x03b2;" ;
	greek["chi"]="&#x03c7;" ;
	greek["del"]="&#x03b4;" ;
	greek["eps"]="&#x03b5;" ;
	greek["eta"]="&#x03b7;" ;
	greek["gam"]="&#x03b3;" ;
	greek["iot"]="&#x03b9;" ;
	greek["kap"]="&#x03ba;" ;
	greek["ksi"]="&#x03be;" ;
	greek["lam"]="&#x03bb;" ;
	greek["mu."]="&#x03bc;" ;
	greek["nu."]="&#x03bd;" ;
	greek["ome"]="&#x03c9;" ;
	greek["omi"]="&#x03bf;" ;
	greek["phi"]="&#x03c6;" ;
	greek["pi."]="&#x03c0;" ;
	greek["psi"]="&#x03c8;" ;
	greek["rho"]="&#x03c1;" ;
	greek["sig"]="&#x03c3;" ;
	greek["tau"]="&#x03c4;" ;
	greek["the"]="&#x03b8;" ;
	greek["ups"]="&#x03c5;" ;
	greek["zet"]="&#x03b6;"}

function xmlText( value, element ) {
	v=sprintf("value=\"%s\"",value) ;
	if(element~/^$/){r=sprintf("<Text %s/>",v)}
	else{r=sprintf("<Text %s>%s</Text>",v,element)}
	return r ;
}

function xmlSuperscript( value, element ) {
	v=sprintf("value=\"%s\"",value) ;
	if(element~/^$/){r=sprintf("<Superscript %s/>",v)}
	else{r=sprintf("<Superscript %s>%s</Superscript>",v,element)}
	return r ;
}

function xmlAnnotationStraight( purpose, anchor, reverse, radiant, element ) {
	p=sprintf("purpose=\"%s\"",purpose) ; a=sprintf("anchor=\"%s\"",anchor) ;
	rev=sprintf("reverse=\"%s\"",reverse) ; rad=sprintf("radiant=\"%s\"",radiant) ;
	return sprintf("<AnnotationStraight %s %s %s %s>%s</AnnotationStraight>",p,a,rev,rad,element) ;
}

function xmlAnnotation( element ) {
	return sprintf("<Annotation>%s</Annotation>",element) ;
}

function xmlSelect( value, element ) {
	v=sprintf("value=\"%s\"",value) ;
	return sprintf("<Select %s>%s</Select>",v,element) ;
}

# $1:  HIP
# $2:  HD
# $3:  HD (alt.)
# $4:  HD (alt.)
# $5:  BSC
# $6:  BSC (alt.)
# $7:  BSC (alt.)
# $8:  Flamsteed
# $9:  Bayer
# $10: Bayer (alt.)
# $11: Variable
# $12: Common Name
# $13: Common Name (alt.)

# Variable, Konstellation und ggf. Common Name
$cat>0 && $11~/^[A-Z]/{
	split($11,n,/_/) ;
	print xmlSelect($cat, xmlAnnotation(xmlAnnotationStraight("bodylettering7","bottomleft","false","false",xmlText(n[1] " " n[2],""))) \
		($12~/..*/?xmlAnnotation(xmlAnnotationStraight("bodylettering0","middleleft","false","false",xmlText($12,""))):"")) ;
	next ;
}

# Bayer, Komponente, Konstellation und ggf. Common Name ODER
# Variable mit Bayer-Bezeichnung, Konstellation und ggf. Common Name
$cat>0 && ( $9~/..*/ || $11~/^[a-z]/ ){
	nbay="" ; nidx="" ;
	split($9,n,/_/) ;
	m=match(n[1],/[0-9]/) ;
	if(m>1){nbay=substr(n[1],1,m-1) ; nidx=substr(n[1],m)+0}
	else{nbay=n[1] ; nidx=0}
	if(length(nbay)==3){nbay=greek[nbay]}
	print xmlSelect($cat, xmlAnnotation(xmlAnnotationStraight("bodylettering7","bottomleft","false","false",
		xmlText(($11~/^[a-z]/?"V":"") nbay,nidx>0?xmlSuperscript(nidx,""):"") xmlText((n[3]~/..*/?" (" n[3] ") ":" ") n[2],""))) \
		($12~/..*/?xmlAnnotation(xmlAnnotationStraight("bodylettering0","middleleft","false","false",xmlText($12,""))):"")) ;
	next ;
}

# Flamsteed, Komponente, Konstellation und ggf. Common Name
$cat>0 && $8~/..*/{
	split($8,n,/_/) ;
	print xmlSelect($cat, xmlAnnotation(xmlAnnotationStraight("bodylettering7","bottomleft","false","false",xmlText(n[1] (n[3]~/..*/?" (" n[3] ") ":" ") n[2],""))) \
		($12~/..*/?xmlAnnotation(xmlAnnotationStraight("bodylettering0","middleleft","false","false",xmlText($12,""))):"")) ;
}
'
