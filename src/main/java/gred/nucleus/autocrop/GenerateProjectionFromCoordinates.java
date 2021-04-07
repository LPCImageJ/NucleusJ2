package gred.nucleus.autocrop;

import gred.nucleus.filesInputOutput.Directory;
import gred.nucleus.filesInputOutput.FilesNames;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.regex.Pattern;


public class GenerateProjectionFromCoordinates {
	
	String pathToGIFTSeg;
	String pathToZprojection;
	String pathToCoordinates;
	String pathToRaw;
	
	
	/**
	 * Constructor
	 *
	 * @param pathToGIFTSeg     path to segmented image's folder
	 * @param pathToZprojection path to Zprojection image's from autocrop
	 * @param pathToCoordinates path to coordinates files from autocrop
	 */
	public GenerateProjectionFromCoordinates(String pathToCoordinates, String pathToGIFTSeg, String pathToZprojection) {
		this.pathToGIFTSeg = pathToGIFTSeg;
		this.pathToZprojection = pathToZprojection;
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
	public static HashMap<String, String> readCoordinatesTXT(File boxFile) {
		
		HashMap<String, String> boxLists = new HashMap<>();
		try {
			Scanner scanner = new Scanner(boxFile);
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
		Directory GIFTSegImages = new Directory(this.pathToGIFTSeg);
		GIFTSegImages.listImageFiles(this.pathToGIFTSeg);
		GIFTSegImages.checkIfEmpty();
		Directory Zprojection = new Directory(this.pathToZprojection);
		Zprojection.listImageFiles(this.pathToZprojection);
		Zprojection.checkIfEmpty();
		Directory Coordinates = new Directory(this.pathToCoordinates);
		Coordinates.listAllFiles(this.pathToCoordinates);
		Coordinates.checkIfEmpty();
		for (short i = 0; i < Coordinates.getNumberFiles(); ++i) {
			File                    coordinateFile        = Coordinates.getFile(i);
			HashMap<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			ArrayList<Integer>      boxNumber             = new ArrayList<>();
			ArrayList<String>       boxListsNucleiNotPass = new ArrayList<>();
			Map<String, String>     sortedMap             = new TreeMap<>(listOfBoxes);
			for (HashMap.Entry<String, String> entry : sortedMap.entrySet()) {
				if (!(GIFTSegImages.checkIfFileExists(entry.getKey()))) {
					boxListsNucleiNotPass.add(entry.getValue());
					System.out.println("add " + entry.getValue());
				}
			}
			File CurrentZprojection = Zprojection.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                                   .substring(0,
			                                                                                              coordinateFile
					                                                                                              .getName()
					                                                                                              .lastIndexOf(
							                                                                                              '.')) +
			                                                                     "_Zprojection");
			AutocropParameters autocropParameters = new AutocropParameters(CurrentZprojection.getParent(),
			                                                               CurrentZprojection.getParent() +
			                                                               Zprojection.getSeparator());
			AnnotateAutoCrop AnnotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         CurrentZprojection,
			                                                         CurrentZprojection.getParent() +
			                                                         Zprojection.getSeparator() +
			                                                         CurrentZprojection.getName()
			                                                                           .substring(0,
			                                                                                      CurrentZprojection.getName()
			                                                                                                        .lastIndexOf(
					                                                                                                        '.')),
			                                                         autocropParameters);
			AnnotateAutoCrop.runAddBadCrop(boxNumber);
		}
	}
	
	
	public void generateCoordinate() throws Exception {
		Directory RawImage = new Directory(this.pathToRaw);
		RawImage.listImageFiles(this.pathToRaw);
		RawImage.checkIfEmpty();
		Directory Coordinates = new Directory(this.pathToCoordinates);
		Coordinates.listAllFiles(this.pathToCoordinates);
		Coordinates.checkIfEmpty();
		
		for (short i = 0; i < Coordinates.getNumberFiles(); ++i) {
			File                    coordinateFile        = Coordinates.getFile(i);
			HashMap<String, String> listOfBoxes           = readCoordinatesTXT(coordinateFile);
			ArrayList<Integer>      boxNumber             = new ArrayList<>();
			ArrayList<String>       boxListsNucleiNotPass = new ArrayList<>();
			for (HashMap.Entry<String, String> entry : listOfBoxes.entrySet()) {
				boxListsNucleiNotPass.add(entry.getValue());
			}
			System.out.println(coordinateFile.getName());
			
			File CurrentRaw = RawImage.searchFileNameWithoutExtension(coordinateFile.getName()
			                                                                        .substring(0,
			                                                                                   coordinateFile.getName()
			                                                                                                 .lastIndexOf(
					                                                                                                 '.')));
			FilesNames outPutFilesNames = new FilesNames(CurrentRaw.toString());
			String     prefix           = outPutFilesNames.prefixNameFile();
			System.out.println("current raw " + CurrentRaw.getName());
			AutocropParameters autocropParameters = new AutocropParameters(CurrentRaw.getParent(),
			                                                               CurrentRaw.getParent() +
			                                                               RawImage.getSeparator());
			AnnotateAutoCrop AnnotateAutoCrop = new AnnotateAutoCrop(boxListsNucleiNotPass,
			                                                         CurrentRaw,
			                                                         CurrentRaw.getParent() + RawImage.getSeparator(),
			                                                         prefix,
			                                                         autocropParameters);
			AnnotateAutoCrop.run();
		}
	}
	
}
