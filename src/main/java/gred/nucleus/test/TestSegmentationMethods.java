package gred.nucleus.test;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;

import java.io.IOException;


public class TestSegmentationMethods {
    /**
     *
     * @param img
     * @param vMin
     * @param vMax
     * @param outputImgString
     */
/**
    public static void testStupid(ImagePlus img, short vMin, int vMax, String outputImgString ) throws FormatException {
        SegmentationParameters segmentationParameters = new SegmentationParameters();
        SegmentationCalling otsuModif = new SegmentationCalling(img, vMin, vMax, outputImgString);
        otsuModif.runSeveralImages2();
    }
*/
    /**
     *
     * @param input
     * @param output


     */
    public static void testStupidSeveralImages(String input, String output ) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }
    public static void testStupidSeveralImages(String input, String output ,String config) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output,config);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }
    /**
     *
     * Main function of the package's tests.
     * @param args
     */
    public static void main(String[] args) throws Exception {
        //testComponentsLabeling(wrapImaJ.test.TestCoreMethods.testImages_8bits[1]);
        String imgPathAxel = "/home/plop/Bureau/image/wideField/test/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4_46_1655_34_8.tif";
        String imgSegPathAxel = "/home/plop/Bureau/image/";
        String inputAxel = "/home/plop/Bureau/image/wideField/test/";
        String outputAxel = "/home/plop/Bureau/image/wideField/testSeg/";
        String outputAxelGift = "/home/plop/Bureau/image/wideField/testSegGift/";

        String imgPathTristan = "/home/titus/Bureau/AA_DEBUG/Raw/";
        String imgSegPathTristan = "";
        //String inputTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/test_crop";

/*
        String inputTristan = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/raw/";
        String outputTristanGift = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/out/GIFT/";
        String outputTristanOtsu = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/out/OTSU/";



        String inputTristan = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/RawDataNucleus/";
        String outputTristanGift = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Segmentation/GIFT/";
        String outputTristanOtsu = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Segmentation/OTSU/";


        String inputTristan = "/home/tridubos/Bureau/Noyaux_test_sphericité/Raw";
        String outputTristanGift = "/home/tridubos/Bureau/Noyaux_test_sphericité/GIFT/";
        String outputTristanOtsu = "/home/tridubos/Bureau/Noyaux_test_sphericité/OTSU/";
                /home/tridubos/Bureau/bille_AXEL/Raw
        String inputTristan = "/home/titus/Bureau/data/Noyaux/Raw";
        String outputTristanGift = "/home/titus/Bureau/data/Noyaux/GIFT/";
        String outputTristanOtsu = "/home/titus/Bureau/data/Noyaux/OTSU/";

        */

        //ImagePlus img  = IJ.openImage(imgPathAxel);
        //System.out.println(img.getTitle()+" Start test");

        //testStupid(img,(short)6.0, (short)400.0,imgSegPathAxel,false);
        /*
        Calibration cal = new Calibration();
        cal.pixelDepth = 0.2;
        cal.pixelHeight = 0.103;
        cal.pixelWidth = 0.103;
 */



        String ExpectedResult = "/home/tridubos/Bureau/TEST_SEG/Results_checked/Z_Col_cot21&17&22__w11 DAPI SIM_s3/";
        String ExpectedResultOutOTSU = "/home/tridubos/Bureau/TEST_SEG/Results_checked/Z_Col_cot21&17&22__w11 DAPI SIM_s3/OTSU/";

        String inputTristan= "/media/tridubos/DATA1/SPERMATO/Manipe_1_57_images_input/Segmentation/Analyse_Segmentation/NucleusPB";
        String outputTristanGift = "/home/tridubos/Bureau/TEST_SEG/Test_analysis/Z_Col_cot21&17&22__w11 DAPI SIM_s3/GIFT/";
        String outputTristanOtsu = "/media/tridubos/DATA1/SPERMATO/Manipe_1_57_images_input/Segmentation/Analyse_Segmentation/Segmented";
        /*
        OuputFileVerification fw = new OuputFileVerification(ExpectedResult,inputTristan);
        fw.GetFileResultExpeted(ExpectedResult);
        fw.GetFilesOutputFolder(inputTristan);
        */
        testStupidSeveralImages("/home/tridubos/Bureau/TEST_SEGMENTATION/RawData", "/home/tridubos/Bureau/TEST_SEGMENTATION/NewWay" );

       // testStupidSeveralImages(ExpectedResult, ExpectedResult, (short)6.0, 300000000,true);
 /*
        fw.GetFilesResultingOfAnalysis(inputTristan);
        fw.CompareAnalysisResult();


        OuputFileVerification fw = new OuputFileVerification();
        fw.GetFileResultExpeted(ExpectedResult);
        fw.GetFilesOutputFolder(outputTristan);
        fw.GetFilesResultingOfAnalysis(outputTristan);
        fw.CompareAnalysisResult();





        ImagePlus img  = IJ.openImage(imgPathTristan);
        Histogram histogram = new Histogram ();
        histogram.run(img);
        HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
        IJ.log("nombre de pixel"+hashMapHisto.get(0));
        */
        System.err.println("The program ended normally.");
    }
}
