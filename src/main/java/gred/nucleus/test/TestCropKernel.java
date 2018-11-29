package gred.nucleus.test;

import java.util.ArrayList;

import gred.nucleus.autocrop.AutoCrop;
import ij.IJ;
import ij.ImagePlus;


/**
 * Class dedicated to examples and test of methods in the package.
 * 
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class TestCropKernel {
	
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
	
	public static void testStupid(String imageSourceFile, String output, String prefix) {
		ImagePlus plop =  IJ.openImage(imageSourceFile);
		AutoCrop autoCrop = new AutoCrop (plop,prefix,output);
		System.out.println(autoCrop.getFileCoordinates());
		autoCrop.thresholdKernels();
		System.out.println("fin otsu");
		autoCrop.cropKernels(autoCrop.computeBoxes(8));
		System.out.println("fin crop");
		autoCrop.getOutputFileArrayList();
		System.out.println("fin array file");
	}


	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) {
		//testComponentsLabeling(wrapImaJ.test.TestCoreMethods.testImages_8bits[1]);
		testStupid("/home/plop/Bureau/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.tif", "/home/plop/Bureau/testStupid","plop");
		// keep components  which contain AT LEAST ONE voxel inside a thick plane
		//testComponentsPredicateFiltering(wrapImaJ.test.TestCoreMethods.testImages_8bits[0],
		//		true, false);
		//for(int i = 0; i <m_test.size();++i){
			//System.out.println(m_test.get(i)+" "+m_test.size());
		//}
		// keep components  which are INCLUDED inside a thick plane		
		//testComponentsPredicateFiltering(wrapImaJ.test.TestCoreMethods.testImages_8bits[0],
		//		false, true);
		
		System.err.println("The program ended normally.");
	}

}
