package gred.nucleus.autocrop;

import gred.nucleus.files.FilesNames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.invoke.MethodHandles;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;


public class CropFromCoordinates {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	Map<String, String> coordinateToRawImage = new HashMap<>();
	
	
	/**
	 * Method to crop image with coordinate in tab file : tabulate file : pathToCoordinateFile pathToRawImageAssociate
	 *
	 * @param linkCoordinateToRawImage tabulate file
	 */
	public CropFromCoordinates(String linkCoordinateToRawImage)
	throws Exception {
		File coordinateFile = new File(linkCoordinateToRawImage);
		try (Scanner scanner = new Scanner(coordinateFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if (!(line.matches("^#.*"))) {
					String[] splitLine = line.split("\\t");
					this.coordinateToRawImage.put(splitLine[0], splitLine[1]);
					
				}
			}
		}
	}
	
	
	public void runCropFromCoordinate() throws Exception {
		
		for (Map.Entry<String, String> listOfFile : coordinateToRawImage.entrySet()) {
			File coordinateFile = new File(listOfFile.getKey());
			File rawImage       = new File(listOfFile.getValue());
			AutocropParameters autocropParameters =
					new AutocropParameters(rawImage.getParent(), rawImage.getParent());
			Map<Double, Box> boxes            = readCoordinatesTXT(coordinateFile);
			FilesNames       outPutFilesNames = new FilesNames(listOfFile.getValue());
			String           prefix           = outPutFilesNames.prefixNameFile();
			AutoCrop         autoCrop         = new AutoCrop(rawImage, prefix, autocropParameters, boxes);
			autoCrop.cropKernels3();
		}
	}
	
	
	public Map<Double, Box> readCoordinatesTXT(File boxesFile) {
		
		Map<Double, Box> boxLists = new HashMap<>();
		double               count    = 0;
		try (Scanner scanner = new Scanner(boxesFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if ((!(line.matches("^#.*")))
				    && (!(line.matches("^FileName.*")))) {
					String[] splitLine = line.split("\\t");
					short    xMax      = (short) (Integer.parseInt(splitLine[3]) + Integer.parseInt(splitLine[6]));
					
					short yMax = (short) (Integer.parseInt(splitLine[4]) + Integer.parseInt(splitLine[7]));
					short zMax = (short) (Integer.parseInt(splitLine[5]) + Integer.parseInt(splitLine[8]));
					
					Box box = new Box(Short.parseShort(splitLine[3]),
					                  xMax,
					                  Short.parseShort(splitLine[4]),
					                  yMax,
					                  Short.parseShort(splitLine[5]),
					                  zMax);
					
					boxLists.put(Double.valueOf(splitLine[2]), box);
				}
				count++;
			}
		} catch (FileNotFoundException e) {
			LOGGER.error("An error occurred.", e);
		}
		return boxLists;
	}
	
}
