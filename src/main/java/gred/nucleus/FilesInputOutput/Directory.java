package gred.nucleus.FilesInputOutput;
import java.io.File;
import java.lang.*;

public class Directory {
    /** Directory path */
    public String _dirPath = "";

    public Directory(String Path) {
       this._dirPath = Path;

    }

    public void CheckAndCreateDir() {
        ChekSeparatorEndPath();
        CreateDire();

    }
    private void ChekSeparatorEndPath(){
        File dir = new  File(this._dirPath);
        Character SeparatorEnd =this._dirPath.charAt(this._dirPath.length() - 1);
        if(!(SeparatorEnd.equals(dir.separator))){
            this._dirPath=this._dirPath+dir.separator;

        }

    }
    private void CreateDire(){
        File dir = new  File(this._dirPath);
        if (!dir.exists()){
            dir.mkdir();
        }
    }
    public String get_dirPath(){
        return this._dirPath;
    }
}
