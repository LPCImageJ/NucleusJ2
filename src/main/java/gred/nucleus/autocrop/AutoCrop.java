package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.FilesInputOutput.OutputTiff;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.imageProcess.Thresholding;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.GaussianBlur3D;
import loci.common.DebugTools;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import omero.ServerError;
import omero.gateway.exception.DSAccessException;
import omero.gateway.exception.DSOutOfServiceException;
import omero.gateway.model.RectangleData;
import omero.gateway.model.ShapeData;
import omero.model.Length;
import omero.model.enums.UnitsLength;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.FilenameUtils;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.metadata.ROIContainer;
import fr.igred.omero.repository.DatasetContainer;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;

/**
 * Class dedicate to crop nuclei in a isolate file from 3D wide field image from
 * microscopy.
 * The process use a OTSU threshold to detect object on the image. To detect
 * specific object you can use different parameters as filter like
 *  - volume of object detected
 *  - minimum intensity of object detected
 *  - slice used to detect defined OTSU threshold
 *
 * This class output one file per object detected and a tab-delimited file which
 * contains per line the box coordinate of each object.
 *
 * Note : concerning multiple channels images the OTSU threshold is compute on
 * one channel (see ChannelToComputeThreshold) and then boxes coordinate are
 * applied on all channel. You can identify from which channel crop from the
 * file name before file extension you can see C0 for channel 0 for example.
 */

public class AutoCrop {


	/**
	 * File to process (Image input)
	 */
	File m_currentFile;
	/**
	 * Raw image
	 */
	private ImagePlus m_rawImg;
	/**
	 * Segmented image
	 */
	private ImagePlus m_imageSeg;
	/**
	 * Segmented image connect component labelled
	 */
	private ImagePlus m_imageSeg_labelled;
	/**
	 * The path of the image to be processed
	 */
	private String m_imageFilePath;
	/**
	 * the path of the directory where are saved the crop of the object
	 */
	private String m_outputDirPath;
	/**
	 * The prefix of the names of the output cropped images, which are automatically numbered
	 */
	private String m_outputFilesPrefix;
	/**
	 * List of the path of the output files created by the cropKernels method
	 */
	private ArrayList<String> m_outputFile = new ArrayList<String>();
	/**
	 * List of boxes coordinates
	 */
	private ArrayList<String> m_boxCoordinates = new ArrayList<String>();
	/**
	 * Number of channels in current image
	 */
	private int m_channelNumbers = 1;
	/**
	 * Get current info inmage analyse
	 */
	private String m_infoImageAnalyse = "";
	/**
	 * Parameters crop analyse
	 */
	private AutocropParameters m_autocropParameters;
	/**
	 * OTSU threshold  used to compute segmented image
	 */
	private int OTSUthreshold;
	/**
	 * Slice start to compute OTSU
	 */
	private String sliceUsedForOTSU;
	/**
	 * Default threshold
	 */
	private boolean m_defaultTreshold = false;
	/**
	 * List of boxes  to crop link to label value
	 */
	private HashMap<Double, Box> m_boxes = new HashMap<Double, Box>();


	/**
	 * Autocrop constructor : initialisation of analyse parameter
	 *
	 * @param imageFile                 : current image analyse
	 * @param outputFilesPrefix         : prefix use for output file name
	 * @param autocropParametersAnalyse : list of analyse parameter
	 */

	public AutoCrop(File imageFile, String outputFilesPrefix,
					AutocropParameters autocropParametersAnalyse)
			throws IOException, FormatException, fileInOut, Exception {
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_currentFile = imageFile;
		this.m_imageFilePath = imageFile.getAbsolutePath();
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		if(this.m_rawImg.getBitDepth()>8) {
			this.m_imageSeg = thresholding.contrastAnd8bits(
					getImageChannel(
							this.m_autocropParameters.getChannelToComputeThreshold()));
		}
		else{
			this.m_imageSeg=this.m_rawImg;
		}
		this.m_infoImageAnalyse =
				autocropParametersAnalyse.getAnalyseParameters();
	}

