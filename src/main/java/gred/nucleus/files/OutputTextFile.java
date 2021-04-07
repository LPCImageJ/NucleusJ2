package gred.nucleus.files;

import ij.IJ;
import org.apache.commons.io.FilenameUtils;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;


public class OutputTextFile extends FilesNames {
	
	public OutputTextFile(String filePath) {
		super(filePath);
	}
	
	
	/**
	 * Method to save file with verification if file already exists
	 * <p> TODO(@DesTristus) ADD ERROR IN LOG FILE
	 */
	public void saveTextFile(String text, boolean keepExistingFile) {
		try {
			if (keepExistingFile) {
				int i = 0;
				while (fileExists()) {
					setFullPathFile(prefixNameFile() + "-" + i + "." + FilenameUtils.getExtension(fileName));
					checkFileExists();
					i++;
				}
			}
			try (BufferedWriter writer = new BufferedWriter(new FileWriter(this.fullPathFile))) {
				writer.write(text);
			}

            /*if (!fileExist()) {
                BufferedWriter writer;
                writer = new BufferedWriter(new FileWriter(new File(this.fullPathFile)));
                writer.write(text);
                writer.close();
            }*/
		} catch (IOException e) {
			IJ.log("\n" + this.fullPathFile + " creation failed\n");
			e.printStackTrace();
		}
		IJ.log("\n" + this.fullPathFile + " created\n");
	}
	
}
