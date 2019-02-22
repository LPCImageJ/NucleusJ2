package gred.nucleus.autocrop;

import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.plugin.ZProjector;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.IOException;
import java.util.ArrayList;

public class annotAutoCrop {

    /** @throws IOException if file problem
     * @throws FormatException Bioformat exception
     */
    private ImagePlus m_zProjection ;

    private ArrayList<String> m_boxCoordinates = new ArrayList<String>();
    /** The path of the image to be processed */
    private String m_imageFilePath;
    /** the path of the directory where image with boxes is saved  */
    private String m_outputDirPath;

    public annotAutoCrop(ArrayList<String> ListBox, String m_imageFilePath) throws IOException, FormatException {

        ImagePlus[] imageInput = BF.openImagePlus(m_imageFilePath);
        ZProjector zProjectionTmp = new ZProjector(imageInput[0]);
        zProjectionTmp.setMethod(1);
        zProjectionTmp.doProjection();
        ImagePlus m_zProjection = zProjectionTmp.getProjection();
        IJ.run(m_zProjection, "Enhance Contrast...", "saturated=0.3");
        IJ.run(m_zProjection, "Invert LUT", "");
       // m_zProjection.show();
        //projImp.setRoi();

        m_boxCoordinates= ListBox;
        for(int i = 0; i < m_boxCoordinates.size(); ++i) {
            String fileImg = m_boxCoordinates.get(i).toString();
            String currentBox[] =m_boxCoordinates.get(i).toString().split("\t");
            //box.getXMin()+"\t"+box.getXMax()+"\t"+box.getYMin()+"\t"+box.getYMax() 1 2 3 4
            //IJ.log(" "+currentBox[1] +" "+currentBox[2]+" "+currentBox[3]+" "+currentBox[4]+ "\n");

            int withBox=Math.abs(Integer.parseInt(currentBox[1])-Integer.parseInt(currentBox[2]));
            int heigthBox=Math.abs(Integer.parseInt(currentBox[3])-Integer.parseInt(currentBox[4]));
            IJ.run("Line Width...", "line=4");
            IJ.run("Colors...", "foreground=black background=black selection=black");
            m_zProjection.setRoi(Integer.parseInt(currentBox[1])-20,Integer.parseInt(currentBox[3])-20,withBox+40,heigthBox+40);
            IJ.run(m_zProjection, "Draw", "stack");

        }
        //m_zProjection.show();
        String outFileZbox = m_imageFilePath.replaceAll(".tif","_Zprojection.tif");
        outFileZbox=outFileZbox.replaceAll(".TIF","_Zprojection.TIF");
        IJ.log("out folder "+outFileZbox);
        saveFile(m_zProjection,outFileZbox);


    }
    /**
     * Save the image file
     *
     * @param imagePlusInput image to save
     * @param pathFile path to save the image
     */
    public void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        fileSaver.saveAsTiff(pathFile);
    }



    /*
    ZProjector test = new ZProjector(img);

		test.setMethod(0);
		test.doProjection();
    ImagePlus projImp = test.getProjection();
		IJ.run(projImp, "Enhance Contrast...", "saturated=0.3");
		IJ.run(projImp, "Invert LUT", "");

		projImp.show();
		projImp.setRoi();
    */
}
