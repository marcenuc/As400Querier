package it.nuccioservizi.as400querier;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.Map;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import com.google.common.collect.ImmutableMap;

public class As400QueryTest {
  private static class FilenameFilterByExtension implements FilenameFilter {
    private final String extension;

    public FilenameFilterByExtension(final String extension) {
      this.extension = "." + extension;
    }

    @Override
    public boolean accept(final File dir, final String name) {
      return name.endsWith(extension) && new File(dir, name).isFile();
    }
  }

  @Test(dataProvider = "predefinedQueries")
  public static void itShouldAcceptAllPredefinedQueries(final File queryFile)
      throws IOException {
    try {
      As400Query.fromFile(queryFile);
    } catch (final IllegalArgumentException ex) {
      Assert.fail(queryFile.getName(), ex);
    }
  }

  @SuppressWarnings("unused")
  @Test
  public static void itShouldAllowForFunctionsInColumns() {
    new As400Query("SELECT SUM(A) AS SA FROM T WHERE 1");
  }

  @Test(dataProvider = "parameterizedQueries")
  public static void itShouldInterpolateParameters(
      final String sourceQuery,
      final Map<String, String> vars,
      final String expectedParsedQuery) {
    Assert.assertEquals(
        new As400Query(sourceQuery).toSql(vars),
        expectedParsedQuery);
  }

  @Test(expectedExceptions = Exception.class, expectedExceptionsMessageRegExp = "(?s)\\[Error: could not access: a; in class:.*")
  public static void itShouldThrowIllegalArgumentExceptionIfMissingQueryVars() {
    final As400Query query = new As400Query("SELECT A AS A FROM T WHERE @{a}");
    query.toSql(ImmutableMap.of("c", "c"));
  }

  @DataProvider
  public static Object[][] parameterizedQueries() {
    return new Object[][] { //
    { "SELECT A AS A FROM T WHERE A = '@{a}'", ImmutableMap.of("a", "A_VAL"),
        "SELECT A AS A FROM T WHERE A = 'A_VAL'" } //
    };
  }

  @DataProvider
  public static Object[][] predefinedQueries() {
    final File[] queryFiles = new File("queries")
        .listFiles(new FilenameFilterByExtension("sql"));

    final Object[][] list = new Object[queryFiles.length][];
    for (int i = 0; i < queryFiles.length; ++i) {
      list[i] = new Object[] { queryFiles[i] };
    }
    return list;
  }

  @Test
  public static void itDetectsUpdateQueries() {
    final As400Query update = new As400Query("UPDATE T SET A = 1 WHERE B = 2");
    Assert.assertTrue(update.isUpdate(), "shoud be an UPDATE query");

    final As400Query select = new As400Query("SELECT A AS B FROM T WHERE C = 1");
    Assert.assertFalse(select.isUpdate(), "should not be an UPDATE query");
  }
}
