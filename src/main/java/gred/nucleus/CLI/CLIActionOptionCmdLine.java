package gred.nucleus.CLI;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;

import static org.apache.commons.lang.Validate.isTrue;

/**
 * class to handle command line option
 */

public class CLIActionOptionCmdLine extends CLIActionOptions {

    /**
     * Path to output folder
     */
    private Option m_outputFolder= Option.builder("out")
            .longOpt("output")
            .type(String.class)
            .desc("Path to output results\n")
            .numberOfArgs(1)
            .build();
    /**
     * Path to second input folder
     * Need in specific action
     */
    private Option m_inputFolder2= Option.builder("in2")
            .longOpt("input2")
            .required()
            .type(String.class)
            .numberOfArgs(1)
            .build();
    /**
     * Path to second input folder
     * Need in specific action
     */
    private Option m_inputFolder3= Option.builder("in3")
            .longOpt("input3")
            .required()
            .type(String.class)
            .numberOfArgs(1)
            .build();
    /**
     *
     * @param args command line argument
     * @throws Exception ParseException
     */
    public CLIActionOptionCmdLine( String[] args) throws Exception {
        super( args);
        this.m_action.setDescription(this.m_action.getDescription()+"\n"+
            "computeParameters : compute paramaters \n"+
            "computeParametersDL : compute parameters for machine leaning\n"+
            "generateProjection : generate projection from coodinates\n"+
            "CropFromCoordinate : crop wide-field image from coordinate\n"+
            "GenerateOverlay : generate overlay from images \n");

        checkSpecificOptions();
        try {
            this.m_cmd = this.m_parser.parse(this.m_options, args);
            isTrue(CMDAvailableAction(this.m_cmd.getOptionValue("action")));

        }
        catch (ParseException exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
        catch (Exception exp) {
            System.out.println("Action option \""+
                    this.m_cmd.getOptionValue("action")+
                    "\" not available"+ "\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     * Method to check action parameter
     * @param action nucleusJ2.0 action to run
     * @return boolean existing action
     */
    private static boolean  CMDAvailableAction(String action) {
        ArrayList<String > actionAvailableInOmero= new ArrayList<>();
        actionAvailableInOmero.add("autocrop");
        actionAvailableInOmero.add("segmentation");
        actionAvailableInOmero.add("computeParameters");
        actionAvailableInOmero.add("computeParametersDL");
        actionAvailableInOmero.add("generateProjection");
        actionAvailableInOmero.add("generateProjectionFiletered");
        actionAvailableInOmero.add("CropFromCoordinate");
        actionAvailableInOmero.add("GenerateOverlay");
        return actionAvailableInOmero.contains(action);
    }

    /**
     * Method to check specific action parameters
     */
    private void checkSpecificOptions() {
        switch(this.m_cmd.getOptionValue("action")) {
            case "autocrop":
            case "segmentation":
                this.m_inputFolder.setDescription(
                        "Path to input folder containing images to analyse\n");
                this.m_options.addOption(this.m_outputFolder);
                break;

            case "computeParameters":
                this.m_inputFolder.setDescription(
                        "Path to input folder containing RAW images\n");
                this.m_inputFolder2.setDescription(
                        "Path to input folder containing SEGMENTED images\n");
                this.m_options.addOption(this.m_inputFolder2);
                this.m_omero.setDescription("NOT AVAILABLE");
                break;

            case "computeParametersDL":
                this.m_inputFolder.setDescription(
                    "Path to input folder containing RAW images\n");
                this.m_inputFolder2.setDescription(
                    "Path to input folder containing machine leaning SEGMENTED images\n");
                this.m_options.addOption(this.m_inputFolder2);
                this.m_omero.setDescription("NOT AVAILABLE");
                break;

            case "generateProjection":
                this.m_inputFolder.setDescription(
                    "Path to input folder containing coordinates files\n");
                this.m_inputFolder2.setDescription(
                    "Path to input folder containing raw data\n");
                this.m_options.addOption(this.m_inputFolder2);
                this.m_omero.setDescription("NOT AVAILABLE");
                break;

            case "generateProjectionFiletered":
                this.m_inputFolder.setDescription(
                    "Path to input folder containing coordinates files\n");
                this.m_inputFolder2.setDescription(
                    "Path to input folder containing kept images after segmentation filter\n");
                this.m_inputFolder3.setDescription(
                    "Path to input folder containing initial Zprojection\n");
                this.m_options.addOption(this.m_inputFolder2);
                this.m_options.addOption(this.m_inputFolder3);
                this.m_omero.setDescription("NOT AVAILABLE");
                break;

            case "CropFromCoordinate":
                this.m_inputFolder.setDescription(
                    "Path to tabulated file containing 2 columns :\n" +
                    "pathToCoordinateFile   pathToRawImageAssociate\n");
                this.m_omero.setDescription("NOT AVAILABLE");
                break;

            case "GenerateOverlay":
                this.m_inputFolder.setDescription(
                    "Path to tabulated file containing 2 columns :\n" +
                    "pathToOverlayFile pathToRawImageAssociate\n");
                this.m_omero.setDescription("NOT AVAILABLE");
                break;
        }

    }

}