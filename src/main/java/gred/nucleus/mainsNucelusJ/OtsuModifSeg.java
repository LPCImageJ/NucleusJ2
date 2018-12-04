package gred.nucleus.mainsNucelusJ;

import gred.nucleus.core.NucleusSegmentation;
import ij.ImagePlus;
import ij.io.FileSaver;

import java.io.File;

public class OtsuModifSeg {

    private ImagePlus _imgInput = new ImagePlus();
    private short _vMin = 0;
    private short _vMax = 0;
    private String _output = "";

    public OtsuModifSeg(ImagePlus img, short vMin, short vMax, String outputImg) {
        this._vMin = vMin;
        this._vMax = vMax;
        this._imgInput = img;
        this._output = outputImg + File.separator + "Segmented" + this._imgInput.getTitle();
    }


    public void runOneImage() {
        ImagePlus imagePlusSegmented= this._imgInput;
        NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
        nucleusSegmentation.setVolumeRange(this._vMin, this._vMax);
        imagePlusSegmented = nucleusSegmentation.applySegmentation(imagePlusSegmented);
        if(nucleusSegmentation.getBestThreshold() == 0)
            System.out.println("Segmentation error: \nNo object is detected between "+this._vMin + "and"+this._vMax);
        else{
            imagePlusSegmented.setTitle(this._output);
            saveFile(imagePlusSegmented, this._output);
            NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(this._imgInput,imagePlusSegmented);
            System.out.println(nucleusAnalysis.nucleusParameter3D());
        }
    }

    /**
     *
     * Method which save the image in the directory.
     *
     * @param imagePlusInput Image to be save
     * @param pathFile path of directory
     */
    public void saveFile ( ImagePlus imagePlusInput, String pathFile) {
        FileSaver fileSaver = new FileSaver(imagePlusInput);
        File file = new File(pathFile);
        fileSaver.saveAsTiffStack( pathFile);

    }

}
