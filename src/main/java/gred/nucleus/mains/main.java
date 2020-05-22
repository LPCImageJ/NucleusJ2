package gred.nucleus.mains;
import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.autocrop.*;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.segmentation.SegmentationCalling;

import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.plugin.Concatenator;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.common.DebugTools;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class main {



    /**
     * Method to run autocrop with only input output folder and with default parameters which are:
     *
     * xCropBoxSize:40
     * yCropBoxSize:40
     * zCropBoxSize:20
     * thresholdOTSUcomputing:20
     * slicesOTSUcomputing:0
     * channelToComputeThreshold:1
     * maxVolumeNucleus:2147483647
     * minVolumeNucleus:1
     *
     * @param inputDirectory path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCropFolder(String inputDirectory, String outputDirectory) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(inputDirectory,outputDirectory);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }

    /**
     * Method to run autocrop with input folder, output folder and with config file analysis:
     * @param inputDirectory path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @param pathToConfig path to config file
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCropFolder(String inputDirectory, String outputDirectory, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(inputDirectory,outputDirectory,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }

    /**
     * Method to run autocrop with input folder, output folder :
     * @param inputDirectory  path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @throws IOException
     * @throws fileInOut
     * @throws Exception
     */

    public static void runAutoCropFile(String inputDirectory, String outputDirectory) throws IOException , fileInOut,Exception{
        //AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutocropParameters autocropParameters= new AutocropParameters(inputDirectory,outputDirectory);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFile(inputDirectory);
    }



    //========================= Segmentation calling ===========================================

    /**
     * Method to run segmentation with input folder, output folder :
     * @param inputDirectory path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @throws Exception
     */

    public static void segmentationFolder(String inputDirectory, String outputDirectory ) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    /**
     * Method to run segmentation with input folder, output folder with config file :
     * @param inputDirectory path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @param config path to config file
     * @throws Exception
     */
    public static void segmentationFolder(String inputDirectory, String outputDirectory ,String config) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory,config);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    /**
     * Method to run segmentation on one image :
     * @param inputDirectory path to one raw image
     * @param outputDirectory path to output folder analysis
     * @throws Exception
     */

    public static void segmentationOneImage(String inputDirectory, String outputDirectory) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    /**
     * Compute parameter from raw data folder and segmented data :
     *
     * @param rawImagesInputDirectory path to the raw image's folder
     * @param segmentedImagesDirectory path to the segmented image's folder
     * @param pathToConfig path to config file
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */

    public static void computeNucleusParameters(String rawImagesInputDirectory, String segmentedImagesDirectory, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory,pathToConfig);
        Directory directoryInput = new Directory(pluginParameters.getInputFolder());
        directoryInput.listImageFiles(pluginParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> rawImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < rawImages.size(); ++i) {
            File currentFile = rawImages.get(i);
            ImagePlus Raw = new ImagePlus(currentFile.getAbsolutePath());
            ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());


            Measure3D mesure3D = new Measure3D(Segmented, Raw, pluginParameters.getXcalibration(Raw), pluginParameters.getYcalibration(Raw),pluginParameters.getZcalibration(Raw));
            outputCropGeneralInfoOTSU+=mesure3D.nucleusParameter3D()+"\n";
        }
        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( outputCropGeneralInfoOTSU);

    }

    // TODO  configFILE FACTORISABLE AVEC computeNucleusParameters SANS CONFINGFILE

    /**
     *
     * Compute parameter from raw data folder and segmented data :
     *
     * @param rawImagesInputDirectory path to the raw image's folder
     * @param segmentedImagesDirectory path to the segmented image's folder
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */

    public static void computeNucleusParameters(String rawImagesInputDirectory, String segmentedImagesDirectory) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory);
        Directory directoryInput = new Directory(pluginParameters.getOutputFolder());
        directoryInput.listImageFiles(pluginParameters.getOutputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> segImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < segImages.size(); ++i) {
            File currentFile = segImages.get(i);
            System.out.println("current File "+currentFile.getName());

            ImagePlus Raw = new ImagePlus(pluginParameters.getInputFolder()+directoryInput.getSeparator()+currentFile.getName());
            ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());
            Measure3D mesure3D = new Measure3D(Segmented, Raw, pluginParameters.getXcalibration(Raw), pluginParameters.getYcalibration(Raw),pluginParameters.getZcalibration(Raw));
            outputCropGeneralInfoOTSU+=mesure3D.nucleusParameter3D()+"\n";
        }

        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( outputCropGeneralInfoOTSU);

    }

    // TODO AJOUTER computeNucleusParametersDL avec configFILE FACTORISABLE AVEC computeNucleusParametersCONFINGFILE

    /**
     *
     *
     * Compute parameter from raw data folder and segmented data from deep leaning output:
     *
     * @param rawImagesInputDirectory path to the raw image's folder
     * @param segmentedImagesDirectory path to the segmented image's folder
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */

    public static void computeNucleusParametersDL(String rawImagesInputDirectory, String segmentedImagesDirectory) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory);
        Directory directoryInput = new Directory(pluginParameters.getOutputFolder());
        directoryInput.listImageFiles(pluginParameters.getOutputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> segImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < segImages.size(); ++i) {
            File currentFile = segImages.get(i);
            System.out.println("current File "+currentFile.getName());
            ImagePlus Raw = new ImagePlus(pluginParameters.getInputFolder()+directoryInput.getSeparator()+currentFile.getName());
            ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());
            // TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
            Segmented[0]=generateSegmentedImage(Segmented[0],1);
            Segmented[0] = BinaryImages.componentsLabeling(Segmented[0], 26,32);
            LabelImages.removeBorderLabels(Segmented[0]);
            Segmented[0]=generateSegmentedImage(Segmented[0],1);
            Histogram histogram = new Histogram ();
            histogram.run(Segmented[0]);
            if (histogram.getNbLabels() > 0) {
                Measure3D mesure3D = new Measure3D(Segmented, Raw, pluginParameters.getXcalibration(Raw), pluginParameters.getYcalibration(Raw), pluginParameters.getZcalibration(Raw));
                outputCropGeneralInfoOTSU += mesure3D.nucleusParameter3D() + "\n";
            }
        }

        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( outputCropGeneralInfoOTSU);

    }


    public static ImagePlus generateSegmentedImage (ImagePlus imagePlusInput,
                                                    int threshold)  {
        ImageStack imageStackInput = imagePlusInput.getStack();
        ImagePlus imagePlusSegmented = imagePlusInput.duplicate();

        imagePlusSegmented.setTitle(imagePlusInput.getTitle());
        ImageStack imageStackSegmented = imagePlusSegmented.getStack();
        for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
            for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
                for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
                    double voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue > 1) {
                        imageStackSegmented.setVoxel(i, j, k, 255);
                        imageStackInput.getVoxel(i, j, k);
                    }
                    else {
                        imageStackSegmented.setVoxel(i, j, k, 0);
                    }
                }
            }
        }
        return imagePlusSegmented;

    }

    /**
     *
     * @return
     */

    public static String getColnameResult(){
        return "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\tMeanIntensity\tStandardDeviation\tMinIntensity\tMaxIntensity\n";
    }

    // UN DOSSIER AVEC LES IMAGETTES
    // UN DOSSIER AVEC LES COORDONNEES
    // UN DOSSIER AVEC LES ZPROJECTION

    public static void generateProjectionFromCoordinnates(String pathToGIFTSeg, String pathToZprojection,String pathToCoordonnate) throws IOException, FormatException,Exception {
        generateProjectionFromCoordonne projection =new generateProjectionFromCoordonne(pathToGIFTSeg, pathToZprojection, pathToCoordonnate);
        projection.run();
    }




    public static void sliceToStack(String pathToSliceDir, String pathToOutputDir) throws Exception {
        HashMap<String, Integer> test = new HashMap();


        Directory directoryOutput = new Directory(pathToOutputDir);
        Directory directoryInput = new Directory(pathToSliceDir);
        directoryInput.listImageFiles(pathToSliceDir);
        //Parcour de l'ensemble des images du dossier
        for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
            String tm = directoryInput.getFile(i).getName();
            tm = tm.substring(0, tm.lastIndexOf("_"));
            tm = tm.substring(0, tm.lastIndexOf("_"));
            if (test.get(tm) != null) {
                test.put(tm, test.get(tm) + 1);
            } else {
                test.put(tm, 1);
            }
        }

        for (Map.Entry<String, Integer> entry : test.entrySet()) {
            ImagePlus[] image = new ImagePlus[entry.getValue()];
            System.out.println("image :" + entry.getKey());
            for (short i = 0; i < image.length; ++i) {
                //image= BF.openImagePlus((directoryInput.m_dirPath
                image[i] = IJ.openImage((directoryInput.m_dirPath
                        + "/"
                        + entry.getKey()
                        + "_"
                        + i + "_MLprediction.tif"));
                IJ.run(image[i], "8-bit", "");
                //
            }
            ImagePlus imp3 = new Concatenator().concatenate(image, false);
            saveFile(imp3, directoryOutput.m_dirPath+directoryOutput.m_separator
                    + entry.getKey() + ".tif");
        }

    }
    public static void cropFromCoordinates(String coordonnateDir) throws IOException, FormatException,Exception {

        CropFromCoordonnate test = new CropFromCoordonnate(coordonnateDir);
        test.runCropFromCoordonnate();
    }

    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }

    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
        DebugTools.enableLogging("OFF");

        System.setProperty("java.awt.headless", "false");

        if(args[0].equals("autocrop")) {
            System.out.println("start "+args[0]);
            if((args.length==4)&& (args[3].equals("ConfigFile"))){
                runAutoCropFolder(args[1], args[2], args[4]);

            }
            else if((args.length==4)&& (args[3].equals("File"))){
                runAutoCropFile(args[1], args[2]);
            }
            else{
                runAutoCropFolder(args[1], args[2]);
            }

        }
        else if(args[0].equals("segmentation")) {
            System.out.println("start " + args[0]);
            if ((args.length == 4) && (args[3].equals("ConfigFile"))) {
                segmentationFolder(args[1], args[2], args[3]);

            } else if ((args.length == 4) && (args[3].equals("File"))) {

                //String input, String output, short vMin, int vMax, boolean gift
                segmentationOneImage(args[1], args[2]);
            } else {
                segmentationFolder(args[1], args[2]);

            }
        }
        else if(args[0].equals("computeParameters")){
            if ((args.length == 4) && (args[3].equals("ConfigFile"))) {
                computeNucleusParameters(args[1], args[2], args[3]);
            }
             else{
                    computeNucleusParameters(args[1], args[2]);
                }
        }
        else if(args[0].equals("computeParametersDL")){
            computeNucleusParametersDL(args[1], args[2]);
        }
        else if(args[0].equals("generateProjection")){
            generateProjectionFromCoordinnates(args[1], args[2],args[3]);
        }
        else if (args[0].equals("SliceToStack")){
            sliceToStack(args[1], args[2]);
        }
        else if(args[0].equals("CropFromCoordonnate")){
            cropFromCoordinates(args[1]);
        }
        else{
            System.out.println("Argument le premier argument doit être   autocrop  ou   segmentation ou computeParameters");
            System.out.println("\nExemples :");
            System.out.println("\njava NucleusJ_giftwrapping.jar autocrop dossier/raw/ dossier/out/");
            System.out.println("\njava NucleusJ_giftwrapping.jar segmentation dossier/raw/ dossier/out/");
            System.out.println("\n\n");
        }
        System.err.println("The program ended normally.");
    }
}

//IJ.log(""+getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber() +" image type " +imgSeg.getType()+"\n");

//long maxMemory = Runtime.getRuntime().maxMemory();
//System.out.println("Maximum memory (bytes): " +(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9));