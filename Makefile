APPL      = astrolabe
MODEL     = $(APPL).xsd

JDK14     = /cygdrive/c/j2sdk1.4.2
JDK15     = /cygdrive/c/programme/java/jre1.5.0_17

.PHONY: all clean tidy
.SUFFIXES: .pdf .ps .xml .class .map

empty =
space = $(empty) $(empty)

vpath %.class $(APPL)/model

CLASSPATH = ./lib/castor-0.9.6.jar \
	./lib/commons-logging-api.jar \
	./lib/commons-cli-1.0.jar \
	./lib/jakarta-oro-2.0.8.jar \
	./lib/xercesImpl.jar \
	./jts-1.8.0/lib/jts-1.8.jar \
	./jts-1.8.0/lib/jtsio-1.8.jar

.xml.ps:
	@( pagesize=`grep Chart.*pagesize= $< |\
		sed -e 's,.*pagesize=",,' -e 's,".*,,'` ; set $$pagesize ;\
		if test $$# -gt 1 ; then \
			echo "pagesizes in $< are $$*" ; else \
			echo "pagesize in $< is $$1" ; fi )
	@time ( PATH=/cygdrive/c/programme/gs/gs8.54/bin:caa-1.17:$$PATH \
		$(JDK15)/bin/java \
			-classpath $(subst $(space),\;, \
			./astrolabe \
			./astrolabe/model \
			./caa-1.17/caa-1.17.jar \
			$(CLASSPATH)) \
			astrolabe.Astrolabe ./$< >$@ )

.ps.pdf:
	@echo "pagesize=$${pagesize:-a4}"
	@time ( PATH=/cygdrive/c/programme/gs/gs8.54/bin:$$PATH \
		gswin32c -q -dBATCH -dNOPAUSE -sPAPERSIZE=$${pagesize:-a4} -sDEVICE=pdfwrite -sOutputFile=$@ $< )

.class.map:
	@$(JDK15)/bin/java \
		-classpath $(subst $(space),\;, \
		$(CLASSPATH)) \
		org.exolab.castor.tools.MappingTool -i $(subst /,.,$(subst .class,,$<)) -o $@

all: $(APPL)/model

$(APPL)/model: $(MODEL)
	@echo -n "Building model... "
	@$(JDK15)/bin/java \
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

