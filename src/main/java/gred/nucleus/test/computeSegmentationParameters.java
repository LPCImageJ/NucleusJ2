package gred.nucleus.test;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import loci.formats.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class computeSegmentationParameters {

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
            ImagePlus Segmented = new ImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());

            Measure3D mesure3D = new Measure3D(Segmented, Raw, pluginParameters.getXCal(), pluginParameters.getYCal(),pluginParameters.getZCal());
            outputCropGeneralInfoOTSU=mesure3D.nucleusParameter3D();
        }


    }
    public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
        Directory directoryInput = new Directory(pluginParameters.getInputFolder());
        directoryInput.listFiles(pluginParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> rawImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < rawImages.size(); ++i) {
            File currentFile = rawImages.get(i);
            System.out.println();
            ImagePlus Raw = new ImagePlus(currentFile.getAbsolutePath());
            ImagePlus Segmented = new ImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());

            Measure3D mesure3D = new Measure3D(Segmented, Raw, pluginParameters.getXcalibration(Raw), pluginParameters.getYcalibration(Raw),pluginParameters.getZcalibration(Raw));
            outputCropGeneralInfoOTSU+=mesure3D.nucleusParameter3D();
        }

        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( outputCropGeneralInfoOTSU);

    }


    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {

        computeNucleusParameters("/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/TEST_CALCULPARAMETERS/RAW",
                "/media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/TEST_CALCULPARAMETERS/SEGED");
                //,"");
    }


    public static String getColnameResult(){
        return "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\tMeanIntensity\tStandardDeviation\tMinIntensity\tMaxIntensity\n";
    }
}

