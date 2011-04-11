package iterate.jz2011.codeinjection.javassist;

/**
 * Run the Javassist example - inject the measurement of method execution time into a target class.
 * <p>
 * To inject the code you need to run the main method of JavassistInstrumenter and then you
 * need to execute this Main with the modified TargetClass class at the beginning of the class path.
 * <p>
 * See README for instructions how to run it.
 */
public class Main {

	public static void main(String[] args) throws Exception {
		System.out.println("\n############### Starting the Javassist example... ###############\n");
		try {
			// We should be instantiating the modified TargetClass binary...
			TargetClass targetClass = new TargetClass();
			targetClass.myMethod();
			targetClass.myMethodSlower();
		} finally {
			System.out.println("\n############### DONE with the Javassist example ###############\n");
		}
	}

}
