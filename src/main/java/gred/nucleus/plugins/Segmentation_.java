package gred.nucleus.plugins;

import gred.nucleus.dialogs.SegmentationConfigDialog;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.dialogs.SegmentationDialog;
import ij.IJ;
import ij.plugin.PlugIn;

import java.io.File;
import java.io.IOException;

public class Segmentation_ implements PlugIn {
    /**
     *Run method for imageJ plugin for the segmentation
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
        SegmentationDialog segmentationDialog = new SegmentationDialog();
        while(segmentationDialog.isShowing()){
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}
        }
        if (segmentationDialog.isStart()) {
            String input = segmentationDialog.getInput();
            String output = segmentationDialog.getOutput();
            String config = segmentationDialog.getConfig();
            if(input==null || input.equals("")) {
                IJ.error("Input file or directory is missing");
            } else if(output==null || output.equals("")) {
                IJ.error("Output directory is missing");
            } else {
                try {
                    IJ.log("Begin Segmentation process ");

                    if(segmentationDialog.getConfigMode()==2) {
                        if(config==null || config.equals("")) {
                            IJ.error("Config file is missing");
                        }
                        else {
                            IJ.log("Config file");
                            segmentationFolder(input, output, config);
                        }
                    } else if(segmentationDialog.getConfigMode()==1){
                        SegmentationConfigDialog scd = segmentationDialog.getSegmentationConfigFileDialog();
                        if(scd.isCalibSelected()) {
                            IJ.log("w/ calib"+
                                    "\nx: "+scd.getxCalibration()+
                                    "\ny: "+scd.getyCalibration()+
                                    "\nz: "+scd.getzCalibration());
                            segmentationFolder(input, output,
                                    scd.getxCalibration(), scd.getyCalibration(), scd.getzCalibration(),
                                    scd.getMinVolume(), scd.getMaxVolume(), scd.getGiftWrapping()
                            );
                        }
                        else {
                            IJ.log("w/out calib");
                            segmentationFolder(input, output,
                                    scd.getMinVolume(), scd.getMaxVolume(), scd.getGiftWrapping()
                            );
                        }
                    } else {
                        IJ.log("w/out config");
                        segmentationFolder(input, output);
                    }

                    IJ.log("\nSegmentation process has ended successfully");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void segmentationFolder(String inputDirectory, String outputDirectory ) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            File file = new File(inputDirectory);
            String log = "";
            if(file.isDirectory())
                log = otsuModif.runSeveralImages2();
            else if(file.isFile())
                log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFolder(String inputDirectory, String outputDirectory ,String config) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory,config);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            File file = new File(inputDirectory);
            String log = "";
            if(file.isDirectory())
                log = otsuModif.runSeveralImages2();
            else if(file.isFile())
                log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFolder(String inputDirectory, String outputDirectory, String minVolume, String maxVolume, boolean isGiftWrapping) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory,
                Integer.parseInt(minVolume),Integer.parseInt(maxVolume),isGiftWrapping);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            File file = new File(inputDirectory);
            String log = "";
            if(file.isDirectory())
                log = otsuModif.runSeveralImages2();
            else if(file.isFile())
                log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFolder(String inputDirectory, String outputDirectory, String xCal, String yCal, String zCal,
                                          String minVolume, String maxVolume, boolean isGiftWrapping) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory,
                Integer.parseInt(minVolume),Integer.parseInt(maxVolume),isGiftWrapping);
        segmentationParameters.setXCal(Integer.parseInt(xCal));
        segmentationParameters.setYCal(Integer.parseInt(yCal));
        segmentationParameters.setZCal(Integer.parseInt(zCal));
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            File file = new File(inputDirectory);
            String log = "";
            if(file.isDirectory())
                log = otsuModif.runSeveralImages2();
            else if(file.isFile())
                log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }
}
