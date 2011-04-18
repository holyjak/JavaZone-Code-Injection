package iterate.jz2011.codeinjection.aspectj;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Enhance the failingMethod of the closed-source TooQuiet3rdPartyClass to
 * log the value of its argument when it fails so that we can find out
 * why it is failing.
 * <p>
 * To show more of AspecJ's power I'm using an {@code @Around} advice instead of
 * {@code @org.aspectj.lang.annotation.AfterThrowing(exception name)}, which would
 * be more suitable in this case.
 * <p>
 * The code can be injected ("woven") into the target class at build time by
 * an Ant task - iajc - or at runtime using the AspectJ's Java 5 agent library:
 * {@code java -javaagent:/path/to/aspectjweaver.jar YourMainClass}
 */
@Aspect
public class LoggingAspect {

	/**
	 * The method to be executed *instead of* the target one, namely {@code failingMethod}.
	 * <p>
	 * The method is public, has an arbitrary name, takes a special AspectJ argument, which
	 * gives it access to information about the original call, and returns an Object.
	 * It's decorated with @Around so that AspectJ knows how to inject it and
	 * specifies a pattern for finding out the method(s) to replace.
	 * <p>
	 * The method could have different signatures based on what "service" we want from
	 * the AspectJ runtime.
	 *
	 * @see http://blog.espenberntsen.net/2010/03/20/aspectj-cheat-sheet/ - good description of pointcut expressions
	 * @see http://www.eclipse.org/aspectj/doc/released/adk15notebook/ataspectj-pcadvice.html
	 * @see http://www.eclipse.org/aspectj/doc/released/quick5.pdf - the official cheat sheet
	 */
	@Around("execution(private void TooQuiet3rdPartyClass.failingMethod(..))")
	public Object interceptAndLog(ProceedingJoinPoint invocation) throws Throwable {
		try {
			return invocation.proceed();
		} catch (Exception e) {
			Logger.getLogger("AspectJ").warning(
				"THE INJECTED CODE SAYS: the method " +
				invocation.getSignature().getName() + " failed for the input '" +
				invocation.getArgs()[0] + "'. Original exception: " + e);
			throw e;
		}
	}

}
