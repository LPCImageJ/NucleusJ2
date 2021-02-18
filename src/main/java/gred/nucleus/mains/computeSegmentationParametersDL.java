package gred.nucleus.mains;

import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.OutputTextFile;
import gred.nucleus.core.Measure3D;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.common.DebugTools;
import loci.plugins.BF;

import java.io.File;
import java.util.ArrayList;

public class computeSegmentationParametersDL {
	
	public static void computeNucleusParameters(String RawImageSourceFile,
	                                            String SegmentedImagesSourceFile,
	                                            String pathToConfig)
	throws Exception {
		PluginParameters pluginParameters =
				new PluginParameters(RawImageSourceFile, SegmentedImagesSourceFile, pathToConfig);
		Directory directoryInput = new Directory(pluginParameters.getInputFolder());
		directoryInput.listImageFiles(pluginParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> rawImages                 = directoryInput.m_listeOfFiles;
		String          outputCropGeneralInfoOTSU = pluginParameters.getAnalyseParameters() + getColnameResult();
		for (File currentFile : rawImages) {
			ImagePlus Raw = new ImagePlus(currentFile.getAbsolutePath());
			System.out.println("current File " + currentFile.getName());
			
			ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			
			Measure3D mesure3D = new Measure3D(Segmented,
			                                   Raw,
			                                   pluginParameters.getXcalibration(Raw),
			                                   pluginParameters.getYcalibration(Raw),
			                                   pluginParameters.getZcalibration(Raw));
			outputCropGeneralInfoOTSU += mesure3D.nucleusParameter3D() + "\n";
		}
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.SaveTextFile(outputCropGeneralInfoOTSU);
		
	}
	
	
	public static void computeNucleusParametersDL(String RawImageSourceFile, String SegmentedImagesSourceFile)
	throws Exception {
		PluginParameters pluginParameters = new PluginParameters(RawImageSourceFile, SegmentedImagesSourceFile);
		Directory        directoryInput   = new Directory(pluginParameters.getOutputFolder());
		directoryInput.listImageFiles(pluginParameters.getOutputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> segImages                 = directoryInput.m_listeOfFiles;
		String          outputCropGeneralInfoOTSU = pluginParameters.getAnalyseParameters() + getColnameResult();
		for (File currentFile : segImages) {
			System.out.println("current File " + currentFile.getName());
			ImagePlus Raw = new ImagePlus(pluginParameters.getInputFolder() +
			                              directoryInput.getSeparator() +
			                              currentFile.getName());
			ImagePlus[] Segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			// TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
			Segmented[0] = generateSegmentedImage(Segmented[0], 1);
			Segmented[0] = BinaryImages.componentsLabeling(Segmented[0], 26, 32);
			LabelImages.removeBorderLabels(Segmented[0]);
			Segmented[0] = generateSegmentedImage(Segmented[0], 1);
			Histogram histogram = new Histogram();
			histogram.run(Segmented[0]);
			if (histogram.getNbLabels() > 0) {
				Measure3D mesure3D = new Measure3D(Segmented,
				                                   Raw,
				                                   pluginParameters.getXcalibration(Raw),
				                                   pluginParameters.getYcalibration(Raw),
				                                   pluginParameters.getZcalibration(Raw));
				outputCropGeneralInfoOTSU += mesure3D.nucleusParameter3D() + "\tNA" + "\n";
			}
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.SaveTextFile(outputCropGeneralInfoOTSU);
		
	}
	
	public static void main(String[] args) throws Exception {
		DebugTools.enableLogging("OFF");
		
		computeNucleusParametersDL(
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/RAW",
				"/media/titus/DATA/ML_ANALYSE_DATA/ANALYSE_COMPARAISON_REANALYSE/129_ANNOTATION_FULL/129_TRIER");
		
		
	}
	
	public static ImagePlus imageSEGTransform(ImagePlus SegmentedTMP) {
		LabelImages.removeBorderLabels(SegmentedTMP);
		return SegmentedTMP;
		
		
	}
	
	public static ImagePlus generateSegmentedImage(ImagePlus imagePlusInput,
	                                               int threshold) {
		ImageStack imageStackInput    = imagePlusInput.getStack();
		ImagePlus  imagePlusSegmented = imagePlusInput.duplicate();
		
		imagePlusSegmented.setTitle(imagePlusInput.getTitle());
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue > 1) {
						imageStackSegmented.setVoxel(i, j, k, 255);
						imageStackInput.getVoxel(i, j, k);
						
					} else {
						imageStackSegmented.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		return imagePlusSegmented;
		
	}
	
	
	public static String getColnameResult() {
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
	
	private static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
}
