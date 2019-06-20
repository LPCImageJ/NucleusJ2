package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTexteFile;
import gred.nucleus.FilesInputOutput.OutputTiff;
import gred.nucleus.connectedComponent.ConnectedComponent;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.imageProcess.Thresholding;
import gred.nucleus.utils.Histogram;
import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import ij.plugin.ChannelSplitter;
import ij.plugin.Duplicator;
import ij.plugin.GaussianBlur3D;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackStatistics;

import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


import inra.ijpb.binary.BinaryImages;
import inra.ijpb.label.LabelImages;



public class AutoCrop {

	/** File to process (Image input) */
	File m_currentFile;
    /** Raw image */
	private ImagePlus m_rawImg;
    /** Segmented image  */
	private ImagePlus m_imageSeg;
	/** Segmented image connect component labelled */
	private ImagePlus m_imageSeg_labelled;

	/** The path of the image to be processed */
	private String m_imageFilePath;
	/** the path of the directory where are saved the crop of the object */
	private String m_outputDirPath;
	/** The prefix of the names of the output cropped images, which are automatically numbered */
	private String m_outputFilesPrefix;
	/** List of the path of the output files created by the cropKernels method */
	private ArrayList<String> m_outputFile =  new ArrayList <String>();
	/** List of boxes coordinates */
	private ArrayList <String> m_boxCoordinates = new ArrayList<String>();
	/** Number of nuclei cropped */
	private int m_nbOfNuc = 0;
	/** Number of channels in current image */
	private int m_channelNumbers=0;
	/** Get current info inmage analyse */
	private String m_infoImageAnalyse = "";
    /** Parameters crop analyse */
    private AutocropParameters m_autocropParameters;
    /** OTSU threshold */
    private int OTSUthreshold;
    /**List of boxes  to crop*/
    private ArrayList <Box> m_boxes;


    /** TODO GESTION OF log4J WARN !!!!! (BF.openImagePlus)
     *  Autocrop constructor : initialisation of analyse parameter
     * @param imageFile : current image analyse
     * @param outputFilesPrefix : prefix use for output file name
     * @param autocropParametersAnalyse : list of analyse parameter
     */

    public AutoCrop(File imageFile, String outputFilesPrefix, AutocropParameters autocropParametersAnalyse ) throws IOException, FormatException, fileInOut,Exception{
        this.m_autocropParameters=autocropParametersAnalyse;
	    this.m_currentFile=imageFile;
        this.m_imageFilePath = imageFile.getAbsolutePath();
        this.m_outputDirPath = this.m_autocropParameters.getOutputFolder();
        Thresholding thresholding = new Thresholding();
        this.m_outputFilesPrefix = outputFilesPrefix;
        setChannelNumbers();
        this.m_imageSeg= thresholding.contrastAnd8bits(getImageChannel(this.m_autocropParameters.getChannelToComputeThreshold()));
        m_infoImageAnalyse=this.m_autocropParameters.getAnalyseParameters();
        this.m_infoImageAnalyse=autocropParametersAnalyse.getAnalyseParameters()+getSpecificImageInfo()+getColoneName();

    }



	/**
	 * Method to get specific channel
	 *
	 * @return image of specific channel
	 */

	public ImagePlus getImageChannel(int channelNumber)throws Exception{
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		ChannelSplitter splitter = new ChannelSplitter();
		currentImage = splitter.split(currentImage[0]);
		return currentImage[channelNumber];
	}



	/**
	 * //TODO to Simplify
	 * Method to check multichannel
	 * @throws Exception
	 */

	public void setChannelNumbers() throws Exception{
		ImagePlus[] currentImage = BF.openImagePlus(this.m_imageFilePath);
		ChannelSplitter channelSplitter = new ChannelSplitter();
		currentImage = channelSplitter.split(currentImage[0]);
		this.m_rawImg=currentImage[0];
        if(currentImage.length>1){
			this.m_channelNumbers=currentImage.length;

		}
	}


