package gred.nucleus.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang.Validate.isTrue;


/** class to handle command line option */
public class CLIActionOptionCmdLine extends CLIActionOptions {
	
	/** Path to output folder */
	private final Option outputFolder = Option.builder("out")
	                                          .longOpt("output")
	                                          .type(String.class)
	                                          .desc("Path to output results\n")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Path to second input folder Need in specific action */
	private final Option inputFolder2 = Option.builder("in2")
	                                          .longOpt("input2")
	                                          .required()
	                                          .type(String.class)
	                                          .numberOfArgs(1)
	                                          .build();
	/** Path to second input folder Need in specific action */
	private final Option inputFolder3 = Option.builder("in3")
	                                          .longOpt("input3")
	                                          .required()
	                                          .type(String.class)
	                                          .numberOfArgs(1)
	                                          .build();
	
	
	/**
	 * @param args command line argument
	 */
	public CLIActionOptionCmdLine(String[] args) {
		super(args);
		this.action.setDescription(this.action.getDescription() + "\n" +
		                           "computeParameters : compute parameters \n" +
		                           "computeParametersDL : compute parameters for machine leaning\n" +
		                           "generateProjection : generate projection from coordinates\n" +
		                           "CropFromCoordinate : crop wide-field image from coordinate\n" +
		                           "GenerateOverlay : generate overlay from images \n");
		
		checkSpecificOptions();
		try {
			this.cmd = this.parser.parse(this.options, args);
			isTrue(availableActionCMD(this.cmd.getOptionValue("action")));
		} catch (ParseException exp) {
			System.console().writer().println(exp.getMessage() + "\n");
			System.console().writer().println(getHelperInfo());
			System.exit(1);
		} catch (Exception exp) {
			System.console().writer().println("Action option \"" +
			                                  this.cmd.getOptionValue("action") +
			                                  "\" not available" + "\n");
			System.console().writer().println(getHelperInfo());
			System.exit(1);
		}
	}
	
	
	/**
	 * Method to check action parameter
	 *
	 * @param action nucleusJ2.0 action to run
	 *
	 * @return boolean existing action
	 */
	private static boolean availableActionCMD(String action) {
		List<String> actionAvailableInOMERO = new ArrayList<>();
		actionAvailableInOMERO.add("autocrop");
		actionAvailableInOMERO.add("segmentation");
		actionAvailableInOMERO.add("computeParameters");
		actionAvailableInOMERO.add("computeParametersDL");
		actionAvailableInOMERO.add("generateProjection");
		actionAvailableInOMERO.add("generateProjectionFiltered");
		actionAvailableInOMERO.add("CropFromCoordinate");
		actionAvailableInOMERO.add("GenerateOverlay");
		return actionAvailableInOMERO.contains(action);
	}
	
	
	/** Method to check specific action parameters */
	private void checkSpecificOptions() {
		switch (this.cmd.getOptionValue("action")) {
			case "autocrop":
			case "segmentation":
				this.inputFolder.setDescription("Path to input folder containing images to analyse\n");
				this.options.addOption(this.outputFolder);
				break;
			
			case "computeParameters":
				this.inputFolder.setDescription("Path to input folder containing RAW images\n");
				this.inputFolder2.setDescription("Path to input folder containing SEGMENTED images\n");
				this.options.addOption(this.inputFolder2);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "computeParametersDL":
				this.inputFolder.setDescription("Path to input folder containing RAW images\n");
				this.inputFolder2.setDescription("Path to input folder containing machine leaning SEGMENTED images\n");
				this.options.addOption(this.inputFolder2);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "generateProjection":
				this.inputFolder.setDescription("Path to input folder containing coordinates files\n");
				this.inputFolder2.setDescription("Path to input folder containing raw data\n");
				this.options.addOption(this.inputFolder2);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "generateProjectionFiltered":
				this.inputFolder.setDescription("Path to input folder containing coordinates files\n");
				this.inputFolder2.setDescription(
						"Path to input folder containing kept images after segmentation filter\n");
				this.inputFolder3.setDescription("Path to input folder containing initial Zprojection\n");
				this.options.addOption(this.inputFolder2);
				this.options.addOption(this.inputFolder3);
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "CropFromCoordinate":
				this.inputFolder.setDescription("Path to tabulated file containing 2 columns :\n" +
				                                "pathToCoordinateFile   pathToRawImageAssociate\n");
				this.omero.setDescription("NOT AVAILABLE");
				break;
			
			case "GenerateOverlay":
				this.inputFolder.setDescription("Path to tabulated file containing 2 columns :\n" +
				                                "pathToOverlayFile pathToRawImageAssociate\n");
				this.omero.setDescription("NOT AVAILABLE");
				break;
		}
	}
	
}