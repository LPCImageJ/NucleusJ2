package gred.nucleus.test;

import gred.nucleus.utils.FileList;


import ij.*;
import ij.plugin.*;
import java.io.IOException;
import gred.nucleus.plugins.ConvexHullPlugin_;

import loci.formats.ImageReader;
import loci.formats.FormatException;
import java.io.File;
import java.util.ArrayList;

/**
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class testGiftWrapping {
    /**
     *
     * @param args
     */
    public static void main(String[] args){
        FileList fileList = new FileList ();
        ArrayList<String> File;
        //OLD PATH /media/tridubos/DATA1/MANIP_MANU_KAKU/Trier/RawDataBadSegmentation/

        File[] tFileRawImage = fileList.run("/home/titus/Bureau/metadata_read/");
                //"/home/tridubos/Bureau/ImageTEST/MANIP_KAKU/");
       /*
        for( int i = 0; i < tFileRawImage.length; ++i){
           IJ.log(""+tFileRawImage[i]);
            ConvexHullPlugin_ test = new ConvexHullPlugin_();
            String burp= tFileRawImage[i].toString();
            test.runCommand(burp);
        }
        */
        ImageReader[] readers = new ImageReader[tFileRawImage.length];
        for (int i=0; i<readers.length; i++) {
            IJ.log("lalalalallalalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa"+readers[i]);
            readers[i] = new ImageReader();
            readers[i].getCoreMetadataList();
            IJ.log("mu "+readers[i].getMetadataValue("timestamp"));
        }
        // Bille qui marche pas /home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04.tif
       //  String burp ="/home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04.tif";
        String burp ="/home/plop/Bureau/Col_Cot24-3_005.tif";
        //String burp ="/home/tridubos/Bureau/ImageTEST/test_bille/c1_cot7-1_10.tif";
        //String burp2 ="/home/tridubos/Bureau/ImageTEST/test_nucleol_bof/c1_cot1-2_09.tif";
        //String burp ="/home/tridubos/Bureau/ImageTEST/20180906_billes4/Exp-03.czi-C2.tif";
       // ConvexHullPlugin_ test = new ConvexHullPlugin_();
       // ((ConvexHullPlugin_) test).run(args[0]);

         //test.run(burp);

         //test.run(burp);


    }


}
