APPL      = astrolabe
MODEL     = $(APPL).xsd

JDK     = /cygdrive/c/programme/java/jre1.5.0_17

.PHONY: all clean tidy
.SUFFIXES: .pdf .ps .xml .class .map

empty =
space = $(empty) $(empty)

vpath %.class $(APPL)/model

CAA = caa-1.17

CLASSPATH = ./lib/castor-0.9.6.jar \
	./lib/commons-logging-api.jar \
	./lib/commons-cli-1.0.jar \
	./lib/jakarta-oro-2.0.8.jar \
	./lib/xercesImpl.jar \
	./jts-1.8.0/lib/jts-1.8.jar \
	./jts-1.8.0/lib/jtsio-1.8.jar

.xml.ps:
	@time $(JDK)/bin/java \
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
	@$(JDK)/bin/java \
		-classpath $(subst $(space),\;, \
		$(CLASSPATH)) \
		org.exolab.castor.tools.MappingTool -i $(subst /,.,$(subst .class,,$<) -o $@

all: $(APPL)/model

$(APPL)/model: $(MODEL)
	@echo -n "Building model... "
	@$(JDK)/bin/java \
		-classpath $(subst $(space),\;, \
		./castor-0.9.6-srcgen-ant-task.jar \
		$(CLASSPATH)) \
		org.exolab.castor.builder.SourceGenerator -i $<
	@touch $@
	@echo "done!"

clean:
	rm -rf $(APPL)/model
	rm -rf *.class

tidy: clean
	rm -rf *.ps *.pdf

	

astrolabe.pdf: astrolabe.xml

catalogADC1239H.pdf: catalogADC1239H.xml
catalogADC1239T.pdf: catalogADC1239T.xml
catalogADC5050.pdf: catalogADC5050.xml
catalogADC6049.pdf: catalogADC6049.xml
catalogADC7118.pdf: catalogADC7118.xml

wallpaper.pdf: wallpaper.xml

intersection.pdf: intersection.xml

.SECONDARY: atlas-stereographic.ps
atlas-stereographic.pdf: atlas-stereographic.xml

atlaspage-template.pdf: atlaspage-template.xml
