package gred.nucleus.test;

import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.mainsNucelusJ.OtsuModifSeg;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.IOException;
import java.util.ArrayList;

import static ij.IJ.open;

public class TestOtsuModifie {
    /**
     *
     * @param img
     * @param vMin
     * @param vMax
     * @param outputImgString
     */

    public static void testStupid(ImagePlus img, short vMin, short vMax, String outputImgString ) {
        OtsuModifSeg otsuModif = new OtsuModifSeg(img, vMin, vMax, outputImgString);
        otsuModif.runOneImage();
    }

    public static void testStupidSeveralImages(String input, String output, short vMin, short vMax, Calibration cal ) {
        OtsuModifSeg otsuModif = new OtsuModifSeg(input, output, vMin, vMax, cal);
        try {
            String log = otsuModif.runSeveralImages();
            System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}

    }

    /**
     * Main function of the package's tests.
     * @param args
     */
    public static void main(String[] args) {
        //testComponentsLabeling(wrapImaJ.test.TestCoreMethods.testImages_8bits[1]);
        String imgPathAxel = "/home/plop/Bureau/image/Col_Cot24-3_005.tif";
        String imgSegPathAxel = "/home/plop/Bureau/image/";
        String inputAxel = "/home/plop/Bureau/image/test/";
        String outputAxel = "/home/plop/Bureau/image/test_res/";

        String imgPathTristan = "";
        String imgSegPathTristan = "";
        String inputTristan = "/home/plop/Bureau/image/";
        String outputTristan = "/home/plop/Bureau/image/";
        ImagePlus img  = IJ.openImage(imgPathAxel);

        //testStupid(img,(short)6.0, (short)40.0,imgSegPathAxel);

        Calibration cal = new Calibration();
        cal.pixelDepth = 0.2;
        cal.pixelHeight = 0.103;
        cal.pixelWidth = 0.103;
        testStupidSeveralImages(inputAxel, outputAxel, (short)6.0, (short)40.0,cal);

        System.err.println("The program ended normally.");
    }
}
