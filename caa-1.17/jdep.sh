for j in caa/*.java ; do c=`basename $j .java|cut -c2-` ; [ -f src/caa/${c}.cpp -a -f src/caa/${c}.h ] || echo $c ; done |\
while true ; do
	read prereqcls || break
	grep -l $prereqcls src/caa/AA[!+]*.cpp | grep -v AATest | sort -u |\
		while true ; do
			read aacls || break
			echo C`basename $aacls .cpp`.class: C${prereqcls}.class
		done
done | sort
