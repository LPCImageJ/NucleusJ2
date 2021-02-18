package gred.nucleus.machineLeaningUtils;

import gred.nucleus.filesInputOutput.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import java.util.HashMap;
import java.util.Map;

public class SliceToStack {

    String m_pathToSliceDir;
    String m_pathToOutputDir;

    /**
     * Constructor
     * @param pathToSliceDir path to slice directory to merge to stack
     * @param pathToOutputDir path to stack image output
     */
    public SliceToStack(String pathToSliceDir, String pathToOutputDir) {
         this.m_pathToSliceDir=pathToSliceDir;
         this.m_pathToOutputDir=pathToOutputDir;
    }

    /**
     * Merge slice to stack :
     *  - images shall have this file name format :
     *          CommonNameOfImageToMerge_NumberOfSlice
     */
    public  void run() {
        HashMap<String, Integer> test = new HashMap<>();
        Directory directoryOutput = new Directory(this.m_pathToOutputDir);
        Directory directoryInput = new Directory(this.m_pathToSliceDir);
        directoryInput.listImageFiles(this.m_pathToSliceDir);
        //Parcour de l'ensemble des images du dossier
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            String tm = directoryInput.getFile(i).getName();
            tm = tm.substring(0, tm.lastIndexOf("_"));
            tm = tm.substring(0, tm.lastIndexOf("_"));
            if (test.get(tm) != null) {
                test.put(tm, test.get(tm) + 1);
            } else {
                test.put(tm, 1);
            }
        }

        for (Map.Entry<String, Integer> entry : test.entrySet()) {
            ImagePlus[] image = new ImagePlus[entry.getValue()];
            System.out.println("image :" + entry.getKey());
            for (short i = 0; i < image.length; ++i) {
                //image= BF.openImagePlus((directoryInput.m_dirPath
                image[i] = IJ.openImage((directoryInput.m_dirPath
                        + "/"
                        + entry.getKey()
                        + "_"
                        + i + "_MLprediction.tif"));
                IJ.run(image[i], "8-bit", "");
                //
            }
            ImagePlus imp3 = new Concatenator().concatenate(image, false);
            saveFile(imp3, directoryOutput.m_dirPath+directoryOutput.m_separator
                    + entry.getKey() + ".tif");
        }

    }

    /**
     * Save output file
     * @param imagePlusInput image to save
     * @param pathFile path to save image
     */
    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }
}
