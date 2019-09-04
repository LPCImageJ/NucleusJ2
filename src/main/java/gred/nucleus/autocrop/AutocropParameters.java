package gred.nucleus.autocrop;

import gred.nucleus.plugins.PluginParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AutocropParameters extends PluginParameters {
    /**
     * Number of pixels take plus object size in x
     */
    private int m_xCropBoxSize = 40;
    /**
     * Number of pixels take plus object size in y
     */
    private int m_yCropBoxSize = 40;
    /**
     * Number of slice take plus object in y
     */
    private int m_zCropBoxSize = 20;

    /**
     * Minimal object volume to crop
     */
    int m_minVolumeNucleus=1;
    /**
     * Maximal object volume to crop
     */
    int m_maxVolumeNucleus=2147483647;

    /**
     * Minimal default OTSU threshold
     */
    private int m_thresholdOTSUcomputing = 20;
    /**
     * Channel to compute OTSU threshold
     */
    private int m_channelToComputeThreshold=0;
    /**
     * Slice start to compute OTSU threshold
     */
    private int m_slicesOTSUcomputing=0;


    /** Constructor with default parameter
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     *
     */
    public AutocropParameters(String inputFolder, String outputFolder){
        super(inputFolder, outputFolder);

    }
    /** Constructor with box size modifications
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     * @param xCropBoxSize : number of voxels add in x axis around object
     * @param yCropBoxSize : number of voxels add in z axis around object
     * @param zCropBoxSize : number of stack add in z axis around object
     */
    public AutocropParameters(String inputFolder, String outputFolder, int xCropBoxSize, int yCropBoxSize, int zCropBoxSize
            , int thresholdOTSUcomputing,int channelToComputeThreshold) {
        super(inputFolder, outputFolder);
        this.m_xCropBoxSize = xCropBoxSize;
        this.m_yCropBoxSize = yCropBoxSize;
        this.m_zCropBoxSize = zCropBoxSize;
        this.m_thresholdOTSUcomputing = thresholdOTSUcomputing;
        this.m_channelToComputeThreshold=channelToComputeThreshold;
    }
    public AutocropParameters(String inputFolder, String outputFolder,
                              int xCropBoxSize, int yCropBoxSize, int zCropBoxSize,
                              int slicesOTSUcomputing,int thresholdOTSUcomputing,int channelToComputeThreshold,
                              int minVolumeNucleus, int maxVolumeNucleus) {

        super(inputFolder, outputFolder);
        this.m_xCropBoxSize = xCropBoxSize;
        this.m_yCropBoxSize = yCropBoxSize;
        this.m_zCropBoxSize = zCropBoxSize;
        this.m_thresholdOTSUcomputing = thresholdOTSUcomputing;
        this.m_slicesOTSUcomputing = slicesOTSUcomputing;
        this.m_channelToComputeThreshold=channelToComputeThreshold;
        this.m_maxVolumeNucleus=maxVolumeNucleus;
        this.m_minVolumeNucleus=minVolumeNucleus;

    }
    /**
     * Constructor with box size modification and slice number used to start OTSU threshold calculation
     * to last slice
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     * @param xCropBoxSize : number of voxels add in x axis around object
     * @param yCropBoxSize : number of voxels add in z axis around object
     * @param zCropBoxSize : number of stack add in z axis around object
     * @param slicesOTSUcomputing : slice from OTSU threshold calculation start
     *
     */
    public AutocropParameters(String inputFolder, String outputFolder, double xCal, double yCal, double zCal,
                              int xCropBoxSize, int yCropBoxSize, int zCropBoxSize,
                              int slicesOTSUcomputing,int thresholdOTSUcomputing,int channelToComputeThreshold,
                              int minVolumeNucleus, int maxVolumeNucleus) {

        super(inputFolder, outputFolder, xCal, yCal, zCal);
        this.m_xCropBoxSize = xCropBoxSize;
        this.m_yCropBoxSize = yCropBoxSize;
        this.m_zCropBoxSize = zCropBoxSize;
        this.m_thresholdOTSUcomputing = thresholdOTSUcomputing;
        this.m_slicesOTSUcomputing = slicesOTSUcomputing;
        this.m_channelToComputeThreshold=channelToComputeThreshold;
        this.m_maxVolumeNucleus=maxVolumeNucleus;
        this.m_minVolumeNucleus=minVolumeNucleus;

    }
    public AutocropParameters (String inputFolder, String outputFolder, String pathToConfigFile){
        super(inputFolder, outputFolder);
        Properties prop = new Properties();
        String fileName = pathToConfigFile;
        InputStream is = null;
        try {
            is = new FileInputStream(fileName);
        } catch (FileNotFoundException ex) {

        }
        try {
            prop.load(is);
        } catch (IOException ex) {

        }
        if(!(prop.getProperty("xCropBoxSize").isEmpty())){
            this.m_xCropBoxSize = Integer.valueOf(prop.getProperty("xCropBoxSize"));
        }
        if(!(prop.getProperty("yCropBoxSize").isEmpty())){
            this.m_xCropBoxSize = Integer.valueOf(prop.getProperty("yCropBoxSize"));
        }
        if(!(prop.getProperty("zCropBoxSize").isEmpty())){
            this.m_xCropBoxSize = Integer.valueOf(prop.getProperty("zCropBoxSize"));
        }
        if(!(prop.getProperty("thresholdOTSUcomputing").isEmpty())){
            this.m_thresholdOTSUcomputing = Integer.valueOf(prop.getProperty("thresholdOTSUcomputing"));
        }
        if(!(prop.getProperty("slicesOTSUcomputing").isEmpty())){
            this.m_slicesOTSUcomputing = Integer.valueOf(prop.getProperty("slicesOTSUcomputing"));
        }
        if(!(prop.getProperty("channelToComputeThreshold").isEmpty())){
            this.m_channelToComputeThreshold = Integer.valueOf(prop.getProperty("channelToComputeThreshold"));
        }
        if(!(prop.getProperty("maxVolumeNucleus").isEmpty())){
            this.m_maxVolumeNucleus = Integer.valueOf(prop.getProperty("maxVolumeNucleus"));
        }
        if(!(prop.getProperty("minVolumeNucleus").isEmpty())){
            this.m_minVolumeNucleus = Integer.valueOf(prop.getProperty("minVolumeNucleus"));

        }


    }


    /**
     * Getter : Herder with analyse parameters image x y z calibration
     * @return output path folder
     */


    public int getxCropBoxSize(){
        return this.m_xCropBoxSize;
    }
    public int getyCropBoxSize(){
        return this.m_yCropBoxSize;
    }
    public int getzCropBoxSize(){
        return this.m_zCropBoxSize;
    }


    public int getThresholdOTSUcomputing(){return this.m_thresholdOTSUcomputing;}
    public int getChannelToComputeThreshold(){return this.m_channelToComputeThreshold;}
    public int getM_minVolumeNucleus(){return this.m_minVolumeNucleus;}
    public int getM_maxVolumeNucleus(){return  this.m_maxVolumeNucleus;}


    public int getSlicesOTSUcomputing(){
        return this.m_slicesOTSUcomputing;
    }

    public String getThresholdOTSUcomputingParameter(){
        if(this.m_slicesOTSUcomputing==0){
            return "all stack";
        }else {
            return "" + this.m_slicesOTSUcomputing;
        }
    }
    public String getAnalyseParameters() {
        super.getAnalyseParameters();
        this.m_headerInfo+="#X box size: "+getxCropBoxSize()+"\n"
                +"#Y box size: "+getyCropBoxSize()+"\n"
                +"#Z box size: "+getzCropBoxSize()+"\n";
        return this.m_headerInfo;
    }


}
