package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.exceptions.fileInOut;
import loci.formats.FormatException;
import java.io.File;
import java.io.IOException;



/**
 *  Core method calling the autocrop method. This method can be run on only one
 *  file or on directory containing multiple tuple file. This class will call
 *  AutoCrop class to detect nuclei in the image.
 * @author Tristan Dubos and Axel Poulet
 */

public class AutoCropCalling {


    /** image prefix name */
    private String _prefix = "";
    /** Get general information of cropping analyse */
    private String m_outputCropGeneralInfo="#HEADER\n";
    /** Parameters crop analyse */
    private AutocropParameters m_autocropParameters;

    /**
     * Constructor
     * Create the output directory if he isn't existed.

     */
    public AutoCropCalling( ) {

    }
    public AutoCropCalling(AutocropParameters autocropParameters ) {
        this.m_autocropParameters =autocropParameters;
        this.m_outputCropGeneralInfo=autocropParameters.getAnalyseParameters()
                +getColnameResult();
    }

    /**
     * Run auto crop on the input:
     * -If input is a file: open the image with bioformat plugin to obtain the
     *  metadata then run the auto crop.
     * -If input is directory, listed the file, foreach tif file loaded file
     *  with bioformat, run the auto crop.
     *
     * @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    public void run() throws Exception {

        Directory directoryInput=new Directory(
                this.m_autocropParameters.getInputFolder());
        directoryInput.listFiles(this.m_autocropParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        directoryInput.checkAndActualiseNDFiles();
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
                File currentFile = directoryInput.getFile(i);
                System.out.println("Current file "
                        +currentFile.getAbsolutePath());
                String fileImg = currentFile.toString();
                FilesNames outPutFilesNames = new FilesNames(fileImg);
                this._prefix = outPutFilesNames.PrefixeNameFile();
                AutoCrop autoCrop = new AutoCrop(currentFile, this._prefix,
                        this.m_autocropParameters);
                autoCrop.thresholdKernels();
                autoCrop.computeConnectcomponent();
                autoCrop.componentBorderFilter();
                autoCrop.componentSizeFilter();
                autoCrop.computeBoxes2();
                autoCrop.boxIntesection();
                autoCrop.cropKernels2();
                autoCrop.writeAnalyseInfo();
                annotAutoCrop test = new annotAutoCrop(
                        autoCrop.getFileCoordinates(), currentFile,
                        this.m_autocropParameters.getOutputFolder()
                                + this._prefix,this.m_autocropParameters);
                test.run();
                this.m_outputCropGeneralInfo=this.m_outputCropGeneralInfo
                        +autoCrop.getImageCropInfo();

        }
        System.out.println(this.m_autocropParameters.getInputFolder()
                +"result_Autocrop_Analyse");
        OutputTexteFile resultFileOutput=new OutputTexteFile(
                this.m_autocropParameters.getOutputFolder()
                        +"result_Autocrop_Analyse.csv");
        resultFileOutput.SaveTexteFile( this.m_outputCropGeneralInfo);
    }

    public String getColnameResult(){
        return "FileName\tNumberOfCrop\tOTSUThreshold\tDefaultOTSUThreshold\n";
    }
}


