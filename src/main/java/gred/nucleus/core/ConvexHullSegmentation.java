package gred.nucleus.core;

import gred.nucleus.utils.ConvexeHullImageMaker;
import gred.nucleus.utils.VoxelRecord;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.image.BufferedImage;
import java.util.ArrayList;


/**
 * Segmentation using Gift Wrapping analysis on 3D segmented image imputed for the different axis combined :
 * 	 - XY
 * 	 - XZ
 * 	 - YZ
 *
 */

public class ConvexHullSegmentation
{
	
	/**
	 *Run the gift wrapping analysis on 3D segmented image imputed for the different axis combined :
	 *   XY
	 *   XZ
	 *   YZ
	 * 
	 * @param imagePlusInput
	 * Current image segmented analysed
	 * @return segmented image
	 */
	public ImagePlus run(ImagePlus imagePlusInput)
	{
		IJ.log(imagePlusInput.getTitle()+" xy ");
		ConvexeHullImageMaker nuc = new ConvexeHullImageMaker();
		nuc.setAxes("xy");
	   	ImagePlus imagePlusXY = nuc.giftWrapping(imagePlusInput);
	   	IJ.log(imagePlusInput.getTitle()+" xz ");
		imagePlusXY.setTitle("XY ConvexHullSegmentation");

		imagePlusXY.show();

		//imagePlusXY.show();

	   	nuc.setAxes("xz");
	   	ImagePlus imagePlusXZ = nuc.giftWrapping(imagePlusInput);
	   	IJ.log(imagePlusInput.getTitle()+" yz ");
	   	nuc.setAxes("yz");

        imagePlusXZ.show();

        //imagePlusXZ.show();

		ImagePlus imagePlusYZ = nuc.giftWrapping(imagePlusInput);
		
		return imageMakingUnion(imagePlusInput, imagePlusXY, imagePlusXZ, imagePlusYZ);
	}



	/**
	 * Make an union of segmented images from the different plans
	 * @see ConvexHullSegmentation#run(ImagePlus)
	 * @param imagePlusXY
	 * Segmented image in XY dimension
	 * @param imagePlusXZ
	 * Segmented image in XZ dimension
	 * @param imagePlusYZ
	 * Segmented image in YZ dimension
	 *
	 * @return
	 */
	
	public ImagePlus imageMakingUnion (ImagePlus imagePlusInput,ImagePlus  imagePlusXY,ImagePlus imagePlusXZ,ImagePlus imagePlusYZ)
	{
		ImagePlus imagePlusOutput = imagePlusInput.duplicate();
		ImageStack imageStackXY= imagePlusXY.getStack();
		ImageStack imageStackXZ= imagePlusXZ.getStack();
		ImageStack imageStackYZ= imagePlusYZ.getStack();
		ImageStack imageStackOutput = imagePlusOutput.getStack();
		
		for (int k = 0; k < imagePlusXY.getNSlices();++k)
			for (int i = 0; i < imagePlusXY.getWidth(); ++i)
				for (int j = 0; j < imagePlusXY.getHeight();++j)
					if (imageStackXY.getVoxel(i, j, k) != 0 || imageStackYZ.getVoxel(j, k, i) != 0 || imageStackXZ.getVoxel(i, k, j) != 0)	
						if(imageStackOutput.getVoxel(i, j, k) == 0)
							imageStackOutput.setVoxel(i, j, k, 255);
		return imagePlusOutput;
	}

}
