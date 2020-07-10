package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;
import gred.nucleus.exceptions.fileInOut;
import loci.formats.FormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class CropFromCoordonnate {


    HashMap<String, String> coordinateToRawImage = new HashMap();


    /**
     * Method to crop image with coordinate in tab file :
     *    tabulate file : pathToCoordinateFile pathToRawImageAssociate
     *
     *
     * @param linkCoordinateToRawImage tabulate file
     */
    public CropFromCoordonnate(String linkCoordinateToRawImage)throws IOException, FormatException, fileInOut, Exception{
        File coordinateFile = new File(linkCoordinateToRawImage);
        Scanner scanner = new Scanner(coordinateFile);
        while (scanner.hasNextLine()) {
            String line = scanner.nextLine();

            if (!(line.matches("^#.*"))) {
                String [] splitLine = line.split("\\t");
                this.coordinateToRawImage.put(splitLine[0],splitLine[1]);

            }
        }
    }

    public void runCropFromCoordonnate()throws IOException, FormatException, fileInOut, Exception{

        for (Map.Entry<String, String> listOfFile : coordinateToRawImage.entrySet()) {
            File coordinateFile = new File(listOfFile.getKey());
            File rawImage =new File(listOfFile.getValue());
            AutocropParameters autocropParameters= new AutocropParameters(rawImage.getParent(),rawImage.getParent());
            HashMap<Double, Box> m_boxes = readCoordonnateTXT(coordinateFile);
            FilesNames outPutFilesNames = new FilesNames(listOfFile.getValue());
            String _prefix = outPutFilesNames.PrefixeNameFile();
            AutoCrop autoCrop = new AutoCrop(rawImage, _prefix,   autocropParameters,m_boxes);
            autoCrop.cropKernels3();
        }
    }







    public HashMap<Double, Box>  readCoordonnateTXT(File boxeFile) {

        HashMap<Double, Box> boxLists = new HashMap<Double, Box>();
        double count=0;
        try {
            Scanner scanner = new Scanner(boxeFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    String [] splitLine = line.split("\\t");
                    short xMax=(short)(Integer.valueOf(splitLine[3])+Integer.valueOf(splitLine[6]));

                    short yMax=(short)(Integer.valueOf(splitLine[4])+Integer.valueOf(splitLine[7]));
                    short zMax=(short)(Integer.valueOf(splitLine[5])+Integer.valueOf(splitLine[8]));

                    Box box = new Box( Short.valueOf(splitLine[3]),
                            xMax ,
                            Short.valueOf(splitLine[4]),
                            yMax,
                            Short.valueOf(splitLine[5]),
                            Short.valueOf(zMax));

                    boxLists.put(Double.valueOf(splitLine[2]),box);
                }
                count++;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return boxLists;
    }

}
