package gred.nucleus.AnalyseTest;


import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class OuputFileVerification {
    /*
    private ArrayList<File> _listInitialFilesInputFolder = new ArrayList<File>();
    private ArrayList<File> _listInitialFileOutputFolder = new ArrayList<File>();
    private ArrayList<File> _listFilesProduceByAnlaysis = new ArrayList<File>();

    */
    /** Key of files expected in the result directory */
    Map<String,Integer> _myMapInitialFilesInputFolder = new HashMap<String,Integer>();

    /** Key of files produce by the analysis*/
    Map<String,Integer> _myMapInitialFileOutputFolder = new HashMap<String,Integer>();

    /** list of files produce by the analysis*/
    Map<String,Integer> _myMapFilesProduceByAnlaysis = new HashMap<String,Integer>();


    public void OuputFileVerification() {
        System.out.println("Init l object ");

    }

    public void GetFileResultExpeted( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            // System.out.println("list est de  " + list.length);
            if (f.isDirectory()) {
               //return listFile;
                GetFileResultExpeted(f.getAbsolutePath());
                 // System.out.println("Dir:" + f.getAbsoluteFile() + " " + f.hashCode());
            }
            else {
                _myMapInitialFilesInputFolder.put(f.getName(),f.hashCode());
                System.out.println("On ajoute ca au hashmap"+f.getName()+" "+f.hashCode());
               // _listInitialFilesInputFolder.add(f);
            }
        }

    }
    public void GetFilesOutputFolder( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            // System.out.println("list est de  " + list.length);
            if (f.isDirectory()) {
                //return listFile;
                GetFilesOutputFolder(f.getAbsolutePath());
                //   System.out.println("Dir:" + f.getAbsoluteFile() + " " + f.hashCode());
            }
            else {
                //_listInitialFileOutputFolder.add(f);
                _myMapInitialFileOutputFolder.put(f.getName(),f.hashCode());
            }
        }

    }
    public void GetFilesResultingOfAnalysis( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            // System.out.println("list est de  " + list.length);
            if (f.isDirectory()) {
                //return listFile;
                GetFilesResultingOfAnalysis(f.getAbsolutePath());
                //   System.out.println("Dir:" + f.getAbsoluteFile() + " " + f.hashCode());
            }
            else {
                //_listFilesProduceByAnlaysis.add(f);
                _myMapFilesProduceByAnlaysis.put(f.getName(),f.hashCode());
                System.out.println("Le resu analyse: "+f.getName()+" "+f.hashCode());

            }
        }

    }
    public void CompareAnalysisResult() {
        System.out.println("Terrible du cul c est le Map qui merde "+_myMapInitialFilesInputFolder.size());


        for(Map.Entry<String, Integer> entry : _myMapInitialFilesInputFolder.entrySet()) {
            String fileName = entry.getKey();
            Integer hashCode = entry.getValue();
            if ( hashCode == _myMapFilesProduceByAnlaysis.get(fileName)){
                System.out.println("Terrible du cul ");
            }
            else {

                System.out.println("Le file : "+fileName+ "  -- Le hashcode : "+hashCode+"" +
                        " le out "+_myMapFilesProduceByAnlaysis.get(fileName)+ " "              +
                        _myMapFilesProduceByAnlaysis.size()                );

            }

        }
        /*
        Iterator<File> itr = _listFilesProduceByAnlaysis.iterator();

        while (itr.hasNext()) {
            if (_listInitialFilesInputFolder.contains(itr)) {
                File element = itr.next();

                System.out.println("Account found");

            }
            else{

                System.out.println("Pas bien");

            }

        }
        */
    }
}
