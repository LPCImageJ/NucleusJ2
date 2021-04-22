package gred.nucleus.test.old;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;

import java.io.IOException;


public class SegmentationImagesIntegration {
	
	/*
	 * @param img
	 * @param vMin
	 * @param vMax
	 * @param outputImgString
	 */
/*
 public static void testStupid(ImagePlus img, short vMin, int vMax, String outputImgString ) throws FormatException {
 SegmentationParameters segmentationParameters = new SegmentationParameters();
 SegmentationCalling otsuModified = new SegmentationCalling(img, vMin, vMax, outputImgString);
 otsuModified.runSeveralImages2();
 }
 */
	
	
	/**
	 * @param input
	 * @param output
	 */
	public static void testStupidSeveralImages(String input, String output) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void testStupidSeveralImages(String input, String output, String config) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output, config);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		String pathToTest   = "/home/tridubos/Bureau/IMAGES_TEST/";
		String pathToOutput = "/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP";
		
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/Gros_Nucleols",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/Gros_Nucleols");
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/Noyaux_Calib_1_1_1",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/Noyaux_Calib_1_1_1",
		                        pathToTest + "/SEGMENTATION/Noyaux_Calib_1_1_1/config_calibration.txt");
		testStupidSeveralImages(pathToTest + "/SEGMENTATION/PB_RADIUS_CONVEXHULL",
		                        pathToTest + "/SEGMENTATION/SEGMENTATION_RESULTS/PB_RADIUS_CONVEXHULL");
		
		// testStupidSeveralImages(ExpectedResult, ExpectedResult, (short)6.0, 300000000,true);
        /*fw.GetFilesResultingOfAnalysis(inputTristan);
        fw.CompareAnalysisResult();
        OutputFileVerification fw = new OutputFileVerification();
        fw.GetFileResultExpected(ExpectedResult);
        fw.GetFilesOutputFolder(outputTristan);
        fw.GetFilesResultingOfAnalysis(outputTristan);
        fw.CompareAnalysisResult();
        */
		System.err.println("The program ended normally.");
	}
	
}
