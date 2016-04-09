package com.szabowexler.cookbook.recipes.parts;

import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Strings;

@Immutable
public abstract class AbstractIngredient {
  public abstract int getQuantity();
  public abstract Optional<String> getUnit();
  public abstract String getDescription();

  public static AbstractIngredient parse(String s) {
    int startUnitSubstring = s.indexOf("(") + 1;
    int endUnitSubstring = s.indexOf(")");

    int quantity = Integer.parseInt(s.substring(0, startUnitSubstring - 1).trim());
    Optional<String> unit = Optional.ofNullable(Strings.emptyToNull(s.substring(startUnitSubstring, endUnitSubstring).trim()));
    String description = s.substring(endUnitSubstring + 1).trim();

    return ImmutableIngredient.builder()
                              .quantity(quantity)
                              .unit(unit)
                              .description(description)
                              .build();
  }
}
