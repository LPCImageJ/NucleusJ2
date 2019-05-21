package gred.nucleus.autocrop;

import gred.nucleus.FilesInputOutput.Directory;
import gred.nucleus.FilesInputOutput.OutputTiff;
import gred.nucleus.connectedComponent.ConnectedComponent;
import gred.nucleus.exceptions.fileInOut;
import gred.nucleus.imageProcess.Thresholding;
import ij.ImagePlus;
import ij.ImageStack;
import ij.io.FileSaver;
import ij.measure.Calibration;
import ij.plugin.ContrastEnhancer;
import ij.plugin.Duplicator;
import ij.plugin.GaussianBlur3D;
import ij.plugin.ZProjector;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackConverter;
import ij.process.StackStatistics;
import loci.common.Region;
import loci.formats.FormatException;
import loci.plugins.BF;
import loci.plugins.in.ImporterOptions;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class AutoCrop2 {

    /** */
    File m_currentFile;

    private ImagePlus m_rawImg;

    private ImagePlus m_imageSeg;
    /** The path of the image to be processed */
    private String m_imageFilePath;
    /** the path of the directory where are saved the crop of the object */
    private String m_outputDirPath;
    /** The prefix of the names of the output cropped images, which are automatically numbered.*/
    private String m_outputFilesPrefix;
    /** List of the path of the output files created by the cropKernels method*/
    private ArrayList<String> m_outputFile =  new ArrayList <String>();
    /** List of boxes coordinates */
    private ArrayList <String> m_boxCoordinates = new ArrayList<String>();
    /** Number of nuclei cropped */
    private int _nbOfNuc = 0;

    public AutoCrop2(File imageFile, String outputDirPath ,String outputFilesPrefix ) throws IOException, FormatException, fileInOut,Exception{
        this.m_currentFile=imageFile;
        this.m_imageFilePath = imageFile.getAbsolutePath();
        this.m_outputDirPath = outputDirPath;
        this.m_rawImg =  BF.openImagePlus(imageFile.getAbsolutePath())[0];
        Thresholding thresholding = new Thresholding();
        this.m_imageSeg= thresholding.contrastAnd8bits(BF.openImagePlus(imageFile.getAbsolutePath())[0]);
        this.m_outputFilesPrefix = outputFilesPrefix;


    }
    public void thresholdKernels(){
        GaussianBlur3D.blur(this.m_imageSeg, 0.5,0.5,1);
        Thresholding thresholding = new Thresholding();
        int thresh	=thresholding.computeOtsuThreshold(this.m_imageSeg);
        if(thresh <20) {
            ImagePlus imp2 = new Duplicator().run(this.m_imageSeg, 40, this.m_imageSeg.getStackSize());
            int thresh2 = thresholding.computeOtsuThreshold(imp2);
            if (thresh2<20)
                thresh=20;
            else
                thresh=thresh2;
        }
        System.out.println("le threshold de  " +this.m_imageSeg.getTitle()+" est de "+ thresh);
        this.m_imageSeg = this.generateSegmentedImage(this.m_imageSeg, thresh);
        System.out.println("le threshold de  " +this.m_imageSeg.getTitle()+" est de "+ thresh);

    }
    //TODO A ENLEVER ?
    public void thresholdKernelsZprojection() {

        ImagePlus temmGaussian =this.m_imageSeg;
        GaussianBlur3D.blur(temmGaussian, 0.5,0.5,1);

        ZProjector zProjectionTmp = new ZProjector(temmGaussian);
        ImagePlus testConvert= projectionMax(zProjectionTmp);

        testConvert.show();
        Thresholding thresholding = new Thresholding();
        int thresh	=thresholding.computeOtsuThreshold(testConvert);
        testConvert.close();
        System.out.println("Gaussian sans Blur "+ thresh);
        //thresh = computeOtsuThreshold(testConvert);
        System.out.println("Gaussian avec Blur "+ thresh);
        this.m_imageSeg = this.generateSegmentedImage(this.m_imageSeg, thresh);
    }
    private ImagePlus projectionMax(ZProjector project){
        project.setMethod(1);
        project.doProjection();
        return project.getProjection();
    }


    /**
     * Detection of the of the bounding box for each object of the image.
     * A Connected component detection is do on the  m_imageThresholding and all the object on the border and inferior a at the threshold volume
     * are removed. The coordinates allow the implementation of the box objects which define the bounding box, and these objects are stocked in a
     * ArrayList.
     * @pre the input image must be a binary image with values 255 and 0 only.
     * In order to use with a grey-level image, use either @see # thresholdKernels() or
     * your own binarisation method.
     * @param thresholdVolumeVoxel: threshold volume of the objects detected by the connected component
     * @return the ArrayList of the bounding boxes.
     */
    public ArrayList <Box> computeBoxes(double thresholdVolumeVoxel) {
        ConnectedComponent connectedComponent;
        ArrayList <Box> boxes =  new ArrayList <Box>();
        try {
            connectedComponent = ConnectedComponent.getLabelledConnectedComponent(m_imageSeg, 255, true, thresholdVolumeVoxel, true);
            Box box = null;
            //ArrayList initialisation
            this._nbOfNuc = connectedComponent.getNumberOfComponents();
            for(short i = 0; i< connectedComponent.getNumberOfComponents();++i)
                boxes.add(new Box(Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE, Short.MAX_VALUE, Short.MIN_VALUE));
            System.out.println();
            // Check the predicate
            for (short k = 0; k < this.m_imageSeg.getNSlices(); ++k) {
                for (short i = 0; i < this.m_imageSeg.getWidth(); ++i) {
                    for (short j = 0; j < this.m_imageSeg.getHeight(); ++j) {
                        // if label different of the background
                        if (connectedComponent.getLabel(i, j, k) > 0) {
                            box = boxes.get(connectedComponent.getLabel(i, j, k)-1);
                            if (i < box.getXMin())
                                box.setXMin(i);
                            else if (i > box.getXMax())
                                box.setXMax(i);
                            if (j < box.getYMin())
                                box.setYMin(j);
                            else if(j > box.getYMax())
                                box.setYMax(j);

                            if (k < box.getZMin())
                                box.setZMin(k);
                            else if (k > box.getZMax())
                                box.setZMax(k);
                        }
                    }
                }
            }
        }
        catch (Exception e){ e.printStackTrace(); }
        return boxes;
    }

    /**
     * Method crops a box of interest, create and save a new small image. This process allow the crop of all the bounding box
     * contained in the input ArrayList and the crop is did on the ImageCore put in input in this method (crop method available in the imagej wrapper). Then the image results
     * obtained was used to create a new ImageCoreIJ, and the image is saved.
     *
     * @param boxes: containing object boxes.
     */
    public void cropKernels(ArrayList <Box> boxes)throws IOException, FormatException, Exception {
        Directory dirOutput= new Directory(m_outputDirPath+File.separator+m_outputFilesPrefix);
        dirOutput.CheckAndCreateDir();
        for(short i = 0; i < boxes.size(); ++i) {
            Box box = boxes.get(i);
            int xmin = box.getXMin()-40;
            int ymin =  box.getYMin()-40;
            int zmin =  box.getZMin()-20;
            String coord= box.getXMin()+"_"+box.getYMin()+"_"+box.getZMin();
            m_boxCoordinates.add(m_outputDirPath+File.separator+m_outputFilesPrefix+"_"+coord+i+"\t"+box.getXMin()+"\t"+box.getXMax()+"\t"+box.getYMin()+"\t"+box.getYMax()+"\t"+box.getZMin()+"\t"+box.getZMax());
            if (xmin < 0)
                xmin = 1;
            if (ymin < 0)
                ymin = 1;
            if (zmin < 0)
                zmin = 1;

            int width = box.getXMax()+80 - box.getXMin();
            int height = box.getYMax()+80 - box.getYMin();
            int depth = box.getZMax()+40 - box.getZMin();
            if (width+xmin >= m_imageSeg.getWidth())
                width-=(width+xmin)-m_imageSeg.getWidth();

            if (height+ymin >= m_imageSeg.getHeight())
                height-=(height+ymin)-m_imageSeg.getHeight();

            if (depth+zmin >= m_imageSeg.getNSlices())
                depth-=(depth+zmin)-m_imageSeg.getNSlices();

            ImagePlus imgResu = cropImage(xmin, ymin, zmin, width, height, depth);


            Calibration cal = m_rawImg.getCalibration();
            imgResu.setCalibration(cal);
            OutputTiff fileOutput = new OutputTiff(m_outputDirPath+File.separator+m_outputFilesPrefix+File.separator+m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
            fileOutput.SaveImage(imgResu);

            {}
            //TODO FACTORISE SAVE FILE FONCTION IN NEW CLASS

			/*
			System.out.println("Heu la on a le out file name : "+m_outputDirPath+"\n"+
					File.separator+m_outputFilesPrefix+"\n"+
					File.separator+m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
			System.out.println("Heu la on a le out file name : "+this.m_outputDirPath+"\n"+
					File.separator+this.m_outputFilesPrefix+"\n"+
					File.separator+this.m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
			*/
            m_outputFile.add(m_outputDirPath+File.separator+m_outputFilesPrefix+File.separator+m_outputFilesPrefix+"_"+coord+"_"+i+".tif");
        }
    }

    /**
     * Getter for the m_outoutFiel ArrayList
     * @return m_outputFile: ArrayList of String for the path of the output files created.
     */
    public ArrayList <String> getOutputFileArrayList(){
        return m_outputFile;
    }

    /**
     * Getter for the m_boxCoordinates
     * @return m_boxCoordinates: ArrayList of String which contain the coordinates of the boxes
     */
    public ArrayList <String> getFileCoordinates(){
        return m_boxCoordinates;
    }

    /**
     * Compute the initial threshold value
     *
     * @param imagePlusInput raw image
     * @return
     *
     * //TODO FACTORING THRESHOLD FONCTION
     */
    private int computeOtsuThreshold (ImagePlus imagePlusInput) {
        AutoThresholder autoThresholder = new AutoThresholder();
        ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
        int [] tHisto = imageStatistics.histogram;
        return autoThresholder.getThreshold(AutoThresholder.Method.Otsu,tHisto);
    }



    /**
     * Create binary image with the threshold value gave in input
     * @param imagePlusInput ImagePlus raw image to binarize
     * @param threshold integer threshold value
     * @return
     */
    private ImagePlus generateSegmentedImage (ImagePlus imagePlusInput, int threshold) {
        ImageStack imageStackInput = imagePlusInput.getStack();
        ImagePlus imagePlusSegmented = imagePlusInput.duplicate();
        ImageStack imageStackSegmented = imagePlusSegmented.getStack();
        for(int k = 0; k < imagePlusInput.getStackSize(); ++k) {
            for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
                for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
                    double voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue >= threshold) imageStackSegmented.setVoxel(i, j, k, 255);
                    else imageStackSegmented.setVoxel(i, j, k, 0);
                }
            }
        }
        return imagePlusSegmented;
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


    /**
     *
     * Crop of the bounding box on the image. The coordinates are inputs of this methods
     *
     * @param xmin: coordinate x min of the crop
     * @param ymin: coordinate y min of the crop
     * @param zmin: coordinate z min of the crop
     * @param width: coordinate x max of the crop
     * @param height: coordinate y max of the crop
     * @param depth: coordinate z max of the crop
     * @return : ImageCoreIJ of the cropped image.
     */
    public ImagePlus cropImage(int xmin, int ymin, int zmin, int width, int height, int depth)throws IOException, FormatException {
        ImporterOptions options = new ImporterOptions();
        options.setId(this.m_imageFilePath);
        options.setAutoscale(true);
        options.setCrop(true);
        options.setCropRegion(0, new Region(xmin, ymin ,width, height));
        ImagePlus[] imps = BF.openImagePlus(options);
        ImagePlus sort = new ImagePlus();
        sort = new Duplicator().run(imps[0],zmin,zmin+depth);
        return sort;

		/*
		m_rawImg.duplicate();
		ImageStack iStack =  m_rawImg.getStack();
    	ImagePlus imp = new ImagePlus();
		imp.setStack(iStack.crop(xmin, ymin, zmin, width, height, depth));
		ImageConverter ic = new ImageConverter(imp);
		imp.updateAndDraw();
		*/
    }

    /**
     *Getter of the number of nuclei contained in the input image
     *
     * @return int the nb of nuclei
     */
    public int getNbOfNuc(){
        return this._nbOfNuc;
    }


}