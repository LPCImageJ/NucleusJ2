package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.annotAutoCrop;
import loci.formats.FormatException;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class MulticropFilter {



    public static void main(String[] args) throws IOException, FormatException,Exception {
        Directory directoryCoordonne = new Directory("/home/titus/Bureau/TEST_NJ/AUTOCROP/coordonneeProjection/coordonnee/");
        Directory directoryTIF = new Directory("/home/titus/Bureau/TEST_NJ/AUTOCROP/coordonneeProjection/raw/");
        directoryCoordonne.listAllFiles(directoryCoordonne.get_dirPath());
        directoryTIF.listAllFiles(directoryTIF.get_dirPath());
        for (short i = 0; i < directoryCoordonne.getNumberFiles(); ++i) {
            File coordinateFile = directoryCoordonne.getFile(i);

            // TODO FAIRE UNE FONCTION POUR CHOPER LE FICHIER IMAGE DANS LE DIR PEUT IMPORTE L EXTENSION !
            File tifFile =directoryTIF.searchFileNameWithoutExention(coordinateFile.getName().split("\\.")[0]);
            if (tifFile !=null) {
                System.out.println("Dedand");

                AutocropParameters autocropParameters= new AutocropParameters(tifFile.getParent(),tifFile.getParent());
                ArrayList<String> listOfBoxes =readCoordonnateTXT(coordinateFile);
                annotAutoCrop annotAutoCrop =new annotAutoCrop(listOfBoxes,tifFile,tifFile.getAbsolutePath(),autocropParameters);

                annotAutoCrop.run();
            }
        }

    }
    public static ArrayList<String> readCoordonnateTXT(File boxeFile) {

        ArrayList<String> boxLists = new ArrayList();
        try {
            Scanner scanner = new Scanner(boxeFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    String [] splitLine = line.split("\\t");
                    int xMax=Integer.valueOf(splitLine[3])+Integer.valueOf(splitLine[6]);
                    int yMax=Integer.valueOf(splitLine[4])+Integer.valueOf(splitLine[7]);
                    int zMax=Integer.valueOf(splitLine[5])+Integer.valueOf(splitLine[8]);
                    boxLists.add(splitLine[0]
                            + "\t" + splitLine[3]
                            + "\t" + xMax
                            + "\t" + splitLine[4]
                            + "\t" + yMax
                            + "\t" + splitLine[5]
                            + "\t" + zMax);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return boxLists;
    }

}