	public AutoCrop(ImageContainer image,
					AutocropParameters autocropParametersAnalyse, 
					Client client)
			throws IOException, FormatException, fileInOut, Exception {
		this.m_currentFile = new File(image.getName());
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = FilenameUtils.removeExtension(image.getName());
		setChannelNumbersOmero(image, client);
		if(this.m_rawImg.getBitDepth()>8) {
			this.m_imageSeg = thresholding.contrastAnd8bits(
					getImageChannelOmero(this.m_autocropParameters.getChannelToComputeThreshold(), 
										 image, 
										 client));
		}
		else{
			this.m_imageSeg=this.m_rawImg;
		}
		this.m_infoImageAnalyse =
				autocropParametersAnalyse.getAnalyseParameters();
	}

	public AutoCrop(File imageFile, String outputFilesPrefix,
					AutocropParameters autocropParametersAnalyse,HashMap<Double, Box> _boxes)
			throws IOException, FormatException, fileInOut, Exception {
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_currentFile = imageFile;
		this.m_imageFilePath = imageFile.getAbsolutePath();
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		this.m_imageSeg=this.m_rawImg;
		this.m_infoImageAnalyse =autocropParametersAnalyse.getAnalyseParameters();
		m_boxes=_boxes;
	}




	/**
	 * Method to get specific channel to compute OTSU threshold
	 *
	 * @param channelNumber : number of channel to compute OTSU for crop
	 * @return image of specific channel
	 */
	public ImagePlus getImageChannel(int channelNumber) throws Exception {
		DebugTools.enableLogging("OFF");    // DEBUG INFO BIOFORMAT OFF
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		ChannelSplitter splitter = new ChannelSplitter();
		currentImage = splitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}

	public ImagePlus getImageChannelOmero(int channelNumber, 
										  ImageContainer image, 
										  Client client) 
		throws Exception {
		int cBound[] = {channelNumber, channelNumber};

		return image.toImagePlus(client, null, null, cBound, null, null);
	}


	/**
	 * Method to check multichannel and initialising channelNumbers variable
	 *
	 * @throws Exception
	 */

	public void setChannelNumbers() throws Exception {
		DebugTools.enableLogging("OFF");      // DEBUG INFO BIOFORMAT OFF
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		ChannelSplitter channelSplitter = new ChannelSplitter();
		currentImage = channelSplitter.split(currentImage[0]);
		this.m_rawImg = currentImage[0];
		
		if (currentImage.length > 1) {
			this.m_channelNumbers = currentImage.length;
		}
	}

	public void setChannelNumbersOmero(ImageContainer image, Client client) throws Exception {
		DebugTools.enableLogging("OFF");      // DEBUG INFO BIOFORMAT OFF

		int cBound[] = {0, 0};
		this.m_rawImg  = image.toImagePlus(client, null, null, cBound, null, null);

		this.m_channelNumbers = image.getPixels().getSizeC();
	}


