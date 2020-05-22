package gred.nucleus.core;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ComputeNucleiParameters {

    PluginParameters m_pluginParameters;
    String m_rawImagesInputDirectory;
    String m_segmentedImagesDirectory;


    /**
     * Contructor
     * @param rawImagesInputDirectory path to raw images
     * @param segmentedImagesDirectory path to segmented images associated
     * @param pathToConfig path to config file
     */
   public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory, String pathToConfig){
       this.m_pluginParameters = new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory,pathToConfig);
       this.m_rawImagesInputDirectory=rawImagesInputDirectory;
       this.m_segmentedImagesDirectory=segmentedImagesDirectory;

    }

    /**
     *
     * @param rawImagesInputDirectory path to raw images
     * @param segmentedImagesDirectory path to segmented images associated
     */
    public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory ){
        this.m_pluginParameters = new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory);
        this.m_rawImagesInputDirectory=rawImagesInputDirectory;
        this.m_segmentedImagesDirectory=segmentedImagesDirectory;

    }

    /**
     * Compute nuclei parameters generate from segmentation (OTSU / GIFT)
     * Useful if parallel segmentation was use to get results parameter in the same folder.
     * @throws Exception
     */
    public void run() throws Exception{
        Directory directoryInput = new Directory(this.m_pluginParameters.getInputFolder());
        directoryInput.listImageFiles(this.m_pluginParameters.getInputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> rawImages =directoryInput.m_listeOfFiles;
        String outputCropGeneralInfoOTSU=this.m_pluginParameters.getAnalyseParameters()+getColnameResult();
        for (short i = 0; i < rawImages.size(); ++i) {
            File currentFile = rawImages.get(i);
            ImagePlus Raw = new ImagePlus(currentFile.getAbsolutePath());
            ImagePlus[] Segmented = BF.openImagePlus(this.m_pluginParameters.getOutputFolder()+currentFile.getName());


            Measure3D mesure3D = new Measure3D(Segmented, Raw, this.m_pluginParameters.getXcalibration(Raw), this.m_pluginParameters.getYcalibration(Raw),this.m_pluginParameters.getZcalibration(Raw));
            outputCropGeneralInfoOTSU+=mesure3D.nucleusParameter3D()+"\n";
        }
        OutputTexteFile resultFileOutputOTSU=new OutputTexteFile(this.m_pluginParameters.getOutputFolder()
                +directoryInput.getSeparator()
                +"result_Segmentation_Analyse.csv");
        resultFileOutputOTSU.SaveTexteFile( outputCropGeneralInfoOTSU);

    }
    /**
     *
     * @return columns names for results
     */
    public static String getColnameResult(){
        return "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected\tMeanIntensity\tStandardDeviation\tMinIntensity\tMaxIntensity\n";
    }

}
