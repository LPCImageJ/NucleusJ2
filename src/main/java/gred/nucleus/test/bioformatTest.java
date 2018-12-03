package gred.nucleus.test;

import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.Prefs;
import ij.io.FileSaver;
import loci.formats.FormatException;
import loci.formats.in.ZeissCZIReader;
import loci.formats.ImageReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import loci.formats.ClassList;
import loci.formats.FormatException;
import loci.formats.IFormatReader;
import loci.formats.ImageReader;
import loci.formats.MetadataTools;
import loci.formats.in.JPEGReader;
import loci.formats.in.ZeissCZIReader;
import loci.formats.in.*;
import loci.formats.FormatHandler;
import loci.formats.meta.IMetadata;



public class bioformatTest {


    public static void main(String[] args)throws FormatException, IOException {
        FileList fileList = new FileList ();
        ArrayList<String> File;
        File[] tFileRawImage = fileList.run("/home/tridubos/Bureau/ImageTEST/test_bille/");
        String burp="/home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04_STACK13-25.tif";
        IJ.log("lala "+tFileRawImage.length);
       /*

        ImagePlus test =new ImagePlus(burp);
        FileSaver fileSaver = new FileSaver(test);
        File file = new File("/home/tridubos/Bureau/ImageTEST/test_bille/aa/");
        fileSaver.saveAsTiffStack("/home/tridubos/Bureau/ImageTEST/test_bille/aa/"+test.getTitle());

          */

        ImageReader[] readers = new ImageReader[tFileRawImage.length];
        for (int i=0; i<readers.length; i++) {
           // Prefs.set(LociPrefs.PREF_CZI_AUTOSTITCH, false);

            try {
                readers[i] = new ImageReader();
              //  readers[i].
               // String temps=tFileRawImage[i];
                readers[i].setId(tFileRawImage[i].toString());
                IJ.log("lalalalallalalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + readers[i]);
                // readers[i].getCoreMetadataList();
                IJ.log("mu " + readers[i].getMetadataValue("Information|Document|CreationDate #1"));
            }
            catch (RuntimeException e){ e.printStackTrace(); }

        }
        IJ.log( "   "+tFileRawImage[0]);

    }
}
