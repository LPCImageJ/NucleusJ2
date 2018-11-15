package gred.nucleus;
<<<<<<< HEAD
import gred.nucleus.utils.FileList;
=======
>>>>>>> a5bf335e2efa27e6f744f38d23733a34679dac83
import ij.*;
import ij.plugin.*;

import gred.nucleus.plugins.ConvexHullPlugin_;

<<<<<<< HEAD
import java.io.File;
import java.util.ArrayList;

public class main {
    public static void main(String[] args){
        FileList fileList = new FileList ();
        ArrayList<String> File;
        File[] tFileRawImage = fileList.run("/media/tridubos/DATA1/MANIP_MANU_KAKU/Trier/RawDataBadSegmentation/c1_cot4-1_28.tif");
        for( int i = 0; i < tFileRawImage.length; ++i){
           IJ.log(""+tFileRawImage[i]);
            ConvexHullPlugin_ test = new ConvexHullPlugin_();
            String burp= tFileRawImage[i].toString();
            test.runCommand(burp);
        }



=======
public class main {
    public static void main(String[] args){
>>>>>>> a5bf335e2efa27e6f744f38d23733a34679dac83
        // Bille qui marche pas /home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04.tif
         String burp ="/home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04.tif";
        //String burp ="/home/tridubos/Bureau/ImageTEST/test_bille/c1_cot7-1_10.tif";
        String burp2 ="/home/tridubos/Bureau/ImageTEST/test_nucleol_bof/c1_cot1-2_09.tif";
        //String burp ="/home/tridubos/Bureau/ImageTEST/20180906_billes4/Exp-03.czi-C2.tif";
        ConvexHullPlugin_ test = new ConvexHullPlugin_();
       // ((ConvexHullPlugin_) test).run(args[0]);
<<<<<<< HEAD
        // test.run(burp);
=======
         test.run(burp);
>>>>>>> a5bf335e2efa27e6f744f38d23733a34679dac83

    }


}
