package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTextFile;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import loci.formats.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class noiseComputing {


    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
        computeMeanNoise(
                "/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW/",
                "/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/TMP_PARAMETERS/"
        );
    }
    public static void computeMeanNoise(String RawImageSourceFile, String SegmentedImagesSourceFile) throws IOException, FormatException ,fileInOut,Exception{
        PluginParameters pluginParameters= new PluginParameters(RawImageSourceFile,SegmentedImagesSourceFile);
        Directory directoryInput = new Directory(pluginParameters.getOutputFolder());
        directoryInput.listImageFiles(pluginParameters.getOutputFolder());
        directoryInput.checkIfEmpty();
        ArrayList<File> segImages =directoryInput.m_listeOfFiles;
        String ResultNoise="NucleusFileName\tMeanNoise\n";
        for (short i = 0; i < segImages.size(); ++i) {
            File currentFile = segImages.get(i);
            System.out.println("current File "+currentFile.getName());
            ImagePlus Raw = new ImagePlus(pluginParameters.getInputFolder()+directoryInput.getSeparator()+currentFile.getName());
            ImagePlus Segmented = new ImagePlus(pluginParameters.getOutputFolder()+currentFile.getName());

            // TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
            double meanNoise= meanIntensityNoise(Raw,Segmented);
            ResultNoise=ResultNoise+currentFile.getName()+"\t"+meanNoise+"\t"+medianComputing(Raw)+"\n";
            System.out.println("Noise mean "+meanNoise);

        }
        OutputTextFile resultFileOutputOTSU=new OutputTextFile(
                "/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/NoiseGIFT.csv");
        resultFileOutputOTSU.SaveTextFile( ResultNoise);
    }


    private static double meanIntensityNoise (ImagePlus _Raw,ImagePlus _Segmented){
        double meanIntensity=0;
        int voxelCounted=0;
        ImageStack imageStackRaw = _Raw.getStack();
        ImageStack imageStackSeg = _Segmented.getStack();
        for(int k = 0; k < _Raw.getStackSize(); ++k) {
            for (int i = 0; i < _Raw.getWidth(); ++i) {
                for (int j = 0; j < _Raw.getHeight(); ++j) {
                    if(imageStackSeg.getVoxel(i, j, k)==0){
                        meanIntensity+=imageStackRaw.getVoxel(i, j, k);
                        voxelCounted++;
                    }

                }
            }
        }
        meanIntensity=meanIntensity/voxelCounted;
        return meanIntensity;

    }
    public static double medianComputing(ImagePlus _Raw){
        double voxelMedianValue=0;
        Histogram histogram = new Histogram();
        histogram.run(_Raw);
        HashMap< Double, Integer> _segmentedNucleusHisto =histogram.getHistogram();
        int medianElementStop= (_Raw.getHeight()*_Raw.getWidth()*_Raw.getNSlices())/2;
        int increment=0;
        for (HashMap.Entry<Double,Integer  > entry :  _segmentedNucleusHisto.entrySet()) {
            increment+=entry.getValue();
            if(increment>medianElementStop){
                voxelMedianValue=entry.getKey();
                break;
            }


        }
        return voxelMedianValue;
    }
}













