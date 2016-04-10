package com.szabowexler.cookbook.recipes;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.szabowexler.cookbook.recipes.ImmutableRecipe.Builder;
import com.szabowexler.cookbook.recipes.parts.AbstractIngredient;
import com.szabowexler.cookbook.recipes.parts.AbstractTimeInterval;

public class RecipeParser {
  private static final Logger LOG = LoggerFactory.getLogger(RecipeParser.class);
  private static final String RECIPE_FILETYPE = ".recipe";
  private static final String COMMENT_PREFIX = "#";

  public static AbstractRecipe parse(File f) {
    Preconditions.checkState(f.exists(), f.getAbsolutePath() + " does not exist");
    Preconditions.checkState(accept(f), f.getAbsolutePath() + " is not a " + RECIPE_FILETYPE + " file");

    try {
      List<String> lines = Resources.readLines(f.toURI().toURL(), Charsets.UTF_8).stream()
                                    .filter(line -> !isNullOrEmpty(line))
                                    .filter(line -> !line.trim().startsWith(COMMENT_PREFIX))
                                    .collect(Collectors.toList());
      Preconditions.checkState(lines.size() > 1, f.getAbsolutePath() + "has no text");

      return parse(f.getAbsolutePath(), lines);
    } catch (IOException ex) {
      throw Throwables.propagate(ex);
    }
  }

  private static AbstractRecipe parse(String filePath, List<String> lines) {
    Builder builder = ImmutableRecipe.builder();
    for (int i = 0; i < lines.size(); i++) {
      String fieldLine = lines.get(i);
      Optional<RecipeField> recipeFieldMaybe = RecipeField.matchingField(fieldLine);

      if (!recipeFieldMaybe.isPresent()) {
        throw new IllegalStateException(filePath + " is not a list of recipe steps. Check the format!");
      }

      RecipeField field = recipeFieldMaybe.get();
      List<String> pieces;
      if (field.isMultiline()) {
        int j = i + 1;
        pieces = new ArrayList<>();
        for (; j < lines.size() && !RecipeField.matchingField(lines.get(j)).isPresent(); j++) {
          String line = lines.get(j);
          if (!line.trim().startsWith("-")) {
            String append = line.trim();
            pieces.add(pieces.remove(pieces.size() - 1) + " " + append);
          } else {
            pieces.add(line.substring(line.indexOf('-') + 1).trim());
          }
        }
        i = j - 1;
      } else {
        pieces = Collections.singletonList(parseField(field, fieldLine));
      }
      addField(builder, field, pieces);
    }

    return builder.build();
  }

  private static void addField(Builder builder, RecipeField field, List<String> args) {
    String singleLine = args.get(0);
    switch (field) {
      case NAME:
        builder.name(singleLine);
        break;
      case SOURCE:
        try {
          builder.source(URI.create(singleLine).toURL());
        } catch (MalformedURLException ex) {
          LOG.error("Unable to parse source URL '{}'", singleLine);
          throw Throwables.propagate(ex);
        }
        break;
      case PREP_TIME:
        builder.prepTime(AbstractTimeInterval.parse(singleLine));
        break;
      case COOK_TIME:
        builder.cookTime(AbstractTimeInterval.parse(singleLine));
        break;
      case PORTIONS:
        builder.portions(Integer.parseInt(singleLine));
        break;
      case CALORIES:
        builder.calories(Integer.parseInt(singleLine));
        break;
      case HINT:
        builder.hint(singleLine);
        break;
      case INGREDIENTS:
        builder.ingredients(args.stream()
                                .map(AbstractIngredient::parse)
                                .collect(Collectors.toList()));
        break;
      case STEPS:
        builder.steps(args);
        break;
      default:
        throw new IllegalStateException("Got an unknown field: " + field);
    }
  }

  private static String parseField(RecipeField field, String line) {
    return line.substring(line.indexOf(field.getFieldName() + ':') + field.getFieldName().length() + 2);
  }

  private static boolean accept(File f) {
    return f.getName().endsWith(RECIPE_FILETYPE);
  }
}
