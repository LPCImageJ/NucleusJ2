package gred.nucleus.CLI;

import fr.igred.omero.Client;
import fr.igred.omero.ImageContainer;
import fr.igred.omero.repository.DatasetContainer;
import gred.nucleus.autocrop.AutoCropCalling;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;

import java.io.Console;
import java.util.List;

public class CLIRunActionOMERO {
    /**
     * List of options
     */
    Options m_options= new Options();
    /**
     * Command line
     */
    CommandLine m_cmd;
    /**
     * OMERO client information
     * see fr.igred.omero.Client
     */
    Client m_client = new Client();

    /**
     * OMERO password connexion
     */
    String m_mdp;

    /**
     * OMERO type of data to analyse :
     * image
     * data
     * dataset
     * tag
     *
     */
    String m_dataType;


    public CLIRunActionOMERO(CommandLine cmd){
        this.m_cmd= cmd;
        getOMEROPassword();
        checkOMEROConnexion();
    }

    public void getOMEROPassword() {
        if(this.m_cmd.hasOption("password"))
            this.m_mdp = this.m_cmd.getOptionValue("password");
        else
        {
            System.out.println("Enter password ");
            Console con = System.console();
            this.m_mdp = String.valueOf(con.readPassword());
        }

    }
    public void checkOMEROConnexion() {
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


    public static void runAutoCropOmero(String inputDirectory, String outputDirectory, Client client, AutoCropCalling autoCrop) throws Exception {
        String[] param = inputDirectory.split("/");

        if(param.length >= 2) {
            if(param[0].equals("image")) {
                Long id = Long.parseLong(param[1]);
                ImageContainer image = client.getImage(id);

                int sizeC = image.getPixels().getSizeC();

                Long outputsDat[] = new Long[sizeC];

                for(int i = 0; i < sizeC; i++) {
                    DatasetContainer dataset = new DatasetContainer("C" + i + "_"  + image.getName() , "");
                    outputsDat[i] = client.getProject(Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
                }

                autoCrop.runImageOmero(image, outputsDat, client);
            }
            else {
                Long id = Long.parseLong(param[1]);
                List<ImageContainer> images = null;

                String name = "";

                if(param[0].equals("dataset")) {
                    DatasetContainer dataset = client.getDataset(id);

                    name = dataset.getName();

                    if(param.length == 4 && param[2].equals("tag")) {
                        images = dataset.getImagesTagged(client, Long.parseLong(param[3]));
                    }
                    else {
                        images = dataset.getImages(client);
                    }
                }
                else if(param[0].equals("tag")) {
                    images = client.getImagesTagged(id);
                }
                else {
                    throw new IllegalArgumentException();
                }

                int sizeC = images.get(0).getPixels().getSizeC();

                Long outputsDat[] = new Long[sizeC];

                for(int i = 0; i < sizeC; i++) {
                    DatasetContainer dataset = new DatasetContainer("raw_C" + i + "_"  + name, "");
                    outputsDat[i] = client.getProject( Long.parseLong(outputDirectory)).addDataset(client, dataset).getId();
                }

                autoCrop.runSeveralImageOmero(images, outputsDat, client);
            }
        }
        else {
            throw new IllegalArgumentException();
        }
    }

}
