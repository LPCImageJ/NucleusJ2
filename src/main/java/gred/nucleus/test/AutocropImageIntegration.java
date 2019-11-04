package gred.nucleus.test;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.exceptions.fileInOut;
import loci.formats.FormatException;

import java.io.IOException;
import java.util.ArrayList;

public class AutocropImageIntegration {



    static ArrayList<String> m_test;

    public static void runAutoCrop(String imageSourceFile, String output, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.run();
    }

    public static void testStupid(String imageSourceFile, String output) throws IOException, FormatException ,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling();
        autoCrop.run();
    }

    public static void runAutoCrop(String imageSourceFile, String output) throws IOException, FormatException , fileInOut,Exception{
        //AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,40,40,20,0,20,0,1,1000000000);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.run();
    }

    /**
     * Main function of the package's tests.
     * @param args
     */
    public static void main(String[] args) throws IOException, FormatException ,Exception{
        String pathToTest ="/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP_IMAGES/AUTOCROP_RAW";
        String pathToOutput ="/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP";




        runAutoCrop(pathToTest+"/RAW_BIOFORMATS",
                pathToTest+"/AUTOCROP_RESULTS/RAW_BIOFORMATS",
                pathToTest+"/RAW_BIOFORMATS/config_calibration.txt");


         runAutoCrop(pathToTest+"/RAW_CZI",
                pathToTest+"/AUTOCROP_RESULTS/RAW_CZI");
        runAutoCrop(pathToTest+"/RAW_ND",
                pathToTest+"/AUTOCROP_RESULTS/RAW_ND",
                pathToTest+"/RAW_ND/config_calibration.txt");
        runAutoCrop(pathToTest+"/RAW_STK",
                pathToTest+"/AUTOCROP_RESULTS/RAW_STK");
        runAutoCrop(pathToTest+"/RAW_TIF_2D",
                pathToTest+"/AUTOCROP_RESULTS/RAW_TIF_2D");
        runAutoCrop(pathToTest+"/RAW_TIF_3D",
                pathToTest+"/AUTOCROP_RESULTS/RAW_TIF_3D");



     //   String ExpectedResult = "/home/tridubos/Bureau/TEST_AUTOCROP/Results_checked";
     //   String inputOneImageTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/Test_Version";

        //String outputTristan = "/home/tridubos/Bureau/TEST_AUTOCROP/out_test_Version";

        //OuputFileVerification fw = new OuputFileVerification();
        //fw.GetFileResultExpeted(ExpectedResult);
        //fw.GetFilesOutputFolder(outputTristan);
        //testStupid(inputOneImageTristan, outputTristan);

       // runAutoCrop("/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Raw",
           //     "/home/tridubos/Bureau/IMAGES_TEST/Nouveau dossier/Autocrop_name/Crop");


        //runAutoCrop("/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/RawData",
        //       "/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/Autocrop",
        //     "/media/tridubos/DATA1/SPERMATO/Manipe_3_30_images/config_file_test");


//fw.GetFilesResultingOfAnalysis(outputTristan);
        //fw.CompareAnalysisResult();
		/*
		String inputOneImageTristan = "/home/tridubos/Bureau/TEST_READING_METADATA/";
		ImporterOptions options = new ImporterOptions();
		options.setId(inputOneImageTristan);
		options.setAutoscale(true);
		options.setCrop(true);
		options.setCropRegion(0, new Region(150, 150 ,50, 50));
		options.setColorMode(ImporterOptions.COLOR_MODE_COMPOSITE);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus sort = new ImagePlus();
		sort = new Duplicator().run(imps[0],1,10);

		saveFile(sort, "/home/tridubos/Bureau/TEST_READING_METADATA/cetruc.tif");
		*/
        //testStupid(inputOneImageTristan, outputTristan);
        System.err.println("The program ended normally.");

        System.out.println("Total memory (bytes): " +
                Runtime.getRuntime().totalMemory()*1e-9);
    }


}
