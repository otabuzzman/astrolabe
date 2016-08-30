# ASTROLABE
A tool to make star charts. Astrolabe started as an idea for an easy to use tool to draw nice star charts in large formats. Early tries had been done with [Gnuplot](http://www.gnuplot.info/) followed by [Postcript](https://www.adobe.com/products/postscript/pdfs/PLRM.pdf). While the project grew the need for debugging arose what finally led to Java. The repository is some sort of chronological documentation of Astrolabe's gradual development. It is not intended to build.

Contributions to Astrolabe end with the rise of [Charta Caeli](https://github.com/otabuzzman/chartacaeli). The successor project is (hopefully) sufficent documented to build and run.

## Cues on repository
Version control during development of Astrolabe has been done rather pragmatically. Whenever a status was reached that was worth keeping, a snapshot of the source folder was captured in a ZIP file. Here is how the ZIP files came to Git.

#### 1. Initialize
Variables in commands below with sample values.
```
srctop=~/Desktop/astrolabe
srcact=${srctop}/_a
srcnxt=${srctop}/_b
reptop=~/src
repact=${reptop}/astrolabe
```
- Create repository `astrolabe` on GitHub
- Create folder `$repact`
- Initialize Git in `$repact`
- Create folders `$srcact` and `$srcnxt`
- Make first contribution to `astrolabe`

Sample ad-hoc shell commands for steps above.
```
# On Windows run Cygwin bash
[ -d $repact ] || mkdir $repact
# On Windows switch to Git Shell
cd ~/src/astrolabe
git init
git remote add origin https://github.com/otabuzzman/astrolabe.git
# On Windows switch back to Cygwin
[ -d $srcact ] || mkdir $srcact
[ -d $srcnxt ] || mkdir $srcnxt
cp $srctop/astrolabe.plt $srcnxt
cp $srcnxt/astrolabe.plt $repact
# On Windows switch to Git Shell
git add astrolabe.plt
git commit -m 'Initial issue @1063086109'
git push -u origin master
```

#### 2. Contribute ZIP files
Foreach ZIP file (see section Changelog below) in alphabetical ascending order (corresponds to chronologial ascending as well) do
- Empty `$srcact`
- Move files from `$srcnxt` to `$srcact`
- Unzip file to `$srcnxt`
- Delete non-essential files and folders from `$srcnxt`
- Foreach new file in `$srcnxt` do

	```
	# Replace german "Nur" by correspondig word in your language
	diff -rq $srcact $srcnxt | grep Nur | grep -v _a[:/]
	diff -rq $srcact $srcnxt | grep Nur | grep -v _a[:/] | sed -e 's,.*_b[:/] *,,' -e 's,\.$,,'
	```
	- If file is essential but external or intermediate add to `.gitignore`
	- Adjust pattern in `.gitignore` if excludes new essential file 
	- If file is folder repeat previous step for contained files
	- Update `.gitattribues` with new files if appropriate (e.g. binaries like PDF's and JPG's)
- Foreach obsolete file in `$srcact` do

	```
	# Replace german "Nur" by correspondig word in your language
	diff -rq $srcact $srcnxt | grep Nur | grep -v _b[:/]
	diff -rq $srcact $srcnxt | grep Nur | grep -v _b[:/] | sed -e 's,.*_a[:/] *,,' -e 's,\.$,,'
	```
	- If file in repository run Git command `git rm <file>`
	- If file in `.gitignore` take off and remove from `$repact` if present
	- If file type in `.gitattributes` consider removal
- Copy `${srcnxt}/*` (new and modified) to `$repact`

	```
	# Find modified files to select what to copy. Mind the "Nur".
	diff -rq $srcact $srcnxt | grep -v Nur | sed 's,.*_b/,,'
	```
- Run Git command `git status -s`
- Check status and adjust if necessary
- Run Git command `git add <modified files>`
- Run Git command `git commit -m '<commit string>'`
- Push to GitHub `git push -u origin master`
- In case of errors in [astrolabe on GitHub](https://github.com/otabuzzman/astrolabe) remove last commit and repeat processing of current ZIP file

	```
	git reset --hard HEAD^
	git push -f origin master
	```

#### 3. Finalize
- Copy `README.md` to `$repdir`
- Run Git command `git add README.md`
- Run Git command `git commit -m 'Final issue.'`
- Push to GitHub with `git push -u origin master`

Sample ad-hoc shell commands for steps above.
```
# On Windows in Cygwin bash
cp $srctop/README.md $repact
# On Windows switch to Git Shell
git add README.md
git commit -m 'Final issue'
git push -u origin master
```

#### Helpful links
- [The Pro Git Book](https://git-scm.com/book/en/v2)
- [On undoing, fixing, or removing commits in git](http://sethrobertson.github.io/GitFixUm/fixup.html)

## Changelog
|Snapshot                  |Commit string            |
|--------------------------|-------------------------|
|astrolabe.plt             |Initial issue @1063086109|
|astrolabe_2005-02-23.zip  |Issue no. 2 @1109159580  |
|astrolabe_2006-05-19.zip  |Issue no. 3 @1148058321  |
|astrolabe_2006-08-13.zip  |Issue no. 4 @1155480671  |
|astrolabe_2006-08-25.zip  |Issue no. 5 @1156500432  |
|astrolabe_2007-01-22.zip  |Issue no. 6 @1169501761  |
|astrolabe_2007-03-21.zip  |Issue no. 7 @1174489386  |
|astrolabe_2007-08-27.zip  |Issue no. 8 @1189271351  |
|astrolabe_2007-09-08.zip  |Issue no. 9 @1190891508  |
|astrolabe_2007-10-04.zip  |Issue no. 10 @1191496853 |
|astrolabe_2007-10-30.zip  |Issue no. 11 @1193766488 |
|astrolabe_2007-11-13.zip  |Issue no. 12 @1194960031 |
|astrolabe_2008-01-20.zip  |Issue no. 13 @1200818481 |
|astrolabe_2008-01-30.zip  |Issue no. 14 @1201701240 |
|astrolabe_2008-02-07.zip  |Issue no. 15 @1202399864 |
|astrolabe_2008-02-11.zip  |Issue no. 16 @1202745301 |
|astrolabe_2008-03-11.zip  |Issue no. 17 @1205232585 |
|astrolabe_2008-03-21.zip  |Issue no. 18 @1206140608 |
|astrolabe_2008-04-13.zip  |Issue no. 19 @1208084046 |
|astrolabe_2008-05-18.zip  |Issue no. 20 @1211144090 |
|astrolabe_2008-07-22.zip  |Issue no. 21 @1216719625 |
|astrolabe_2008-09-30.zip  |Issue no. 22 @1222767707 |
|astrolabe_2008-11-10.zip  |Issue no. 23 @1226350952 |
|astrolabe_2008-11-19.zip  |Issue no. 24 @1227124240 |
|astrolabe_2009-11-22.zip  |Issue no. 25 @1258915828 |
|astrolabe_2009-12-16.zip  |Issue no. 26 @1260983202 |
|astrolabe_2010-01-03.zip  |Issue no. 27 @1262527220 |
|astrolabe_2010-01-10.zip  |Issue no. 28 @1263132748 |
|astrolabe_2010-01-28.zip  |Issue no. 29 @1265306618 |
|astrolabe_2010-02-19.zip  |Issue no. 30 @1266593313 |
|astrolabe_2010-02-23.zip  |Issue no. 31 @1266947168 |
|astrolabe_2010-03-10.zip  |Issue no. 32 @1268238635 |
|astrolabe_2010-03-19.zip  |Issue no. 33 @1268986983 |
|astrolabe_2010-09-08.zip  |Issue no. 34 @1283950581 |
|astrolabe_2010-10-08.zip  |Issue no. 35 @1286550606 |
|astrolabe_2010-11-01.zip  |Issue no. 36 @1288637675 |
|astrolabe_2010-11-20.zip  |Issue no. 37 @1290279504 |
|astrolabe_2010-12-19.zip  |Issue no. 38 @1292695601 |
|astrolabe_2011-02-13.zip  |Issue no. 39 @1297619580 |
|astrolabe_2011-02-26.zip  |Issue no. 40 @1298740032 |
|astrolabe_2011-06-14.zip  |Issue no. 41 @1308075484 |
|astrolabe_2011-07-18.zip  |Issue no. 42 @1311001044 |
|astrolabe_2011-07-24.zip  |Issue no. 43 @1311531220 |
|astrolabe_2011-09-06.zip  |Issue no. 44 @1315291991 |
|astrolabe_2011-10-06.zip  |Issue no. 45 @1317898316 |
|astrolabe_2011-11-14.zip  |Issue no. 46 @1321275970 |
|astrolabe_2011-11-25.zip  |Issue no. 47 @1322252219 |
|astrolabe_2012-01-18.zip  |Issue no. 48 @1326874560 |
|astrolabe_2012-02-08.zip  |Issue no. 49 @1328690548 |
|astrolabe_2012-02-20.zip  |Issue no. 50 @1329770271 |
|astrolabe_2012-03-04.zip  |Issue no. 51 @1330886439 |
|astrolabe_2012-03-19.zip  |Issue no. 52 @1332150741 |
|astrolabe_2012-05-17.zip  |Issue no. 53 @1337264806 |
|astrolabe_2012-06-12.zip  |Issue no. 54 @1339531427 |
|astrolabe_2012-06-26.zip  |Issue no. 55 @1340710520 |
|astrolabe_2012-07-05.zip  |Issue no. 56 @1341495452 |
|astrolabe_2012-07-11.zip  |Issue no. 57 @1342099925 |
|astrolabe_2012-07-20.zip  |Issue no. 58 @1342808581 |
|astrolabe_2013-06-09.zip  |Issue no. 59 @1370796054 |
|astrolabe_2013-07-31.zip  |Issue no. 60 @1375284891 |
|astrolabe_2013-09-14.zip  |Issue no. 61 @1379161847 |
|astrolabe_2014-01-26.zip  |Issue no. 62 @1390738904 |
|astrolabe_2014-04-17.zip  |Issue no. 63 @1397722213 |
|astrolabe_2014-07-25.zip  |Issue no. 64 @1406317256 |
|astrolabe_2014-09-09.zip  |Issue no. 65 @1410285912 |
|astrolabe_2014-09-19.zip  |Issue no. 66 @1411156163 |
|astrolabe_2014-10-16.zip  |Issue no. 67 @1413490269 |
|astrolabe_2014-10-17.zip  |Issue no. 68 @1413556783 |
|astrolabe_2014-12-13.zip  |Issue no. 69 @1418495302 |
|astrolabe_2014-12-15.zip  |Issue no. 70 @1418675072 |
|astrolabe_2014-12-21.zip  |Issue no. 71 @1419183165 |
|astrolabe_2015-01-07.zip  |Issue no. 72 @1420655702 |
|astrolabe_2015-03-31.zip  |Issue no. 73 @1427830117 |
|astrolabe_2015-05-19.zip  |Issue no. 74 @1432056729 |
|README.md                 |Final issue              |
