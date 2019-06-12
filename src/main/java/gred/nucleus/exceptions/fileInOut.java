package gred.nucleus.exceptions;

import org.apache.commons.io.FileExistsException;

public class fileInOut extends Exception{
    public fileInOut(String fileName){

        System.err.println("File "+fileName+" already exist ");
    }


}


