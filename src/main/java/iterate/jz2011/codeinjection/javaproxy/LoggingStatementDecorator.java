package iterate.jz2011.codeinjection.javaproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.BatchUpdateException;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Remember values passed into a {@link PreparedStatement} via setString etc. for later logging.
 *
 * Source: http://theholyjava.wordpress.com/2009/05/23/a-logging-wrapper-around-prepareds/
 * (partly adjusted)
 */
class LoggingStatementDecorator implements InvocationHandler {

	private PreparedStatement target;
    private List<List<Object>> batch = new LinkedList<List<Object>>();
    private List<Object> currentRow = new LinkedList<Object>();
    private int successfulBatchCounter = 0;

    private LoggingStatementDecorator(PreparedStatement target) {
        if (target == null) throw new IllegalArgumentException("'target' can't be null.");
        this.target = target;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        try {
            Object result = method.invoke(target, args);
            updateLog(method, args);
            return result;
        } catch (InvocationTargetException e) {
        	Throwable cause = e.getTargetException();
        	if (tryHandleFailure(cause))
        		return null;
        	else
        		throw cause;
        }

    }

	private boolean tryHandleFailure(Throwable cause) {
		if (cause instanceof BatchUpdateException) {
			int failedBatchNr = successfulBatchCounter + 1;
			Logger.getLogger("JavaProxy").warning(
					"THE INJECTED CODE SAYS: " +
					"Batch update failed for batch# " + failedBatchNr +
					" (counting from 1) with values: [" +
					getValuesAsCsv() + "]. Cause: " + cause.getMessage());
			return true;
		}
		return false;
	}

	/**
	 * Store set* arguments, reset batch number etc as appropriate for the current type of call.
	 */
	private void updateLog(Method method, Object[] args) {
		// All the interesting set<Something> methods have the signature: (int index, Something value [, ...])
		if (method.getName().startsWith("setNull")
                && (args.length >=1 && Integer.TYPE == method.getParameterTypes()[0] ) ) {
            handleSetSomething((Integer) args[0], null);
        } else if (method.getName().startsWith("set")
                && (args.length >=2 && Integer.TYPE == method.getParameterTypes()[0] ) ) {
            handleSetSomething((Integer) args[0], args[1]);
        } else if ("addBatch".equals(method.getName())) {
            handleAddBatch();
        } else if ("executeBatch".equals(method.getName())) {
            handleExecuteBatch();
        }
	}

    private void handleExecuteBatch() {
    	++successfulBatchCounter;
    	batch.clear();

	}

	private void handleSetSomething(int index, Object value) {
        currentRow.add(value);
    }

    private void handleAddBatch() {
        batch.add(currentRow);
        currentRow = new LinkedList<Object>();
    }

    public List<List<Object>> getValues() {
        return batch;
    }

    public PreparedStatement getTarget() { return target; }

    /** Values as comma-separated values. */
    public String getValuesAsCsv() {
        StringBuilder csv = new StringBuilder();
        for (List<Object> row : getValues()) {
            for (Object field : row) {
                // Escape Strings
                if (field instanceof String) {
                    field = "'" + ((String) field).replaceAll("'", "''") + "'";
                }
                csv.append(field).append(",");
            }
            csv.append("\n");
        }
        return csv.toString();
    } /* getValuesAsCsv */

    public static PreparedStatement createProxy(PreparedStatement target) {
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class[] { PreparedStatement.class },
                new LoggingStatementDecorator(target));
    };

}
