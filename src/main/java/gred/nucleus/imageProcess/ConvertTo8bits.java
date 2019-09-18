package gred.nucleus.imageProcess;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.StackConverter;

public class ConvertTo8bits {

    public ConvertTo8bits(){}
    /**
     * 16bits image preprocessing
     * normalised the histohram distribution
     * apply a gaussian filter to smooth the signal
     * convert the image in 8bits
     * @param img 16bits ImagePlus
     */

    public ImagePlus preProcessImage(ImagePlus img){
        if (img.getType() == ImagePlus.GRAY16) {
            ContrastEnhancer enh = new ContrastEnhancer();
            enh.setNormalize(true);
            enh.setUseStackHistogram(true);
            enh.setProcessStack(true);
            enh.stretchHistogram(img.getProcessor(), 0.05);
            GaussianBlur3D.blur(img, 0.5, 0.5, 1);
            StackConverter stackConverter = new StackConverter(img);
            stackConverter.convertToGray8();
        }
        return img;
    }
}
