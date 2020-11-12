package gred.nucleus.CLI;

import org.apache.commons.cli.HelpFormatter;

/**
 * Class to generate helper
 */
public class CLIHelper {
    String NJversion ="1.1.0";


    /* Constructor*/
    public CLIHelper(){

    }
    /**
     *  Method get help for OMERO command line
     *  with example command line
     */
    public void OMEROHelp() throws Exception {
        String example_command = "java -jar NucleusJ_2-" + NJversion + ".jar ";
        String example_argument = "-action segmentation " +
                "-input path/to/input/folder/ " +
                "-output path/to/output/folder/ " +
                "-omero " +
                "-hostname omero-server-adress " +
                "-port 0 " +
                "-group 000";
        String[] exemple_OMEROCmd = example_argument.split(" ");
        CLIActionOptionOMERO command = new CLIActionOptionOMERO(exemple_OMEROCmd);
        HelpFormatter formatter = new HelpFormatter();
        System.out.println("ici");

        formatter.printHelp("NucleusJ2.0 OMERO mode: ", command.getM_options());
        System.out.println("\nCommand line exemple : \n\n" +
                example_command + " " + example_argument);
        System.exit(1);
    }

    /**
     *  Method get help for command line
     * with example command line
     */
    public void CmdHelp() throws Exception {
        String example_command = "java -jar NucleusJ_2-" + NJversion + ".jar ";
        String example_argument = "-action segmentation " +
                "-input path/to/input/folder/ " +
                "-output path/to/output/folder/ ";
        String[] exemple_OMEROCmd = example_argument.split(" ");
        CLIActionOptionCmdLine command = new CLIActionOptionCmdLine(exemple_OMEROCmd);
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("NucleusJ2.0 command line mode: ", command.getM_options());
        System.out.println("\nCommand line exemple : \n" +
                example_command + " " + example_argument);
        System.exit(1);
    }
}
