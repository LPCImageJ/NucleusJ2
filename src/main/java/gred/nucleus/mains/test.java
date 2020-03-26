package gred.nucleus.mains;

import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {

    public static void main(String[] args)throws IOException, FormatException, fileInOut,Exception{
        int test= 0-0;
        /*
        ImagePlus[] test = BF.openImagePlus("/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/RAWBUG/test_0_C1.tif");
        test[0].show();
        ImagePlus[] test2 =BF.openImagePlus("/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/RAWBUG/test_1_C1.tif");
        test2[0].show();

        ImagePlus[] test =BF.openImagePlus("/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/RAW/test_0_C1.tif");
      //  File currentFile = .getFile(i);
        test[0].show();
        ImagePlus[] test2 =BF.openImagePlus("/home/titus/Bureau/TEST_NJ/AUTOCROP/DEBUG/RAW/2018051_1527078140.76_Ath_KAKU4-1--CRWN1-2--CRWN4-1_Cot_J13_STD_FIXE_H258_A1_0_C0.tif");
        test2[0].show();
        //currentFile.getName();
        ImageStack imageStackSeg = test[0].getStack();
        double voxelValue = imageStackSeg.getVoxel(46, 59, 13);
        System.out.println("et la ca marche : "+voxelValue);
        Histogram histogram = new Histogram();
        histogram.run(test[0]);
        //IJ.run(test[0], "Histogram", "stack");
        HashMap<Double, Integer> hashMapHisto = histogram.getHistogram();
        for(Map.Entry<Double , Integer> toto : hashMapHisto.entrySet()) {
            System.out.println(" " + toto.getKey()+ " et la "+toto.getValue());

        }
    */
    }
}
