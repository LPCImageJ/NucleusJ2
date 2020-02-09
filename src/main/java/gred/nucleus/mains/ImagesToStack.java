package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import java.util.HashMap;
import java.util.Map;

public class ImagesToStack {
    public static void main(String[] args) {
        HashMap<String,Integer > test=new HashMap();
        Directory directoryInput=new Directory("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/OUTPUT_ML");
        directoryInput.listFiles("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/OUTPUT_ML");
        //Parcour de l'ensemble des images du dossier
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            String tm = directoryInput.getFile(i).getName();
            tm=tm.substring(0, tm.lastIndexOf("_"));
            tm=tm.substring(0, tm.lastIndexOf("_"));
            if(test.get(tm)!=null){
                test.put(tm,test.get(tm)+1);
            }
            else{
                test.put(tm,1);
            }
        }

        for(Map.Entry<String , Integer> entry : test.entrySet()) {
            ImagePlus[] image =new ImagePlus[entry.getValue()];

            for (short i = 0; i < image.length; ++i) {

                image[i]=IJ.openImage(("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/OUTPUT_ML"
                        +"/"
                        +entry.getKey()
                        +"_"
                        +i+"_MLprediction.tif"));
                IJ.run(image[i], "8-bit", "");
                //
            }
            ImagePlus imp3 = new Concatenator().concatenate(image, false);
            saveFile(imp3,"/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/OUTPUT_ML_MERGED/"
                    +entry.getKey()+".tif");



        }
        /**
            ImagePlus[] image =new ImagePlus[99];
        Directory directoryInput2=new Directory("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test_sliceToStack");
        directoryInput.listFiles("/media/tridubos/DATA1/DATA_ANALYSE/OTSUm_VS_GIFT-W/test_sliceToStack");
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
          //
            //System.out.println(directoryInput.getFile(i).getAbsolutePath());

        }
        //image[0].show();

        */
    }

    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }
}
