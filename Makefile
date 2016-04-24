APPL      = astrolabe

M4FLAGS   = --undefine=shift --define=constproc=$(CONSTPROC)
M4FILES   = $(APPL).m4

BCFILES   = $(APPL).vars $(APPL).bc

CONSTPROC = 6049.ps

all: $(APPL).ps

$(CONSTPROC): prep6049.sh
	./$< 6049_bound_20.dat > $@

$(APPL).ps: $(APPL).in $(M4FILES) $(BCFILES) $(CONSTPROC)
	m4 $(M4FLAGS) $(M4FILES) - < $< > $@

clean:
	rm -f *.ps
