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
 * You need to run the application with the folder with modified classes preceding the original
 * location on the classpath.
 *
 * @see http://theholyjava.wordpress.com/2010/06/25/implementing-build-time-instrumentation-with-javassist/
 */
public class JavassistInstrumenter {

	public void insertTimingIntoMethod() throws NotFoundException, CannotCompileException, IOException {
		Logger logger = Logger.getLogger("Javassist");
		final String targetClass = "iterate.jz2011.codeinjection.javassist.TargetClass";
		final String targetMethod = "myMethod";
		final String targetFolder = "./target/javassist";

		try {
			// Advice TargetClass.myMethod(..) with a before and after advices

			final ClassPool pool = ClassPool.getDefault();
			// Tell Javassist where to look for classes - into our ClassLoader
			pool.appendClassPath(new LoaderClassPath(getClass().getClassLoader()));
			final CtClass compiledClass = pool.get(targetClass);
			final CtMethod method = compiledClass.getDeclaredMethod(targetMethod);

			 method.addLocalVariable("startMs", CtClass.longType);
			 method.insertBefore("startMs = System.currentTimeMillis();");
			 method.insertAfter("{final long endMs = System.currentTimeMillis();" +
			   "iterate.jz2011.codeinjection.javassist.PerformanceMonitor.logPerformance(\"" +
			   targetMethod + "\",(endMs-startMs));}");

			compiledClass.writeFile(targetFolder);
			// Enjoy the new $targetFolder/iterate/jz2011/codeinjection/javassist/TargetClass.class

			logger.info("The modified class has been saved under " + targetFolder);
		} catch (NotFoundException e) {
			logger.warning("Failed to find the target class to modify, " +
					targetClass + ", verify that it ClassPool has been configured to look " +
					"into the right location");
		}
	}

	public static void main(String[] args) throws Exception {
		new JavassistInstrumenter().insertTimingIntoMethod();
	}

}
