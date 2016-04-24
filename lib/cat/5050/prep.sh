gunzip <catalog.dat.gz |\
gawk 'BEGIN{FS="" ;
	greek["Alp"]="&#x03b1;" ;
	greek["Bet"]="&#x03b2;" ;
	greek["Chi"]="&#x03c7;" ;
	greek["Del"]="&#x03b4;" ;
	greek["Eps"]="&#x03b5;" ;
	greek["Eta"]="&#x03b7;" ;
	greek["Gam"]="&#x03b3;" ;
	greek["Iot"]="&#x03b9;" ;
	greek["Kap"]="&#x03ba;" ;
	greek["Lam"]="&#x03bb;" ;
	greek["Mu"]="&#x03bc;" ;
	greek["Nu"]="&#x03bd;" ;
	greek["Ome"]="&#x03c9;" ;
	greek["Omi"]="&#x03bf;" ;
	greek["Phi"]="&#x03c6;" ;
	greek["Pi"]="&#x03c0;" ;
	greek["Psi"]="&#x03c8;" ;
	greek["Rho"]="&#x03c1;" ;
	greek["Sig"]="&#x03c3;" ;
	greek["Tau"]="&#x03c4;" ;
	greek["The"]="&#x03b8;" ;
	greek["Ups"]="&#x03c5;" ;
	greek["Xi"]="&#x03be;" ;
	greek["Zet"]="&#x03b6;"}
	{
		id=substr($0,1,4) ;
		gsub(/ /,"",id) ;
		name=substr($0,5,10) ;
		nflm=substr(name,1,3) ;
		gsub(/ /,"",nflm) ;
		nbay=substr(name,4,3) ;
		gsub(/ /,"",nbay) ;
		nidx=substr(name,7,1) ;
		gsub(/ /,"",nidx) ;
		ncon=substr(name,8,3) ;
		gsub(/ /,"",ncon) ;
		if ( length(nbay)>0 ) {
			nbay=greek[nbay] ;
		}
	
	print id "|" nflm "|" nbay "|" nidx "|" ncon ;
	}
'
