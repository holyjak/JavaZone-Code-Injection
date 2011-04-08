package iterate.jz2011.codeinjection.javassist;

import java.io.IOException;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.NotFoundException;

/**
 *
 * @author jholy
 * @see http://theholyjava.wordpress.com/2010/06/25/implementing-build-time-instrumentation-with-javassist/
 */
public class JavassistInstrumenter {

	public void insertTimingIntoMethod() throws NotFoundException, CannotCompileException, IOException {
		// Advice my.example.TargetClass.myMethod(..) with a before and after advices
		final ClassPool pool = ClassPool.getDefault();
		final CtClass compiledClass = pool.get("my.example.TargetClass");
		final CtMethod method = compiledClass.getDeclaredMethod("myMethod");

		 method.addLocalVariable("startMs", CtClass.longType);
		 method.insertBefore("startMs = System.currentTimeMillis();");
		 method.insertAfter("{final long endMs = System.currentTimeMillis();" +
		   "System.out.println(\"Executed in ms: \" + (endMs-startMs));}");

		compiledClass.writeFile("/tmp/modifiedClassesFolder");
		// Enjoy the new /tmp/modifiedClassesFolder/my/example/TargetClass.class

	}

}
