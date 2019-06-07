package gred.nucleus.FilesInputOutput;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.lang.*;
import java.util.ArrayList;

/**
 * Class get to get Directory information.
 *
 *
 */


public class Directory {
    /**
     * Directory path
     */
    public String _dirPath = "";
    public ArrayList<File> m_listeOfFiles = new ArrayList<File>();
    public Boolean m_containNdFile=false;
    public ArrayList<File> m_listeOfFilesND = new ArrayList<File>();


    public Directory(String Path) {
        this._dirPath = Path;

    }

    public void CheckAndCreateDir() {
        ChekSeparatorEndPath();
        CreateDire();

    }

    private void ChekSeparatorEndPath() {
        File dir = new File(this._dirPath);
        Character SeparatorEnd = this._dirPath.charAt(this._dirPath.length() - 1);
        if (!(SeparatorEnd.equals(dir.separator))) {
            this._dirPath = this._dirPath + dir.separator;

        }

    }

    private void CreateDire() {
        File dir = new File(this._dirPath);
        if (!dir.exists()) {
            dir.mkdir();
        }
    }

    public String get_dirPath() {
        return this._dirPath;
    }

    public void GetListFiles(String Path) {
        File root = new File(Path);
        File[] list = root.listFiles();
        for (File f : list) {
            if (f.isDirectory()) {
                GetListFiles(f.getAbsolutePath());
            } else {
                this.m_listeOfFiles.add(f);
                if(FilenameUtils.getExtension(f.getName()).equals("nd")) {
                    this.m_containNdFile=true;
                    this.m_listeOfFilesND.add(f);

                }
            }
        }
    }

    public ArrayList<File> ListFiles() {
        return this.m_listeOfFiles;
    }


    public File getFile(int indice) {
        return this.m_listeOfFiles.get(indice);
    }
    public int getNumberFiles(){
        return this.m_listeOfFiles.size();
    }



    public boolean getContainNdFile(){
        return this.m_containNdFile;
    }
    public int getNumberNDFiles(){
        return this.m_listeOfFilesND.size();
    }
    public ArrayList<File> ListFilesND() {
        return this.m_listeOfFilesND;
    }
    public File getNDFile(int indice) {
        return this.m_listeOfFilesND.get(indice);
    }

}
