package segmentationtest;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Locale;


public class SegmentationTestRunner {
	public static final String PATH_TO_SEGMENTATION = "segmentation/";
	public static final String PATH_TO_CONFIG = SegmentationTest.PATH_TO_INPUT
	                                            + PATH_TO_SEGMENTATION
	                                            + "config/seg.config";
	
	
	public static void run(String dir) throws Exception {
		File   file  = new File(dir + PATH_TO_SEGMENTATION);
		File[] files = file.listFiles();
		System.out.println("Running test on directory : " + dir + PATH_TO_SEGMENTATION);
		
		if (files != null) {
			for (File f : files ) {
				String name = f.getName();
				
				if (f.isDirectory()) {
					System.out.println("Directory skipped : " + name);
				}
				else {
					String extension = FilenameUtils.getExtension(name).toLowerCase(Locale.ROOT);
					if (!extension.equals("tif")){
						System.out.println("File of type " + extension + " skipped");
					}
					else {
						System.out.println("Beginning process on : " + name);
						runSegmentation(f.toString(),
						                SegmentationTest.PATH_TO_OUTPUT + PATH_TO_SEGMENTATION + name,
						                PATH_TO_CONFIG);
						System.out.println("Finished process on : "+ name);
						
						System.out.println("Checking results :");
						SegmentationTestChecker checker = new SegmentationTestChecker(name);
						checker.checkValues(f);
					}
				}
			}
		}
	}
	
	private static void runSegmentation(String imageSourceFile, String output, String configFile) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(imageSourceFile, output, configFile);
		SegmentationCalling segmentation = new SegmentationCalling(segmentationParameters);
		segmentation.runOneImage(imageSourceFile);
		segmentation.saveCropGeneralInfo();
	}
	
	
	
}
