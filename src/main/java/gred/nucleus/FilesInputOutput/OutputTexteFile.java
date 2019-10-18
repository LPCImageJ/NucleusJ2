package gred.nucleus.FilesInputOutput;

import gred.nucleus.exceptions.fileInOut;
import java.io.FileWriter;

import java.io.*;

public class OutputTexteFile extends FilesNames {

    public OutputTexteFile(String filePath){
        super(filePath);
    }
    /** Method to save file
     * with verification if file already exists
     * TODO(@DesTristus) ADD ERROR IN LOG FILE*/
    public void SaveTexteFile(String text) throws IOException {
        try {
            if (!is_fileExist()) {
                BufferedWriter writer;
                writer = new BufferedWriter(new FileWriter(new File(this._fullPathFile+".txt")));
                writer.write(text);
                writer.close();
            }
            else{
                throw new fileInOut(this._fullPathFile+".txt");
            }
        }
        catch (fileInOut e){
        }
    }
}
