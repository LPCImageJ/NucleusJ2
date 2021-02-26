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
	
	public static void computeNucleusParameters(String RawImageSourceFile,
	                                            String SegmentedImagesSourceFile,
	                                            String pathToConfig)
	throws Exception {
		PluginParameters pluginParameters =
				new PluginParameters(RawImageSourceFile, SegmentedImagesSourceFile, pathToConfig);
		Directory directoryInput = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> rawImages = directoryInput.m_fileList;
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalyseParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			ImagePlus   Raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			
			
			Measure3D measure3D = new Measure3D(Segmented,
			                                    Raw,
			                                    pluginParameters.getXCalibration(Raw),
			                                    pluginParameters.getYCalibration(Raw),
			                                    pluginParameters.getZCalibration(Raw));
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString());
		
	}
	
	
	public static void computeNucleusParameters(String RawImageSourceFile, String SegmentedImagesSourceFile)
	throws Exception {
		PluginParameters pluginParameters = new PluginParameters(RawImageSourceFile, SegmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> rawImages = directoryInput.m_fileList;
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalyseParameters() + getResultsColumnNames());
		for (File currentFile : rawImages) {
			System.out.println("current File " + currentFile.getName());
			
			ImagePlus   Raw       = new ImagePlus(currentFile.getAbsolutePath());
			ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			Measure3D measure3D = new Measure3D(Segmented,
			                                    Raw,
			                                    pluginParameters.getXCalibration(Raw),
			                                    pluginParameters.getYCalibration(Raw),
			                                    pluginParameters.getZCalibration(Raw));
			
			outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\tNA").append("\n");
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString());
		
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
	
	
	public static int recomputeOTSU(ImagePlus _Raw, ImagePlus _Segmented) {
		int        OTSUThreshold = Integer.MAX_VALUE;
		ImageStack imageStackRaw = _Raw.getStack();
		ImageStack imageStackSeg = _Segmented.getStack();
		for (int k = 0; k < _Raw.getStackSize(); ++k) {
			for (int i = 0; i < _Raw.getWidth(); ++i) {
				for (int j = 0; j < _Raw.getHeight(); ++j) {
					if ((imageStackSeg.getVoxel(i, j, k) == 255) &&
					    (OTSUThreshold >= imageStackRaw.getVoxel(i, j, k))) {
						OTSUThreshold = (int) (imageStackRaw.getVoxel(i, j, k));
					}
				}
			}
		}
		return OTSUThreshold;
	}
	
}

