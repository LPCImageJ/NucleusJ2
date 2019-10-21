package gred.nucleus.test;

import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;

import java.io.IOException;

public class SegmentationImagesIntegration {

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



            testStupidSeveralImages("/home/titus/Bureau/data/Test_Image_Reproductibilite/IMAGE_TEST_NJ/AUTOCROP_RAW/RAW_BIOFORMATS",
            "/home/titus/Bureau/data/Test_Image_Reproductibilite/IMAGE_TEST_NJ/AUTOCROP_RESULTS",
            "/home/titus/Bureau/data/Test_Image_Reproductibilite/IMAGE_TEST_NJ/SEGMENTATION_RESULTS/config_calibration");
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
