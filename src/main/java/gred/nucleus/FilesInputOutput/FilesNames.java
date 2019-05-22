package gred.nucleus.FilesInputOutput;

import java.io.File;
import org.apache.commons.io.FilenameUtils;


public class FilesNames {
    /** Path file input*/
    String _pathFile=new String();
    /** File name */
    String _fileName=new String();

    boolean _fileExist=true;

    public FilesNames(){}

    public FilesNames(String filePath){
        this._pathFile=filePath;
        File file= new File(this._pathFile);
        this._fileName= file.getName();
        CheckExistFile();
    }

    public String PrefixeNameFile(){
        return FilenameUtils.removeExtension(this._fileName);
    }
    public void CheckExistFile (){
        File file = new  File(this._pathFile);
        if (!file.exists()){
            this._fileExist = false;
        }

    }


    public boolean is_fileExist() {
        return _fileExist;
    }

    public String get_pathFile() {
        return this._pathFile;
    }
}
