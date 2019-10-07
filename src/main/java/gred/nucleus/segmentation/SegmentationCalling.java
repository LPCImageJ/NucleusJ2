package gred.nucleus.segmentation;


import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.core.ConvexHullSegmentation;
import gred.nucleus.core.Measure3D;
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
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

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


    public SegmentationCalling(){}
    /**
     *  Constructor for ImagePlus input
     *

     * @param semgemtationParameters : list of parameters in config file.
     *
     */

    public SegmentationCalling(SegmentationParameters semgemtationParameters) {

        this.m_semgemtationParameters=semgemtationParameters;

    }


    public SegmentationCalling(String inputDir, String outputDir,SegmentationParameters semgemtationParameters) {
        this._inputDir = inputDir;
        this._output = outputDir;
        this.m_semgemtationParameters=semgemtationParameters;

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

    public String runSeveralImages2() throws IOException, FormatException , Exception{
        String log = "";
        String resu = "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\n";
        Directory directoryInput = new Directory(this.m_semgemtationParameters.getInputFolder());
        directoryInput.listFiles(this.m_semgemtationParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            File currentFile = directoryInput.getFile(i);
            String fileImg = currentFile.toString();
            FilesNames outPutFilesNames = new FilesNames(fileImg);
            this._prefix = outPutFilesNames.PrefixeNameFile();
            NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(currentFile ,this._prefix,this.m_semgemtationParameters);
            nucleusSegmentation.findOTSUmaximisingSephericity();
             this._imgSeg= nucleusSegmentation.applySegmentation2();

            if (nucleusSegmentation.getBadCrop()==true || nucleusSegmentation.getBestThreshold() == -1) {
                IJ.log("Bad crop " +fileImg+ "  "+nucleusSegmentation.getBestThreshold());
                File file = new File(this._inputDir+"/BadCrop");
                if (!file.exists()){
                    file.mkdir();
                }
                File fileToMove = new File(fileImg);
                fileToMove.renameTo(new File(this._inputDir+fileToMove.separator+"BadCrop"+fileToMove.separator+this._imgSeg.getTitle()));
                IJ.log("test "+this._inputDir+fileToMove.separator+"BadCrop"+fileToMove.separator+this._imgSeg.getTitle()+"\n");
                //FileUtils.moveFileToDirectory(fileImg, this._inputDir+"/BadCrop"/, REPLACE_EXISTING);

            }
            else {
                if (nucleusSegmentation.getBestThreshold() == -1) {
                    log = log + fileImg + "\n";
                }
                else {
                    if (this.m_semgemtationParameters.getGiftWrapping()) {
                        ConvexHullSegmentation nuc = new ConvexHullSegmentation();
                        this._imgSeg = nuc.run(this._imgSeg,this.m_semgemtationParameters);
                    }

                    String pathSeg = this.m_semgemtationParameters.getOutputFolder() + currentFile.getName();

                    this._imgSeg.setTitle(pathSeg);

                    saveFile(this._imgSeg, pathSeg);
                    resu+=nucleusSegmentation.saveImageResult(this._imgSeg);

                    /**
                    NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(this._imgSeg, this._imgSeg);
                    nucleusAnalysis.setResu(resu);
                    resu = nucleusAnalysis.nucleusParameter3D();
                     */

                }
            }



        }
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(new File(this.m_semgemtationParameters.getOutputFolder()+File.separator+"ParametersResults.txt")));
        writer.write(resu);
        writer.close();
        return log;

    }
        public String runSeveralImages() throws IOException, FormatException , Exception{
        String log = "";
        String resu = "";
            File [] fileList = new File(this.m_semgemtationParameters.getInputFolder()).listFiles();
        for(int i = 0; i < fileList.length; ++i) {
            String fileImg = fileList[i].toString();
            if (fileImg.contains(".tif")) {
                ImagePlus[] imgTab = BF.openImagePlus(fileImg);
                ImagePlus img = imgTab[0];
                ImagePlus imgSeg = img;
                if (imgSeg.getType() == ImagePlus.GRAY16)
                    this.preProcessImage(imgSeg);
                NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(imgSeg,this.m_semgemtationParameters.getM_minVolumeNucleus(), this.m_semgemtationParameters.getM_maxVolumeNucleus(),this.m_semgemtationParameters);
                imgSeg = nucleusSegmentation.applySegmentation(imgSeg);
                // TODO A Nettoyer les else !!!

                if (nucleusSegmentation.getBadCrop()==true || nucleusSegmentation.getBestThreshold() == -1) {
                    IJ.log("Bad crop " +fileImg+ "  "+nucleusSegmentation.getBestThreshold());
                    File file = new File(this.m_semgemtationParameters.getInputFolder()+"/BadCrop");
                    if (!file.exists()){
                        file.mkdir();
                    }
                    File fileToMove = new File(fileImg);
                    fileToMove.renameTo(new File(this.m_semgemtationParameters.getInputFolder()+fileToMove.separator+"BadCrop"+fileToMove.separator+img.getTitle()));
                    IJ.log("test "+this.m_semgemtationParameters.getInputFolder()+fileToMove.separator+"BadCrop"+fileToMove.separator+img.getTitle()+"\n");

                    //FileUtils.moveFileToDirectory(fileImg, this._inputDir+"/BadCrop"/, REPLACE_EXISTING);


                }
                else {
                    if (nucleusSegmentation.getBestThreshold() == -1) {
                        log = log + fileImg + "\n";
                    } else {
                        System.out.println(fileImg + "\totsu modif threshold " + nucleusSegmentation.getBestThreshold() + "\n");
                        if (this.m_semgemtationParameters.getGiftWrapping()) {
                            ConvexHullSegmentation nuc = new ConvexHullSegmentation();
                            imgSeg = nuc.run(imgSeg,this.m_semgemtationParameters);
                        }
                        String pathSeg = this._output + img.getTitle();
                        imgSeg.setTitle(pathSeg);
                        saveFile(imgSeg, pathSeg);
                        NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(img, imgSeg,this.m_semgemtationParameters);
                        nucleusAnalysis.setResu(resu);
                       // resu = nucleusAnalysis.nucleusParameter3D();
                    }
                }
            }
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter(new File(this.m_semgemtationParameters.getOutputFolder()+File.separator+"ParametersResults.txt")));
            writer.write(resu);
            writer.close();
        }

        return log;
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
    //TODO A ENLEVER APRES RESTRUCTURATION !!!!!
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
}