package iterate.jz2011.codeinjection.aspectj;

import java.util.logging.Logger;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

/**
 * Enhance the failingMethod of the closed-source TooQuiet3rdPartyClass to
 * log the value of its argument when it fails so that we can find out
 * why it is failing.
 *
 * To show more of AspecJ's power I'm using an {@code @Around} advice instead of
 * {@code @org.aspectj.lang.annotation.AfterThrowing(exception name)}.
 *
 * The code can be injected ("woven") into the target class at compile time by
 * a special tool or at runtime using the AspectJ's Java 5 agent:
 * {@code java -javaagent:/path/to/aspectjweaver.jar YourMainClass}
 */
@Aspect
public class LoggingAspect {

	/**
	 * The method to be executed *instead of* the target one, namely {@code failingMethod}.
	 *
	 * The method has an arbitrary name, takes a special AspectJ argument, which
	 * gives it access to information about the original call, and returns an Object.
	 * It's decorated with @Around so that AspectJ knows how to inject it and
	 * specifies a pattern for finding out the methods to replace.
	 *
	 * The method could have different singatures based on what "service" we want from
	 * the AspectJ runtime.
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
			return null; // normally we would just rethrow it...
		}
	}

}
