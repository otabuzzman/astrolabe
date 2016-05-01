# ./pdfsplit.sh <page-first> <page-last> <page-offset> <out> <pdf>
#

f=$1 ; shift
l=$1 ; shift
o=$1 ; shift
out=$1 ; shift
pdf=$1

while true ; do
	${GS:-gswin32c} \
		-dFirstPage=$f \
		-dLastPage=$f \
		-sDEVICE=pdfwrite -o ${pdf}- $pdf
	
	n=`expr $f + $o`
	mv ${pdf}- `printf $out $n`
	
	if test $f -eq $l ; then
		break
	fi
	
	f=`expr $f + 1`
done

