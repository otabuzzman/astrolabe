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
		printf( "key = m.message( ApplicationConstant.LK_ADC5109_%s ) ;\n", toupper( $3 ), $3 ) > "reg"
		printf( "Registry.registerName( key, %s ) ;\n", $3 ) > "reg"
		
		# ApplicationConstant...
		printf( "public final static String LK_ADC5109_%s		= \"substitute.adc5109.%s\" ;\n", toupper( $3 ) , $3 ) > "cst"
		
		# .properties
		printf( "substitute.adc5109.%s=%s\n", $3, $3 ) > "pro"
	}
	$1!~/^[0-9]+/ {
		printf( "			// " ) > "dec"
		for ( nf=1 ; nf<=NF ; nf++ )
			printf( "%s ", $nf ) > "dec"
		print "" > "dec"
	}
' <<-!!
   9  27    SKY2000 *Identifier based on J2000 position
  28  35    ID       Skymap number
  36  41    HD       ?Henry Draper <III/135> number
  42  42  m_HD      *[1239]? HD duplicity indication
  43  43  u_HD       HD identification uncertain
  44  49    SAO      ? SAO <I/131> number
  50  50  m_SAO      SAO component
  51  61    DM       Durchmusterung (BD <I/122>; SD <I/119>;
                            CD <I/114>;  CP <I/108>)
  62  62  m_DM       Durchmusterung supplement letter
  63  63  u_DM       [: ] DM identification uncertain
  64  67    HR       ?Harvard Revised <V/50> num. (=BS)
  68  77    WDS      Washington Double Stars <I/237> number
  78  82  m_WDS      WDS components
  83  83  u_WDS      [: ] WDS identification uncertain
  84  89    PPM      ?Position and Proper Motion number
                             (<I/146>, <I/193>, <I/208>)
  90  90  u_PPM      [: ] PPM identification uncertain
  91  98  ID_merg    ?Skymap num. of last skymap entry merged
                           with this star
  99 108    Name     Star name (or AGK3 number)
 109 118    Vname    Variable star name (or
                            doubtful variability)
 119 120    RAh      Right ascension (J2000) hours
 121 122    RAm      Right ascension (J2000) minutes
 123 129    RAs      Right ascension (J2000) seconds
 130 130    DE       Declination sign
 131 132    DEd      Declination degrees (J2000)
 133 134    DEm      Declination minutes (J2000)
 135 140    DEs      Declination seconds (J2000)
 141 146   e_pos     Position uncertainty
 147 147  f_pos      [b] Blended position flag
 148 149  r_pos      Source of position
 150 157    pmRA     Proper motion in RA (J2000)
 158 165    pmDE     Proper motion in Dec (J2000)
 166 167  r_pm       ?Source of proper motion data
 168 173    RV       ?Radial velocity
 174 175  r_RV      *?Source of radial velocity data
 176 183    Plx      ?Trigonometric parallax
 184 191   e_Plx     ?Trigonometric parallax uncertainty
 192 193  r_Plx      ?Source of trigonometric parallax data
 194 202    GCI_X   *GCI unit vector in X (J2000)
 203 211    GCI_Y   *GCI unit vector in Y (J2000)
 212 220    GCI_Z   *GCI unit vector in Z (J2000)
 221 226    GLON     Galactic longitude (B1950)
 227 232    GLAT     Galactic latitude (B1950)
 233 238    Vmag     ?Observed visual magnitude (V or v)
 239 243    Vder     ?Derived visual magnitude
 244 248  e_Vmag     ?Derived v or observed visual magnitude
                            uncertainty
 249 249  f_Vmag     [b] Blended visual magnitude flag
 250 251  r_Vmag    *?Source of visual magnitude
 252 252  n_Vmag     ?V magnitude derivation flag
 253 258    Bmag     ?B magnitude (observed)
 259 264    BV      ?B V color (observed)
 265 269  e_Bmag     ?B or (B V) magnitude uncertainty
 270 270  f_Bmag     [b] Blended b magnitude flag
 271 272  r_Bmag     ?Source of b magnitude
 273 278    Umag     ?U magnitude (observed)
 279 284    UB      ?U B color (observed)
 285 289  e_Umag     ?U or (U B) magnitude uncertainty
 290 290  n_Umag     Blended u magnitude flag
 291 292  r_Umag    *?Source of u magnitude
 293 296    Ptv      ?Photovisual magnitude (observed)
 297 298  r_Ptv      ?Source of ptv magnitudes
 299 302    Ptg      ?Photographic magnitude (observed)
 303 304  r_Ptg      ?Source of ptg magnitudes
 305 334    SpMK     Morgan Keenan (MK) spectral type
 335 336  r_SpMK     ?Source of MK spectral type data
 337 339    Sp      *One dimensional spectral class
 340 341  r_Sp       ?Source of one dimen. spectral class
 342 348    sep      ?Separation of brightest and second
                            brightest components
 349 353    Dmag     ?Magnitude difference of the brightest
                            and second brightest components
 354 360    orbPer  *?Orbital period
 361 363    PA       ?Position angle
 364 370    date     ?Year of observation (AD)
 371 372  r_dup      ?Source of multiplicity data
 373 373  n_Dmag     Passband of multiple star mag. dif.
 374 380    dist1    ?Distance to nearest neighboring star in
                            the master catalog
 381 387    dist2    ?Dist. to nearest neighboring master
                            cat. star no more than 2 mag. fainter
 388 395    ID_A     ?Skymap number of primary component
 396 403    ID_B     ?Skymap number of second component
 404 411    ID_C     ?Skymap number of third component
 412 416    magMax  *?Maximum variable magnitude
 417 421    magMin  *?Minimum variable magnitude
 422 426    varAmp  ?Variability amplitude
 427 427  n_varAmp   Passband of variability amplitude
 428 435    varPer   ?Period of variability
 436 443    varEp    ?Epoch of variability (JD 2400000)
 444 446    varTyp   ?Type of variable star
 447 448  r_var      ?Source of variability data
 449 454    mag1     ?Passband #1 magnitude (observed)
 455 460    vmag1   ?v   passband #1 color
 461 465  e_mag1     ?Passband #1 uncertainty in mag. or col.
 466 466  n_mag1    *[RJC] Passband #1 photometric system
 467 467  p_mag1    *[R] Passband #1
 468 469  r_mag1    *?Source of passband #1: mag. or color
 470 475    mag2     ?Passband #2 magnitude (observed)
 476 481    vmag2   ?v   passband #2 color
 482 486  e_mag2     ?Passband #2 uncertainty in mag. or col.
 487 487  n_mag2    *[JEC] Passband #2 photometric system
 488 488  p_mag2    *[I] Passband #2
 489 490  r_mag2    *?Source of passband #2: mag. or color
 491 496    ci1 2    ?Passband #1   passband #2 color
 497 497  f_mag1     [b] Blended passband #1 mag/color flag
 498 498  f_mag2     [b] Blended passband #2 mag/color flag
 499 504    mag3    *?Passband #3 magnitude (observed)
 505 510    vmag3  *?v   passband #3 color
 511 515  e_mag3    *?Passband #3 uncertainty in mag. or col.
 516 516  n_mag3    *Passband #3 photometric system
 527 517  p_mag3    *[X] Passband #3
 518 519  r_mag3    *?Source of passband #3: mag. or color
 520 520  f_mag3    *[b] Blended passband #3 mag/color flag
!!
