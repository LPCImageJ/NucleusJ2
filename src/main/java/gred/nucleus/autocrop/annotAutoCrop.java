package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import ij.IJ;
import ij.ImagePlus;
import ij.gui.TextRoi;
import ij.io.FileSaver;
import ij.plugin.ZProjector;
import loci.formats.FormatException;
import loci.plugins.BF;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
* This class create Z projection file of 3D stack (wide field image)
* and report boxes for each nucleus croped by the Autocrop class.
* It takes the raw images imput crop and the list of boxes coordinate
* generate by the Autocrop class.
*
* @author Tristan Dubos and Axel Poulet
*/

public class annotAutoCrop {

       /** File to process (Image input) */
    File m_currentFile;
    /** ImagePlus of the Z projection */
    private ImagePlus m_zProjection ;
    /** List of the coordinate boxes of croped nucleus */
    private ArrayList<String> m_boxCoordinates;
    /** the path of the directory where image with boxes is saved  */
    private String m_outputDirPath;
    /** Parameters crop analyse */
    private AutocropParameters m_autocropParameters;
    /**
     * The prefix of the names of the output cropped images, which are automatically numbered
     */
    private String m_outputFilesPrefix;

    /**
     * Constructor for autocrop
     * @param ListBox : ArrayList of coordinate (coordinate of nuclei cropped)
     * @param imageFile : File of current image analysed
     * @param outputDirPath : path to the output folder
     * @param autocropParameters : autocrop parameters used to crop nuclei
     * @param prefix : name of raw image (use for z projection)
     * @throws IOException
     * @throws FormatException
     */

    public annotAutoCrop(ArrayList<String> ListBox,
                         File imageFile,
                         String outputDirPath,
                         String prefix,
                         AutocropParameters autocropParameters)
            throws IOException, FormatException {
        this.m_autocropParameters=autocropParameters;
        this.m_currentFile=imageFile;
        this.m_zProjection = BF.openImagePlus(
                imageFile.getAbsolutePath())[
                        this.m_autocropParameters.getSlicesOTSUcomputing()];
        this.m_boxCoordinates = ListBox;
        this.m_outputFilesPrefix=prefix;
        this.m_outputDirPath=outputDirPath;
        Directory dirOutput= new Directory(
                this.m_outputDirPath+"zprojection");
        System.out.println("le dir "+this.m_outputDirPath+"zprojection");
        dirOutput.CheckAndCreateDir();


    }
    /**
     * Constructor for re-generate projection after segmentatio
     * @param ListBox : ArrayList of coordinate (coordinate of nuclei cropped)
     * @param imageFile : File of current image analysed
     * @param outputDirPath : path to the output folder
     * @param autocropParameters : autocrop parameters used to crop nuclei
     * @throws IOException
     * @throws FormatException
     */

    public annotAutoCrop(ArrayList<String> ListBox,
                         File imageFile,
                         String outputDirPath,
                         AutocropParameters autocropParameters)
            throws IOException, FormatException {
        this.m_autocropParameters=autocropParameters;
        this.m_currentFile=imageFile;
        this.m_zProjection = BF.openImagePlus(
                imageFile.getAbsolutePath())[
                this.m_autocropParameters.getSlicesOTSUcomputing()];
        this.m_boxCoordinates = ListBox;


        this.m_outputDirPath=outputDirPath;



    }


    /**
     * Main method to generate Z projection of wide fild 3D image.
     * Parameter use are max intensity projection (projectionMax method)
     * and contrast modification of 0,3.
     *
     */
    public void runAddBadCrop(ArrayList<Integer> _box){
        IJ.run(this.m_zProjection, "Enhance Contrast", "saturated=0.35");
        IJ.run(this.m_zProjection, "RGB Color", "");
        ZProjector zProjectionTmp = new ZProjector(this.m_zProjection);

        for(int i = 0; i < this.m_boxCoordinates.size(); ++i) {
            String [] splitLine = this.m_boxCoordinates.get(i).split("\\t");
            String [] fileName =splitLine[0].split("\\/");
            String [] Name =fileName[fileName.length-1].split("_");
            addBadCropBoxToZProjection(this.m_boxCoordinates.get(i),Integer.parseInt(Name[Name.length-2]));
        }
        String outFileZbox = this.m_outputDirPath+"_BAD_CROP_LESS.tif";
        saveFile(this.m_zProjection,outFileZbox);


    }


