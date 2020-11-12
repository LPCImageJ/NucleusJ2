package gred.nucleus.CLI;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.ParseException;

import java.util.ArrayList;

/**
 * class to handle command line option
 */

public class CLIActionOptionCmdLine extends CLIActionOptions {

    /**
     * @param args command line argument
     * @throws Exception
     */
    public CLIActionOptionCmdLine( String[] args) throws Exception {
        super( args);
        this.m_imputFolder.setDescription(
                "Path to input folder containing images to analyse\n");
        this.m_action.setDescription(this.m_action.getDescription()+
                "computeParameters : compute nucleus parameters\n" +
                "computeParametersDL : compute nucleus parameters DL \n");
        try {

            this.m_cmd = this.m_parser.parse(this.m_options, args);
        }
        catch (ParseException exp){
            System.out.println(exp.getMessage()+"\n");
            System.out.println(getHelperInfos());
            System.exit(1);
        }
    }

    /**
     * Method to check action parameter
     * @param action nucleusJ2.0 action to run
     * @return boolean existing action
     */
    public static boolean  OMEROAvailableAction(String action) {
        ArrayList<String > actionAvailableInOmero= new ArrayList<>();
        actionAvailableInOmero.add("autocrop");
        actionAvailableInOmero.add("segmentation");
        actionAvailableInOmero.add("computeParameters");
        actionAvailableInOmero.add("computeParametersDL");
        actionAvailableInOmero.add("generateProjection");
        actionAvailableInOmero.add("CropFromCoordinate");
        actionAvailableInOmero.add("GenerateOverlay");
        return actionAvailableInOmero.contains(action);
    }


}