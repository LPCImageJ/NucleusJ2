package gred.nucleus.FilesInputOutput;

import gred.nucleus.exceptions.fileInOut;
import ij.IJ;
import org.apache.commons.io.FilenameUtils;

import java.io.FileWriter;

import java.io.*;

public class OutputTextFile extends FilesNames {

    public OutputTextFile(String filePath){
        super(filePath);
    }
    /** Method to save file
     * with verification if file already exists
     * TODO(@DesTristus) ADD ERROR IN LOG FILE*/
    public void SaveTextFile(String text) throws IOException {
        try {
            int i = 0;
            while (is_fileExist()){
                set_fullPathFile(PrefixeNameFile()+"-"+i+"."+FilenameUtils.getExtension(_fileName));
                CheckExistFile();
                i++;
            }
            BufferedWriter writer;
            writer = new BufferedWriter(new FileWriter(new File(this._fullPathFile)));
            writer.write(text);
            writer.close();

/*            if (!is_fileExist()) {
                BufferedWriter writer;
                writer = new BufferedWriter(new FileWriter(new File(this._fullPathFile)));
                writer.write(text);
                writer.close();
            }*/
        }
        catch (IOException e){
            IJ.log("\n"+this._fullPathFile+" creation failed");
            e.printStackTrace();
        }
        IJ.log("\n"+this._fullPathFile+" created");
    }
}
