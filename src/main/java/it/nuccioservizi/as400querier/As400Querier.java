package it.nuccioservizi.as400querier;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;

public class As400Querier {

	private final AppProperties	properties;
	private Statement						statement	= null;

	public As400Querier(final AppProperties properties) {
		this.properties = properties;
	}

	public ResultSet executeQuery(final As400Query query, final Map<String, String> vars) throws SQLException {
		return getStatement().executeQuery(query.getSqlQuery(vars));
	}

	private synchronized Statement getStatement() throws SQLException {
		if (statement == null) {
			statement = newStatement();
		}
		return statement;
	}

	private Statement newStatement() throws SQLException {
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
