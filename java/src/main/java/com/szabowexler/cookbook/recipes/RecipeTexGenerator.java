package com.szabowexler.cookbook.recipes;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecipeTexGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(RecipeTexGenerator.class);
  private RecipeTexGenerator() {}

  public static void generateRecipeTex(AbstractRecipe recipe) {
    LOG.info("{}", recipe);
  }
}
