package gred.nucleus.core;

import java.util.ArrayList;
import java.util.Map.Entry;
import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.Gradient;
import gred.nucleus.utils.Histogram;
import ij.*;
import ij.plugin.Filters3D;
import ij.process.*;
import ij.measure.*;
import ij.process.AutoThresholder.Method;
import inra.ijpb.binary.ConnectedComponents;

/**
 * Object segmentation method for 3D images. This segmentation used as initial threshold
 * the method of Otsu, and then maximize he sphericity of the object detected .
 *
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NucleusSegmentation {

    /** Threshold detected by the Otsu modified method*/
	private int _bestThreshold = -1;
    /** volume min of the detected object*/
	private short _vMin;
    /** volume max of the detected object*/
    private  int _vMax;
	/** String stocking the file name if any nucleus is detected*/
	private String _logErrorSeg = "";
    /** ImagePlus input to process*/
    private ImagePlus _imgRaw;
	/** Check if the segmentation is not in border */
    private boolean _badCrop =false;

	/**
	 *
	 */
	public NucleusSegmentation (short vMin, int vMax){
        this._vMin = vMin;
        this._vMax = vMax;
	}



	/**
	 * Compute of the first threshold of input image with the method of Otsu.
	 * From this initial value we will seek the better segmentation possible:
	 * for this we will take the voxels value superior at the threshold value of method of Otsu :
	 * Then we compute the standard deviation of this values voxel > threshold value
	 * determines which allows range of value we will search the better threshodl value :
	 *   thresholdOtsu - ecartType et thresholdOtsu + ecartType.
	 * For each threshold test; we do an opening and a closing, then run the holesFilling methods.
     * To finish we compute the sphericity.
     *
	 * The aim of this method is to maximize the sphericity to obtain the segmented object
	 * nearest of the biological object.
     *
     * //TODO methode a reecrire y a moyen de faire plus propre mais pas urgent
	 *
	 * @param imagePlusInput ImagePlus raw image
	 * @return ImagePlus Segmented image
	 */
	public ImagePlus applySegmentation (ImagePlus imagePlusInput) {
		double sphericityMax = -1.0;
		double sphericity;
		double volume;
		Calibration calibration = imagePlusInput.getCalibration();
		final double xCalibration = calibration.pixelWidth;
		final double yCalibration = calibration.pixelHeight;
		final double zCalibration = calibration.pixelDepth;
		Measure3D measure3D = new Measure3D();
		Gradient gradient = new Gradient(imagePlusInput);
		final double imageVolume = xCalibration*imagePlusInput.getWidth()*yCalibration*imagePlusInput.getHeight()*zCalibration*imagePlusInput.getStackSize();
		ImagePlus imagePlusSegmented = new ImagePlus();
		ArrayList<Integer> arrayListThreshold = computeMinMaxThreshold(imagePlusInput);  // methode OTSU

		for (int t = arrayListThreshold.get(0) ; t <= arrayListThreshold.get(1); ++t) {

			ImagePlus imagePlusSegmentedTemp = generateSegmentedImage(imagePlusInput,t);
			ImagePlus tem =generateSegmentedImage(imagePlusInput,t);

			imagePlusSegmentedTemp = ConnectedComponents.computeLabels(imagePlusSegmentedTemp, 26, 32);


			deleteArtefact(imagePlusSegmentedTemp);

			imagePlusSegmentedTemp.setCalibration(calibration);
			volume = measure3D.computeVolumeObject(imagePlusSegmentedTemp,255);
			imagePlusSegmentedTemp.setCalibration(calibration);
			boolean firstStack = isVoxelThresholded(imagePlusSegmentedTemp,255, 0);
			boolean lastStack = isVoxelThresholded(imagePlusSegmentedTemp,255, imagePlusInput.getStackSize()-1);
			//boolean xyBorder;
			if (testRelativeObjectVolume(volume,imageVolume) &&
					volume >= _vMin &&
					volume <= _vMax && firstStack == false && lastStack == false) {
				sphericity = measure3D.computeSphericity(volume,measure3D.computeComplexSurface(imagePlusSegmentedTemp,gradient));
				if (sphericity > sphericityMax ) {
					_bestThreshold = t;
					sphericityMax = sphericity;
					StackConverter stackConverter = new StackConverter( imagePlusSegmentedTemp );
					stackConverter.convertToGray8();
					imagePlusSegmented= imagePlusSegmentedTemp.duplicate();

				}
			}


		}

		ImageStack imageStackInput = imagePlusSegmented.getImageStack();

		if(_bestThreshold != -1 ) {
			morphologicalCorrection(imagePlusSegmented);
		}

		checkBorder(imagePlusSegmented);

		return imagePlusSegmented;
	}


	/**
	 * Compute the beginig threshold value
	 *
	 * @param imagePlusInput raw image
	 * @return
	 */
	private int computeThreshold (ImagePlus imagePlusInput) {
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int [] tHisto = imageStatistics.histogram;
		return autoThresholder.getThreshold(Method.Otsu,tHisto);
	}

	/**
	 * Creation of the nucleus segmented image
	 *
	 * @param imagePlusInput raw image
	 * @param threshold threshold value for the segmentation
	 * @return segmented image of the nucleus
	 */
	private ImagePlus generateSegmentedImage (ImagePlus imagePlusInput, int threshold) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue >= threshold)
						imageStackSegmented.setVoxel(i, j, k, 255);
					else
						imageStackSegmented.setVoxel(i, j, k, 0);
				}
			}
		}
		return imagePlusSegmented;
	}

	/**
	 * Method to check if the final segmented image got pixel on border of the image
	 * (filter of partial nucleus)
	 *
	 * @param imagePlusInput Segmented image with the OTSU modified threshold
	 */

	private void checkBorder (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			if((k==0 ) || k == imagePlusInput.getStackSize()-1){ // Verification first and last slice
				for (int i = 0; i < imagePlusInput.getWidth(); i++) {
					for (int j = 0; j < imagePlusInput.getHeight(); j++) {
						if (imageStackInput.getVoxel(i, j, k) ==255.0){
							this._badCrop=true;
						}
					}
				}
			}

			for (int i = 0; i < imagePlusInput.getWidth(); i+=(imagePlusInput.getWidth())-1) {
				for (int j = 0; j < imagePlusInput.getHeight(); j++) {
					if (imageStackInput.getVoxel(i, j, k) ==255.0){
						this._badCrop=true;
					}
				}
			}
			for (int j = 0; j < imagePlusInput.getHeight(); j+=(imagePlusInput.getHeight()-1)) {

				for (int i = 0; i < imagePlusInput.getWidth(); i++) {
					if (imageStackInput.getVoxel(i, j, k) == 255.0) {
						this._badCrop = true;
					}
				}
			}
		}
	}

	/**
	 *
	 * @return True if the nucleus is partial
	 */

	public boolean getBadCrop (){
		return this._badCrop;
	}



	/**
	 * Determine of the minimum and the maximum value o find the better threshold value
	 *
	 * @param imagePlusInput raw image
	 * @return array lis which contain at the index 0 the min valu and index 1 the max value
	 *
	 */
	private ArrayList<Integer> computeMinMaxThreshold(ImagePlus imagePlusInput) {
		ArrayList<Integer> arrayListMinMaxThreshold = new ArrayList<Integer>();
		int threshold = computeThreshold (imagePlusInput);
		StackStatistics stackStatistics = new StackStatistics(imagePlusInput);
		double stdDev =stackStatistics.stdDev ;
		double min = threshold - stdDev*2;
		double max = threshold + stdDev/2;
		if ( min < 0)
			arrayListMinMaxThreshold.add(6);
		else
			arrayListMinMaxThreshold.add((int)min);
		arrayListMinMaxThreshold.add((int)max);
		return arrayListMinMaxThreshold;
	}

	/**
	 * Determines the number of pixel on the stack index in input return true if the number of pixel>=10
	 *
	 * @param imagePlusSegmented ImagePlus segmented image
	 * @param threshold int number of pixel
	 * @param stackIndex index of the slice of interest
	 * @return boolean true if the nb of pixel is > to threshold else false
	 */
	private boolean isVoxelThresholded (ImagePlus imagePlusSegmented, int threshold, int stackIndex) {
		boolean voxelThresolded = false;
		int nbVoxelThresholded = 0;
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for (int i = 0; i < imagePlusSegmented.getWidth(); ++i ) {
			for (int j = 0; j < imagePlusSegmented.getHeight(); ++j ) {
				if ( imageStackSegmented.getVoxel(i,j,stackIndex) >= threshold)
					nbVoxelThresholded++;
			}
		}
		if (nbVoxelThresholded >= 10)
			voxelThresolded = true;
		return voxelThresolded;
	}



	/**
	 * 	 method to realise morphological correction (filling holes and top hat)
	 *
	 * @param imagePlusSegmented image to be correct
	 */
	private void morphologicalCorrection (ImagePlus imagePlusSegmented) {
		FillingHoles holesFilling = new FillingHoles();
		int temps =imagePlusSegmented.getBitDepth();
		computeOpening(imagePlusSegmented);
		computeClosing(imagePlusSegmented);
		imagePlusSegmented = holesFilling.apply2D(imagePlusSegmented);
	}


	/**
	 * compute closing with the segmented image
	 *
	 * @param imagePlusInput image segmented
	 */
	private void computeClosing (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}

	/**
	 * compute opening with the segmented image 
	 *
	 * @param imagePlusInput image segmented
	 */
	private void computeOpening (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MIN,1,1,(float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput, Filters3D.MAX,1,1,(float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}


	/**
	 * getter: return the threshold value computed
	 * @return the final threshold value
	 */
	public int getBestThreshold (){
		return _bestThreshold;
	}

	/**
	 * if the detected object is superior or equal at 70% of the image return false
	 *
	 * @param objectVolume double volume of the object
	 * @return boolean if ratio object/image > 70% return false else return true
	 */
	private boolean testRelativeObjectVolume(double objectVolume,double imageVolume) {
		final double ratio = (objectVolume/imageVolume)*100;
		if (ratio >= 70)
			return false;
		else
			return true;
	}


	/**
	 * Keep the bigger object in the image at 255 put the other at 0.
     *
	 * @param imgSeg ImagePlus of the segmented image
	 */
	private void deleteArtefact (ImagePlus imgSeg) {
		double voxelValue;
		double mode = getLabelOfLargestObject(imgSeg);
		ImageStack imageStackInput = imgSeg.getStack();
		for(int k = 0; k < imgSeg.getNSlices(); ++k) {
			for (int i = 0; i < imgSeg.getWidth(); ++i) {
				for (int j = 0; j < imgSeg.getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == mode)
						imageStackInput.setVoxel(i, j, k, 255);
					else
						imageStackInput.setVoxel(i, j, k, 0);
				}
			}
		}
	}

	/**
	 * Detection of the bigger object segmented in the image
	 * @param imgSeg ImagePlus segmented img
	 * @return double the label of the bigger object
	 */
    private double getLabelOfLargestObject(ImagePlus imgSeg) {
		Histogram histogram = new Histogram();
		histogram.run(imgSeg);
		double labelMax = 0;
		double nbVoxelMax = -1;
		for(Entry<Double, Integer> entry : histogram.getHistogram().entrySet()) {
			double label = entry.getKey();
			int nbVoxel = entry.getValue();
			if (nbVoxel > nbVoxelMax) {
				nbVoxelMax = nbVoxel;
				labelMax = label;
			}
		}
		return labelMax;
	}
}