	/**
	 * Method computing OTSU threshold and creating segmented image from this threshold.
	 * Case where OTSU threshold is under 20 computation using only half of last slice
	 * (Skipping top slice with lot of noise)
	 * If OTSU threshold is still under 20 threshold threshold value is 20.
	 *
	 * //TODO AJOUTER PARAMETTRAGE DES SLICES
	 * // TODO MODIFICATION DU THRESHOLD
	 * //TODO AJOUTER MESSAGE POUR UTILISATEUR SI THRESHOLD INFERIEUR A 20
	 * 	//TODO AJOUTER INFO DU THRESHLOD DANS LE OUTPUT VOIR PCA
	 */
	public void thresholdKernels(){
		GaussianBlur3D.blur(this.m_imageSeg, 0.5,0.5,1);
		Thresholding thresholding = new Thresholding();
		int thresh	=thresholding.computeOtsuThreshold(this.m_imageSeg);
		if(thresh <20) {
			ImagePlus imp2 = new Duplicator().run(this.m_imageSeg, this.m_autocropParameters.getThresholdOTSUcomputing()/2, this.m_imageSeg.getStackSize());
			int thresh2 = thresholding.computeOtsuThreshold(imp2);
			if (thresh2<20)
				thresh=20;
			else
				thresh=thresh2;
		}
        this.OTSUthreshold=thresh;
		this.m_imageSeg = this.generateSegmentedImage(this.m_imageSeg, thresh);

	}

	public void computeConnectcomponent(){
		this.m_imageSeg_labelled = BinaryImages.componentsLabeling(this.m_imageSeg, 26, 32);
	}

