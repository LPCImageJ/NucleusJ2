package gred.nucleus.mains;
import gred.nucleus.MachineLeaningUtils.SliceToStack;
import gred.nucleus.MachineLeaningUtils.ComputeNucleiParametersML;
import gred.nucleus.autocrop.*;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import loci.common.DebugTools;
import loci.formats.FormatException;
import java.io.IOException;



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
    // UN DOSSIER AVEC LES ZPROJECTION
    // UN DOSSIER AVEC LES COORDONNEES


    public static void generateProjectionFromCoordinnates(String pathToCoordonnate, String pathToRaw) throws IOException, FormatException,Exception {
        generateProjectionFromCoordonne projection =new generateProjectionFromCoordonne(pathToCoordonnate, pathToRaw);
        System.out.println("le run 2 :: ");
        projection.run2();
    }



    // UN DOSSIER AVEC LES IMAGETTES
    // UN DOSSIER AVEC LES ZPROJECTION
    // UN DOSSIER AVEC LES COORDONNEES


    public static void generateProjectionFromCoordinnates(String pathToGIFTSeg, String pathToZprojection,String pathToCoordonnate) throws IOException, FormatException,Exception {
        generateProjectionFromCoordonne projection =new generateProjectionFromCoordonne(pathToGIFTSeg, pathToZprojection, pathToCoordonnate);
        projection.run();
    }


    public static void sliceToStack(String pathToSliceDir, String pathToOutputDir) throws Exception {
        SliceToStack createStack =new SliceToStack(pathToSliceDir,pathToOutputDir);
        createStack.run();

        /*
        * Method to crop image with coordinate in tab file :
        *    tabulate file : pathToCoordinateFile pathToRawImageAssociate
        *
        */

    }
    public static void cropFromCoordinates(String coordonnateDir) throws Exception {

        CropFromCoordonnate test = new CropFromCoordonnate(coordonnateDir);
        test.runCropFromCoordonnate();
    }
    // DIC_path zprojection_path
    public static void genereOV(String linkOverlayProjection) throws Exception {

        GenerateOverlay ov = new GenerateOverlay(linkOverlayProjection);
        ov.run();

    }
    public static void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }

    public static void main(String[] args) throws Exception {
        // SET OFF BIOFORMATS WARNINGS
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
            if(args.length==4) {
                generateProjectionFromCoordinnates(args[1], args[2], args[3]);
            }
            else{
                generateProjectionFromCoordinnates(args[1], args[2]);
            }
        }
        else if (args[0].equals("SliceToStack")){
            sliceToStack(args[1], args[2]);
        }
        else if(args[0].equals("CropFromCoordonnate")){
            cropFromCoordinates(args[1]);
        }
        else if(args[0].equals("GenerateOverlay")){
            genereOV(args[1]);
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