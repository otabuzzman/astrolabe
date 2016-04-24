###
# Ein Astrolabium für gnuplot.
#
# Stereopolare Projektion des Nordhimmels. der Betrachter
# sieht vom Himmelsnordpol auf die Ebene des Himmelsäqua-
# tors (Projektionsebene).
#
# Koordinatensysteme:
#
#   Kartesisch (x/y)
#   Geografisch (Latitude/Longitude)
#   Horizontal (Altitude/Azimuth)
#   Geozentrisch Äquatorial (Deklination/Rektaszension)
#   Topozentrisch Äquatorial (Deklination/Rektaszension)
#
#
# Variablen:
#   x[A-Z]+
#   y[A-Z]+
#   la[A-Z]+ Breite (Geografisch)
#   lo[A-Z]+ Länge (Geografisch)
#   al[A-Z]+ Höhe (Horizontal)
#   az[A-Z]+ Azimuth (Horizontal)
#   ra[A-Z]+ Rektaszension (Geozentrisch Äquatorial)
#   de[A-Z]+ Deklination (Geozentrisch Äquatorial)
#   ha[A-Z]+ Stundenwinkel (Topozentrisch Äquatorial)
#   de[A-Z]+ Deklination (Topozentrisch Äquatorial)
#
#   r[A-Z]* Distanz (Äquatorial)
#
#   o[A-Z]+ Schiefe zur Ebene des Himmelsäquators
#
###



###
# Umgebung einstellen.
###

# set terminal postscript; set output 'astrolabe.ps'

set multiplot
set noborder
set noxtics
set noytics
set nox2tics
set noy2tics

set parametric
set angles degrees

set size square
set xrange [-1:1]
set yrange [-1:1]
set trange [0:360]

set samples 180

# Radius des Äquators in der Projektionsebene.
r = 0.5



###
# Allgemeine Konstanten, Variablen und Funktionen.
###

# Standort.
laO = 51.53
loO = 7.45

# Ekliptik.
oE = 23.45

# Horizont.
oH = 90-laO

# Stereopolare Projektion von Deklination de und
# Rektaszension ra auf Projektionsebene (Äquator).
# Umwandlung von  geozentrisch äquatorialen (de,ra)
# in kartesische Koordinaten (x,y).
x(de,ra) = r*tan(45-de/2)*cos(ra)
y(de,ra) = r*tan(45-de/2)*sin(ra)

# Bestimmung von Deklination und Rektaszension eines
# Grosskreises mit Schiefe o zur Äquatorebene bei Rek-
# taszension ra bzw. Deklination de.
# ra2de(o,ra) = atan(sin(ra)*tan(o))
# de2ra(o,de) = asin(tan(de)/tan(o))

# Bestimmung von Deklinaltion ind Rekatszension aus
# horizontalen Koordinaten al/az auf geografischer
# Breite la.
ho2de(al,az,la) = asin(sin(la)*sin(al)-cos(la)*cos(al)*cos(az))
ho2ra(al,az,la) = az>180 ? acos((cos(la)*sin(al)+sin(la)*cos(al)*cos(az))/cos(ho2de(al,az,la))) : -acos((cos(la)*sin(al)+sin(la)*cos(al)*cos(az))/cos(ho2de(al,az,la)))

lim4az(al,la,de) = acos(sin(de)/(-cos(la)*cos(al))+tan(la)*tan(al))
lim4al(az,la,de) = 90-acos((sin(de)*sin(la)-cos(de)*cos(la)*cos(az-180)*sqrt(1-(sin(az)*cos(la)/cos(de))**2))/(1-cos(la)**2*sin(az)**2))

# Konvertierung von Zeit in Grad.
tim2deg(h,m,s) = h*15+m*15/60+s*15/3600



###
# Feste Markierungen (Unabhängig von Zeit und Standort).
###

# Unabhängige Variable ra (Default t) speichert Rektaszension.
set dummy ra

# Äquator.
plot x(0,ra),y(0,ra) notitle

# Nördlicher und südlicher Sonnenwendekreis.
plot x(oE,ra),y(oE,ra) notitle
plot x(-oE,ra),y(-oE,ra) notitle

# Polarkreis.
plot x(90-oE,ra),y(90-oE,ra) notitle

# Zirkumpolarkreis.
plot x(oH,ra),y(oH,ra) notitle

# Ekliptik. Entspricht einem Horizont mit geografischer
# Breite 90-oE bei Sternzeit 18h0h0.0s (270 Grad).
st = tim2deg(18,0,0) ; plot x(ho2de(0,ra,90-oE),ho2ra(0,ra,90-oE)+st),y(ho2de(0,ra,90-oE),ho2ra(0,ra,90-oE)+st) notitle

# Deklinationslinien.
de = -20 ; plot x(de,ra),y(de,ra) notitle
de = -10 ; plot x(de,ra),y(de,ra) notitle
de = 10 ; plot x(de,ra),y(de,ra) notitle
de = 20 ; plot x(de,ra),y(de,ra) notitle
de = 30 ; plot x(de,ra),y(de,ra) notitle
de = 40 ; plot x(de,ra),y(de,ra) notitle
de = 50 ; plot x(de,ra),y(de,ra) notitle
de = 60 ; plot x(de,ra),y(de,ra) notitle
de = 70 ; plot x(de,ra),y(de,ra) notitle
de = 80 ; plot x(de,ra),y(de,ra) notitle

# Unabhängige Variable de (Default t) speichert Deklination.
set dummy de

# Rektaszensionslinien.
ra = 0 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 15 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 30 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 45 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 60 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 75 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 90 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 105 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 120 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 135 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 150 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 165 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 180 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 195 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 210 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 225 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 240 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 255 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 270 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 285 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 300 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 315 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 330 ; plot [-oE:85] x(de,ra),y(de,ra) notitle
ra = 345 ; plot [-oE:85] x(de,ra),y(de,ra) notitle



###
# Variable Markierungen (Abhängig von Zeit und Standort).
###

# Lokale Sternzeit.
lst = tim2deg(1,37,0)

# Unabhängige Variable az (Default t) speichert Azimuth.
set dummy az

# Höhenlinien (Altitude).
al = 0 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 10 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 20 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 30 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 40 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 50 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 60 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 70 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
al = 80 ; plot [lim4az(al,laO,-oE):360-lim4az(al,laO,-oE)] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle

# Unabhängige Variable al (Default t) speichert Höhe.
set dummy al

# Azimuthlinien.
az = 0 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 20 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 40 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 60 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 80 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 100 ; plot[ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85]  x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 120 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 140 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 160 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 180 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 200 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 220 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 240 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 260 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 280 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 300 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 320 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
az = 340 ; plot [ho2de(0,az,laO)<-oE ? lim4al(az,laO,-oE) : 0:85] x(ho2de(al,az,laO),ho2ra(al,az,laO)+lst),y(ho2de(al,az,laO),ho2ra(al,az,laO)+lst) notitle
