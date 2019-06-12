package gred.nucleus.test;

import gred.nucleus.exceptions.fileInOut;
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
	
	public static void testStupid(String imageSourceFile, String output) throws IOException, FormatException , fileInOut,Exception {
        AutoCropCalling autoCrop = new AutoCropCalling(imageSourceFile,output);
		autoCrop.run();

	}


	/**
	 * Main function of the package's tests.
	 * @param args
	 */
	public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {

		System.err.println("start prog");

		String inputOneImageAxel = "/home/plop/Bureau/image/wideField/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.TIF";
		String inputDirAxel = "/home/plop/Bureau/image/wideField/";
		String outputAxel = "/home/plop/Bureau/image/wideField/test";

		// TODO AJOUTER WARNING QUAND LE THRESHOLD EST TROP BAS (VERIFIER LES NOYAUX )
		//TODO VERIFIER AUTOCROP NE PREND PLUS QUE UN FOLDER EN ENTRER : AJOUTER CHEMIN VERS IMAGE UNIQUE

		//String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/raw/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s9.TIF";
		//String inputDirTristan = "/home/tridubos/Bureau/Demo_Autocrop/Out/";

		// String outputTristan = "/home/titus/Bureau/data/test_autocrop/";


		String ExpectedResult = "/home/tridubos/Bureau/TEST_AUTOCROP/Results_checked";
		String inputOneImageTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/RAW_TEST/raw";



		//String imagetestmulti = "/home/tridubos/Bureau/TEST_AUTOCROP/RAW_TEST/Raw_Multi/Cot3bis2_TEST1.tif";
		String imagetestmulti = "/home/tridubos/Bureau/TEST_AUTOCROP/RAW_TEST/Raw_Multi/Z_Col_cot15&19&23__w11 DAPI SIM_s5.TIF";


		String imagetestmulti2 = "/home/tridubos/Bureau/TEST_AUTOCROP/Test_Version";
		String outputTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/out_test_Version";
		testStupid(imagetestmulti2, outputTristan);


		/**
		ImagePlus[] truc = BF.openImagePlus(imagetestmulti2);
		ChannelSplitter ne = new ChannelSplitter();

		ImagePlus[] hum = ne.split(truc[0]);
		for (int i = 0; i < hum.length; i++) {
			hum[i].show();
			System.out.println("le " + hum[i].getProperties()+" "+hum.length);

		}
		 */
		System.err.println("The program ended normally.");

		System.out.println("Total memory (bytes): " +
				Runtime.getRuntime().totalMemory() * 1e-9);
	}




	/**
	 *
	 * for (int i = 0; i < hum.length; i++) {
	 * 			// Prefs.set(LociPrefs.PREF_CZI_AUTOSTITCH, false);
	 *
	 * 			try {
	 * 				readers[i] = new ImageReader();
	 * 				//  readers[i].
	 * 				// String temps=tFileRawImage[i];
	 * 				readers[i].setId(file.getAbsolutePath());
	 * 				IJ.log("lalalalallalalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + readers[i]);
	 * 				// readers[i].getCoreMetadataList();
	 * 				IJ.log("mu " + readers[i].getMetadataValue("Information|Document|CreationDate #1"));
	 *
	 * 				Set set = readers[i].getGlobalMetadata().entrySet();
	 *
	 * 				Iterator iterator =set.iterator();
	 * 				while(iterator.hasNext()) {
	 * 					Map.Entry mentry = (Map.Entry)iterator.next();
	 * 					System.out.print("key is: "+ mentry.getKey() + " & Value is: ");
	 * 					System.out.println(mentry.getValue());
	 *                                }* 			} catch (RuntimeException e) {
	 * 				e.printStackTrace();
	 * 			}
	 */

}
