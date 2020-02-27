package gred.nucleus.mains;

import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.HashMap;
import java.util.Map;

public class test {

    public static void main(String[] args)throws IOException, FormatException, fileInOut,Exception{

        ImagePlus[] test =BF.openImagePlus("/media/tridubos/DATA1/DATA_ANALYSE/WARIO_SEG_TRAINING/ML_EXTRAIT/Col_Cot13-2_001.tif");

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
    }
}
