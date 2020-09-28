package gred.nucleus.mains;

import gred.nucleus.MachineLeaningUtils.ComputeNucleiParametersML;
import gred.nucleus.core.ChromocentersEnhancement;
import gred.nucleus.dialogs.ChromocenterSegmentationPipelineBatchDialog;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.ChromocenterSegmentationBatchPlugin_;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.utils.FileList;
import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.GaussianBlur3D;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class test {

    public static void main(String[] args) throws IOException, FormatException, fileInOut, Exception {
        //    segmentationFolder("/home/titus/Bureau/TEST_NJ/TEST_ANALYSE/raw", "/home/titus/Bureau/TEST_NJ/TEST_ANALYSE/SEG_OTSU_GIFT");
        //computeNucleusParametersDL("/home/tridubos/MACHINE_LEARNING/TEST_APPLY_MODEL/RAW_DONE/","/home/tridubos/MACHINE_LEARNING/TEST_APPLY_MODEL/PREDICTION/");
        //segmentationFolder("/home/tridubos/Bureau/TEST/Raw", "/home/tridubos/Bureau/TEST//home/tridubos/Bureau/TEST/BALEC");
  //      tmp();
        /*
        ChromocenterSegmentationPipelineBatchDialog _chromocenterSegmentationPipelineBatchDialog = new ChromocenterSegmentationPipelineBatchDialog();
        while( _chromocenterSegmentationPipelineBatchDialog.isShowing()) {
            try {Thread.sleep(1);}
            catch (InterruptedException e) {e.printStackTrace();}

        }

        System.out.println(_chromocenterSegmentationPipelineBatchDialog.getxCalibration2());
*/


    }


    public static void segmentationFolder(String input, String output) throws Exception {
        SegmentationParameters segmentationParameters = new SegmentationParameters(input, output);
        SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
        try {
            String log = otsuModif.runSeveralImages2();
            if (!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n" + log);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void computeNucleusParametersDL(String rawImagesInputDirectory, String segmentedImagesDirectory) throws IOException, FormatException, fileInOut, Exception {
        ComputeNucleiParametersML computeParameters = new ComputeNucleiParametersML(rawImagesInputDirectory, segmentedImagesDirectory);
        computeParameters.run();
    }

    public static void tmp() {
        FileList fileList = new FileList();
        File[] tFileRawData = fileList.run("/home/tridubos/Bureau/TEST/burp");
        if (fileList.isDirectoryOrFileExist(".+RawDataNucleus.+", tFileRawData) &&
                fileList.isDirectoryOrFileExist(".+SegmentedDataNucleus.+", tFileRawData)) {
            double xCalibration = 0.102;
            double yCalibration = 0.102;
            double zCalibration = 0.2;
            String unit = "µm";
            ArrayList<String> arrayListImageSegmenetedDataNucleus = fileList.fileSearchList(".+SegmentedDataNucleus.+", tFileRawData);
            String workDirectory = "/home/tridubos/Bureau/TEST/burp";
            for (int i = 0; i < arrayListImageSegmenetedDataNucleus.size(); ++i) {
                IJ.log("image" + (i + 1) + " / " + arrayListImageSegmenetedDataNucleus.size());
                String pathImageSegmentedNucleus = arrayListImageSegmenetedDataNucleus.get(i);
                String pathNucleusRaw = pathImageSegmentedNucleus.replaceAll("SegmentedDataNucleus", "RawDataNucleus");
                IJ.log(pathNucleusRaw);
                if (fileList.isDirectoryOrFileExist(pathNucleusRaw, tFileRawData)) {
                    ImagePlus imagePlusSegmented = IJ.openImage(pathImageSegmentedNucleus);
                    ImagePlus imagePlusInput = IJ.openImage(pathNucleusRaw);
                    GaussianBlur3D.blur(imagePlusInput,0.25,0.25,1);
                    ImageStack imageStack= imagePlusInput.getStack();
                    int max = 0;
                    for(int k = 0; k < imagePlusInput.getStackSize(); ++k)
                        for (int b = 0; b < imagePlusInput.getWidth(); ++b )
                            for (int j = 0; j < imagePlusInput.getHeight(); ++j){
                                if (max < imageStack.getVoxel(b, j, k)){
                                    max = (int) imageStack.getVoxel(b, j, k);
                                }
                            }
                    IJ.setMinAndMax(imagePlusInput, 0, max);
                    IJ.run(imagePlusInput, "Apply LUT", "stack");
                    Calibration calibration = new Calibration();
                    calibration.pixelDepth = zCalibration;
                    calibration.pixelWidth = xCalibration;
                    calibration.pixelHeight = yCalibration;
                    calibration.setUnit(unit);
                    imagePlusSegmented.setCalibration(calibration);
                    imagePlusInput.setCalibration(calibration);
                    ChromocentersEnhancement chromocenterSegmentation = new ChromocentersEnhancement();
                    ImagePlus imagePlusConstraste = chromocenterSegmentation.applyEnhanceChromocenters(imagePlusInput, imagePlusSegmented);
                    imagePlusConstraste.setTitle(imagePlusInput.getTitle());
                    saveFile(imagePlusConstraste, workDirectory + File.separator + "ConstrastDataNucleus");
                }
            }
            IJ.log("End of the chromocenter segmentation , the results are in " + "/home/tridubos/Bureau/TEST/burp");
        } else
            IJ.showMessage("There are no the two subdirectories (See the directory name) or subDirectories are empty");
    }
    public static void saveFile ( ImagePlus imagePlus, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlus);
        File file = new File(pathFile);
        if (file.exists())
            fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
        else {
            file.mkdir();
            fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
        }
    }

}
