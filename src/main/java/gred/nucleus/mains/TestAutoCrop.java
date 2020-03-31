package gred.nucleus.mains;

import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.autocrop.AutoCropCalling;

import ij.IJ;



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
		String inputOneImageAxel = "/home/plop/Bureau/image/wideField/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.TIF";
        String inputDirAxel = "/home/plop/Bureau/image/wideField/";
        String outputAxel = "/home/plop/Bureau/image/wideField/test";


        //String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/raw/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s9.TIF";
        //String inputDirTristan = "/home/tridubos/Bureau/Demo_Autocrop/Out/";

       // String outputTristan = "/home/titus/Bureau/data/test_autocrop/";

        String ExpectedResult = "/home/tridubos/Bureau/TEST_AUTOCROP/Results_checked";
		String inputOneImageTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/Test_Version";

		String outputTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/out_test_Version";

		//OuputFileVerification fw = new OuputFileVerification();
		//fw.GetFileResultExpeted(ExpectedResult);
		//fw.GetFilesOutputFolder(outputTristan);
		//testStupid(inputOneImageTristan, outputTristan);

		//runAutoCrop("/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Raw",
		//		"/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Crop");



		//

		//runAutoCrop("/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/test_projetction/Raw",
		//       "/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/test_projetction/Autocrop",
		//       "/media/tridubos/DATA1/DATA_ANALYSE/SPERMATO/Manipe_3_30_images/test_projetction/config_file_test");
		//runAutoCrop("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Raw/TwoChannels",
		//		"/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Autocrop_2/TwoChannels",
		//		"/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/Raw/config_file_TwoChannel");
/**
        runAutoCrop(
				"/home/tridubos/Bureau/IMAGES_TEST/Segmentation/Raw",
				"/home/tridubos/Bureau/IMAGES_TEST/Segmentation/auto_SUPPR");
*/

		runAutoCropFolder("/home/titus/Bureau/TEST_NJ/AUTOCROP/autocrop/raw",
		"/home/titus/Bureau/TEST_NJ/AUTOCROP/autocrop/AUTOCROP_30_30",
				"/home/titus/Bureau/TEST_NJ/AUTOCROP/autocrop/config.txt");

		System.err.println("The program ended normally.");

		System.out.println("Total memory (bytes): " +
				Runtime.getRuntime().totalMemory()*1e-9);
	}



}
