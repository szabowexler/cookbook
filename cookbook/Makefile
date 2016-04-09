PAPER = cookbook
TEX = $(wildcard *.tex)
FIGS = $(wildcard figures/*.pdf figures/*.png pic/*.pdf pic/*.jpg)

.PHONY: all clean

$(PAPER).pdf: $(TEX) $(FIGS)
	echo $(FIGS)
	pdflatex $(PAPER)
	pdflatex $(PAPER)

clean:
	rm -f *.aux *.bbl *.blg *.log *.out $(PAPER).pdf

