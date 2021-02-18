package gred.nucleus.cli;

import gred.nucleus.machineLeaningUtils.ComputeNucleiParametersML;
import gred.nucleus.autocrop.*;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.apache.commons.cli.CommandLine;

import java.io.IOException;

public class CLIRunAction {
	/** Command line */
	private final CommandLine m_cmd;
	
	public CLIRunAction(CommandLine cmd) throws Exception {
		this.m_cmd = cmd;
		switch (this.m_cmd.getOptionValue("action")) {
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
				runGenereOV();
				break;
		}
	}
	
	private void runGenereOV() throws Exception {
		GenerateOverlay ov = new GenerateOverlay(this.m_cmd.getOptionValue("input"));
		ov.run();
	}
	
	private void runCropFromCoordinates() throws Exception {
		CropFromCoordinates test = new CropFromCoordinates(this.m_cmd.getOptionValue("input"));
		test.runCropFromCoordinate();
	}
	
	private void runProjectionFromCoordinates() throws Exception {
		if (this.m_cmd.hasOption("coordinateFiltered")) {
			generateProjectionFromCoordinates projection =
					new generateProjectionFromCoordinates(this.m_cmd.getOptionValue("input"),
					                                      this.m_cmd.getOptionValue("input2"),
					                                      this.m_cmd.getOptionValue("input3"));
			projection.generateCoordinateFiltered();
		} else {
			generateProjectionFromCoordinates projection =
					new generateProjectionFromCoordinates(this.m_cmd.getOptionValue("input"),
					                                      this.m_cmd.getOptionValue("input2"));
			projection.generateCoordinate();
		}
	}
	
	private void runAutocrop() throws Exception {
		AutocropParameters autocropParameters = new AutocropParameters(
				this.m_cmd.getOptionValue("input")
				, this.m_cmd.getOptionValue("output"));
		if (this.m_cmd.hasOption("config")) {
			autocropParameters.addGeneralProperties(this.m_cmd.getOptionValue("config"));
			autocropParameters.addProperties(this.m_cmd.getOptionValue("config"));
		}
		if (this.m_cmd.hasOption("file")) {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			autoCrop.runFile(this.m_cmd.getOptionValue("file"));
		} else {
			AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
			autoCrop.runFolder();
		}
	}
	
	private void runSegmentation() throws Exception {
		SegmentationParameters segmentationParameters =
				new SegmentationParameters(this.m_cmd.getOptionValue("input"), this.m_cmd.getOptionValue("output"));
		if (this.m_cmd.hasOption("config")) {
			segmentationParameters.addGeneralProperties(this.m_cmd.getOptionValue("config"));
			segmentationParameters.addProperties(this.m_cmd.getOptionValue("config"));
		}
		if (this.m_cmd.hasOption("file")) {
			SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
			try {
				String log = otsuModif.runOneImage(this.m_cmd.getOptionValue("input"));
				if (!(log.equals(""))) System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
			try {
				String log = otsuModif.runSeveralImages2();
				if (!(log.equals(""))) System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private void runComputeNucleiParameters() throws Exception {
		ComputeNucleiParameters generateParameters =
				new ComputeNucleiParameters(this.m_cmd.getOptionValue("input"), this.m_cmd.getOptionValue("input2"));
		if (this.m_cmd.hasOption("config")) generateParameters.addConfigParameters(this.m_cmd.getOptionValue("config"));
		generateParameters.run();
	}
	
	private void runComputeNucleiParametersDL() throws Exception {
		ComputeNucleiParametersML computeParameters =
				new ComputeNucleiParametersML(this.m_cmd.getOptionValue("input"), this.m_cmd.getOptionValue("input2"));
		computeParameters.run();
	}
	
}
