package gred.nucleus.FilesInputOutput;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.*;
import java.util.ArrayList;

/**
 * Class get to list directory and sub directory.
 *
 *
 */


public class Directory {
    /**
     * Directory path
     */
    public String _dirPath = "";
    /** List of files in current forlder + recursive folder */
    public ArrayList<File> m_listeOfFiles = new ArrayList<File>();
    /** Check if directory contain nd files */
    public Boolean m_containNdFile=false;
    /** List of nd files */
    public ArrayList<File> m_listeOfFilesND = new ArrayList<File>();

    /** Constructor
     * @param Path of directory
     */
    public Directory(String Path) {
        this._dirPath = Path;

    }

    /**
     * Method to check if directory and create if doesn't
     */
    public void CheckAndCreateDir() {
        ChekSeparatorEndPath();
        CreateDire();

    }

    /**
     * Check if separator exist
     */
    private void ChekSeparatorEndPath() {
        File dir = new File(this._dirPath);
        Character SeparatorEnd = this._dirPath.charAt(this._dirPath.length() - 1);
        if (!(SeparatorEnd.equals(dir.separator))) {
            this._dirPath = this._dirPath + dir.separator;

        }

    }

    /**
     * Method creating folder if doesn't exist.
     */
    private void CreateDire() {
        File dir = new File(this._dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    /**
     *
     * @return path current directory
     */
    public String get_dirPath() {
        return this._dirPath;
    }

    /**
     * Method to recursively list files contains in folder and subfolder. (Argument needed because of recursive way)
     * @param Path path of folder
     */
    public void listFiles(String Path) {
        File root = new File(Path);
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
               listFiles(f.getAbsolutePath());
            } else {
                this.m_listeOfFiles.add(f);
                if(FilenameUtils.getExtension(f.getName()).equals("nd")) {
                    this.m_containNdFile=true;
                    this.m_listeOfFilesND.add(f);

                }
            }
        }
    }

    /**
     * Replace list files if ND files have been listed.
     */
    public void checkAndActualiseNDFiles(){
        if(this.m_containNdFile ==true){
            this.m_listeOfFiles=this.m_listeOfFilesND;
        }
    }

    /**
     * @return list of files
     */

    public ArrayList<File> ListFiles() {
        return this.m_listeOfFiles;
    }

    /**
     * @param indice of file in list array
     * @return File
     */
    public File getFile(int indice) {
        return this.m_listeOfFiles.get(indice);
    }

    /**
     *
     * @return number of file listed
     */

    public int getNumberFiles(){
        return this.m_listeOfFiles.size();
    }





}
