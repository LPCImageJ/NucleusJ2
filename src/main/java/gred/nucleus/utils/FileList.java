package gred.nucleus.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author Tristan Dubos and Axel Poulet
 * <p>
 * Several method on the file
 */
public class FileList {
	boolean _windows = false;
	
	/**
	 *
	 */
	public FileList() {
		_windows = System.getProperty("os.name").startsWith("Windows");
	}
	
	/**
	 * run the methods to list all the file in one input directory
	 *
	 * @param repertoire
	 *
	 * @return Liste of file
	 */
	public File[] run(String repertoire) {
		return repertoryFileList(repertoire);
	}
	
	/**
	 * method to list all the file in one input directory
	 *
	 * @param directory
	 *
	 * @return list file
	 */
	public File[] repertoryFileList(String directory) {
		File   directoryToScan = new File(directory);
		File[] tFileDirectory  = null;
		tFileDirectory = directoryToScan.listFiles();
		for (int i = 0; i < tFileDirectory.length; ++i) {
			if (tFileDirectory[i].isDirectory()) {
				File[] tTempBeforeElement = stockFileBefore(i, tFileDirectory);
				File[] tTempAfterElement  = stockFileAfter(i, tFileDirectory);
				File[] tTempFile          = repertoryFileList(tFileDirectory[i].toString());
				if (tTempFile.length != 0) {
					tFileDirectory = this.resize(tTempBeforeElement, tTempAfterElement, tTempFile, i);
				}
			}
		}
		return tFileDirectory;
	}
	
	/**
	 * methode to list on subdirectory
	 *
	 * @param tTempBeforeElement
	 * @param tTempAfterElement
	 * @param tTempFile
	 * @param indiceMax
	 *
	 * @return
	 */
	public File[] resize(File[] tTempBeforeElement, File[] tTempAfterElement, File[] tTempFile, int indiceMax) {
		File[] tFile = new File[tTempBeforeElement.length + tTempFile.length + tTempAfterElement.length - 1];
		//element insertion in the file list
		for (int j = 0; j < tFile.length; ++j) {
			//list file before the directory :
			if (j < indiceMax) {
				tFile[j] = tTempBeforeElement[j];
			}
			//listed file in the directory :
			else {
				if (j < indiceMax + tTempFile.length) {
					tFile[j] = tTempFile[j - indiceMax];
				}
				//listed files after directory :
				else {
					tFile[j] = tTempAfterElement[j - indiceMax - tTempFile.length];
				}
			}
		}
		return tFile;
	}
	
	/**
	 * @param indiceMax
	 * @param tFile
	 *
	 * @return
	 */
	public File[] stockFileBefore(int indiceMax, File[] tFile) {
		File[] tTempBeforeElement = new File[indiceMax];
		if (indiceMax >= 0) System.arraycopy(tFile, 0, tTempBeforeElement, 0, indiceMax);
		return tTempBeforeElement;
	}
	
	/**
	 * @param indiceMax
	 * @param tFile
	 *
	 * @return
	 */
	public File[] stockFileAfter(int indiceMax, File[] tFile) {
		File[] tTempAfterElement = new File[tFile.length - indiceMax];
		int    j                 = 0;
		for (int k = (indiceMax + 1); k < tFile.length; ++k) {
			tTempAfterElement[j] = tFile[k];
			++j;
		}
		return tTempAfterElement;
	}
	
	/**
	 * @param filePathway
	 * @param tableFile
	 *
	 * @return
	 */
	public boolean isInDirectory(String filePathway, File[] tableFile) {
		boolean testFile = false;
		for (File file : tableFile) {
			if (file.toString().equals(filePathway)) {
				testFile = true;
				break;
			}
		}
		return testFile;
	}
	
	/**
	 * @param regex
	 * @param tFile
	 *
	 * @return
	 */
	public String fileSearch(String regex, File[] tFile) {
		if (_windows) {
			String as  = "\\";
			String das = "\\\\";
			regex = regex.replace(as, das);
		}
		String file = null;
		for (File value : tFile) {
			if (value.toString().matches((regex))) {
				file = value.toString();
				break;
			}
		}
		return file;
	}
	
	
	/**
	 * @param regex
	 * @param tFile
	 *
	 * @return
	 */
	public boolean isDirectoryOrFileExist(String regex, File[] tFile) {
		if (_windows) {
			String as  = "\\";
			String das = "\\\\";
			regex = regex.replace(as, das);
		}
		boolean testFile = false;
		for (File file : tFile) {
			if (file.toString().matches((regex))) {
				testFile = true;
				break;
			}
		}
		return testFile;
	}
	
	
	/**
	 * @param directory
	 * @param tFile
	 *
	 * @return
	 */
	public String[] getDirectoryFiles(String directory, File[] tFile) {
		String[]                 tRef            = directory.split("\\" + File.separator);
		String[]                 tTemp           = new String[0];
		ArrayList<String>        arrayList       = new ArrayList<>();
		HashMap<String, Integer> hasMapDirectory = new HashMap<>();
		for (File file : tFile) {
			String[] temp = file.toString().split("\\" + File.separator);
			if (temp.length > tRef.length + 1) {
				if (!hasMapDirectory.containsKey(temp[tRef.length])) {
					hasMapDirectory.put(temp[tRef.length], 1);
					arrayList.add(temp[tRef.length]);
				}
			}
		}
		if (arrayList.size() > 0) {
			tTemp = new String[arrayList.size()];
			for (int i = 0; i < arrayList.size(); ++i) {
				tTemp[i] = arrayList.get(i);
			}
		}
		return tTemp;
	}
	
	
	/**
	 * @param regex
	 * @param tFile
	 *
	 * @return
	 */
	public ArrayList<String> fileSearchList(String regex, File[] tFile) {
		if (_windows) {
			String as  = "\\";
			String das = "\\\\";
			regex = regex.replace(as, das);
		}
		ArrayList<String> arrayListFile = new ArrayList<>();
		for (File file : tFile) {
			if (file.toString().matches((regex))) {
				arrayListFile.add(file.toString());
			}
		}
		return arrayListFile;
	}
}