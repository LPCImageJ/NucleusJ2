package gred.nucleus.exceptions;


public class fileInOut extends Exception {
	public fileInOut(String fileName) {
		
		System.err.println("File " + fileName + " already exist ");
	}
	
	
}


