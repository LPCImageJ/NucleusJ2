package gred.nucleus.test;

import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.mainsNucelusJ.OtsuModifSeg;
import ij.IJ;
import ij.ImagePlus;

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


    /**
     * Main function of the package's tests.
     * @param args
     */
    public static void main(String[] args) {
        //testComponentsLabeling(wrapImaJ.test.TestCoreMethods.testImages_8bits[1]);
        String imgPathAxel = "/home/plop/Bureau/image/Col_Cot24-3_005.tif";
        String imgSegPathAxel = "/home/plop/Bureau/image/";
        String imgPathTristan = "";
        String imgSegPathTristan = "";
        ImagePlus img  = IJ.openImage(imgPathAxel);

        testStupid(img,(short)6.0, (short)40.0,imgSegPathAxel);
        // keep components  which contain AT LEAST ONE voxel inside a thick plane
        //testComponentsPredicateFiltering(wrapImaJ.test.TestCoreMethods.testImages_8bits[0],
        //		true, false);
        //for(int i = 0; i <m_test.size();++i){
        //System.out.println(m_test.get(i)+" "+m_test.size());
        //}
        // keep components  which are INCLUDED inside a thick plane
        //testComponentsPredicateFiltering(wrapImaJ.test.TestCoreMethods.testImages_8bits[0],
        //		false, true);

        System.err.println("The program ended normally.");
    }
}
