package com.szabowexler.cookbook.recipes;

import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Charsets;
import com.google.common.base.Preconditions;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;
import com.szabowexler.cookbook.ImmutableRecipe;
import com.szabowexler.cookbook.ImmutableRecipe.Builder;

@Immutable
public abstract class AbstractRecipe {
  private static final String RECIPE_FILETYPE = ".recipe";

  enum RecipeField {
    NAME("name", false),
    PREP_TIME("prepTime", false),
    COOK_TIME("cookTime", false),
    CALORIES("calories", false),
    INGREDIENTS("ingredients", true),
    STEPS("steps", true),;

    private final String fieldName;
    private final boolean isMultiline;

    RecipeField(String fieldName, boolean isMultiline) {
      this.fieldName = fieldName;
      this.isMultiline = isMultiline;
    }

    public boolean isMultiline() {
      return isMultiline;
    }

    private boolean matches(String line) {
      return line.startsWith(fieldName);
    }

    private static Optional<RecipeField> matchingField(String line) {
      for (RecipeField field : values()) {
        if (field.matches(line)) {
          return Optional.of(field);
        }
      }
      return Optional.empty();
    }
  }

  public abstract Map<RecipeField, List<String>> getFields();

  public static AbstractRecipe parseFrom(File f) {
    Preconditions.checkState(f.exists(), f.getAbsolutePath() + " does not exist");
    Preconditions.checkState(accept(f), f.getAbsolutePath() + " is not a " + RECIPE_FILETYPE + " file");

    try {
      List<String> lines = Resources.readLines(f.toURI().toURL(), Charsets.UTF_8).stream()
                                    .filter(line -> !isNullOrEmpty(line))
                                    .collect(Collectors.toList());
      Preconditions.checkState(lines.size() > 1, f.getAbsolutePath() + "has no text");

      return parseFrom(f.getAbsolutePath(), lines);
    } catch (IOException ex) {
      throw Throwables.propagate(ex);
    }
  }

  private static AbstractRecipe parseFrom(String filePath, List<String> lines) {
    Builder builder = ImmutableRecipe.builder();
    for (int i = 0; i < lines.size(); i ++) {
      String fieldLine = lines.get(i);
      Optional<RecipeField> recipeFieldMaybe = RecipeField.matchingField(fieldLine);

      if (!recipeFieldMaybe.isPresent()) {
        throw new IllegalStateException(filePath + " is not a list of recipe steps. Check the format!");
      }

      RecipeField field = recipeFieldMaybe.get();
      if (field.isMultiline()) {
        int j = i + 1;
        List<String> pieces = new ArrayList<>();
        for (; j < lines.size() && !RecipeField.matchingField(lines.get(j)).isPresent(); j++) {
          String line = lines.get(j);
          pieces.add(line.substring(line.indexOf('-') + 1).trim());
        }
        builder.putFields(field, pieces);
        i = j - 1;
      } else {
        builder.putFields(field, Collections.singletonList(parseField(field, fieldLine)));
      }
    }

    return builder.build();
  }

  private static String parseField(RecipeField field, String line) {
    return line.substring(line.indexOf(field.fieldName + ':') + field.fieldName.length() + 2);
  }

  private static boolean accept(File f) {
    return f.getName().endsWith(RECIPE_FILETYPE);
  }
}
