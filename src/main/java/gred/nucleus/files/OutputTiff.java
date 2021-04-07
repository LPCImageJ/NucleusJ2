package gred.nucleus.files;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;


public class OutputTiff extends FilesNames {
	
	/** Constructor to create file to output */
	public OutputTiff(String filePath) {
		super(filePath);
	}
	
	
	/**
	 * Method to save file with verification if file already exists
	 * <p> TODO ADD ERROR IN LOG FILE
	 */
	public void saveImage(ImagePlus imageToSave) {
		try {
			if (!fileExists()) {
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this.fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this.fullPathFile);
				}
			} else {
				File old = new File(this.fullPathFile);
				if (old.delete()) {
					IJ.log("Deleted old " + this.fullPathFile);
				}
				if (imageToSave.getNSlices() > 1) {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiffStack(this.fullPathFile);
				} else {
					FileSaver fileSaver = new FileSaver(imageToSave);
					fileSaver.saveAsTiff(this.fullPathFile);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}



