package iterate.jz2011.codeinjection.aspectj;

/**
 * Run the AspectJ example - adding parameter logging upon failure to
 * a 3rd party class/method.
 *
 * @author jholy
 *
 */
public class Main {

	public static void main(String[] args) {
		String[] actualArgs = (args.length == 0)?
				new String[]{"I'm ok!", null, "fail", "won't get till me..."}
			: args;
		new TooQuiet3rdPartyClass().batchProcess(actualArgs);
	}

}
