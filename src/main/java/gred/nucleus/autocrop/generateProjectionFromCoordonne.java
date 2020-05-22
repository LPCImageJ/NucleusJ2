package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import loci.formats.FormatException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;

public class generateProjectionFromCoordonne {

    String m_pathToGIFTSeg;
    String m_pathToZprojection;
    String m_pathToCoordonnate;

   public generateProjectionFromCoordonne(String pathToGIFTSeg, String pathToZprojection,String pathToCoordonnate) throws Exception {
        m_pathToGIFTSeg=pathToGIFTSeg;
        m_pathToZprojection=pathToZprojection;
        m_pathToCoordonnate=pathToCoordonnate;
    }

    public void run()throws Exception{
        Directory GIFTsegImages = new Directory(m_pathToGIFTSeg);
        GIFTsegImages.listImageFiles(m_pathToGIFTSeg);
        GIFTsegImages.checkIfEmpty();
        Directory Zprojection = new Directory(m_pathToZprojection);
        Zprojection.listImageFiles(m_pathToZprojection);
        Zprojection.checkIfEmpty();
        Directory Coordonnate = new Directory(m_pathToCoordonnate);
        Coordonnate.listAllFiles(m_pathToCoordonnate);
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

}
