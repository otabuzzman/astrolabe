APPL      = astrolabe
MODEL     = $(APPL).xsd

JDK       = /cygdrive/c/j2sdk1.4.2

.PHONY: all clean tidy

CLASSPATH = "./castor-0.9.6/castor-0.9.6-srcgen-ant-task.jar;./castor-0.9.6/castor-0.9.6-xml.jar;./castor-0.9.6/castor-0.9.6.jar;./castor-0.9.6/jdbc-se2.0.jar;./castor-0.9.6/jta1.0.1.jar;./commons-logging-1.0.5/commons-logging-api.jar;./commons-logging-1.0.5/commons-logging-optional.jar;./commons-logging-1.0.5/commons-logging.jar;./xerces-2_5_0/xercesImpl.jar;./xerces-2_5_0/xml-apis.jar;./xerces-2_5_0/xmlParserAPIs.jar"

all: .model

.model: $(MODEL)
	@echo -n "Building model... "
	@$(JDK)/bin/java -classpath $(CLASSPATH) \
		org.exolab.castor.builder.SourceGenerator \
		-i $< -package $(APPL)$@
	@touch $@
	@echo "done!"

clean:
	rm -rf .model $(APPL)

tidy: clean
