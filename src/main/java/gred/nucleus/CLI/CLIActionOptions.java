package gred.nucleus.CLI;

import org.apache.commons.cli.*;

/**
 * Generic class to handle command line option
 */
public class CLIActionOptions {

    /**
     * List of options
     */
    Options m_options= new Options();
    /**
     * Command line
     */
    CommandLine m_cmd;
    /**
     * Help formatter
     */
    HelpFormatter m_formatter ;
    /**
     * Command line parser
     */
    CommandLineParser m_parser= new DefaultParser();
    /**
     * NucleusJ version
     */
    String NJversion ="1.1.0";
    /**
     * Path to input folder
     */
    Option m_imputFolder= Option.builder("in")
            .longOpt("input")
            .required()
            .type(String.class)
            .numberOfArgs(1)
            .build();
    /**
     * Path to output folder
     */
    Option m_outputFolder= Option.builder("out")
            .longOpt("output")
            .required()
            .type(String.class)
            .desc("Path to output folder containing images to analyse\n")
            .numberOfArgs(1)
            .build();
    /**
     * Path to config file
     */
    Option m_configFile= Option.builder("c")
            .longOpt("config")
            .type(String.class)
            .desc("Path to config file\n")
            .numberOfArgs(1)
            .build();
    /**
     * List of available actions
     */
    Option m_action = Option.builder("a")
            .longOpt("action")
            .required()
            .type(String.class)
            .desc("Action available:\n" +
                    "autocrop : crop wide field images\n" +
                    "segmentation : nucleus segmentation\n")
            .numberOfArgs(1)
            .build();


    /**
     * Default constructor
     */
    public CLIActionOptions() {
    }

    /**
     Constructor with argument
     * @param argument : list of command line argument
     * @throws Exception
     */
    public CLIActionOptions(String[] argument)throws Exception   {
        this.m_options.addOption(this.m_imputFolder);
        this.m_options.addOption(this.m_outputFolder);
        this.m_options.addOption(this.m_configFile);
        this.m_options.addOption(this.m_action);
        try {
            this.m_cmd = this.m_parser.parse(this.m_options, argument,true);
        }
        catch (ParseException  exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     * @return : helper info
     */
    public String  getHelperInfos() {
        return "More details :\n" +
               "java -jar NucleusJ_2-"+NJversion+".jar -h \n" +
                "or \n"+
                "java -jar NucleusJ_2-"+NJversion+".jar -help \n";
    }
    /**
     * @return list of options
     */
    public Options getM_options() {
        return this.m_options;
    }
    public CommandLine getCmd() {
        return this.m_cmd;
    }

}