package gred.nucleus.core;

import java.io.File;
import java.util.ArrayList;
import java.util.Map.Entry;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.imageProcess.Thresholding;
import gred.nucleus.segmentation.SegmentationParameters;
import gred.nucleus.utils.FillingHoles;
import gred.nucleus.utils.Gradient;
import gred.nucleus.utils.Histogram;
import ij.*;
import ij.io.FileSaver;
import ij.plugin.ChannelSplitter;
import ij.plugin.Filters3D;
import ij.plugin.GaussianBlur3D;
import ij.process.*;
import ij.measure.*;
import inra.ijpb.binary.ConnectedComponents;
import loci.common.DebugTools;
import loci.plugins.BF;

/**
 * Object segmentation method for 3D images. This segmentation used as initial
 * threshold the method of Otsu, and then select the object maximizing the
 * sphericity of the segmented object.
 *
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NucleusSegmentation {


	/**
	 * Threshold detected by the Otsu modified method
	 */
	private int _bestThreshold = -1;
	/**
	 * volume min of the detected object
	 */
	private int _vMin;
	/**
	 * volume max of the detected object
	 */
	private int _vMax;
	/** String stocking the file name if any nucleus is detected*/
	/**
	 * ImagePlus input to process
	 */
	private ImagePlus _imgRaw;
	/**
	 * ImagePlus input to process
	 */
	private ImagePlus _imgRawTransformed = null;
	/**
	 * Check if the segmentation is not in border
	 */
	private boolean _badCrop = false;
    /**
     * List of the 3D parameters computed associated to the segmented image
     */
	public Measure3D _mesure3D;
    /**
     * Current image analysed
     */
	private File m_currentFile;
    /**
     * String containing the output prefix
     */
	private String m_outputFilesPrefix;
    /**
     * Segmented image
     */
	private ImagePlus[] m_imageSeg;
    /**
     * Segmentation parameters for the analyse
     */
	private SegmentationParameters m_semgemtationParameters;

    /**
     * Constructor for the segmentation analyse for a single image.
     * @param imgRaw raw image to analyse
     * @param vMin minimum volume of detected object
     * @param vMax maximum volume of detected object
     * @param semgemtationParameters list the parameters for the analyse
     * @throws Exception
     */
	public NucleusSegmentation(ImagePlus imgRaw,
                               int vMin,
                               int vMax,
                               SegmentationParameters semgemtationParameters)
            throws Exception {
		this._vMin = vMin;
		this._vMax = vMax;
		this.m_semgemtationParameters = semgemtationParameters;
		this._imgRaw = imgRaw;
		this._imgRaw = getImageChannel(0);
		this._imgRawTransformed = this._imgRaw;


	}

    /**
     * Constructor for the segmentation analyse for a folder containing images.
     * @param imageFile Current image analysed
     * @param outputFilesPrefix prefix for the output file
     * @param semgemtationParameters list the parameters for the analyse
     * @throws Exception
     */
	public NucleusSegmentation(File imageFile,
                               String outputFilesPrefix,
                               SegmentationParameters semgemtationParameters)
            throws Exception {
		this.m_semgemtationParameters = semgemtationParameters;
		this.m_currentFile = imageFile;
		this._imgRaw = getImageChannel(0);
		// TODO ADD CHANNEL PARAMETERS (CASE OF CHANNELS UNSPLITED)
		this._imgRaw.setTitle(imageFile.getName());
		this._imgRawTransformed = this._imgRaw.duplicate();
		this._imgRawTransformed.setTitle(imageFile.getName());
		Directory dirOutputOTSU = new Directory(
		        this.m_semgemtationParameters.getOutputFolder() + "OTSU");
		dirOutputOTSU.CheckAndCreateDir();
		if (this.m_semgemtationParameters.getGiftWrapping()) {
			Directory dirOutputGIFT = new Directory(
			    this.m_semgemtationParameters.getOutputFolder() + "GIFT");
			dirOutputGIFT.CheckAndCreateDir();
		}
	}

    /**
     * Method to set a specific channel image
     * @param channelNumber channel number of the current image to analyse
     * @return channel image
     * @throws Exception
     */

	public ImagePlus getImageChannel(int channelNumber) throws Exception {
		DebugTools.enableLogging("OFF");       // DEBUG INFO BIOFORMAT OFF
		ImagePlus[] currentImage = BF.openImagePlus(
		        this.m_currentFile.getAbsolutePath());
		ChannelSplitter splitter = new ChannelSplitter();
		currentImage = splitter.split(currentImage[channelNumber]);
		return currentImage[0];
	}

    /**
     * Method to save 3D parameters computed
     * @return list of 3D parameter computed
     */
	public String saveImageResult() {
		return this._mesure3D.nucleusParameter3D();

	}
    /**
     * Method to save 3D parameters computed
     * @param imageseg segmented image
     * @return
     */
	public String saveImageResult(ImagePlus[] imageseg) {

		this._mesure3D = new Measure3D(imageseg,
                this._imgRaw, getXcalibration(),
                getYcalibration(),
                getZcalibration());
		return this._mesure3D.nucleusParameter3D();


	}
    /**
     * Method to save segmented image
     * @param imagePlusInput segmented image
     * @param pathFile path to save the image
     */
	private void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
    /**
     * Compute of the first threshold of input image with the method of Otsu.
     * From this initial value we will seek the better segmentation possible:
     * for this we will take the voxels value superior at the threshold value of
     * method of Otsu : Then we compute the standard deviation of this values
     * voxel > threshold value determines which allows range of value we will
     * search the better threshodl value :
     * thresholdOtsu - ecartType et thresholdOtsu + ecartType.
     * For each threshold test; we do an opening and a closing, then run the
     * holesFilling methods. To finish we compute the sphericity.
     *
     * The aim of this method is to maximize the sphericity to obtain the
     * segmented object nearest of the biological object.
     *
     * //TODO methode a reecrire y a moyen de faire plus propre mais pas urgent
     *
     * @return ImagePlus Segmented image
     */

	public void findOTSUmaximisingSephericity() throws Exception {

		double imageVolume = getVoxelVolume() * this._imgRaw.getWidth() *
                this._imgRaw.getHeight() * this._imgRaw.getStackSize();
		Gradient gradient = new Gradient(this._imgRaw);
		double bestSphericity = -1;
		ArrayList<Integer> arrayListThreshold = computeMinMaxThreshold(
		        this._imgRawTransformed);  // methode OTSU
		for (int t = arrayListThreshold.get(0); t <= arrayListThreshold.get(1);
             ++t) {
			ImagePlus tempSeg ;
			tempSeg = generateSegmentedImage(this._imgRawTransformed, t);

			tempSeg = ConnectedComponents.computeLabels(tempSeg,
                    26,
                    32);
			if(this.m_semgemtationParameters.getManualParameter()) {
				IJ.run(tempSeg, "Properties...",
                        " unit=µm pixel_width="
                                + this.m_semgemtationParameters.getXCal()
                                + " pixel_height="
                                + this.m_semgemtationParameters.getYCal()
                                + " voxel_depth="
                                + this.m_semgemtationParameters.getZCal());
			}
			else{
				IJ.run(tempSeg, "Properties...",
                        " unit=µm pixel_width="
                                + this._imgRaw.getCalibration().pixelWidth
                                + " pixel_height="
                                + this._imgRaw.getCalibration().pixelHeight
                                + " voxel_depth="
                                + this._imgRaw.getCalibration().pixelDepth);
			}
			ImagePlus[] tempSegPlus=new ImagePlus[1];
			tempSegPlus[0]=tempSeg;
			Measure3D measure3D = new Measure3D(tempSegPlus,
                    this._imgRawTransformed,
                    getXcalibration(),
                    getYcalibration(),
                    getZcalibration());
			deleteArtefact(tempSeg);
			double volume = measure3D.computeVolumeObject2(255);
			boolean firstStack = isVoxelThresholded(tempSeg,
                    255, 0);
			boolean lastStack = isVoxelThresholded(tempSeg,
                    255,
                    tempSeg.getStackSize() - 1);
			if (testRelativeObjectVolume(volume, imageVolume) &&
				volume>=this.m_semgemtationParameters.getM_minVolumeNucleus() &&
				volume<=this.m_semgemtationParameters.getM_maxVolumeNucleus() &&
                    firstStack == false && lastStack == false) {

				double sphericity = measure3D.computeSphericity(volume,
                        measure3D.computeComplexSurface(tempSeg, gradient));
				if (sphericity > bestSphericity) {
					this._bestThreshold = t;
					bestSphericity = sphericity;
					this._bestThreshold = t;
					this.m_imageSeg = tempSegPlus;
					this.m_imageSeg[0].setTitle(
					        this._imgRawTransformed.getTitle());
				}
			}
			measure3D = null;
		}

        if (this._bestThreshold != -1) {
            morphologicalCorrection(this.m_imageSeg[0]);
			checkBorder(this.m_imageSeg[0]);
		}

	}

    /**
     * Pre process ot the raw image :
     *    - Gaussian blur
     *    - LUT application
     * TODO object fonction image transformation
     */
	public void preProcessImage() {

		GaussianBlur3D.blur(this._imgRawTransformed, 0.25, 0.25, 1);
		ImageStack imageStack = this._imgRawTransformed.getStack();
		int max = 0;
		for (int k = 0; k < this._imgRawTransformed.getStackSize(); ++k) {
			for (int b = 0; b < this._imgRawTransformed.getWidth(); ++b) {
				for (int j = 0; j < this._imgRawTransformed.getHeight(); ++j) {
					if (max < imageStack.getVoxel(b, j, k)) {
						max = (int) imageStack.getVoxel(b, j, k);
					}
				}
			}
		}
		IJ.setMinAndMax(this._imgRawTransformed, 0, max);
        IJ.run(this._imgRawTransformed, "Apply LUT", "stack");
		if (this._imgRaw.getType() == ImagePlus.GRAY16) {
			StackConverter stackConverter = new StackConverter(this._imgRawTransformed);
			stackConverter.convertToGray8();
		}
	}

    /**@deprecated
     * Method to apply the semgentation to find the maximum sphericity.
     * @param imagePlusInput
     * @return
     * @throws Exception
     */

	public ImagePlus applySegmentation (ImagePlus imagePlusInput)
            throws Exception {
		double sphericityMax = -1.0;
		double sphericity;
		double volume;
		Calibration calibration = imagePlusInput.getCalibration();
		final double xCalibration = getXcalibration();
		final double yCalibration = getYcalibration();
		final double zCalibration = getZcalibration();
		Measure3D measure3D = new Measure3D();
		Gradient gradient = new Gradient(imagePlusInput);
		final double imageVolume = xCalibration
                *imagePlusInput.getWidth()
                *yCalibration*imagePlusInput.getHeight()
                *zCalibration*imagePlusInput.getStackSize();
		ImagePlus imagePlusSegmented = new ImagePlus();
		ArrayList<Integer> arrayListThreshold =
                computeMinMaxThreshold(imagePlusInput);
		for (int t = arrayListThreshold.get(0) ;
             t <= arrayListThreshold.get(1);
             ++t) {
			ImagePlus imagePlusSegmentedTemp = generateSegmentedImage(
			        imagePlusInput,t);
			imagePlusSegmentedTemp = ConnectedComponents.computeLabels(
			        imagePlusSegmentedTemp, 26, 32);
			deleteArtefact(imagePlusSegmentedTemp);
			imagePlusSegmentedTemp.setCalibration(calibration);
			volume = measure3D.computeVolumeObject(imagePlusSegmentedTemp,
                    255);
			imagePlusSegmentedTemp.setCalibration(calibration);
			boolean firstStack = isVoxelThresholded(imagePlusSegmentedTemp,
                    255,
                    0);
			boolean lastStack = isVoxelThresholded(imagePlusSegmentedTemp,
                    255,
                    imagePlusInput.getStackSize()-1);
			//boolean xyBorder;
			if (testRelativeObjectVolume(volume,imageVolume) &&
					volume >= _vMin &&
					volume <= _vMax && firstStack == false
                    && lastStack == false) {
				sphericity = measure3D.computeSphericity(
				        volume,
                        measure3D.computeComplexSurface(imagePlusSegmentedTemp,
                        gradient));
				if (sphericity > sphericityMax ) {
					_bestThreshold = t;
					sphericityMax = sphericity;
					StackConverter stackConverter = new StackConverter(
					        imagePlusSegmentedTemp );
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
	 * Creation of the nucleus segmented image from a OTSU threshold.
	 *
	 * @param imagePlusInput raw image
	 * @param threshold threshold value for the segmentation
	 * @return segmented image of the nucleus
	 */
	public ImagePlus generateSegmentedImage (ImagePlus imagePlusInput,
                                             int threshold)  {
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
        imagePlusSegmented.setTitle(imagePlusInput.getTitle());
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
	 * Method to check if the final segmented image got pixel on border of the
     * image (filter of partial nucleus).
	 *
	 * @param imagePlusInput Segmented image with the OTSU modified threshold
	 */

	private void checkBorder (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			if((k==0 ) || k == imagePlusInput.getStackSize()-1){
				for (int i = 0; i < imagePlusInput.getWidth(); i++) {
					for (int j = 0; j < imagePlusInput.getHeight(); j++) {
						if (imageStackInput.getVoxel(i, j, k) ==255.0){
							this._badCrop=true;
						}
					}
				}
			}

			for (int i = 0; i < imagePlusInput.getWidth();
                 i+=(imagePlusInput.getWidth())-1) {
				for (int j = 0; j < imagePlusInput.getHeight(); j++) {
					if (imageStackInput.getVoxel(i, j, k) ==255.0){
						this._badCrop=true;
					}
				}
			}
			for (int j = 0; j < imagePlusInput.getHeight();
                 j+=(imagePlusInput.getHeight()-1)) {

				for (int i = 0; i < imagePlusInput.getWidth(); i++) {
					if (imageStackInput.getVoxel(i, j, k) == 255.0) {
						this._badCrop = true;
					}
				}
			}
		}
	}

	/**
	 *Method to check if the nucleus is truncated (last slice or border).
	 * @return True if the nucleus is partial
	 */

	public boolean getBadCrop (){
		return this._badCrop;
	}



	/**
	 * Determine of the minimum and the maximum value o find the better
     * threshold value.
	 *
	 * @param imagePlusInput raw image
	 * @return array lis which contain at the index 0 the min valu and index 1 the max value
	 *
	 */
	private ArrayList<Integer> computeMinMaxThreshold(ImagePlus imagePlusInput){
		ArrayList<Integer> arrayListMinMaxThreshold = new ArrayList<Integer>();
        Thresholding thresholding = new Thresholding();
        int threshold = thresholding.computeOtsuThreshold(imagePlusInput);
		StackStatistics stackStatistics = new StackStatistics(imagePlusInput);
		double stdDev =stackStatistics.stdDev ;
		double min = threshold - stdDev*2;
		double max = threshold + stdDev/2;
		if ( min < 0) {
            arrayListMinMaxThreshold.add(6);
        }
		else {
            arrayListMinMaxThreshold.add((int) min);
        }
		arrayListMinMaxThreshold.add((int)max);
		return arrayListMinMaxThreshold;
	}

	/**
	 * Determines the number of pixel on the stack index in input return true if
     * the number of pixel>=10.
	 *
	 * @param imagePlusSegmented ImagePlus segmented image
	 * @param threshold int number of pixel
	 * @param stackIndex index of the slice of interest
	 * @return boolean true if the nb of pixel is > to threshold else false
	 */
	private boolean isVoxelThresholded (ImagePlus imagePlusSegmented,
                                        int threshold,
                                        int stackIndex) {
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
	 * Compute closing with the segmented image.
	 *
	 * @param imagePlusInput image segmented
	 */
	private void computeClosing (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput,
                Filters3D.MAX,
                1,
                1,
                (float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput,
                Filters3D.MIN,
                1,
                1,
                (float)0.5);
		imagePlusInput.setStack(imageStackInput);
	}

	/**
	 * Compute opening with the segmented image
	 *
	 * @param imagePlusInput image segmented
	 */
	private void computeOpening (ImagePlus imagePlusInput) {
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		imageStackInput = Filters3D.filter(imageStackInput,
                Filters3D.MIN,
                1,
                1,
                (float)0.5);
		imageStackInput = Filters3D.filter(imageStackInput,
                Filters3D.MAX,
                1,
                1,
                (float)0.5);
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
	 * Method to detected if the object is superior or equal at 70% of the image
     * return false.
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
	 * Detection of the label of the biggest object segmented in the image
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
    /**
     * Method to get X calibration if it's present in parameters of analyse or
     * get the metadata of the image.
     * TODO verifier cette methode si elle est au bonne endroit
     * @return X calibration
     */
	public double getXcalibration() {
		double xCal;
		if (this.m_semgemtationParameters.m_manualParameter == true) {
			xCal = this.m_semgemtationParameters.getXCal();
		}
		else {
			xCal = this._imgRawTransformed.getCalibration().pixelWidth;
		}
		return xCal;
	}
    /**
     * Method to get Y calibration if it's present in parameters of analyse or
     * get the metadata of the image.
     * TODO verifier cette methode si elle est au bonne endroit
     * @return Y calibration
     */
	public double getYcalibration(){
		double yCal;
		if(this.m_semgemtationParameters.m_manualParameter==true){
			yCal=this.m_semgemtationParameters.getYCal();
		}
		else{
			yCal=this._imgRawTransformed.getCalibration().pixelHeight;
		}
		return yCal;
	}
    /**
     * Method to get Y calibration if it's present in parameters of analyse or
     * get the metadata of the image.
     * TODO verifier cette methode si elle est à ca place
     * @return Z calibration
     */
	public double getZcalibration(){
		double zCal;
		if(this.m_semgemtationParameters.getManualParameter()==true){
			zCal=this.m_semgemtationParameters.getZCal();
		}
		else{
			zCal=this._imgRawTransformed.getCalibration().pixelDepth;
		}
		return zCal;
	}
    /**
     * Method to compute the voxel volume : if it's present in parameters of
     * analyse or get the metadata of the image.
     * TODO verifier cette methode si elle est à ca place
     * @return Z calibration
     */
    public double getVoxelVolume(){
        double calibration=0;
        if(this.m_semgemtationParameters.m_manualParameter==true){
            calibration=m_semgemtationParameters.getVoxelVolume();
        }
        else{
            Calibration cal = this._imgRawTransformed.getCalibration();
            calibration= cal.pixelDepth*cal.pixelWidth*cal.pixelHeight;
        }

        return calibration ;
    }
    /**
     * Method to move bad crop (truncated nucleus) to badcrop folder.
     * @param inputPathDir folder of the input to create badcrop folder.
     * TODO verifier cette methode si elle est à ca place
     */
    public void checkBadCrop(String inputPathDir){
    	if((this._badCrop) || (this.getBestThreshold()==-1)){
			File file = new File(inputPathDir);
            File BadCropFolder= new File(inputPathDir
                    +file.separator
                    +"BadCrop");
            System.out.println("et du coup on est dedans ou quoi ...........\n"+BadCropFolder+
".....................................");

            if (!BadCropFolder.exists()){
                BadCropFolder.mkdir();
			}
			File fileToMove = new File(inputPathDir
                    +file.separator
                    +this._imgRawTransformed.getTitle());
			fileToMove.renameTo(new File(BadCropFolder+file.separator+this._imgRawTransformed.getTitle()));
		}
	}
    /**
     * Method to save the OTSU segmented image.
     * TODO verifier cette methode si elle est à ca place
     */
	public void saveOTSUSegmented(){
        if(getBadCrop()==false && getBestThreshold() != -1) {
			String pathSegOTSU = this.m_semgemtationParameters.getOutputFolder() + "OTSU" + this.m_currentFile.separator + this.m_imageSeg[0].getTitle();
			saveFile(this.m_imageSeg[0], pathSegOTSU);

		}
	}
    /**
     * Method to save the OTSU segmented image.
     * TODO verifier cette methode si elle est à ca place
     */
	public void saveGiftWrappingSeg(){

		if(getBadCrop()==false && getBestThreshold() != -1
                && this.m_semgemtationParameters.getGiftWrapping()) {
			ConvexHullSegmentation nuc = new ConvexHullSegmentation();
			this.m_imageSeg[0] = nuc.run(this.m_imageSeg[0]
                    , this.m_semgemtationParameters);
			String pathSegGIFT = this.m_semgemtationParameters.getOutputFolder()
                    + "GIFT" + this.m_currentFile.separator
                    + this._imgRaw.getTitle();
			this.m_imageSeg[0].setTitle(pathSegGIFT);
			saveFile(this.m_imageSeg[0], pathSegGIFT);
        }
	}
    /**
     * Method to get the parameter of the 3D parameters for OTSU segmented image
     * if the object can't be segmented return -1 for
     * parameters.
     * TODO verifier cette methode si elle est à ca place
     * @return
     */
    public String getImageCropInfoOTSU(){
        if(getBadCrop()==false && getBestThreshold() != -1) {
            return saveImageResult(this.m_imageSeg)
                    + "\t"
                    + this._bestThreshold
                    + "\n";
        }
        else {
            return this._imgRaw.getTitle()
                    + "\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1"
                    + "\n";
        }
    }
    /**
     * Method to get the parameter of the 3D parameters for Gift wrapping
     * segmented image if the object can't be segmented return -1 for
     * parameters.
     * TODO verifier DUPLICATION DE getImageCropInfoOTSU
     * TODO a ca place ?
     *
     * @return
     */
    public String getImageCropInfoGIFT(){
        if(getBadCrop()==false && getBestThreshold() != -1) {

            return saveImageResult(this.m_imageSeg)
                    + "\t"
                    + this._bestThreshold
                    + "\n";
        }
        else {
            return this._imgRaw.getTitle()
                    + "\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1\t-1"
                    + "\n";
        }
    }
}