package it.nuccioservizi.as400querier;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.ImmutableMap;

public class ArgsParser {
	private static final File	QUERIES_FOLDER	= new File("queries");
	private final String[]		args;

	ArgsParser(final String[] args) {
		if (args.length < 1) {
			throw new IllegalArgumentException("No default action. Tell me what you want to query...");
		}
		this.args = args;
	}

	As400Query getQuery() throws IOException {
		final String queryName = args[0];
		if (!queryName.matches("^[a-z0-9]+$")) {
			throw new IllegalArgumentException("Invalid query name.");
		}
		final File queryFile = new File(QUERIES_FOLDER, queryName + ".sql");
		if (!queryFile.isFile()) {
			throw new IllegalArgumentException("Query not found.");
		}

		return As400Query.fromFile(queryFile);
	}

	Map<String, String> getVars() {
		final ImmutableMap.Builder<String, String> varsBuilder = new ImmutableMap.Builder<String, String>();
		if (args.length > 1) {
			final Pattern varPattern = Pattern.compile("^([a-zA-Z]+)=([a-zA-Z0-9.%]+)$");
			for (int i = 1; i < args.length; ++i) {
				final Matcher m = varPattern.matcher(args[i]);
				if (m.matches()) {
					varsBuilder.put(m.group(1), m.group(2));
				}
				else {
					throw new IllegalArgumentException(args[i]);
				}
			}
		}
		return varsBuilder.build();
	}
}
