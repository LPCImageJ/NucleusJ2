package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class generateProjectionFromCoordonne {

    String m_pathToGIFTSeg;
    String m_pathToZprojection;
    String m_pathToCoordonnate;

    /**
     * Constructor
     * @param pathToGIFTSeg path to segmented image's folder
     * @param pathToZprojection path to Zprojection image's from autocrop
     * @param pathToCoordinnate path to coordinates files from autocrop
     * @throws Exception
     */
   public generateProjectionFromCoordonne(String pathToGIFTSeg, String pathToZprojection,String pathToCoordinnate) throws Exception {
        this.m_pathToGIFTSeg=pathToGIFTSeg;
        this.m_pathToZprojection=pathToZprojection;
        this.m_pathToCoordonnate=pathToCoordinnate;
    }

    /**
     * Run new annotation of Zprojection, color in red nuclei which were filtered
     * (in case of GIFT wrapping color in red nuclei which not pass the segmentation
     * most of case Z truncated )
     * @throws Exception
     */
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
            HashMap<String, String> listOfBoxes = readCoordinnateTXT(coordinateFile);
            ArrayList<Integer> boxNumber =new ArrayList();
            ArrayList<String> boxListsNucleiNotPass = new ArrayList();
            Map<String, String> sortedMap = new TreeMap<String, String>(listOfBoxes);
            for (HashMap.Entry<String, String> entry : sortedMap.entrySet()) {
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

    /**
     * Compute list of boxes from coordinates file.
     *
     * @param boxeFile coordinate file
     * @return  list of boxes file to draw in red
     */
    public static HashMap<String,String> readCoordinnateTXT(File boxeFile) {

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
