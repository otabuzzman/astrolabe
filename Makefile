APPL      = astrolabe
MODEL     = $(APPL).xsd

JDK     = /cygdrive/c/program\ files\ \(x86\)/java/jre1.5.0_17
JDO		= -Xcheck:jni -Dhttp \
			-Xmx1536m \
			-Dftp.proxyHost=proxy.materna.de \
			-Dftp.proxyPort=8080

.PHONY: all clean tidy
.SUFFIXES: .pdf .ps .xml .class .map

empty =
space = $(empty) $(empty)

vpath %.class $(APPL)/model

CAA = caa-1.17

CLASSPATH = ./lib/castor-1.3.1.jar \
	./lib/castor-1.3.1-core.jar \
	./lib/jts-1.13.jar \
	./lib/jtsio-1.13.jar \
	./lib/runcc.jar \
	./lib/commons-math3-3.1.1.jar \
	./lib/commons-logging-1.1.1.jar

JAVA_UNICODEBLOCK = $(APPL)/UnicodeBlock.java
PREP_UNICODEBLOCK = ./prepUnicodeBlock.sh

BLOCKS = ./lib/Blocks-4.1.0.txt

.xml.ps:
	@time $(JDK)/bin/java $(JDO) \
			-Djava.library.path=./$(CAA) \
			-classpath $(subst $(space),\;, \
			./astrolabe \
			./astrolabe/model \
			./$(CAA) \
			$(CLASSPATH)) \
			astrolabe.Astrolabe ./$< >$@

.ps.pdf:
	@time $${GS:-gswin32c} -q -dBATCH -dNOPAUSE -sDEVICE=pdfwrite -sOutputFile=$@ $<

.class.map:
	@$(JDK)/bin/java $(JDO) \
		-classpath $(subst $(space),\;, \
		$(CLASSPATH)) \
		org.exolab.castor.tools.MappingTool -i $(subst /,.,$(subst .class,,$<)) -o $@

all: $(APPL)/model $(JAVA_UNICODEBLOCK)

$(APPL)/model: $(MODEL)
	@echo -n "Building model... "
	@$(JDK)/bin/java $(JDO) \
		-classpath $(subst $(space),\;, \
		./castor-1.3.1-codegen.jar \
		./castor-1.3.1-xml-schema.jar \
		$(CLASSPATH)) \
		org.exolab.castor.builder.SourceGeneratorMain -i $< \
		-binding-file binding.xml \
	@touch $@
	@echo "done!"

$(JAVA_UNICODEBLOCK): $(PREP_UNICODEBLOCK) $(BLOCKS)
	$(PREP_UNICODEBLOCK) $(BLOCKS) >$@

clean:
	rm -rf $(APPL)/model
	rm -f $(APPL)/*.class
	rm -f *.class

tidy: clean
	rm -f $(JAVA_UNICODEBLOCKS)
	rm -f *.ps *.pdf

.SECONDARY: astrolabe.ps
