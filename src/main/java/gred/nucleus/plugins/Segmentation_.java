package gred.nucleus.plugins;

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
            } else if(segmentationDialog.isConfigBoxSelected()) {
                if(config==null || config.equals(""))
                    IJ.error("Config file is missing");
            } else {
                try {
                    IJ.log("Begin Segmentation process ");

                    if(segmentationDialog.isConfigBoxSelected()) {
                        segmentationFolder(input, output, config);
                    } else if(new File(input).isDirectory()) {
                        segmentationFolder(input, output);
                    } else {
                        segmentationFile(input, output);
                    }

                    IJ.log("Segmentation process has ended successfully");
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
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFolder(String inputDirectory, String outputDirectory ,String config) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory,config);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }

    public static void segmentationFile(String inputDirectory, String outputDirectory) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory,outputDirectory);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runOneImage(inputDirectory);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }
}
