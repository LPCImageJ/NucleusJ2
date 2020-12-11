package gred.nucleus.core;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import ij.measure.Calibration;
import loci.plugins.BF;

import java.io.File;
import java.util.ArrayList;

public class ComputeNucleiParameters {

    private  PluginParameters m_pluginParameters;

    /**
     * Constructor with input, output and config files
     * @param rawImagesInputDirectory path to raw images
     * @param segmentedImagesDirectory path to segmented images associated
     * @param pathToConfig path to config file
     */
   public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory, String pathToConfig){
       this.m_pluginParameters = new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory,pathToConfig);


    }

    /**
     * Constructor with input and output files
     * @param rawImagesInputDirectory path to raw images
     * @param segmentedImagesDirectory path to segmented images associated
     */
    public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory ){
        this.m_pluginParameters = new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory);


    }

    /**
     * Constructor with input, output files and calibration from dialog.
     * @param rawImagesInputDirectory path to raw images
     * @param segmentedImagesDirectory path to segmented images associated
     * @param cal calibration from dialog
     */
    public ComputeNucleiParameters(String rawImagesInputDirectory, String segmentedImagesDirectory ,
                                   Calibration cal){
        this.m_pluginParameters = new PluginParameters(rawImagesInputDirectory,segmentedImagesDirectory,
        cal.pixelWidth,cal.pixelHeight,cal.pixelDepth);

    }



    /**
     * Compute nuclei parameters generate from segmentation ( OTSU / GIFT)
     * Useful if parallel segmentation was use to get results parameter in the same folder.
     * @throws Exception : file exceptions
     */
    public void run() throws Exception{
        Directory directoryRawInput = new Directory(this.m_pluginParameters.getInputFolder());
        directoryRawInput.listImageFiles(this.m_pluginParameters.getInputFolder());
        directoryRawInput.checkIfEmpty();
        Directory directorySegmentedInput = new Directory(this.m_pluginParameters.getOutputFolder());
        directorySegmentedInput.listImageFiles(this.m_pluginParameters.getOutputFolder());
        directorySegmentedInput.checkIfEmpty();
        ArrayList<File> segmentedImages =directorySegmentedInput.m_listeOfFiles;
        StringBuilder outputCropGeneralInfoOTSU = new StringBuilder();

         outputCropGeneralInfoOTSU.append(this.m_pluginParameters.getAnalyseParameters()).append(getColNameResult());

        for (File f : segmentedImages) {
            ImagePlus Raw = new ImagePlus(this.m_pluginParameters.getInputFolder()+File.separator+f.getName());
            try {
                ImagePlus[] Segmented = BF.openImagePlus(f.getAbsolutePath());

                Measure3D measure3D = new Measure3D(Segmented, Raw, this.m_pluginParameters.getXcalibration(Raw), this.m_pluginParameters.getYcalibration(Raw), this.m_pluginParameters.getZcalibration(Raw));
                outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }
        OutputTextFile resultFileOutputOTSU=new OutputTextFile(
                this.m_pluginParameters.getOutputFolder()
                +directoryRawInput.getSeparator()
                +"result_Segmentation_Analyse.csv");

        resultFileOutputOTSU.SaveTextFile(outputCropGeneralInfoOTSU.toString());



    }

    public void addConfigParameters(String pathToconfig) throws Exception{
        this.m_pluginParameters.addGeneralProperties(pathToconfig);

    }

    /**
     *
     * @return columns names for results
     */
    private String getColNameResult(){
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

}
