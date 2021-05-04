package segmentationtest;

import org.junit.Test;

public class SegmentationTest {
	public static final String PATH_TO_INPUT = "../test-images/input/";
	public static final String PATH_TO_OUTPUT = "../test-images/output/";
	
	@Test
	public void test() throws Exception {
		SegmentationTestRunner.run(PATH_TO_INPUT);
	}

}