package gred.nucleus.plugins;

import gred.nucleus.filesInputOutput.Directory;
import ij.IJ;
import ij.ImagePlus;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;


public class PluginParameters {
	
	/** Activation of manual calibration parameter */
	public boolean m_manualParameter = false;
	/** X calibration plugin parameter */
	public double  m_xCal            = 1;
	/** y calibration plugin parameter */
	public double  m_yCal            = 1;
	/** z calibration plugin parameter */
	public double  m_zCal            = 1;
	/** Input folder */
	public String  m_inputFolder;
	/** Output folder */
	public String  m_outputFolder;
	/** Autocrop parameters information */
	public String  m_headerInfo;
	
	
	/** Constructor with default parameter */
	public PluginParameters() {
	}
	
	
	/**
	 * Constructor with default parameter
	 *
	 * @param inputFolder    Path folder containing Images
	 * @param outputFolder   Path folder output analyse
	 */
	public PluginParameters(String inputFolder, String outputFolder) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.CheckAndCreateDir();
		this.m_outputFolder = dirOutput.getDirPath();
		
		
	}
	
	
	/**
	 * Constructor with specific calibration in x y and z
	 *
	 * @param inputFolder    Path folder containing Images
	 * @param outputFolder   Path folder output analyse
	 * @param xCal         x calibration voxel
	 * @param yCal           Y calibration voxel
	 * @param zCal           Z calibration voxel
	 */
	public PluginParameters(String inputFolder, String outputFolder, double xCal, double yCal, double zCal) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.CheckAndCreateDir();
		this.m_outputFolder = dirOutput.getDirPath();
		this.m_manualParameter = true;
		this.m_xCal = xCal;
		this.m_yCal = yCal;
		this.m_zCal = zCal;
		
	}
	
	
	/**
	 * Constructor using input , output folders and config file (for command line execution)
	 *
	 * @param inputFolder        Path folder containing Images
	 * @param outputFolder       Path folder output analyse
	 * @param pathToConfigFile   Path to the config file
	 */
	public PluginParameters(String inputFolder, String outputFolder, String pathToConfigFile) {
		checkInputPaths(inputFolder, outputFolder);
		Directory dirOutput = new Directory(outputFolder);
		dirOutput.CheckAndCreateDir();
		this.m_outputFolder = dirOutput.getDirPath();
		addGeneralProperties(pathToConfigFile);
		
	}
	
	
	public void addGeneralProperties(String pathToConfigFile) {
		
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
				case "xCal":
					setXCal(Double.parseDouble(prop.getProperty("xCal")));
					break;
				case "yCal":
					setYCal(Double.parseDouble(prop.getProperty("yCal")));
					break;
				case "zCal":
					setZCal(Double.parseDouble(prop.getProperty("zCal")));
					break;
			}
		}
	}
	
	
	private void checkInputPaths(String inputFolder, String outputFolder) {
		File input = new File(inputFolder);
		if (input.isDirectory()) {
			this.m_inputFolder = inputFolder;
		} else if (input.isFile()) {
			this.m_inputFolder = input.getParent();
			
		} else {
			System.err.println(inputFolder + " : can't find the input folder/file !");
			IJ.error(inputFolder + " : can't find the input folder/file !");
//            System.exit(-1);
		}
		if (outputFolder == null) {
			IJ.error("Output directory is missing");
			System.exit(-1);
		}
	}
	
	
	/**
	 * Getter : input path
	 *
	 * @return input path folder
	 */
	public String getInputFolder() {
		return this.m_inputFolder;
	}
	
	
	/**
	 * Getter : output path
	 *
	 * @return output path folder
	 */
	public String getOutputFolder() {
		return this.m_outputFolder;
	}
	
	
	/**
	 * Getter : HEADER parameter of the analyse containing path input output folder and x y z calibration on parameter
	 * per line
	 *
	 * @return output path folder
	 */
	public String getAnalyseParameters() {
		this.m_headerInfo = "#Header \n"
		                    + "#Star time analyse: " + getLocalTime() + "\n"
		                    + "#Input folder: " + this.m_inputFolder + "\n"
		                    + "#Output folder: " + this.m_outputFolder + "\n"
		                    + "#Calibration:" + getInfoCalibration() + "\n";
		return this.m_headerInfo;
		
	}
	
	
	/**
	 * Getter : image x y z calibration
	 *
	 * @return output path folder
	 */
	public String getInfoCalibration() {
		String parameters_info;
		if (this.m_manualParameter) {
			parameters_info = "x:" + this.m_xCal + "-y:" + this.m_yCal + "-z:" + this.m_zCal;
		} else {
			parameters_info = "x:default-y:default-z:default";
		}
		return parameters_info;
		
	}
	
	
	/**
	 * get local time start analyse information yyyy-MM-dd:HH-mm-ss format
	 *
	 * @return time in yyyy-MM-dd:HH-mm-ss format
	 */
	public String getLocalTime() {
		return new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
	}
	
	
	public double getVoxelVolume() {
		return this.m_xCal * this.m_yCal * this.m_zCal;
		
	}
	
	
	public double getXCal() {
		return this.m_xCal;
	}
	
	
	public void setXCal(double manualXCal) {
		this.m_xCal = manualXCal;
		this.m_manualParameter = true;
	}
	
	
	public double getYCal() {
		return this.m_yCal;
	}
	
	
	public void setYCal(double manualYCal) {
		this.m_yCal = manualYCal;
		this.m_manualParameter = true;
	}
	
	
	public double getZCal() {
		return this.m_zCal;
	}
	
	
	public void setZCal(double manualZCal) {
		this.m_zCal = manualZCal;
		this.m_manualParameter = true;
	}
	
	
	public boolean getManualParameter() {
		return this.m_manualParameter;
	}
	
	
	public double getXCalibration(ImagePlus raw) {
		double xCal;
		if (this.m_manualParameter) {
			xCal = this.m_xCal;
		} else {
			xCal = raw.getCalibration().pixelWidth;
		}
		return xCal;
	}
	
	
	public double getYCalibration(ImagePlus raw) {
		double yCal;
		if (this.m_manualParameter) {
			yCal = this.m_yCal;
		} else {
			yCal = raw.getCalibration().pixelHeight;
		}
		return yCal;
	}
	
	
	public double getZCalibration(ImagePlus raw) {
		double zCal;
		if (this.m_manualParameter) {
			zCal = this.m_zCal;
		} else {
			zCal = raw.getCalibration().pixelDepth;
		}
		return zCal;
	}
	
}
