package gred.nucleus.mainsNucelusJ;

import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.autocrop.annotAutoCrop;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;
import loci.formats.FormatException;
import loci.plugins.BF;
import java.io.File;
import java.io.IOException;


/**
 *  Core method calling the autocrop method. this method can be run on only one file
 *  or on directory containing multiple tuple file.
 *  This class will call AutoCrop class to detect the mutipe nuclei in the image.
 * @author Tristan Dubos and Axel Poulet
 */

public class AutoCropCalling {
    /** path of the input file*/
    private String _input;
    /** output to save the images cropped*/
    private String _output;
    /** prefix for the name of the image*/
    private String _prefix = "";



    /**
     * Constructor
     * Create the output directory if he isn't existed.
     * @param imageSourceFile  String path of input file(s)
     * @param output String to save the results image
     */
    public AutoCropCalling(String imageSourceFile, String output ) {
        this._input = imageSourceFile;
        this._output = output;
        File outputFile = new File(this._output);
        if(!(outputFile .exists()))
            outputFile.mkdir();
    }


    /**
     * Run auto crop on the input,
     * If input is a file: open the image with bioformat plugin to obtain the metadata then run the auto crop.
     * If input is directory, listed the file, foreach tif file loaded file with bioformat, run the auto crop.
     *
     * @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    public void run() throws IOException, FormatException {
        File inputFile = new File(_input);
        if(inputFile.isFile()){
            String[] tNameFile =  this._input.split(File.separator);
            this._prefix = tNameFile[tNameFile.length-1].replaceAll(".tif","");
            this._prefix = _prefix.replaceAll(".TIF","");
            ImagePlus[] img = BF.openImagePlus(_input);
            Calibration cal = img[0].getCalibration();
            System.out.println(img[0].getTitle()+"\t"+cal.pixelWidth+"\t"+cal.pixelHeight+"\t"+cal.pixelDepth);
            autocropMethod(img[0]);
        }
        else{
            File[] listOfFiles = new File(_input).listFiles();
            for(int i = 0; i < listOfFiles.length; ++i) {
                String fileImg = listOfFiles[i].toString();
                if (fileImg.contains(".tif") || fileImg.contains(".TIF")) {
                    long maxMemory = Runtime.getRuntime().freeMemory();
                    /* Maximum amount of memory the JVM will attempt to use */
                    System.out.println("Image suivante : "+listOfFiles[i].toString()+" la ram en est la : " +
                            (maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9));
                    String[] tNameFile =  fileImg.split(File.separator);
                    this._prefix = tNameFile[tNameFile.length-1].replaceAll(".tif","");
                    this._prefix = _prefix.replaceAll(".TIF","");
                    ImagePlus[] img = BF.openImagePlus(fileImg);
                    Calibration cal = img[0].getCalibration();
                    System.out.println(img[0].getTitle()+"\t"+cal.pixelWidth+"\t"+cal.pixelHeight+"\t"+cal.pixelDepth);
                    autocropMethod(img[0]);
                }
            }
        }
    }


    /**
     *  @throws IOException if file problem
     *  @throws FormatException Bioformat exception
     *
     * private method calling the autoCrop Class,
     * @param img ImagePlus input to crop
     */
    private void autocropMethod(ImagePlus img)throws IOException, FormatException{

        AutoCrop autoCrop = new AutoCrop (img,this._prefix,this._output,this._input);
        autoCrop.thresholdKernels();
        autoCrop.cropKernels(autoCrop.computeBoxes(1));
        autoCrop.getOutputFileArrayList();
        annotAutoCrop projectionWithBoxes  = new annotAutoCrop(autoCrop.getFileCoordinates(),this._input+img.getTitle());
        //annotAutoCrop test  = new annotAutoCrop(autoCrop.getFileCoordinates(),this._input+img.getTitle());
        /*
        ICI C ETAIT UN AUTOCROP SUR LA Z PROJECTION !!!!

        AutoCrop autoCropZ = new AutoCrop (img,this._prefix,this._outputZprojection,this._input);
        autoCropZ.thresholdKernelsZprojection();
        autoCropZ.cropKernels(autoCropZ.computeBoxes(1));
        autoCropZ.getOutputFileArrayList();
        annotAutoCrop projectionWithBoxesZ  = new annotAutoCrop(autoCropZ.getFileCoordinates(),this._input+img.getTitle());

        annotAutoCrop testZ  = new annotAutoCrop(autoCropZ.getFileCoordinates(),this._input+img.getTitle());
        */
        System.out.println(_prefix+"\t"+autoCrop.getNbOfNuc()+" nuclei detected");


    }


}
