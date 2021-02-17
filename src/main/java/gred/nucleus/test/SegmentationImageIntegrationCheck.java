package gred.nucleus.test;

import gred.nucleus.AnalyseTest.OuputFileVerification;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;

import java.io.IOException;

public class SegmentationImageIntegrationCheck {
	
	
	/*
	 
	  @param img
	 * @param vMin
	 * @param vMax
	 * @param outputImgString
	 */
/*
 public static void testStupid(ImagePlus img, short vMin, int vMax, String outputImgString ) throws FormatException {
 SegmentationParameters segmentationParameters = new SegmentationParameters();
 SegmentationCalling otsuModif = new SegmentationCalling(img, vMin, vMax, outputImgString);
 otsuModif.runSeveralImages2();
 }
 */
	/**
	 * @param input
	 * @param output
	 */
	public static void testStupidSeveralImages(String input, String output) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
		SegmentationCalling    otsuModif              = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModif.runSeveralImages2();
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void testStupidSeveralImages(String input, String output, String config) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(input, output, config);
		SegmentationCalling    otsuModif              = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModif.runSeveralImages2();
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
		String pathToTest     = "/home/tridubos/Bureau/IMAGES_TEST/SEGMENTATION_IMAGES/SEGMENTATION_VERIF";
		String pathToExpected = "/home/tridubos/Bureau/IMAGES_TEST/SEGMENTATION_IMAGES/SEGMENTATION";
		
		/*
		 testStupidSeveralImages(pathToTest+"/Gros_Nucleols",
		 pathToTest+"/SEGMENTATION_RESULTS/Gros_Nucleols");
		 testStupidSeveralImages(pathToTest+"/Noyaux_Calib_1_1_1",
		 pathToTest+"/SEGMENTATION_RESULTS/Noyaux_Calib_1_1_1",
		 pathToTest+"/Noyaux_Calib_1_1_1/config_calibration.txt");
		 testStupidSeveralImages(pathToTest+"/PB_RADIUS_CONVEXHULL",
		 pathToTest+"/SEGMENTATION_RESULTS/PB_RADIUS_CONVEXHULL");
		 
		 */
		OuputFileVerification fw = new OuputFileVerification(pathToExpected, pathToTest);
		fw.GetFileResultExpeted(pathToExpected);
		//fw.GetFilesOutputFolder(pathToTest);
		fw.GetFilesResultingOfAnalysis(pathToTest);
		fw.CompareAnalysisResult();
            /*
            OuputFileVerification fw = new OuputFileVerification();
            fw.GetFileResultExpeted(ExpectedResult);
            fw.GetFilesOutputFolder(outputTristan);
            fw.GetFilesResultingOfAnalysis(outputTristan);
             fw.CompareAnalysisResult();
             */
		System.err.println("The program ended normally.");
	}
	
	
}
