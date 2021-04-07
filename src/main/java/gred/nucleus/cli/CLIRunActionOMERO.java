package gred.nucleus.cli;

import fr.igred.omero.Client;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ProjectWrapper;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.Console;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;


public class CLIRunActionOMERO {
	/** List of options */
	Options     options = new Options();
	/** Command line */
	CommandLine cmd;
	/** OMERO client information see fr.igred.omero.Client */
	Client      client  = new Client();
	
	/** OMERO password connexion */
	String mdp;
	
	/** OMERO type of data to analyse : image data dataset tag */
	String dataType;
	
	
	public CLIRunActionOMERO(CommandLine cmd) throws Exception {
		this.cmd = cmd;
		getOMEROPassword();
		checkOMEROConnexion();
		switch (this.cmd.getOptionValue("action")) {
			case "autocrop":
				runAutoCropOMERO();
				break;
			case "segmentation":
				runSegmentationOMERO();
				break;
		}
		this.client.disconnect();
	}
	
	
	public static void autoCropOMERO(String inputDirectory,
	                                 String outputDirectory,
	                                 Client client,
	                                 AutoCropCalling autoCrop) throws Exception {
		String[] param = inputDirectory.split(Pattern.quote(File.separator));
		
		if (param.length >= 2) {
			Long id = Long.parseLong(param[1]);
			if (param[0].equals("image")) {
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
			throw new IllegalArgumentException("Wrong input parameter : "
			                                   + inputDirectory + "\n\n\n"
			                                   + "Example format expected:\n"
			                                   + "dataset/OMERO_ID \n");
		}
	}
	
	
	public void getOMEROPassword() {
		if (this.cmd.hasOption("password")) {
			this.mdp = this.cmd.getOptionValue("password");
		} else {
			System.out.println("Enter password ");
			Console con = System.console();
			this.mdp = String.valueOf(con.readPassword());
		}
	}
	
	
	public void checkOMEROConnexion() {
		try {
			client.connect(this.cmd.getOptionValue("hostname"),
			               Integer.parseInt(this.cmd.getOptionValue("port")),
			               this.cmd.getOptionValue("username"),
			               this.mdp,
			               Long.valueOf(this.cmd.getOptionValue("group")));
		} catch (Exception exp) {
			System.out.println("OMERO connexion error : \n");
			System.out.println(exp.getMessage() + "\n");
			System.exit(1);
		}
	}
	
	
	public void runAutoCropOMERO() throws Exception {
		AutocropParameters autocropParameters = new AutocropParameters(".", ".");
		if (this.cmd.hasOption("config")) {
			autocropParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			autocropParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		try {
			autoCropOMERO(this.cmd.getOptionValue("input"),
			              this.cmd.getOptionValue("output"),
			              this.client,
			              autoCrop);
		} catch (IllegalArgumentException exp) {
			System.out.println(exp.getMessage());
			System.exit(1);
		}
	}
	
	
	public void runSegmentationOMERO() throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".");
		if (this.cmd.hasOption("config")) {
			segmentationParameters.addGeneralProperties(this.cmd.getOptionValue("config"));
			segmentationParameters.addProperties(this.cmd.getOptionValue("config"));
		}
		SegmentationCalling otsuModified = new SegmentationCalling(segmentationParameters);
		segmentationOMERO(this.cmd.getOptionValue("input"),
		                  this.cmd.getOptionValue("output"),
		                  this.client,
		                  otsuModified);
	}
	
	
	public void segmentationOMERO(String inputDirectory,
	                              String outputDirectory,
	                              Client client,
	                              SegmentationCalling otsuModified) throws Exception {
		String[] param = inputDirectory.split(Pattern.quote(File.separator));
		
		if (param.length >= 2) {
			Long id = Long.parseLong(param[1]);
			if (param[0].equals("image")) {
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
	
}
