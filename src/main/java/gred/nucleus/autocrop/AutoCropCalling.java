package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.FilesInputOutput.OutputTextFile;
import ij.IJ;
import loci.formats.FormatException;

import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * Core method calling the autocrop method. This method can be run on only one file or on directory containing multiple
 * tuple file. This class will call AutoCrop class to detect nuclei in the image.
 *
 * @author Tristan Dubos and Axel Poulet
 */

public class AutoCropCalling {
	
	
	/**
	 * image prefix name
	 */
	private String             _prefix                 = "";
	/**
	 * Get general information of cropping analyse
	 */
	private String             m_outputCropGeneralInfo = "#HEADER\n";
	/**
	 * Parameters crop analyse
	 */
	private AutocropParameters m_autocropParameters;
	
	/**
	 * Constructor Create the output directory if he isn't existed.
	 */
	public AutoCropCalling() {
	
	}
	
	public AutoCropCalling(AutocropParameters autocropParameters) {
		this.m_autocropParameters = autocropParameters;
		this.m_outputCropGeneralInfo = autocropParameters.getAnalyseParameters()
		                               + getColnameResult();
	}
	
	/**
	 * Run auto crop on image's folder: -If input is a file: open the image with bioformat plugin to obtain the metadata
	 * then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with bioformat, run
	 * the auto crop.
	 *
	 * @throws IOException     if file problem
	 * @throws FormatException Bioformat exception
	 */
	public void runFolder() throws Exception {
		Directory directoryInput = new Directory(this.m_autocropParameters.getInputFolder());
		directoryInput.listImageFiles(this.m_autocropParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		directoryInput.checkAndActualiseNDFiles();
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			File currentFile = directoryInput.getFile(i);
			System.out.println("Current file " + currentFile.getAbsolutePath());
			String     fileImg          = currentFile.toString();
			FilesNames outPutFilesNames = new FilesNames(fileImg);
			this._prefix = outPutFilesNames.PrefixeNameFile();
			try {
				AutoCrop autoCrop = new AutoCrop(currentFile, this._prefix, this.m_autocropParameters);
				autoCrop.thresholdKernels();
				autoCrop.computeConnectcomponent();
				autoCrop.componentBorderFilter();
				autoCrop.componentSizeFilter();
				autoCrop.computeBoxes2();
				autoCrop.addCROP_parameter();
				autoCrop.boxIntersection();
				autoCrop.cropKernels2();
				autoCrop.writeAnalyseInfo();
				annotAutoCrop test = new annotAutoCrop(autoCrop.getFileCoordinates(),
				                                       currentFile,
				                                       this.m_autocropParameters.getOutputFolder(),
				                                       this._prefix,
				                                       this.m_autocropParameters);
				test.run();
				this.m_outputCropGeneralInfo = this.m_outputCropGeneralInfo + autoCrop.getImageCropInfo();
			} catch (Exception e) {
				IJ.error("Cannot run autocrop on " + currentFile.getName());
				e.printStackTrace();
			}
		}
		System.out.println(this.m_autocropParameters.getInputFolder() + "result_Autocrop_Analyse");
		OutputTextFile resultFileOutput =
				new OutputTextFile(this.m_autocropParameters.getOutputFolder() + "result_Autocrop_Analyse.csv");
		resultFileOutput.SaveTextFile(this.m_outputCropGeneralInfo);
	}
	
	
	/**
	 * Run auto crop on one image : -If input is a file: open the image with bioformat plugin to obtain the metadata
	 * then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with bioformat, run
	 * the auto crop.
	 *
	 * @param file
	 *
	 * @throws Exception
	 */
	public void runFile(String file) throws Exception {
		File       currentFile      = new File(file);
		String     fileImg          = currentFile.toString();
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this._prefix = outPutFilesNames.PrefixeNameFile();
		AutoCrop autoCrop = new AutoCrop(currentFile, this._prefix, this.m_autocropParameters);
		autoCrop.thresholdKernels();
		autoCrop.computeConnectcomponent();
		autoCrop.componentBorderFilter();
		autoCrop.componentSizeFilter();
		autoCrop.computeBoxes2();
		autoCrop.addCROP_parameter();
		autoCrop.boxIntersection();
		autoCrop.cropKernels2();
		autoCrop.writeAnalyseInfo();
		annotAutoCrop test = new annotAutoCrop(autoCrop.getFileCoordinates(),
		                                       currentFile,
		                                       this.m_autocropParameters.getOutputFolder() + currentFile.separator,
		                                       this._prefix,
		                                       this.m_autocropParameters);
		test.run();
		this.m_outputCropGeneralInfo = this.m_outputCropGeneralInfo + autoCrop.getImageCropInfo();
	}
	
	public void runImageOmero(ImageContainer image, Long[] outputsDatImages, Client client) throws Exception {
		String fileImg = image.getName();
		System.out.println("Current file : " + fileImg);
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this._prefix = outPutFilesNames.PrefixeNameFile();
		AutoCrop autoCrop = new AutoCrop(image, this.m_autocropParameters, client);
		autoCrop.thresholdKernels();
		autoCrop.computeConnectcomponent();
		autoCrop.componentBorderFilter();
		autoCrop.componentSizeFilter();
		autoCrop.computeBoxes2();
		autoCrop.addCROP_parameter();
		autoCrop.boxIntersection();
		autoCrop.cropKernelsOmero(image, outputsDatImages, client);
		autoCrop.writeAnalyseInfoOmero(outputsDatImages[this.m_autocropParameters.getChannelToComputeThreshold()],
		                               client);
		this.m_outputCropGeneralInfo = this.m_outputCropGeneralInfo + autoCrop.getImageCropInfo();
	}
	
	public void runSeveralImageOmero(List<ImageContainer> images, Long[] outputsDatImages, Client client)
	throws Exception {
		for (ImageContainer image : images) runImageOmero(image, outputsDatImages, client);
	}
	
	
	/**
	 * List of columns name in csv coordinates output file.
	 *
	 * @return columns name
	 */
	
	public String getColnameResult() {
		return "FileName\tNumberOfCrop\tOTSUThreshold\tDefaultOTSUThreshold\n";
	}
}


