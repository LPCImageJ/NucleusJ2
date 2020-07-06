package gred.nucleus.plugins;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.dialogs.AutocropDialog;
import gred.nucleus.exceptions.fileInOut;
import ij.IJ;
import ij.plugin.PlugIn;

import java.io.IOException;

public class Autocrop_ implements PlugIn {
    /**
     *Run method for imageJ plugin for the autocrop
     * @param arg use by imageJ
     */
    @Override
    public void run(String arg) {
//        ImagePlus img = WindowManager.getCurrentImage();
//        if (null == img) {
//            IJ.noImage();
//            return;
//        }
        if (IJ.versionLessThan("1.32c"))
            return;
        AutocropDialog autocropDialog = new AutocropDialog();
        while(autocropDialog.isShowing()){
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        if (autocropDialog.isStart()) {
            String input = autocropDialog.getInput();
            String output = autocropDialog.getOutput();
            String config = autocropDialog.getConfig();
            if(input==null || input.equals("")) {
                IJ.error("Input file or directory is missing");
            } else if(output==null || output.equals("")) {
                IJ.error("Output directory is missing");
            } else if(autocropDialog.isConfigBoxSelected()) {
                if(config==null || config.equals(""))
                    IJ.error("Config file is missing");
            } else {
                try {
                    IJ.log("Begin Autocrop process ");

                    if(autocropDialog.isConfigBoxSelected()) {
                        runAutoCropFolder(input, output, config);
                    } else {
                        runAutoCropFolder(input, output);
                    }

                    IJ.log("Autocrop process has ended successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void runAutoCropFolder(String imageSourceFile, String output, String pathToConfig) throws IOException, fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }


    public static void runAutoCropFolder(String imageSourceFile, String output) throws IOException , fileInOut,Exception{
        //AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFolder();
    }
    public static void runAutoCropFile(String imageSourceFile, String output) throws IOException , fileInOut,Exception{
        //AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.runFile(imageSourceFile);
    }
}
