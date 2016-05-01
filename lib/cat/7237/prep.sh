gawk '
	$1~/^[0-9]+/ {
		# public String...
		printf( "public String %s	; // ", $3 ) > "dec"
		for ( nf=4 ; nf<=NF ; nf++ )
			printf( "%s ", $nf ) > "dec"
		print "" > "dec"
		
		# v =...
		printf( "%s		= data.substring( %d, %d ).trim() ;\n", $3, $1-1, $2 ) > "def"
		
		# Registry...
		printf( "key = m.message( ApplicationConstant.LK_ADC7237_%s ) ;\n", toupper( $3 ) ) > "reg"
		printf( "Registry.registerName( key, %s ) ;\n", $3 ) > "reg"
		
		# ApplicationConstant...
		printf( "public final static String LK_ADC7237_%s		= \"substitute.adc7237.%s\" ;\n", toupper( $3 ) , $3 ) > "cst"
		
		# .properties
		printf( "substitute.adc7237.%s=%s\n", $3, $3 ) > "pro"
	}
	$1!~/^[0-9]+/ {
		printf( "			// " ) > "dec"
		for ( nf=1 ; nf<=NF ; nf++ )
			printf( "%s ", $nf ) > "dec"
		print "" > "dec"
	}
' <<-!!
   4  10    PGC     [1/3099300] PGC number
  13  14    RAh     Right ascension (J2000)
  15  16    RAm     Right ascension (J2000)
  17  20    RAs     Right ascension (J2000)
  21  21    DE      Declination sign (J2000)
  22  23    DEd     Declination (J2000)
  24  25    DEm     Declination (J2000)
  26  27    DEs     Declination (J2000)
  29  30    OType   [GM ] Object type (1)
  32  36    MType   Provisional morphological type from LEDA
                     according to the RC2 code.
  37  41    logD25  ?=9.99 Apparent diameter (2)
  45  48  e_logD25  ?=9.99 Actual error of logD25
  51  54    logR25  ?=9.99 Axis ratio in log scale
                      (log of major axis to minor axis)
  58  61  e_logR25  ?=9.99 Actual error on logR25
  64  67    PA      ?=999. Adopted 1950-position angle (3)
  71  74  e_PA      ?=999. rms uncertainty on PA
  76  77  o_ANames  Number of alternate names.
  79 341    ANames  Alternate names (4)
!!
