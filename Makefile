WORKING_PATH = $(shell pwd)
RECIPE_PATH = "$(WORKING_PATH)/recipes"
TEX_PATH = "$(WORKING_PATH)/tex"
JAVA_PATH = "$(WORKING_PATH)/java/src/main"
LOGS = "$(WORKING_PATH)/logs"
PAPER = cookbook
RECIPES = $(wildcard RECIPE_PATH/*.recipe)
JAVA = $(wildcard JAVA_PATH/*.java)

.PHONY: all clean

$(PAPER).pdf: $(RECIPES) $(JAVA)
	@echo "\tCreating log directory..."
	@mkdir -p $(LOGS)
	@echo "\tTranspiling recipes into LaTeX..."
	@cd java && \
	mvn clean package &> "$(LOGS)/compile.log" && \
	java -jar target/cookbook-texify.jar $(RECIPE_PATH) &> "$(LOGS)/transpile.log"
	@echo "\tCompiling LaTeX..."
	@cd tex && \
	pdflatex $(PAPER) &> "$(LOGS)/tex1.log" && \
	echo "\tResolving indices and links..." && \
	pdflatex $(PAPER) &> "$(LOGS)/tex2.log"
	@mv "$(TEX_PATH)/$(PAPER).pdf" "$(WORKING_PATH)/$(PAPER).pdf"
	@echo "\tCleaning up..."
	@rm -rf "$(TEX_PATH)"
	@echo "\tOpening cookbook!"
	@open "$(WORKING_PATH)/$(PAPER).pdf" &

clean:
	@echo "\tDeleting logs..."
	@rm -rf $(LOGS)
	@echo "\tRemoving current cookbook pdf..."
	@rm -f $(PAPER).pdf

