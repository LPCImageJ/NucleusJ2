package gred.nucleus.mainsNucelusJ;

import gred.nucleus.core.ConvexHullSegmentation;
import gred.nucleus.core.NucleusSegmentation;
import ij.IJ;
import ij.ImagePlus;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.StackConverter;
import loci.formats.FormatException;
import loci.plugins.BF;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class SegmentationMethods {

    private ImagePlus _imgInput = new ImagePlus();
    private short _vMin = 0;
    private short _vMax = 0;
    private String _output = "";
    private String _inputDir = "";


    /**
     *
     * @param img
     * @param vMin
     * @param vMax
     * @param outputImg
     */
    public SegmentationMethods(ImagePlus img, short vMin, short vMax, String outputImg) {
        this._vMin = vMin;
        this._vMax = vMax;
        this._imgInput = img;
        this._output = outputImg + File.separator + "Segmented" + this._imgInput.getTitle();
    }

    /**
     *
     * @param inputDir
     * @param outputDir
     * @param vMin
     * @param vMax
     */
    public SegmentationMethods(String inputDir, String outputDir, short vMin, short vMax) {
        this._vMin = vMin;
        this._vMax = vMax;
        this._inputDir = inputDir;
        this._output = outputDir;
        File file = new File(this._output);
        if (file.exists()==false){file.mkdir();}
    }

    /**
     *
     */
    public void runOneImage(boolean giftWrapping) {
        ImagePlus imgSeg= this._imgInput;
        NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
        nucleusSegmentation.setVolumeRange(this._vMin, this._vMax);

        Calibration cal = imgSeg.getCalibration();
        System.out.println(imgSeg.getTitle()+"\t"+cal.pixelWidth+"\t"+cal.pixelHeight+"\t"+cal.pixelDepth);
        if(imgSeg.getType() == ImagePlus.GRAY16)
            this.preProcessImage(imgSeg);


        imgSeg = nucleusSegmentation.applySegmentation(imgSeg);
        if(nucleusSegmentation.getBestThreshold() == -1)
            System.out.println("Segmentation error: \nNo object is detected between "+this._vMin + "and"+this._vMax);
        else{
            System.out.println("otsu modif threshold: "+nucleusSegmentation.getBestThreshold()+"\n");
            if (giftWrapping){
                ConvexHullSegmentation nuc = new ConvexHullSegmentation();
                ImagePlus imgGift = nuc.run(imgSeg);
                imgSeg = imgGift;
                //imgGift.setTitle("test ConvexHullPlugin_");
                //imgGift.show();
            }
            imgSeg.setTitle(this._output);
            saveFile(imgSeg, this._output);
            NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(this._imgInput,imgSeg);
            System.out.println(nucleusAnalysis.nucleusParameter3D());
        }
    }


    /**
     *
     * @return
     * @throws IOException
     */
    public String runSeveralImages(boolean giftWrapping) throws IOException, FormatException {
        String log = "";
        String resu = "";
        File [] fileList = fillList(this._inputDir);
        for(int i = 0; i < fileList.length; ++i) {
            String fileImg = fileList[i].toString();
            if (fileImg.contains(".tif")) {
                ImagePlus[] imgTab = BF.openImagePlus(fileImg);
                ImagePlus img  = imgTab[0];
                //img.setCalibration(this._cal);
                ImagePlus imgSeg = img;
                if(imgSeg.getType() == ImagePlus.GRAY16)
                    this.preProcessImage(imgSeg);
                NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
                nucleusSegmentation.setVolumeRange(this._vMin, this._vMax);
                imgSeg  = nucleusSegmentation.applySegmentation(imgSeg);
                if(nucleusSegmentation.getBestThreshold() == -1)
                    log = log+fileImg+"\n";
                else{
                    System.out.println(fileImg+"\totsu modif threshold "+nucleusSegmentation.getBestThreshold()+"\n");
                    if (giftWrapping){
                        ConvexHullSegmentation nuc = new ConvexHullSegmentation();
                        ImagePlus imgGift = nuc.run(imgSeg);
                        imgSeg = imgGift;
                    }
                    String pathSeg = this._output + File.separator+ img.getTitle();
                    imgSeg.setTitle(this._output);
                    saveFile(imgSeg, pathSeg);
                    NucleusAnalysis nucleusAnalysis = new NucleusAnalysis(img,imgSeg);
                    nucleusAnalysis.setResu(resu);
                    resu = nucleusAnalysis.nucleusParameter3D();
                }
            }
        }
        BufferedWriter writer;
        writer = new BufferedWriter(new FileWriter(new File(this._output+File.separator+"ParametersResults.txt")));
        writer.write(resu);
        writer.close();
        return log;
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

    /**
     *
     * @param dir
     * @return
     * @throws IOException
     */
    private File[] fillList(String dir) throws IOException {
        File folder = new File(dir);
        File[] listOfFiles = folder.listFiles();
        return listOfFiles;
    }

    /**
     *
     * @param img
     */
    private void preProcessImage(ImagePlus img){
        ContrastEnhancer enh = new ContrastEnhancer();
        enh.setNormalize(true);
        enh.setUseStackHistogram(true);
        enh.setProcessStack(true);
        enh.stretchHistogram(img.getProcessor(), 0.05);
        GaussianBlur3D.blur(img, 0.5,0.5,1);
        StackConverter stackConverter = new StackConverter( img );
        stackConverter.convertToGray8();
    }

}