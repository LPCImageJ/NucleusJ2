package gred.nucleus.mains;

import gred.nucleus.files.Directory;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NoiseComputing {
	
	
	public static void main(String[] args) {
		computeMeanNoise(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW/",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/TMP_PARAMETERS/"
		                );
	}
	
	
	public static void computeMeanNoise(String rawImageSourceFile, String segmentedImagesSourceFile) {
		PluginParameters pluginParameters = new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getOutputFolder());
		directoryInput.listImageFiles(pluginParameters.getOutputFolder());
		directoryInput.checkIfEmpty();
		List<File>    segImages   = directoryInput.fileList;
		StringBuilder resultNoise = new StringBuilder("NucleusFileName\tMeanNoise\n");
		for (File currentFile : segImages) {
			System.out.println("current File " + currentFile.getName());
			ImagePlus raw = new ImagePlus(pluginParameters.getInputFolder() +
			                              directoryInput.getSeparator() +
			                              currentFile.getName());
			ImagePlus segmented = new ImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			
			// TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
			double meanNoise = meanIntensityNoise(raw, segmented);
			resultNoise.append(currentFile.getName()).append("\t")
			           .append(meanNoise).append("\t")
			           .append(medianComputing(raw)).append("\n");
			System.out.println("Noise mean " + meanNoise);
			
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/NoiseGIFT.csv");
		resultFileOutputOTSU.saveTextFile(resultNoise.toString(), true);
	}
	
	
	private static double meanIntensityNoise(ImagePlus raw, ImagePlus segmented) {
		double     meanIntensity = 0;
		int        voxelCounted  = 0;
		ImageStack imageStackRaw = raw.getStack();
		ImageStack imageStackSeg = segmented.getStack();
		for (int k = 0; k < raw.getStackSize(); ++k) {
			for (int i = 0; i < raw.getWidth(); ++i) {
				for (int j = 0; j < raw.getHeight(); ++j) {
					if (imageStackSeg.getVoxel(i, j, k) == 0) {
						meanIntensity += imageStackRaw.getVoxel(i, j, k);
						voxelCounted++;
					}
				}
			}
		}
		meanIntensity /= voxelCounted;
		return meanIntensity;
		
	}
	
	
	public static double medianComputing(ImagePlus raw) {
		double    voxelMedianValue = 0;
		Histogram histogram        = new Histogram();
		histogram.run(raw);
		
		HashMap<Double, Integer> segmentedNucleusHistogram = histogram.getHistogram();
		
		int medianElementStop = (raw.getHeight() * raw.getWidth() * raw.getNSlices()) / 2;
		int increment         = 0;
		
		for (Map.Entry<Double, Integer> entry : segmentedNucleusHistogram.entrySet()) {
			increment += entry.getValue();
			if (increment > medianElementStop) {
				voxelMedianValue = entry.getKey();
				break;
			}
		}
		return voxelMedianValue;
	}
	
}













