package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.FilesNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class generateProjectionFromCoordonne {

    String m_pathToGIFTSeg;
    String m_pathToZprojection;
    String m_pathToCoordonnate;
    String m_pathToRaw;
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
     * Constructor
     * @param pathToCoordonnate path to segmented image's folder
     * @param pathToRaw path to raw image
     * @throws Exception
     */
    public generateProjectionFromCoordonne(String pathToCoordonnate, String pathToRaw) throws Exception {
        this.m_pathToCoordonnate=pathToCoordonnate;
        this.m_pathToRaw=pathToRaw;
    }
    /**
     * Run new annotation of Zprojection, color in red nuclei which were filtered
     * (in case of GIFT wrapping color in red nuclei which not pass the segmentation
     * most of case Z truncated )
     * @throws Exception
     */
    public void run()throws Exception{
        Directory GIFTsegImages = new Directory(this.m_pathToGIFTSeg);
        GIFTsegImages.listImageFiles(this.m_pathToGIFTSeg);
        GIFTsegImages.checkIfEmpty();
        Directory Zprojection = new Directory(this.m_pathToZprojection);
        Zprojection.listImageFiles(this.m_pathToZprojection);
        Zprojection.checkIfEmpty();
        Directory Coordonnate = new Directory(this.m_pathToCoordonnate);
        Coordonnate.listAllFiles(this.m_pathToCoordonnate);
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
                    System.out.println("add "+ entry.getValue());
                }
            }
            File CurrentZprojection=  Zprojection.searchFileNameWithoutExention(coordinateFile.getName().substring(0, coordinateFile.getName().lastIndexOf('.'))+"_Zprojection");
            AutocropParameters autocropParameters= new AutocropParameters(CurrentZprojection.getParent(),
                    CurrentZprojection.getParent()+Zprojection.getSeparator());
            annotAutoCrop annotAutoCrop =new annotAutoCrop(boxListsNucleiNotPass,
                            CurrentZprojection,
                            CurrentZprojection.getParent()+Zprojection.getSeparator()+CurrentZprojection.getName().substring(0, CurrentZprojection.getName().lastIndexOf('.')),
                            autocropParameters);
            annotAutoCrop.runAddBadCrop(boxNumber);
        }

    }

    public void run2()throws Exception{
        Directory RawImage = new Directory(this.m_pathToRaw);
        RawImage.listImageFiles(this.m_pathToRaw);
        RawImage.checkIfEmpty();
        Directory Coordonnate = new Directory(this.m_pathToCoordonnate);
        Coordonnate.listAllFiles(this.m_pathToCoordonnate);
        Coordonnate.checkIfEmpty();

        for (short i = 0; i < Coordonnate.getNumberFiles(); ++i) {
            File coordinateFile = Coordonnate.getFile(i);
            HashMap<String, String> listOfBoxes = readCoordinnateTXT(coordinateFile);
            ArrayList<Integer> boxNumber =new ArrayList();
            ArrayList<String> boxListsNucleiNotPass = new ArrayList();
            Map<String, String> sortedMap = new TreeMap<String, String>(listOfBoxes);
            for (HashMap.Entry<String, String> entry : listOfBoxes.entrySet()) {
                    boxListsNucleiNotPass.add(entry.getValue());

            }
            System.out.println(coordinateFile.getName());

            File CurrentRaw=  RawImage.searchFileNameWithoutExention(coordinateFile.getName().substring(0, coordinateFile.getName().lastIndexOf('.')));
            FilesNames outPutFilesNames = new FilesNames(CurrentRaw.toString());
            String prefix = outPutFilesNames.PrefixeNameFile();
            System.out.println("la current raw " +CurrentRaw.getName());
            AutocropParameters autocropParameters= new AutocropParameters(CurrentRaw.getParent(),
                    CurrentRaw.getParent()+RawImage.getSeparator());
            annotAutoCrop annotAutoCrop =new annotAutoCrop(boxListsNucleiNotPass,
                    CurrentRaw,
                    CurrentRaw.getParent()+RawImage.getSeparator(),
                    prefix,
                    autocropParameters);
            annotAutoCrop.run();
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
