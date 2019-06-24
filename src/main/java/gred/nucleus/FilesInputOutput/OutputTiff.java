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
     * with verification if file already exists
     * TODO ADD ERROR IN LOG FILE*/
    public void SaveImage(ImagePlus imageToSave) {
        try {
            if (!is_fileExist()) {
                if(imageToSave.getNSlices()>1) {
                    FileSaver fileSaver = new FileSaver(imageToSave);
                    fileSaver.saveAsTiffStack(this._fullPathFile);
                }
                else{
                    FileSaver fileSaver = new FileSaver(imageToSave);
                    fileSaver.saveAsTiff(this._fullPathFile);
                }
            }
            else{
                throw new fileInOut(imageToSave.getTitle());
            }
        }
        catch (fileInOut e){
        }
    }
}



