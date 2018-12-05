package gred.nucleus.test;

import java.util.ArrayList;

import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.mainsNucelusJ.AutoCropCalling;
import ij.IJ;
import ij.ImagePlus;


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
	
	public static void testStupid(String imageSourceFile, String output) {
        AutoCropCalling autoCrop = new AutoCropCalling(imageSourceFile,output);
        autoCrop.run();
	}


	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {

	    System.err.println("start prog");
		String inputOneImageAxel = "/home/plop/Bureau/image/wideField/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.tif";
        String inputDirAxel = "/home/plop/Bureau/image/wideField/";
        String outputAxel = "/home/plop/Bureau/image/wideField/test";

        String inputOneImageTristan = "";
        String inputDirTristan = "";
        String outputTristan = "";

        testStupid(inputDirAxel, outputAxel);

		
		System.err.println("The program ended normally.");
	}

}
