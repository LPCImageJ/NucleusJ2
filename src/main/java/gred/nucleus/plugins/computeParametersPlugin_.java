package gred.nucleus.plugins;
import gred.nucleus.core.ComputeNucleiParameters;
import gred.nucleus.dialogs.computeParamertersDialog;

import ij.measure.Calibration;
import ij.plugin.PlugIn;


public class computeParametersPlugin_ implements PlugIn {
    /**
     * Run computing parameters method.
     *
     */

    public void run(String arg) {
        computeParamertersDialog computeParamertersDialog = new computeParamertersDialog();

        while (computeParamertersDialog.isShowing()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
        try {
            if (computeParamertersDialog.isStart()) {
                if(computeParamertersDialog.getCalibrationStatus()) {
                    Calibration calibration = new Calibration();
                    calibration.pixelDepth = computeParamertersDialog.getZCalibration();
                    calibration.pixelWidth = computeParamertersDialog.getXCalibration();
                    calibration.pixelHeight = computeParamertersDialog.getYCalibration();
                    calibration.setUnit(computeParamertersDialog.getUnit());
                    ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
                            computeParamertersDialog.getRawDataDirectory(),
                            computeParamertersDialog.getWorkDirectory(),
                            calibration);
                    generateParameters.run();
                }
                else{
                    ComputeNucleiParameters generateParameters = new ComputeNucleiParameters(
                            computeParamertersDialog.getRawDataDirectory(),
                            computeParamertersDialog.getWorkDirectory());
                    generateParameters.run();
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}