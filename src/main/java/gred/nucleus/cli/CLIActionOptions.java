package gred.nucleus.cli;

import gred.nucleus.mains.Version;
import org.apache.commons.cli.*;


/** Generic class to handle command line option */
public class CLIActionOptions {
	
	/** NucleusJ version */
	private static final String NJ_version  = Version.get();
	/** Path to input folder */
	public               Option inputFolder = Option.builder("in")
	                                                .longOpt("input")
	                                                .required()
	                                                .type(String.class)
	                                                .numberOfArgs(1)
	                                                .build();
	/** Path to config file */
	public               Option configFile  = Option.builder("c")
	                                                .longOpt("config")
	                                                .type(String.class)
	                                                .desc("Path to config file\n" +
	                                                      "To generate config file example in current folder:\n" +
	                                                      "java -jar NucleusJ_2-" +
	                                                      NJ_version +
	                                                      ".jar -h configFileExample")
	                                                .numberOfArgs(1)
	                                                .build();
	/** List of available actions */
	public               Option action      = Option.builder("a")
	                                                .longOpt("action")
	                                                .required()
	                                                .type(String.class)
	                                                .desc("Action available:\n" +
	                                                      "autocrop : crop wide field images\n" +
	                                                      "segmentation : nucleus segmentation\n")
	                                                .numberOfArgs(1)
	                                                .build();
	/** OMERO activate */
	public               Option omero       = Option.builder("ome")
	                                                .longOpt("omero")
	                                                .type(boolean.class)
	                                                .desc("Use of NucleusJ2.0 in OMERO\n")
	                                                .build();
	/** List of options */
	Options           options = new Options();
	/** Command line */
	CommandLine       cmd;
	/** Command line parser */
	CommandLineParser parser  = new DefaultParser();
	
	
	/**
	 * Constructor with argument
	 *
	 * @param argument List of command line argument
	 */
	public CLIActionOptions(String[] argument) {
		this.options.addOption(this.inputFolder);
		this.options.addOption(this.configFile);
		this.options.addOption(this.action);
		this.options.addOption(this.omero);
		try {
			this.cmd = this.parser.parse(this.options, argument, true);
		} catch (ParseException exp) {
			System.out.println(exp.getMessage() + "\n");
			System.out.println(getHelperInfo());
			System.exit(1);
		}
	}
	
	
	/** @return : helper info */
	public String getHelperInfo() {
		return "More details for available actions:\n" +
		       "java -jar NucleusJ_2-" + NJ_version + ".jar -h \n" +
		       "java -jar NucleusJ_2-" + NJ_version + ".jar -help \n\n" +
		       "More details for a specific action:\n" +
		       "java -jar NucleusJ_2-" + NJ_version + ".jar -h <action>\n" +
		       "java -jar NucleusJ_2-" + NJ_version + ".jar -help <action>";
	}
	
	
	/** @return list of options */
	public Options getOptions() {
		return this.options;
	}
	
	
	public CommandLine getCmd() {
		return this.cmd;
	}
	
}