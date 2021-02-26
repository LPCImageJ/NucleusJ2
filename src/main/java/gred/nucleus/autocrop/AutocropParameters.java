package gred.nucleus.autocrop;

import gred.nucleus.plugins.PluginParameters;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/** This class extend plugin parameters and contain the list of specific parameters available for Autocrop function. */
public class AutocropParameters extends PluginParameters {
	/** Minimal object volume to crop */
	int m_minVolumeNucleus = 1;
	/** Maximal object volume to crop */
	int m_maxVolumeNucleus = 2147483647;
	/** Number of pixels take plus object size in x */
	private int     m_xCropBoxSize                = 40;
	/** Number of pixels take plus object size in y */
	private int     m_yCropBoxSize                = 40;
	/** Number of slice take plus object in y */
	private int     m_zCropBoxSize                = 20;
	/** Minimal default OTSU threshold */
	private int     m_thresholdOTSUComputing      = 20;
	/** Channel to compute OTSU threshold */
	private int     m_channelToComputeThreshold   = 0;
	/** Slice start to compute OTSU threshold */
	private int     m_slicesOTSUComputing         = 0;
	/** Surface percent of boxes to groups them */
	private int     m_boxesPercentSurfaceToFilter = 50;
	/** Activation of boxes regrouping */
	private boolean m_boxesRegrouping             = true;
	
	
	public AutocropParameters() {
	}
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder    Path folder containing Images
	 * @param outputFolder   Path folder output analyse
	 */
	public AutocropParameters(String inputFolder, String outputFolder) {
		super(inputFolder, outputFolder);
	}
	
	
	/**
	 * Constructor with box size modifications
	 *
	 * @param inputFolder    Path folder containing Images
	 * @param outputFolder   Path folder output analyse
	 * @param xCropBoxSize   Number of voxels add in x axis around object
	 * @param yCropBoxSize   Number of voxels add in z axis around object
	 * @param zCropBoxSize   Number of stack add in z axis around object
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold) {
		super(inputFolder, outputFolder);
		this.m_xCropBoxSize = xCropBoxSize;
		this.m_yCropBoxSize = yCropBoxSize;
		this.m_zCropBoxSize = zCropBoxSize;
		this.m_thresholdOTSUComputing = thresholdOTSUComputing;
		this.m_channelToComputeThreshold = channelToComputeThreshold;
	}
	
	
	/**
	 * Constructor with all manual parameters
	 *
	 * @param inputFolder                 Path folder containing Images
	 * @param outputFolder                Path folder output analyse
	 * @param xCropBoxSize                Number of voxels add in x axis around object
	 * @param yCropBoxSize                Number of voxels add in z axis around object
	 * @param zCropBoxSize                Number of stack add in z axis around object
	 * @param channelToComputeThreshold   Channel number to compute OTSU
	 * @param slicesOTSUComputing         Slice start to compute OTSU
	 * @param thresholdOTSUComputing      Minimum OTSU threshold used
	 * @param maxVolumeNucleus            Volume maximum of objects detected
	 * @param minVolumeNucleus            Volume minimum of objects detected
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus) {
		
		super(inputFolder, outputFolder);
		this.m_xCropBoxSize = xCropBoxSize;
		this.m_yCropBoxSize = yCropBoxSize;
		this.m_zCropBoxSize = zCropBoxSize;
		this.m_thresholdOTSUComputing = thresholdOTSUComputing;
		this.m_slicesOTSUComputing = slicesOTSUComputing;
		this.m_channelToComputeThreshold = channelToComputeThreshold;
		this.m_maxVolumeNucleus = maxVolumeNucleus;
		this.m_minVolumeNucleus = minVolumeNucleus;
	}
	
	
	/**
	 * Constructor with all manual parameters 2
	 *
	 * @param inputFolder                   Path folder containing Images
	 * @param outputFolder                  Path folder output analyse
	 * @param xCropBoxSize                  Number of voxels add in x axis around object
	 * @param yCropBoxSize                  Number of voxels add in z axis around object
	 * @param zCropBoxSize                  Number of stack add in z axis around object
	 * @param channelToComputeThreshold     Channel number to compute OTSU
	 * @param slicesOTSUComputing           Slice start to compute OTSU
	 * @param thresholdOTSUComputing        Minimum OTSU threshold used
	 * @param maxVolumeNucleus              Volume maximum of objects detected
	 * @param minVolumeNucleus              Volume minimum of objects detected
	 * @param boxesPercentSurfaceToFilter   Surface percent of boxes to groups them
	 * @param boxesRegrouping               Activation of boxes regrouping
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus,
	                          int boxesPercentSurfaceToFilter,
	                          boolean boxesRegrouping) {
		
		super(inputFolder, outputFolder);
		this.m_xCropBoxSize = xCropBoxSize;
		this.m_yCropBoxSize = yCropBoxSize;
		this.m_zCropBoxSize = zCropBoxSize;
		this.m_thresholdOTSUComputing = thresholdOTSUComputing;
		this.m_slicesOTSUComputing = slicesOTSUComputing;
		this.m_channelToComputeThreshold = channelToComputeThreshold;
		this.m_maxVolumeNucleus = maxVolumeNucleus;
		this.m_minVolumeNucleus = minVolumeNucleus;
		this.m_boxesRegrouping = boxesRegrouping;
		this.m_boxesPercentSurfaceToFilter = boxesPercentSurfaceToFilter;
	}
	
	
	/**
	 * Constructor with box size modification and slice number used to start OTSU threshold calculation to last slice
	 *
	 * @param inputFolder                 Path folder containing Images
	 * @param outputFolder                Path folder output analyse
	 * @param xCropBoxSize                Number of voxels add in x axis around object
	 * @param yCropBoxSize                Number of voxels add in z axis around object
	 * @param zCropBoxSize                Number of stack add in z axis around object
	 * @param channelToComputeThreshold   Channel number to compute OTSU
	 * @param slicesOTSUComputing         Slice start to compute OTSU
	 * @param thresholdOTSUComputing      Minimum OTSU threshold used
	 * @param maxVolumeNucleus            Volume maximum of objects detected
	 * @param minVolumeNucleus            Volume minimum of objects detected
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          double xCal,
	                          double yCal,
	                          double zCal,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus) {
		
		super(inputFolder, outputFolder, xCal, yCal, zCal);
		this.m_xCropBoxSize = xCropBoxSize;
		this.m_yCropBoxSize = yCropBoxSize;
		this.m_zCropBoxSize = zCropBoxSize;
		this.m_thresholdOTSUComputing = thresholdOTSUComputing;
		this.m_slicesOTSUComputing = slicesOTSUComputing;
		this.m_channelToComputeThreshold = channelToComputeThreshold;
		this.m_maxVolumeNucleus = maxVolumeNucleus;
		this.m_minVolumeNucleus = minVolumeNucleus;
		
	}
	
	
	/**
	 * Constructor with box size modification and slice number used to start OTSU threshold calculation to last slice
	 *
	 * @param inputFolder                   Path folder containing Images
	 * @param outputFolder                  Path folder output analyse
	 * @param xCal                          Image calibration X
	 * @param yCal                          Image calibration Y
	 * @param zCal                          Image calibration Z
	 * @param xCropBoxSize                  Number of voxels add in x axis around object
	 * @param yCropBoxSize                  Number of voxels add in z axis around object
	 * @param zCropBoxSize                  Number of stack add in z axis around object
	 * @param channelToComputeThreshold     Channel number to compute OTSU
	 * @param slicesOTSUComputing           Slice start to compute OTSU
	 * @param thresholdOTSUComputing        Minimum OTSU threshold used
	 * @param maxVolumeNucleus              Volume maximum of objects detected
	 * @param minVolumeNucleus              Volume minimum of objects detected
	 * @param boxesPercentSurfaceToFilter   Surface percent of boxes to groups them
	 * @param regroupBoxes                  Activation of boxes regrouping
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          double xCal,
	                          double yCal,
	                          double zCal,
	                          int xCropBoxSize,
	                          int yCropBoxSize,
	                          int zCropBoxSize,
	                          int slicesOTSUComputing,
	                          int thresholdOTSUComputing,
	                          int channelToComputeThreshold,
	                          int minVolumeNucleus,
	                          int maxVolumeNucleus,
	                          int boxesPercentSurfaceToFilter,
	                          boolean regroupBoxes) {
		
		super(inputFolder, outputFolder, xCal, yCal, zCal);
		this.m_xCropBoxSize = xCropBoxSize;
		this.m_yCropBoxSize = yCropBoxSize;
		this.m_zCropBoxSize = zCropBoxSize;
		this.m_thresholdOTSUComputing = thresholdOTSUComputing;
		this.m_slicesOTSUComputing = slicesOTSUComputing;
		this.m_channelToComputeThreshold = channelToComputeThreshold;
		this.m_maxVolumeNucleus = maxVolumeNucleus;
		this.m_minVolumeNucleus = minVolumeNucleus;
		this.m_boxesPercentSurfaceToFilter = boxesPercentSurfaceToFilter;
		this.m_boxesRegrouping = regroupBoxes;
		
	}
	
	
	/**
	 * Constructor using input , output folders and config file (for command line execution)
	 *
	 * @param inputFolder        Path folder containing Images
	 * @param outputFolder       Path folder output analyse
	 * @param pathToConfigFile   Path to the config file
	 */
	public AutocropParameters(String inputFolder, String outputFolder,
	                          String pathToConfigFile) {
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
			switch (idProp) {
				case "xCropBoxSize":
					this.m_xCropBoxSize = Integer.parseInt(prop.getProperty("xCropBoxSize"));
					break;
				case "yCropBoxSize":
					this.m_yCropBoxSize = Integer.parseInt(prop.getProperty("yCropBoxSize"));
					break;
				case "zCropBoxSize":
					this.m_zCropBoxSize = Integer.parseInt(prop.getProperty("zCropBoxSize"));
					break;
				case "thresholdOTSUComputing":
					this.m_thresholdOTSUComputing = Integer.parseInt(prop.getProperty("thresholdOTSUComputing"));
					break;
				case "slicesOTSUComputing":
					this.m_slicesOTSUComputing = Integer.parseInt(prop.getProperty("slicesOTSUComputing"));
					break;
				case "channelToComputeThreshold":
					this.m_channelToComputeThreshold = Integer.parseInt(prop.getProperty("channelToComputeThreshold"));
					break;
				case "maxVolumeNucleus":
					this.m_maxVolumeNucleus = Integer.parseInt(prop.getProperty("maxVolumeNucleus"));
					break;
				case "minVolumeNucleus":
					this.m_minVolumeNucleus = Integer.parseInt(prop.getProperty("minVolumeNucleus"));
					break;
				case "boxesPercentSurfaceToFilter":
					this.m_boxesPercentSurfaceToFilter =
							Integer.parseInt(prop.getProperty("boxesPercentSurfaceToFilter"));
					break;
				case "boxesRegrouping":
					this.m_boxesRegrouping = Boolean.parseBoolean(prop.getProperty("boxesRegrouping"));
					break;
			}
		}
	}
	
	
	/**
	 * Method to get parameters of the analyse
	 *
	 * @return : list of the parameters used for the analyse
	 */
	public String getAnalyseParameters() {
		super.getAnalyseParameters();
		this.m_headerInfo += "#X box size: " + m_xCropBoxSize + "\n"
		                     + "#Y box size: " + m_yCropBoxSize + "\n"
		                     + "#Z box size: " + m_zCropBoxSize + "\n"
		                     + "#thresholdOTSUComputing: " + m_thresholdOTSUComputing + "\n"
		                     + "#slicesOTSUComputing: " + m_slicesOTSUComputing + "\n"
		                     + "#channelToComputeThreshold: " + m_channelToComputeThreshold + "\n"
		                     + "#maxVolumeNucleus:" + m_maxVolumeNucleus + "\n"
		                     + "#minVolumeNucleus: " + m_minVolumeNucleus + "\n";
		return this.m_headerInfo;
	}
	
	
	/**
	 * Getter for x box size in pixel
	 *
	 * @return x box size in pixel
	 */
	public int getXCropBoxSize() {
		return this.m_xCropBoxSize;
	}
	
	
	/**
	 * Getter for y box size in pixel
	 *
	 * @return y box size in pixel
	 */
	public int getYCropBoxSize() {
		return this.m_yCropBoxSize;
	}
	
	
	/**
	 * Getter for z box size in pixel
	 *
	 * @return z box size in pixel
	 */
	public int getZCropBoxSize() {
		return this.m_zCropBoxSize;
	}
	
	
	/**
	 * Getter for OTSU threshold used to compute segmented image
	 *
	 * @return OTSU threshold used
	 */
	public int getThresholdOTSUComputing() {
		return this.m_thresholdOTSUComputing;
	}
	
	
	/**
	 * Getter for channel number used to segmented image (OTSU computing)
	 *
	 * @return channel number
	 */
	public int getChannelToComputeThreshold() {
		return this.m_channelToComputeThreshold;
	}
	
	
	/**
	 * Getter for minimum volume object segmented
	 *
	 * @return minimum volume
	 */
	public int getMinVolumeNucleus() {
		return this.m_minVolumeNucleus;
	}
	
	
	/**
	 * Getter for maximum volume object segmented
	 *
	 * @return maximum volume
	 */
	public int getMaxVolumeNucleus() {
		return this.m_maxVolumeNucleus;
	}
	
	
	/**
	 * Getter for start slice used to compute OTSU
	 *
	 * @return start slice
	 */
	public int getSlicesOTSUComputing() {
		return this.m_slicesOTSUComputing;
	}
	
	
	/**
	 * Getter boxes merging activation
	 *
	 * @return status
	 */
	public boolean getBoxesRegrouping() {
		return this.m_boxesRegrouping;
	}
	
	
	/**
	 * Getter percent of surface intersection to merge 2 rectangles.
	 *
	 * @return percentage surface
	 */
	public int getBoxesPercentSurfaceToFilter() {
		return this.m_boxesPercentSurfaceToFilter;
	}
	
}
