package com.szabowexler.cookbook;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;

public class CookbookRunner {
  private final static Logger LOG = LoggerFactory.getLogger(CookbookRunner.class);

  private void run(List<File> files) {
    LOG.info("Running: {} files", files.size());
    files.forEach(this::texify);
  }

  private void texify(File f) {
    if (!f.exists()) {
      LOG.error("{}: does not exist, ignoring", f.getAbsolutePath());
      return;
    }

    if (!f.getName().endsWith(".recipe")) {
      LOG.error("{}: does not end with .recipe, ignoring", f.getAbsolutePath());
      return;
    }
  }

  public static void main(String[] args) {
    if (args.length == 0) {
      System.out.println("Usage: give us a list of (absolute) *.recipe files, and we'll make 'em pretty!");
      System.exit(0);
    }

    Guice.createInjector(new CookbookModule()).getInstance(CookbookRunner.class)
         .run(Arrays.asList(args).stream()
                    .map(File::new)
                    .collect(Collectors.toList()));
  }
}
