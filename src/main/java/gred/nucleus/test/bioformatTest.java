package gred.nucleus.test;

import gred.nucleus.utils.FileList;
import ij.IJ;
import loci.formats.FormatException;
import loci.formats.ImageReader;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class bioformatTest {
    public static void main(String[] args)throws FormatException, IOException {
        FileList fileList = new FileList ();
        ArrayList<String> File;
        File[] tFileRawImage = fileList.run("/home/tridubos/Bureau/ImageTEST/test_bille/");
        String burp="/home/tridubos/Bureau/ImageTEST/test_bille/20180919_psf_63x_b_z 0_2-04.tif";
        IJ.log("lala "+tFileRawImage.length);
        ImageReader[] readers = new ImageReader[tFileRawImage.length];
        for (int i=0; i<readers.length; i++) {

            readers[i] = new ImageReader();

            readers[i].setId(burp);
            IJ.log("lalalalallalalaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" + readers[i]);
            // readers[i].getCoreMetadataList();
            IJ.log("mu " + readers[i].getMetadataValue("Information|Document|CreationDate #1"));

        }

    }
}
