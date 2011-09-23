package it.nuccioservizi.tailor.as400;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

public class Querier {
	private static final Pattern	QUERY_PATTERN	= Pattern.compile("^SELECT (?:[A-Z0-9$_]+ AS ([a-z0-9]+), )*[A-Z0-9$_]+ AS ([a-z0-9]+) FROM .* WHERE .*");

	private static String getRecordString(final ResultSet record, final String campo) throws SQLException {
		String v = record.getString(campo.toUpperCase());
		if (v != null) {
			v = v.trim();
		}
		return v == null || v.isEmpty() ? null : v;
	}

	private static AppProperties loadProperties() throws IOException {
		try {
			return AppProperties.load();
		} catch (final IllegalArgumentException ex) {
			System.err.println("Mancano alcuni parametri di configurazione:");
			System.err.println(ex.getMessage());
			System.err.println("Creare o modificare il file '" + AppProperties.LOCAL_PROPERTIES_FILE_NAME + "'.");
			System.err.println("In alternativa si possono definire passando parametri nel formato -Dproprietà=valore al comando.");
			System.exit(1);
			throw new IllegalStateException(ex.getMessage());
		}
	}

	private static String loadQuery(final String queryName) throws IOException {
		InputStream is = null;
		try {
			is = Querier.class.getResourceAsStream("/queries/" + queryName + ".sql");
			return new Scanner(is).useDelimiter("\\Z").next();
		} finally {
			if (is != null)
				is.close();
		}

	}

	/**
	 * @param args
	 *          Query to submit to the as400 server.
	 * @throws IOException
	 *           For errors reading configuration file.
	 * @throws SQLException
	 *           For errors connecting to AS400.
	 */
	public static void main(final String[] args) throws IOException, SQLException {
		if (args.length != 1) {
			System.err.println("Fornire una query da eseguire.");
			System.exit(1);
			throw new IllegalStateException();
		}

		final String query = loadQuery(args[0]);
		final Matcher queryMatcher;
		{
			queryMatcher = QUERY_PATTERN.matcher(query);
			if (!queryMatcher.matches()) {
				System.err.println("Query non valida: " + query + ".");
				System.exit(1);
				throw new IllegalStateException();
			}
		}

		final JsonFactory jsonFactory = new JsonFactory();
		final JsonGenerator jg = jsonFactory.createJsonGenerator(System.out);
		jg.writeStartObject();

		jg.writeArrayFieldStart("columnNames");
		final String[] columnNames;
		{
			final int columnCount = queryMatcher.groupCount();
			columnNames = new String[columnCount];
			for (int i = 0; i < columnNames.length; ++i) {
				columnNames[i] = queryMatcher.group(i + 1);
				jg.writeString(columnNames[i]);
			}
		}
		jg.writeEndArray();

		final AppProperties properties = loadProperties();
		final Statement statement = newStatement(properties);
		final ResultSet results = statement.executeQuery(query);

		jg.writeArrayFieldStart("rows");
		while (results.next()) {
			jg.writeStartArray();
			for (int i = 0; i < columnNames.length; ++i) {
				jg.writeString(getRecordString(results, columnNames[i]));
			}
			jg.writeEndArray();
		}
		jg.writeEndArray();

		jg.writeEndObject();
		jg.close();
	}

	private static Statement newStatement(final AppProperties properties) throws SQLException {
		final String host = properties.get(Property.AS400_HOST);
		final String username = properties.get(Property.AS400_USERNAME);
		final String password = properties.get(Property.AS400_PASSWORD);

		if (!properties.isSet(Property.AS400_ENABLE_PORTMAPPING)) {
			// Make it work through ssh tunnel.
			final com.ibm.as400.access.AS400 as400 = new com.ibm.as400.access.AS400(host);
			as400.setServicePortsToDefault();
		}

		DriverManager.registerDriver(new com.ibm.as400.access.AS400JDBCDriver());
		final Connection connection = DriverManager.getConnection("jdbc:as400://" + host, username, password);

		return connection.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}

}
