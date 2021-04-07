package gred.nucleus.autocrop;

import gred.nucleus.files.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class GenerateOverlay {
	
	HashMap<String, String> linkOverlayProjection = new HashMap<>();
	
	
	public GenerateOverlay(String linkOverlayProjection) throws Exception {
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
			ImagePlus Zprojection = IJ.openImage(listOfFile.getValue());
			System.out.println("\n\n" + listOfFile.getKey() + "\n" + listOfFile.getValue() + "\n\n");
			//IJ.run(Zprojection, "Fire", "");
			//IJ.run(Zprojection, "Invert LUT", "");
			overlay.show();
			Zprojection.show();
			IJ.run("Add Image...", overlay + " x=0 y=0 opacity=50");
			saveFile(Zprojection, output.getDirPath() + File.separator +
			                      zprojectionFile.getName().substring(0, zprojectionFile.getName().lastIndexOf('.')) +
			                      "_MERGED.tiff");
			overlay.close();
			Zprojection.close();
		}
	}
	
}
