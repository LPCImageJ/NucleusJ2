package gred.nucleus.mainsNucelusJ;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.autocrop.annotAutoCrop;
import gred.nucleus.exceptions.fileInOut;
import ij.ImagePlus;
import loci.formats.FormatException;
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
        directoryInput.listFiles(this._input);
        directoryInput.checkIfEmpty();
        directoryInput.checkAndActualiseNDFiles();
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
                File currentFile = directoryInput.getFile(i);
                String fileImg = currentFile.toString();
                FilesNames outPutFilesNames = new FilesNames(fileImg);
                this._prefix = outPutFilesNames.PrefixeNameFile();

                AutoCrop autoCrop = new AutoCrop(currentFile, this._output, this._prefix);
                autoCrop.thresholdKernels();
                autoCrop.cropKernels(autoCrop.computeBoxes(1));

                annotAutoCrop test = new annotAutoCrop(autoCrop.getFileCoordinates(), currentFile);
                test.run();
        }
             /*
       TODO remove after control
        if(directoryInput.getContainNdFile()){
            for (short i = 0; i < directoryInput.getNumberNDFiles(); ++i) {
                File currentFile = directoryInput.getNDFile(i);
                String fileImg = currentFile.toString();
                FilesNames outPutFilesNames = new FilesNames(fileImg);
                this._prefix = outPutFilesNames.PrefixeNameFile();

                AutoCrop autoCrop = new AutoCrop(currentFile, this._output, this._prefix);
                autoCrop.thresholdKernels();
                autoCrop.cropKernels(autoCrop.computeBoxes(1));

                annotAutoCrop test = new annotAutoCrop(autoCrop.getFileCoordinates(), currentFile);
                test.run();
            }
        }
        else {
        */

    }

}