	/**
	 * Method computing OTSU threshold and creating segmented image from this
	 * threshold.
	 * Before OTSU threshold a Gaussian Blur is applied (case of anisotropic
	 * voxels)
	 * TODO add case where voxel are not anisotropic for Gaussian Blur
	 * Case where OTSU threshold is under 20 computation using only half of last
	 * slice (useful in case of top slice with lot of noise)
	 * If OTSU threshold is still under 20 threshold default threshold value is
	 * 20.
	 */
	public void thresholdKernels() {
		this.sliceUsedForOTSU = "default";
		GaussianBlur3D.blur(this.m_imageSeg, 0.5, 0.5, 1);

		Thresholding thresholding = new Thresholding();
		int thresh = thresholding.computeOtsuThreshold(this.m_imageSeg);
		if (thresh < this.m_autocropParameters.getThresholdOTSUcomputing()) {
			ImagePlus imp2;
			if (m_autocropParameters.getSlicesOTSUcomputing() == 0) {
				this.sliceUsedForOTSU = "Start:"
						+ this.m_imageSeg.getStackSize() / 2
						+ "-" + this.m_imageSeg.getStackSize();
				imp2 = new Duplicator().run(
						this.m_imageSeg,
						this.m_imageSeg.getStackSize() / 2,
						this.m_imageSeg.getStackSize());
			} else {
				this.sliceUsedForOTSU = "Start:"
						+ this.m_autocropParameters.getSlicesOTSUcomputing()
						+ "-" + this.m_imageSeg.getStackSize();
				imp2 = new Duplicator().run(
						this.m_imageSeg,
						this.m_autocropParameters.getSlicesOTSUcomputing(),
						this.m_imageSeg.getStackSize());
			}
			int thresh2 = thresholding.computeOtsuThreshold(imp2);
			if (thresh2 < this.m_autocropParameters.getThresholdOTSUcomputing()) {
				thresh = this.m_autocropParameters.getThresholdOTSUcomputing();
				this.m_defaultTreshold = true;
			} else {
				thresh = thresh2;
			}
		}
		this.OTSUthreshold = thresh;
		this.m_imageSeg = this.generateSegmentedImage(this.m_imageSeg, thresh);
	}

	/**
	 * MorpholibJ Method computing connect component using OTSU segmented image
	 */
	public void computeConnectcomponent() {
		this.m_imageSeg_labelled = BinaryImages.componentsLabeling(
				this.m_imageSeg,
				26,
				32);

	}

	/**
	 * Initialize hashMap m_boxes containing component connect pixel value
	 * associate to number of voxels composing it.
	 * Filter connect component based on minimum volume (default 1 ) and maximum
	 * volume (default 2147483647)
	 */

	public void componentSizeFilter() {
		Histogram histogram = new Histogram();
		histogram.run(this.m_imageSeg_labelled);
		histogram.getHistogram();
		HashMap<Double, Integer> parcour = histogram.getHistogram();

		for (Map.Entry<Double, Integer> entry : parcour.entrySet()) {
			Double cle = entry.getKey();
			Integer valeur = entry.getValue();
			if (!((valeur * getVoxelVolume() <
					this.m_autocropParameters.getM_minVolumeNucleus()) ||
					(valeur * getVoxelVolume() >
							this.m_autocropParameters.getM_maxVolumeNucleus())) && valeur > 1) {
				Box initializeBox = new Box(Short.MAX_VALUE, Short.MIN_VALUE,
						Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE,
						Short.MIN_VALUE);
				this.m_boxes.put(cle, initializeBox);

			}
		}
		getNumberOfBox();

	}

	/**
	 * MorpholibJ Method filtering border connect component
	 */
	public void componentBorderFilter() {
		LabelImages.removeBorderLabels(this.m_imageSeg_labelled);
	}

