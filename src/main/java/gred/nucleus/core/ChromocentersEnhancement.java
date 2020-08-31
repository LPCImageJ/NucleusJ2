package gred.nucleus.core;
import gred.nucleus.myGradient.MyGradient;
import gred.nucleus.utils.RegionalExtremaFilter;
import ij.io.FileSaver;
import ij.measure.*;
import ij.*;
import ij.process.*;
import inra.ijpb.binary.ConnectedComponents;
import inra.ijpb.watershed.Watershed;

import java.io.File;

/**
 * Several method to create the image of contrasted regions
 * 
 * @author Tristan Dubos and Axel Poulet

 */
public class ChromocentersEnhancement {

    /**
     *
     */
	public ChromocentersEnhancement (){
	}


	/**
	 * compute and create the image contrast with the raw image and the segmented image
	 *  
	 * @param imagePlusRaw	raw image
	 * @param imagePlusSegmented segmented image of the nucleus
	 * @return image of the cotrasted region
	 */

	public ImagePlus applyEnhanceChromocenters(ImagePlus imagePlusRaw,ImagePlus imagePlusSegmented) {
		MyGradient myGradient = new MyGradient (imagePlusRaw,imagePlusSegmented);
		ImagePlus imagePlusGradient = myGradient.run();
		RegionalExtremaFilter regionalExtremaFilter = new RegionalExtremaFilter();
	    regionalExtremaFilter.setMask(imagePlusSegmented);
	    ImagePlus imagePlusExtrema = regionalExtremaFilter.applyWithMask( imagePlusGradient);
	    ImagePlus imagePlusLabels = ConnectedComponents.computeLabels(imagePlusExtrema, 26, 32);
	    ImagePlus imagePlusWatershed = Watershed.computeWatershed(imagePlusGradient,imagePlusLabels,imagePlusSegmented, 26,false,false);
		imagePlusWatershed=tmp(imagePlusWatershed);
		double [] contrast = computeContrast (imagePlusRaw,imagePlusWatershed);
		return computeImage (imagePlusWatershed, contrast);
	}
	public static void saveFile ( ImagePlus imagePlus, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlus);
		File file = new File(pathFile);
		if (file.exists())
			fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
		else {
			file.mkdir();
			fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
		}
	}

	/**
	 * Compute the region adjacency graph. The aim is to detect the  neighboring region.
	 * 
	 * @param imagePlusWatershed image results of the watershed
	 * @return a float table which contain the value of the contrast between each region
	 */
	private double [][] getRegionAdjacencyGraph (ImagePlus imagePlusWatershed) {
		int voxelValue;
		int neighborVoxelValue;
		ImageStatistics imageStatistics = new StackStatistics(imagePlusWatershed);
		double [][] tRegionAdjacencyGraph = new double [(int)imageStatistics.histMax + 1] [(int)imageStatistics.histMax + 1];
		Calibration calibration = imagePlusWatershed.getCalibration();
		double volumeVoxel = calibration.pixelWidth * calibration.pixelHeight * calibration.pixelDepth;
		ImageStack imageStackWatershed = imagePlusWatershed.getStack();
		for (int k = 1; k < imagePlusWatershed.getNSlices()-1; ++k)
			for (int i = 1; i < imagePlusWatershed.getWidth()-1; ++i)
				for (int j = 1; j < imagePlusWatershed.getHeight()-1; ++j)
				{
					voxelValue = (int)imageStackWatershed.getVoxel(i,j,k);
					for (int kk = k-1; kk <= k+1; kk += 2){
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(i,j,kk);
						System.out.println(i+" "+j+" "+kk+" "+neighborVoxelValue +" "+voxelValue);

						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							tRegionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
					for (int jj = j-1; jj <= j+1; jj += 2){
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(i,jj,k);
						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							tRegionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
					for (int ii = i-1; ii <= i+1; ii += 2){
						neighborVoxelValue = (int) imageStackWatershed.getVoxel(ii,j,k);
						if (neighborVoxelValue > 0 && voxelValue != neighborVoxelValue)
							tRegionAdjacencyGraph[voxelValue][neighborVoxelValue] += volumeVoxel;
					}
				}
		return tRegionAdjacencyGraph;
	}

	/**
	 * Compute the contrasts between neighboring region.
	 * 
	 * @param imagePlusRaw raw image
	 * @param imagePlusRegions imag of the contrasted regions
	 * @return table of contrast
	 */
	
	private double [] computeContrast (ImagePlus imagePlusRaw,ImagePlus imagePlusRegions) {
		double [][] tRegionAdjacencyGraph = getRegionAdjacencyGraph(imagePlusRegions);
		double [] tMean = computeMeanIntensity (imagePlusRaw,imagePlusRegions);
		double [] tContrast= new double [tRegionAdjacencyGraph.length+1];
		double neighborVolumeTotal;
		for(int i = 1; i < tRegionAdjacencyGraph.length; ++i) {
			neighborVolumeTotal = 0;
			for(int j = 1; j < tRegionAdjacencyGraph[i].length; ++j) {
				if (tRegionAdjacencyGraph[i][j] > 0 && i != j) {
					tContrast[i] +=  tRegionAdjacencyGraph[i][j]*(tMean[i]-tMean[j]);
					neighborVolumeTotal += tRegionAdjacencyGraph[i][j];
				}
			}
			if (tContrast[i] <= 0)
			    tContrast[i] = 0;
			else
			    tContrast[i] = tContrast[i] / neighborVolumeTotal;
		}
		return tContrast;
    }


	/**
	 * Compute the mean of value voxel for each region
	 * 
	 * @param imagePlusInput ImagePlus raw image
	 * @param imagePlusWatershed ImagePlus of the results of the watershed
	 * @return table of double of average intensity for each watershed label
	 */
	
	private double [] computeMeanIntensity (ImagePlus imagePlusInput,ImagePlus imagePlusWatershed) {
		ImageStatistics imageStatistics = new StackStatistics(imagePlusWatershed);
		ImageStack imageStackWatershed = imagePlusWatershed.getStack();
		ImageStack imageStackInput = imagePlusInput.getStack();
		double [] tIntensityTotal = new double [(int)imageStatistics.histMax + 1];
		double [] tIntensityMean = new double [(int)imageStatistics.histMax + 1];
		int [] tNbVoxelInEachRegion = new int [(int)imageStatistics.histMax + 1];
		int voxelValue;
		for (int k = 0; k < imagePlusWatershed.getNSlices(); ++k) {
            for (int i = 0; i < imagePlusWatershed.getWidth(); ++i) {
                for (int j = 0; j < imagePlusWatershed.getHeight(); ++j) {
                    voxelValue = (int) imageStackWatershed.getVoxel(i, j, k);
                    if (voxelValue > 0) {
                        tIntensityTotal[voxelValue] += imageStackInput.getVoxel(i, j, k);
                        ++tNbVoxelInEachRegion[voxelValue];
                    }
                }
            }
        }
		for (int i = 1; i < tIntensityTotal.length; ++i)
			tIntensityMean[i] = tIntensityTotal[i] / tNbVoxelInEachRegion [i];
		return tIntensityMean;
	}

	/**
	 * Creation of the image of contrasted regions
	 * 
	 * @param imagePlusInput ImagePlus raw image
	 * @param tVoxelValue table of double of the mean region value
	 * @return ImagePlus image contrast
	 */
	private ImagePlus computeImage (ImagePlus imagePlusInput, double [] tVoxelValue) {
		double voxelValue;
		ImagePlus imagePlusContrast = imagePlusInput.duplicate();
		ImageStack imageStackConstrast = imagePlusContrast.getStack();
		for (int k = 0; k < imagePlusContrast.getNSlices(); ++k) {
            for (int i = 0; i < imagePlusContrast.getWidth(); ++i) {
                for (int j = 0; j < imagePlusContrast.getHeight(); ++j) {
                    voxelValue = imageStackConstrast.getVoxel(i, j, k);
                    if (voxelValue > 0)
                        imageStackConstrast.setVoxel(i, j, k, tVoxelValue[(int) voxelValue]);
                }
            }
        }
		return imagePlusContrast;
	}

	/**
	 * Passe les valeurs de -1 a 0 suite à la nouvelle version de MorpholibJ
	 *
	 * @param imagePlusInput
	 * @return
	 */
	private ImagePlus tmp (ImagePlus imagePlusInput) {
		double voxelValue;
		ImagePlus imagePlusContrast = imagePlusInput.duplicate();
		ImageStack imageStackConstrast = imagePlusContrast.getStack();
		for (int k = 0; k < imagePlusContrast.getNSlices(); ++k) {
			for (int i = 0; i < imagePlusContrast.getWidth(); ++i) {
				for (int j = 0; j < imagePlusContrast.getHeight(); ++j) {
					if (imageStackConstrast.getVoxel(i, j, k)==-1.0){
						imageStackConstrast.setVoxel(i, j, k,0.0);
					}
				}
			}
		}
		return imagePlusContrast;
	}
}