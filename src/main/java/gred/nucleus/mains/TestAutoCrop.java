package gred.nucleus.mains;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.CropFromCoordinates;
import gred.nucleus.core.NucleusSegmentation;
import ij.IJ;
import ij.ImagePlus;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;


/**
 * Class dedicated to examples and test of methods in the package.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class TestAutoCrop {
	
	/**
	 * Test for labeling connected components of a binarized image. Only connected components with no voxel on the
	 * image's boundary are kept in the filtering process.
	 * <p>
	 * Connected components with a volume below some threshold are also removed.
	 * <p>
	 * a constant random gray level is set on each connected component.
	 *
	 * @param imageSourceFile the input image file on disk
	 */
	static ArrayList<String> test;
	
	
	public static void runAutoCropFolder(String imageSourceFile, String output, String pathToConfig) {
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output, pathToConfig);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	public static void runAutoCropFolder(String imageSourceFile, String output) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	public static void runAutoCropFile(String imageSourceFile, String output) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters = new AutocropParameters(imageSourceFile, output);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(imageSourceFile);
	}
	
	
	public static void runCropFromCoordinates(String coordinateDir) throws Exception {
		
		CropFromCoordinates test = new CropFromCoordinates(coordinateDir);
		test.runCropFromCoordinate();
	}
	
	
	/**
	 * Main function of the package's tests.
	 *
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		File currentFile      = new File("/media/titus/DATA/TEST_NJ2/TEST_GIFT/RAW/KAKU4-2--CRWN1-2--CRWN4-1_Cot_J13_STD_FIXE_H258_A1_0_C0.tif");
		URLClassLoader child = new URLClassLoader(new URL[] {new URL("file:/home/titus/Lab/3D_Convex_Hull.jar")}, Main.class.getClassLoader());
		ImagePlus img = new ImagePlus("/media/titus/DATA/TEST_NJ2/TEST_GIFT/RAW/KAKU4-2--CRWN1-2--CRWN4-1_Cot_J13_STD_FIXE_H258_A1_0_C0.tif");
		IJ.run(img, "Make Convex Hull Vertices Stack...", "");
		img.show();
		/*
		NucleusSegmentation nucleusSegmentation =
				new NucleusSegmentation(currentFile, this.prefix, this.segmentationParameters);
		nucleusSegmentation.preProcessImage();
		nucleusSegmentation.findOTSUMaximisingSphericity();
		nucleusSegmentation.checkBadCrop(this.segmentationParameters.inputFolder);
		nucleusSegmentation.saveOTSUSegmented();
		infoOtsu.append(nucleusSegmentation.getImageCropInfoOTSU());
		nucleusSegmentation.saveGiftWrappingSeg();
		infoGift.append(nucleusSegmentation.getImageCropInfoGIFT());
		*/
	}
	
}
