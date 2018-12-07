package gred.nucleus.mainsNucelusJ;

import gred.nucleus.autocrop.AutoCrop;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;

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
    public void run() throws IOException, FormatException {
        File inputFile = new File(_input);
        if(inputFile.isFile()){
            String[] tNameFile =  this._input.split(File.separator);
            this._prefix = tNameFile[tNameFile.length-1].replaceAll(".tif","");
            this._prefix = tNameFile[tNameFile.length-1].replaceAll(".TIF","");
            ImagePlus[] img = BF.openImagePlus(_input);
            Calibration cal = img[0].getCalibration();
            System.out.println(img[0].getTitle()+"\t"+cal.pixelWidth+"\t"+cal.pixelHeight+"\t"+cal.pixelDepth);
            autocropMethod(img[0]);
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
                    ImagePlus[] img = BF.openImagePlus(fileImg);
                    Calibration cal = img[0].getCalibration();
                    System.out.println(img[0].getTitle()+"\t"+cal.pixelWidth+"\t"+cal.pixelHeight+"\t"+cal.pixelDepth);
                    autocropMethod(img[0]);
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
