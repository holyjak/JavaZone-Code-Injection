package iterate.jz2011.codeinjection.javaproxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.PreparedStatement;
import java.util.LinkedList;
import java.util.List;

/**
 * Remember values passed into a sql statement via setString etc. for later logging.
 *
 * Source: http://theholyjava.wordpress.com/2009/05/23/a-logging-wrapper-around-prepareds/
 */
class LoggingStatementDecorator implements InvocationHandler {

    /** File's Subversion info (version etc.). */
    public static final String SVN_ID = "$id$";

    private List<List<Object>> batch = new LinkedList<List<Object>>();
    private List<Object> currentRow = new LinkedList<Object>();
    private PreparedStatement target;
    private boolean failed = false;

    public LoggingStatementDecorator(PreparedStatement target) {
        if (target == null) throw new IllegalArgumentException("'target' can't be null.");
        this.target = target;
    }

     // @see java.lang.reflect.InvocationHandler#invoke(java.lang.Object, java.lang.reflect.Method, java.lang.Object[]) */
    public Object invoke(Object proxy, Method method, Object[] args)
            throws Throwable {

        final Object result;

        try {
            result = method.invoke(target, args);
            failed = false;
        } catch (InvocationTargetException e) {
            failed = true;
            throw e.getTargetException();
        } catch (Exception e) {
            failed = true;
            throw e;
        }

        if ( method.getName().startsWith("setNull")
                && (args.length >=1 && Integer.TYPE == method.getParameterTypes()[0] ) ) {
            handleSetSomething((Integer) args[0], null);
        } else if ( method.getName().startsWith("set")
                && (args.length >=2 && Integer.TYPE == method.getParameterTypes()[0] ) ) {
            handleSetSomething((Integer) args[0], args[1]);
        } else if ("addBatch".equals(method.getName())) {
            handleAddBatch();
        }

        return result;
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

    /** Has the last method called on the Statement caused an exception? */
    public boolean isFailed() { return failed; }

    public String toString() { return "LoggingHandler[failed="+failed+"]"; }

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

    public PreparedStatement createProxy() {
        return (PreparedStatement) Proxy.newProxyInstance(
                PreparedStatement.class.getClassLoader(),
                new Class[] { PreparedStatement.class },
                this);
    };

}
