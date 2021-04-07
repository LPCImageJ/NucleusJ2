package gred.nucleus.machineLearningUtils;

import gred.nucleus.core.Measure3D;
import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.OutputTextFile;
import gred.nucleus.plugins.PluginParameters;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.plugins.BF;

import java.io.File;
import java.util.ArrayList;


public class ComputeNucleiParametersML {
	String rawImagesInputDirectory;
	String segmentedImagesDirectory;
	
	
	/**
	 * Constructor
	 *
	 * @param rawImagesInputDirectory  path to raw images
	 * @param segmentedImagesDirectory path to list of segmented images from machine learning associated to raw
	 */
	public ComputeNucleiParametersML(String rawImagesInputDirectory, String segmentedImagesDirectory) {
		this.rawImagesInputDirectory = rawImagesInputDirectory;
		this.segmentedImagesDirectory = segmentedImagesDirectory;
	}
	
	
	/**
	 * Filter connected connected component if more then 1 nuclei
	 *
	 * @param imagePlusInput
	 * @param threshold
	 *
	 * @return
	 */
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
					if (voxelValue >= 1) {
						imageStackSegmented.setVoxel(i, j, k, 255);
					} else {
						imageStackSegmented.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		return imagePlusSegmented;
		
	}
	
	
	/**
	 * Run parameters computation parameters see Measure3D
	 *
	 * @throws Exception
	 */
	public void run() throws Exception {
		PluginParameters pluginParameters =
				new PluginParameters(this.rawImagesInputDirectory, this.segmentedImagesDirectory);
		Directory directoryInput = new Directory(pluginParameters.getOutputFolder());
		directoryInput.listImageFiles(pluginParameters.getOutputFolder());
		directoryInput.checkIfEmpty();
		ArrayList<File> segImages = directoryInput.fileList;
		StringBuilder outputCropGeneralInfoOTSU =
				new StringBuilder(pluginParameters.getAnalysisParameters() + getResultsColumnNames());
		for (File currentFile : segImages) {
			System.out.println("current File " + currentFile.getName());
			ImagePlus raw = new ImagePlus(pluginParameters.getInputFolder() +
			                              directoryInput.getSeparator() +
			                              currentFile.getName());
			ImagePlus[] segmented = BF.openImagePlus(pluginParameters.getOutputFolder() + currentFile.getName());
			// TODO TRANSFORMATION FACTORISABLE AVEC METHODE DU DESSUS !!!!!
			segmented[0] = generateSegmentedImage(segmented[0], 1);
			segmented[0] = BinaryImages.componentsLabeling(segmented[0], 26, 32);
			LabelImages.removeBorderLabels(segmented[0]);
			segmented[0] = generateSegmentedImage(segmented[0], 1);
			Histogram histogram = new Histogram();
			histogram.run(segmented[0]);
			if (histogram.getNbLabels() > 0) {
				Measure3D measure3D = new Measure3D(segmented,
				                                    raw,
				                                    pluginParameters.getXCalibration(raw),
				                                    pluginParameters.getYCalibration(raw),
				                                    pluginParameters.getZCalibration(raw));
				outputCropGeneralInfoOTSU.append(measure3D.nucleusParameter3D()).append("\n");
			}
		}
		
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(pluginParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse.csv");
		resultFileOutputOTSU.saveTextFile(outputCropGeneralInfoOTSU.toString(), true);
		
	}
	
	
	/**
	 * @return columns names for results
	 */
	public String getResultsColumnNames() {
		return "NucleusFileName\t" +
		       "Volume\t" +
		       "Flatness\t" +
		       "Elongation\t" +
		       "Sphericity\t" +
		       "Esr\t" +
		       "SurfaceArea\t" +
		       "SurfaceAreaCorrected\t" +
		       "SphericityCorrected\t" +
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
	
}
