package gred.nucleus.test;

import gred.nucleus.mainsNucelusJ.AutoCropCalling;
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
	
	public static void testStupid(String imageSourceFile, String output) throws IOException, FormatException {
        AutoCropCalling autoCrop = new AutoCropCalling(imageSourceFile,output);
        autoCrop.run();
	}


	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) throws IOException, FormatException {

	    System.err.println("start prog");
		String inputOneImageAxel = "/home/plop/Bureau/image/wideField/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.TIF";
        String inputDirAxel = "/home/plop/Bureau/image/wideField/";
        String outputAxel = "/home/plop/Bureau/image/wideField/test";

        String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/test_outpout/Z_Col_cot15&19&23__w11_DAPI_SIM_s5.tif";
        //String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/raw/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s9.TIF";
        String inputDirTristan = "/home/tridubos/Bureau/Bille_4Micro_02-2019/AutocropDuSchnaps/";
        String outputTristan = "/home/tridubos/Bureau/Bille_4Micro_02-2019/OutputDuSchnaps/";
		System.out.println("la le Arg1 "+args[0]);
        testStupid(args[0],args[1] );

		System.err.println("The program ended normally.");
	}

}
