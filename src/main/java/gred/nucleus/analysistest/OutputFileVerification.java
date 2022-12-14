package gred.nucleus.analysistest;

import org.apache.commons.codec.digest.DigestUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * This class is used to check results after structural code modification which not affect results and specifically:
 * <ul>
 * <li>Images formats handling</li>
 * <li>Analysis results (3D parameters computation)</li>
 * </ul>
 * <p>
 * The method use a folder with images analysed at a specific version "under control" (expected results). A second
 * folder where the same images are analysed with of a new version of the code which should not affect results. Here we
 * use md5sum to check changes between files. The class produce a report with the list of file changed.
 * <p> TODO Create class to produce a report
 * <p> TODO Complete the description
 * <p> TODO May be remove this class after GIT integration
 *
 * @author Tristan Dubos
 */
public class OutputFileVerification {
	
	/** Key of files expected in the result directory */
	Map<String, String> myMapInitialFilesInputFolder = new HashMap<>();
	/** Key of files produce by the analysis */
	Map<String, String> myMapInitialFileOutputFolder = new HashMap<>();
	/** list of files produce by the analysis */
	Map<String, String> myMapFilesProduceByAnalysis  = new HashMap<>();
	
	
	/** Path output analysis files */
	String rawPathOutPut;
	/** Raw path expected files */
	String rawPathExpectedResult;
	
	
	/**
	 * Constructor containing 2 parameters path containing expected results and path to the raw data
	 *
	 * @param pathExpectedResult Path of expected result (version under control)
	 * @param pathOutPut         Path of out results new version
	 */
	public OutputFileVerification(String pathExpectedResult, String pathOutPut) {
		this.rawPathExpectedResult = pathExpectedResult;
		this.rawPathOutPut = pathOutPut;
	}
	
	
	/**
	 * List files expected and compute md5sum stored in hashMap (read recursively folders)
	 *
	 * @param path Path of folder which contains files expected
	 */
	public void getFileResultExpected(String path) {
		File   root = new File(path);
		File[] list = root.listFiles();
		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					getFileResultExpected(f.getAbsolutePath());
				} else {
					String temps = f.getPath().replace(
							this.rawPathExpectedResult, "");
					this.myMapInitialFilesInputFolder.put(temps, md5(f.getPath()));
				}
			}
		}
	}
	
	
	/**
	 * List files already inside the output folder and compute md5sum stored in hashMap (read recursively folders)
	 *
	 * @param path Path of folder which contains files expected
	 */
	public void getFilesOutputFolder(String path) {
		File   root = new File(path);
		File[] list = root.listFiles();
		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					getFilesOutputFolder(f.getAbsolutePath());
				} else {
					String temps = f.getPath().replace(this.rawPathOutPut, "");
					this.myMapInitialFileOutputFolder.put(temps, md5(f.getPath()));
				}
			}
		}
	}
	
	
	/**
	 * List files output folder produce by the analyse and compute md5sum stored in hashMap (read recursively folders)
	 *
	 * @param path Path of folder which contains files expected
	 */
	public void getFilesResultingOfAnalysis(String path) {
		File   root = new File(path);
		File[] list = root.listFiles();
		if (list != null) {
			for (File f : list) {
				if (f.isDirectory()) {
					getFilesResultingOfAnalysis(f.getAbsolutePath());
				} else {
					String temps = f.getPath().replace(this.rawPathOutPut
							, "");
					System.out.println(temps);
					this.myMapFilesProduceByAnalysis.put(temps, md5(f.getPath()));
				}
			}
		}
	}
	
	
	/** Method to compare md5sum of files from output analysis with expected results */
	public void compareAnalysisResult() {
		for (Map.Entry<String, String> entry :
				this.myMapInitialFilesInputFolder.entrySet()) {
			String fileName = entry.getKey();
			String hashCode = entry.getValue();
			if (hashCode.equals(
					this.myMapFilesProduceByAnalysis.get(fileName))) {
				System.out.println("Terrible du cul " + fileName);
			} else {
				System.out.println("le fichier n'existe pas ou diff hash "
				                   + fileName + "\n"
				                   + hashCode + "\n"
				                   + this.myMapFilesProduceByAnalysis.get(fileName) + "\n");
			}
		}
	}
	
	
	/**
	 * Method to compute md5sum of a file
	 *
	 * @param path File path
	 *
	 * @return MD5 hash of file
	 */
	public String md5(String path) {
		String checksumMD5 = "Na";
		try {
			checksumMD5 = DigestUtils.md5Hex(new FileInputStream(path));
		} catch (IOException e) {
			e.printStackTrace();
		}
		return checksumMD5;
	}
	
}
