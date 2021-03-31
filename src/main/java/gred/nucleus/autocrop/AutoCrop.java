package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.metadata.ROIContainer;
import fr.igred.omero.metadata.ShapeContainer;
import fr.igred.omero.repository.DatasetContainer;
import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.OutputTextFile;
import gred.nucleus.filesInputOutput.OutputTiff;
import gred.nucleus.imageProcess.Thresholding;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.GaussianBlur3D;
import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;
import loci.common.DebugTools;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;
import org.apache.commons.io.FilenameUtils;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Class dedicate to crop nuclei in a isolate file from 3D wide field image from microscopy. The process use a OTSU
 * threshold to detect object on the image. To detect specific object you can use different parameters as filter like -
 * volume of object detected - minimum intensity of object detected - slice used to detect defined OTSU threshold
 * <p>
 * This class output one file per object detected and a tab-delimited file which contains per line the box coordinate of
 * each object.
 * <p>
 * Note : concerning multiple channels images the OTSU threshold is compute on one channel (see
 * ChannelToComputeThreshold) and then boxes coordinate are applied on all channel. You can identify from which channel
 * crop from the file name before file extension you can see C0 for channel 0 for example.
 */
public class AutoCrop {
	/** File to process (Image input) */
	File m_currentFile;
	/** Raw image */
	private ImagePlus            m_rawImg;
	/** Segmented image */
	private ImagePlus            m_imageSeg;
	/** Segmented image connect component labelled */
	private ImagePlus            m_imageSeg_labelled;
	/** The path of the image to be processed */
	private String               m_imageFilePath;
	/** the path of the directory where are saved the crop of the object */
	private String               m_outputDirPath;
	/** The prefix of the names of the output cropped images, which are automatically numbered */
	private String               m_outputFilesPrefix;
	/** List of the path of the output files created by the cropKernels method */
	private ArrayList<String>    m_outputFile       = new ArrayList<>();
	/** List of boxes coordinates */
	private ArrayList<String>    m_boxCoordinates   = new ArrayList<>();
	/** Number of channels in current image */
	private int                  m_channelNumbers   = 1;
	/** Get current info image analyse */
	private String               m_infoImageAnalyse;
	/** Parameters crop analyse */
	private AutocropParameters   m_autocropParameters;
	/** OTSU threshold  used to compute segmented image */
	private int                  OTSUThreshold;
	/** Slice start to compute OTSU */
	private String               sliceUsedForOTSU;
	/** Default threshold */
	private boolean              m_defaultThreshold = false;
	/** List of boxes  to crop link to label value */
	private HashMap<Double, Box> m_boxes            = new HashMap<>();
	
	
	/**
	 * Autocrop constructor : initialisation of analyse parameter
	 *
	 * @param imageFile                 Current image analyse
	 * @param outputFilesPrefix         Prefix use for output file name
	 * @param autocropParametersAnalyse List of analyse parameter
	 */
	public AutoCrop(File imageFile, String outputFilesPrefix, AutocropParameters autocropParametersAnalyse)
	throws Exception {
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_currentFile = imageFile;
		this.m_imageFilePath = imageFile.getAbsolutePath();
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		if (this.m_rawImg.getBitDepth() > 8) {
			this.m_imageSeg =
					thresholding.contrastAnd8bits(getImageChannel(this.m_autocropParameters.getChannelToComputeThreshold()));
		} else {
			this.m_imageSeg = getImageChannel(this.m_autocropParameters.getChannelToComputeThreshold());
		}
		this.m_infoImageAnalyse = autocropParametersAnalyse.getAnalyseParameters();
	}
	
	
	public AutoCrop(ImageContainer image, AutocropParameters autocropParametersAnalyse, Client client)
	throws Exception {
		this.m_currentFile = new File(image.getName());
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = FilenameUtils.removeExtension(image.getName());
		setChannelNumbersOmero(image, client);
		if (this.m_rawImg.getBitDepth() > 8) {
			this.m_imageSeg =
					thresholding.contrastAnd8bits(getImageChannelOmero(this.m_autocropParameters.getChannelToComputeThreshold(),
					                                                   image,
					                                                   client));
		} else {
			this.m_imageSeg = this.m_rawImg;
		}
		this.m_infoImageAnalyse = autocropParametersAnalyse.getAnalyseParameters();
	}
	
	
	public AutoCrop(File imageFile,
	                String outputFilesPrefix,
	                AutocropParameters autocropParametersAnalyse,
	                HashMap<Double, Box> boxes)
	throws Exception {
		this.m_autocropParameters = autocropParametersAnalyse;
		this.m_currentFile = imageFile;
		this.m_imageFilePath = imageFile.getAbsolutePath();
		this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
		Thresholding thresholding = new Thresholding();
		this.m_outputFilesPrefix = outputFilesPrefix;
		setChannelNumbers();
		this.m_imageSeg = this.m_rawImg;
		this.m_infoImageAnalyse = autocropParametersAnalyse.getAnalyseParameters();
		m_boxes = boxes;
	}
	
	
	/**
	 * Method to get specific channel to compute OTSU threshold
	 *
	 * @param channelNumber Number of channel to compute OTSU for crop
	 *
	 * @return image of specific channel
	 */
	public ImagePlus getImageChannel(int channelNumber) throws Exception {
		DebugTools.enableLogging("OFF");    /* DEBUG INFO BIO-FORMATS OFF*/
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		currentImage = ChannelSplitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}
	
	
	public ImagePlus getImageChannelOmero(int channelNumber, ImageContainer image, Client client) throws Exception {
		int[] cBound = {channelNumber, channelNumber};
		return image.toImagePlus(client, null, null, cBound, null, null);
	}
	
	
	/**
	 * Method to check multichannel and initialising channelNumbers variable
	 *
	 * @throws Exception
	 */
	public void setChannelNumbers() throws Exception {
		DebugTools.enableLogging("OFF");      /* DEBUG INFO BIO-FORMATS OFF*/
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		currentImage = ChannelSplitter.split(currentImage[0]);
		this.m_rawImg = currentImage[0];
		if (currentImage.length > 1) {
			this.m_channelNumbers = currentImage.length;
		}
	}
	
	
	public void setChannelNumbersOmero(ImageContainer image, Client client) throws Exception {
		DebugTools.enableLogging("OFF");      /* DEBUG INFO BIO-FORMATS OFF*/
		int[] cBound = {this.m_autocropParameters.getChannelToComputeThreshold(),
		                this.m_autocropParameters.getChannelToComputeThreshold()};
		this.m_rawImg = image.toImagePlus(client, null, null, cBound, null, null);
		this.m_channelNumbers = image.getPixels().getSizeC();
	}
	
	
	/**
	 * Method computing OTSU threshold and creating segmented image from this threshold. Before OTSU threshold a
	 * Gaussian Blur is applied (case of anisotropic voxels)
	 * <p> TODO add case where voxel are not anisotropic for Gaussian Blur Case where OTSU threshold is under 20
	 * computation using only half of last slice (useful in case of top slice with lot of noise) If OTSU threshold is
	 * still under 20 threshold default threshold value is 20.
	 */
	public void thresholdKernels() {
		if (this.m_imageSeg == null) {
			return;
		}
		this.sliceUsedForOTSU = "default";
		GaussianBlur3D.blur(this.m_imageSeg, 0.5, 0.5, 1);
		Thresholding thresholding = new Thresholding();
		int          thresh       = thresholding.computeOtsuThreshold(this.m_imageSeg);
		if (thresh < this.m_autocropParameters.getThresholdOTSUComputing()) {
			ImagePlus imp2;
			if (m_autocropParameters.getSlicesOTSUComputing() == 0) {
				this.sliceUsedForOTSU =
						"Start:" + this.m_imageSeg.getStackSize() / 2 + "-" + this.m_imageSeg.getStackSize();
				imp2 = new Duplicator().run(this.m_imageSeg,
				                            this.m_imageSeg.getStackSize() / 2,
				                            this.m_imageSeg.getStackSize());
			} else {
				this.sliceUsedForOTSU = "Start:" +
				                        this.m_autocropParameters.getSlicesOTSUComputing() +
				                        "-" +
				                        this.m_imageSeg.getStackSize();
				imp2 = new Duplicator().run(this.m_imageSeg,
				                            this.m_autocropParameters.getSlicesOTSUComputing(),
				                            this.m_imageSeg.getStackSize());
			}
			int thresh2 = thresholding.computeOtsuThreshold(imp2);
			if (thresh2 < this.m_autocropParameters.getThresholdOTSUComputing()) {
				thresh = this.m_autocropParameters.getThresholdOTSUComputing();
				this.m_defaultThreshold = true;
			} else {
				thresh = thresh2;
			}
		}
		this.OTSUThreshold = thresh;
		this.m_imageSeg = this.generateSegmentedImage(this.m_imageSeg, thresh);
	}
	
	
	/** MorpholibJ Method computing connected components using OTSU segmented image */
	public void computeConnectedComponent() {
		this.m_imageSeg_labelled = BinaryImages.componentsLabeling(this.m_imageSeg, 26, 32);
	}
	
	
	/**
	 * Initializes hashMap m_boxes containing connected components pixel value associate to number of voxels composing
	 * it. Filter connected components based on minimum volume (default 1 ) and maximum volume (default 2147483647)
	 */
	public void componentSizeFilter() {
		Histogram histogram = new Histogram();
		histogram.run(this.m_imageSeg_labelled);
		HashMap<Double, Integer> histogramData = histogram.getHistogram();
		for (Map.Entry<Double, Integer> entry : histogramData.entrySet()) {
			Double  key   = entry.getKey();
			Integer value = entry.getValue();
			if (!((value * getVoxelVolume() < this.m_autocropParameters.getMinVolumeNucleus()) ||
			      (value * getVoxelVolume() > this.m_autocropParameters.getMaxVolumeNucleus())) && value > 1) {
				Box initializedBox = new Box(Short.MAX_VALUE,
				                             Short.MIN_VALUE,
				                             Short.MAX_VALUE,
				                             Short.MIN_VALUE,
				                             Short.MAX_VALUE,
				                             Short.MIN_VALUE);
				this.m_boxes.put(key, initializedBox);
			}
		}
		getNumberOfBox();
	}
	
	
	/** MorpholibJ Method filtering border connect component */
	public void componentBorderFilter() {
		LabelImages.removeBorderLabels(this.m_imageSeg_labelled);
	}
	
	
	/**
	 * Detection of the of the bounding box for each object of the image. A Connected component detection is do on the
	 * m_imageThresholding and all the object on the border and under or upper threshold volume are removed. The
	 * coordinates allow the implementation of the box objects which define the bounding box, and these objects are
	 * stocked in a ArrayList. In order to use with a grey-level image, use either @see # thresholdKernels() or your own
	 * binarization method.
	 */
	public void computeBoxes2() {
		try {
			ImageStack imageStackInput = this.m_imageSeg_labelled.getStack();
			Box        box;
			for (short k = 0; k < this.m_imageSeg_labelled.getNSlices(); ++k) {
				for (short i = 0; i < this.m_imageSeg_labelled.getWidth(); ++i) {
					for (short j = 0; j < this.m_imageSeg_labelled.getHeight(); ++j) {
						if ((imageStackInput.getVoxel(i, j, k) > 0) &&
						    (this.m_boxes.containsKey(imageStackInput.getVoxel(i, j, k)))) {
							box = this.m_boxes.get(imageStackInput.getVoxel(i, j, k));
							if (i < box.getXMin()) {
								box.setXMin(i);
							} else if (i > box.getXMax()) {
								box.setXMax(i);
							}
							if (j < box.getYMin()) {
								box.setYMin(j);
							} else if (j > box.getYMax()) {
								box.setYMax(j);
							}
							if (k < box.getZMin()) {
								box.setZMin(k);
							} else if (k > box.getZMax()) {
								box.setZMax(k);
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method to add X voxels in x y z around the connected component. X by default is 20 in x y z. Parameter can be
	 * modified in autocrop parameters:
	 * <ul>
	 *     <li>m_xCropBoxSize</li>
	 *     <li>m_yCropBoxSize</li>
	 *     <li>m_zCropBoxSize</li>
	 * </ul>
	 */
	public void addCROP_parameter() {
		for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
			Box    box         = entry.getValue();
			int    xMin        = (int) box.getXMin() - this.m_autocropParameters.getXCropBoxSize();
			int    yMin        = (int) box.getYMin() - this.m_autocropParameters.getYCropBoxSize();
			int    zMin        = (int) box.getZMin() - this.m_autocropParameters.getZCropBoxSize();
			String coordinates = box.getXMin() + "_" + box.getYMin() + "_" + box.getZMin();
			
			if (xMin <= 0) {
				xMin = 1;
			}
			if (yMin <= 0) {
				yMin = 1;
			}
			if (zMin <= 0) {
				zMin = 1;
			}
			int width = box.getXMax() + (2 * this.m_autocropParameters.getXCropBoxSize()) - box.getXMin();
			if (width > m_imageSeg.getWidth()) {
				width = m_imageSeg.getWidth() - 1;
			}
			if (width + xMin >= this.m_imageSeg.getWidth() || width < 0) {
				width = this.m_imageSeg.getWidth() - xMin;
			}
			int height = box.getYMax() + (2 * this.m_autocropParameters.getYCropBoxSize()) - box.getYMin();
			if ((height + yMin) >= this.m_imageSeg.getHeight() || (height < 0)) {
				height = this.m_imageSeg.getHeight() - yMin;
			}
			int depth = box.getZMax() + (2 * this.m_autocropParameters.getZCropBoxSize()) - box.getZMin();
			if (depth + zMin >= this.m_imageSeg.getNSlices() || depth < 0) {
				depth = this.m_imageSeg.getNSlices() - zMin;
			}
			/*System.out.println(yMin+" " +height +" "+this.m_imageSeg.getHeight());*/
			box.setXMin((short) xMin);
			box.setXMax((short) (xMin + width));
			box.setYMin((short) yMin);
			box.setYMax((short) (yMin + height));
			box.setZMin((short) zMin);
			box.setZMax((short) (zMin + depth));
			entry.setValue(box);
		}
	}
	
	
	/**
	 * Method crops a box of interest, create and save a new small image. This process allow the crop of all the
	 * bounding box contained in the input ArrayList and the crop is did on the ImageCore put in input in this method
	 * (crop method available in the imagej wrapper). Then the image results obtained was used to create a new
	 * ImageCoreIJ, and the image is saved.
	 */
	public void cropKernels2() throws Exception {
		Directory dirOutput = new Directory(this.m_outputDirPath + "nuclei");
		dirOutput.CheckAndCreateDir();
		this.m_infoImageAnalyse += getSpecificImageInfo() + getColumnNames();
		for (int c = 0; c < this.m_channelNumbers; c++) {
			for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
				int       i           = entry.getKey().intValue();
				Box       box         = entry.getValue();
				int       xMin        = box.getXMin();
				int       yMin        = box.getYMin();
				int       zMin        = box.getZMin();
				int       width       = box.getXMax() - box.getXMin();
				int       height      = box.getYMax() - box.getYMin();
				int       depth       = box.getZMax() - box.getZMin();
				String    coordinates = box.getXMin() + "_" + box.getYMin() + "_" + box.getZMin();
				ImagePlus croppedImage;
				if (this.m_rawImg.getNSlices() > 1) {
					croppedImage = cropImage(xMin, yMin, zMin, width, height, depth, c);
				} else {
					croppedImage = cropImage2D(xMin, yMin, width, height, c);
				}
				Calibration cal = this.m_rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = dirOutput.getDirPath() + File.separator +
				                  this.m_outputFilesPrefix + "_" + i + "_C" + c + ".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				this.m_infoImageAnalyse = this.m_infoImageAnalyse +
				                          tiffPath + "\t" +
				                          c + "\t" +
				                          i + "\t" +
				                          xMin + "\t" +
				                          yMin + "\t" +
				                          zMin + "\t" +
				                          width + "\t" +
				                          height + "\t" +
				                          depth + "\n";
				fileOutput.SaveImage(croppedImage);
				this.m_outputFile.add(this.m_outputDirPath + File.separator +
				                      this.m_outputFilesPrefix + File.separator +
				                      this.m_outputFilesPrefix + "_" + i + ".tif");
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.m_boxCoordinates.add(this.m_outputDirPath + File.separator +
					                          this.m_outputFilesPrefix + "_" + i + "_C0" + "\t" +
					                          xMin + "\t" +
					                          xMax + "\t" +
					                          yMin + "\t" +
					                          yMax + "\t" +
					                          zMin + "\t" +
					                          zMax);
					//xMin, yMin,zMin, width,height, depth,y
				}
			}
		}
	}
	
	
	public void cropKernelsOmero(ImageContainer image, Long[] outputsDat, Client client) throws Exception {
		this.m_infoImageAnalyse += getSpecificImageInfo() + getColumnNames();
		for (int c = 0; c < this.m_channelNumbers; c++) {
			for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
				DatasetContainer dataset     = client.getDataset(outputsDat[c]);
				int              i           = entry.getKey().intValue();
				Box              box         = entry.getValue();
				int              xMin        = box.getXMin();
				int              yMin        = box.getYMin();
				int              zMin        = box.getZMin();
				int              width       = box.getXMax() - box.getXMin();
				int              height      = box.getYMax() - box.getYMin();
				int              depth       = box.getZMax() - box.getZMin();
				String           coordinates = box.getXMin() + "_" + box.getYMin() + "_" + box.getZMin();
				int[]            xBound      = {box.getXMin(), box.getXMax() - 1};
				int[]            yBound      = {box.getYMin(), box.getYMax() - 1};
				int[]            zBound      = {box.getZMin(), box.getZMax() - 1};
				int[]            cBound      = {c, c};
				
				List<ShapeContainer> shapes = new ArrayList<>();
				for (int z = box.getZMin(); z < box.getZMax(); z++) {
					ShapeContainer rectangle = new ShapeContainer(ShapeContainer.RECTANGLE);
					rectangle.setRectangleCoordinates(xMin, yMin, width, height);
					rectangle.setC(c);
					rectangle.setZ(z);
					rectangle.setT(0);
					rectangle.setText(String.valueOf(i));
					rectangle.setFontSize(45);
					rectangle.setStroke(Color.GREEN);
					shapes.add(rectangle);
				}
				ROIContainer roi = new ROIContainer(shapes);
				image.saveROI(client, roi);
				ImagePlus   croppedImage = image.toImagePlus(client, xBound, yBound, cBound, zBound, null);
				Calibration cal          = this.m_rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = new java.io.File(".").getCanonicalPath() + File.separator +
				                  this.m_outputFilesPrefix + "_" + i + ".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				this.m_infoImageAnalyse = this.m_infoImageAnalyse + m_outputDirPath +
				                          this.m_outputFilesPrefix + File.separator +
				                          this.m_outputFilesPrefix + "_" + i + ".tif" + "\t" +
				                          c + "\t" +
				                          i + "\t" +
				                          xMin + "\t" +
				                          yMin + "\t" +
				                          zMin + "\t" +
				                          width + "\t" +
				                          height + "\t" +
				                          depth + "\n";
				fileOutput.SaveImage(croppedImage);
				this.m_outputFile.add(this.m_outputFilesPrefix + "_" + i + ".tif");
				dataset.importImages(client, tiffPath);
				File    file    = new File(tiffPath);
				boolean deleted = file.delete();
				if (!deleted) System.err.println("File not deleted: " + tiffPath);
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.m_boxCoordinates.add(this.m_outputDirPath + File.separator +
					                          this.m_outputFilesPrefix + "_" + coordinates +
					                          i + "\t" +
					                          xMin + "\t" +
					                          xMax + "\t" +
					                          yMin + "\t" +
					                          yMax + "\t" +
					                          zMin + "\t" +
					                          zMax);
					//xMin, yMin,zMin, width,height, depth,y
				}
			}
		}
	}
	
	
	/** Method crops a box of interest, from coordinate files. */
	public void cropKernels3() throws Exception {
		Directory dirOutput = new Directory(this.m_outputDirPath + File.separator + "Nuclei");
		dirOutput.CheckAndCreateDir();
		this.m_infoImageAnalyse += getSpecificImageInfo() + getColumnNames();
		for (int c = 0; c < this.m_channelNumbers; c++) {
			for (Map.Entry<Double, Box> entry : this.m_boxes.entrySet()) {
				int       i           = entry.getKey().intValue();
				Box       box         = entry.getValue();
				int       xMin        = box.getXMin();
				int       yMin        = box.getYMin();
				int       zMin        = box.getZMin();
				int       width       = box.getXMax() - box.getXMin();
				int       height      = box.getYMax() - box.getYMin();
				int       depth       = box.getZMax() - box.getZMin();
				String    coordinates = box.getXMin() + "_" + box.getYMin() + "_" + box.getZMin();
				ImagePlus croppedImage;
				if (this.m_rawImg.getNSlices() > 1) {
					croppedImage = cropImage(xMin, yMin, zMin, width, height, depth, c);
				} else {
					croppedImage = cropImage2D(xMin, yMin, width, height, c);
				}
				Calibration cal = this.m_rawImg.getCalibration();
				croppedImage.setCalibration(cal);
				String tiffPath = dirOutput.getDirPath() + File.separator +
				                  this.m_outputFilesPrefix + "_" + i + "_C" + c + ".tif";
				OutputTiff fileOutput = new OutputTiff(tiffPath);
				this.m_infoImageAnalyse = this.m_infoImageAnalyse +
				                          tiffPath + "\t" +
				                          c + "\t" +
				                          i + "\t" +
				                          xMin + "\t" +
				                          yMin + "\t" +
				                          zMin + "\t" +
				                          width + "\t" +
				                          height + "\t" +
				                          depth + "\n";
				fileOutput.SaveImage(croppedImage);
				this.m_outputFile.add(this.m_outputDirPath + File.separator +
				                      this.m_outputFilesPrefix + File.separator +
				                      this.m_outputFilesPrefix + "_" + i + ".tif");
				if (c == 0) {
					int xMax = xMin + width;
					int yMax = yMin + height;
					int zMax = zMin + depth;
					this.m_boxCoordinates.add(this.m_outputDirPath + File.separator +
					                          this.m_outputFilesPrefix +
					                          "_" +
					                          i + "\t" +
					                          xMin + "\t" +
					                          xMax + "\t" +
					                          yMin + "\t" +
					                          yMax + "\t" +
					                          zMin + "\t" +
					                          zMax);
					//xMin, yMin,zMin, width,height, depth,y
				}
			}
		}
	}
	
	
	/**
	 * Getter for the m_outputFile ArrayList
	 *
	 * @return m_outputFile: ArrayList of String for the path of the output files created.
	 */
	public ArrayList<String> getOutputFileArrayList() {
		return this.m_outputFile;
	}
	
	
	/**
	 * Getter for the m_boxCoordinates
	 *
	 * @return m_boxCoordinates: ArrayList of String which contain the coordinates of the boxes
	 */
	public ArrayList<String> getFileCoordinates() {
		return this.m_boxCoordinates;
	}
	
	
	/**
	 * Create binary image with the threshold value gave in input
	 *
	 * @param imagePlusInput ImagePlus raw image to binarize
	 * @param threshold      integer threshold value
	 *
	 * @return
	 */
	private ImagePlus generateSegmentedImage(ImagePlus imagePlusInput, int threshold) {
		ImageStack imageStackInput     = imagePlusInput.getStack();
		ImagePlus  imagePlusSegmented  = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
					double voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue >= threshold) {
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
	 * Crop of the bounding box on 3D image. The coordinates are inputs of this methods
	 *
	 * @param xMin:          coordinate x min of the crop
	 * @param yMin:          coordinate y min of the crop
	 * @param zMin:          coordinate z min of the crop
	 * @param width:         coordinate x max of the crop
	 * @param height:        coordinate y max of the crop
	 * @param depth:         coordinate z max of the crop
	 * @param channelNumber: channel to crop
	 *
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage(int xMin, int yMin, int zMin, int width, int height, int depth, int channelNumber)
	throws Exception {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.m_imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus   sort = new ImagePlus();
		imps = ChannelSplitter.split(imps[0]);
		sort.setStack(imps[channelNumber].getStack().crop(xMin, yMin, zMin, width, height, depth));
		return sort;
	}
	
	
	/**
	 * Crop of the bounding box on 2D image. The coordinates are inputs of this methods.
	 *
	 * @param xMin:          coordinate x min of the crop
	 * @param yMin:          coordinate y min of the crop
	 * @param width:         coordinate x max of the crop
	 * @param height:        coordinate y max of the crop
	 * @param channelNumber: channel to crop
	 *
	 * @return : ImageCoreIJ of the cropped image.
	 */
	public ImagePlus cropImage2D(int xMin, int yMin, int width, int height, int channelNumber) throws Exception {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.m_imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus   sort = imps[channelNumber];
		sort.setRoi(xMin, yMin, width, height);
		sort.crop();
		return sort;
	}
	
	
	/**
	 * Getter of the number of nuclei contained in the input image
	 *
	 * @return int the nb of nuclei
	 */
	public int getNbOfNuc() {
		return this.m_boxes.size();
	}
	
	
	/** @return Header current image info analyse */
	public String getSpecificImageInfo() {
		Calibration cal = this.m_rawImg.getCalibration();
		return "#Image: " +
		       this.m_imageFilePath +
		       "\n#OTSU threshold: " +
		       this.OTSUThreshold +
		       "\n#Slice used for OTSU threshold: " +
		       this.sliceUsedForOTSU +
		       "\n";
	}
	
	
	/**
	 * Getter column name for the tab delimited file
	 *
	 * @return columns name for output text file
	 */
	public String getColumnNames() {
		return "FileName\tChannelNumber\tCropNumber\tXStart\tYStart\tZStart\twidth\theight\tdepth\n";
	}
	
	
	/**
	 * Write analysis info in output text file
	 */
	public void writeAnalyseInfo() {
		Directory dirOutput = new Directory(this.m_outputDirPath + "coordinates");
		dirOutput.CheckAndCreateDir();
		OutputTextFile resultFileOutput = new OutputTextFile(this.m_outputDirPath +
		                                                     "coordinates" +
		                                                     File.separator +
		                                                     this.m_outputFilesPrefix +
		                                                     ".txt");
		resultFileOutput.saveTextFile(this.m_infoImageAnalyse, true);
	}
	
	
	/** Write analyse info in output text file */
	public void writeAnalyseInfoOmero(Long id, Client client) {
		try {
			String path = new File(".").getCanonicalPath() + this.m_outputFilesPrefix + ".txt";
			
			File             file             = new File(path);
			OutputTextFile   resultFileOutput = new OutputTextFile(path);
			DatasetContainer dataset          = client.getDataset(id);
			
			resultFileOutput.saveTextFile(this.m_infoImageAnalyse, false);
			dataset.addFile(client, file);
			boolean deleted = file.delete();
			if (!deleted) System.err.println("File not deleted: " + path);
		} catch (Exception e) {
			System.err.println("Error writing analysis information to OMERO");
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Getter number of crop
	 *
	 * @return number of object detected
	 */
	public String getImageCropInfo() {
		return this.m_imageFilePath + "\t" +
		       getNbOfNuc() + "\t" +
		       this.OTSUThreshold + "\t" +
		       this.m_defaultThreshold + "\n";
	}
	
	
	public void getNumberOfBox() {
		System.out.println("Number of box :" + this.m_boxes.size());
	}
	
	
	/**
	 * Compute volume voxel of current image analysed
	 *
	 * @return voxel volume
	 */
	public double getVoxelVolume() {
		double calibration;
		if (this.m_autocropParameters.m_manualParameter) {
			calibration = m_autocropParameters.getVoxelVolume();
		} else {
			Calibration cal = this.m_rawImg.getCalibration();
			calibration = cal.pixelDepth * cal.pixelWidth * cal.pixelHeight;
		}
		return calibration;
	}
	
	
	/** Compute boxes merging if intersecting */
	public void boxIntersection() {
		if (this.m_autocropParameters.getBoxesRegrouping()) {
			RectangleIntersection recompute = new RectangleIntersection(this.m_boxes, this.m_autocropParameters);
			recompute.runRectangleRecompilation();
			this.m_boxes = recompute.getNewBoxes();
		}
	}
	
	
	/**
	 * Set a list of boxes
	 *
	 * @param boxes list of boxes
	 */
	public void setBoxes(HashMap<Double, Box> boxes) {
		this.m_boxes = boxes;
	}
	
}
