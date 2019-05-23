package gred.nucleus.FilesInputOutput;

import gred.nucleus.exceptions.fileInOut;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.apache.commons.io.FileExistsException;

public class OutputTiff extends FilesNames {

    /** Constructor to create file to output */
    public OutputTiff(String filePath){
        super(filePath);
    }
    /** Method to save file
     * with verification if file already exists */
    public void SaveImage(ImagePlus imageToSave)throws Exception {
        try {
            if (!is_fileExist()) {
                FileSaver fileSaver = new FileSaver(imageToSave);
                fileSaver.saveAsTiffStack(this._fullPathFile);
            }
        }
        catch (Exception e){
            System.err.println("Oops, something went wrong for ID "+imageToSave+"! Here's the stack trace:");

            e.printStackTrace();
        }
    }
}



