package com.szabowexler.cookbook.recipes;

import java.net.URL;
import java.util.List;
import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.szabowexler.cookbook.recipes.parts.AbstractIngredient;
import com.szabowexler.cookbook.recipes.parts.AbstractTimeInterval;

@Immutable
public abstract class AbstractRecipe {
  public abstract String getName();
  public abstract Optional<URL> getSource();
  public abstract AbstractTimeInterval getPrepTime();
  public abstract AbstractTimeInterval getCookTime();
  public abstract int getPortions();
  public abstract Optional<Integer> getCalories();

  public abstract Optional<String> getHint();

  public abstract List<AbstractIngredient> getIngredients();
  public abstract List<String> getSteps();
}
