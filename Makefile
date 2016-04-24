APPL      = astrolabe
MODEL     = $(APPL).xsd

JDK14     = /cygdrive/c/j2sdk1.4.2
JDK15     = /cygdrive/c/programme/java/jre1.5.0_11

.PHONY: all clean tidy
.SUFFIXES: .pdf .ps .xml

empty =
space = $(empty) $(empty)

CLASSPATH = ./castor-0.9.6/castor-0.9.6.jar \
	./castor-0.9.6/castor-0.9.6-xml.jar \
	./castor-0.9.6/jdbc-se2.0.jar \
	./castor-0.9.6/jta1.0.1.jar \
	./commons-logging-1.0.5/commons-logging.jar \
	./commons-logging-1.0.5/commons-logging-api.jar \
	./commons-logging-1.0.5/commons-logging-optional.jar \
	./xerces-2_5_0/xercesImpl.jar \
	./xerces-2_5_0/xmlParserAPIs.jar \
	./xerces-2_5_0/xml-apis.jar \
	./jts-1.8.0/lib/jts-1.8.jar \
	./jts-1.8.0/lib/jtsio-1.8.jar \
	./jakarta-oro-2.0.8/jakarta-oro-2.0.8.jar \
	./commons-cli-1.0/commons-cli-1.0.jar

.xml.ps:
	@( pagesize=`grep pagesize= $< |\
		sed -e 's,.*pagesize=",,' -e 's,".*,,'` ; set $$pagesize ;\
		if test $$# -gt 1 ; then \
			echo "pagesizes in $< are $$*" ; else \
			echo "pagesize in $< is $$1" ; fi )
	@time ( PATH=/cygdrive/c/programme/gs/gs7.04/bin:caa-1.17:$$PATH \
		$(JDK15)/bin/java \
			-classpath $(subst $(space),\;, \
			./astrolabe \
			./astrolabe/model \
			./caa-1.17/caa-1.17.jar \
			$(CLASSPATH)) \
			Main ./$< >$@ )

.ps.pdf:
	@echo "pagesize=$${pagesize:-a4}"
	@time ( PATH=/cygdrive/c/programme/gs/gs7.04/bin:$$PATH \
		gs -q -dBATCH -dNOPAUSE -sPAPERSIZE=$${pagesize:-a4} -sDEVICE=pdfwrite -sOutputFile=$@ $< )

all: $(APPL)/model

$(APPL)/model: $(MODEL)
	@echo -n "Building model... "
	@$(JDK14)/bin/java \
		-classpath $(subst $(space),\;, \
		./castor-0.9.6/castor-0.9.6-srcgen-ant-task.jar \
		$(CLASSPATH)) \
		org.exolab.castor.builder.SourceGenerator -i $<
	@touch $@
	@echo "done!"

clean:
	rm -rf $(APPL)/model
	rm -rf *.class *.ps

tidy: clean
	rm -rf *.pdf



astrolabe.pdf: astrolabe.xml

catalogADC1239H.pdf: catalogADC1239H.xml
catalogADC1239T.pdf: catalogADC1239T.xml
catalogADC5050.pdf: catalogADC5050.xml
catalogADC6049.pdf: catalogADC6049.xml
catalogADC7118.pdf: catalogADC7118.xml

chartaecaeli-pick.pdf: chartaecaeli-pick.xml
chartaecaeli-full.pdf: chartaecaeli-full.xml
chartaecaeli-grid.pdf: chartaecaeli-grid.xml
