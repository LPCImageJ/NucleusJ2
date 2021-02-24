package gred.nucleus.filesInputOutput;

import org.apache.commons.io.FilenameUtils;

import java.io.File;


public class FilesNames {
	/** Path file input */
	String _pathFile     = "";
	/** File name */
	String _fileName     = "";
	/** Complete pathFile */
	String _fullPathFile = "";
	
	boolean _fileExist = true;
	
	public FilesNames() {
	}
	
	
	/** Constructor to create file object */
	public FilesNames(String filePath) {
		this._fullPathFile = filePath;
		File file = new File(filePath);
		this._pathFile = file.getParent() + File.separator;
		this._fileName = file.getName();
		CheckExistFile();
	}
	
	
	public String prefixNameFile() {
		return FilenameUtils.removeExtension(this._fileName);
	}
	
	
	/** Method to check if file exists */
	public void CheckExistFile() {
		File file = new File(this._fullPathFile);
		if (!file.exists()) {
			this._fileExist = false;
		}
	}
	
	
	/** return boolean true for existing file */
	public boolean is_fileExist() {
		return _fileExist;
	}
	
	
	/** return path to file */
	public String get_pathFile() {
		return this._pathFile;
	}
	
	
	public void set_fullPathFile(String fileName) {
		this._fullPathFile = _pathFile + fileName;
	}
}
