package edu.skidmore.cs276.lab04.webapps.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

/**
 * Creates the quiz schema and sample data on first startup when the target
 * database is empty.
 */
public final class DatabaseBootstrapper {
	private static final Logger LOG = Logger.getLogger(DatabaseBootstrapper.class);

	private DatabaseBootstrapper() {
	}

	public static void initializeIfNeeded(String url, String user, String password) {
		try (Connection conn = DriverManager.getConnection(url, user, password)) {
			if (quizzesTableExists(conn)) {
				LOG.info("Database already initialized; skipping bootstrap.");
				return;
			}

			LOG.info("No quiz tables found. Bootstrapping schema and seed data.");
			conn.setAutoCommit(false);
			executeSqlResource(conn, "/edu/skidmore/cs276/lab04/sql/schema.sql");
			executeSqlResource(conn, "/edu/skidmore/cs276/lab04/sql/data.sql");
			conn.commit();
			LOG.info("Database bootstrap complete.");
		} catch (SQLException e) {
			throw new RuntimeException("Failed to initialize the Sports Quiz database", e);
		}
	}

	private static boolean quizzesTableExists(Connection conn) throws SQLException {
		DatabaseMetaData metaData = conn.getMetaData();
		String catalog = conn.getCatalog();
		try (ResultSet resultSet = metaData.getTables(catalog, null, "quizzes", new String[] { "TABLE" })) {
			if (resultSet.next()) {
				return true;
			}
		}
		try (ResultSet resultSet = metaData.getTables(catalog, null, "QUIZZES", new String[] { "TABLE" })) {
			return resultSet.next();
		}
	}

	private static void executeSqlResource(Connection conn, String resourcePath) {
		List<String> statements = loadStatements(resourcePath);
		try (Statement statement = conn.createStatement()) {
			for (String sql : statements) {
				statement.execute(sql);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed executing SQL from " + resourcePath, e);
		}
	}

	private static List<String> loadStatements(String resourcePath) {
		try (InputStream input = DatabaseBootstrapper.class.getResourceAsStream(resourcePath)) {
			if (input == null) {
				throw new RuntimeException("SQL resource not found: " + resourcePath);
			}

			List<String> statements = new ArrayList<>();
			StringBuilder current = new StringBuilder();
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, StandardCharsets.UTF_8))) {
				String line;
				while ((line = reader.readLine()) != null) {
					String trimmedLine = stripComment(line).trim();
					if (trimmedLine.isEmpty()) {
						continue;
					}

					current.append(trimmedLine).append(' ');
					if (trimmedLine.endsWith(";")) {
						String sql = current.toString().trim();
						statements.add(sql.substring(0, sql.length() - 1));
						current.setLength(0);
					}
				}
			}
			return statements;
		} catch (IOException e) {
			throw new RuntimeException("Unable to read SQL resource: " + resourcePath, e);
		}
	}

	private static String stripComment(String line) {
		int commentIndex = line.indexOf("--");
		if (commentIndex >= 0) {
			return line.substring(0, commentIndex);
		}
		return line;
	}
}
