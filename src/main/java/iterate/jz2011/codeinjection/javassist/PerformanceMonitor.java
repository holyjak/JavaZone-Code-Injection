package iterate.jz2011.codeinjection.javassist;

import java.util.logging.Logger;

public class PerformanceMonitor {

	private static PerformanceMonitor instance = new PerformanceMonitor();

	private PerformanceMonitor() {}

	public static void logPerformance(String methodName, long executionTime) {
		instance.instanceLogPerformance(methodName, executionTime);
	}

	private void instanceLogPerformance(String methodName, long executionTime) {
		// We could do something fance like computing max,min,avg etc. but for now
		// will just log the time
		Logger.getLogger("Javassist").info(
				String.format("THE INJECTED CODE SAYS: the method  %s executed in %d ms"
						, methodName, executionTime));
	}

}
