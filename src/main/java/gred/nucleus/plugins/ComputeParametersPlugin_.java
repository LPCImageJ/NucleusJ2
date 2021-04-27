package gred.nucleus.plugins;

import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.dialogs.ComputeParametersDialog;
import ij.measure.Calibration;
import ij.plugin.PlugIn;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;


public class ComputeParametersPlugin_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	/** Run computing parameters method. */
	public void run(String arg) {
		
		ComputeParametersDialog computeParametersDialog = new ComputeParametersDialog();
		
		while (computeParametersDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("Interrupted exception.", e);
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
			LOGGER.error("An error occurred.", e);
		}
	}
	
}