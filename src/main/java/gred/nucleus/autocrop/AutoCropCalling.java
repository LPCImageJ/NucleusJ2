package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.autocrop.AutoCrop;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.annotAutoCrop;
import gred.nucleus.exceptions.fileInOut;
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
    /** Get general information of cropping analyse */
    private String m_outputCropGeneralInfo="#HEADER\n";
    /** Parameters crop analyse */
    private AutocropParameters m_autocropParameters;

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
    public AutoCropCalling(AutocropParameters autocropParameters ) {
        this.m_autocropParameters =autocropParameters;
        this._input = autocropParameters.getInputFolder();
        this._output = autocropParameters.getOutputFolder();
        Directory dirOutput =new Directory(this._output);
        dirOutput.CheckAndCreateDir();
        this._output=dirOutput.get_dirPath();
        this.m_outputCropGeneralInfo=autocropParameters.getAnalyseParameters()+getColnameResult();
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
                AutoCrop autoCrop = new AutoCrop(currentFile, this._prefix,this.m_autocropParameters);

                autoCrop.thresholdKernels();
                autoCrop.computeConnectcomponent();
                autoCrop.componentBorderFilter();
                autoCrop.componentSizeFilter();
                autoCrop.computeBoxes2();
                autoCrop.cropKernels2();
                autoCrop.writeAnalyseInfo();
                annotAutoCrop test = new annotAutoCrop(autoCrop.getFileCoordinates(), currentFile, this._output + this._prefix);
                test.run();
                this.m_outputCropGeneralInfo=this.m_outputCropGeneralInfo+autoCrop.getImageCropInfo();

        }
        System.out.println(this._input+"result_Autocrop_Analyse");
        OutputTexteFile resultFileOutput=new OutputTexteFile(this._output+"result_Autocrop_Analyse");
        resultFileOutput.SaveTexteFile( this.m_outputCropGeneralInfo);
    }

    public String getColnameResult(){
        return "Filename\tNumber_of_Crop\n";
    }
    }


