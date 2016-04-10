package com.szabowexler.cookbook;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.Files;
import com.google.inject.Guice;
import com.szabowexler.cookbook.recipes.RecipeParser;
import com.szabowexler.cookbook.tex.CombinedCookbookTexGenerator;
import com.szabowexler.cookbook.tex.RecipeTexGenerator;

public class CookbookRunner {
  private final static Logger LOG = LoggerFactory.getLogger(CookbookRunner.class);

  private void run(File rootRecipeDirectory) {
    Preconditions.checkState(rootRecipeDirectory.isDirectory(), "Recipes collection must be a folder!");

    Map<String, List<File>> categoryTexFiles = new HashMap<>();
    for (File category : rootRecipeDirectory.listFiles()) {
      if (category.getName().equalsIgnoreCase("example")) {
        LOG.debug("Skipping example directory.");
        continue;
      }
      
      Preconditions.checkState(category.isDirectory(), category.getAbsolutePath() + " is a category, but isn't a directory.");
      LOG.info("Categeory '{}': building...", category.getName());
      List<File> texFilesInCategory = Arrays.asList(category.listFiles()).stream()
                                            .map(this::texify)
                                            .filter(Optional::isPresent)
                                            .map(Optional::get)
                                            .collect(Collectors.toList());
      categoryTexFiles.put(category.getName(), texFilesInCategory);
      LOG.info("Category '{}': built, with {} recipes", category.getName(), texFilesInCategory.size());
    }

    writeCollectionTexFile(rootRecipeDirectory, categoryTexFiles);
  }

  private Optional<File> texify(File f) {
    if (!f.exists()) {
      LOG.error("{}: does not exist, ignoring", f.getAbsolutePath());
      return Optional.empty();
    }

    if (!f.getName().endsWith(".recipe")) {
      LOG.error("{}: does not end with .recipe, ignoring", f.getAbsolutePath());
      return Optional.empty();
    }

    LOG.info("{}: texifying", f.getName());
    String tex = RecipeTexGenerator.generateRecipeTex(RecipeParser.parse(f));
    try {
      return Optional.of(writeTex(f, tex));
    } catch (IOException ex) {
      LOG.error("Unable to write tex file for {}", f.getAbsolutePath(), ex);
      return Optional.empty();
    }
  }

  private static File writeTex(File originalRecipeFile, String tex) throws IOException {
    File texFile = new File(
        originalRecipeFile.getAbsolutePath()
                          .replace("/recipes/", "/tex/")
                          .replace(".recipe", ".tex"));
    LOG.info(" --> " + texFile.getAbsolutePath());
    Files.createParentDirs(texFile);
    Files.write(tex.getBytes(), texFile);
    return texFile;
  }

  private static void writeCollectionTexFile(File recipeRoot,
                                             Map<String, List<File>> categoryTexFiles) {
    String tex = CombinedCookbookTexGenerator.generateCookbookTex(categoryTexFiles);
    File texDirectory = Paths.get(recipeRoot.getParentFile().getAbsolutePath(), "tex").toFile();
    File texFile = Paths.get(texDirectory.getAbsolutePath(), "cookbook.tex").toFile();
    try {
      CombinedCookbookTexGenerator.writeCookyBookyFilesForTex(texDirectory);
      Files.write(tex.getBytes(), texFile);
    } catch (IOException ex) {
      LOG.error("Unable to write tex to file {}", texFile.getAbsolutePath(), ex);
    }
  }

  public static void main(String[] args) throws IOException {
    if (args.length == 0) {
      System.out.println("Usage: root recipe directory as sole argument (got " + args.length + " args)");
      System.exit(0);
    }

    LOG.info("Running against '{}'", args[0]);
    Guice.createInjector(new CookbookModule()).getInstance(CookbookRunner.class)
         .run(new File(args[0]));
  }
}
