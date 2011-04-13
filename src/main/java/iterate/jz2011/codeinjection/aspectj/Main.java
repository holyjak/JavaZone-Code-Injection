package iterate.jz2011.codeinjection.aspectj;


/**
 * Run the AspectJ example - inject logging of method arguments in the case of an exception to
 * a 3rd party class/method.
 * <p>
 * The code is injected when the target class is being loaded by a JVM by the AspectJ agent library.
 * <p>
 * See README for instructions how to run it.
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("\n############### Starting the AspectJ example...###############\n");

		try {

			String[] actualArgs = (args.length == 0)?
					new String[]{"I'm ok!", null, "failNow!", "won't get till me..."}
				: args;

			new TooQuiet3rdPartyClass().batchProcess(actualArgs);

		} catch (Exception e) {
			// ignore - already logged
		} finally {
			System.out.println("\n############### DONE with the AspectJ example ###############\n");
		}
	}

}
