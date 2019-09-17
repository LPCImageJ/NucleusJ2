package gred.nucleus.test;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.segmentation.SegmentationCalling;

import loci.formats.FormatException;
//import org.apache.commons.cli.Options;

import java.io.IOException;
import java.util.ArrayList;



public class main {


    static ArrayList <String> m_test;

    /**
     * Method to run autocrop with only input output folder and with default parameters which are:
     *
     * xCropBoxSize:40
     * yCropBoxSize:40
     * zCropBoxSize:20
     * thresholdOTSUcomputing:20
     * slicesOTSUcomputing:0
     * channelToComputeThreshold:1
     * maxVolumeNucleus:2147483647
     * minVolumeNucleus:1
     *
     * @param imageSourceFile : path to the image's folder
     * @param output : path to output folder analysis
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCrop(String imageSourceFile, String output) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.run();
    }

    /**
     * Method to run autocrop with input folder, output folder and with config file analysis:
     * @param imageSourceFile :: path to the image's folder
     * @param output : path to output folder analysis
     * @param pathToConfig : path to config file
     * @throws IOException
     * @throws FormatException
     * @throws fileInOut
     * @throws Exception
     */
    public static void runAutoCrop(String imageSourceFile, String output, String pathToConfig) throws IOException, FormatException ,fileInOut,Exception{
        AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output,pathToConfig);
        AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
        autoCrop.run();
    }

    public static void segmentation(String input, String output, short vMin, int vMax, boolean gift ) throws FormatException {
        SegmentationCalling otsuModif = new SegmentationCalling(input, output, vMin, vMax);
        try {
            String log = otsuModif.runSeveralImages(gift);
            if(!(log.equals("")))
                System.out.println("Nuclei which didn't pass the segmentation\n"+log);
        }catch (IOException e) { e.printStackTrace();}
    }



    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
      //  Options options = new Options();
        if(args[0].equals("autocrop")) {
            System.out.println("start "+args[0]);
            String inputOneImageAxel = "/home/plop/Bureau/image/wideField/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s4.TIF";
            String inputDirAxel = "/home/plop/Bureau/image/wideField/";
            String outputAxel = "/home/plop/Bureau/image/wideField/test";

            String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/test_outpout/Z_Col_cot15&19&23__w11_DAPI_SIM_s5.tif";
            //String inputOneImageTristan = "/home/tridubos/Bureau/AUTOCROP_TEST/raw/Z_c1c4_cot11&12&13-_w11 DAPI SIM variable_s9.TIF";
            String inputDirTristan = "/home/tridubos/Bureau/Bille_4Micro_02-2019/AutocropDuSchnaps/";
            String outputTristan = "/home/tridubos/Bureau/Bille_4Micro_02-2019/OutputDuSchnaps/";
            if((args.length==2)&& (args[3].equals("ConfigFile"))){
                runAutoCrop(args[1], args[2], args[4]);

            }
            else{
                runAutoCrop(args[1], args[2]);
            }

        }
        else if(args[0].equals("segmentation")) {
            System.out.println("start "+args[0]);
            segmentation(args[1], args[2]+"/OTSU/" , (short)1.0, 300000000,false);
            segmentation(args[1], args[2]+"/GIFT/", (short)6.0, 300000000,true);
        }
        else{
            System.out.println("Argument le premier argument doit être   autocrop  ou   segmentation");
            System.out.println("\nExemples :");
            System.out.println("\njava NucleusJ_giftwrapping.jar autocrop dossier/raw/ dossier/out/");
            System.out.println("\njava NucleusJ_giftwrapping.jar segmentation dossier/raw/ dossier/out/");
            System.out.println("\n\n");
        }
        System.err.println("The program ended normally.");
    }
}

//IJ.log(""+getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber() +" image type " +imgSeg.getType()+"\n");

//long maxMemory = Runtime.getRuntime().maxMemory();
//System.out.println("Maximum memory (bytes): " +(maxMemory == Long.MAX_VALUE ? "no limit" : maxMemory*1e-9));