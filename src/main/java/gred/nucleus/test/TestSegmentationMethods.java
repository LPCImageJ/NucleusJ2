package gred.nucleus.test;

import gred.nucleus.mainsNucelusJ.SegmentationMethods;
import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import ij.process.ImageProcessor;
import loci.formats.FormatException;

import java.io.IOException;
import java.util.HashMap;

import static ij.IJ.open;

import ij.plugin.filter.GaussianBlur;


public class TestSegmentationMethods {
    /**
     *
     * @param img
     * @param vMin
     * @param vMax
     * @param outputImgString
     */

    public static void testStupid(ImagePlus img, short vMin, int vMax, String outputImgString, boolean gift ) {
        SegmentationMethods otsuModif = new SegmentationMethods(img, vMin, vMax, outputImgString);
        otsuModif.runOneImage(gift);
    }

    /**
     *
     * @param input
     * @param output
     * @param vMin
     * @param vMax

     */
    public static void testStupidSeveralImages(String input, String output, short vMin, int vMax, boolean gift ) throws FormatException {
        SegmentationMethods otsuModif = new SegmentationMethods(input, output, vMin, vMax);
        try {
            String log = otsuModif.runSeveralImages(gift);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    /**
     *
     * Main function of the package's tests.
     * @param args
     */
    public static void main(String[] args) throws FormatException {
        //testComponentsLabeling(wrapImaJ.test.TestCoreMethods.testImages_8bits[1]);
        String imgPathAxel = "/home/plop/Bureau/image/wideField/test/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4_46_1655_34_8.tif";
        String imgSegPathAxel = "/home/plop/Bureau/image/";
        String inputAxel = "/home/plop/Bureau/image/wideField/test/";
        String outputAxel = "/home/plop/Bureau/image/wideField/testSeg/";
        String outputAxelGift = "/home/plop/Bureau/image/wideField/testSegGift/";

        String imgPathTristan = "/home/titus/Bureau/AA_DEBUG/Raw/";
        String imgSegPathTristan = "";
        //String inputTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/test_crop";


        String inputTristan = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/raw/";
        String outputTristanGift = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/out/GIFT/";
        String outputTristanOtsu = "/home/tridubos/Bureau/VERIFICATION_SEGMENTATION_MANUEL_NUCLEOLE/Verification/out/OTSU/";

/*

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
        testStupidSeveralImages(inputTristan, outputTristanOtsu , (short)1.0, 300000000,false);

        testStupidSeveralImages(inputTristan, outputTristanGift, (short)6.0, 300000000,true);
        /*
        ImagePlus img  = IJ.openImage(imgPathTristan);
        Histogram histogram = new Histogram ();
        histogram.run(img);
        HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
        IJ.log("nombre de pixel"+hashMapHisto.get(0));
        */
        System.err.println("The program ended normally.");
    }
}
