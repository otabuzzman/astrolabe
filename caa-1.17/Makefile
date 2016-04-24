PKG   = caa
VER   = 1.17

APPL  = $(PKG)-$(VER)

JDK   = /cygdrive/c/j2sdk1.4.2
JDK   = /cygdrive/c/programme/java/jdk1.5.0_01

DLL   = cyg$(APPL).dll
JAR   = $(APPL).jar

.PHONY: all clean tidy

srcdir  = aaplus

objects = $(patsubst $(srcdir)/%.cpp,%.o,$(filter-out $(srcdir)/AATest.cpp,$(wildcard $(srcdir)/*.cpp))) \
          $(patsubst %.cpp,%.o,$(wildcard CAA*.cpp))
          
headers = $(patsubst %.cpp,$(PKG)_%.h,$(wildcard CAA*.cpp))

classes = $(patsubst %.java,%.class,$(wildcard *.java))

vpath %.h $(srcdir)
vpath %.cpp $(srcdir)

all: $(JAR) $(DLL) AATest.exe

%.o: %.cpp %.h
	$(CXX) -I$(JDK)/include -I$(JDK)/include/win32 -Wall -mno-cygwin -shared -c $< -o $@

CAA%.class: CAA%.java
	$(JDK)/bin/javac -d . $<
	$(JDK)/bin/javah -jni -force $(PKG).`basename $< ".java"`

$(DLL): $(objects)
	$(CXX) -Wall -mno-cygwin -shared -Wl,--export-all-symbols -Wl,--add-stdcall-alias -o $@ $^

.cpp.o:
	$(CXX) -I$(JDK)/include -I$(JDK)/include/win32 -Wall -mno-cygwin -shared -c $< -o $@

AATest.o: AATest.cpp
	$(CXX) -D_DEBUG -Wall -shared -c $< -o $@

AATest.exe: AATest.o $(DLL)
	$(CXX) -o $@ $< -L. -l$(APPL)

$(JAR): $(classes)
	$(JDK)/bin/jar -cf $@ $(PKG)/*.class

clean:
	rm -f $(classes) $(objects) AATest.o
	rm -rf $(PKG)

tidy: clean
	rm -f $(JAR) $(headers) $(DLL) AATest.exe

