package gred.nucleus.mains;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.autocrop.AutocropParameters;
import gred.nucleus.autocrop.annotAutoCrop;
import gred.nucleus.exceptions.fileInOut;


import loci.common.DebugTools;
import loci.formats.FormatException;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class projectionAfterGIFT {



    public  projectionAfterGIFT(String pathToGIFTSeg, String pathToZprojection,String pathToCoordonnate) throws Exception {
        Directory GIFTsegImages = new Directory(pathToGIFTSeg);
        GIFTsegImages.listImageFiles(pathToGIFTSeg);
        GIFTsegImages.checkIfEmpty();
        Directory Zprojection = new Directory(pathToZprojection);
        Zprojection.listImageFiles(pathToZprojection);
        Zprojection.checkIfEmpty();
        Directory Coordonnate = new Directory(pathToCoordonnate);
        Coordonnate.listAllFiles(pathToCoordonnate);
        Coordonnate.checkIfEmpty();
        for (short i = 0; i < Coordonnate.getNumberFiles(); ++i) {
            File coordinateFile = Coordonnate.getFile(i);
            System.out.println("la clef "+coordinateFile.getName()+"\n"+Coordonnate.getFile(i).getName());

            HashMap<String, String> listOfBoxes = readCoordonnateTXT(coordinateFile);
            ArrayList<Integer> boxNumber =new ArrayList();
            ArrayList<String> boxListsNucleiNotPass = new ArrayList();
            Map<String, String> sortedMap = new TreeMap<String, String>(listOfBoxes);
            for (HashMap.Entry<String, String> entry : sortedMap.entrySet()) {
                System.out.println("la clef "+entry.getKey());

                if (!(GIFTsegImages.checkIfFileExists(entry.getKey()))) {
                    boxListsNucleiNotPass.add(entry.getValue());
                }
            }

            File CurrentZprojection=  Zprojection.searchFileNameWithoutExention(coordinateFile.getName().split("\\.")[0]+"_Zprojection");
            AutocropParameters autocropParameters= new AutocropParameters(CurrentZprojection.getParent(),
                            CurrentZprojection.getParent()+Zprojection.getSeparator());
            annotAutoCrop annotAutoCrop =new annotAutoCrop(boxListsNucleiNotPass,CurrentZprojection,CurrentZprojection.getParent()+Zprojection.getSeparator()+CurrentZprojection.getName().split("\\.")[0]+"_GIFTAnnotation",autocropParameters);
            annotAutoCrop.runAddBadCrop(boxNumber);
        }




    }
    public static HashMap<String,String> readCoordonnateTXT(File boxeFile) {

        HashMap<String,String> boxLists = new HashMap();
        try {
            Scanner scanner = new Scanner(boxeFile);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();

                if ((!(line.matches("^#.*")))
                        && (!(line.matches("^FileName.*")))) {
                    String [] splitLine = line.split("\\t");
                    String [] fileName =splitLine[0].split("\\/");
                    int xMax=Integer.valueOf(splitLine[3])+Integer.valueOf(splitLine[6]);
                    int yMax=Integer.valueOf(splitLine[4])+Integer.valueOf(splitLine[7]);
                    int zMax=Integer.valueOf(splitLine[5])+Integer.valueOf(splitLine[8]);
                    boxLists.put(fileName[fileName.length-1],splitLine[0]
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

    public static void main(String[] args) throws IOException, FormatException, fileInOut,Exception {
        DebugTools.enableLogging("OFF");
        projectionAfterGIFT aa = new projectionAfterGIFT("/home/titus/Bureau/TEST_NJ/ANALYSE_COMPLETE/SEG/GIFT_H4",
                "/home/titus/Bureau/TEST_NJ/ANALYSE_COMPLETE/Zprojection" ,
                "/home/titus/Bureau/TEST_NJ/ANALYSE_COMPLETE/Coordonnee");

    }

}
