awk 'BEGIN{
#        "<de>:<ra>:<text scale>:<constellation name>"
c["AND"]="0:0:1:Andromeda";
c["ANT"]="0:0:1:Antila";
c["APS"]="0:0:1:Apus";
c["AQR"]="0:0:1:Aquarius";
c["AQL"]="0:0:1:Aquila";
c["ARA"]="0:0:1:Ara";
c["ARI"]="0:0:1:Aries";
c["AUR"]="0:0:1:Auriga";
c["BOO"]="0:0:1:Bootes";
c["CAE"]="0:0:1:Caelum";
c["CAM"]="0:0:1:Camelopardis";
c["CNC"]="0:0:1:Cancer";
c["CVN"]="0:0:1:Canes Venatici";
c["CMA"]="0:0:1:Canis Major";
c["CMI"]="0:0:1:Canis Minor";
c["CAP"]="0:0:1:Capricornus";
c["CAR"]="0:0:1:Carina";
c["CAS"]="0:0:1:Cassiopeia";
c["CEN"]="0:0:1:Centaurus";
c["CEP"]="0:0:1:Cepheus";
c["CET"]="0:0:1:Cetus";
c["CHA"]="0:0:1:Chamaeleon";
c["CIR"]="0:0:1:Circinus";
c["COL"]="0:0:1:Columba";
c["COM"]="0:0:1:Coma Berenices";
c["CRA"]="0:0:1:Corona Australis";
c["CRB"]="0:0:1:Corona Borealis";
c["CRV"]="0:0:1:Corvus";
c["CRT"]="0:0:1:Crater";
c["CRU"]="0:0:1:Crux";
c["CYG"]="0:0:1:Cygnus";
c["DEL"]="0:0:1:Delphinus";
c["DOR"]="0:0:1:Dorado";
c["DRA"]="0:0:1:Draco";
c["EQU"]="0:0:1:Equuleus";
c["ERI"]="0:0:1:Eridanus";
c["FOR"]="0:0:1:Fornax";
c["GEM"]="0:0:1:Gemini";
c["GRU"]="0:0:1:Grus";
c["HER"]="0:0:1:Hercules";
c["HOR"]="0:0:1:Horologium";
c["HYA"]="0:0:1:Hydra";
c["HYI"]="0:0:1:Hydrus";
c["IND"]="0:0:1:Indus";
c["LAC"]="0:0:1:Lacerta";
c["LEO"]="0:0:1:Leo";
c["LMI"]="0:0:1:Leo Minor";
c["LEP"]="0:0:1:Lepus";
c["LIB"]="0:0:1:Libra";
c["LUP"]="0:0:1:Lupus";
c["LYN"]="0:0:1:Lynx";
c["LYR"]="0:0:1:Lyra";
c["MEN"]="0:0:1:Mensa";
c["MIC"]="0:0:1:Microscopium";
c["MON"]="0:0:1:Monoceros";
c["MUS"]="0:0:1:Musca";
c["NOR"]="0:0:1:Norma";
c["OCT"]="0:0:1:Octans";
c["OPH"]="0:0:1:Ophiuchus";
c["ORI"]="0:0:1:Orion";
c["PAV"]="0:0:1:Pavo";
c["PEG"]="0:0:1:Pegasus";
c["PER"]="0:0:1:Perseus";
c["PHE"]="0:0:1:Phoenix";
c["PIC"]="0:0:1:Pictor";
c["PSC"]="0:0:1:Pisces";
c["PSA"]="0:0:1:Pisces Austrinus";
c["PUP"]="0:0:1:Puppis";
c["PYX"]="0:0:1:Pyxis";
c["RET"]="0:0:1:Reticulum";
c["SGE"]="0:0:1:Sagitta";
c["SGR"]="0:0:1:Sagittarius";
c["SCO"]="0:0:1:Scorpius";
c["SCL"]="0:0:1:Sculptor";
c["SCT"]="0:0:1:Scutum";
c["SER1"]="0:0:1:Serpens Caput";
c["SER2"]="0:0:1:Serpens Cauda";
c["SEX"]="0:0:1:Sextans";
c["TAU"]="0:0:1:Taurus";
c["TEL"]="0:0:1:Telescopium";
c["TRI"]="0:0:1:Triangulum";
c["TRA"]="0:0:1:Triangulum Australe";
c["TUC"]="0:0:1:Tucana";
c["UMA"]="0:0:1:Ursa Major";
c["UMI"]="0:0:1:Ursa Minor";
c["VEL"]="0:0:1:Vela";
c["VIR"]="0:0:1:Virgo";
c["VOL"]="0:0:1:Volans";
c["VUL"]="0:0:1:Vulpecula";
print "gsave [1 convMCU2WCU dup 4 mul] 0 setdash"
print "/constInit {currentpoint /y0 exch def /x0 exch def /dX 0 def /dY 0 def} def"
print "/constCalc {currentpoint x0 y0 vecSub 2 copy vecLen dX dY vecLen gt {/dY exch def /dX exch def} {pop pop} ifelse} def"}
	function pcps(de, ra, cnl) {
		split(cnl, _cnl, /:/)
		printf("/de x0 y0 dX dY .5 vecMul vecAdd vecLen projStereoPolarI def\n")
		printf("/ra x0 y0 dX dY .5 vecMul vecAdd exch atan 360 exch sub def\n")
		printf("newpath stNull laNPole de %s dup 0 eq {pop} {exch pop} ifelse 0 0 pathAlDirRev 4 pathAlParallel\n", _cnl[1])
		printf("[[fontName fontSizeMin fontSizeMax fontSize de %s dup 0 eq {pop} {exch pop} ifelse findFontSizeDe %s mul 0 true true\n", _cnl[1], _cnl[3])
		printf("(%s)]] stNull laNPole de %s dup 0 eq {pop} {exch pop} ifelse ra %s dup 0 eq {pop} {exch pop} ifelse 0 findAlParallelArcLen ptShow\n", toupper(_cnl[4]), _cnl[1], _cnl[2])
	}
	{	if($3!=cns){
			if(NR>1){
				print "closepath stroke"
				pcps(de, ra, c[cns])}
			printf("newpath %s %s convEqC2WC moveto constInit\n", $2, $1*15)
			de=$2; ra=$1*15; cns=$3}
		else {
			cns=$3; printf("%s %s convEqC2WC lineto\n", $2, $1*15)
			if(!(NR%4))
				printf("constCalc\n")}
	}
END{
print "grestore";}' < $1
