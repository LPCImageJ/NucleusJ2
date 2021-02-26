package gred.nucleus.exceptions;


public class FileInOut extends Exception {
	public FileInOut(String fileName) {
		
		System.err.println("File " + fileName + " already exist ");
	}
	
}


