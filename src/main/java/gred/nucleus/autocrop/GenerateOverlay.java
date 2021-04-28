package gred.nucleus.autocrop;

import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class GenerateOverlay {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	Map<String, String> linkOverlayProjection = new HashMap<>();
	
	
	public GenerateOverlay(String linkOverlayProjection) throws FileNotFoundException {
		File overlayProjection = new File(linkOverlayProjection);
		try (Scanner scanner = new Scanner(overlayProjection)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (!(line.matches("^#.*"))) {
					String[] splitLine = line.split("\\t");
					this.linkOverlayProjection.put(splitLine[0], splitLine[1]);
				}
			}
		}
	}
	
	
	/**
	 * Save output file in png format for OMERO
	 *
	 * @param imagePlusInput image to save
	 * @param pathFile       path to save image
	 */
	public static void saveFile(ImagePlus imagePlusInput, String pathFile) {
		FileSaver fileSaver = new FileSaver(imagePlusInput);
		fileSaver.saveAsPng(pathFile);
	}
	
	
	public void run() {
		for (Map.Entry<String, String> listOfFile : this.linkOverlayProjection.entrySet()) {
			File zprojectionFile = new File(listOfFile.getValue());
			Directory output = new Directory(zprojectionFile.getParent() + File.separator +
			                                 "Overlay_Projection_MERGED");
			output.checkAndCreateDir();
			ImagePlus overlay     = IJ.openImage(listOfFile.getKey());
			ImagePlus zProjection = IJ.openImage(listOfFile.getValue());
			LOGGER.debug("{}: {}", listOfFile.getKey(), listOfFile.getValue());
			//IJ.run(zProjection, "Fire", "");
			//IJ.run(zProjection, "Invert LUT", "");
			overlay.show();
			zProjection.show();
			IJ.run("Add Image...", overlay + " x=0 y=0 opacity=50");
			saveFile(zProjection, output.getDirPath() + File.separator +
			                      zprojectionFile.getName().substring(0, zprojectionFile.getName().lastIndexOf('.')) +
			                      "_MERGED.tiff");
			overlay.close();
			zProjection.close();
		}
	}
	
}
