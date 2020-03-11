package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.core.Measure3D;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.plugins.BF;

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

    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {

        computeNucleusParameters("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_MICROSCOPE_02-2020/AutoCrop/Output/Segmented/RAW/",
                "/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_MICROSCOPE_02-2020/AutoCrop/Output/Segmented/GIFT/"
                ,"/media/tridubos/DATA1/DATA_ANALYSE/WARIO_SEG_TRAINING/config_1_1_1");

/**
computeNucleusParameters("/media/tridubos/DATA1/DATA_ANALYSE/WARIO_SEG_TRAINING/RAW",
                "/media/tridubos/DATA1/DATA_ANALYSE/WARIO_SEG_TRAINING/STACK_RECONSTITUEES"
                ,"/media/tridubos/DATA1/DATA_ANALYSE/WARIO_SEG_TRAINING/config_1_1_1");
*/
    }
    /**
    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {

        computeNucleusParameters("/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/test_calib_segmentation/Raw",
                "/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/test_calib_segmentation/Segmented/GIFT"
                ,"/media/tridubos/DATA1/DATA_ANALYSE/ANALYSE_BILLES_11-2019/test_calib_segmentation/Segmented/config_1_1_1");

    }
        */


    public static String getColnameResult(){
        return "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\tMeanIntensity\tStandardDeviation\tMinIntensity\tMaxIntensity\n";
    }
}

