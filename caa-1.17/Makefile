PKG   = caa
VER   = 1.17

APPL  = $(PKG)-$(VER)

JDK   = /cygdrive/c/programme/java/jdk1.5.0_01

DLL   = cyg$(APPL).dll
JAR   = $(APPL).jar

.PHONY: all clean tidy

srcdir  = naa

objects = $(patsubst $(srcdir)/%.cpp,%.o,$(filter-out $(srcdir)/AATest.cpp,$(wildcard $(srcdir)/*.cpp))) \
          $(patsubst %.cpp,%.o,$(wildcard CAA*.cpp))
          
classes = $(patsubst %.java,%.class,$(wildcard *.java))

vpath %.cpp $(srcdir)

all: $(JAR) $(DLL) AATest.exe

%.o: %.cpp
	$(CXX) -I$(JDK)/include -I$(JDK)/include/win32 -Wall -mno-cygwin -c $< -o $@

CAA%.class: CAA%.java
	$(JDK)/bin/javac -d . $<

$(DLL): $(objects)
	$(CXX) -Wall -mno-cygwin -shared -Wl,--export-all-symbols -Wl,--add-stdcall-alias -o $@ $^

AATest.o: AATest.cpp
	$(CXX) -D_DEBUG -Wall -c $< -o $@

AATest.exe: AATest.o $(DLL)
	$(CXX) -o $@ $< -L. -l$(APPL)

$(JAR): $(classes)
	$(JDK)/bin/jar -cf $@ $(PKG)/*.class

clean:
	rm -f $(classes) $(objects) AATest.o
	rm -rf $(PKG)

tidy: clean
	rm -f $(JAR) $(headers) $(DLL) AATest.exe

