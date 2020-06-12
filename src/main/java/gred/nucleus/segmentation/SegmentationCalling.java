package gred.nucleus.segmentation;

import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.core.ConvexHullSegmentation;
import gred.nucleus.core.NucleusSegmentation;
import gred.nucleus.nucleusCaracterisations.NucleusAnalysis;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.StackConverter;
import loci.formats.FormatException;
import loci.plugins.BF;
import ome.jxrlib.ImageData;
import omero.gateway.model.TableData;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import fr.gredclermont.omero.Client;
import fr.gredclermont.omero.ImageContainer;
import fr.gredclermont.omero.metadata.ROIContainer;
import fr.gredclermont.omero.metadata.TableContainer;
import fr.gredclermont.omero.repository.DatasetContainer;
import fr.gredclermont.omero.repository.ProjectContainer;
import gred.nucleus.FilesInputOutput.Directory;


/**
 * This class call the different segmentation methods available to detect the nucleus.
 * The Ostu method modified and the gift wrapping 3D. Methods can be call for analysis of several images or only one.
 * The gift wrapping is initialized by teh Otsu method modified, then the gift wrapping algorithm process the result
 * obtain with the first method.
 * If the first method doesn't detect a nucleus, a message is print on the console.
 *
 * if the nucleus input image is 16bit, a preprocess is done to convert it in 8bit, and also increase the contrast
 * and decrease the noise, then the 8bits image is used for the nuclear segmentation.
 *
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class SegmentationCalling {
    /** ImagePlus raw image*/
    private ImagePlus _imgInput = new ImagePlus();
    /** ImagePlus segmented image*/
    private ImagePlus _imgSeg = new ImagePlus();
    /** String of of the path for the output files*/
    private String _output;
    /** String of the input dir for several nuclei analysis*/
    private String _inputDir = "";

    String _prefix="";

    private SegmentationParameters m_semgemtationParameters;

    private String m_outputCropGeneralInfoOTSU;
    private String m_outputCropGeneralInfoGIFT;


    public SegmentationCalling(){}
    /**
     *  Constructor for ImagePlus input
     *

     * @param semgemtationParameters : list of parameters in config file.
     *
     */

    public SegmentationCalling(SegmentationParameters semgemtationParameters) {

        this.m_semgemtationParameters=semgemtationParameters;
        this.m_outputCropGeneralInfoOTSU=this.m_semgemtationParameters.getAnalyseParameters()+getColnameResult();
        this.m_outputCropGeneralInfoGIFT=this.m_semgemtationParameters.getAnalyseParameters()+getColnameResult();


    }


    public SegmentationCalling(String inputDir, String outputDir) {
        this._inputDir = inputDir;
        this._output = outputDir;
        this.m_outputCropGeneralInfoOTSU=this.m_semgemtationParameters.getAnalyseParameters()+getColnameResult();
        this.m_outputCropGeneralInfoGIFT=this.m_semgemtationParameters.getAnalyseParameters()+getColnameResult();


    }

    /**
     *  Constructor for ImagePlus input
     *
     * @param img ImagePlus raw image
     * @param vMin volume min of the detected object
     * @param vMax volume max of the detected object
     * @param outputImg String of of the path to save the img of the segmented nucleus.
     *
     */
    public SegmentationCalling(ImagePlus img, short vMin, int vMax, String outputImg) {
        this.m_semgemtationParameters.setMinVolumeNucleus(vMin);
        this.m_semgemtationParameters.setMaxVolumeNucleus(vMax);
        this._imgInput = img;
        this._output = outputImg + File.separator + "Segmented" + this._imgInput.getTitle();
    }

    /**
     *  Constructor for ImagePlus input
     *
     * @param img ImagePlus raw image
     * @param vMin volume min of the detected object
     * @param vMax volume max of the detected object
     *
     */
    public SegmentationCalling(ImagePlus img, short vMin, int vMax) {
        this.m_semgemtationParameters.setMinVolumeNucleus(vMin);
        this.m_semgemtationParameters.setMaxVolumeNucleus(vMax);
        this._imgInput = img;
    }

    /**
     * Constructor for directory input
     *
     * @param inputDir String path of the input containing the tif/TIF file
     * @param outputDir String of of the path to save results img of the segmented nucleus.
     * @param vMin volume min of the detected object
     * @param vMax volume max of the detected object
     */
    public SegmentationCalling(String inputDir, String outputDir, short vMin, int vMax) {
        this.m_semgemtationParameters.setMinVolumeNucleus(vMin);
        this.m_semgemtationParameters.setMaxVolumeNucleus(vMax);
        this._inputDir = inputDir;
        this._output = outputDir;
        Directory dirOutput =new Directory(this._output );
        dirOutput.CheckAndCreateDir();
        this._output=dirOutput.get_dirPath();
    }
    /**
     * Method to run an ImagePlus input
     * the method will call method in NucleusSegmentation and ConvexHullSegmentation to segment the input nucleus.
     * if the input boolean is true the gift wrapping will be use, if false the Otsu modified method will be used.
     * If a segmentation results is find the method will then computed the different parameters with the NucleusAnalysis
     * class, results will be print in the console. If no nucleus is detected a log message is print in teh console
     *
     * @return ImagePlus the segmented nucleus
     */
    public int runOneImage() throws Exception{
        ImagePlus imgSeg= this._imgInput;
        NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(imgSeg,this.m_semgemtationParameters.getM_minVolumeNucleus(), this.m_semgemtationParameters.getM_maxVolumeNucleus(),this.m_semgemtationParameters);

        Calibration cal = imgSeg.getCalibration();
        if(imgSeg.getType() == ImagePlus.GRAY16)
            this.preProcessImage(imgSeg);

        imgSeg = nucleusSegmentation.applySegmentation(imgSeg);
        if(nucleusSegmentation.getBestThreshold() == -1)
            System.out.println("Segmentation error: \nNo object is detected between "+this.m_semgemtationParameters.getM_minVolumeNucleus() + "and"+this.m_semgemtationParameters.getM_maxVolumeNucleus());
        else{
            System.out.println("otsu modif threshold: "+nucleusSegmentation.getBestThreshold()+"\n");
            if (this.m_semgemtationParameters.getGiftWrapping()){
                ConvexHullSegmentation nuc = new ConvexHullSegmentation();
                imgSeg = nuc.run(imgSeg,this.m_semgemtationParameters);
            }
            imgSeg.setTitle(this._output);
            if(!this._output.equals(""))
                saveFile(imgSeg, this._output);
            NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(this._imgInput,imgSeg,this.m_semgemtationParameters);
           // System.out.println(nucleusAnalysis.nucleusParameter3D());
        }
        this._imgSeg = imgSeg;
        return nucleusSegmentation.getBestThreshold() ;
    }

    /**
     * getter of the image segmented
     * @return
     */
    public ImagePlus getImageSegmented(){
        return this._imgSeg;
    }


    /**
     *
     * Method to run the nuclear segmentation of images stocked in input dir.
     * First listing of the tif files contained in input dir.
     * then for each images:
     * the method will call method in NucleusSegmentation and ConvexHullSegmentation to segment the input nucleus.
     * if the input boolean is true the gift wrapping will be use, if false the Otsu modified method will be used.
     * If a segmentation results is find the method will then computed the different parameters with the NucleusAnalysis
     *  class, and save in file in the outputDir. If no nucleus is detected a log message is print in the console
     *
     * Open the image with bioformat plugin to obtain the metadata:
     *          ImagePlus[] imgTab = BF.openImagePlus(fileImg);
     *
     * @return String with the name files which failed in the segmentation step
     * @throws IOException if file doesn't existed
     * @throws FormatException Bioformat exception
     */

    public String runSeveralImages2() throws  Exception{
        String log = "";
        Directory directoryInput = new Directory(this.m_semgemtationParameters.getInputFolder());
        directoryInput.listImageFiles(this.m_semgemtationParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            File currentFile = directoryInput.getFile(i);
            String fileImg = currentFile.toString();
            FilesNames outPutFilesNames = new FilesNames(fileImg);
            this._prefix = outPutFilesNames.PrefixeNameFile();
            System.out.println("Current image in process "+currentFile);

            String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
            System.out.println( "Start :"+ timeStampStart);

            NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(currentFile ,this._prefix,this.m_semgemtationParameters);
            nucleusSegmentation.preProcessImage();
            nucleusSegmentation.findOTSUmaximisingSephericity();
            nucleusSegmentation.checkBadCrop(this.m_semgemtationParameters.m_inputFolder);
            nucleusSegmentation.saveOTSUSegmented();
            this.m_outputCropGeneralInfoOTSU= this.m_outputCropGeneralInfoOTSU+nucleusSegmentation.getImageCropInfoOTSU();
            nucleusSegmentation.saveGiftWrappingSeg();
            this.m_outputCropGeneralInfoGIFT= this.m_outputCropGeneralInfoGIFT+nucleusSegmentation.getImageCropInfoGIFT();

            timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
            System.out.println( "Fin :"+ timeStampStart);

        }
        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(this.m_semgemtationParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"OTSU"
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( this.m_outputCropGeneralInfoOTSU);
        if(this.m_semgemtationParameters.getGiftWrapping()) {
            OutputTexteFile resultFileOutputGIFT = new OutputTexteFile(this.m_semgemtationParameters.getOutputFolder()
                    + directoryInput.getSeparator()
                    + "GIFT"
                    + directoryInput.getSeparator()
                    + "result_Segmentation_Analyse.csv");
            resultFileOutputGIFT.SaveTexteFile(this.m_outputCropGeneralInfoGIFT);
        }

        return log;
    }

    public String runOneImage(String filePath) throws  Exception{
        String log = "";
        File currentFile = new File(filePath);
        String fileImg = currentFile.toString();
        FilesNames outPutFilesNames = new FilesNames(fileImg);
        this._prefix = outPutFilesNames.PrefixeNameFile();
        System.out.println("Current image in process "+currentFile);

        String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Start :"+ timeStampStart);
        NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(currentFile ,this._prefix,this.m_semgemtationParameters);
        nucleusSegmentation.preProcessImage();
        nucleusSegmentation.findOTSUmaximisingSephericity();
        nucleusSegmentation.checkBadCrop(this.m_semgemtationParameters.m_inputFolder);
        nucleusSegmentation.saveOTSUSegmented();
        this.m_outputCropGeneralInfoOTSU= this.m_outputCropGeneralInfoOTSU+nucleusSegmentation.getImageCropInfoOTSU();
        nucleusSegmentation.saveGiftWrappingSeg();
        this.m_outputCropGeneralInfoGIFT= this.m_outputCropGeneralInfoGIFT+nucleusSegmentation.getImageCropInfoGIFT();

        timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Fin :"+ timeStampStart);
        return log;
    }

    public String runOneImageOmero(ImageContainer image, Long output, Client client) throws  Exception{
        String log = "";

        String fileImg = image.getName();
        System.out.println("Current image in process "+ fileImg);

        String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Start :"+ timeStampStart);
        NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(image, this.m_semgemtationParameters, client);
        nucleusSegmentation.preProcessImage();
        nucleusSegmentation.findOTSUmaximisingSephericity();
        nucleusSegmentation.checkBadCrop(image, client);

        nucleusSegmentation.saveOTSUSegmentedOmero(client, output);
        this.m_outputCropGeneralInfoOTSU= this.m_outputCropGeneralInfoOTSU+nucleusSegmentation.getImageCropInfoOTSU();
        nucleusSegmentation.saveGiftWrappingSegOmero(client, output);
        this.m_outputCropGeneralInfoGIFT= this.m_outputCropGeneralInfoGIFT+nucleusSegmentation.getImageCropInfoGIFT();

        timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Fin :"+ timeStampStart);

        return log;
    }

    public String runSeveralImageOmero(List<ImageContainer> images, Long output, Client client) throws  Exception{
        String log = "";

        for (ImageContainer image : images) {
            log += runOneImageOmero(image, output, client);
        }

        DatasetContainer dataset = client.getProject(output).getDataset("OTSU").get(0);

        String path = new java.io.File( "." ).getCanonicalPath() + "result_Segmentation_Analyse.csv";
        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(path);
        resultFileOutputOTSU.SaveTexteFile( this.m_outputCropGeneralInfoOTSU);
        
        File file = new File(path);
        dataset.addFile(client, file);
        file.delete();

        if(this.m_semgemtationParameters.getGiftWrapping()) {
            dataset = client.getProject(output).getDataset("GIFT").get(0);
            OutputTexteFile resultFileOutputGIFT = new OutputTexteFile(path);
            resultFileOutputGIFT.SaveTexteFile(this.m_outputCropGeneralInfoGIFT);

            file = new File(path);
            dataset.addFile(client, file);
            file.delete();
        }

        return log;
    }

    

    public String runOneImageOmeroROI(ImageContainer image, Long output, Client client) throws  Exception{

        List<ROIContainer> rois = image.getROIs(client);

        String log = "";

        String fileImg = image.getName();
        System.out.println("Current image in process "+ fileImg);

        String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Start :"+ timeStampStart);

        int i = 0;

        for(ROIContainer roi : rois)
        {
            System.out.println("Current ROI in process : n°" + i);

            NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(image, roi, i, this.m_semgemtationParameters, client);
            nucleusSegmentation.preProcessImage();
            nucleusSegmentation.findOTSUmaximisingSephericity();
            nucleusSegmentation.checkBadCrop(image, client);

            nucleusSegmentation.saveOTSUSegmentedOmero(client, output);
            this.m_outputCropGeneralInfoOTSU= this.m_outputCropGeneralInfoOTSU+nucleusSegmentation.getImageCropInfoOTSU();
            nucleusSegmentation.saveGiftWrappingSegOmero(client, output);
            this.m_outputCropGeneralInfoGIFT= this.m_outputCropGeneralInfoGIFT+nucleusSegmentation.getImageCropInfoGIFT();

            i++;
        }

        timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        System.out.println( "Fin :"+ timeStampStart);

        DatasetContainer dataset = client.getProject(output).getDataset("OTSU").get(0);
        String path = new java.io.File( "." ).getCanonicalPath() + "result_Segmentation_Analyse.csv";
        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(path);
        resultFileOutputOTSU.SaveTexteFile( this.m_outputCropGeneralInfoOTSU);
        
        File file = new File(path);
        dataset.addFile(client, file);
        file.delete();

        if(this.m_semgemtationParameters.getGiftWrapping()) {
            dataset = client.getProject(output).getDataset("GIFT").get(0);
            OutputTexteFile resultFileOutputGIFT = new OutputTexteFile(path);
            resultFileOutputGIFT.SaveTexteFile(this.m_outputCropGeneralInfoGIFT);

            file = new File(path);
            dataset.addFile(client, file);
            file.delete();
        }

        return log;
    }

    public String runSeveralImageOmeroROI(List<ImageContainer> images, Long output, Client client) throws  Exception{
        String log = "";

        for (ImageContainer image : images) {
            log += runOneImageOmeroROI(image, output, client);
        }

        return log;
    }

    private static void addColumnName(TableContainer table)
    {
        table.setColumn(0, "NucleusFileName", String.class);
        table.setColumn(1, "Volume", String.class);
        table.setColumn(2, "Flatness", String.class);
        table.setColumn(3, "Elongation", String.class);
        table.setColumn(4, "Sphericity", String.class);
        table.setColumn(5, "Esr", String.class);
        table.setColumn(6, "SurfaceArea", String.class);
        table.setColumn(7, "SurfaceAreaCorrected", String.class);
        table.setColumn(8, "SphericityCorrected", String.class);
        table.setColumn(9, "MeanIntensityNucleus", String.class);
        table.setColumn(10, "MeanIntensityBackground", String.class);
        table.setColumn(11, "StandardDeviation", String.class);
        table.setColumn(12, "MinIntensity", String.class);
        table.setColumn(13, "MaxIntensity", String.class);
        table.setColumn(14, "MedianIntensityImage", String.class);
        table.setColumn(15, "MedianIntensityNucleus", String.class);
        table.setColumn(16, "MedianIntensityBackground", String.class);
        table.setColumn(17, "ImageSize", String.class);
        table.setColumn(18, "OTSUThreshold", String.class);
    }

    private static void saveTable(TableContainer table, 
                                  Long id, 
                                  String name, 
                                  Client client)
        throws Exception
    {
        ProjectContainer project = client.getProject(id);
        List<DatasetContainer> datasets = project.getDataset(name);

        DatasetContainer dataset;
        dataset = datasets.get(0);

        dataset.addTable(client, table);
    }


    /**
     * Method which save the image in the directory.
     *
     * @param imagePlusInput Image to be save
     * @param pathFile path of directory
     */
    private void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiffStack(pathFile);
    }

    /**
     * 16bits image preprocessing
     * normalised the histohram distribution
     * apply a gaussian filter to smooth the signal
     * convert the image in 8bits
     * @param img 16bits ImagePlus
     */
    //TODO A ENLEVER APRES RESTRUCTURATION ATTENTION INTEGRATION DANS LES FENETRES GRAPHIQUES PAS ENCORE UPDATE DC CA CRASH!!!!!
    private void preProcessImage(ImagePlus img){
        ContrastEnhancer enh = new ContrastEnhancer();
        enh.setNormalize(true);
        enh.setUseStackHistogram(true);
        enh.setProcessStack(true);
        enh.stretchHistogram(img.getProcessor(), 0.05);
        GaussianBlur3D.blur(img, 0.5,0.5,1);
        StackConverter stackConverter = new StackConverter( img );
        stackConverter.convertToGray8();
    }


    public String getColnameResult(){
        return "NucleusFileName\t" +
                "Volume\t" +
                "Flatness\t" +
                "Elongation\t" +
                "Sphericity\t" +
                "Esr\t" +
                "SurfaceArea\t" +
                "SurfaceAreaCorrected\t" +
                "SphericityCorrected\t" +
                "MeanIntensityNucleus\t" +
                "MeanIntensityBackground\t" +
                "StandardDeviation\t" +
                "MinIntensity\t" +
                "MaxIntensity\t" +
                "MedianIntensityImage\t" +
                "MedianIntensityNucleus\t" +
                "MedianIntensityBackground\t" +
                "ImageSize\t" +
                "OTSUThreshold\n";
    }
}