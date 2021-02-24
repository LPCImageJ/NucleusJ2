package gred.nucleus.core;

import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.utils.ConvexeHullImageMaker;
import ij.ImagePlus;
import ij.ImageStack;


/**
 * Segmentation using Gift Wrapping analysis on 3D segmented image imputed for the different axis combined : - XY - XZ -
 * YZ
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class ConvexHullSegmentation {
	
	
	private SegmentationParameters m_segmentationParameters;
	
	/**
	 * Run the gift wrapping analysis on 3D segmented image imputed for the different axis combined : XY XZ YZ
	 *
	 * @param imagePlusInput Current image segmented analysed
	 *
	 * @return segmented image
	 */
	public ImagePlus runGIFTWrapping(ImagePlus imagePlusInput, SegmentationParameters segmentationParameters) {
		ConvexeHullImageMaker nuc = new ConvexeHullImageMaker();
		nuc.setAxes("xy");
		ImagePlus imagePlusXY = nuc.giftWrapping(imagePlusInput, segmentationParameters);
		nuc.setAxes("xz");
		ImagePlus imagePlusXZ = nuc.giftWrapping(imagePlusInput, segmentationParameters);
		nuc.setAxes("yz");
		ImagePlus imagePlusYZ = nuc.giftWrapping(imagePlusInput, segmentationParameters);
		return imageMakingUnion(imagePlusInput, imagePlusXY, imagePlusXZ, imagePlusYZ);
	}
	
	
	/**
	 * Make an union of segmented images from the different plans
	 *
	 * @param imagePlusXY Segmented image in XY dimension
	 * @param imagePlusXZ Segmented image in XZ dimension
	 * @param imagePlusYZ Segmented image in YZ dimension
	 *
	 * @return ImagePlus image results of the gift wrapping
	 *
	 * @see ConvexHullSegmentation#
	 */
	private ImagePlus imageMakingUnion(ImagePlus imagePlusInput,
	                                   ImagePlus imagePlusXY,
	                                   ImagePlus imagePlusXZ,
	                                   ImagePlus imagePlusYZ) {
		ImagePlus  imagePlusOutput  = imagePlusInput.duplicate();
		ImageStack imageStackXY     = imagePlusXY.getStack();
		ImageStack imageStackXZ     = imagePlusXZ.getStack();
		ImageStack imageStackYZ     = imagePlusYZ.getStack();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		
		for (int k = 0; k < imagePlusXY.getNSlices(); ++k) {
			for (int i = 0; i < imagePlusXY.getWidth(); ++i) {
				for (int j = 0; j < imagePlusXY.getHeight(); ++j) {
					if (imageStackXY.getVoxel(i, j, k) != 0 ||
					    imageStackYZ.getVoxel(j, k, i) != 0 ||
					    imageStackXZ.getVoxel(i, k, j) != 0) {
						if (imageStackOutput.getVoxel(i, j, k) == 0) {
							imageStackOutput.setVoxel(i, j, k, 255);
						}
					}
				}
			}
		}
		return imagePlusOutput;
	}
}
