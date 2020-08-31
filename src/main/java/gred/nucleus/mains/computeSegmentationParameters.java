package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTextFile;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import ij.ImageStack;
import loci.common.DebugTools;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class computeSegmentationParameters {

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
        OutputTextFile resultFileOutputOTSU=new OutputTextFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTextFile( outputCropGeneralInfoOTSU);

    }


    public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
        Directory directoryInput = new Directory(pluginParameters.getInputFolder());
        directoryInput.listImageFiles(pluginParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> rawImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < rawImages.size(); ++i) {
            File currentFile = rawImages.get(i);
            System.out.println("current File "+currentFile.getName());

            ImagePlus Raw = new ImagePlus(currentFile.getAbsolutePath());
            ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());
            Measure3D mesure3D = new Measure3D(Segmented,
                    Raw,
                    pluginParameters.getXcalibration(Raw),
                    pluginParameters.getYcalibration(Raw),
                    pluginParameters.getZcalibration(Raw));

            outputCropGeneralInfoOTSU+=mesure3D.nucleusParameter3D()+"\tNA"+"\n";
        }

        OutputTextFile resultFileOutputOTSU=new OutputTextFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTextFile( outputCropGeneralInfoOTSU);

    }

    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
        DebugTools.enableLogging("OFF");
        computeNucleusParameters(
                "/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW",
                        "/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/GIFT");
    }



    public static String getColnameResult(){
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
    public static int recomputeOTSU(ImagePlus _Raw , ImagePlus _Segmented){
        int OTSUthreshold= Integer.MAX_VALUE;
        ImageStack imageStackRaw = _Raw.getStack();
        ImageStack imageStackSeg = _Segmented.getStack();
        for(int k = 0; k < _Raw.getStackSize(); ++k) {
            for (int i = 0; i < _Raw.getWidth(); ++i) {
                for (int j = 0; j < _Raw.getHeight(); ++j) {
                    if((imageStackSeg.getVoxel(i, j, k)==255) && (OTSUthreshold>=imageStackRaw.getVoxel(i, j, k))){
                        OTSUthreshold=(int)(imageStackRaw.getVoxel(i, j,k));
                    }

                }
            }
        }
        return OTSUthreshold;

    }

}

