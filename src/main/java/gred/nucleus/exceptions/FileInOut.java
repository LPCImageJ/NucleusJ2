package gred.nucleus.exceptions;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class FileInOut extends Exception {
	public FileInOut(String fileName) {
		Logger logger = LoggerFactory.getLogger(getClass());
		logger.error("File {} already exist ", fileName);
	}
	
}


