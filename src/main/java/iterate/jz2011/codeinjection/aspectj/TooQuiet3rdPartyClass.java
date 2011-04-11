package iterate.jz2011.codeinjection.aspectj;

/**
 * A 3rd party class whose source code you haven't.
 * (Well, you do have them in this case but imagine you
 * only had the binary .class :-).)
 */
public class TooQuiet3rdPartyClass {

	/**
	 * A 3rd party class method called multiple times for different input values
	 * during a single invocation of a higher abstraction level public method
	 * and failing for one of the input values.
	 */
	private void failingMethod(String someArgument) {
		if("fail".equalsIgnoreCase(someArgument))
			throw new RuntimeException("I'm an evil method, I've failed and won't tell for what argument!");
	}

	public void batchProcess(String[] batchToProcess) {
		for (String currentArgument : batchToProcess) {
			this.failingMethod(currentArgument);
		}
	}

}
