package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.exceptions.fileInOut;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import loci.formats.FormatException;

import java.awt.*;
import java.io.IOException;
import java.lang.reflect.Array;

public class ImagesToStack {
    public static void main(String[] args) {
        ImagePlus[] image =new ImagePlus[5];
        Directory directoryInput=new Directory("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test_sliceToStack");
        directoryInput.listFiles("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test_sliceToStack");
        System.out.println(directoryInput.getFile(0).getAbsolutePath());
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            image[i]=IJ.openImage((directoryInput.getFile(i).getAbsolutePath()));
        }
        image[0].show();
        //ImagePlus imp3 = Concatenator.concatenate(image,0);
        ImagePlus imp3 = new Concatenator().concatenate(image, false);

        ImagePlus image1 = IJ.openImage("" +
                "/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test/Col_Cot13-2_001_0_MLprediction.tif");
        ImagePlus image2 = IJ.openImage("" +
                "/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test/Col_Cot13-2_001_0_MLprediction.tif");

        saveFile(imp3,"/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/euuu.tiff");
    }

    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }
}
