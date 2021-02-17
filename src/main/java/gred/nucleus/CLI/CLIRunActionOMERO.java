package gred.nucleus.CLI;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.repository.DatasetContainer;
import fr.igred.omero.repository.ProjectContainer;
import gred.nucleus.autocrop.AutoCropCalling;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.segmentation.SegmentationParameters;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.Console;
import java.io.IOException;
import java.util.List;

public class CLIRunActionOMERO {
	/**
	 * List of options
	 */
	Options     m_options = new Options();
	/**
	 * Command line
	 */
	CommandLine m_cmd;
	/**
	 * OMERO client information see fr.igred.omero.Client
	 */
	Client      m_client  = new Client();
	
	/**
	 * OMERO password connexion
	 */
	String m_mdp;
	
	/**
	 * OMERO type of data to analyse : image data dataset tag
	 */
	String m_dataType;
	
	
	public CLIRunActionOMERO(CommandLine cmd) throws Exception {
		this.m_cmd = cmd;
		getOMEROPassword();
		checkOMEROConnexion();
		switch (this.m_cmd.getOptionValue("action")) {
			case "autocrop":
				runAutoCropOmero();
				break;
			case "segmentation":
				runSegmentationOmero();
				break;
		}
		this.m_client.disconnect();
		
		
	}
	
	public static void autoCropOmero(String inputDirectory,
	                                 String outputDirectory,
	                                 Client client,
	                                 AutoCropCalling autoCrop) throws Exception {
		String[] param = inputDirectory.split("/");
		
		if (param.length >= 2) {
			if (param[0].equals("image")) {
				Long           id    = Long.parseLong(param[1]);
				ImageContainer image = client.getImage(id);
				
				int sizeC = image.getPixels().getSizeC();
				
				Long[] outputsDat = new Long[sizeC];
				
				for (int i = 0; i < sizeC; i++) {
					DatasetContainer dataset = new DatasetContainer("C" + i + "_" + image.getName(), "");
					outputsDat[i] =
							client.getProject(Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
				}
				
				autoCrop.runImageOmero(image, outputsDat, client);
			} else {
				Long                 id     = Long.parseLong(param[1]);
				List<ImageContainer> images = null;
				
				String name = "";
				
				if (param[0].equals("dataset")) {
					DatasetContainer dataset = client.getDataset(id);
					
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
					DatasetContainer dataset = new DatasetContainer("raw_C" + i + "_" + name, "");
					outputsDat[i] =
							client.getProject(Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
				}
				
				autoCrop.runSeveralImageOmero(images, outputsDat, client);
			}
		} else {
			throw new IllegalArgumentException("Wrong input parameter : "
			                                   + inputDirectory + "\n\n\n"
			                                   + "Exemple format expected:\n"
			                                   + "dataset/OMERO_ID \n");
		}
	}
	
	public void getOMEROPassword() {
		if (this.m_cmd.hasOption("password")) {
			this.m_mdp = this.m_cmd.getOptionValue("password");
		} else {
			System.out.println("Enter password ");
			Console con = System.console();
			this.m_mdp = String.valueOf(con.readPassword());
		}
		
	}
	
	public void checkOMEROConnexion() throws Exception {
		try {
			m_client.connect(this.m_cmd.getOptionValue("hostname"),
			                 Integer.parseInt(this.m_cmd.getOptionValue("port")),
			                 this.m_cmd.getOptionValue("username"),
			                 this.m_mdp,
			                 Long.valueOf(this.m_cmd.getOptionValue("group")));
		} catch (Exception exp) {
			System.out.println("OMERO connexion error : \n");
			System.out.println(exp.getMessage() + "\n");
			System.exit(1);
		}
	}
	
	public void runAutoCropOmero() throws Exception {
		AutocropParameters autocropParameters = new AutocropParameters(".", ".");
		if (this.m_cmd.hasOption("config")) {
			autocropParameters.addGeneralProperties(this.m_cmd.getOptionValue("config"));
			autocropParameters.addProperties(this.m_cmd.getOptionValue("config"));
		}
		AutoCropCalling autoCrop = new AutoCropCalling(autocropParameters);
		try {
			autoCropOmero(this.m_cmd.getOptionValue("input")
					, this.m_cmd.getOptionValue("output"), this.m_client, autoCrop);
		} catch (IllegalArgumentException exp) {
			System.out.println(exp.getMessage());
			System.exit(1);
		}
	}
	
	public void runSegmentationOmero() throws Exception {
		SegmentationParameters segmentationParameters = new SegmentationParameters(".", ".");
		if (this.m_cmd.hasOption("config")) {
			segmentationParameters.addGeneralProperties(this.m_cmd.getOptionValue("config"));
			segmentationParameters.addProperties(this.m_cmd.getOptionValue("config"));
		}
		SegmentationCalling otsuModif = new SegmentationCalling(segmentationParameters);
		segmentationOmero(this.m_cmd.getOptionValue("input")
				, this.m_cmd.getOptionValue("output"), this.m_client, otsuModif);
		
		
	}
	
	public void segmentationOmero(String inputDirectory,
	                              String outputDirectory,
	                              Client client,
	                              SegmentationCalling otsuModif) throws Exception {
		String[] param = inputDirectory.split("/");
		
		if (param.length >= 2) {
			if (param[0].equals("image")) {
				Long           id    = Long.parseLong(param[1]);
				ImageContainer image = client.getImage(id);
				
				try {
					String log;
					if (param.length == 3 && param[2].equals("ROI")) {
						log = otsuModif.runOneImageOmeroROI(image, Long.parseLong(outputDirectory), client);
					} else {
						log = otsuModif.runOneImageOmero(image, Long.parseLong(outputDirectory), client);
					}
					if (!(log.equals(""))) {
						System.out.println("Nuclei which didn't pass the segmentation\n" + log);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			} else {
				Long                 id     = Long.parseLong(param[1]);
				List<ImageContainer> images = null;
				
				switch (param[0]) {
					case "dataset":
						DatasetContainer dataset = client.getDataset(id);
						
						if (param.length == 4 && param[2].equals("tag")) {
							images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
						} else {
							images = dataset.getImages(client);
						}
						break;
					case "project":
						ProjectContainer project = client.getProject(id);
						
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
						log = otsuModif.runSeveralImageOmeroROI(images, Long.parseLong(outputDirectory), client);
					} else {
						log = otsuModif.runSeveralImageOmero(images, Long.parseLong(outputDirectory), client);
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
