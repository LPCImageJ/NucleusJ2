package gred.nucleus.mains;


import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;



public class SegmentationParameters extends PluginParameters {
    /** GIFT wrapping option */
    boolean m_giftVrapping=true;
    /** Minimal object volume to segment */
    int m_minVolumeNucleus=1;
    /** Maximal object volume to segment */
    int m_maxVolumeNucleus=3000000;


    /** Constructor with default parameter
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     *
     */
    public SegmentationParameters(String inputFolder, String outputFolder){
        super(inputFolder, outputFolder);
    }

    public SegmentationParameters(String inputFolder, String outputFolder,int minVolume,int maxVolume,boolean gift){
        super(inputFolder, outputFolder);
        this.m_minVolumeNucleus=minVolume;
        this.m_maxVolumeNucleus=maxVolume;
        this.m_giftVrapping=gift;

    }
    public SegmentationParameters (String inputFolder, String outputFolder, String pathToConfigFile){
        super(inputFolder, outputFolder,pathToConfigFile);
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
        for (String idProp :prop.stringPropertyNames()){
            if(idProp.equals("GiftWrapping")){this.m_giftVrapping = Boolean.valueOf(prop.getProperty("GiftWrapping"));}
            if(idProp.equals("maxVolumeNucleus")){this.m_maxVolumeNucleus = Integer.valueOf(prop.getProperty("maxVolumeNucleus"));}
            if(idProp.equals("minVolumeNucleus")){this.m_minVolumeNucleus = Integer.valueOf(prop.getProperty("minVolumeNucleus"));}

        }
    }

    public void setMinVolumeNucleus(int vMin){
        this.m_minVolumeNucleus=vMin;
    }

    public void setMaxVolumeNucleus(int vMax){
        this.m_maxVolumeNucleus=vMax;
    }



    public String getAnalyseParameters() {
        super.getAnalyseParameters();
        this.m_headerInfo+="#maxVolumeNucleus:"+getM_maxVolumeNucleus()+"\n"
                +"#minVolumeNucleus: "+getM_minVolumeNucleus()+"\n"
                +"#GiftWrapping: "+getGiftWrapping()+"\n";
        return this.m_headerInfo;
    }
    public int getM_minVolumeNucleus(){return this.m_minVolumeNucleus;}
    public int getM_maxVolumeNucleus(){return  this.m_maxVolumeNucleus;}
    public boolean getGiftWrapping(){return  this.m_giftVrapping;}

}
