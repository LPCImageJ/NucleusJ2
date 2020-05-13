package gred.nucleus.mains;
import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.annotAutoCrop;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.autocrop.AutoCropCalling;
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
//import org.apache.commons.cli.Options;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


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
            String log = otsuModif.runSeveralImages2();
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

    public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
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
     * MA BITE
     *
     * @param RawImageSourceFile
     * @param SegmentedImagesSourceFile
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */

    public static void computeNucleusParametersDL(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
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
    public static void generateProjectionFromCoordonne(String coordonnateDir,String tiffAssociatedDir) throws IOException, FormatException,Exception {
        Directory directoryCoordonne = new Directory(coordonnateDir);
        Directory directoryTIF = new Directory(tiffAssociatedDir);
        directoryCoordonne.listAllFiles(directoryCoordonne.get_dirPath());
        directoryTIF.listAllFiles(directoryTIF.get_dirPath());
        for (short i = 0; i < directoryCoordonne.getNumberFiles(); ++i) {
            File coordinateFile = directoryCoordonne.getFile(i);

            // TODO FAIRE UNE FONCTION POUR CHOPER LE FICHIER IMAGE DANS LE DIR PEUT IMPORTE L EXTENSION !
            File tifFile =directoryTIF.searchFileNameWithoutExention(coordinateFile.getName().split("\\.")[0]);
            if (tifFile !=null) {
                System.out.println("Dedand "+tifFile.getAbsolutePath());

                AutocropParameters autocropParameters= new AutocropParameters(tifFile.getParent(),tifFile.getParent());
                ArrayList<String> listOfBoxes =readCoordonnateTXT(coordinateFile);
                annotAutoCrop annotAutoCrop =new annotAutoCrop(listOfBoxes,tifFile,tifFile.getAbsolutePath(),autocropParameters);

                annotAutoCrop.run();
            }
        }

    }
    public static ArrayList<String> readCoordonnateTXT(File boxeFile) {

        ArrayList<String> boxLists = new ArrayList();
        try {
            Scanner scanner = new Scanner(boxeFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    String [] splitLine = line.split("\\t");
                    int xMax=Integer.valueOf(splitLine[3])+Integer.valueOf(splitLine[6]);
                    int yMax=Integer.valueOf(splitLine[4])+Integer.valueOf(splitLine[7]);
                    int zMax=Integer.valueOf(splitLine[5])+Integer.valueOf(splitLine[8]);
                    boxLists.add(splitLine[0]
                            + "\t" + splitLine[3]
                            + "\t" + xMax
                            + "\t" + splitLine[4]
                            + "\t" + yMax
                            + "\t" + splitLine[5]
                            + "\t" + zMax);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return boxLists;
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
            generateProjectionFromCoordonne(args[1], args[2]);
        }
        else if (args[0].equals("SliceToStack")){
            sliceToStack(args[1], args[2]);
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