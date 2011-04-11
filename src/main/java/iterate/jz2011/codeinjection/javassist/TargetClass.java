package iterate.jz2011.codeinjection.javassist;

public class TargetClass {

	/**
	 * Method whose execution time we want to measure
	 */
	public void myMethod() {
		try {
			Thread.sleep(100);
		} catch (InterruptedException e) {}
	}

}
