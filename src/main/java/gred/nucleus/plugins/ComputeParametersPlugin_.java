package gred.nucleus.plugins;

import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.dialogs.ComputeParametersDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ComputeParametersPlugin_ implements PlugIn {
	/** Run computing parameters method. */
	public void run(String arg) {
		Logger logger = LoggerFactory.getLogger(this.getClass());
		
		ComputeParametersDialog computeParametersDialog = new ComputeParametersDialog();
		
		while (computeParametersDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				logger.error("Interrupted exception.", e);
				Thread.currentThread().interrupt();
			}
		}
		try {
			if (computeParametersDialog.isStart()) {
				if (computeParametersDialog.getCalibrationStatus()) {
					Calibration calibration = new Calibration();
					calibration.pixelDepth = computeParametersDialog.getZCalibration();
					calibration.pixelWidth = computeParametersDialog.getXCalibration();
					calibration.pixelHeight = computeParametersDialog.getYCalibration();
					calibration.setUnit(computeParametersDialog.getUnit());
					ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
							computeParametersDialog.getRawDataDirectory(),
							computeParametersDialog.getWorkDirectory(),
							calibration);
					generateParameters.run();
				} else {
					ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
							computeParametersDialog.getRawDataDirectory(),
							computeParametersDialog.getWorkDirectory());
					generateParameters.run();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}