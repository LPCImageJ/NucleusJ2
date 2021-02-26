package gred.nucleus.plugins;

import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.dialogs.ComputeParametersDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;


public class ComputeParametersPlugin_ implements PlugIn {
	/** Run computing parameters method. */
	public void run(String arg) {
		ComputeParametersDialog _computeParametersDialog = new ComputeParametersDialog();
		
		while (_computeParametersDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			if (_computeParametersDialog.isStart()) {
				if (_computeParametersDialog.getCalibrationStatus()) {
					Calibration calibration = new Calibration();
					calibration.pixelDepth = _computeParametersDialog.getZCalibration();
					calibration.pixelWidth = _computeParametersDialog.getXCalibration();
					calibration.pixelHeight = _computeParametersDialog.getYCalibration();
					calibration.setUnit(_computeParametersDialog.getUnit());
					ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
							_computeParametersDialog.getRawDataDirectory(),
							_computeParametersDialog.getWorkDirectory(),
							calibration);
					generateParameters.run();
				} else {
					ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
							_computeParametersDialog.getRawDataDirectory(),
							_computeParametersDialog.getWorkDirectory());
					generateParameters.run();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}