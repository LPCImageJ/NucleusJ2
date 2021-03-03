package gred.nucleus.cli;

import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.OutputTextFile;
import gred.nucleus.mains.Version;
import org.apache.commons.cli.HelpFormatter;


/** Class to generate helper */
public class CLIHelper {
	
	/**
	 * Main method
	 *
	 * @param args command line arguments
	 */
	public static void run(String[] args) {
		if (args.length == 2) {
			specificAction(args[1]);
		} else {
			CmdHelpFull();
		}
	}
	
	
	/**
	 * Method get help for command line with example command line
	 */
	private static void CmdHelpFull() {
		String example_command = "java -jar NucleusJ_2-" + Version.get() + ".jar ";
		String example_argument = "-action segmentation " +
		                          "-input path/to/input/folder/ " +
		                          "-output path/to/output/folder/ ";
		String[]               example_Cmd = example_argument.split(" ");
		CLIActionOptionCmdLine command     = new CLIActionOptionCmdLine(example_Cmd);
		HelpFormatter          formatter   = new HelpFormatter();
		formatter.printHelp("NucleusJ2.0 cli : ", command.getOptions());
		System.out.println("\nCommand line example : \n" +
		                   example_command + " " + example_argument + "\n\n");
		
		String example_argument_OMERO = "-omero " +
		                                "-action segmentation " +
		                                "-input path/to/input/folder/ " +
		                                "-output path/to/output/folder/ " +
		                                "-hostname omero-server-address " +
		                                "-port 0 " +
		                                "-group 000";
		String[]             example_OMEROCmd = example_argument_OMERO.split(" ");
		CLIActionOptionOMERO command_OMERO    = new CLIActionOptionOMERO(example_OMEROCmd);
		formatter.printHelp("NucleusJ2.0 OMERO MODE: ", command_OMERO.getOptions());
		System.out.println("\nCommand line example : \n\n" +
		                   example_command + " " + example_argument_OMERO);
		
		System.exit(1);
	}
	
	
	/**
	 * Helper for specific action.
	 *
	 * @param action action
	 */
	private static void specificAction(String action) {
		String                 example_command = "java -jar NucleusJ_2-" + Version.get() + ".jar ";
		String                 example_argument;
		String[]               example_Cmd;
		HelpFormatter          formatter;
		CLIActionOptionCmdLine command;
		switch (action) {
			case "segmentation":
				example_argument = "-action segmentation " +
				                   "-input path/to/input/folder/ " +
				                   "-output path/to/output/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 segmentation cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				
				String example_argument_OMERO = "-omero " +
				                                "-action segmentation " +
				                                "-input path/to/input/folder/ " +
				                                "-output path/to/output/folder/ " +
				                                "-hostname omero-server-address " +
				                                "-port 0 " +
				                                "-group 000";
				
				String[] example_OMEROCmd = example_argument_OMERO.split(" ");
				CLIActionOptionOMERO command_OMERO = new CLIActionOptionOMERO(example_OMEROCmd);
				formatter.printHelp("NucleusJ2.0 segmentation OMERO MODE: ", command_OMERO.getOptions());
				System.out.println("\nCommand line example : \n\n" +
				                   example_command + " " + example_argument_OMERO);
				break;
			
			case "autocrop":
				example_argument = "-action autocrop " +
				                   "-input path/to/input/folder/ " +
				                   "-output path/to/output/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 autocrop cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				
				
				example_argument_OMERO = "-omero " +
				                         "-action autocrop " +
				                         "-input path/to/input/folder/ " +
				                         "-output path/to/output/folder/ " +
				                         "-hostname omero-server-address " +
				                         "-port 0 " +
				                         "-group 000";
				example_OMEROCmd = example_argument_OMERO.split(" ");
				command_OMERO = new CLIActionOptionOMERO(example_OMEROCmd);
				formatter.printHelp("NucleusJ2.0 autocrop OMERO MODE: ", command_OMERO.getOptions());
				System.out.println("\nCommand line example : \n\n" +
				                   example_command + example_argument_OMERO);
				break;
			
			case "computeParameters":
				example_argument = "-action computeParameters " +
				                   "-input path/to/raw/image/folder/ " +
				                   "-input2 path/to/segmented/image/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 computeParameters cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "computeParametersDL":
				example_argument = "-action computeParametersDL " +
				                   "-input path/to/raw/image/folder/ " +
				                   "-input2 path/to/segmented/image/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 computeParametersDL cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "generateProjection":
				example_argument = "-action generateProjection " +
				                   "-input path/to/coordinate/file/folder/ " +
				                   "-input2 path/to/raw/image/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 generateProjection cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "generateProjectionFiltered":
				example_argument = "-action generateProjectionFiltered " +
				                   "-input path/to/coordinate/file/folder/ " +
				                   "-input2 path/to/segmented/image/folder/ " +
				                   "-input3 path/to/ZProjection/folder/";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 generateProjectionFiltered cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "CropFromCoordinate":
				example_argument = "-action CropFromCoordinate " +
				                   "-input path/to/coordinate/file/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 CropFromCoordinate cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "GenerateOverlay":
				example_argument = "-action GenerateOverlay " +
				                   "-input path/to/coordinate/file/folder/ ";
				example_Cmd = example_argument.split(" ");
				command = new CLIActionOptionCmdLine(example_Cmd);
				formatter = new HelpFormatter();
				formatter.printHelp("NucleusJ2.0 GenerateOverlay cli : ", command.getOptions());
				System.out.println("\nCommand line example : \n" +
				                   example_command + example_argument + "\n\n");
				break;
			
			case "configFileExample":
				
				String autocropConfigOption = "xCropBoxSize:40\n" +
				                              "yCropBoxSize:40\n" +
				                              "zCropBoxSize:20\n" +
				                              "minVolumeNucleus:1\n" +
				                              "maxVolumeNucleus:2147483647\n" +
				                              "thresholdOTSUComputing:20\n" +
				                              "channelToComputeThreshold:0\n" +
				                              "slicesOTSUComputing:0\n" +
				                              "boxesPercentSurfaceToFilter:50\n" +
				                              "boxesRegrouping:10\n" +
				                              "xCal:1\n" +
				                              "yCal:1\n" +
				                              "zCal:1";
				
				String segConfigOption =
						"thresholdOTSUComputing:20\n" +
						"GiftWrapping:true\n" +
						"xCal:1\n" +
						"yCal:1\n" +
						"zCal:1";
				System.out.println("Two config file with default parameters generate: \n");
				
				saveFile(autocropConfigOption, "autocropConfigListParameters");
				saveFile(segConfigOption, "segmentationConfigListParameters");
				System.out.println("autocrop parameters details: " +
				                   "https://gitlab.com/DesTristus/NucleusJ2.0/-/wikis/Autocrop#list-of-available-parameters \n" +
				                   "segmentation parameters details: " +
				                   "https://gitlab.com/DesTristus/NucleusJ2.0/-/wikis/Nucleus-segmentation#list-of-available-parameters");
				break;
			
			default:
				example_argument = "-action segmentation " +
				                   "-input path/to/input/folder/ " +
				                   "-output path/to/output/folder/ ";
				example_Cmd = example_argument.split(" ");
				CLIActionOptions wrongAction = new CLIActionOptions(example_Cmd);
				System.out.println("Invalid action \"" + action + "\" :\n");
				System.out.println(wrongAction.getHelperInfo());
				break;
		}
	}
	
	
	/**
	 * Save information use to save config file parameter example.
	 *
	 * @param text     text to save
	 * @param fileName file name
	 */
	public static void saveFile(String text, String fileName) {
		Directory dirOutput = new Directory(System.getProperty("user.dir"));
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(
				dirOutput.getDirPath()
				+ dirOutput.getSeparator()
				+ fileName);
		resultFileOutputOTSU.saveTextFile(text, true);
	}
	
}
