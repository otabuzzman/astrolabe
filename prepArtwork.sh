# ./prepArtwork.sh HIP|BSC <prefix> <constellationsart.fab>
#
# Gebrauch:
# ./prepArtwork.sh HIP http://bazaar.launchpad.net/~stellarium/stellarium/trunk/files/head:/skycultures/western/ /usr/src/stellarium/0.12/skycultures/western/constellationsart.fab
# ./prepArtwork.sh BSC ../stellarium/0.12/skycultures/western/ /usr/src/stellarium/0.12/skycultures/western/constellationsart.fab
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
prf=$1 ; shift

cat $1 |\
while true ; do
	read line || break ;
	[ -z "$line" ] && {
		continue
	}
	set $line
	nam=$1 ; shift
	url=$1 ; shift
	sav=$*
	printf "<Artwork name=\"%s\" url=\"%s%s\" heaven=\"true\" tonemap=\"pale\">" $nam $prf $url
	for s in 1 2 3 ; do
		echo -n "<Popper>"
		set $sav
		x=$1 ; shift
		y=$1 ; shift
		hip=`printf %g $1` ; shift
		sav=$*
		bsc=`grep "^$hip|" $map | gawk 'BEGIN{FS="|"}{print $5}'`
		cid=`gawk 'BEGIN{FS="|"} $2~/ +'$hip'$/ {print ; exit}' $cat`
		pos=`echo $cid | gawk 'BEGIN{FS="|"} {
			split($4, lon, " ")
			split($5, lat, " ")
			printf("%d %d %.2f %s %d %d %.2f", \
					lon[1], lon[2], lon[3], \
					lat[1]<0?"true":"false", lat[1]<0?lat[1]*-1:lat[1], lat[2], lat[3])}'`
		set $pos
		case $idx in
			HIP)
				LC_NUMERIC=C
				printf "<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
				"$hip" $1 $2 $3 $4 $5 $6 $7
				;;
			*)
				LC_NUMERIC=C
				printf "<Position name=\"%s\"><lon><HMS neg=\"false\" hrs=\"%d\" min=\"%d\" sec=\"%.2f\"/></lon><lat><DMS neg=\"%s\" deg=\"%d\" min=\"%d\" sec=\"%.2f\"/></lat></Position>" \
				"$bsc" $1 $2 $3 $4 $5 $6 $7
				;;
		esac
		printf "<Cartesian x=\"%d\" y=\"%d\"/>" $x $y
		echo -n "</Popper>"
	done
	echo -n "</Artwork>"
	echo -n . >&2
done
echo
