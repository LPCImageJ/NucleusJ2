package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.MachineLeaningUtils.SliceToStack;
import gred.nucleus.MachineLeaningUtils.ComputeNucleiParametersML;
import gred.nucleus.autocrop.*;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.segmentation.SegmentationCalling;

import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import loci.common.DebugTools;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.gredclermont.omero.Client;
import fr.gredclermont.omero.ImageContainer;
import fr.gredclermont.omero.repository.DatasetContainer;
import fr.gredclermont.omero.repository.ProjectContainer;


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

    public static void runAutoCropOmero(String inputDirectory, String outputDirectory, Client client) throws Exception {
        AutocropParameters autocropParameters = new AutocropParameters(".", ".");
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);

        String[] param = inputDirectory.split("/");

        if(param.length >= 2) {
            if(param[0].equals("Image")) {
                Long id = Long.parseLong(param[1]);
                ImageContainer image = client.getImage(id);

                autoCrop.runImageOmero(image, Long.parseLong(outputDirectory), client);
            }
            else {
                Long id = Long.parseLong(param[1]);
                List<ImageContainer> images = null; 

                if(param[0].equals("Dataset")) {
                    DatasetContainer dataset = client.getDataset(id);

                    if(param.length == 4 && param[2].equals("Tag")) {
                        images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
                    }
                    else {
                        images = dataset.getImages(client);
                    }
                }
                else if(param[0].equals("Project")) {
                    ProjectContainer project = client.getProject(id);

                    if(param.length == 4 && param[2].equals("Tag")) {
                        images = project.getImagesTagged(client, Long.parseLong(param[3]));
                    }
                    else {
                        images = project.getImages(client);
                    }
                }
                else if(param[0].equals("Tag")) {
                    images = client.getImagesTagged(id);
                }
                else {
                    throw new IllegalArgumentException();
                }

                autoCrop.runSeveralImageOmero(images, Long.parseLong(outputDirectory), client);
            }   
        }
        else {
            throw new IllegalArgumentException();
        }

        //autoCrop.runFileOmero();
    }



    //========================= Segmentation calling ===========================================

    /**
     * Method to run segmentation with input folder, output folder :
     * @param inputDirectory path to the raw image's folder
     * @param outputDirectory path to output folder analysis
     * @throws Exception
     */

    public static void segmentationFolder(String inputDirectory, String outputDirectory ) throws Exception {
        System.out.println("test " + inputDirectory);
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

    public static void segmentationOmero(String inputDirectory, String outputDirectory, Client client)  throws Exception
    {
        SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".");
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);

        String[] param = inputDirectory.split("/");

        if(param.length >= 2) {
            if(param[0].equals("Image")) {
                Long id = Long.parseLong(param[1]);
                ImageContainer image = client.getImage(id);

                try {
                    String log;
                    if(param.length == 3 && param[2].equals("ROI")) {
                        log = otsuModif.runOneImageOmero(image, Long.parseLong(outputDirectory), client);
                    }
                    else {
                        log = otsuModif.runOneImageOmeroROI(image, Long.parseLong(outputDirectory), client);
                    }
                    if(!(log.equals("")))
                        System.out.println("Nuclei which didn't pass the segmentation\n"+log);
                }catch (IOException e) { e.printStackTrace();}
            }
            else {
                Long id = Long.parseLong(param[1]);
                List<ImageContainer> images = null; 

                if(param[0].equals("Dataset")) {
                    DatasetContainer dataset = client.getDataset(id);

                    if(param.length == 4 && param[2].equals("Tag")) {
                        images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
                    }
                    else {
                        images = dataset.getImages(client);
                    }
                }
                else if(param[0].equals("Project")) {
                    ProjectContainer project = client.getProject(id);

                    if(param.length == 4 && param[2].equals("Tag")) {
                        images = project.getImagesTagged(client, Long.parseLong(param[3]));
                    }
                    else {
                        images = project.getImages(client);
                    }
                }
                else if(param[0].equals("Tag")) {
                    images = client.getImagesTagged(id);
                }
                else {
                    throw new IllegalArgumentException();
                }
                try {
                    String log;
                    if ((param.length == 3 && param[2].equals("ROI")) || (param.length == 5 && param[4].equals("ROI"))) {
                        log = otsuModif.runSeveralImageOmeroROI(images, Long.parseLong(outputDirectory), client);
                    }   
                    else {
                        log = otsuModif.runSeveralImageOmero(images, Long.parseLong(outputDirectory), client);
                    }   
                    if(!(log.equals("")))
                        System.out.println("Nuclei which didn't pass the segmentation\n"+log);
                }catch (IOException e) { e.printStackTrace();}
            }
        }
        else
        {
            throw new IllegalArgumentException();
        }
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

        public static void computeNucleusParameters(String rawImagesInputDirectory, String segmentedImagesDirectory,String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
            ComputeNucleiParameters generateParameters =new ComputeNucleiParameters(rawImagesInputDirectory, segmentedImagesDirectory,pathToConfig);
            generateParameters.run();
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
        ComputeNucleiParameters generateParameters =new ComputeNucleiParameters(rawImagesInputDirectory, segmentedImagesDirectory);
        generateParameters.run();
    }

    // TODO AJOUTER computeNucleusParametersDL avec configFILE FACTORISABLE AVEC computeNucleusParametersCONFINGFILE



    public static void computeNucleusParametersDL(String rawImagesInputDirectory, String segmentedImagesDirectory) throws IOException, FormatException ,fileInOut,Exception{
        ComputeNucleiParametersML computeParameters = new ComputeNucleiParametersML(rawImagesInputDirectory,  segmentedImagesDirectory);
        computeParameters.run();
    }






    // UN DOSSIER AVEC LES IMAGETTES
    // UN DOSSIER AVEC LES COORDONNEES
    // UN DOSSIER AVEC LES ZPROJECTION

    public static void generateProjectionFromCoordinnates(String pathToGIFTSeg, String pathToZprojection,String pathToCoordonnate) throws IOException, FormatException,Exception {
        generateProjectionFromCoordonne projection =new generateProjectionFromCoordonne(pathToGIFTSeg, pathToZprojection, pathToCoordonnate);
        projection.run();
    }




    public static void sliceToStack(String pathToSliceDir, String pathToOutputDir) throws Exception {
        SliceToStack createStack =new SliceToStack(pathToSliceDir,pathToOutputDir);
        createStack.run();


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

            } else if((args.length==4)&& (args[3].equals("File"))){
                runAutoCropFile(args[1], args[2]);
            } else if(args.length==7) {
                Client client = new Client();
                client.connect(args[3], Integer.parseInt(args[4]), args[5], args[6]);
                runAutoCropOmero(args[1], args[2], client);
            } else if(args.length==8) {
                Client client = new Client();
                client.connect(args[3], Integer.parseInt(args[4]), args[5], args[6], Long.parseLong(args[7]));
                runAutoCropOmero(args[1], args[2], client);
            } else {
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
            } else if(args.length==7) {
                Client client = new Client();
                client.connect(args[3], Integer.parseInt(args[4]), args[5], args[6]);
                segmentationOmero(args[1], args[2], client);
            } else if(args.length==8) {
                Client client = new Client();
                client.connect(args[3], Integer.parseInt(args[4]), args[5], args[6], Long.parseLong(args[7]));
                segmentationOmero(args[1], args[2], client);
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
        
        System.out.println("The program ended normally.");
    }
}

//IJ.log(""+getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber() +" image type " +imgSeg.getType()+"\n");

//long maxMemory = Runtime.getRuntime().maxMemory();
//System.out.println("Maximum memory (bytes): " +(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9));