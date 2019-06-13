package gred.nucleus.autocrop;

import gred.nucleus.plugins.PluginParameters;

public class AutocropParameters extends PluginParameters {

    /** Number of pixels take plus object size in x */
    private int m_xCropBoxSize=40;
    /** Number of pixels take plus object size in y*/
    private int m_yCropBoxSize=40;
    /** Number of slice take plus object in y */
    private int m_zCropBoxSize=20;

    /** Number of slice take plus object in y */
    private int m_slicesOTSUcomputing=20;

    /** Default constructor taking all parameters*/

    public AutocropParameters(String inputFolder,String outputFolder,int xCropBoxSize,int yCropBoxSize,int zCropBoxSize,int slicesOTSUcomputing){
        super(inputFolder, outputFolder);
        this.m_xCropBoxSize=xCropBoxSize;
        this.m_yCropBoxSize=yCropBoxSize;
        this.m_zCropBoxSize=zCropBoxSize;
        this.m_slicesOTSUcomputing=slicesOTSUcomputing;
    }

    /** Constructor taking manual calibration parameters*/

    public AutocropParameters(String inputFolder,String outputFolder,double xCal ,double yCal,double zCal,
                              int xCropBoxSize,int yCropBoxSize,int zCropBoxSize,int slicesOTSUcomputing){

        super(inputFolder ,outputFolder, xCal , yCal, zCal);
        this.m_xCropBoxSize=xCropBoxSize;
        this.m_yCropBoxSize=yCropBoxSize;
        this.m_zCropBoxSize=zCropBoxSize;
        this.m_slicesOTSUcomputing=slicesOTSUcomputing;
    }

    public void hum (){
        this.m_manualParameter=true;
    }


}
