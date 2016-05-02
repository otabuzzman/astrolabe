# ./pdfsplit.sh <first page> <last page> <page offset> <autput page file name template> <pdf>
#

set -o errexit

export page1st=$1 ; shift
export pageNth=$1 ; shift
export pageoff=$1 ; shift
export pagefmt=$1 ; shift
export pdf=$1 ; shift

n=`expr $page1st + $pageoff`

while true ; do
	pageout=`printf $pagefmt $n`

	$GS \
		-dFirstPage=$n \
		-dLastPage=$n \
		-sDEVICE=pdfwrite -o $pageout $pdf
	
	if test $n -eq $pageNth ; then
		break
	fi

	n=`expr $n + $pageoff + 1`
done
