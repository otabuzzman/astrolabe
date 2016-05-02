# ./prepArtwork1239.sh <constellationsart.fab>
#
# Gebrauch:
# ./prepArtwork1239.sh /usr/src/stellarium/0.12/skycultures/western/constellationsart.fab
#

fil=lib/cat/1239/hip_main.dat.gz
[ -f $fil ] || {
	exit
}
cat=`mktemp`
gunzip <$fil >$cat

cat $1 |\
while true ; do
	read line || break ;
	[ -z "$line" ] && {
		continue
	}
	set $line
	nam=$1
	shift
	shift
	for s in 1 2 3 ; do
		shift
		shift
		hip=`printf %g $1` ; shift	
		cid=`gawk 'BEGIN{FS="|"} $2~/ +'$hip'$/ {print $0 ; exit}' $cat`
		echo "$cid"
	done
done