	/**
	 * Detection of the of the bounding box for each object of the image.
	 * A Connected component detection is do on the m_imageThresholding and all
	 * the object on the border and under or upper threshold volume.
	 * are removed. The coordinates allow the implementation of the box objects
	 * which define the bounding box, and these objects are stocked in a
	 * ArrayList.
	 * In order to use with a grey-level image, use either @see #
	 * thresholdKernels() or your own binarisation method.
	 */
	public void computeBoxes2() {
		try {
			ImageStack imageStackInput = this.m_imageSeg_labelled.getStack();
			Box box;
			for (short k = 0; k < this.m_imageSeg_labelled.getNSlices(); ++k) {
				for (short i = 0; i < this.m_imageSeg_labelled.getWidth(); ++i) {
					for (short j = 0;
						 j < this.m_imageSeg_labelled.getHeight(); ++j) {
						if ((imageStackInput.getVoxel(i, j, k) > 0) && (
								this.m_boxes.containsKey(
										imageStackInput.getVoxel(i, j, k)))) {
							box = this.m_boxes.get(
									imageStackInput.getVoxel(i, j, k));
							if (i < box.getXMin())
								box.setXMin(i);
							else if (i > box.getXMax())
								box.setXMax(i);
							if (j < box.getYMin())
								box.setYMin(j);
							else if (j > box.getYMax())
								box.setYMax(j);

							if (k < box.getZMin())
								box.setZMin(k);
							else if (k > box.getZMax())
								box.setZMax(k);
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    /**
     * Method to add X voxels in x y z arround the connected component.
     * X by default is 20 in x y z. Parameter can be modified in autocrop
     * parameters :
     *      -m_xCropBoxSize
     *      -m_yCropBoxSize
     *      -m_zCropBoxSize
     *
     */

	public void addCROP_parameter()throws Exception{
		for (int y =0 ;y<this.m_channelNumbers;y++) {
			int i = 0;
			for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
				Box box = entry.getValue();
				//System.out.println(box.getXMin()+" "+box.getYMin()+" "+box.getZMin());
				int xmin = box.getXMin()
						- this.m_autocropParameters.getxCropBoxSize();
				int ymin = box.getYMin()
						- this.m_autocropParameters.getxCropBoxSize();
				int zmin = box.getZMin()
						- this.m_autocropParameters.getzCropBoxSize();
				String coord = box.getXMin()
						+ "_" + box.getYMin()
						+ "_" + box.getZMin();

				if (xmin <= 0)
					xmin = 1;
				if (ymin <= 0) { ymin = 1;}
				if (zmin <= 0)
					zmin = 1;
				int width = box.getXMax()
						+ (2 * this.m_autocropParameters.getxCropBoxSize())
						- box.getXMin();
				if(width>m_imageSeg.getWidth()){
					width=m_imageSeg.getWidth()-1;
				}
				int height = box.getYMax()
						+ (2 * this.m_autocropParameters.getxCropBoxSize())
						- box.getYMin();
				int depth = box.getZMax()
						+ (2 * this.m_autocropParameters.getzCropBoxSize())
						- box.getZMin();
				if (width + xmin >= this.m_imageSeg.getWidth() || width<0)
					width =  this.m_imageSeg.getWidth()-xmin;

				if ((height + ymin) >= this.m_imageSeg.getHeight() ||(height<0)){
					height = this.m_imageSeg.getHeight()-ymin;
				}
				if (depth + zmin >= this.m_imageSeg.getNSlices() || depth<0)
					depth =  this.m_imageSeg.getNSlices()-zmin;
				//System.out.println(ymin+" " +height +" "+this.m_imageSeg.getHeight());
				box.setXMin((short)xmin);
				box.setXMax((short)(xmin+width));
				box.setYMin((short)ymin);

				box.setYMax((short)(ymin+height));
				box.setZMin((short)zmin);
				box.setZMax((short)(zmin+depth));


			}
			for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
				Box box = entry.getValue();

				if(box.getYMax()>this.m_imageSeg.getHeight()) {
					System.out.println("on en a bordel"+box.getYMax()+"   " +this.m_imageSeg.getHeight()+"\n"
					+" "+ box.getXMin()+" "+box.getYMin()+" "+box.getZMin()+" "+entry.getKey());
				}

				//System.out.println("les clef "+entry.getKey());
				if(entry.getKey()==2.0) {
					//System.out.println("on en a bordel"+box.getYMax()+"\n"
					//		+" "+ box.getXMin()+" "+box.getYMin()+" "+box.getZMin()+" "+entry.getKey());
				}
			}
		}
	}
	/**
	 * Method crops a box of interest, create and save a new small image. This
	 * process allow the crop of all the bounding box contained in the input
	 * ArrayList and the crop is did on the ImageCore put in input in this
	 * method (crop method available in the imagej wrapper). Then the image
	 * results obtained was used to create a new ImageCoreIJ, and the image is
	 * saved.
	 */
	public void cropKernels2()throws Exception {
		Directory dirOutput= new Directory(
			this.m_outputDirPath+File.separator+this.m_outputFilesPrefix);
		dirOutput.CheckAndCreateDir();
		this.m_infoImageAnalyse += getSpecificImageInfo() + getColoneName();
		for (int y =0 ;y<this.m_channelNumbers;y++) {
			int i=0;
			for (Map.Entry<Double , Box> entry : this.m_boxes.entrySet()) {
				Box box =  entry.getValue();
				int xmin = box.getXMin();
				int ymin = box.getYMin();
				int zmin = box.getZMin();
				String coord = box.getXMin()
						+ "_" + box.getYMin()
						+ "_" + box.getZMin();

				int width = box.getXMax()- box.getXMin();
				int height = box.getYMax()- box.getYMin();
				int depth = box.getZMax() - box.getZMin();

				ImagePlus imgResu;
				if(this.m_rawImg.getNSlices()>1) {
					imgResu = cropImage(xmin, ymin,zmin, width,height, depth,y);
				}
				else{
					imgResu = cropImage2D(xmin, ymin, width, height, y);
				}

				Calibration cal = this.m_rawImg.getCalibration();
				imgResu.setCalibration(cal);
				OutputTiff fileOutput = new OutputTiff(
						this.m_outputDirPath
								+ this.m_outputFilesPrefix
								+ File.separator
								+ this.m_outputFilesPrefix
								+ "_"
								+ i
								+"_C"
								+ y
								+ ".tif");
				this.m_infoImageAnalyse=this.m_infoImageAnalyse
						+ m_outputDirPath
						+ this.m_outputFilesPrefix
						+ File.separator
						+ this.m_outputFilesPrefix
						+ "_"
						+ i
						+"_C"
						+ y
						+ ".tif\t"
						+ y  + "\t"
						+ i + "\t"
						+ xmin + "\t"
						+ ymin + "\t"
						+ zmin + "\t"
						+ width + "\t"
						+ height + "\t"
						+ depth + "\n";
				fileOutput.SaveImage(imgResu);
				this.m_outputFile.add(
						this.m_outputDirPath
						+ File.separator
						+ this.m_outputFilesPrefix
						+ File.separator
						+ this.m_outputFilesPrefix
						+  "_"
						+ i
						+ ".tif");

				if(y==0) {
					int xmax=xmin+width;
					int ymax=ymin+height;
					int zmax =zmin+depth;
					this.m_boxCoordinates.add(
							this.m_outputDirPath
									+ File.separator
									+ this.m_outputFilesPrefix + "_"
									+ coord + i
									+ "\t" + xmin
									+ "\t" + xmax
									+ "\t" + ymin
									+ "\t" + ymax
									+ "\t" + zmin
									+ "\t" + zmax);
					//xmin, ymin,zmin, width,height, depth,y
				}
				i++;
			}
		}
	}


	public Long cropKernelsOmero(ImageContainer image, 
								 Long datasetId, 
								 Client client)
		throws Exception {

		DatasetContainer dataset = client.getDataset(datasetId);

		this.m_infoImageAnalyse += getSpecificImageInfo() + getColoneName();
		for (int y =0 ;y<this.m_channelNumbers;y++) {
			int i=0;
			for (Map.Entry<Double , Box> entry : this.m_boxes.entrySet()) {
				Box box =  entry.getValue();
				int xmin = box.getXMin();
				int ymin = box.getYMin();
				int zmin = box.getZMin();
				String coord = box.getXMin()
						+ "_" + box.getYMin()
						+ "_" + box.getZMin();

				int width = box.getXMax()- box.getXMin();
				int height = box.getYMax()- box.getYMin();
				int depth = box.getZMax() - box.getZMin();


				int xBound[] = { box.getXMin(), box.getXMax() - 1};
				int yBound[] = { box.getYMin(), box.getYMax() - 1};
				int zBound[] = { box.getZMin(), box.getZMax() - 1};
				int cBound[] = { y, y };
				
				List<ShapeData> shapes = new ArrayList<ShapeData>();

				for(int z = box.getZMin(); z < box.getZMax(); z++) {
					RectangleData rectangle = new RectangleData(xmin, ymin, width, height);
					rectangle.setC(y);
					rectangle.setZ(z);
					rectangle.setT(0);
					rectangle.setText("" + i);

					rectangle.getShapeSettings().getFontSize(UnitsLength.YOTTAMETER).setValue(45);
					rectangle.getShapeSettings().setStroke(Color.GREEN);

					shapes.add(rectangle);
				}

				ROIContainer roi = new ROIContainer(shapes);

				image.saveROI(client, roi);

				ImagePlus imgResu = image.toImagePlus(client, xBound, yBound, cBound, zBound, null);

				Calibration cal = this.m_rawImg.getCalibration();
				imgResu.setCalibration(cal);
				String path = new java.io.File( "." ).getCanonicalPath() 
				    + "/"
					+ this.m_outputFilesPrefix
					+ "_"
					+ i
					+"_C"
					+ y
					+ ".tif";

				OutputTiff fileOutput = new OutputTiff(path);
				this.m_infoImageAnalyse=this.m_infoImageAnalyse
						+ m_outputDirPath
						+ this.m_outputFilesPrefix
						+ File.separator
						+ this.m_outputFilesPrefix
						+ "_"
						+ i
						+"_C"
						+ y
						+ ".tif\t"
						+ y  + "\t"
						+ i + "\t"
						+ xmin + "\t"
						+ ymin + "\t"
						+ zmin + "\t"
						+ width + "\t"
						+ height + "\t"
						+ depth + "\n";

				fileOutput.SaveImage(imgResu);
				this.m_outputFile.add(this.m_outputFilesPrefix
						+  "_"
						+ i
						+ ".tif");

				dataset.importImages(client, path);

				File file = new File(path);
				file.delete();

				if(y==0) {
					int xmax=xmin+width;
					int ymax=ymin+height;
					int zmax =zmin+depth;
					this.m_boxCoordinates.add(
							this.m_outputDirPath
									+ File.separator
									+ this.m_outputFilesPrefix + "_"
									+ coord + i
									+ "\t" + xmin
									+ "\t" + xmax
									+ "\t" + ymin
									+ "\t" + ymax
									+ "\t" + zmin
									+ "\t" + zmax);
					//xmin, ymin,zmin, width,height, depth,y
				}
				i++;
			}
		}
		return datasetId;
	}

    /**
     * Method crops a box of interest, from coordinate files.
     *
     */
    public void cropKernels3()throws Exception {
        Directory dirOutput= new Directory(
                this.m_outputDirPath+File.separator+this.m_outputFilesPrefix);
        dirOutput.CheckAndCreateDir();
        this.m_infoImageAnalyse += getSpecificImageInfo() + getColoneName();
        for (int y =0 ;y<this.m_channelNumbers;y++) {

            for (Map.Entry<Double , Box> entry : this.m_boxes.entrySet()) {
                int i = (entry.getKey().intValue());
                Box box =  entry.getValue();
                int xmin = box.getXMin();
                int ymin = box.getYMin();
                int zmin = box.getZMin();
                String coord = box.getXMin()
                        + "_" + box.getYMin()
                        + "_" + box.getZMin();

                int width = box.getXMax()- box.getXMin();
                int height = box.getYMax()- box.getYMin();
                int depth = box.getZMax() - box.getZMin();

                ImagePlus imgResu;
                if(this.m_rawImg.getNSlices()>1) {
                    imgResu = cropImage(xmin, ymin,zmin, width,height, depth,y);
                }
                else{
                    imgResu = cropImage2D(xmin, ymin, width, height, y);
                }

                Calibration cal = this.m_rawImg.getCalibration();
                imgResu.setCalibration(cal);
                OutputTiff fileOutput = new OutputTiff(
                        this.m_outputDirPath
                                + this.m_outputFilesPrefix
                                + File.separator
                                + this.m_outputFilesPrefix
                                + "_"
                                + i
                                +"_C"
                                + y
                                + ".tif");
                this.m_infoImageAnalyse=this.m_infoImageAnalyse
                        + m_outputDirPath
                        + this.m_outputFilesPrefix
                        + File.separator
                        + this.m_outputFilesPrefix
                        + "_"
                        + i
                        +"_C"
                        + y
                        + ".tif\t"
                        + y  + "\t"
                        + i + "\t"
                        + xmin + "\t"
                        + ymin + "\t"
                        + zmin + "\t"
                        + width + "\t"
                        + height + "\t"
                        + depth + "\n";
                fileOutput.SaveImage(imgResu);
                this.m_outputFile.add(
                        this.m_outputDirPath
                                + File.separator
                                + this.m_outputFilesPrefix
                                + File.separator
                                + this.m_outputFilesPrefix
                                +  "_"
                                + i
                                + ".tif");

                if(y==0) {
                    int xmax=xmin+width;
                    int ymax=ymin+height;
                    int zmax =zmin+depth;
                    this.m_boxCoordinates.add(
                            this.m_outputDirPath
                                    + File.separator
                                    + this.m_outputFilesPrefix + "_"
                                    + coord + i
                                    + "\t" + xmin
                                    + "\t" + xmax
                                    + "\t" + ymin
                                    + "\t" + ymax
                                    + "\t" + zmin
                                    + "\t" + zmax);
                    //xmin, ymin,zmin, width,height, depth,y
                }
                i++;
            }
        }
    }
	/**
	 * Getter for the m_outoutFiel ArrayList
	 * @return m_outputFile: ArrayList of String for the path of the output
	 * files created.
	 */
	public ArrayList <String> getOutputFileArrayList(){
		return this.m_outputFile;
	}

	/**
	 * Getter for the m_boxCoordinates
	 * @return m_boxCoordinates: ArrayList of String which contain the
	 * coordinates of the boxes
	 */
	public ArrayList <String> getFileCoordinates(){
		return this.m_boxCoordinates;
	}



	/**
	 * Create binary image with the threshold value gave in input
	 * @param imagePlusInput ImagePlus raw image to binarize
	 * @param threshold integer threshold value
	 * @return
	 */
	private ImagePlus generateSegmentedImage (ImagePlus imagePlusInput,
											  int threshold) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue >= threshold){
						imageStackSegmented.setVoxel(i, j, k, 255);
					}
					else {
						imageStackSegmented.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		return imagePlusSegmented;
	}

	/**
	 *
	 * Crop of the bounding box on 3D image. The coordinates are inputs of this
	 * methods
	 *
	 * @param xmin: coordinate x min of the crop
	 * @param ymin: coordinate y min of the crop
	 * @param zmin: coordinate z min of the crop
	 * @param width: coordinate x max of the crop
	 * @param height: coordinate y max of the crop
	 * @param depth: coordinate z max of the crop
	 * @param channelNumber: channel to crop
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage(int xmin, int ymin, int zmin, int width,
							   int height, int depth,int channelNumber)
			throws Exception {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.m_imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus sort = new ImagePlus();
		ChannelSplitter channelSplitter = new ChannelSplitter();
		imps = channelSplitter.split(imps[0]);
		sort.setStack(imps[channelNumber].getStack().crop(xmin, ymin ,zmin,width
				, height,depth));
		return sort;
	}
	/**
	 *
	 * Crop of the bounding box on 2D image. The coordinates are inputs of this
	 * methods.
	 *
	 * @param xmin: coordinate x min of the crop
	 * @param ymin: coordinate y min of the crop
	 * @param width: coordinate x max of the crop
	 * @param height: coordinate y max of the crop
	 * @param channelNumber: channel to crop
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage2D(int xmin, int ymin,  int width, int height,
								 int channelNumber)throws Exception {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.m_imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus sort = imps[channelNumber];
		sort.setRoi(xmin, ymin ,width, height);
		sort.crop();
		return sort;

	}


	/**
	 *Getter of the number of nuclei contained in the input image
	 *
	 * @return int the nb of nuclei
	 */
	public int getNbOfNuc(){
		return this.m_boxes.size();
	}

    /**
     *
     * @return Header current image info analyse
     */
	public String getSpecificImageInfo(){
        Calibration cal = this.m_rawImg.getCalibration();
        return  "#Image: "+this.m_imageFilePath+"\n"
                +"#OTSU threshold: "+this.OTSUthreshold+"\n"
				+"#Slice used for OTSU threshol: "+this.sliceUsedForOTSU+"\n";


	}

    /**
     * Getter column name for the tab delimited file
     * @return columns name for output text file
     */
    public String getColoneName() {
        String colonneName = "FileName\tChannelNumber\tCropNumber\tXStart" +
				"\tYStart\tZStart\twidth\theight\tdepth\n";
        return colonneName;
    }

    /**
     * Write analyse info in output texte file
     * @throws IOException
     */
    public void writeAnalyseInfo() throws IOException {
        OutputTexteFile resultFileOutput=new OutputTexteFile(
        		this.m_outputDirPath + File.separator
						+ this.m_outputFilesPrefix + File.separator
						+ this.m_outputFilesPrefix+".txt");
        resultFileOutput.SaveTexteFile(this.m_infoImageAnalyse);

	}
	
    /**
     * Write analyse info in output texte file
     * @throws IOException
     */
	public void writeAnalyseInfoOmero(Long id, 
									  Client client) 
		throws DSOutOfServiceException,
			   IOException,
			   DSAccessException,
			   ExecutionException,
			   ServerError
		{
		String path = new java.io.File( "." ).getCanonicalPath() 
					  + this.m_outputFilesPrefix+".txt";
					  
		OutputTexteFile resultFileOutput=new OutputTexteFile(path);
		
		resultFileOutput.SaveTexteFile(this.m_infoImageAnalyse);
		
		DatasetContainer dataset = client.getDataset(id);
		File file = new File(path);

		dataset.addFile(client, file);
		file.delete();
    }


	/**
	 *  Getter number of crop
	 * @return number of object detected
	 */
	public String getImageCropInfo(){
		return this.m_imageFilePath+"\t"+getNbOfNuc()+"\t"+this.OTSUthreshold
				+"\t"+this.m_defaultTreshold+"\n";
	}

	public void getNumberOfBox(){
        System.out.println("Number of box :"+this.m_boxes.size());
    }

	/**
	 * Compute volume voxel of current image analysed
	 * @return voxel volume
	 */
	public double getVoxelVolume(){
		double calibration=0;
    	if(this.m_autocropParameters.m_manualParameter==true){
    		calibration=m_autocropParameters.getVoxelVolume();
		}
		else{
			Calibration cal = this.m_rawImg.getCalibration();
			calibration= cal.pixelDepth*cal.pixelWidth*cal.pixelHeight;
		}

    	return calibration ;
	}

    /**
     * Compute boxes merging if intersecting
     */
	public void boxIntesection(){
        if(this.m_autocropParameters.getboxesRegroupement()) {
            rectangleIntersection recompute = new rectangleIntersection(this.m_boxes, this.m_autocropParameters);
            recompute.runRectangleRecompilation();
            this.m_boxes = recompute.getNewBoxes();
        }
	}

	/**
	 * Set a list of boxes
	 * @param boxes list of boxes
	 */
	public void setBoxes(HashMap<Double, Box> boxes){
		this.m_boxes=boxes;
	}
}