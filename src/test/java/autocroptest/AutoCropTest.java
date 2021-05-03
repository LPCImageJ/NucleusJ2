package autocroptest;

import gred.nucleus.autocrop.Box;
import org.junit.Test;


public class AutoCropTest {
	public static final String PATH_TO_INPUT = "../test-images/input/";
	public static final String PATH_TO_OUTPUT = "../test-images/output/";
	// Make sure the output folder is empty before running the test otherwise the checker might use the wrong files

	@Test
	public void test() throws Exception {
		AutocropTestRunner.run(PATH_TO_INPUT);
	}
	
}
