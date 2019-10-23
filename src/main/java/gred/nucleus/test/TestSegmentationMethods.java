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



        ///home/titus/Bureau/data/Test_Image_Reproductibilite/IMAGE_TEST_NJ/AUTOCROP_RAW/RAW_BIOFORMATS


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
        String pathToTest ="/home/tridubos/Bureau/IMAGES_TEST/AUTOCROP";
        long maxMemory = Runtime.getRuntime().maxMemory();
        System.out.println("Maximum memory (bytes) /RAW_CZI : " +
                (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9)+" "+Runtime.getRuntime().freeMemory()*1e-9);
        testStupidSeveralImages(pathToTest+"/RAW_CZI",
                pathToTest+"/AUTOCROP_RESULTS/RAW_CZI");
       // testStupidSeveralImages("/home/titus/Bureau/data/Noyaux/Raw", "/home/titus/Bureau/data/Noyaux/output");
        // testStupidSeveralImages(ExpectedResult, ExpectedResult, (short)6.0, 300000000,true);
        /*fw.GetFilesResultingOfAnalysis(inputTristan);
        fw.CompareAnalysisResult();
        OuputFileVerification fw = new OuputFileVerification();
        fw.GetFileResultExpeted(ExpectedResult);
        fw.GetFilesOutputFolder(outputTristan);
        fw.GetFilesResultingOfAnalysis(outputTristan);
        fw.CompareAnalysisResult();
        */
        System.err.println("The program ended normally.");
    }


}
