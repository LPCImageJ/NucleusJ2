package gred.nucleus.exceptions;

import org.apache.commons.io.FileExistsException;

public class fileInOut extends Exception{
    public fileInOut(){
        System.out.println("File  already exist");
    }
}
