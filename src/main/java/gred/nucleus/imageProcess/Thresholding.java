package gred.nucleus.imageProcess;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackConverter;
import ij.process.StackStatistics;

public class Thresholding {


    public Thresholding() {}

    /**
     * Compute the initial threshold value from OTSU method
     *
     * @param imagePlusInput raw image
     * @return OTSU threshold
     * TODO STRUCTURES PROBABLY NEEDED
     */
    public int computeOtsuThreshold (ImagePlus imagePlusInput) {
        AutoThresholder autoThresholder = new AutoThresholder();
        ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
        int [] tHisto = imageStatistics.histogram;
        return autoThresholder.getThreshold(AutoThresholder.Method.Otsu,tHisto);
    }

    public ImagePlus contrastAnd8bits(ImagePlus imagePlusInput){
        ContrastEnhancer enh = new ContrastEnhancer();
        enh.setNormalize(true);
        enh.setUseStackHistogram(true);
        enh.setProcessStack(true);
        enh.stretchHistogram(imagePlusInput.getProcessor(), 0.05);
        StackConverter stackConverter = new StackConverter(imagePlusInput );
        stackConverter.convertToGray8();
        return imagePlusInput;

    }

}

