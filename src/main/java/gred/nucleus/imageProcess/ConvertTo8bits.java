package gred.nucleus.imageProcess;

import ij.ImagePlus;
import ij.plugin.ContrastEnhancer;
import ij.plugin.GaussianBlur3D;
import ij.process.StackConverter;

public class ConvertTo8bits {

    private ImagePlus preProcessImage(ImagePlus img){
        ContrastEnhancer enh = new ContrastEnhancer();
        enh.setNormalize(true);
        enh.setUseStackHistogram(true);
        enh.setProcessStack(true);
        enh.stretchHistogram(img.getProcessor(), 0.05);
        GaussianBlur3D.blur(img, 0.5,0.5,1);
        StackConverter stackConverter = new StackConverter( img );
        stackConverter.convertToGray8();
        return img;
    }
}
