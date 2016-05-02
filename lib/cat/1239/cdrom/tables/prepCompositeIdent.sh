# ./prepCompositeIdent.sh <outdir>
#

outdir=$1 ; shift

[ -d $outdir ] || {
	mkdir $outdir
}

norm() {
	sed -e 's,^ *,,' -e 's, *| *,|,'	|\
	sort -t\| -nk2				|\
	gawk 'BEGIN{m=0 ; FS="|"}
		END{printf("\n")}
		{if($2==m){printf("|%s",$1)}else{printf("%s%s|%s",NR>1?"\n":"",$2,$1) ; m=$2}}'	
}

grow() {
	gawk 'BEGIN{m=0 ; FS="|"}
		END{while(++m<129999) {print m "|"}}
		{if(($1-m)>1){while(++m<$1){print m "|"} ; m--}print $0 ; m=$1}'
}

equalNF() {
	gawk -v "nf=$1" 'BEGIN{FS="|"}
		NF<=nf{printf("%s",$0) ; while(NF++<nf){printf("%s","|")} printf("\n")}'
}

orderNF() {
	case $1 in
		2) cat ;;
		3) cat ;;
		4) gawk 'BEGIN{FS="|"}
				$2~/^[a-z]/{print $1 "||" $2 "|" $3}
				$2!~/^[a-z]/{if($3~/52_Ari/||$3~/66_Gem/){print $1 "|" $3 "|" $4 "|"}else{print}}' ;;
		5) cat ;;
		6) cat ;;
	esac
}

# HIP/ HD mapping
[ -f ${outdir}/ident2.dat ] || {
	rm -f ${outdir}/ident.dat
	gunzip <ident2.doc.gz | norm | grow | equalNF 4 >${outdir}/ident2.dat
}
# HIP/ BSC (V/50) mapping
[ -f ${outdir}/ident3.dat ] || {
	rm -f ${outdir}/ident.dat
	gunzip <ident3.doc.gz | norm | grow | equalNF 4 >${outdir}/ident3.dat
}
# HIP/ Bayer and Flamsteed mapping
[ -f ${outdir}/ident4.dat ] || {
	rm -f ${outdir}/ident.dat
	gunzip <ident4.doc.gz | norm | grow | equalNF 4 | orderNF 4 >${outdir}/ident4.dat
}
# HIP/ Variable Star Name mapping
[ -f ${outdir}/ident5.dat ] || {
	rm -f ${outdir}/ident.dat
	gunzip <ident5.doc.gz | norm | grow | equalNF 2 >${outdir}/ident5.dat
}
# HIP Common Name mapping
[ -f ${outdir}/ident6.dat ] || {
	rm -f ${outdir}/ident.dat
	gunzip <ident6.doc.gz | norm | grow | equalNF 3 >${outdir}/ident6.dat
}

if test -f ${outdir}/ident.dat ; then
	cat ${outdir}/ident.dat
else
	join -t\| ${outdir}/ident2.dat ${outdir}/ident3.dat |\
		join -t\| - ${outdir}/ident4.dat |\
		join -t\| - ${outdir}/ident5.dat |\
		join -t\| - ${outdir}/ident6.dat | tee ${outdir}/ident.dat
fi