    /**
     * Main method to generate Z projection of wide fild 3D image.
     * Parameter use are max intensity projection (projectionMax method)
     * and contrast modification of 0,3.
     *
     */
    public void run(){
        ZProjector zProjectionTmp = new ZProjector(this.m_zProjection);
        this.m_zProjection= projectionMax(zProjectionTmp);
        ajustContrast(0.3);
        for(int i = 0; i < this.m_boxCoordinates.size(); ++i) {
            addBoxCropToZProjection(this.m_boxCoordinates.get(i),i);
        }
        String outFileZbox = this.m_outputDirPath+this.m_currentFile.separator+"zprojection"+this.m_currentFile.separator+m_outputFilesPrefix+"_Zprojection.tif";
        System.out.println("outFileZbox "+ outFileZbox);

        saveFile(this.m_zProjection,outFileZbox);


    }
    /**
     * Save the ImagePlus Zprojection image
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
     * Draw box from coordinate in the Z projection image and add the crop
     * number.
     *
     * @param coordinateList : list of coordinate of the current box of nucleus
     *                       crop
     * @param boxNumber : number of the crop in the list (used in the output of
     *                  nucleus crop)
     */

    private void addBoxCropToZProjection(String coordinateList ,int boxNumber){
        String currentBox[] =coordinateList.split("\t");
        /** withBox calculation */

        int withBox=Math.abs(Integer.parseInt(currentBox[2]))-Math.abs(Integer.parseInt(currentBox[1]));
        /** heigthBox calculation */
        int heigthBox=Math.abs(Integer.parseInt(currentBox[4]))-Math.abs(Integer.parseInt(currentBox[3]));
        /** Line size parameter */
        IJ.setForegroundColor(0, 0, 0);
        IJ.run("Line Width...", "line=4");
        /** Set draw current box*/
        this.m_zProjection.setRoi(Integer.parseInt(currentBox[1]),
                        Integer.parseInt(currentBox[3]),
                        withBox,heigthBox);
        IJ.run(this.m_zProjection, "Draw", "stack");


        /** Calculation of the coordinate to add nuclei Number */
        int xBorder=Integer.parseInt(currentBox[1])-100;
        int yBorder=Integer.parseInt(currentBox[3])+((
                Integer.parseInt(currentBox[4])
                        -Integer.parseInt(currentBox[3]))/2)
                -20;
        if (xBorder <= 40){  // When the box is in left border the number need
                             // to be write on the right of the box
           xBorder =Integer.parseInt(currentBox[2])+60;
        }
        Font font = new Font("Arial", Font.PLAIN, 30);
        TextRoi left = new TextRoi(xBorder,
                yBorder,
                Integer.toString(boxNumber),
                font);
        this.m_zProjection.setRoi(left);
        /** Draw the nucleus number aside the box */
        IJ.run(this.m_zProjection, "Draw", "stack");

    }


    private void addBadCropBoxToZProjection(String coordinateList ,int boxNumber){
        String currentBox[] =coordinateList.split("\t");
        /** withBox calculation */

        int withBox=Math.abs(Integer.parseInt(currentBox[2]))-Math.abs(Integer.parseInt(currentBox[1]));
        /** heigthBox calculation */
        int heigthBox=Math.abs(Integer.parseInt(currentBox[4]))-Math.abs(Integer.parseInt(currentBox[3]));
        /** Line size parameter */

      /** !!!!!!!!!!! on contrast la projection sinon elle est en GRIS ?????? */
        //IJ.run(this.m_zProjection, "Enhance Contrast", "saturated=0.35");
        //IJ.run(this.m_zProjection, "RGB Color", "");
        IJ.setForegroundColor(255, 0, 0);
        IJ.run("Line Width...", "line=4");
        /** Set draw current box*/
        this.m_zProjection.setRoi(Integer.parseInt(currentBox[1]),
                Integer.parseInt(currentBox[3]),
                withBox,heigthBox);
        IJ.run(this.m_zProjection, "Draw", "stack");


        /** Calculation of the coordinate to add nuclei Number */
        int xBorder=Integer.parseInt(currentBox[1])-100;
        int yBorder=Integer.parseInt(currentBox[3])+((
                Integer.parseInt(currentBox[4])
                        -Integer.parseInt(currentBox[3]))/2)
                -20;
        if (xBorder <= 40){  // When the box is in left border the number need
            // to be write on the right of the box
            xBorder =Integer.parseInt(currentBox[2])+60;
        }
        Font font = new Font("Arial", Font.PLAIN, 30);

        IJ.run("Colors...", "foreground=red");
        TextRoi left = new TextRoi(xBorder,
                yBorder,
                Integer.toString(boxNumber),
                font);
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
        IJ.run(this.m_zProjection,
                "Enhance Contrast...",
                "saturated="+contrast);
        IJ.run(this.m_zProjection,
                "Invert LUT", "");
    }

}
