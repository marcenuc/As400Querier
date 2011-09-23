package it.nuccioservizi.tailor.as400;

import java.util.Properties;

public enum Property {
	AS400_HOST,
	AS400_USERNAME,
	AS400_PASSWORD,
	AS400_ENABLE_PORTMAPPING;

	public static void validate(final Properties properties) {
		for (final Property p : values()) {
			if (p.get(properties) == null)
				throw new IllegalArgumentException("Definire la proprietà " + p.getKey());
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
