package iterate.jz2011.codeinjection.aspectj;

/**
 * Run the AspectJ example - adding parameter logging upon failure to
 * a 3rd party class/method.
 * <p>
 * Run as follows:
 * {@code java "-javaagent:target/aspectj/aspectjweaver.jar" -classpath "target/classes:/path/to/aspectjrt-1.6.10.jar" iterate.jz2011.codeinjection.aspectj.Main}
 *
 */
public class Main {

	public static void main(String[] args) {
		System.out.println("\n############### Starting the AspectJ example...###############\n");

		try {

			String[] actualArgs = (args.length == 0)?
					new String[]{"I'm ok!", null, "fail", "won't get till me..."}
				: args;
			new TooQuiet3rdPartyClass().batchProcess(actualArgs);

		} finally {
			System.out.println("\n############### DONE with the AspectJ example ###############\n");
		}
	}

}
