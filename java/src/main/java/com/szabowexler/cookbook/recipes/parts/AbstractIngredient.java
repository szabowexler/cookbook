package com.szabowexler.cookbook.recipes.parts;

import java.util.Optional;

import org.immutables.value.Value.Immutable;

import com.google.common.base.Preconditions;
import com.szabowexler.cookbook.recipes.parts.ImmutableIngredient.Builder;

@Immutable
public abstract class AbstractIngredient {
  public abstract String getQuantity();
  public abstract Optional<String> getUnit();
  public abstract String getDescription();

  public static AbstractIngredient parse(String s) {
    int startUnitSubstring = s.indexOf("(") + 1;
    int endUnitSubstring = s.indexOf(")");
    String[] quantityAndUnit = s.substring(startUnitSubstring, endUnitSubstring).split("\\s+", 2);
    Preconditions.checkState(quantityAndUnit.length == 1 || quantityAndUnit.length == 2,
        "Unable to parse quantity/unit in " + s);

    Builder builder = ImmutableIngredient.builder();
    builder.quantity(quantityAndUnit[0].trim());
    if (quantityAndUnit.length == 2) {
      builder.unit(quantityAndUnit[1].trim());
    }
    builder.description(s.substring(endUnitSubstring + 1).trim());

    return builder.build();
  }

  public String toTexString() {
    String amount;
    Optional<String> unitMaybe = getUnit();
    if (unitMaybe.isPresent()) {
      amount = "\\unit[" + getQuantity() + "]{" + unitMaybe.get() + "}";
    } else {
      amount = getQuantity();
    }

    return amount + " & " + getDescription() + " \\\\";
  }
}
