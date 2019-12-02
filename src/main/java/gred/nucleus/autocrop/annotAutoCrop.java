package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.FilesNames;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.TextRoi;
import ij.io.FileSaver;
import ij.plugin.ZProjector;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackStatistics;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
* This class create Z projection file of 3D stack (wildfield image)
* and report boxes for each nucleus croped by the Autocrop class.
* It takes the raw images imput crop and the list of boxes coordinate
* generate by the Autocrop class.
*
* @author Tristan Dubos and Axel Poulet
*/

public class annotAutoCrop {

    /**
     * @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    /** File to process (Image input) */
    File m_currentFile;



    /** ImagePlus of the Z projection */
    private ImagePlus m_zProjection ;
    /** List of the coordinate boxes of croped nucleus */
    private ArrayList<String> m_boxCoordinates = new ArrayList<String>();
    /** The path of the image to be processed */
    private String m_imageFilePath;
    /** the path of the directory where image with boxes is saved  */
    private String m_outputDirPath;
    /** Parameters crop analyse */
    private AutocropParameters m_autocropParameters;


    public annotAutoCrop(ArrayList<String> ListBox, File imageFile,String outputDirPath,AutocropParameters autocropParameters) throws IOException, FormatException {
        this.m_autocropParameters=autocropParameters;
        this.m_currentFile=imageFile;
        this.m_zProjection = BF.openImagePlus(imageFile.getAbsolutePath())[this.m_autocropParameters.getSlicesOTSUcomputing()];
        this.m_boxCoordinates = ListBox;
        this.m_outputDirPath=outputDirPath;



    }

    public void run(){
        ZProjector zProjectionTmp = new ZProjector(this.m_zProjection);
        this.m_zProjection= projectionMax(zProjectionTmp);
        ajustContrast(0.3);
        for(int i = 0; i < this.m_boxCoordinates.size(); ++i) {
            String fileImg = this.m_boxCoordinates.get(i).toString();
            addBoxCropToZProjection(this.m_boxCoordinates.get(i).toString(),i);
        }

        FilesNames outPutFilesNames=new FilesNames(this.m_currentFile.getAbsolutePath());
        String outFileZbox = this.m_outputDirPath+"_Zprojection.tif";
        File outputFile = new File(outFileZbox);
        saveFile(this.m_zProjection,outFileZbox);


    }
    /**
     * Save the  ImagePlus images
     *
     * @param imagePlusInput image to save
     * @param pathFile path to save the image
     */
    public void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }

    /**
     * Method to project 3D stack to 2D images using Max method projection.
     * @param project : raw data
     * @return Z projection
     */


    private ImagePlus projectionMax(ZProjector project){
       project.setMethod(1);
       project.doProjection();
       return project.getProjection();
    }

    /**
     * Draw the box in the Z projection and add the number of the crop
     *
     * @param corrdinateList : list of coordinate of the current box of nucleus crop
     * @param boxNumber : number of the crop in the list (used in the output of nucleus crop)
     */

    private void  addBoxCropToZProjection(String corrdinateList ,int boxNumber){
        String currentBox[] =corrdinateList.split("\t");
        /** withBox calculation */
        int withBox=Math.abs(Integer.parseInt(currentBox[1])-Integer.parseInt(currentBox[2]))+80;
        /** heigthBox calculation */
        int heigthBox=Math.abs(Integer.parseInt(currentBox[3])-Integer.parseInt(currentBox[4]))+80;
        /** Line size parameter */
        IJ.run("Line Width...", "line=4");
        /** Set draw current box*/
        this.m_zProjection.setRoi(Integer.parseInt(currentBox[1])-40,Integer.parseInt(currentBox[3])-40,withBox,heigthBox);
        IJ.run(this.m_zProjection, "Draw", "stack");
        /** Calculation of the coordinate to add nuclei Number */
        int xBorder=Integer.parseInt(currentBox[1])-100;
        int yBorder=Integer.parseInt(currentBox[3])+((Integer.parseInt(currentBox[4])-Integer.parseInt(currentBox[3]))/2)-20;
        if (xBorder <= 40){  // When the box is in left border the number need to be write on the write
           xBorder =Integer.parseInt(currentBox[2])+60;
        }
        Font font = new Font("Arial", Font.PLAIN, 30);
        TextRoi left = new TextRoi(xBorder,yBorder,Integer.toString(boxNumber) ,font);
        this.m_zProjection.setRoi(left);
        /** Draw the nucleus number aside the box */
        IJ.run(this.m_zProjection, "Draw", "stack");

    }

    /**
     * Method to Contrast the images values and invert the LUT.
     *
     * @param contrast : double number for contrast
     */

    private void ajustContrast (double contrast){
        IJ.run(this.m_zProjection, "Enhance Contrast...", "saturated="+contrast);
        IJ.run(this.m_zProjection, "Invert LUT", "");
    }

}
