WORKING_PATH = $(shell pwd)
RECIPE_PATH = "$(WORKING_PATH)/recipes"
TEX_PATH = "$(WORKING_PATH)/tex"
JAVA_PATH = "$(WORKING_PATH)/java/src/main"
PAPER = cookbook
RECIPES = $(wildcard RECIPE_PATH/*.recipe)
JAVA = $(wildcard JAVA_PATH/*.java)

.PHONY: all clean

$(PAPER).pdf: $(RECIPES) $(JAVA)
	cd java && \
	mvn clean package && \
	java -jar target/cookbook-texify.jar $(RECIPE_PATH)
	cd tex && \
	pdflatex $(PAPER) && \
	pdflatex $(PAPER)
	mv "$(TEX_PATH)/$(PAPER).pdf" "$(WORKING_PATH)/$(PAPER).pdf"

clean:
	rm -rf $(TEX_PATH)

