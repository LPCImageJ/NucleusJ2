package gred.nucleus.imageProcess;

import ij.ImagePlus;
import ij.process.AutoThresholder;
import ij.process.ImageStatistics;
import ij.process.StackStatistics;

public class Thresholding {

    public Thresholding() {}


    public int computeOtsuThreshold (ImagePlus imagePlusInput) {
        AutoThresholder autoThresholder = new AutoThresholder();
        ImageStatistics imageStatistics = new StackStatistics(imagePlusInput);
        int [] tHisto = imageStatistics.histogram;
        return autoThresholder.getThreshold(AutoThresholder.Method.Otsu,tHisto);
    }
}

