package gred.nucleus.core;

import gred.nucleus.utils.Histogram;
import ij.IJ;
import ij.ImagePlus;
import ij.measure.Calibration;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


/**
 * Several method to realise and create the outfile for the nuclear Analysis this class contains the chromocenter
 * parameters
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class NucleusChromocentersAnalysis {
	/**
	 *
	 */
	public NucleusChromocentersAnalysis() {
	}
	
	//TODO INTEGRATION CLASS NEW MEASURE 3D
	
	
	/**
	 * Analysis for one nucleus, the results are stock on the IJ log windows
	 *
	 * @param rhfChoice
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 */
	public void computeParameters(String rhfChoice,
	                              ImagePlus imagePlusInput,
	                              ImagePlus imagePlusSegmented,
	                              ImagePlus imagePlusChromocenter) {
		IJ.log("3D PARAMETERS ");
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		Calibration calibration = imagePlusInput.getCalibration();
		double      voxelVolume = calibration.pixelDepth * calibration.pixelHeight * calibration.pixelWidth;
		Measure3D   measure3D   = new Measure3D();
		double      volume      = measure3D.computeVolumeObject(imagePlusSegmented, 255);
		double      surfaceArea = measure3D.computeComplexSurface();
		String text = imagePlusSegmented.getTitle() + " " + volume + " "
		              + measure3D.equivalentSphericalRadius(volume) + " "
		              + surfaceArea + " "
		              + measure3D.computeFlatnessAndElongation(255)[0] + " "
		              + measure3D.computeFlatnessAndElongation(255)[1] + " "
		              + measure3D.computeSphericity(volume, surfaceArea);
		if (rhfChoice.equals("Volume and intensity")) {
			IJ.log("ImageTitle Volume ESR SurfaceArea Flatness Elongation Sphericity IntensityRHF VolumeRHF NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean VoxelVolume");
			text += " " + measure3D.computeIntensityRHF(imagePlusInput, imagePlusSegmented, imagePlusChromocenter) + " "
			        + measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter) + " ";
		} else if (rhfChoice.equals("Volume")) {
			IJ.log("ImageTitle Volume ESR SurfaceArea Flatness Elongation Sphericity VolumeRHF NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean VoxelVolume");
			text += " " + measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter) + " ";
		} else {
			IJ.log("ImageTitle Volume ESR SurfaceArea Flatness Elongation Sphericity IntensityRHF NbCc VCcMean VCcTotal DistanceBorderToBorderMean DistanceBarycenterToBorderMean VoxelVolume");
			text += " " +
			        measure3D.computeIntensityRHF(imagePlusInput, imagePlusSegmented, imagePlusChromocenter) +
			        " ";
		}
		
		if (histogram.getNbLabels() > 0) {
			double[]       tVolumesObjects = measure3D.computeVolumeOfAllObjects(imagePlusChromocenter);
			RadialDistance radialDistance  = new RadialDistance();
			double[] tBorderToBorderDistance =
					radialDistance.computeBorderToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
			double[] tBarycenterToBorderDistance =
					radialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
			double volumeCcMean = computeMeanOfTable(tVolumesObjects);
			int    nbCc         = measure3D.getNumberOfObject(imagePlusChromocenter);
			text += nbCc + " "
			        + volumeCcMean + " "
			        + volumeCcMean * nbCc + " "
			        + computeMeanOfTable(tBorderToBorderDistance) + " "
			        + computeMeanOfTable(tBarycenterToBorderDistance) + " " + voxelVolume;
		} else {
			text += "0 0 0 NaN NaN " + voxelVolume;
		}
		IJ.log(text);
	}
	
	
	/**
	 * Analysis for several nuclei, the results are stock on output file
	 *
	 * @param pathResultsFile
	 * @param rhfChoice
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @param imagePlusChromocenter
	 *
	 * @throws IOException
	 */
	public void computeParameters(String pathResultsFile,
	                              String rhfChoice,
	                              ImagePlus imagePlusInput,
	                              ImagePlus imagePlusSegmented,
	                              ImagePlus imagePlusChromocenter) throws IOException {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusChromocenter);
		Calibration calibration = imagePlusInput.getCalibration();
		double      voxelVolume = calibration.pixelDepth * calibration.pixelHeight * calibration.pixelWidth;
		ImagePlus[] tmp         = new ImagePlus[1];
		tmp[0] = imagePlusSegmented;
		Measure3D measure3D = new Measure3D(tmp,
		                                    imagePlusInput,
		                                    calibration.pixelDepth,
		                                    calibration.pixelHeight,
		                                    calibration.pixelWidth);
		double  volume      = measure3D.computeVolumeObject(imagePlusSegmented, 255);
		double  surfaceArea = measure3D.computeComplexSurface();
		File    fileResults = new File(pathResultsFile);
		boolean exist       = fileResults.exists();
		try (BufferedWriter bufferedWriterOutput = new BufferedWriter(new FileWriter(fileResults, true))) {
			String text = "";
			if (!exist) {
				if (rhfChoice.equals("Volume and intensity")) {
					text =
							"ImageTitle\tVolume\tESR\tSurfaceArea\tFlatness\tElongation\tSphericity\tIntensityRHF\tVolumeRHF\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tVoxelVolume\n";
				} else if (rhfChoice.equals("Volume")) {
					text =
							"ImageTitle\tVolume\tESR\tSurfaceArea\tFlatness\tElongation\tSphericity\tVolumeRHF\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tVoxelVolume\n";
				} else {
					text =
							"ImageTitle\tVolume\tESR\tSurfaceArea\tFlatness\tElongation\tSphericity\tIntensityRHF\tNbCc\tVCcMean\tVCcTotal\tDistanceBorderToBorderMean\tDistanceBarycenterToBorderMean\tVoxelVolume\n";
				}
			}
			text += imagePlusSegmented.getTitle() + "\t"
			        + volume + "\t"
			        + measure3D.equivalentSphericalRadius(volume) + "\t"
			        + surfaceArea + "\t"
			        + measure3D.computeFlatnessAndElongation(255)[0] + "\t"
			        + measure3D.computeFlatnessAndElongation(255)[1] + "\t"
			        + measure3D.computeSphericity(volume, surfaceArea) + "\t";
			if (rhfChoice.equals("Volume and intensity")) {
				text += measure3D.computeIntensityRHF(imagePlusInput, imagePlusSegmented, imagePlusChromocenter) + "\t";
				text += measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter) + "\t";
			} else if (rhfChoice.equals("intensity")) {
				text += measure3D.computeIntensityRHF(imagePlusInput, imagePlusSegmented, imagePlusChromocenter) + "\t";
			} else {
				text += measure3D.computeVolumeRHF(imagePlusSegmented, imagePlusChromocenter) + "\t";
			}
			if (histogram.getNbLabels() > 0) {
				double[]       tVolumesObjects = measure3D.computeVolumeOfAllObjects(imagePlusChromocenter);
				double         volumeCcMean    = computeMeanOfTable(tVolumesObjects);
				int            nbCc            = measure3D.getNumberOfObject(imagePlusChromocenter);
				RadialDistance radialDistance  = new RadialDistance();
				double[] tBorderToBorderDistance =
						radialDistance.computeBorderToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
				double[] tBarycenterToBorderDistance =
						radialDistance.computeBarycenterToBorderDistances(imagePlusSegmented, imagePlusChromocenter);
				text += nbCc + "\t"
				        + volumeCcMean + "\t"
				        + volumeCcMean * nbCc + "\t"
				        + computeMeanOfTable(tBorderToBorderDistance) + "\t"
				        + computeMeanOfTable(tBarycenterToBorderDistance) + "\t";
			} else {
				text += "0\t0\t0\tNaN\tNaN\t";
			}
			
			text += voxelVolume + "\n";
			bufferedWriterOutput.write(text);
			bufferedWriterOutput.flush();
		}
	}
	
	
	/**
	 * Computes the mean of the value in the table
	 *
	 * @param tInput Table of value
	 *
	 * @return Mean of the table
	 */
	public double computeMeanOfTable(double[] tInput) {
		double mean = 0;
		for (double v : tInput) {
			mean += v;
		}
		mean /= (tInput.length);
		return mean;
	}
	
}