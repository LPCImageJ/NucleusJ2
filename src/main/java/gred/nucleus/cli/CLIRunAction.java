package gred.nucleus.cli;

import gred.nucleus.autocrop.*;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.machinelearning.ComputeNucleiParametersML;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.io.IOException;


public class CLIRunAction {
	/** Command line */
	private final CommandLine cmd;
	
	
	public CLIRunAction(CommandLine cmd) throws Exception {
		this.cmd = cmd;
		switch (this.cmd.getOptionValue("action")) {
			case "autocrop":
				runAutocrop();
				break;
			case "segmentation":
				runSegmentation();
				break;
			case "computeParameters":
				runComputeNucleiParameters();
				break;
			case "computeParametersDL":
				runComputeNucleiParametersDL();
				break;
			case "generateProjection":
				runProjectionFromCoordinates();
				break;
			case "cropFromCoordinate":
				runCropFromCoordinates();
				break;
			case "GenerateOverlay":
				runGenerateOV();
				break;
		}
	}
	
	
	private void runGenerateOV() throws Exception {
		GenerateOverlay ov = new GenerateOverlay(this.cmd.getOptionValue("input"));
		ov.run();
	}
	
	
	private void runCropFromCoordinates() throws Exception {
		CropFromCoordinates test = new CropFromCoordinates(this.cmd.getOptionValue("input"));
		test.runCropFromCoordinate();
	}
	
	
	private void runProjectionFromCoordinates() throws Exception {
		if (this.cmd.hasOption("coordinateFiltered")) {
			GenerateProjectionFromCoordinates projection =
					new GenerateProjectionFromCoordinates(this.cmd.getOptionValue("input"),
					                                      this.cmd.getOptionValue("input2"),
					                                      this.cmd.getOptionValue("input3"));
			projection.generateCoordinateFiltered();
		} else {
			GenerateProjectionFromCoordinates projection =
					new GenerateProjectionFromCoordinates(this.cmd.getOptionValue("input"),
					                                      this.cmd.getOptionValue("input2"));
			projection.generateCoordinate();
		}
	}
	
	
	private void runAutocrop() {
		AutocropParameters autocropParameters = new AutocropParameters(
				this.cmd.getOptionValue("input")
				, this.cmd.getOptionValue("output"));
		if (this.cmd.hasOption("config")) {
			autocropParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			autocropParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		File path = new File(this.cmd.getOptionValue("input"));
		if (path.isFile()) {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			autoCrop.runFile(this.cmd.getOptionValue("input"));
			autoCrop.saveGeneralInfo();
		} else {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			autoCrop.runFolder();
		}
	}
	
	
	private void runSegmentation() throws Exception {
		SegmentationParameters segmentationParameters =
				new SegmentationParameters(this.cmd.getOptionValue("input"), this.cmd.getOptionValue("output"));
		if (this.cmd.hasOption("config")) {
			segmentationParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			segmentationParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		File path = new File(this.cmd.getOptionValue("input"));
		if (path.isFile()) {
			SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
			try {
				String log = otsuModified.runOneImage(this.cmd.getOptionValue("input"));
				otsuModified.saveCropGeneralInfo();
				if (!(log.equals(""))) System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
			try {
				String log = otsuModified.runSeveralImages2();
				if (!(log.equals(""))) System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private void runComputeNucleiParameters() {
		ComputeNucleiParameters generateParameters =
				new ComputeNucleiParameters(this.cmd.getOptionValue("input"), this.cmd.getOptionValue("input2"));
		if (this.cmd.hasOption("config")) generateParameters.addConfigParameters(this.cmd.getOptionValue("config"));
		generateParameters.run();
	}
	
	
	private void runComputeNucleiParametersDL() throws Exception {
		ComputeNucleiParametersML computeParameters =
				new ComputeNucleiParametersML(this.cmd.getOptionValue("input"), this.cmd.getOptionValue("input2"));
		computeParameters.run();
	}
	
}
