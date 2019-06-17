package gred.nucleus.plugins;

import gred.nucleus.FilesInputOutput.Directory;

import java.text.SimpleDateFormat;
import java.util.Calendar;

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

    /** Constructor with default parameter
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     *
     */
    public PluginParameters(String inputFolder,String outputFolder){
        this.m_inputFolder=inputFolder;
        Directory dirOutput =new Directory(outputFolder);
        dirOutput.CheckAndCreateDir();
        this.m_outputFolder=dirOutput.get_dirPath();

    }
    /** Constructor with specific calibration in x y and z
     *
     * @param inputFolder : path folder containing Images
     * @param outputFolder : path folder output analyse
     * @param xCal x calibration voxel
     * @param yCal : y calibration voxel
     * @param zCal : z calibration voxel
     *
     */
    public PluginParameters(String inputFolder,String outputFolder,double xCal ,double yCal,double zCal){
        this.m_inputFolder=inputFolder;
        Directory dirOutput =new Directory(outputFolder);
        dirOutput.CheckAndCreateDir();
        this.m_outputFolder=dirOutput.get_dirPath();
        this.m_manualParameter=true;
        this.m_xCal=xCal;
        this.m_yCal=xCal;
        this.m_zCal=xCal;

    }

    /**
     * Getter : input path
     * @return input path folder
     */
    public String getInputFolder(){
        return this.m_inputFolder;
    }
    /**
     * Getter : output path
     * @return output path folder
     */
    public String getOutputFolder(){
        return this.m_outputFolder;
    }
    /**
     * Getter : HEADER parameter of the analyse containing
     * path input output folder and x y z calibration
     * on parameter per line
     * @return output path folder
     */
    public String getAnalyseParameters(){
        this.m_headerInfo="#Header \n"
                +"#Star time analyse: "+getLocalTime()+"\n"
                +"#Input folder: "+this.m_inputFolder+"\n"
                +"#Output folder: "+this.m_inputFolder+"\n"
                +"#Calibration:"+getInfoCalibration()+"\n";

        return this.m_headerInfo;

    }
    /**
     * Getter : image x y z calibration
     * @return output path folder
     */
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

    /**
     * get local time start analyse information yyyy-MM-dd:HH-mm-ss format
     * @return time in yyyy-MM-dd:HH-mm-ss format
     */
    public String getLocalTime() {
        String timeStamp = new SimpleDateFormat("yyyy-MM-dd:HH-mm-ss").format(Calendar.getInstance().getTime());
        return timeStamp;
    }
}
