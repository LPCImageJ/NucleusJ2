package gred.nucleus.autocrop;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.ImageWrapper;
import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;
import gred.nucleus.files.OutputTextFile;
import ij.IJ;

import java.io.File;
import java.util.List;
import java.util.concurrent.ExecutionException;


/**
 * Core method calling the autocrop method.
 * <p>This method can be run on only one file or on directory containing multiple tuple file.
 * <p>This class will call AutoCrop class to detect nuclei in the image.
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class AutoCropCalling {
	/** Column names */
	private static final String HEADERS = "FileName\tNumberOfCrop\tOTSUThreshold\tDefaultOTSUThreshold\n";
	
	/** image prefix name */
	private String             prefix                = "";
	/** Get general information of cropping analyse */
	private String             outputCropGeneralInfo = "#HEADER\n";
	/** Parameters crop analyse */
	private AutocropParameters autocropParameters;
	
	
	/** Constructor Create the output directory if it doesn't exist. */
	public AutoCropCalling() {
	}
	
	
	public AutoCropCalling(AutocropParameters autocropParameters) {
		this.autocropParameters = autocropParameters;
		this.outputCropGeneralInfo = autocropParameters.getAnalysisParameters() + HEADERS;
	}
	
	
	/**
	 * Run auto crop on image's folder: -If input is a file: open the image with bio-formats plugin to obtain the
	 * metadata then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with
	 * bio-formats, run the auto crop.
	 */
	public void runFolder() {
		Directory directoryInput = new Directory(this.autocropParameters.getInputFolder());
		directoryInput.listImageFiles(this.autocropParameters.getInputFolder());
		directoryInput.checkIfEmpty();
		directoryInput.checkAndActualiseNDFiles();
		for (short i = 0; i < directoryInput.getNumberFiles(); ++i) {
			runFile(directoryInput.getFile(i).getAbsolutePath());
		}
		saveGeneralInfo();
	}
	
	
	/**
	 * Run auto crop on one image : -If input is a file: open the image with bio-formats plugin to obtain the metadata
	 * then run the auto crop. -If input is directory, listed the file, foreach tif file loaded file with bio-formats,
	 * run the auto crop.
	 *
	 * @param file
	 */
	public void runFile(String file) {
		File currentFile = new File(file);
		System.out.println("Current file " + currentFile.getAbsolutePath());
		String     fileImg          = currentFile.toString();
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this.prefix = outPutFilesNames.prefixNameFile();
		try {
			AutoCrop autoCrop = new AutoCrop(currentFile, this.prefix, this.autocropParameters);
			autoCrop.thresholdKernels();
			autoCrop.computeConnectedComponent();
			autoCrop.componentBorderFilter();
			autoCrop.componentSizeFilter();
			autoCrop.computeBoxes2();
			autoCrop.addCROPParameter();
			autoCrop.boxIntersection();
			autoCrop.cropKernels2();
			autoCrop.writeAnalyseInfo();
			AnnotateAutoCrop test = new AnnotateAutoCrop(autoCrop.getFileCoordinates(),
			                                             currentFile,
			                                             this.autocropParameters.getOutputFolder() + File.separator,
			                                             this.prefix,
			                                             this.autocropParameters);
			test.run();
			this.outputCropGeneralInfo += autoCrop.getImageCropInfo();
		} catch (Exception e) {
			IJ.error("Cannot run autocrop on " + currentFile.getName());
			e.printStackTrace();
		}
	}
	
	public void saveGeneralInfo(){
		System.out.println(this.autocropParameters.getInputFolder() + "result_Autocrop_Analyse");
		OutputTextFile resultFileOutput =
				new OutputTextFile(this.autocropParameters.getOutputFolder() + "result_Autocrop_Analyse.csv");
		resultFileOutput.saveTextFile(this.outputCropGeneralInfo, true);
	}
	
	
	public void runImageOMERO(ImageWrapper image, Long[] outputsDatImages, Client client) throws Exception {
		String fileImg = image.getName();
		System.out.println("Current file : " + fileImg);
		FilesNames outPutFilesNames = new FilesNames(fileImg);
		this.prefix = outPutFilesNames.prefixNameFile();
		AutoCrop autoCrop = new AutoCrop(image, autocropParameters, client);
		autoCrop.thresholdKernels();
		autoCrop.computeConnectedComponent();
		autoCrop.componentBorderFilter();
		autoCrop.componentSizeFilter();
		autoCrop.computeBoxes2();
		autoCrop.addCROPParameter();
		autoCrop.boxIntersection();
		autoCrop.cropKernelsOMERO(image, outputsDatImages, client);
		autoCrop.writeAnalyseInfoOMERO(outputsDatImages[autocropParameters.getChannelToComputeThreshold()], client);
		this.outputCropGeneralInfo += autoCrop.getImageCropInfoOmero(image.getName());
	}
	
	
	public void runSeveralImageOMERO(List<ImageWrapper> images, Long[] outputsDatImages, Client client)
	throws Exception {
		for (ImageWrapper image : images) runImageOMERO(image, outputsDatImages, client);
		
		saveGeneralInfoOmero(client, outputsDatImages);
	}
	
	public void saveGeneralInfoOmero(Client client, Long[] outputsDatImages) throws Exception{
		String resultPath = this.autocropParameters.getOutputFolder() + "result_Autocrop_Analyse.csv";
		File resultFile = new File(resultPath);
		OutputTextFile resultFileOutput = new OutputTextFile(resultPath);
		resultFileOutput.saveTextFile(this.outputCropGeneralInfo, false);
		
		client.getDataset(outputsDatImages[autocropParameters.getChannelToComputeThreshold()]).addFile(client, resultFile);
		boolean deleted = resultFile.delete();
		if (!deleted) System.err.println("File not deleted: " + resultPath);
	}
	
	/**
	 * List of columns names in csv coordinates output file.
	 *
	 * @return columns name
	 */
	public String getResultsColumnNames() {
		return HEADERS;
	}
	
}
