package gred.nucleus.segmentation;


import gred.nucleus.plugins.PluginParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


public class SegmentationParameters extends PluginParameters {
	/** GIFT wrapping option */
	boolean giftWrapping     = true;
	/** Minimal object volume to segment */
	int     minVolumeNucleus = 1;
	/** Maximal object volume to segment */
	int     maxVolumeNucleus = 3000000;
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder  Path folder containing Images
	 * @param outputFolder Path folder output analyse
	 */
	public SegmentationParameters(String inputFolder, String outputFolder) {
		super(inputFolder, outputFolder);
	}
	
	
	public SegmentationParameters(String inputFolder, String outputFolder, int minVolume, int maxVolume, boolean gift) {
		super(inputFolder, outputFolder);
		this.minVolumeNucleus = minVolume;
		this.maxVolumeNucleus = maxVolume;
		this.giftWrapping = gift;
		
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
		this.minVolumeNucleus = minVolume;
		this.maxVolumeNucleus = maxVolume;
		this.giftWrapping = gift;
		
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
				this.giftWrapping = Boolean.parseBoolean(prop.getProperty("GiftWrapping"));
			}
			if (idProp.equals("maxVolumeNucleus")) {
				this.maxVolumeNucleus = Integer.parseInt(prop.getProperty("maxVolumeNucleus"));
			}
			if (idProp.equals("minVolumeNucleus")) {
				this.minVolumeNucleus = Integer.parseInt(prop.getProperty("minVolumeNucleus"));
			}
		}
	}
	
	
	public String getAnalysisParameters() {
		super.getAnalysisParameters();
		this.headerInfo += "#maxVolumeNucleus:" + maxVolumeNucleus + "\n"
		                   + "#minVolumeNucleus: " + minVolumeNucleus + "\n"
		                   + "#GiftWrapping: " + giftWrapping + "\n";
		return this.headerInfo;
	}
	
	
	public int getMinVolumeNucleus() {
		return this.minVolumeNucleus;
	}
	
	
	public void setMinVolumeNucleus(int vMin) {
		this.minVolumeNucleus = vMin;
	}
	
	
	public int getMaxVolumeNucleus() {
		return this.maxVolumeNucleus;
	}
	
	
	public void setMaxVolumeNucleus(int vMax) {
		this.maxVolumeNucleus = vMax;
	}
	
	
	public boolean getGiftWrapping() {
		return this.giftWrapping;
	}
	
}
