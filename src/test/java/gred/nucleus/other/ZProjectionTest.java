package gred.nucleus.other;

import fr.igred.omero.Client;
import fr.igred.omero.exception.AccessException;
import fr.igred.omero.exception.ServiceException;
import fr.igred.omero.repository.DatasetWrapper;
import fr.igred.omero.repository.ImageWrapper;
import fr.igred.omero.roi.ROIWrapper;
import fr.igred.omero.roi.RectangleWrapper;
import fr.igred.omero.roi.ShapeList;
import gred.nucleus.autocrop.*;
import gred.nucleus.files.FilesNames;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.ImageRoi;
import ij.gui.Overlay;
import ij.plugin.Duplicator;
import ij.plugin.LutLoader;
import ij.plugin.OverlayCommands;
import ij.plugin.PlugIn;
import ij.process.ImageConverter;
import ij.process.LUT;
import loci.formats.FormatException;
import org.apache.commons.io.FilenameUtils;
import org.checkerframework.checker.units.qual.C;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

public class ZProjectionTest implements PlugIn {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    static Client client = new Client();

    public static void main(String[] args) throws Exception {
        checkOMEROConnection("".toCharArray());

        GenerateOverlay generateOverlay = new GenerateOverlay();
        generateOverlay.runFromOMERO(
                "1381",
               "1380",
                "657",
                client);
    }

    public void checkIfFileResultAreMissingOrDifferent() throws AccessException, ServiceException {
        checkOMEROConnection("".toCharArray());

        DatasetWrapper datSource = client.getDataset(20736L);
        DatasetWrapper dat1 = client.getDataset(20923L);
        DatasetWrapper dat2 = client.getDataset(20935L);

        for (ImageWrapper img : datSource.getImages(client)) {
            String imagePrefix = img.getName().split(" ")[0];

            int imageNb = dat1.getImages(client, imagePrefix + " DAPI SIM variable.TIF_" + img.getId() + "_Zprojection.tif").size();
            if(imageNb != 1 ){
                LOGGER.info("Image \"{}\" in dataset 1 found = {} times", img.getName(), imageNb);
            }
            imageNb = dat2.getImages(client, imagePrefix + " DAPI SIM variable_Zprojection.tif").size();
            if(imageNb != 1 ){
                LOGGER.info("Image \"{}\" in dataset 2 found = {} times", img.getName(), imageNb);
            }
        }
        client.disconnect();
    }

    public void projectionFromCoordinate() throws IOException, FormatException {
        GenerateProjectionFromCoordinates projection = new GenerateProjectionFromCoordinates(
                "E:\\alexw\\Desktop\\coordinates",
                "E:\\alexw\\Desktop\\i"
        );
        projection.generateProjection();
    }

    @Override
    public void run(String s) {
        try {
            runAutocrop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private static void runAutocrop() throws Exception {
        AutocropParameters parameters = new AutocropParameters(
                ".",
                "."
        );
        AutoCropCalling autoCrop = new AutoCropCalling(parameters);
        autoCrop.setExecutorThreads(1); // Set thread number


        Scanner scanner = new Scanner(System.in);
        System.out.println("Password for \"demo\" :");
        String password = scanner.nextLine();


        long source = 20647;
        long output = 11201;

        IJ.log("Acquiring images");
        DatasetWrapper datasetWrapper = client.getDataset(source);


        List<ImageWrapper> images = datasetWrapper.getImages(client);
        int          sizeC      = images.get(0).getPixels().getSizeC();
        Long[]       outputsDat = new Long[sizeC];
        IJ.log("Acquiring output datasets");
        for (int i = 0; i < sizeC; i++) {
            DatasetWrapper dataset = new DatasetWrapper("C" + i + "_" + datasetWrapper.getName(), "");
            outputsDat[i] = client.getProject(output).addDataset(client, dataset).getId();
        }
        IJ.log("Running images");
        IJ.error("Running images");
        autoCrop.runSeveralImageOMERO(images, outputsDat, client);
        IJ.error("Ended process");
        IJ.log("Saving general info");
        //autoCrop.saveGeneralInfoOmero(client, outputsDat);
    }

    static void checkOMEROConnection(char[] password) {
        try {
            client.connect("omero.mesocentre.uca.fr",
                    4064,
                    "alrongier1",
                    password,
                    53L);
        } catch (Exception exp) {
            LOGGER.error("OMERO connection error: " + exp.getMessage(), exp);
            System.exit(1);
        }
    }
}
