package gred.nucleus.plugins;

import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.dialogs.AutocropConfigDialog;
import gred.nucleus.dialogs.AutocropDialog;
import gred.nucleus.exceptions.fileInOut;
import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;

public class Autocrop_ implements PlugIn {
    /**
     *Run method for imageJ plugin for the autocrop
     * @param arg use by imageJ
     */
    @Override
    public void run(String arg) {

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
            } else {
                try {
                    IJ.log("Begin Autocrop process ");

                    if(autocropDialog.getConfigMode()==2) {
                        if(config==null || config.equals("")) {
                            IJ.error("Config file is missing");
                        }
                        else {
                            IJ.log("Config file");
                            runAutoCropFolder(input, output, config);
                        }
                    } else if(autocropDialog.getConfigMode()==1){
                        AutocropConfigDialog acd = autocropDialog.getAutocropConfigFileDialog();
                        if(acd.isCalibSelected()) {
                            IJ.log("w/ calib");
                            runAutoCropFolder(input, output,
                                    acd.getxCalibration(), acd.getyCalibration(), acd.getzCalibration(),
                                    acd.getxCropBoxSize(), acd.getyCropBoxSize(), acd.getzCropBoxSize(),
                                    acd.getSlicesOTSUcomputing(), acd.getThresholdOTSUComputing(), acd.getChannelToComputeThreshold(),
                                    acd.getMinVolume(), acd.getMaxVolume(),
                                    acd.getBoxesPercentSurfaceToFilter(), acd.getBoxesRegroupementSelected()
                            );
                        }
                        else {
                            IJ.log("w/out calib");
                            runAutoCropFolder(input, output,
                                    acd.getxCropBoxSize(), acd.getyCropBoxSize(), acd.getzCropBoxSize(),
                                    acd.getSlicesOTSUcomputing(), acd.getThresholdOTSUComputing(), acd.getChannelToComputeThreshold(),
                                    acd.getMinVolume(), acd.getMaxVolume(),
                                    acd.getBoxesPercentSurfaceToFilter(), acd.getBoxesRegroupementSelected()
                            );
                        }
                    } else {
                        IJ.log("w/out config");
                        runAutoCropFolder(input, output);
                    }

                    IJ.log("\nAutocrop process has ended successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void runAutoCropFolder(String imageSource, String output, String pathToConfig) throws IOException, fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSource,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        File file = new File(imageSource);
        if(file.isDirectory())
            autoCrop.runFolder();
        else if(file.isFile())
            autoCrop.runFile(imageSource);
    }
    public static void runAutoCropFolder(String imageSource, String output) throws IOException , fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSource,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        File file = new File(imageSource);
        if(file.isDirectory())
            autoCrop.runFolder();
        else if(file.isFile())
            autoCrop.runFile(imageSource);
    }
    public static void runAutoCropFolder(String imageSource, String output,
                                         String xCropBoxSize,String yCropBoxSize,String zCropBoxSize,
                                         String slicesOTSUcomputing,String thresholdOTSUcomputing,String channelToComputeThreshold,
                                         String minVolumeNucleus,String maxVolumeNucleus,
                                         String boxesPercentSurfaceToFilter, boolean boxesRegroupement) throws IOException , fileInOut,Exception {

        AutocropParameters autocropParameters= new AutocropParameters(imageSource,output,
                Integer.parseInt(xCropBoxSize),Integer.parseInt(yCropBoxSize),Integer.parseInt(zCropBoxSize),
                Integer.parseInt(slicesOTSUcomputing),Integer.parseInt(thresholdOTSUcomputing),Integer.parseInt(channelToComputeThreshold),
                Integer.parseInt(minVolumeNucleus),Integer.parseInt(maxVolumeNucleus),
                Integer.parseInt(boxesPercentSurfaceToFilter),boxesRegroupement);

        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        File file = new File(imageSource);
        if(file.isDirectory())
            autoCrop.runFolder();
        else if(file.isFile())
            autoCrop.runFile(imageSource);
    }
    public static void runAutoCropFolder(String imageSource, String output,
                                         String xCal,String yCal,String zCal,
                                         String xCropBoxSize,String yCropBoxSize,String zCropBoxSize,
                                         String slicesOTSUcomputing,String thresholdOTSUcomputing,String channelToComputeThreshold,
                                         String minVolumeNucleus,String maxVolumeNucleus,
                                         String boxesPercentSurfaceToFilter, boolean boxesRegroupement) throws IOException , fileInOut,Exception {

        AutocropParameters autocropParameters= new AutocropParameters(imageSource,output,
                Integer.parseInt(xCal),Integer.parseInt(yCal),Integer.parseInt(zCal),
                Integer.parseInt(xCropBoxSize),Integer.parseInt(yCropBoxSize),Integer.parseInt(zCropBoxSize),
                Integer.parseInt(slicesOTSUcomputing),Integer.parseInt(thresholdOTSUcomputing),Integer.parseInt(channelToComputeThreshold),
                Integer.parseInt(minVolumeNucleus),Integer.parseInt(maxVolumeNucleus),
                Integer.parseInt(boxesPercentSurfaceToFilter),boxesRegroupement);

        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        File file = new File(imageSource);
        if(file.isDirectory())
            autoCrop.runFolder();
        else if(file.isFile())
            autoCrop.runFile(imageSource);
    }
}
