package it.nuccioservizi.as400querier;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.mvel2.templates.CompiledTemplate;
import org.mvel2.templates.TemplateCompiler;
import org.mvel2.templates.TemplateRuntime;

public class As400Query {
  private static final Pattern QUERY_PATTERN = Pattern
      .compile("\\A(UPDATE\\s+[A-Z0-9$_.]+\\s+SET(?:.|\\s)+|SELECT\\s+(?:[A-Z0-9$_()]+ AS ([A-Z0-9_]+),\\s*)*[A-Z0-9$_()]+ AS ([A-Z0-9_]+)\\s+FROM [A-Z0-9$_.]+)\\s+WHERE (?:.|\\s)+");

  public static As400Query fromFile(final File queryFile) throws IOException {
    return new As400Query(loadFile(queryFile));
  }

  private static String loadFile(final File queryFile) throws IOException {
    try (final Scanner scanner = new Scanner(queryFile)) {
      return scanner.useDelimiter("\\Z").next();
    }
  }

  private final CompiledTemplate queryTemplate;

  As400Query(final String query) {
    final Matcher queryMatcher = QUERY_PATTERN.matcher(query);
    if (!queryMatcher.matches()) {
      throw new IllegalArgumentException("Invalid query.");
    }
    this.queryTemplate = TemplateCompiler.compileTemplate(query);
  }

  public String getSqlQuery(final Map<String, String> vars) {
    return (String) TemplateRuntime.execute(queryTemplate, vars);
  }
}
