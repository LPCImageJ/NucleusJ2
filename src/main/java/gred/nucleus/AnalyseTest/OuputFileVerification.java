package gred.nucleus.AnalyseTest;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.codec.digest.DigestUtils;


/**
 * This class is used to check results after structural code modification which
 * not affect results and specifically :
 *  -Images formats handling
 *  -Analysis results (3D parameters computation)
 *
 * The method use a folder with images analysed at a specific version "under
 * control" (expected results).
 * A second folder where the same images are analysed with of a new version of
 * the code which should not affect results.
 * Here we use md5sum to check changes between files.
 * The class produce a report with the list of file changed.
 *
 * TODO Create class to produce a report
 * TODO Complete the description
 * TODO May be remove this class after GIT integration
 *
 * @author Tristan Dubos
 */

public class OuputFileVerification {

    /** Key of files expected in the result directory */
    Map<String,String> _myMapInitialFilesInputFolder = new HashMap<String,String>();
    /** Key of files produce by the analysis */
    Map<String,String> _myMapInitialFileOutputFolder = new HashMap<String,String>();
    /** list of files produce by the analysis */
    Map<String,String> _myMapFilesProduceByAnlaysis = new HashMap<String,String>();


    /** Path output analysis files */
    String _rawPathOutPut;
    /** Raw path expected files */
    String _rawPathExpectedResult;

    /**
     * Constructor containing 2 parameters path containing expected results
     * and path to the raw data
     * @param PathExpectedResult : Path of expected result (version under
     * control)
     * @param PathOutPut : Path of out results new version
     */
    public OuputFileVerification(String PathExpectedResult,String PathOutPut) {
        this._rawPathExpectedResult=PathExpectedResult;
        this._rawPathOutPut=PathOutPut;
    }

    /**
    List files expected and compute md5sum stored in hashMap (read recursively
     folders)
     @param path : path of folder which contains files expected
    */

    public void GetFileResultExpeted( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                GetFileResultExpeted(f.getAbsolutePath());
            }
            else {
                try {
                    String temps= f.getPath().replace(
                            this._rawPathExpectedResult,"");
                    this._myMapInitialFilesInputFolder.put(temps, md5(
                            f.getPath()));
                    }
                catch (IOException e){

                }
            }
        }

    }
    /**
     List files already inside the output folder and compute md5sum stored in
     hashMap (read recursively folders)
     @param path : path of folder which contains files expected

     */

    public void GetFilesOutputFolder( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                GetFilesOutputFolder(f.getAbsolutePath());
            }
            else {
                try {
                    String temps= f.getPath().replace(this._rawPathOutPut,
                            "");
                    this._myMapInitialFileOutputFolder.put(temps,
                            md5(f.getPath()));
                }
                catch (IOException e){

                }
            }
        }

    }
    /**
     List files output folder produce by the analyse and compute md5sum stored
     in hashMap (read recursively folders)
     @param path : path of folder which contains files expected
    */
    public void GetFilesResultingOfAnalysis( String path ) {
        File root = new File(path);
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                GetFilesResultingOfAnalysis(f.getAbsolutePath());
            }
            else {
               try {
                   String temps= f.getPath().replace(this._rawPathOutPut
                           ,"");
                   System.out.println(temps);
                   this._myMapFilesProduceByAnlaysis.put(temps
                           , md5(f.getPath()));
               }
               catch (IOException e){

               }
            }
        }

    }
    /**
     *Method to compare md5sum of files from output analysis
     * with expected results
     *
     */
    public void CompareAnalysisResult() {
        for(Map.Entry<String, String> entry :
                this._myMapInitialFilesInputFolder.entrySet()) {
            String fileName = entry.getKey();
            String hashCode = entry.getValue();
            if ( hashCode.equals(
                    this._myMapFilesProduceByAnlaysis.get(fileName))){
                System.out.println("Terrible du cul "+fileName);
            }
            else {
                System.out.println("le fichier n'existe pas ou diff hash "
                        +fileName+"\n"
                        +hashCode+"\n"
                        +this._myMapFilesProduceByAnlaysis.get(fileName)+"\n");
            }

        }
    }

    /**
     * Method to compute md5sum of file
     * @param path path of file
     * @return hash md5 of file
     * @throws IOException
     */

    public String md5(String path) throws IOException  {
        String checksumMD5="Na";
        try {
            checksumMD5 = DigestUtils.md5Hex(new FileInputStream(path));
        }
        catch (IOException e){

        }
        return checksumMD5;

    }
}
