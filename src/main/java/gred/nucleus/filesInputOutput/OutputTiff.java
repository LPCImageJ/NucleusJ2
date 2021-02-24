package gred.nucleus.filesInputOutput;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;

public class OutputTiff extends FilesNames {
	
	/** Constructor to create file to output */
	public OutputTiff(String filePath) {
		super(filePath);
	}
	
	
	/** Method to save file with verification if file already exists TODO ADD ERROR IN LOG FILE */
	public void SaveImage(ImagePlus imageToSave) {
		try {
			if (!is_fileExist()) {
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this._fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this._fullPathFile);
				}
			} else {
				File old = new File(this._fullPathFile);
				if (old.delete()) {
					IJ.log("Deleted old " + this._fullPathFile);
				}
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this._fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this._fullPathFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}



