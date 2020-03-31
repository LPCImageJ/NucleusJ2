package gred.nucleus.mains;
import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.segmentation.SegmentationCalling;

import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.plugins.BF;
//import org.apache.commons.cli.Options;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;



public class main {


    static ArrayList <String> m_test;

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
     * @param imageSourceFile : path to the image's folder
     * @param output : path to output folder analysis
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCropFolder(String imageSourceFile, String output) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }

    /**
     * Method to run autocrop with input folder, output folder and with config file analysis:
     * @param imageSourceFile :: path to the image's folder
     * @param output : path to output folder analysis
     * @param pathToConfig : path to config file
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCropFolder(String imageSourceFile, String output, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }

    public static void runAutoCropFile(String imageSourceFile, String output) throws IOException , fileInOut,Exception{
        //AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFile(imageSourceFile);
    }



    //========================= Segmentation calling ===========================================


    public static void segmentationFolder(String input, String output ) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFolder(String input, String output ,String config) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output,config);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }


    public static void segmentationOneImage(String input, String output) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input,output);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runOneImage(input);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }


    public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile,pathToConfig);
        Directory directoryInput = new Directory(pluginParameters.getInputFolder());
        directoryInput.listFiles(pluginParameters.getInputFolder());
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


    public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
        Directory directoryInput = new Directory(pluginParameters.getOutputFolder());
        directoryInput.listFiles(pluginParameters.getOutputFolder());
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
    public static String getColnameResult(){
        return "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\tMeanIntensity\tStandardDeviation\tMinIntensity\tMaxIntensity\n";
    }




    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
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