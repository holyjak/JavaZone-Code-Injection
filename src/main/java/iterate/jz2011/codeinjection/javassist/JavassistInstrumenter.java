package iterate.jz2011.codeinjection.javassist;

import java.io.IOException;
import java.util.logging.Logger;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.LoaderClassPath;
import javassist.NotFoundException;

/**
 * Load the TargetClass.class file, modify it to measure and log the execution time of
 * its method {@code myMethod} and save it into a file.
 *
 * @see http://theholyjava.wordpress.com/2010/06/25/implementing-build-time-instrumentation-with-javassist/
 */
public class JavassistInstrumenter {

	public void insertTimingIntoMethod(String targetClass, String targetMethod) throws NotFoundException, CannotCompileException, IOException {
		Logger logger = Logger.getLogger("Javassist");
		final String targetFolder = "./target/javassist";

		try {
			final ClassPool pool = ClassPool.getDefault();
			// Tell Javassist where to look for classes - into our ClassLoader
			pool.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));
			final CtClass compiledClass = pool.get(targetClass);
			final CtMethod method = compiledClass.getDeclaredMethod(targetMethod);

			// Add something to the beginning of the method:
			method.addLocalVariable("startMs", CtClass.longType);
			method.insertBefore("startMs = System.currentTimeMillis();");
			// And also to its very end:
			method.insertAfter("{final long endMs = System.currentTimeMillis();" +
			   "iterate.jz2011.codeinjection.javassist.PerformanceMonitor.logPerformance(\"" +
			   targetMethod + "\",(endMs-startMs));}");

			compiledClass.writeFile(targetFolder);
			// Enjoy the new $targetFolder/iterate/jz2011/codeinjection/javassist/TargetClass.class

			logger.info(targetClass + "." + targetMethod +
					" has been modified and saved under " + targetFolder);
		} catch (NotFoundException e) {
			logger.warning("Failed to find the target class to modify, " +
					targetClass + ", verify that it ClassPool has been configured to look " +
					"into the right location");
		}
	}

	/**
	 * Run this to perform the injection of the code - the target class must be already
	 * compiled and on the class path and its modified version will be written into
	 * a special output folder.
	 */
	public static void main(String[] args) throws Exception {
		final String defaultTargetClass = "iterate.jz2011.codeinjection.javassist.TargetClass";
		final String defaultTargetMethod = "myMethod";
		final boolean targetProvided = args.length == 2;

		new JavassistInstrumenter().insertTimingIntoMethod(
				targetProvided? args[0] : defaultTargetClass
				, targetProvided? args[1] : defaultTargetMethod
		);
	}

}
