package gred.nucleus.FilesInputOutput;
import java.io.File;
import java.lang.*;
import java.util.ArrayList;

public class Directory {
    /**
     * Directory path
     */
    public String _dirPath = "";
    public ArrayList<File> m_boxCoordinates = new ArrayList<File>();


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
                this.m_boxCoordinates.add(f);
            }
        }
    }

    public ArrayList<File> ListFiles() {
        return this.m_boxCoordinates;
    }

    public File getFile(int indice) {
        return this.m_boxCoordinates.get(indice);
    }

    public int getNumberFiles(){
        return this.m_boxCoordinates.size();
    }
}
