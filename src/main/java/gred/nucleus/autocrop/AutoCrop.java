package gred.nucleus.autocrop;

import java.io.File;
import java.util.ArrayList;
import gred.nucleus.connectedComponent.ConnectedComponent;
import ij.IJ;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackConverter;
import ij.process.StackStatistics;
import ij.process.AutoThresholder.Method;
import ij.process.ImageConverter;

/**
 * Allows to compute bounding boxes, to automatically crop and to save. 
 * Input image can be a binary Image or an 8 bit gray level image. If it is a gray level image, the method thresholdKernels allow the segmentation
 *  of the image.
 * The ConnectedComponent Class is used and permit thresholding of the objects volume to remove the smaller connected components
 * which can be artefact. 
 * 
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class AutoCrop {
	/**
	 * Input image, it is a binary Image. If it is not the case the method thresholdKernels
	 * allow the segmentation of the image. The image is initialised in the constructor 
	 * and this image is modified during the process.
	 */
	private ImagePlus m_rawImg;
	
	private ImagePlus m_imageSeg;
	/** The path of the image to be processed */
	private String m_imageFilePath;
	/** the path of the directory where are saved the crop of the object */
	private String m_outputDirPath;
	/** The prefix of the names of the output cropped images, which are automatically numbered.*/
	private String m_outputFilesPrefix;
	/** List of the path of the output files created by the cropKernels method*/
	private ArrayList <String> m_outputFile =  new ArrayList <String>();
	/** List of boxes coordinates */
	private ArrayList <String> m_boxCoordinates = new ArrayList<String>();	
	/** */
	private int _nbOfNuc = 0;

	/**
	 * Initialises the attributes of this instance and attempts loading the input image
	 * 
	 * @param imageFile: the path to the input 8bit gray level or binary image, to crop the sub objects.
	 * @param outputFilesPrefix: prefix of the names of the output cropped images
	 * @param outputDirPath: the path to saved the cropped images
	 */
	
	public AutoCrop(String imageFile,String outputFilesPrefix, String outputDirPath) {
		m_imageFilePath = imageFile;
		m_outputDirPath = outputDirPath;
		this.m_rawImg = IJ.openImage(m_imageFilePath);
		m_imageSeg= IJ.openImage(m_imageFilePath);
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.setUseStackHistogram(true);
		enh.stretchHistogram(m_imageSeg, 0.05);
		StackConverter stackConverter = new StackConverter( m_imageSeg );
		stackConverter.convertToGray8();
		m_outputFilesPrefix = outputFilesPrefix;
	}

	/**
	 * Initialises the attributes of this instance.
	 * 
	 * @param imageThresholding: the input 8bit gray level or binary image, to crop the sub objects.
	 * @param outputFilesPrefix: prefix of the names of the output cropped images
	 * @param outputDirPath: the path to saved the cropped images
	 */
	public AutoCrop(ImagePlus imageThresholding,String outputFilesPrefix, String outputDirPath) {
		m_outputDirPath = outputDirPath;
		m_rawImg = imageThresholding.duplicate();
		m_imageSeg= imageThresholding;
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.setUseStackHistogram(true);
		enh.setProcessStack(true);
		enh.stretchHistogram(m_imageSeg.getProcessor(), 0.05);
		StackConverter stackConverter = new StackConverter( m_imageSeg );
		stackConverter.convertToGray8();
		m_outputFilesPrefix = outputFilesPrefix;
	}

	/**
	 * Threshold of the input using the Otsu method:
	 * First step is the  binomial blur of the wrapImaJ package with 6 6 1 as parameters.
	 * Then Otsu thresholding is applied. This method can be used if the input image is 8bit gray level image.
	 */
	public void thresholdKernels() {
		GaussianBlur3D.blur(m_imageSeg, 0.5,0.5,1);
		int thresh = computeOtsuThreshold(m_imageSeg);
		m_imageSeg = this.generateSegmentedImage(m_imageSeg, thresh);
	}
	
	/**
	 * Detection of the of the bounding box for each object of the image.
	 * A Connected component detection is do on the  m_imageThresholding and all the object on the border and inferior a at the threshold volume
	 * are removed. The coordinates allow the implementation of the box objects which define the bounding box, and these objects are stocked in a 
	 * ArrayList.
	 * @pre the input image must be a binary image with values 255 and 0 only.
	 * In order to use with a grey-level image, use either @see # thresholdKernels() or 
	 * your own binarisation method.
	 * @param thresholdVolumeVoxel: threshold volume of the objects detected by the connected component
	 * @return the ArrayList of the bounding boxes.
	 */
	public ArrayList <Box> computeBoxes(double thresholdVolumeVoxel) {
		ConnectedComponent connectedComponent; 
		ArrayList <Box> boxes =  new ArrayList <Box>();
		try {
			connectedComponent = ConnectedComponent.getLabelledConnectedComponent(m_imageSeg, 255, true, thresholdVolumeVoxel, true);
			Box box = null;
			//ArrayList initialisation
            this._nbOfNuc = connectedComponent.getNumberOfComponents();
			for(short i = 0; i< connectedComponent.getNumberOfComponents();++i)
				boxes.add(new Box(Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE));

			// Check the predicate
			for (short k = 0; k < this.m_imageSeg.getNSlices(); ++k) {
				for (short i = 0; i < this.m_imageSeg.getWidth(); ++i) {
					for (short j = 0; j < this.m_imageSeg.getHeight(); ++j) {
						// if label different of the background
						if (connectedComponent.getLabel(i, j, k) > 0) {
							box = boxes.get(connectedComponent.getLabel(i, j, k)-1);
							if (i < box.getXMin())
							    box.setXMin(i);
							else if (i > box.getXMax())
							    box.setXMax(i);
							if (j < box.getYMin())
							    box.setYMin(j);
							else if(j > box.getYMax())
							    box.setYMax(j);
							
							if (k < box.getZMin())
							    box.setZMin(k);
							else if (k > box.getZMax())
							    box.setZMax(k);
						}
					}
				}
			}
		}
		catch (Exception e){ e.printStackTrace(); }
		return boxes;
	}
	
	/**
	 * Method crops a box of interest, create and save a new small image. This process allow the crop of all the bounding box 
	 * contained in the input ArrayList and the crop is did on the ImageCore put in input in this method (crop method available in the imagej wrapper). Then the image results 
	 * obtained was used to create a new ImageCoreIJ, and the image is saved. 
	 * 
	 * @param boxes: containing object boxes.
     */
	public void cropKernels(ArrayList <Box> boxes) {
		for(short i = 0; i < boxes.size(); ++i) {
			Box box = boxes.get(i);
			int xmin = box.getXMin()-40;
			int ymin =  box.getYMin()-40;
			int zmin =  box.getZMin()-20;
			String coord= box.getXMin()+"_"+box.getYMin()+"_"+box.getZMin();
			m_boxCoordinates.add(m_outputDirPath+File.separator+m_outputFilesPrefix+"_"+coord+i+"\t"+box.getXMin()+"\t"+box.getXMax()+"\t"+box.getYMin()+"\t"+box.getYMax()+"\t"+box.getZMin()+"\t"+box.getZMax());
			if (xmin < 0)
			    xmin = 1;
			if (ymin < 0)
			    ymin = 1;
			if (zmin < 0)
			    zmin = 1;

			int width = box.getXMax()+80 - box.getXMin();
			int height = box.getYMax()+80 - box.getYMin();
			int depth = box.getZMax()+40 - box.getZMin();
			if (width+xmin >= m_imageSeg.getWidth())
	         	width-=(width+xmin)-m_imageSeg.getWidth();

			if (height+ymin >= m_imageSeg.getHeight())
	        	height-=(height+ymin)-m_imageSeg.getHeight();

			if (depth+zmin >= m_imageSeg.getNSlices())
	         	depth-=(depth+zmin)-m_imageSeg.getNSlices();
			ImagePlus imgResu = cropImage(xmin, ymin, zmin, width, height, depth);
			Calibration cal = m_rawImg.getCalibration();
			imgResu.setCalibration(cal);
			File outputFile = new File(m_outputDirPath+File.separator+m_outputFilesPrefix);
			if(!(outputFile .exists())) {
				outputFile.mkdir();
			}
	       	saveFile(imgResu,m_outputDirPath+File.separator+m_outputFilesPrefix+File.separator+m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
			m_outputFile.add(m_outputDirPath+File.separator+m_outputFilesPrefix+File.separator+m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
	       	System.gc();
		}	
	}

	/**
	 * Getter for the m_outoutFiel ArrayList
	 * @return m_outputFile: ArrayList of String for the path of the output files created.
	 */
	public ArrayList <String> getOutputFileArrayList(){
	    return m_outputFile;
	}
	
	/**
	 * Getter for the m_boxCoordinates
	 * @return m_boxCoordinates: ArrayList of String which contain the coordinates of the boxes
	 */
	public ArrayList <String> getFileCoordinates(){
	    return m_boxCoordinates;
	}
	
	/**
	 * Compute the initial threshold value
	 * 
	 * @param imagePlusInput raw image
	 * @return
	 */
	private int computeOtsuThreshold (ImagePlus imagePlusInput) {
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int [] tHisto = imageStatistics.histogram;
		return autoThresholder.getThreshold(Method.Otsu,tHisto);
	}
	
	/**
	 * Create binary image with the threshold value gave in input
	 * @param imagePlusInput ImagePlus raw image to binarize
	 * @param threshold integer threshold value
	 * @return
	 */
	private ImagePlus generateSegmentedImage (ImagePlus imagePlusInput, int threshold) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
            for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
                for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
                    double voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue >= threshold) imageStackSegmented.setVoxel(i, j, k, 255);
                    else imageStackSegmented.setVoxel(i, j, k, 0);
                }
            }
        }
		return imagePlusSegmented;
	}
	
	/**
	 * Save the image file
	 * 
	 * @param imagePlusInput image to save
	 * @param pathFile path to save the image
	 */	
	public void saveFile ( ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
	    fileSaver.saveAsTiff(pathFile);
	}
	
    
    /**
     *
     * Crop of the bounding box on the image. The coordinates are inputs of this methods  
     *
	 * @param xmin: coordinate x min of the crop
     * @param ymin: coordinate y min of the crop
     * @param zmin: coordinate z min of the crop
     * @param width: coordinate x max of the crop
     * @param height: coordinate y max of the crop
     * @param depth: coordinate z max of the crop
     * @return : ImageCoreIJ of the cropped image.
     */
    public ImagePlus cropImage(int xmin, int ymin, int zmin, int width, int height, int depth) {
    	ImageStack iStack =  m_rawImg.getStack();
    	ImagePlus imp = new ImagePlus();
	   	imp.setStack(iStack.crop(xmin, ymin, zmin, width, height, depth));
		ImageConverter ic = new ImageConverter(imp);
		ic.convertToGray8();
		imp.updateAndDraw();
    	return imp;
    }

    /**
     *Getter of the number of nuclei contained in the input image
	 *
     * @return int the nb of nuclei
     */
    public int getNbOfNuc(){
        return this._nbOfNuc;
    }


}