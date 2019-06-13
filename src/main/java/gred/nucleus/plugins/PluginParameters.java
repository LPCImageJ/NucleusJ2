package gred.nucleus.plugins;

public class PluginParameters {

    /** Activation of manual calibration parameter */
    public boolean m_manualParameter = false;
    /** X calibration plugin parameter */
    public double m_xCal=1;
    /** y calibration plugin parameter */
    public double m_yCal=1;
    /** z calibration plugin parameter */
    public double m_zCal=1;
    /** Input folder */
    public String m_inputFolder;
    /** Output folder */
    public String m_outputFolder;
    /** Autocrop parameters information */
    public String m_headerInfo;


    public PluginParameters(String inputFolder,String outputFolder){
        this.m_inputFolder=inputFolder;
        this.m_outputFolder=outputFolder;

    }
    public PluginParameters(String inputFolder,String outputFolder,double xCal ,double yCal,double zCal){
        this.m_inputFolder=inputFolder;
        this.m_outputFolder=outputFolder;
        this.m_manualParameter=true;
        this.m_xCal=xCal;
        this.m_yCal=xCal;
        this.m_zCal=xCal;

    }
    public String getInputFolder(){
        return this.m_inputFolder;
    }
    public String getOutputFolder(){
        return this.m_outputFolder;
    }
    public String getInfoParameters(){
        this.m_headerInfo="#Header \n"
                +"#Input folder: "+this.m_inputFolder+"\n"
                +"#Output folder: "+this.m_inputFolder+"\n"
                +"#Calibration:"+getInfoCalibration()+"\n"
                        +"";
        return this.m_headerInfo;

    }
    public String getInfoCalibration(){
        String parameters_info;
        if(this.m_manualParameter=true){
            parameters_info="x:"+this.m_xCal+"-y:"+this.m_yCal+"-z:"+this.m_zCal;
        }
        else{
            parameters_info="x:default-y:default-z:default";
        }
        return parameters_info;

    }
}
