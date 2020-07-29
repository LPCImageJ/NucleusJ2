package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.exceptions.fileInOut;
import loci.formats.FormatException;
import java.io.File;
import java.io.IOException;
import java.util.List;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.repository.DatasetContainer;



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
     * Run auto crop on image's folder:
     * -If input is a file: open the image with bioformat plugin to obtain the
     *  metadata then run the auto crop.
     * -If input is directory, listed the file, foreach tif file loaded file
     *  with bioformat, run the auto crop.
     *
     * @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    public void runFolder() throws Exception {

        Directory directoryInput=new Directory(
                this.m_autocropParameters.getInputFolder());
        directoryInput.listImageFiles(this.m_autocropParameters.getInputFolder());
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
                autoCrop.addCROP_parameter();
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


    /**
     *  Run auto crop on one image :
     *   -If input is a file: open the image with bioformat plugin to obtain the
     *   metadata then run the auto crop.
     *   -If input is directory, listed the file, foreach tif file loaded file
     *   with bioformat, run the auto crop.
     *
     * @param file
     * @throws Exception
     */
    public void runFile(String file) throws Exception {
        File currentFile =  new File(file);
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
        autoCrop.addCROP_parameter();
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

    public void runImageOmero(ImageContainer image, 
                              Long outputDirectoryImages,
                              Long outputDirectoryProjection,
                              Client client) 
        throws Exception
    {
        String fileImg = image.getName();
        FilesNames outPutFilesNames = new FilesNames(fileImg);
        this._prefix = outPutFilesNames.PrefixeNameFile();
        AutoCrop autoCrop = new AutoCrop(image, this.m_autocropParameters, client);
        autoCrop.thresholdKernels();
        autoCrop.computeConnectcomponent();
        autoCrop.componentBorderFilter();
        autoCrop.componentSizeFilter();
        autoCrop.computeBoxes2();
        autoCrop.addCROP_parameter();
        autoCrop.boxIntesection();
        Long id = autoCrop.cropKernelsOmero(image, outputDirectoryImages, client);
        autoCrop.writeAnalyseInfoOmero(id, client);
        
        annotAutoCrop test = new annotAutoCrop(
                autoCrop.getFileCoordinates(), image, this.m_autocropParameters, client);
        test.runOmero(outputDirectoryProjection, client);

        this.m_outputCropGeneralInfo=this.m_outputCropGeneralInfo
                +autoCrop.getImageCropInfo();
    }

    public void runSeveralImageOmero(List<ImageContainer> images, 
                                     Long outputDirectory, 
                                     Client client) 
        throws Exception {
	DatasetContainer datasetRes = new DatasetContainer("resultsAutocrop", "");
	DatasetContainer datasetProj = new DatasetContainer("projectionsAutocrop", "");

	Long datasetResId = client.getProject(outputDirectory).addDataset(client, datasetRes).getId();
	Long datasetProjId = client.getProject(outputDirectory).addDataset(client, datasetProj).getId();

        for(ImageContainer image : images) {
            runImageOmero(image, datasetResId, datasetProjId, client);
        }
    }



    /**
     * List of colunms name in csv coordinates output file.
     * @return columns name
     */

    public String getColnameResult(){
    return "FileName\tNumberOfCrop\tOTSUThreshold\tDefaultOTSUThreshold\n";
    }
}


