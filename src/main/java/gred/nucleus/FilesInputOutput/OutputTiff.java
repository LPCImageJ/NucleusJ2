package gred.nucleus.FilesInputOutput;

import gred.nucleus.exceptions.fileInOut;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.apache.commons.io.FileExistsException;

public class OutputTiff extends FilesNames {


    public OutputTiff(String filePath){
        super(filePath);
    }
    public void SaveImage(ImagePlus imageToSave)throws Exception {

        try {
            if (!is_fileExist()) {
                FileSaver fileSaver = new FileSaver(imageToSave);
                fileSaver.saveAsTiffStack(this._pathFile);
            }
        }
        catch (Exception e){
            System.err.println("Oops, something went wrong for ID "+imageToSave+"! Here's the stack trace:");

            e.printStackTrace();
        }



        /**  Ca fonctionne
         *
         *
        if (is_fileExist()) {
            throw new fileInOut();
        } else {
            FileSaver fileSaver = new FileSaver(imageToSave);
            fileSaver.saveAsTiffStack(this._pathFile);
        }

         */
    }



        /**

        try {
            if (!is_fileExist()) {
                FileSaver fileSaver = new FileSaver(imageToSave);
                fileSaver.saveAsTiffStack(this._pathFile);
            }
        }
        catch (fileInOut e){
            e.getMessage();
        }
    }

        try{
            CheckExistFile();
            FileSaver fileSaver = new FileSaver(imageToSave);
            fileSaver.saveAsTiffStack(this._pathFile);
        }

         */
}



