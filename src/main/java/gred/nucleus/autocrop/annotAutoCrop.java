package gred.nucleus.autocrop;

import ij.IJ;
import ij.ImagePlus;
import ij.gui.TextRoi;
import ij.io.FileSaver;
import ij.plugin.ZProjector;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.awt.*;
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
    /** ImagePlus of the Z projection */
    private ImagePlus m_zProjection ;
    /** List of the coordinate boxes of croped nucleus */
    private ArrayList<String> m_boxCoordinates = new ArrayList<String>();
    /** The path of the image to be processed */
    private String m_imageFilePath;
    /** the path of the directory where image with boxes is saved  */
    private String m_outputDirPath;

    public annotAutoCrop(ArrayList<String> ListBox, String m_imageFilePath) throws IOException, FormatException {

        ImagePlus[] imageInput = BF.openImagePlus(m_imageFilePath);
        ZProjector zProjectionTmp = new ZProjector(imageInput[0]);
        this.m_zProjection= projectionMax(zProjectionTmp);
        ajustContrast(0.3);
        m_boxCoordinates= ListBox;
        for(int i = 0; i < m_boxCoordinates.size(); ++i) {
            String fileImg = m_boxCoordinates.get(i).toString();
            addBoxCropToZProjection(m_boxCoordinates.get(i).toString(),i);
        }
        //m_zProjection.show();
        String outFileZbox = m_imageFilePath.replaceAll(".tif","_Zprojection.tif");
        outFileZbox=outFileZbox.replaceAll(".TIF","_Zprojection.TIF");
        saveFile(m_zProjection,outFileZbox);


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
        int withBox=Math.abs(Integer.parseInt(currentBox[1])-Integer.parseInt(currentBox[2]));
        int heigthBox=Math.abs(Integer.parseInt(currentBox[3])-Integer.parseInt(currentBox[4]));
        IJ.run("Line Width...", "line=4");
        this.m_zProjection.setRoi(Integer.parseInt(currentBox[1])-20,Integer.parseInt(currentBox[3])-20,withBox+40,heigthBox+40);
        IJ.run(this.m_zProjection, "Draw", "stack");
        int xBorder=(Integer.parseInt(currentBox[1])-60);
        int xBorderLeft =(Integer.parseInt(currentBox[1])-60);
        int xBorderRigth =this.m_zProjection.getWidth()-(Integer.parseInt(currentBox[1])-60);
        if (xBorderLeft <= 50){
            xBorder =withBox+60;

        }
        if (xBorderRigth <= 50){
            xBorder =xBorderRigth;
        }

        Font font = new Font("Arial", Font.PLAIN, 30);

        TextRoi left = new TextRoi(xBorder,(Integer.parseInt(currentBox[3]))-heigthBox/2,Integer.toString(boxNumber) ,font);


        this.m_zProjection.setRoi(left);
        IJ.run(this.m_zProjection, "Draw", "stack");
        this.m_zProjection.show();

    }

    /**
     * Method to Contrast the images values and invert the LUT.
     *
     * @param contrast : double number for contrast
     */

    private void ajustContrast (double contrast){
        IJ.run(this.m_zProjection, "Enhance Contrast...", "saturated="+contrast);
        IJ.run(this.m_zProjection, "Invert LUT", "");
        this.m_zProjection.show();
    }

}
