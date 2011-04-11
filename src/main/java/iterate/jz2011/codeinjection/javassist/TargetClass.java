package iterate.jz2011.codeinjection.javassist;

import java.util.logging.Logger;

public class TargetClass {

	/**
	 * Method whose execution time we want to measure
	 */
	public void myMethod() {
		try {
			Thread.sleep(100);
			Logger.getLogger("Javassist").info("Method done!");
		} catch (InterruptedException e) {
		}
	}

	public void myMethodSlower() {
		try {
			Thread.sleep(500);
			Logger.getLogger("Javassist").info("Method done!");
		} catch (InterruptedException e) {
		}
	}

}
