package gred.nucleus.mains;

import fr.igred.omero.Client;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.autocrop.*;
import gred.nucleus.cli.*;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.exceptions.FileInOut;
import gred.nucleus.machinelearning.ComputeNucleiParametersML;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.io.FileSaver;
import loci.common.DebugTools;
import loci.formats.FormatException;
import org.apache.commons.cli.*;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;


public class Main {
	
	/**
	 * Method to run autocrop with only input output folder and with default parameters which are:
	 * <p>
	 * xCropBoxSize:40 yCropBoxSize:40 zCropBoxSize:20 thresholdOTSUComputing:20 slicesOTSUComputing:0
	 * channelToComputeThreshold:1 maxVolumeNucleus:2147483647 minVolumeNucleus:1
	 *
	 * @param inputDirectory  path to the raw image's folder
	 * @param outputDirectory path to output folder analysis
	 */
	public static void runAutoCropFolder(String inputDirectory, String outputDirectory) {
		AutocropParameters autocropParameters = new AutocropParameters(inputDirectory, outputDirectory);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	/**
	 * Method to run autocrop with input folder, output folder and with config file analysis :
	 *
	 * @param inputDirectory  path to the raw image's folder
	 * @param outputDirectory path to output folder analysis
	 * @param pathToConfig    path to config file
	 */
	public static void runAutoCropFolder(String inputDirectory, String outputDirectory, String pathToConfig) {
		AutocropParameters autocropParameters = new AutocropParameters(inputDirectory, outputDirectory, pathToConfig);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFolder();
	}
	
	
	/**
	 * Method to run autocrop with input folder, output folder :
	 *
	 * @param inputDirectory  path to the raw image's folder
	 * @param outputDirectory path to output folder analysis
	 */
	public static void runAutoCropFile(String inputDirectory, String outputDirectory) {
		//AutocropParameters autocropParameters= new AutocropParameters(imageSourceFile,output);
		AutocropParameters autocropParameters = new AutocropParameters(inputDirectory, outputDirectory);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		autoCrop.runFile(inputDirectory);
	}
	
	
	public static void runAutoCropOMERO(String inputDirectory,
	                                    String outputDirectory,
	                                    Client client,
	                                    AutoCropCalling autoCrop) throws Exception {
		String[] param = inputDirectory.split(Pattern.quote(File.separator));
		
		if (param.length >= 2) {
			if (param[0].equals("image")) {
				Long         id    = Long.parseLong(param[1]);
				ImageWrapper image = client.getImage(id);
				
				int sizeC = image.getPixels().getSizeC();
				
				Long[] outputsDat = new Long[sizeC];
				
				for (int i = 0; i < sizeC; i++) {
					DatasetWrapper dataset = new DatasetWrapper("C" + i + "_" + image.getName(), "");
					outputsDat[i] =
							client.getProject(Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
				}
				
				autoCrop.runImageOMERO(image, outputsDat, client);
			} else {
				Long               id = Long.parseLong(param[1]);
				List<ImageWrapper> images;
				
				String name = "";
				
				if (param[0].equals("dataset")) {
					DatasetWrapper dataset = client.getDataset(id);
					
					name = dataset.getName();
					
					if (param.length == 4 && param[2].equals("tag")) {
						images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
					} else {
						images = dataset.getImages(client);
					}
				} else if (param[0].equals("tag")) {
					images = client.getImagesTagged(id);
				} else {
					throw new IllegalArgumentException();
				}
				
				int sizeC = images.get(0).getPixels().getSizeC();
				
				Long[] outputsDat = new Long[sizeC];
				
				for (int i = 0; i < sizeC; i++) {
					DatasetWrapper dataset = new DatasetWrapper("raw_C" + i + "_" + name, "");
					outputsDat[i] =
							client.getProject(Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
				}
				
				autoCrop.runSeveralImageOMERO(images, outputsDat, client);
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	
	public static void runAutoCropOMERO(String inputDirectory,
	                                    String outputDirectory,
	                                    String pathToConfig,
	                                    Client client) throws Exception {
		AutocropParameters autocropParameters = new AutocropParameters(".", ".", pathToConfig);
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		
		runAutoCropOMERO(inputDirectory, outputDirectory, client, autoCrop);
	}
	
	
	public static void runAutoCropOMERO(String inputDirectory, String outputDirectory, Client client) throws Exception {
		AutocropParameters autocropParameters = new AutocropParameters(".", ".");
		AutoCropCalling    autoCrop           = new AutoCropCalling(autocropParameters);
		
		runAutoCropOMERO(inputDirectory, outputDirectory, client, autoCrop);
	}
	
	
	//========================= Segmentation calling ===========================================
	
	
	/**
	 * Method to run segmentation with input folder, output folder :
	 *
	 * @param inputDirectory  path to the raw image's folder
	 * @param outputDirectory path to output folder analysis
	 *
	 * @throws Exception
	 */
	public static void segmentationFolder(String inputDirectory, String outputDirectory) throws Exception {
		System.out.println("test " + inputDirectory);
		SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory, outputDirectory);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method to run segmentation with input folder, output folder with config file :
	 *
	 * @param inputDirectory  path to the raw image's folder
	 * @param outputDirectory path to output folder analysis
	 * @param config          path to config file
	 *
	 * @throws Exception
	 */
	public static void segmentationFolder(String inputDirectory, String outputDirectory, String config)
	throws Exception {
		SegmentationParameters segmentationParameters =
				new SegmentationParameters(inputDirectory, outputDirectory, config);
		SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runSeveralImages2();
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	/**
	 * Method to run segmentation on one image :
	 *
	 * @param inputDirectory  path to one raw image
	 * @param outputDirectory path to output folder analysis
	 *
	 * @throws Exception
	 */
	public static void segmentationOneImage(String inputDirectory, String outputDirectory) throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(inputDirectory, outputDirectory);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		try {
			String log = otsuModified.runOneImage(inputDirectory);
			if (!(log.equals(""))) {
				System.out.println("Nuclei which didn't pass the segmentation\n" + log);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void segmentationOMERO(String inputDirectory,
	                                     String outputDirectory,
	                                     Client client,
	                                     SegmentationCalling otsuModified)
	throws Exception {
		
		String[] param = inputDirectory.split(Pattern.quote(File.separator));
		
		if (param.length >= 2) {
			if (param[0].equals("image")) {
				Long         id    = Long.parseLong(param[1]);
				ImageWrapper image = client.getImage(id);
				
				try {
					String log;
					if (param.length == 3 && param[2].equals("ROI")) {
						log = otsuModified.runOneImageOMERObyROIs(image, Long.parseLong(outputDirectory), client);
					} else {
						log = otsuModified.runOneImageOMERO(image, Long.parseLong(outputDirectory), client);
					}
					if (!(log.equals(""))) {
						System.out.println("Nuclei which didn't pass the segmentation\n" + log);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Long               id = Long.parseLong(param[1]);
				List<ImageWrapper> images;
				
				switch (param[0]) {
					case "dataset":
						DatasetWrapper dataset = client.getDataset(id);
						
						if (param.length == 4 && param[2].equals("tag")) {
							images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
						} else {
							images = dataset.getImages(client);
						}
						break;
					case "project":
						ProjectWrapper project = client.getProject(id);
						
						if (param.length == 4 && param[2].equals("tag")) {
							images = project.getImagesTagged(client, Long.parseLong(param[3]));
						} else {
							images = project.getImages(client);
						}
						break;
					case "tag":
						images = client.getImagesTagged(id);
						break;
					default:
						throw new IllegalArgumentException();
				}
				try {
					String log;
					if ((param.length == 3 && param[2].equals("ROI")) ||
					    (param.length == 5 && param[4].equals("ROI"))) {
						log = otsuModified.runSeveralImageOMERObyROIs(images, Long.parseLong(outputDirectory), client);
					} else {
						log = otsuModified.runSeveralImageOMERO(images, Long.parseLong(outputDirectory), client);
					}
					if (!(log.equals(""))) {
						System.out.println("Nuclei which didn't pass the segmentation\n" + log);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} else {
			throw new IllegalArgumentException();
		}
	}
	
	
	public static void segmentationOMERO(String inputDirectory, String outputDirectory, String config, Client client)
	throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".", config);
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		
		segmentationOMERO(inputDirectory, outputDirectory, client, otsuModified);
	}
	
	
	public static void segmentationOMERO(String inputDirectory, String outputDirectory, Client client)
	throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".");
		SegmentationCalling    otsuModified           = new SegmentationCalling(segmentationParameters);
		
		segmentationOMERO(inputDirectory, outputDirectory, client, otsuModified);
	}
	
	
	/**
	 * Compute parameter from raw data folder and segmented data :
	 *
	 * @param rawImagesInputDirectory  path to the raw image's folder
	 * @param segmentedImagesDirectory path to the segmented image's folder
	 * @param pathToConfig             path to config file
	 */
	public static void computeNucleusParameters(String rawImagesInputDirectory,
	                                            String segmentedImagesDirectory,
	                                            String pathToConfig) {
		ComputeNucleiParameters generateParameters =
				new ComputeNucleiParameters(rawImagesInputDirectory, segmentedImagesDirectory, pathToConfig);
		generateParameters.run();
	}
	
	// TODO  configFILE FACTORISABLE AVEC computeNucleusParameters SANS CONFINGFILE
	
	
	/**
	 * Compute parameter from raw data folder and segmented data :
	 *
	 * @param rawImagesInputDirectory  path to the raw image's folder
	 * @param segmentedImagesDirectory path to the segmented image's folder
	 */
	public static void computeNucleusParameters(String rawImagesInputDirectory, String segmentedImagesDirectory) {
		ComputeNucleiParameters generateParameters =
				new ComputeNucleiParameters(rawImagesInputDirectory, segmentedImagesDirectory);
		generateParameters.run();
	}
	
	// TODO AJOUTER computeNucleusParametersDL avec configFILE FACTORISABLE AVEC computeNucleusParametersCONFINGFILE
	
	
	/**
	 * Compute parameters from segmented images produce by ML segmentation. During this process we keep only biggest
	 * connected component.
	 *
	 * @param rawImagesInputFolder  Path raw images folder
	 * @param segmentedImagesFolder Path segmented images folder
	 *
	 * @throws IOException
	 * @throws FormatException
	 * @throws FileInOut
	 * @throws Exception
	 */
	public static void computeNucleusParametersDL(String rawImagesInputFolder, String segmentedImagesFolder)
	throws IOException, FormatException, FileInOut, Exception {
		ComputeNucleiParametersML computeParameters =
				new ComputeNucleiParametersML(rawImagesInputFolder, segmentedImagesFolder);
		computeParameters.run();
	}
	
	
	/**
	 * Generate a projection from coordinate file. 2 steps : - 1 generate max projection from raw image - 2 draw boxes
	 * on max projection
	 *
	 * @param pathToCoordinates Folder containing coordinate files
	 * @param pathToRaw         Folder containing raw images associate
	 *
	 * @throws Exception
	 */
	
	
	// UN DOSSIER AVEC LES ZPROJECTION
	// UN DOSSIER AVEC LES COORDONNEES
	public static void generateProjectionFromCoordinates(String pathToCoordinates, String pathToRaw)
	throws Exception {
		GenerateProjectionFromCoordinates
				projection = new GenerateProjectionFromCoordinates(pathToCoordinates, pathToRaw);
		projection.generateCoordinate();
	}
	
	
	/**
	 * Method to draw missing boxes in initial projection after manual filtering.
	 *
	 * @param pathToGIFTSeg     Path to cropped images
	 * @param pathToZprojection Path to projection images
	 * @param pathToCoordinate  Path to coordinate
	 *
	 * @throws IOException
	 * @throws FormatException
	 * @throws Exception
	 */
	
	
	public static void generateProjectionFromCoordinates(String pathToGIFTSeg,
	                                                     String pathToZprojection,
	                                                     String pathToCoordinate)
	throws IOException, FormatException, Exception {
		GenerateProjectionFromCoordinates projection =
				new GenerateProjectionFromCoordinates(pathToGIFTSeg, pathToZprojection, pathToCoordinate);
		projection.generateCoordinateFiltered();
	}
	
	
	/**
	 * Method to crop an image from coordinates (case of multi channels). Tabulated file containing associate coordinate
	 * file and images , 1 association per line example  :
	 * <p>
	 * pathToCoordinateFile pathToRawImageAssociate
	 *
	 * @param coordinatesDir
	 *
	 * @throws Exception
	 */
	public static void cropFromCoordinates(String coordinatesDir) throws Exception {
		
		CropFromCoordinates test = new CropFromCoordinates(coordinatesDir);
		test.runCropFromCoordinate();
	}
	
	
	/**
	 * Method merge overlay and raw images together (cellular population annotation , example guard cells pavement
	 * cells). This method contrast Tabulated file containing associate coordinate file and images ,
	 * <p>
	 * 1 association per line example  : pathToOverlayFile pathToRawImageAssociate
	 *
	 * @param linkOverlayProjection
	 *
	 * @throws Exception
	 */
	
	
	public static void generateOV(String linkOverlayProjection) throws Exception {
		
		GenerateOverlay ov = new GenerateOverlay(linkOverlayProjection);
		ov.run();
		
	}
	
	
	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsTiff(pathFile);
	}
	
	
	public static void main(String[] args) throws Exception {
		DebugTools.enableLogging("OFF");
		List<String> listArgs = Arrays.asList(args);
		System.setProperty("java.awt.headless", "false");
		
		if (listArgs.contains("-h") || listArgs.contains("-help")) {
			CLIHelper.run(args);
		} else if ((listArgs.contains("-ome")) || (listArgs.contains("-omero"))) {
			CLIActionOptionOMERO command = new CLIActionOptionOMERO(args);
			new CLIRunActionOMERO(command.getCmd());
		} else {
			CLIActionOptionCmdLine command = new CLIActionOptionCmdLine(args);
			new CLIRunAction(command.getCmd());
		}
	}
	
	
	public static boolean OMEROAvailableAction(String action) {
		ArrayList<String> actionAvailableInOMERO = new ArrayList<>();
		actionAvailableInOMERO.add("autocrop");
		actionAvailableInOMERO.add("segmentation");
		
		return actionAvailableInOMERO.contains(action);
	}
	
	
	public static void main2(String[] args) throws Exception {
		DebugTools.enableLogging("OFF");
		Console con = System.console();
		
		System.setProperty("java.awt.headless", "false");
		CommandLine cmd;
		
		Options options = new Options();
		options.addOption("a", "action", true, "Action to make");
		options.addOption("in", "input", true, "Input path");
		options.addOption("out", "output", true, "Output path");
		options.addOption("co", "coordinates", true, "Coordinates file");
		options.addOption("f", "file", false, "Input is a file");
		options.addOption("c", "config", true, "Path to config file");
		options.addOption("ome", "omero", false, "Usage of OMERO");
		options.addOption("h", "hostname", true, "Hostname of the OMERO server");
		options.addOption("pt", "port", true, "Port used by OMERO");
		options.addOption("u", "username", true, "Username in OMERO");
		options.addOption("p", "password", true, "Password in OMERO");
		options.addOption("g", "group", true, "Group in OMERO");
		
		CommandLineParser parser = new DefaultParser();
		
		cmd = parser.parse(options, args);
		HelpFormatter formatter = new HelpFormatter();
		cmd.getOptionValue("action");
		
		
		switch (cmd.getOptionValue("action")) {
			case "autocrop":
				System.out.println("start autocrop");
				
				if (cmd.hasOption("omero")) {
					Client client = new Client();
					String mdp;
					
					if (cmd.hasOption("password")) {
						mdp = cmd.getOptionValue("password");
					} else {
						System.out.println("Enter password ");
						mdp = String.valueOf(con.readPassword());
					}
					
					client.connect(cmd.getOptionValue("hostname"),
					               Integer.parseInt(cmd.getOptionValue("port")),
					               cmd.getOptionValue("username"),
					               mdp,
					               Long.valueOf(cmd.getOptionValue("group")));
					
					if (cmd.hasOption("config")) {
						runAutoCropOMERO(cmd.getOptionValue("input"),
						                 cmd.getOptionValue("output"),
						                 cmd.getOptionValue("config"),
						                 client);
					} else {
						runAutoCropOMERO(cmd.getOptionValue("input"),
						                 cmd.getOptionValue("output"),
						                 client);
					}
				} else {
					if (cmd.hasOption("config")) {
						runAutoCropFolder(cmd.getOptionValue("input"),
						                  cmd.getOptionValue("output"),
						                  cmd.getOptionValue("config"));
						
					} else if (cmd.hasOption("file")) {
						runAutoCropFile(cmd.getOptionValue("input"),
						                cmd.getOptionValue("output"));
					} else {
						runAutoCropFolder(cmd.getOptionValue("input"),
						                  cmd.getOptionValue("output"));
					}
				}
				break;
			case "segmentation":
				System.out.println("start " + "segmentation");
				
				if (cmd.hasOption("omero")) {
					Client client = new Client();
					String mdp;
					
					if (cmd.hasOption("password")) {
						mdp = cmd.getOptionValue("password");
					} else {
						System.out.println("Enter password: ");
						mdp = String.valueOf(con.readPassword());
					}
					
					client.connect(cmd.getOptionValue("hostname"),
					               Integer.parseInt(cmd.getOptionValue("port")),
					               cmd.getOptionValue("username"),
					               mdp,
					               Long.valueOf(cmd.getOptionValue("group")));
					
					if (cmd.hasOption("config")) {
						segmentationOMERO(cmd.getOptionValue("input"),
						                  cmd.getOptionValue("output"),
						                  cmd.getOptionValue("config"),
						                  client);
					} else {
						segmentationOMERO(cmd.getOptionValue("input"),
						                  cmd.getOptionValue("output"),
						                  client);
					}
				} else {
					if (cmd.hasOption("config")) {
						segmentationFolder(cmd.getOptionValue("input"),
						                   cmd.getOptionValue("output"),
						                   cmd.getOptionValue("config"));
						
					} else if (cmd.hasOption("file")) {
						segmentationOneImage(cmd.getOptionValue("input"),
						                     cmd.getOptionValue("output"));
					} else {
						segmentationFolder(cmd.getOptionValue("input"),
						                   cmd.getOptionValue("output"));
					}
				}
				break;
			case "computeParameters":
				//else if(args[0].equals("computeParameters")){
				if ((cmd.hasOption("config"))) {
					computeNucleusParameters(cmd.getOptionValue("input"),
					                         cmd.getOptionValue("output"),
					                         cmd.getOptionValue("config"));
				} else {
					computeNucleusParameters(cmd.getOptionValue("input"),
					                         cmd.getOptionValue("output"));
				}
				break;
			case "computeParametersDL":
				computeNucleusParametersDL(cmd.getOptionValue("input"),
				                           cmd.getOptionValue("output"));
				break;
			case "generateProjection":
				if (cmd.hasOption("config")) {
					generateProjectionFromCoordinates(cmd.getOptionValue("input"),
					                                  cmd.getOptionValue("output"),
					                                  cmd.getOptionValue("config"));
				} else {
					generateProjectionFromCoordinates(cmd.getOptionValue("input"),
					                                  cmd.getOptionValue("output"));
				}
				break;
			case "CropFromCoordinate":
				cropFromCoordinates(cmd.getOptionValue("input"));
				break;
			case "GenerateOverlay":
				generateOV(cmd.getOptionValue("input"));
				break;
			default:
				formatter.printHelp("NucleusJ2.0", options, true);
/*
            System.out.println("Argument le premier argument doit être   autocrop  ou   segmentation ou computeParameters");
            System.out.println("\nExemples :");
            System.out.println("\njava NucleusJ_giftwrapping.jar autocrop dossier/raw/ dossier/out/");
            System.out.println("\njava NucleusJ_giftwrapping.jar segmentation dossier/raw/ dossier/out/");
            System.out.println("\n\n");
            */
				break;
		}
		System.out.println("Fin du programme");
	}
	
}


