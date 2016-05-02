# ./prepSign.sh HIP|BSC <constellationship.fab>
#
# Gebrauch:
# ./prepSign.sh HIP /usr/src/stellarium/0.12/skycultures/western/constellationship.fab
#

fil=lib/cat/1239/hip_main.dat.gz
[ -f $fil ] || {
	exit
}
cat=`mktemp`
gunzip <$fil >$cat

map=lib/cat/1239/cdrom/tables/tmp/ident.dat
[ -f $map ] || {
	exit
}

idx=$1 ; shift

cat $1 |\
while true ; do
	read line || break ;
	[ -z "$line" ] && {
		continue
	}
	set $line
	nam=$1 ; shift
	num=$1 ; shift
	printf "<Sign name=\"%s\" nature=\"constellation\">" $nam
	ahip=$1 ; shift
	bhip=$1 ; shift
	save=$*
	absc=`grep "^$ahip|" $map | gawk 'BEGIN{FS="|"}{print $5}'`
	bbsc=`grep "^$bhip|" $map | gawk 'BEGIN{FS="|"}{print $5}'`
	case $idx in
	HIP)
		a=$ahip
		b=$bhip ;;
	*)
		a=$absc
		b=$bbsc ;;
	esac
	apos=`gawk 'BEGIN{FS="|"} $2~/ +'$ahip'$/ {
		split($4, lon, " ")
		split($5, lat, " ")
		printf("%d %d %.2f %s %d %d %.2f", \
				lon[1], lon[2], lon[3], \
				lat[1]<0?"true":"false", lat[1]<0?lat[1]*-1:lat[1], lat[2], lat[3])}' $cat`
	set $apos
	LC_NUMERIC=C ; printf \
	"<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
	$a $1 $2 $3 $4 $5 $6 $7
	bpos=`gawk 'BEGIN{FS="|"} $2~/ +'$bhip'$/ {
		split($4, lon, " ")
		split($5, lat, " ")
		printf("%d %d %.2f %s %d %d %.2f", \
				lon[1], lon[2], lon[3], \
				lat[1]<0?"true":"false", lat[1]<0?lat[1]*-1:lat[1], lat[2], lat[3])}' $cat`
	set $bpos
	LC_NUMERIC=C ; printf \
	"<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
	$b $1 $2 $3 $4 $5 $6 $7
	for (( i=1 ; i<num ; i++ )) ; do
		echo -n "<Limb>"
		set $save
		ahip=$1 ; shift
		bhip=$1 ; shift
		save=$*
		absc=`grep "^$ahip|" $map | gawk 'BEGIN{FS="|"}{print $5}'`
		bbsc=`grep "^$bhip|" $map | gawk 'BEGIN{FS="|"}{print $5}'`
		case $idx in
		HIP)
			a=$ahip
			b=$bhip ;;
		*)
			a=$absc
			b=$bbsc ;;
		esac
		alon=`gawk 'BEGIN{FS="|"} $2~/ +'$ahip'$/ {split($4, lon, " ") ; printf("%d %d %.2f", lon[1], lon[2], lon[3])}' $cat`
		alat=`gawk 'BEGIN{FS="|"} $2~/ +'$ahip'$/ {split($5, lat, " ") ; printf("%s %d %d %.2f", lat[1]<0?"true":"false", lat[1]<0?lat[1]*-1:lat[1], lat[2], lat[3])}' $cat`
		set $alon $alat
		LC_NUMERIC=C ; printf \
		"<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
		$a $1 $2 $3 $4 $5 $6 $7
		blon=`gawk 'BEGIN{FS="|"} $2~/ +'$bhip'$/ {split($4, lon, " ") ; printf("%d %d %.2f", lon[1], lon[2], lon[3])}' $cat`
		blat=`gawk 'BEGIN{FS="|"} $2~/ +'$bhip'$/ {split($5, lat, " ") ; printf("%s %d %d %.2f", lat[1]<0?"true":"false", lat[1]<0?lat[1]*-1:lat[1], lat[2], lat[3])}' $cat`
		set $blon $blat
		LC_NUMERIC=C ; printf \
		"<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
		$b $1 $2 $3 $4 $5 $6 $7
	done
	for (( i=1 ; i<num ; i++ )) ; do
		echo -n "</Limb>"
	done
	echo -n "</Sign>"
	echo -n . >&2
done
echo
