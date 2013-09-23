package it.nuccioservizi.as400querier;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class AppProperties {

  public static final String LOCAL_PROPERTIES_FILE_NAME = "local.properties";
  private static final String DEFAULT_PROPERTIES_FILE_NAME = "/defaults.properties";

  public synchronized static AppProperties load() throws IOException {
    final Properties defaultProperties = new Properties();
    loadPropertiesFromResource(DEFAULT_PROPERTIES_FILE_NAME, defaultProperties);

    final Properties properties = new Properties(defaultProperties);
    try {
      loadPropertiesFromFile(LOCAL_PROPERTIES_FILE_NAME, properties);
    } catch (final FileNotFoundException e) {
      // No local properties.
      // TODO add logging and issue a info/debug message here.
    }
    return new AppProperties(properties);
  }

  private static void loadPropertiesFromFile(
      final String propertiesFileName,
      final Properties properties) throws IOException {
    try (final InputStream in = new FileInputStream(propertiesFileName)) {
      properties.load(in);
    }
  }

  private static void loadPropertiesFromResource(
      final String propertiesResourceName,
      final Properties properties) throws IOException {
    try (final InputStream in = AppProperties.class
        .getResourceAsStream(propertiesResourceName)) {
      properties.load(in);
    }
  }

  private final Properties properties;

  private AppProperties(final Properties properties) {
    Property.validate(properties);
    this.properties = properties;
  }

  public synchronized String get(final Property property) {
    return property.get(properties);
  }

  public synchronized boolean isSet(final Property property) {
    return property.isSet(properties);
  }

  public synchronized void save() throws IOException {
    try (final OutputStream out = new FileOutputStream(
        LOCAL_PROPERTIES_FILE_NAME)) {
      properties.store(
          out,
          "--- Edit this file to configure the application ---");
    }
  }

  public synchronized void set(final Property property, final String value) {
    property.set(properties, value);
  }

}
