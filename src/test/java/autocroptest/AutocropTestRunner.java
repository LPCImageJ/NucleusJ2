package autocroptest;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.util.Locale;


public class AutocropTestRunner {
	public static final String PATH_TO_AUTOCROP = "autocrop/";
	
	public static void run(String dir) {
		File   file  = new File(dir + PATH_TO_AUTOCROP);
		File[] files = file.listFiles();
		System.out.println("Running test on directory : " + dir + PATH_TO_AUTOCROP);
		
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
						runAutoCrop(f.toString(), AutoCropTest.PATH_TO_OUTPUT
						                          + PATH_TO_AUTOCROP
						                          + name);
						System.out.println("Finished process on : "+ name);
						System.out.println("Checking results :");
						AutocropTestChecker checker = new AutocropTestChecker(name);
						checker.checkValues(f);
					}
				}
			}
		}
	}
	
	private static void runAutoCrop(String imageSourceFile, String output) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(imageSourceFile);
		autoCrop.saveGeneralInfo();
	}
}
