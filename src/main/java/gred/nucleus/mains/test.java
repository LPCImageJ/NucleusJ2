package gred.nucleus.mains;

import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
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

    public static void main(String[] args)throws IOException, FormatException, fileInOut,Exception {
    //    segmentationFolder("/home/titus/Bureau/TEST_NJ/TEST_ANALYSE/raw", "/home/titus/Bureau/TEST_NJ/TEST_ANALYSE/SEG_OTSU_GIFT");
        int toto[]={};
        toto[10]=1;
        System.out.println(toto.length);
        String sequence ="toto";
        String [] tableau_sequence =sequence.split("");
    }


    public static void segmentationFolder(String input, String output ) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

}
