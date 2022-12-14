package gred.nucleus.autocrop;

import gred.nucleus.files.Directory;
import gred.nucleus.files.FilesNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;


public class GenerateProjectionFromCoordinates {
	
	String pathToGIFTSeg;
	String pathToZProjection;
	String pathToCoordinates;
	String pathToRaw;
	
	
	/**
	 * Constructor
	 *
	 * @param pathToGIFTSeg     path to segmented image's folder
	 * @param pathToZProjection path to Zprojection image's from autocrop
	 * @param pathToCoordinates path to coordinates files from autocrop
	 */
	public GenerateProjectionFromCoordinates(String pathToCoordinates, String pathToGIFTSeg, String pathToZProjection) {
		this.pathToGIFTSeg = pathToGIFTSeg;
		this.pathToZProjection = pathToZProjection;
		this.pathToCoordinates = pathToCoordinates;
	}
	
	
	/**
	 * Constructor
	 *
	 * @param pathToCoordinates path to segmented image's folder
	 * @param pathToRaw         path to raw image
	 */
	public GenerateProjectionFromCoordinates(String pathToCoordinates, String pathToRaw) {
		this.pathToCoordinates = pathToCoordinates;
		this.pathToRaw = pathToRaw;
	}
	
	
	/**
	 * Compute list of boxes from coordinates file.
	 *
	 * @param boxFile coordinates file
	 *
	 * @return list of boxes file to draw in red
	 */
	public static Map<String, String> readCoordinatesTXT(File boxFile) {
		
		HashMap<String, String> boxLists = new HashMap<>();
		try (Scanner scanner = new Scanner(boxFile)) {
			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				if ((!(line.matches("^#.*")))
				    && (!(line.matches("^FileName.*")))) {
					String[] splitLine = line.split("\\t");
					String[] fileName  = splitLine[0].split(Pattern.quote(File.separator));
					int      xMax      = Integer.parseInt(splitLine[3]) + Integer.parseInt(splitLine[6]);
					int      yMax      = Integer.parseInt(splitLine[4]) + Integer.parseInt(splitLine[7]);
					int      zMax      = Integer.parseInt(splitLine[5]) + Integer.parseInt(splitLine[8]);
					boxLists.put(fileName[fileName.length - 1], splitLine[0] + "\t"
					                                            + splitLine[3] + "\t"
					                                            + xMax + "\t"
					                                            + splitLine[4] + "\t"
					                                            + yMax + "\t"
					                                            + splitLine[5] + "\t"
					                                            + zMax);
					System.out.println("EUUU" + fileName[fileName.length - 1] + "value"
					                   + splitLine[0] + "\t"
					                   + splitLine[3] + "\t"
					                   + xMax + "\t"
					                   + splitLine[4] + "\t"
					                   + yMax + "\t"
					                   + splitLine[5] + "\t"
					                   + zMax);
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return boxLists;
	}
	
	
	/**
	 * Run new annotation of Zprojection, color in red nuclei which were filtered (in case of GIFT wrapping color in red
	 * nuclei which not pass the segmentation most of case Z truncated )
	 *
	 * @throws Exception
	 */
	public void generateCoordinateFiltered() throws Exception {
		Directory giftSegImages = new Directory(this.pathToGIFTSeg);
		giftSegImages.listImageFiles(this.pathToGIFTSeg);
		giftSegImages.checkIfEmpty();
		Directory zProjection = new Directory(this.pathToZProjection);
		zProjection.listImageFiles(this.pathToZProjection);
		zProjection.checkIfEmpty();
		Directory coordinates = new Directory(this.pathToCoordinates);
		coordinates.listAllFiles(this.pathToCoordinates);
		coordinates.checkIfEmpty();
		for (short i = 0; i < coordinates.getNumberFiles(); ++i) {
			File                coordinateFile        = coordinates.getFile(i);
			Map<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			ArrayList<Integer>  boxNumber             = new ArrayList<>();
			ArrayList<String>   boxListsNucleiNotPass = new ArrayList<>();
			Map<String, String> sortedMap             = new TreeMap<>(listOfBoxes);
			for (Map.Entry<String, String> entry : sortedMap.entrySet()) {
				if (!(giftSegImages.checkIfFileExists(entry.getKey()))) {
					boxListsNucleiNotPass.add(entry.getValue());
					System.out.println("add " + entry.getValue());
				}
			}
			File currentZProjection = zProjection.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                                   .substring(0,
			                                                                                              coordinateFile
					                                                                                              .getName()
					                                                                                              .lastIndexOf(
							                                                                                              '.')) +
			                                                                     "_Zprojection");
			AutocropParameters autocropParameters = new AutocropParameters(currentZProjection.getParent(),
			                                                               currentZProjection.getParent() +
			                                                               zProjection.getSeparator());
			AnnotateAutoCrop annotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         currentZProjection,
			                                                         currentZProjection.getParent() +
			                                                         zProjection.getSeparator() +
			                                                         currentZProjection.getName()
			                                                                           .substring(0,
			                                                                                      currentZProjection.getName()
			                                                                                                        .lastIndexOf(
					                                                                                                        '.')),
			                                                         autocropParameters);
			annotateAutoCrop.runAddBadCrop(boxNumber);
		}
	}
	
	
	public void generateCoordinate() throws Exception {
		Directory rawImage = new Directory(this.pathToRaw);
		rawImage.listImageFiles(this.pathToRaw);
		rawImage.checkIfEmpty();
		Directory coordinates = new Directory(this.pathToCoordinates);
		coordinates.listAllFiles(this.pathToCoordinates);
		coordinates.checkIfEmpty();
		
		for (short i = 0; i < coordinates.getNumberFiles(); ++i) {
			File                coordinateFile        = coordinates.getFile(i);
			Map<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			ArrayList<String>   boxListsNucleiNotPass = new ArrayList<>();
			for (Map.Entry<String, String> entry : listOfBoxes.entrySet()) {
				boxListsNucleiNotPass.add(entry.getValue());
			}
			System.out.println(coordinateFile.getName());
			
			File currentRaw = rawImage.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                        .substring(0,
			                                                                                   coordinateFile.getName()
			                                                                                                 .lastIndexOf(
					                                                                                                 '.')));
			FilesNames outPutFilesNames = new FilesNames(currentRaw.toString());
			String     prefix           = outPutFilesNames.prefixNameFile();
			System.out.println("current raw " + currentRaw.getName());
			AutocropParameters autocropParameters = new AutocropParameters(currentRaw.getParent(),
			                                                               currentRaw.getParent() +
			                                                               rawImage.getSeparator());
			AnnotateAutoCrop annotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         currentRaw,
			                                                         currentRaw.getParent() + rawImage.getSeparator(),
			                                                         prefix,
			                                                         autocropParameters);
			annotateAutoCrop.run();
		}
	}
	
}
