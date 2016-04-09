package com.szabowexler.cookbook.tex;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Throwables;
import com.google.common.io.Resources;

public class CombinedCookbookTexGenerator {
  private static final Logger LOG = LoggerFactory.getLogger(CombinedCookbookTexGenerator.class);
  private static final Joiner NEWLINE_JOINER = Joiner.on("\n");

  private static final String DTX_FILENAME = "xcookybooky.dtx";
  private static final String INS_FILENAME = "xcookybooky.ins";

  private CombinedCookbookTexGenerator() {}

  public static String generateCookbookTex(Map<String, List<File>> categoryTexFiles) {
    List<String> texEntries = categoryTexFiles.entrySet().stream()
                                              .sorted(Comparator.comparing(Entry::getKey))
                                              .map(CombinedCookbookTexGenerator::generateTex)
                                              .collect(Collectors.toList());
    String includes = NEWLINE_JOINER.join(texEntries);
    try {
      List<String> lines = Resources.readLines(Resources.getResource("cookbook.tex"), Charsets.UTF_8);
      StringBuilder builder = new StringBuilder();

      for (String line : lines) {
        if (line.equals("{{RECIPE_ENTRIES}}")) {
          builder.append(includes);
        } else {
          builder.append(line)
                 .append("\n");
        }
      }

      return builder.toString();
    } catch (IOException ex) {
      LOG.error("Unable to generate cookbook wrapper tex!", ex);
      throw Throwables.propagate(ex);
    }
  }

  private static String generateTex(Entry<String, List<File>> category) {
    String categoryName = category.getKey();
    List<File> texFiles = category.getValue();

    StringBuilder builder = new StringBuilder();

    builder.append("\t\\section{").append(categoryName).append("}\n");
    texFiles.forEach(texFile -> {
      builder.append("\t\t\\input{").append(texFile.getAbsolutePath()).append("}\n")
             .append("\t\t\\newpage\n\n");
    });

    return builder.toString();
  }

  public static void writeCookyBookyFilesForTex(File texDirectory) throws IOException {
    Path templateDtx = Paths.get(texDirectory.getAbsolutePath(), DTX_FILENAME);
    Path templateIns = Paths.get(texDirectory.getAbsolutePath(), INS_FILENAME);

    Files.write(templateDtx, loadBytes(DTX_FILENAME));
    Files.write(templateIns, loadBytes(INS_FILENAME));
  }

  private static byte[] loadBytes(String resource) throws IOException {
    return NEWLINE_JOINER.join(Resources.readLines(Resources.getResource(resource), Charsets.UTF_8)).getBytes();
  }
}
