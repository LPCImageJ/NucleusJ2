package gred.nucleus.plugins;
import gred.nucleus.core.*;
import gred.nucleus.dialogs.NucleusSegmentationDialog;
import gred.nucleus.utils.ConvexeHullDetection;
import ij.*;
import ij.io.FileSaver;
import ij.measure.Calibration;

import ij.plugin.*;

import java.io.File;

public class ConvexHullPlugin_  implements PlugIn {
	/**
	 * @param
	 *
	 */


	ImagePlus _imagePlusInput;

	/**
	 * This method permit to execute the ReginalExtremFilter on the selected image
	 *
	 * @param arg
	 */
	public void run(String arg) {
		//IJ.log(""+ getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber()+" \n variable arg : -   -----"+ arg + "------\n");


		_imagePlusInput = WindowManager.getCurrentImage();
		if(arg != null){
			_imagePlusInput= new ImagePlus(arg);

		}
		if (null == _imagePlusInput) {
			IJ.noImage();
			return;
		}
		NucleusSegmentationDialog nucleusSegmentationDialog = new NucleusSegmentationDialog(_imagePlusInput.getCalibration());
		while (nucleusSegmentationDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (nucleusSegmentationDialog.isStart()) {
			double xCalibration = nucleusSegmentationDialog.getXCalibration();
			double yCalibration = nucleusSegmentationDialog.getYCalibration();
			double zCalibration = nucleusSegmentationDialog.getZCalibration();
			String unit = nucleusSegmentationDialog.getUnit();
			double volumeMin = nucleusSegmentationDialog.getMinVolume();
			double volumeMax = nucleusSegmentationDialog.getMaxVolume();
			Calibration calibration = new Calibration();
			calibration.pixelDepth = zCalibration;
			calibration.pixelWidth = xCalibration;
			calibration.pixelHeight = yCalibration;
			calibration.setUnit(unit);
			_imagePlusInput.setCalibration(calibration);
			ImagePlus imagePlusSegmented = _imagePlusInput;
			NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
			nucleusSegmentation.setVolumeRange(volumeMin, volumeMax);
			imagePlusSegmented = nucleusSegmentation.applySegmentation(imagePlusSegmented);
			imagePlusSegmented.setTitle("Seg");
			imagePlusSegmented.show();
            saveFile(imagePlusSegmented,"/home/tridubos/Bureau/IMAGES/out_test/burp.tiff");

            ConvexHullSegmentation nuc = new ConvexHullSegmentation();
			ImagePlus plopi = nuc.run(imagePlusSegmented);
			plopi.setTitle("test ConvexHullPlugin_");
			plopi.show();
			NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
			nucleusAnalysis.nucleusParameter3D(_imagePlusInput, plopi);
			nucleusAnalysis.nucleusParameter3D(_imagePlusInput, imagePlusSegmented);

		}
	}


	public void runCommand(String arg) {
		if(arg != null){
			_imagePlusInput= new ImagePlus(arg);

		}
		if (null == _imagePlusInput) {
			IJ.noImage();
			return;
		}
		Calibration	cal =_imagePlusInput.getCalibration();
		double xCalibration = cal.pixelWidth;
		double yCalibration = cal.pixelHeight;
		double zCalibration = cal.pixelDepth;
		String unit = cal.getUnit();
		double volumeMin = 1;
		double volumeMax = 1000;
		Calibration calibration = new Calibration();
		calibration.pixelDepth = zCalibration;
		calibration.pixelWidth = xCalibration;
		calibration.pixelHeight = yCalibration;
		calibration.setUnit(unit);
		_imagePlusInput.setCalibration(calibration);
		ImagePlus imagePlusSegmented = _imagePlusInput;
		NucleusSegmentation nucleusSegmentation = new NucleusSegmentation();
		nucleusSegmentation.setVolumeRange(volumeMin, volumeMax);
		imagePlusSegmented = nucleusSegmentation.applySegmentation(imagePlusSegmented);
		imagePlusSegmented.setTitle(_imagePlusInput.getTitle()+"_seg1");
		saveFile(imagePlusSegmented,"/home/tridubos/Bureau/ImageTEST/MANIP_KAKU/NEW_SEGMENTATION/OTSU_modif/");
		ConvexHullSegmentation nuc = new ConvexHullSegmentation();
		ImagePlus plopi = nuc.run(imagePlusSegmented);
		plopi.setTitle(_imagePlusInput.getTitle()+"_seg2");
		saveFile(plopi,"/home/tridubos/Bureau/ImageTEST/MANIP_KAKU/NEW_SEGMENTATION/GIFT/");
		NucleusAnalysis nucleusAnalysis = new NucleusAnalysis();
		nucleusAnalysis.nucleusParameter3D(_imagePlusInput, plopi);
		nucleusAnalysis.nucleusParameter3D(_imagePlusInput, imagePlusSegmented);
	}



    public void saveFile ( ImagePlus imagePlus, String pathFile)
    {
        FileSaver fileSaver = new FileSaver(imagePlus);
        File file = new File(pathFile);
        if (file.exists())
            fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
        else
        {
            file.mkdir();
            fileSaver.saveAsTiffStack( pathFile+File.separator+imagePlus.getTitle());
        }
    }
}
