package it.nuccioservizi.as400querier;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import org.codehaus.jackson.JsonFactory;
import org.codehaus.jackson.JsonGenerator;

import com.google.common.base.CaseFormat;

public class AppCli {


	private static String getRecordString(final ResultSet record, final int columnIndex) throws SQLException {
		String v = record.getString(columnIndex);
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

	/**
	 * @param args
	 *          Query to submit to the as400 server.
	 * @throws IOException
	 *           For errors reading configuration file.
	 * @throws SQLException
	 *           For errors connecting to AS400.
	 */
	public static void main(final String[] args) throws IOException, SQLException {
		final ArgsParser argsParser = new ArgsParser(args);

		final JsonFactory jsonFactory = new JsonFactory();
		final JsonGenerator jg = jsonFactory.createJsonGenerator(System.out);
		jg.writeStartObject();

		final AppProperties properties = loadProperties();
		final As400Querier as400Querier = new As400Querier(properties);
		final ResultSet results = as400Querier.executeQuery(argsParser.getQuery(), argsParser.getVars());

		final ResultSetMetaData metadata = results.getMetaData();
		final int lastColumnIndex = metadata.getColumnCount();

		jg.writeArrayFieldStart("columnNames");
		for (int columnIndex = 1; columnIndex <= lastColumnIndex; ++columnIndex) {
			jg.writeString(CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.LOWER_CAMEL, metadata.getColumnLabel(columnIndex)));
		}
		jg.writeEndArray();

		jg.writeArrayFieldStart("rows");
		while (results.next()) {
			jg.writeStartArray();
			for (int columnIndex = 1; columnIndex <= lastColumnIndex; ++columnIndex) {
				jg.writeString(getRecordString(results, columnIndex));
			}
			jg.writeEndArray();
		}
		jg.writeEndArray();

		jg.writeEndObject();
		jg.close();
	}

}
