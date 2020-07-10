package gred.nucleus.mains;

import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.CropFromCoordonnate;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.autocrop.AutoCropCalling;

import ij.IJ;
import loci.formats.FormatException;


import java.io.IOException;
import java.util.ArrayList;



/**
 * Class dedicated to examples and test of methods in the package.
 * 
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class TestAutoCrop {
	
	/**
	 * Test for labeling connected components of a binarized image.
	 * Only connected components with no voxel on the image's boundary
	 * are kept in the filtering process.
	 * 
	 * Connected components with a volume below some threshold are
	 * also removed.
	 * 
	 * a constant random gray level is set on each connected component.
	 * 
	 * @param imageSourceFile the input image file on disk 
	 */
	
	static ArrayList <String> m_test;

    public static void runAutoCropFolder(String imageSourceFile, String output, String pathToConfig) throws IOException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }


	public static void runAutoCropFolder(String imageSourceFile, String output) throws IOException , fileInOut,Exception{
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	public static void runAutoCropFile(String imageSourceFile, String output) throws IOException , fileInOut,Exception{
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(imageSourceFile);
	}

	public static void runCropFromCoordinates(String coordonnateDir) throws IOException, FormatException,Exception {

		CropFromCoordonnate test = new CropFromCoordonnate(coordonnateDir);
		test.runCropFromCoordonnate();
	}
	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) throws IOException ,Exception{

	    System.err.println("start prog");
		long maxMemory = Runtime.getRuntime().maxMemory();
		/* Maximum amount of memory the JVM will attempt to use */
		System.out.println("Maximum memory (bytes): " +
				(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9));


		runCropFromCoordinates("C:/Users/Martin/Documents/IMAGE_TEST_NJ/AUTOCROP/RESULTS/RAW_ND/tab_file.txt");

		System.err.println("The program ended normally.");

		System.out.println("Total memory (bytes): " +
				Runtime.getRuntime().totalMemory()*1e-9);
	}



}