	public void componentSizeFilter(){

	}
	public void componentBorderFilter(){

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
			ImagePlus imagePlusLabels = BinaryImages.componentsLabeling(m_imageSeg, 26, 32);

			Histogram histogram = new Histogram ();
			histogram.run(m_imageSeg);
			histogram.getHistogram();
			HashMap<Double , Integer> parcour =histogram.getHistogram();
			for(Map.Entry<Double , Integer> entry : parcour.entrySet()) {
				Double cle = entry.getKey();
				Integer valeur = entry.getValue();
				//System.out.println(cle+"   "+valeur+"\n");

			}



			int[] test=new int [10];
			LabelImages.replaceLabels(m_imageSeg,test,12);


			Box box = null;
			//ArrayList initialisation
			this.m_nbOfNuc = connectedComponent.getNumberOfComponents();
			System.out.println("la taille des boxes " +boxes.size());

			for(short i = 0; i< connectedComponent.getNumberOfComponents();++i) {
				boxes.add(new Box(Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE));
				System.out.println("La box " +i+"\n"
						+Short.MAX_VALUE+" "+ Short.MIN_VALUE+" "+Short.MAX_VALUE+" "+Short.MIN_VALUE+" "+Short.MAX_VALUE+" "+Short.MIN_VALUE+"\n"
				);
			}

			// Check the predicate
			for (short k = 0; k < this.m_imageSeg.getNSlices(); ++k) {
				for (short i = 0; i < this.m_imageSeg.getWidth(); ++i) {
					for (short j = 0; j < this.m_imageSeg.getHeight(); ++j) {
						// if label different of the background
						if (connectedComponent.getLabel(i, j, k) > 0) {
							System.out.println("la taille des boxes "+ connectedComponent.getLabel(i, j, k));
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
	public void cropKernels(ArrayList <Box> boxes)throws IOException, FormatException, Exception {
		Directory dirOutput= new Directory(this.m_outputDirPath+File.separator+this.m_outputFilesPrefix);
		dirOutput.CheckAndCreateDir();
		for (int y =0 ;y<=this.m_channelNumbers;y++) {

			for (short i = 0; i < boxes.size(); ++i) {
				Box box = boxes.get(i);
				int xmin = box.getXMin() - this.m_autocropParameters.getxCropBoxSize();
				int ymin = box.getYMin() - this.m_autocropParameters.getxCropBoxSize();
				int zmin = box.getZMin() - this.m_autocropParameters.getzCropBoxSize();
				String coord = box.getXMin() + "_" + box.getYMin() + "_" + box.getZMin();
				this.m_boxCoordinates.add(this.m_outputDirPath + File.separator + this.m_outputFilesPrefix + "_" + coord + i + "\t" + box.getXMin() + "\t" + box.getXMax() + "\t" + box.getYMin() + "\t" + box.getYMax() + "\t" + box.getZMin() + "\t" + box.getZMax());
				if (xmin <= 0)
					xmin = 1;
				if (ymin <= 0)
					ymin = 1;
				if (zmin <= 0)
					zmin = 1;

				int width = box.getXMax() + (2*this.m_autocropParameters.getxCropBoxSize()) - box.getXMin();
				int height = box.getYMax() + (2*this.m_autocropParameters.getxCropBoxSize()) - box.getYMin();
				int depth = box.getZMax() + (2*this.m_autocropParameters.getzCropBoxSize()) - box.getZMin();
				if (width + xmin >= this.m_imageSeg.getWidth())
					width -= (width + xmin) - this.m_imageSeg.getWidth();

				if (height + ymin >= this.m_imageSeg.getHeight())
					height -= (height + ymin) - this.m_imageSeg.getHeight();

				if (depth + zmin >= this.m_imageSeg.getNSlices())
					depth -= (depth + zmin) - this.m_imageSeg.getNSlices();

				ImagePlus imgResu = cropImage(xmin, ymin, zmin, width, height, depth,y);

				Calibration cal = this.m_rawImg.getCalibration();
				imgResu.setCalibration(cal);
				OutputTiff fileOutput = new OutputTiff(this.m_outputDirPath + this.m_outputFilesPrefix + File.separator + this.m_outputFilesPrefix + "_" + coord + "_" + i +"_C"+y+".tif");
                this.m_infoImageAnalyse=this.m_infoImageAnalyse+m_outputDirPath + this.m_outputFilesPrefix + File.separator + this.m_outputFilesPrefix + "_" + coord + "_" + i +"_C"+y+".tif\t"
                +y+"\t"
                        +i+"\t"
                        +xmin+"\t"
                        +ymin+"\t"
                        +zmin+"\t"
                        +width+"\t"
                        +height+"\t"
                        +depth+"\n";

				fileOutput.SaveImage(imgResu);


				this.m_outputFile.add(this.m_outputDirPath + File.separator + this.m_outputFilesPrefix + File.separator + this.m_outputFilesPrefix + "_" + coord + "_" + i + ".tif");
			}
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
	 *
	 * //TODO FACTORING THRESHOLD FONCTION
	 */
	private int computeOtsuThreshold (ImagePlus imagePlusInput) {
		AutoThresholder autoThresholder = new AutoThresholder();
		ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
		int [] tHisto = imageStatistics.histogram;
		return autoThresholder.getThreshold(AutoThresholder.Method.Otsu,tHisto);
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
	 *
	 * Crop of the bounding box on the image. The coordinates are inputs of this methods
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
	public ImagePlus cropImage(int xmin, int ymin, int zmin, int width, int height, int depth,int channelNumber)throws IOException, FormatException,Exception {
		ImporterOptions options = new ImporterOptions();
		options.setId(this.m_imageFilePath);
		options.setAutoscale(true);
		options.setCrop(true);
		ImagePlus[] imps = BF.openImagePlus(options);
		ImagePlus sort = new ImagePlus();
        sort.setStack(imps[channelNumber].getStack().crop(xmin, ymin ,zmin,width, height,depth));
		return sort;

	}

	/**
	 *Getter of the number of nuclei contained in the input image
	 *
	 * @return int the nb of nuclei
	 */
	public int getNbOfNuc(){
		return this.m_nbOfNuc;
	}

    /**
     *
     * @return Header current image info analyse
     */
	public String getSpecificImageInfo(){
        Calibration cal = this.m_rawImg.getCalibration();
        return "#Calibration image: x:"+ cal.pixelDepth+"-y:"+cal.pixelWidth+"-z:"+cal.pixelHeight+"\n"
                +"#Image: "+this.m_imageFilePath+"\n"
                +"#OTSU threshold: "+this.OTSUthreshold+"\n";

    }

    /**
     *
     * @return columns name for output text file
     */
    public String getColoneName() {
        String colonneName = "FileName\tChannel\tCrop_number\tX_start\tY_start\tZ_start\twidth\theight\tdepth\n";
        return colonneName;
    }

    /**
     * Write analyse info in output texte file
     * @throws IOException
     */
    public void writeAnalyseInfo() throws IOException {
        OutputTexteFile resultFileOutput=new OutputTexteFile(this.m_outputDirPath + File.separator+ this.m_outputFilesPrefix + File.separator + this.m_outputFilesPrefix);
        resultFileOutput.SaveTexteFile(this.m_infoImageAnalyse);

    }
    public String getImageCropInfo(){
		return this.m_imageFilePath+"\t"+getNbOfNuc()+"\n";
	}
}