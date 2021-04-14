package gred.nucleus.plugins;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.dialogs.*;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;
import java.util.List;


public class Segmentation_ implements PlugIn, IDialogListener {
	SegmentationDialog segmentationDialog;
	
	
	public static void segmentationFolder(String inputDirectory, String outputDirectory) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory, outputDirectory);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			File   file = new File(inputDirectory);
			String log  = "";
			if (file.isDirectory()) {
				log = otsuModified.runSeveralImages2();
			} else if (file.isFile()) {
				log = otsuModified.runOneImage(inputDirectory);
			}
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void segmentationFolder(String inputDirectory, String outputDirectory, String config) throws Exception {
		SegmentationParameters segmentationParameters =
				new SegmentationParameters(inputDirectory, outputDirectory, config);
		SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
		try {
			File   file = new File(inputDirectory);
			String log  = "";
			if (file.isDirectory()) {
				log = otsuModified.runSeveralImages2();
			} else if (file.isFile()) {
				log = otsuModified.runOneImage(inputDirectory);
			}
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void segmentationFolder(String inputDirectory,
	                                      String outputDirectory,
	                                      String minVolume,
	                                      String maxVolume,
	                                      boolean isGiftWrapping) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,
		                                                                           outputDirectory,
		                                                                           Integer.parseInt(minVolume),
		                                                                           Integer.parseInt(maxVolume),
		                                                                           isGiftWrapping);
		SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
		try {
			File   file = new File(inputDirectory);
			String log  = "";
			if (file.isDirectory()) {
				log = otsuModified.runSeveralImages2();
			} else if (file.isFile()) {
				log = otsuModified.runOneImage(inputDirectory);
			}
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void segmentationFolder(String inputDirectory,
	                                      String outputDirectory,
	                                      String xCal,
	                                      String yCal,
	                                      String zCal,
	                                      String minVolume,
	                                      String maxVolume,
	                                      boolean isGiftWrapping) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,
		                                                                           outputDirectory,
		                                                                           Integer.parseInt(minVolume),
		                                                                           Integer.parseInt(maxVolume),
		                                                                           isGiftWrapping);
		segmentationParameters.setXCal(Integer.parseInt(xCal));
		segmentationParameters.setYCal(Integer.parseInt(yCal));
		segmentationParameters.setZCal(Integer.parseInt(zCal));
		SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
		try {
			File   file = new File(inputDirectory);
			String log  = "";
			if (file.isDirectory()) {
				log = otsuModified.runSeveralImages2();
			} else if (file.isFile()) {
				log = otsuModified.runOneImage(inputDirectory);
			}
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Run method for imageJ plugin for the segmentation
	 *
	 * @param arg use by imageJ
	 */
	@Override
	public void run(String arg) {
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		segmentationDialog = new SegmentationDialog(this);
	}
	
	@Override
	public void OnStart() {
		if (segmentationDialog.isOmeroEnabled()) {
			runOmeroSegmentation();
		} else {
			runLocalSegmentation();
		}
	}
	
	public Client checkOMEROConnexion (String hostname,
	                                   String port,
	                                   String username,
	                                   String password,
	                                   String group){
		Client client = new Client();
		try {
			client.connect(hostname,
			               Integer.parseInt(port),
			               username,
			               password,
			               Long.valueOf(group));
		} catch (Exception exp) {
			IJ.error("Invalid connection values");
			return null;
		}
		return client;
	}
	
	private void runOmeroSegmentation () {
		// Check connection
		String hostname = segmentationDialog.getHostname();
		String port     = segmentationDialog.getPort();
		String username = segmentationDialog.getUsername();
		String password = segmentationDialog.getPassword();
		String group    = segmentationDialog.getGroup();
		Client client   = checkOMEROConnexion(hostname, port, username, password, group);
		
		SegmentationParameters segmentationParameters = null;
		// Check config
		String configFile = segmentationDialog.getConfig();
		switch (segmentationDialog.getConfigMode()) {
			case DEFAULT:
				segmentationParameters = new SegmentationParameters(".", ".");
				break;
			case FILE:
				segmentationParameters = new SegmentationParameters(".", ".", configFile);
				break;
			case INPUT:
				SegmentationConfigDialog scd = segmentationDialog.getSegmentationConfigFileDialog();
				if (scd.isCalibrationSelected()) {
					IJ.log("w/ calibration");
					segmentationParameters = new SegmentationParameters(".", ".",
					                                                    Integer.parseInt(scd.getXCalibration()),
					                                                    Integer.parseInt(scd.getYCalibration()),
					                                                    Integer.parseInt(scd.getZCalibration()),
					                                                    Integer.parseInt(scd.getMinVolume()),
					                                                    Integer.parseInt(scd.getMaxVolume()),
					                                                    scd.getGiftWrapping()
					);
				} else {
					IJ.log("w/out calibration");
					segmentationParameters = new SegmentationParameters(".", ".",
					                                                    Integer.parseInt(scd.getMinVolume()),
					                                                    Integer.parseInt(scd.getMaxVolume()),
					                                                    scd.getGiftWrapping()
					);
				}
				break;
		}
		
		SegmentationCalling segmentation = new SegmentationCalling(segmentationParameters);
		
		// Handle the source according to the type given
		String dataType = segmentationDialog.getDataType();
		Long   inputID  = Long.valueOf(segmentationDialog.getSourceID());
		Long   outputID = Long.valueOf(segmentationDialog.getOutputProject());
		try {
			if (dataType.equals("Image")) {
				ImageWrapper image = client.getImage(inputID);
				String log;
				
				log = segmentation.runOneImageOMERO(image, outputID, client);
				
				if (!(log.equals(""))) {
					IJ.log("Nuclei which didn't pass the segmentation\n" + log);
				}
			} else {
				List<ImageWrapper> images = null;
				
				switch (segmentationDialog.getDataType()) {
					case "Dataset":
						DatasetWrapper dataset = client.getDataset(inputID);
						images = dataset.getImages(client);
						break;
					case "Project":
						ProjectWrapper project = client.getProject(inputID);
						images = project.getImages(client);
						break;
					case "Tag":
						images = client.getImagesTagged(inputID);
						break;
				}
				String log;
				log = segmentation.runSeveralImageOMERO(images, outputID, client);
				if (!(log.equals(""))) {
					IJ.log("Nuclei which didn't pass the segmentation\n" + log);
				}
			}
		} catch (ServiceException se) {
			IJ.error("Unable to access to OMERO service");
		} catch (AccessException ae) {
			IJ.error("Cannot access " + dataType + "with ID = " + inputID + ".");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runLocalSegmentation(){
		String input  = segmentationDialog.getInput();
		String output = segmentationDialog.getOutput();
		String config = segmentationDialog.getConfig();
		if (input == null || input.equals("")) {
			IJ.error("Input file or directory is missing");
		} else if (output == null || output.equals("")) {
			IJ.error("Output directory is missing");
		} else {
			try {
				IJ.log("Begin Segmentation process ");
				
				if (segmentationDialog.getConfigMode() == SegmentationDialog.ConfigMode.FILE) {
					if (config == null || config.equals("")) {
						IJ.error("Config file is missing");
					} else {
						IJ.log("Config file");
						segmentationFolder(input, output, config);
					}
				} else if (segmentationDialog.getConfigMode() == SegmentationDialog.ConfigMode.INPUT) {
					SegmentationConfigDialog scd = segmentationDialog.getSegmentationConfigFileDialog();
					if (scd.isCalibrationSelected()) {
						IJ.log("w/ calibration" +
						       "\nx: " + scd.getXCalibration() +
						       "\ny: " + scd.getYCalibration() +
						       "\nz: " + scd.getZCalibration());
						segmentationFolder(input, output,
						                   scd.getXCalibration(), scd.getYCalibration(), scd.getZCalibration(),
						                   scd.getMinVolume(), scd.getMaxVolume(), scd.getGiftWrapping()
						                  );
					} else {
						IJ.log("w/out calibration");
						segmentationFolder(input, output,
						                   scd.getMinVolume(), scd.getMaxVolume(), scd.getGiftWrapping()
						                  );
					}
				} else {
					IJ.log("w/out config");
					segmentationFolder(input, output);
				}
				
				IJ.log("\nSegmentation process has ended successfully");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
