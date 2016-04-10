package com.szabowexler.cookbook.tex;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.szabowexler.cookbook.recipes.AbstractRecipe;
import com.szabowexler.cookbook.recipes.parts.AbstractIngredient;
import com.szabowexler.cookbook.recipes.parts.AbstractTimeInterval;

public class RecipeTexGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(RecipeTexGenerator.class);
  private static final Joiner NEWLINE_JOINER = Joiner.on("\n");

  private static final String PREP_TIME_TEX = "preparationtime";
  private static final String BAKING_TIME_TEX = "bakingtime";
  private static final String BAKING_TEMP_TEX = "bakingtemperature";
  private static final String PORTION_TEX = "portion";
  private static final String CALORIE_TEX = "calory";

  private RecipeTexGenerator() {}

  public static String generateRecipeTex(AbstractRecipe recipe) {
    StringBuilder builder = new StringBuilder();

    builder.append("\\begin{recipe}\n")
           .append("[ %\n")
           .append("\t").append(prepTimeTex(recipe.getPrepTime())).append(",\n")
           .append("\t").append(cookTimeTex(recipe.getCookTime())).append(",\n")
           .append("\t").append(portionTex(recipe.getPortions())).append(",\n");
    recipe.getCalories().ifPresent(calories -> {
      builder.append("\t").append(calorieTex(calories));
    });

    builder.append("]\n")
           .append("{").append(recipe.getName()).append("}\n")
           .append(ingredientsTex(recipe.getIngredients())).append("\n")
           .append(stepsTex(recipe.getSteps())).append("\n");

    recipe.getHint().ifPresent(hint -> {
      builder.append(hintTex(hint)).append("\n");
    });

    builder.append("\\end{recipe}\n");

    return builder.toString();
  }

  private static String prepTimeTex(AbstractTimeInterval time) {
    return PREP_TIME_TEX + " = " + time.toTexUnit();
  }

  private static String cookTimeTex(AbstractTimeInterval time) {
    return BAKING_TIME_TEX + " = " + time.toTexUnit();
  }

  private static String portionTex(int portions) {
    return PORTION_TEX + " = {\\portion{" + portions + "}}";
  }

  private static String calorieTex(int calories) {
    return CALORIE_TEX + " = {" + calories + "}";
  }

  private static String ingredientsTex(List<AbstractIngredient> ingredients) {
    return "\t\\ingredients\n" +
        "\t{%\n" +
        NEWLINE_JOINER.join(ingredients.stream()
                                       .map(ingr -> "\t\t" + ingr.toTexString())
                                       .iterator()) + "\n" +
        "\t}\n";
  }

  private static String stepsTex(List<String> steps) {
    return "\t\\preparation\n" +
        "\t{%\n" +
        NEWLINE_JOINER.join(steps.stream()
                                 .map(step -> "\t\t\\step " + step)
                                 .iterator()) + "\n" +
        "\t}\n";
  }

  private static final String hintTex(String hint) {
    return "\t\\hint\n" +
        "\t{%\n" +
        "\t\t" + hint.trim() + "\n" +
        "\t}\n";
  }
}
