package it.nuccioservizi.as400querier;

import java.util.Properties;

public enum Property {
  AS400_HOST, AS400_USERNAME, AS400_PASSWORD;

  public static void validate(final Properties properties) {
    for (final Property p : values()) {
      if (p.get(properties) == null)
        throw new IllegalArgumentException("Definire " + p.getKey());
    }
  }

  public String get(final Properties properties) {
    return properties.getProperty(getKey());
  }

  public String getKey() {
    return toString();
  }

  public boolean isSet(final Properties properties) {
    return "true".equals(get(properties));
  }

  public void set(final Properties properties, final String value) {
    properties.setProperty(getKey(), value);
  }
}
