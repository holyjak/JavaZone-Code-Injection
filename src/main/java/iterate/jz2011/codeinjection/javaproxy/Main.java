package iterate.jz2011.codeinjection.javaproxy;

import java.sql.BatchUpdateException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.jakubholy.testing.dbunit.embeddeddb.EmbeddedDbTester;

public class Main {

	private static final int BATCH_SIZE = 2;

	private static final String ILLEGAL_VALUE = "Only values of 10 or less characters are allowed";

	private static EmbeddedDbTester testDb = new EmbeddedDbTester();

	private void failingJdbcBatchInsert(Connection connection, Map<Integer, String> data) throws SQLException {

		PreparedStatement rawPrepStmt = connection.prepareStatement("INSERT INTO my_test_schema.my_test_table (id,some_text) VALUES (?,?)");
		PreparedStatement loggingPrepStmt = LoggingStatementDecorator.createProxy(rawPrepStmt);

		int batchCounter = 0;
		for (Entry<Integer, String> row : data.entrySet()) {
			loggingPrepStmt.setInt(1, row.getKey());
			loggingPrepStmt.setString(2, row.getValue());
			loggingPrepStmt.addBatch();
			++batchCounter;

			if (batchCounter % BATCH_SIZE == 0)
				loggingPrepStmt.executeBatch();
		}

		// Execute remaining batches if any
		if (batchCounter % BATCH_SIZE != 0)
			loggingPrepStmt.executeBatch();

	}

	/**
	 * Initialize DB, perform updates.
	 */
	public static void main(String[] args) throws Exception {
		System.out.println("\n############### Starting the Java Proxy example... ###############\n");

		try {
			// Create the DB from the DDL
			try {
				net.jakubholy.testing.dbunit.embeddeddb.DatabaseCreator.createAndInitializeTestDb();
			} catch (BatchUpdateException e) {/* perhaps the DB exists already*/}

			// Initialize DB connection, clear existing data from a previous run
			testDb.onSetup();

			// Prepare & insert data!
			@SuppressWarnings("serial")
			Map<Integer, String> data = new HashMap<Integer, String>() {{
				// Batch 0
				put(100, "ok value 1");
				put(200, "ok value 2");
				// Batch 1
				put(300, "ok value 3");
				put(300, ILLEGAL_VALUE);
				// Batch 2
				put(400, "ok value 4");
			}};

			new Main().failingJdbcBatchInsert(testDb.getSqlConnection(), data);
		} finally {
			System.out.println("\n############### DONE with the Java Proxy example ###############\n");
		}

	}

}
