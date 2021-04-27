package gred.nucleus.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;

import static org.apache.commons.lang.Validate.isTrue;


/** Inherited class to handle OMERO command line option */
public class CLIActionOptionOMERO extends CLIActionOptions {
	/** Host name server */
	private final Option hostname     = Option.builder("ho")
	                                          .longOpt("hostname")
	                                          .required()
	                                          .type(String.class)
	                                          .desc("Hostname of the OMERO server")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Server port connection */
	private final Option port         = Option.builder("pt")
	                                          .longOpt("port")
	                                          .required()
	                                          .type(Integer.class)
	                                          .desc("Port used by OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** username connection */
	private final Option username     = Option.builder("u")
	                                          .longOpt("username")
	                                          .type(String.class)
	                                          .desc("Username in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** OMERO password connection */
	private final Option password     = Option.builder("p")
	                                          .longOpt("password")
	                                          .type(String.class)
	                                          .desc("Password in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Group user connection */
	private final Option group        = Option.builder("g")
	                                          .longOpt("group")
	                                          .required()
	                                          .type(String.class)
	                                          .desc("Group in OMERO")
	                                          .numberOfArgs(1)
	                                          .build();
	/** Path to output folder */
	private final Option outputFolder = Option.builder("out")
	                                          .longOpt("output")
	                                          .type(String.class)
	                                          .desc("Path to output folder containing images to analyse\n")
	                                          .numberOfArgs(1)
	                                          .build();
	
	
	/**
	 * Constructor with argument
	 *
	 * @param argument List of command line argument
	 */
	public CLIActionOptionOMERO(String[] argument) {
		super(argument);
		this.options.addOption(this.action);
		this.options.addOption(this.outputFolder);
		this.options.addOption(this.port);
		this.options.addOption(this.hostname);
		this.options.addOption(this.username);
		this.options.addOption(this.password);
		this.options.addOption(this.group);
		this.inputFolder.setDescription(
				"OMERO  inputs 2 information separated with slash separator :  " +
				"Type input: dataset, project, image, tag " +
				"Input id number" + "\n" +
				"Example : " + "\n" +
				"          dataset/1622");
		try {
			this.cmd = this.parser.parse(this.options, argument);
			isTrue(availableActionOMERO(this.cmd.getOptionValue("action")));
			
		} catch (ParseException exp) {
			System.out.println(exp.getMessage() + "\n");
			System.out.println(getHelperInfo());
			System.exit(1);
		} catch (Exception exp) {
			System.out.println("Action option \"" +
			                   this.cmd.getOptionValue("action") +
			                   "\" not available" + "\n");
			System.out.println(getHelperInfo());
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
	private static boolean availableActionOMERO(String action) {
		ArrayList<String> actionAvailableInOMERO = new ArrayList<>();
		actionAvailableInOMERO.add("autocrop");
		actionAvailableInOMERO.add("segmentation");
		return actionAvailableInOMERO.contains(action);
	}
	
	
	private Option getGroup() {
		return this.group;
	}
	
	
	private Option getHostname() {
		return this.hostname;
	}
	
	
	private Option getPort() {
		return this.port;
	}
	
	
	private Option getUsername() {
		return this.username;
	}
	
	
	private Option getPassword() {
		return this.password;
	}
	
	
	private Option getOutputFolder() {
		return this.outputFolder;
	}
	
}

//        if(cmd.getOptionValue("action").equals("autocrop")) {
/*
ArrayList<String> actionList = new ArrayList<>();
        actionList.add("autocrop");
        actionList.add("segmentation");
        actionList.add("computeParameters");
        actionList.add("computeParametersDL");
        actionList.add("generateProjection");
        actionList.add("CropFromCoordinate");
        actionList.add("GenerateOverlay");
 */