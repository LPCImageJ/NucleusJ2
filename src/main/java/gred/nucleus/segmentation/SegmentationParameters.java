package gred.nucleus.segmentation;


import gred.nucleus.plugins.PluginParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SegmentationParameters extends PluginParameters {
	/** GIFT wrapping option */
	boolean m_giftWrapping     = true;
	/** Minimal object volume to segment */
	int     m_minVolumeNucleus = 1;
	/** Maximal object volume to segment */
	int     m_maxVolumeNucleus = 3000000;
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder    Path folder containing Images
	 * @param outputFolder   Path folder output analyse
	 */
	public SegmentationParameters(String inputFolder, String outputFolder) {
		super(inputFolder, outputFolder);
	}
	
	
	public SegmentationParameters(String inputFolder, String outputFolder, int minVolume, int maxVolume, boolean gift) {
		super(inputFolder, outputFolder);
		this.m_minVolumeNucleus = minVolume;
		this.m_maxVolumeNucleus = maxVolume;
		this.m_giftWrapping = gift;
		
	}
	
	
	public SegmentationParameters(String inputFolder,
	                              String outputFolder,
	                              int xCal,
	                              int yCal,
	                              int zCal,
	                              int minVolume,
	                              int maxVolume,
	                              boolean gift) {
		super(inputFolder, outputFolder, xCal, yCal, zCal);
		this.m_minVolumeNucleus = minVolume;
		this.m_maxVolumeNucleus = maxVolume;
		this.m_giftWrapping = gift;
		
	}
	
	
	public SegmentationParameters(String inputFolder, String outputFolder, String pathToConfigFile) {
		super(inputFolder, outputFolder, pathToConfigFile);
		addProperties(pathToConfigFile);
	}
	
	
	public void addProperties(String pathToConfigFile) {
		Properties  prop = new Properties();
		InputStream is   = null;
		try {
			is = new FileInputStream(pathToConfigFile);
		} catch (FileNotFoundException ex) {
			System.err.println(pathToConfigFile + " : can't find the config file !");
			System.exit(-1);
		}
		try {
			prop.load(is);
		} catch (IOException ex) {
			System.err.println(pathToConfigFile + " : can't load the config file !");
			System.exit(-1);
		}
		for (String idProp : prop.stringPropertyNames()) {
			if (idProp.equals("GiftWrapping")) {
				this.m_giftWrapping = Boolean.parseBoolean(prop.getProperty("GiftWrapping"));
			}
			if (idProp.equals("maxVolumeNucleus")) {
				this.m_maxVolumeNucleus = Integer.parseInt(prop.getProperty("maxVolumeNucleus"));
			}
			if (idProp.equals("minVolumeNucleus")) {
				this.m_minVolumeNucleus = Integer.parseInt(prop.getProperty("minVolumeNucleus"));
			}
		}
	}
	
	
	public void setMinVolumeNucleus(int vMin) {
		this.m_minVolumeNucleus = vMin;
	}
	
	
	public void setMaxVolumeNucleus(int vMax) {
		this.m_maxVolumeNucleus = vMax;
	}
	
	
	public String getAnalyseParameters() {
		super.getAnalyseParameters();
		this.m_headerInfo += "#maxVolumeNucleus:" + m_maxVolumeNucleus + "\n"
		                     + "#minVolumeNucleus: " + m_minVolumeNucleus + "\n"
		                     + "#GiftWrapping: " + m_giftWrapping + "\n";
		return this.m_headerInfo;
	}
	
	
	public int getMinVolumeNucleus() {
		return this.m_minVolumeNucleus;
	}
	
	
	public int getMaxVolumeNucleus() {
		return this.m_maxVolumeNucleus;
	}
	
	
	public boolean getGiftWrapping() {
		return this.m_giftWrapping;
	}
	
}
