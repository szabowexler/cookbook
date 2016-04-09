package com.szabowexler.cookbook.recipes.parts;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.immutables.value.Value.Immutable;

@Immutable
public abstract class AbstractTimeInterval {
  public abstract long getTime();
  public abstract TimeUnit getTimeUnit();

  public static AbstractTimeInterval parse(String s) {
    Matcher matcher = Pattern.compile("\\d+").matcher(s);
    matcher.find();

    int time = Integer.parseInt(s.substring(matcher.start(), matcher.end()));
    String units = matcher.replaceAll("").trim();

    return ImmutableTimeInterval.builder()
                                .time(time)
                                .timeUnit(toTimeUnit(units))
                                .build();
  }

  private static TimeUnit toTimeUnit(String unit) {
    switch(unit.toLowerCase()) {
      case "days":
        return TimeUnit.DAYS;
      case "hours":
        return TimeUnit.HOURS;
      case "minutes":
        return TimeUnit.MINUTES;
      case "seconds":
        return TimeUnit.SECONDS;
      default:
        throw new IllegalArgumentException(unit + " can't be parsed as a time unit");
    }
  }
}
