package gred.nucleus.segmentation;

import fr.igred.omero.Client;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.roi.ROIWrapper;
import fr.igred.omero.repository.DatasetWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import gred.nucleus.files.OutputTextFile;
import gred.nucleus.core.ConvexHullSegmentation;
import gred.nucleus.core.NucleusSegmentation;
import gred.nucleus.nucleuscaracterisations.NucleusAnalysis;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.StackConverter;
import loci.formats.FormatException;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;


/**
 * This class call the different segmentation methods available to detect the nucleus. The Otsu method modified and the
 * gift wrapping 3D. Methods can be call for analysis of several images or only one. The gift wrapping is initialized by
 * the Otsu method modified, then the gift wrapping algorithm process the result obtain with the first method. If the
 * first method doesn't detect a nucleus, a message is print on the console.
 * <p>
 * if the nucleus input image is 16bit, a preprocess is done to convert it in 8bit, and also increase the contrast and
 * decrease the noise, then the 8bits image is used for the nuclear segmentation.
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class SegmentationCalling {
	String prefix = "";
	/** ImagePlus raw image */
	private ImagePlus imgInput = new ImagePlus();
	/** ImagePlus segmented image */
	private ImagePlus imgSeg   = new ImagePlus();
	/** String of of the path for the output files */
	private String    output;
	/** String of the input dir for several nuclei analysis */
	private String    inputDir = "";
	
	private SegmentationParameters segmentationParameters;
	
	private String outputCropGeneralInfoOTSU;
	private String outputCropGeneralInfoGIFT;
	
	
	public SegmentationCalling() {
	}
	
	
	/**
	 * Constructor for ImagePlus input
	 *
	 * @param segmentationParameters List of parameters in config file.
	 */
	public SegmentationCalling(SegmentationParameters segmentationParameters) {
		this.segmentationParameters = segmentationParameters;
		this.outputCropGeneralInfoOTSU =
				this.segmentationParameters.getAnalysisParameters() + getResultsColumnNames();
		this.outputCropGeneralInfoGIFT =
				this.segmentationParameters.getAnalysisParameters() + getResultsColumnNames();
	}
	
	
	public SegmentationCalling(String inputDir, String outputDir) {
		this.inputDir = inputDir;
		this.output = outputDir;
		this.outputCropGeneralInfoOTSU =
				this.segmentationParameters.getAnalysisParameters() + getResultsColumnNames();
		this.outputCropGeneralInfoGIFT =
				this.segmentationParameters.getAnalysisParameters() + getResultsColumnNames();
	}
	
	
	/**
	 * Constructor for ImagePlus input
	 *
	 * @param img       ImagePlus raw image
	 * @param vMin      volume min of the detected object
	 * @param vMax      volume max of the detected object
	 * @param outputImg String of of the path to save the img of the segmented nucleus.
	 */
	public SegmentationCalling(ImagePlus img, short vMin, int vMax, String outputImg) {
		this.segmentationParameters.setMinVolumeNucleus(vMin);
		this.segmentationParameters.setMaxVolumeNucleus(vMax);
		this.imgInput = img;
		this.output = outputImg + File.separator + "Segmented" + this.imgInput.getTitle();
	}
	
	
	/**
	 * Constructor for ImagePlus input
	 *
	 * @param img  ImagePlus raw image
	 * @param vMin volume min of the detected object
	 * @param vMax volume max of the detected object
	 */
	public SegmentationCalling(ImagePlus img, short vMin, int vMax) {
		this.segmentationParameters.setMinVolumeNucleus(vMin);
		this.segmentationParameters.setMaxVolumeNucleus(vMax);
		this.imgInput = img;
	}
	
	
	/**
	 * Constructor for directory input
	 *
	 * @param inputDir  String path of the input containing the tif/TIF file
	 * @param outputDir String of of the path to save results img of the segmented nucleus.
	 * @param vMin      volume min of the detected object
	 * @param vMax      volume max of the detected object
	 */
	public SegmentationCalling(String inputDir, String outputDir, short vMin, int vMax) {
		this.segmentationParameters.setMinVolumeNucleus(vMin);
		this.segmentationParameters.setMaxVolumeNucleus(vMax);
		this.inputDir = inputDir;
		this.output = outputDir;
		Directory dirOutput = new Directory(this.output);
		dirOutput.checkAndCreateDir();
		this.output = dirOutput.getDirPath();
	}
	
	
	/**
	 * @return ImagePlus the segmented nucleus
	 *
	 * @deprecated Method to run an ImagePlus input the method will call method in NucleusSegmentation and
	 * ConvexHullSegmentation to segment the input nucleus. if the input boolean is true the gift wrapping will be use,
	 * if false the Otsu modified method will be used. If a segmentation results is find the method will then computed
	 * the different parameters with the NucleusAnalysis class, results will be print in the console. If no nucleus is
	 * detected a log message is print in teh console
	 */
	public int runOneImage() throws Exception {
		ImagePlus imgSeg = this.imgInput;
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(imgSeg,
		                                                                  this.segmentationParameters.getMinVolumeNucleus(),
		                                                                  this.segmentationParameters.getMaxVolumeNucleus(),
		                                                                  this.segmentationParameters);
		
		Calibration cal = imgSeg.getCalibration();
		if (imgSeg.getType() == ImagePlus.GRAY16) {
			this.preProcessImage(imgSeg);
		}
		
		imgSeg = nucleusSegmentation.applySegmentation(imgSeg);
		if (nucleusSegmentation.getBestThreshold() == -1) {
			System.out.println("Segmentation error: \nNo object is detected between " +
			                   this.segmentationParameters.getMinVolumeNucleus() +
			                   "and" +
			                   this.segmentationParameters.getMaxVolumeNucleus());
		} else {
			System.out.println("otsu modified threshold: " + nucleusSegmentation.getBestThreshold() + "\n");
			if (this.segmentationParameters.getGiftWrapping()) {
				ConvexHullSegmentation nuc = new ConvexHullSegmentation();
				imgSeg = nuc.runGIFTWrapping(imgSeg, this.segmentationParameters);
			}
			imgSeg.setTitle(this.output);
			if (!this.output.equals("")) {
				saveFile(imgSeg, this.output);
			}
			NucleusAnalysis nucleusAnalysis =
					new NucleusAnalysis(this.imgInput, imgSeg, this.segmentationParameters);
			// System.out.println(nucleusAnalysis.nucleusParameter3D());
		}
		this.imgSeg = imgSeg;
		return nucleusSegmentation.getBestThreshold();
	}
	
	
	/**
	 * getter of the image segmented
	 *
	 * @return
	 */
	public ImagePlus getImageSegmented() {
		return this.imgSeg;
	}
	
	
	/**
	 * Method to run the nuclear segmentation of images stocked in input dir. First listing of the tif files contained
	 * in input dir. then for each images: the method will call method in NucleusSegmentation and ConvexHullSegmentation
	 * to segment the input nucleus. if the input boolean is true the gift wrapping will be use, if false the Otsu
	 * modified method will be used. If a segmentation results is find the method will then computed the different
	 * parameters with the NucleusAnalysis class, and save in file in the outputDir. If no nucleus is detected a log
	 * message is print in the console
	 * <p>
	 * Open the image with bio-formats plugin to obtain the metadata: ImagePlus[] imgTab = BF.openImagePlus(fileImg);
	 *
	 * @return String with the name files which failed in the segmentation step
	 *
	 * @throws IOException     if file doesn't existed
	 * @throws FormatException Bio-formats exception
	 */
	public String runSeveralImages2() throws Exception {
		String        log            = "";
		StringBuilder info           = new StringBuilder();
		Directory     directoryInput = new Directory(this.segmentationParameters.getInputFolder());
		directoryInput.listImageFiles(this.segmentationParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File       currentFile      = directoryInput.getFile(i);
			String     fileImg          = currentFile.toString();
			FilesNames outPutFilesNames = new FilesNames(fileImg);
			this.prefix = outPutFilesNames.prefixNameFile();
			System.out.println("Current image in process " + currentFile);
			
			String timeStampStart =
					new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
			System.out.println("Start :" + timeStampStart);
			
			NucleusSegmentation nucleusSegmentation =
					new NucleusSegmentation(currentFile, this.prefix, this.segmentationParameters);
			nucleusSegmentation.preProcessImage();
			nucleusSegmentation.findOTSUMaximisingSphericity();
			nucleusSegmentation.checkBadCrop(this.segmentationParameters.inputFolder);
			nucleusSegmentation.saveOTSUSegmented();
			info.append(nucleusSegmentation.getImageCropInfoOTSU());
			nucleusSegmentation.saveGiftWrappingSeg();
			info.append(nucleusSegmentation.getImageCropInfoGIFT());
			
			timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
			System.out.println("Fin :" + timeStampStart);
			
		}
		this.outputCropGeneralInfoOTSU += info.toString();
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(this.segmentationParameters.getOutputFolder()
		                                                         + directoryInput.getSeparator()
		                                                         + "OTSU"
		                                                         + directoryInput.getSeparator()
		                                                         + "result_Segmentation_Analyse_OTSU.csv");
		resultFileOutputOTSU.saveTextFile(this.outputCropGeneralInfoOTSU, true);
		if (this.segmentationParameters.getGiftWrapping()) {
			OutputTextFile resultFileOutputGIFT = new OutputTextFile(this.segmentationParameters.getOutputFolder()
			                                                         + directoryInput.getSeparator()
			                                                         + "GIFT"
			                                                         + directoryInput.getSeparator()
			                                                         + "result_Segmentation_Analyse_GIFT.csv");
			resultFileOutputGIFT.saveTextFile(this.outputCropGeneralInfoGIFT, true);
		}
		
		return log;
	}
	
	
	public String runOneImage(String filePath) throws Exception {
		String     log              = "";
		File       currentFile      = new File(filePath);
		String     fileImg          = currentFile.toString();
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this.prefix = outPutFilesNames.prefixNameFile();
		System.out.println("Current image in process " + currentFile);
		
		String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Start :" + timeStampStart);
		NucleusSegmentation nucleusSegmentation =
				new NucleusSegmentation(currentFile, this.prefix, this.segmentationParameters);
		nucleusSegmentation.preProcessImage();
		nucleusSegmentation.findOTSUMaximisingSphericity();
		nucleusSegmentation.checkBadCrop(this.segmentationParameters.inputFolder);
		nucleusSegmentation.saveOTSUSegmented();
		this.outputCropGeneralInfoOTSU += nucleusSegmentation.getImageCropInfoOTSU();
		nucleusSegmentation.saveGiftWrappingSeg();
		this.outputCropGeneralInfoGIFT += nucleusSegmentation.getImageCropInfoGIFT();
		
		timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Fin :" + timeStampStart);
		return log;
	}
	
	
	public String runOneImageOMERO(ImageWrapper image, Long output, Client client) throws Exception {
		String log = "";
		
		String fileImg = image.getName();
		System.out.println("Current image in process " + fileImg);
		
		String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Start :" + timeStampStart);
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation(image, this.segmentationParameters, client);
		nucleusSegmentation.preProcessImage();
		nucleusSegmentation.findOTSUMaximisingSphericity();
		nucleusSegmentation.checkBadCrop(image, client);
		
		nucleusSegmentation.saveOTSUSegmentedOMERO(client, output);
		this.outputCropGeneralInfoOTSU += nucleusSegmentation.getImageCropInfoOTSU();
		nucleusSegmentation.saveGiftWrappingSegOMERO(client, output);
		this.outputCropGeneralInfoGIFT += nucleusSegmentation.getImageCropInfoGIFT();
		
		timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Fin :" + timeStampStart);
		
		return log;
	}
	
	
	public String runSeveralImageOMERO(List<ImageWrapper> images, Long output, Client client) throws Exception {
		StringBuilder log = new StringBuilder();
		
		for (ImageWrapper image : images) {
			log.append(runOneImageOMERO(image, output, client));
		}
		
		DatasetWrapper dataset = client.getProject(output).getDatasets("OTSU").get(0);
		
		String path =
				new java.io.File(".").getCanonicalPath() + dataset.getName() + "result_Segmentation_Analyse.csv";
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(path);
		resultFileOutputOTSU.saveTextFile(this.outputCropGeneralInfoOTSU, false);
		
		File file = new File(path);
		dataset.addFile(client, file);
		boolean deleted = file.delete();
		if (!deleted) System.err.println("File not deleted: " + path);
		
		if (this.segmentationParameters.getGiftWrapping()) {
			dataset = client.getProject(output).getDatasets("GIFT").get(0);
			OutputTextFile resultFileOutputGIFT = new OutputTextFile(path);
			resultFileOutputGIFT.saveTextFile(this.outputCropGeneralInfoGIFT, false);
			
			file = new File(path);
			dataset.addFile(client, file);
			deleted = file.delete();
			if (!deleted) System.err.println("File not deleted: " + path);
		}
		
		return log.toString();
	}
	
	
	public String runOneImageOMERObyROIs(ImageWrapper image, Long output, Client client) throws Exception {
		StringBuilder info = new StringBuilder();
		
		List<ROIWrapper> rois = image.getROIs(client);
		
		String log = "";
		
		String fileImg = image.getName();
		System.out.println("Current image in process " + fileImg);
		
		String timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Start :" + timeStampStart);
		
		int i = 0;
		
		for (ROIWrapper roi : rois) {
			System.out.println("Current ROI in process : " + i);
			
			NucleusSegmentation nucleusSegmentation =
					new NucleusSegmentation(image, roi, i, this.segmentationParameters, client);
			nucleusSegmentation.preProcessImage();
			nucleusSegmentation.findOTSUMaximisingSphericity();
			nucleusSegmentation.checkBadCrop(roi, client);
			
			
			nucleusSegmentation.saveOTSUSegmentedOMERO(client, output);
			info.append(nucleusSegmentation.getImageCropInfoOTSU());
			nucleusSegmentation.saveGiftWrappingSegOMERO(client, output);
			info.append(nucleusSegmentation.getImageCropInfoGIFT());
			
			i++;
		}
		this.outputCropGeneralInfoOTSU += info.toString();
		
		timeStampStart = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
		System.out.println("Fin :" + timeStampStart);
		
		DatasetWrapper dataset = client.getProject(output).getDatasets("OTSU").get(0);
		String path =
				new java.io.File(".").getCanonicalPath() + "result_Segmentation_Analyse.csv";
		OutputTextFile resultFileOutputOTSU = new OutputTextFile(path);
		resultFileOutputOTSU.saveTextFile(this.outputCropGeneralInfoOTSU, false);
		
		File file = new File(path);
		dataset.addFile(client, file);
		boolean deleted = file.delete();
		if (!deleted) System.err.println("File not deleted: " + path);
		
		if (this.segmentationParameters.getGiftWrapping()) {
			dataset = client.getProject(output).getDatasets("GIFT").get(0);
			OutputTextFile resultFileOutputGIFT = new OutputTextFile(path);
			resultFileOutputGIFT.saveTextFile(this.outputCropGeneralInfoGIFT, false);
			
			file = new File(path);
			dataset.addFile(client, file);
			deleted = file.delete();
			if (!deleted) System.err.println("File not deleted: " + path);
		}
		
		return log;
	}
	
	
	public String runSeveralImageOMERObyROIs(List<ImageWrapper> images, Long output, Client client) throws Exception {
		StringBuilder log = new StringBuilder();
		
		for (ImageWrapper image : images) {
			log.append(runOneImageOMERObyROIs(image, output, client));
		}
		
		return log.toString();
	}
	
	
	/**
	 * Method which save the image in the directory.
	 *
	 * @param imagePlusInput Image to be save
	 * @param pathFile       path of directory
	 */
	private void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiffStack(pathFile);
	}
	
	
	/**
	 * 16bits image preprocessing normalised the histogram distribution apply a gaussian filter to smooth the signal
	 * convert the image in 8bits
	 *
	 * @param img 16bits ImagePlus
	 */
	//TODO A ENLEVER APRES RESTRUCTURATION ATTENTION INTEGRATION DANS LES FENETRES GRAPHIQUES PAS ENCORE UPDATE DC CA CRASH!!!!!
	private void preProcessImage(ImagePlus img) {
		ContrastEnhancer enh = new ContrastEnhancer();
		enh.setNormalize(true);
		enh.setUseStackHistogram(true);
		enh.setProcessStack(true);
		enh.stretchHistogram(img.getProcessor(), 0.05);
		GaussianBlur3D.blur(img, 0.5, 0.5, 1);
		StackConverter stackConverter = new StackConverter(img);
		stackConverter.convertToGray8();
	}
	
	
	public String getResultsColumnNames() {
		return "NucleusFileName\t" +
		       "Volume\t" +
		       "Flatness\t" +
		       "Elongation\t" +
		       "Esr\t" +
		       "SurfaceArea\t" +
		       "Sphericity\t" +
		       "MeanIntensityNucleus\t" +
		       "MeanIntensityBackground\t" +
		       "StandardDeviation\t" +
		       "MinIntensity\t" +
		       "MaxIntensity\t" +
		       "MedianIntensityImage\t" +
		       "MedianIntensityNucleus\t" +
		       "MedianIntensityBackground\t" +
		       "ImageSize\t" +
		       "OTSUThreshold\n";
	}
	
}