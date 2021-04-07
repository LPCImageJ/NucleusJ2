package gred.nucleus.mains;

import gred.nucleus.core.Measure3D;
import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import ij.ImagePlus;
import ij.ImageStack;
import loci.common.DebugTools;
import loci.plugins.BF;

import java.io.File;
import java.util.ArrayList;


public class ComputeSegmentationParameters {
	
	public static void computeNucleusParameters(String rawImageSourceFile,
	                                            String segmentedImagesSourceFile,
	                                            String pathToConfig)
	throws Exception {
		PluginParameters pluginParameters =
				new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile, pathToConfig);
		Directory directoryInput = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> rawImages = directoryInput.fileList;
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			ImagePlus   raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			
			
			Measure3D measure3D = new Measure3D(segmented,
			                                    raw,
			                                    pluginParameters.getXCalibration(raw),
			                                    pluginParameters.getYCalibration(raw),
			                                    pluginParameters.getZCalibration(raw));
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
		
	}
	
	
	public static void computeNucleusParameters(String rawImageSourceFile, String segmentedImagesSourceFile)
	throws Exception {
		PluginParameters pluginParameters = new PluginParameters(rawImageSourceFile, segmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> rawImages = directoryInput.fileList;
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			System.out.println("current File " + currentFile.getName());
			
			ImagePlus   raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			Measure3D measure3D = new Measure3D(segmented,
			                                    raw,
			                                    pluginParameters.getXCalibration(raw),
			                                    pluginParameters.getYCalibration(raw),
			                                    pluginParameters.getZCalibration(raw));
			
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\tNA").append("\n");
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
		
	}
	
	
	public static void main(String[] args) throws Exception {
		DebugTools.enableLogging("OFF");
		computeNucleusParameters(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/GIFT");
	}
	
	
	public static String getResultsColumnNames() {
		return "NucleusFileName\t" +
		       "Volume\t" +
		       "Flatness\t" +
		       "Elongation\t" +
		       "Esr\t" +
		       "SurfaceArea\t" +
		       "Sphericity\t" +
		       "MeanIntensityNucleus\t" +
		       "MeanIntensityBackground\t" +
		       "StandardDeviation\t" +
		       "MinIntensity\t" +
		       "MaxIntensity\t" +
		       "MedianIntensityImage\t" +
		       "MedianIntensityNucleus\t" +
		       "MedianIntensityBackground\t" +
		       "ImageSize\t" +
		       "OTSUThreshold\n";
	}
	
	
	public static int recomputeOTSU(ImagePlus raw, ImagePlus segmented) {
		int        otsuThreshold = Integer.MAX_VALUE;
		ImageStack imageStackRaw = raw.getStack();
		ImageStack imageStackSeg = segmented.getStack();
		for (int k = 0; k < raw.getStackSize(); ++k) {
			for (int i = 0; i < raw.getWidth(); ++i) {
				for (int j = 0; j < raw.getHeight(); ++j) {
					if ((imageStackSeg.getVoxel(i, j, k) == 255) &&
					    (otsuThreshold >= imageStackRaw.getVoxel(i, j, k))) {
						otsuThreshold = (int) (imageStackRaw.getVoxel(i, j, k));
					}
				}
			}
		}
		return otsuThreshold;
	}
	
}

