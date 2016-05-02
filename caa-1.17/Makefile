PKG   = caa
VER   = 1.17

APPL  = $(PKG)-$(VER)

JDK   = /cygdrive/c/program\ files\ \(x86\)/java/jre1.5.0_17

DLL   = cyg$(APPL).dll
JAR   = $(APPL).jar

.PHONY: all clean tidy
.SUFFIXES: .cxx .java .class

srcdir  = src/$(PKG)
pkgdir  = $(PKG)

objects = $(patsubst $(srcdir)/%.cpp,%.o,$(filter-out $(srcdir)/AATest.cpp,$(wildcard $(srcdir)/*.cpp))) \
          $(patsubst $(pkgdir)/%.cxx,%.o,$(wildcard $(pkgdir)/CAA*.cxx))
          
classes = $(patsubst $(pkgdir)/%.java,$(pkgdir)/%.class,$(wildcard $(pkgdir)/*.java))

vpath %.cpp $(srcdir)
vpath %.cxx $(pkgdir)
vpath %.java $(pkgdir)

.cxx.o .cpp.o:
	$(CXX) -Isrc -I$(JDK)/include -I$(JDK)/include/win32 -Wall -mno-cygwin -c $< -o $@

.java.class:
	$(JDK)/bin/javac -d . $<

all: $(JAR) $(DLL) AATest.exe
	
$(JAR): $(classes)
	$(JDK)/bin/jar -cf $@ $^

$(DLL): $(objects)
	$(CXX) -Wall -mno-cygwin -shared -Wl,--export-all-symbols -Wl,--add-stdcall-alias -o $@ $^

AATest.o: AATest.cpp
	$(CXX) -D_DEBUG -Wall -c $< -o $@

AATest.exe: AATest.o $(DLL)
	$(CXX) -o $@ $< -L. -l$(APPL)

clean:
	rm -f $(classes) $(objects) AATest.o

tidy: clean
	rm -f $(JAR) $(headers) $(DLL) AATest.exe
	
CAAAberation.class: CAA2DCoordinate.class
CAAAberation.class: CAA3DCoordinate.class
CAAAberation.class: CAAAberration.class
CAABinaryStar.class: CAABinaryStarDetails.class
CAACoordinateTransformation.class: CAA2DCoordinate.class
CAADate.class: CAACalendarDate.class
CAAEaster.class: CAAEasterDetails.class
CAAEclipses.class: CAALunarEclipseDetails.class
CAAEclipses.class: CAASolarEclipseDetails.class
CAAEclipticalElements.class: CAAEclipticalElementDetails.class
CAAElliptical.class: CAA2DCoordinate.class
CAAElliptical.class: CAA3DCoordinate.class
CAAElliptical.class: CAAAberration.class
CAAElliptical.class: CAAEllipticalObjectDetails.class
CAAElliptical.class: CAAEllipticalObjectElements.class
CAAElliptical.class: CAAEllipticalPlanetaryDetails.class
CAAEquationOfTime.class: CAA2DCoordinate.class
CAAFK5.class: CAA3DCoordinate.class
CAAGalileanMoons.class: CAAGalileanMoonDetail.class
CAAGalileanMoons.class: CAAGalileanMoonsDetails.class
CAAJewishCalendar.class: CAACalendarDate.class
CAAMoslemCalendar.class: CAACalendarDate.class
CAANodes.class: CAAEllipticalObjectElements.class
CAANodes.class: CAANodeObjectDetails.class
CAANodes.class: CAAParabolicObjectElements.class
CAAParabolic.class: CAA3DCoordinate.class
CAAParabolic.class: CAAParabolicObjectDetails.class
CAAParabolic.class: CAAParabolicObjectElements.class
CAAParallax.class: CAA2DCoordinate.class
CAAParallax.class: CAATopocentricEclipticDetails.class
CAAPhysicalJupiter.class: CAAPhysicalJupiterDetails.class
CAAPhysicalMars.class: CAA2DCoordinate.class
CAAPhysicalMars.class: CAAPhysicalMarsDetails.class
CAAPhysicalMoon.class: CAA2DCoordinate.class
CAAPhysicalMoon.class: CAAPhysicalMoonDetails.class
CAAPhysicalMoon.class: CAASelenographicMoonDetails.class
CAAPhysicalSun.class: CAAPhysicalSunDetails.class
CAAPrecession.class: CAA2DCoordinate.class
CAARiseTransitSet.class: CAA2DCoordinate.class
CAARiseTransitSet.class: CAARiseTransitSetDetails.class
CAASaturnMoons.class: CAA2DCoordinate.class
CAASaturnMoons.class: CAASaturnMoonDetail.class
CAASaturnMoons.class: CAASaturnMoonsDetails.class
CAASaturnRings.class: CAA2DCoordinate.class
CAASaturnRings.class: CAASaturnRingDetails.class
CAASun.class: CAA3DCoordinate.class
