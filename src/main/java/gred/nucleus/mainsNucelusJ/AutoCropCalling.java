package gred.nucleus.mainsNucelusJ;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.autocrop.annotAutoCrop;
import gred.nucleus.exceptions.fileInOut;
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
    /** image prefix name */
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
        Directory dirOutput =new Directory(this._output);
        dirOutput.CheckAndCreateDir();
        this._output=dirOutput.get_dirPath();
    }


    /**
     * Run auto crop on the input,
     * If input is a file: open the image with bioformat plugin to obtain the metadata then run the auto crop.
     * If input is directory, listed the file, foreach tif file loaded file with bioformat, run the auto crop.
     *
     * @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    public void run() throws IOException, FormatException,fileInOut,Exception {
        Directory directoryInput=new Directory(this._input);
        directoryInput.GetListFiles(this._input);

        for(short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            File currentFile = directoryInput.getFile(i);
            String fileImg = currentFile.toString();
            FilesNames outPutFilesNames=new FilesNames(fileImg);
            this._prefix=outPutFilesNames.PrefixeNameFile();
            ImagePlus[] img = BF.openImagePlus(fileImg);
           // autocropMethod(img[0]);

            AutoCrop autoCrop = new AutoCrop(currentFile,this._output,this._prefix);
            autoCrop.thresholdKernels();
            autoCrop.cropKernels(autoCrop.computeBoxes(1));
        }
        /**  OLD
        File inputFile = new File(this._input);

        if(inputFile.isFile()){
            FilesNames outPutFilesNames=new FilesNames(this._input);
            this._prefix=outPutFilesNames.PrefixeNameFile();
            ImagePlus[] img = BF.openImagePlus(_input);
            autocropMethod(img[0]);
        }
        else{
            File[] listOfFiles = new File(_input).listFiles();
            for(int i = 0; i < listOfFiles.length; ++i) {
                String fileImg = listOfFiles[i].toString();
                FilesNames outPutFilesNames=new FilesNames(fileImg);
                this._prefix=outPutFilesNames.PrefixeNameFile();
                ImagePlus[] img = BF.openImagePlus(fileImg);
                autocropMethod(img[0]);
            }
        }
         */
    }


    /**
     *  @throws IOException if file problem
     *  @throws FormatException Bioformat exception
     *
     * private method calling the autoCrop Class,
     * @param img ImagePlus input to crop
     */
    // TODO Clean method
    private void autocropMethod(ImagePlus img)throws IOException, FormatException, fileInOut ,Exception{
        System.out.println("Ici on a le truc"+ img.getTitle()+" "+this._prefix+" "+this._output+" "+this._input);

      /*
        AutoCrop autoCrop = new AutoCrop (img,this._prefix,this._output,this._input);
        autoCrop.thresholdKernels();
        autoCrop.cropKernels(autoCrop.computeBoxes(1));
        //autoCrop.getOutputFileArrayList();
        //annotAutoCrop projectionWithBoxes  = new annotAutoCrop(autoCrop.getFileCoordinates(),this._input+img.getTitle());
        //annotAutoCrop test  = new annotAutoCrop(autoCrop.getFileCoordinates(),this._input+img.getTitle());
        /*
        ICI C ETAIT UN AUTOCROP SUR LA Z PROJECTION !!!!
        AutoCrop autoCropZ = new AutoCrop (img,this._prefix,this._outputZprojection,this._input);
        autoCropZ.thresholdKernelsZprojection();
        autoCropZ.cropKernels(autoCropZ.computeBoxes(1));
        autoCropZ.getOutputFileArrayList();
        annotAutoCrop projectionWithBoxesZ  = new annotAutoCrop(autoCropZ.getFileCoordinates(),this._input+img.getTitle());
        annotAutoCrop testZ  = new annotAutoCrop(autoCropZ.getFileCoordinates(),this._input+img.getTitle());


        System.out.println(_prefix+"\t"+autoCrop.getNbOfNuc()+" nuclei detected");
         */


    }


}
