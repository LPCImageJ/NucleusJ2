package gred.nucleus.mainsNucelusJ;

import gred.nucleus.autocrop.AutoCrop;
import ij.IJ;
import ij.ImagePlus;

import java.io.File;

/**
 *
 */
public class AutoCropCalling {

    private String _input = "";
    private String _output = "";
    private String _prefix = "";
    /**
     *
     * @param imageSourceFile
     * @param output
     */
    public AutoCropCalling(String imageSourceFile, String output) {
        this._input = imageSourceFile;
        this._output = output;
        File outputFile = new File(this._output);
        if(!(outputFile .exists()))
            outputFile.mkdir();
    }


    /**
     *
     */
    public void run(){
        File inputFile = new File(_input);
        if(inputFile.isFile()){
           String[] tNameFile =  this._input.split(File.separator);
           this._prefix = tNameFile[tNameFile.length-1].replaceAll(".tif","");
            this._prefix = tNameFile[tNameFile.length-1].replaceAll(".TIF","");
           ImagePlus img = IJ.openImage(this._input);
           autocropMethod(img);
        }
        else{
            File folder = new File(_input);
            File[] listOfFiles = folder.listFiles();
            for(int i = 0; i < listOfFiles.length; ++i) {
                String fileImg = listOfFiles[i].toString();
                if (fileImg.contains(".tif") || fileImg.contains(".TIF")) {
                    String[] tNameFile =  fileImg.split(File.separator);
                    this._prefix = tNameFile[tNameFile.length-1].replaceAll(".tif","");
                    this._prefix = tNameFile[tNameFile.length-1].replaceAll(".TIF","");
                    ImagePlus img = IJ.openImage(fileImg);
                    autocropMethod(img);
                }
            }
        }
    }


    /**
     *
     * @param img
     */
    private void autocropMethod(ImagePlus img){
        AutoCrop autoCrop = new AutoCrop (img,this._prefix,this._output);
        autoCrop.thresholdKernels();
        autoCrop.cropKernels(autoCrop.computeBoxes(8));
        autoCrop.getOutputFileArrayList();
        System.out.println(_prefix+"\t"+autoCrop.getNbOfNuc()+" nuclei detected");

    }
}
