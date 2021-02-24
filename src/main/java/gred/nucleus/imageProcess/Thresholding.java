package gred.nucleus.imageProcess;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.process.*;

public class Thresholding {
	
	
	public Thresholding() {
	}
	
	
	/**
	 * Compute the initial threshold value from OTSU method
	 *
	 * @param imagePlusInput raw image
	 *
	 * @return OTSU threshold TODO STRUCTURES PROBABLY NEEDED
	 */
	public int computeOtsuThreshold(ImagePlus imagePlusInput) {
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int[]           tHistogram      = imageStatistics.histogram;
		return autoThresholder.getThreshold(AutoThresholder.Method.Otsu, tHistogram);
	}
	
	
	/**
	 * TODO COMMENT !!!! 2D 3D
	 *
	 * @param imagePlusInput
	 *
	 * @return
	 */
	public ImagePlus contrastAnd8bits(ImagePlus imagePlusInput) {
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.setUseStackHistogram(true);
		enh.setProcessStack(true);
		enh.stretchHistogram(imagePlusInput.getProcessor(), 0.05);
		if (imagePlusInput.getNSlices() > 1) { // 3D
			StackConverter stackConverter = new StackConverter(imagePlusInput);
			stackConverter.convertToGray8();
		} else { // 2D
			ImageConverter imageConverter = new ImageConverter(imagePlusInput);
			imageConverter.convertToGray8();
		}
		return imagePlusInput;
		
	}
}

