package gred.nucleus.CLI;

import org.apache.commons.cli.*;

public class CLIActionOptions {
    Options m_options= new Options();
    CommandLine m_cmd;
    HelpFormatter m_formatter ;
    CommandLineParser m_parser= new DefaultParser();
    String NJversion ="1.1.0";

    Option m_imputFolder= Option.builder("i")
            .longOpt("input")
            .required()
            .type(String.class)
            .desc("Path to input folder containing images to analyse\n")
            .numberOfArgs(1)
            .build();
    Option m_outputFolder= Option.builder("o")
            .longOpt("output")
            .required()
            .type(String.class)
            .desc("Path to output folder containing images to analyse\n")
            .numberOfArgs(1)
            .build();
    Option m_configFile= Option.builder("c")
            .longOpt("config")
            .type(String.class)
            .desc("Path to config file\n")
            .numberOfArgs(1)
            .build();
    Option m_action = Option.builder("a")
            .longOpt("action")
            .required()
            .type(String.class)
            .desc("Action to make :" +
                    "chose between \n" +
                    " autocrop : crop wide field images\n" +
                    " segmentation : nucleus segmentation\n" +
                    "computeParameters : compute nucleus parameters\n" +
                    "computeParametersDL : compute nucleus parameters DL \n")
            .numberOfArgs(1)
            .build();
    public CLIActionOptions(String[] args)throws Exception   {
        this.m_options.addOption(this.m_imputFolder);
        this.m_options.addOption(this.m_outputFolder);
        this.m_options.addOption(this.m_configFile);
        this.m_options.addOption(this.m_action);
        try {

            this.m_cmd = this.m_parser.parse(this.m_options, args);
        }
        catch (ParseException  exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
    }
    public String  getHelperInfos() {
        return "More details :\n" +
               "java -jar NucleusJ_2-"+NJversion+" -h \n" +
                "or \n"+
                "java -jar NucleusJ_2-"+NJversion+" -help \n";
    }
}