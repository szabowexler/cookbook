package com.szabowexler.cookbook.recipes;

import java.util.Optional;

enum RecipeField {
  NAME("name", false),
  PREP_TIME("prepTime", false),
  COOK_TIME("cookTime", false),
  PORTIONS("portions", false),
  CALORIES("caloriesPerPortion", false),
  HINT("hint", false),
  INGREDIENTS("ingredients", true),
  STEPS("steps", true),;

  private final String fieldName;
  private final boolean isMultiline;

  RecipeField(String fieldName, boolean isMultiline) {
    this.fieldName = fieldName;
    this.isMultiline = isMultiline;
  }

  boolean isMultiline() {
    return isMultiline;
  }

  String getFieldName() {
    return fieldName;
  }

  boolean matches(String line) {
    return line.startsWith(fieldName);
  }

  static Optional<RecipeField> matchingField(String line) {
    for (RecipeField field : values()) {
      if (field.matches(line)) {
        return Optional.of(field);
      }
    }
    return Optional.empty();
  }
}
