package gred.nucleus.mains;

import gred.nucleus.exceptions.fileInOut;
import ij.ImagePlus;
import ij.ImageStack;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.IOException;

public class test {

    public static void main(String[] args)throws IOException, FormatException, fileInOut,Exception{

        ImagePlus[] test =BF.openImagePlus("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/OUTPUT_ML_MERGED/Col_Cot16-1_016.tif");

        ImageStack imageStackSeg = test[0].getStack();
        double voxelValue = imageStackSeg.getVoxel(1, 1, 1);
        System.out.println("et la ca marche : "+voxelValue);


    }
}
