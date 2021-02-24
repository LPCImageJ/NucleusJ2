package gred.nucleus.plugins;

import gred.nucleus.dialogs.NucleusSegmentationAndAnalysisBatchDialog;
import gred.nucleus.segmentation.SegmentationCalling;
import ij.IJ;
import ij.plugin.PlugIn;

/**
 * Method to segment and analyse the nucleus on batch
 *
 * @author Tristant Dubos and Axel Poulet
 */
public class NucleusSegmentationAndAnalysisBatchPlugin_ implements PlugIn {
	NucleusSegmentationAndAnalysisBatchDialog _nucleusPipelineBatchDialog =
			new NucleusSegmentationAndAnalysisBatchDialog();
	
	/** TODO CHANGER LES METHODES APPELER !!!!!!! */
	public void run(String arg) {
		while (_nucleusPipelineBatchDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		if (_nucleusPipelineBatchDialog.isStart()) {
			IJ.log("Beginning of the segmentation of nuclei, the data are in " +
			       _nucleusPipelineBatchDialog.getRawDataDirectory());
			SegmentationCalling otsuModified =
					new SegmentationCalling(_nucleusPipelineBatchDialog.getRawDataDirectory(),
					                        _nucleusPipelineBatchDialog.getWorkDirectory(),
					                        (short) _nucleusPipelineBatchDialog.getMinVolume(),
					                        (short) _nucleusPipelineBatchDialog.getMaxVolume());
			try {
				String log = otsuModified.runSeveralImages2();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			IJ.log("End of the segmentation the nuclei, the results are in " +
			       _nucleusPipelineBatchDialog.getWorkDirectory());
		}
	}
	
	
	/** @return  */
	public int getNbCpu() {
		return _nucleusPipelineBatchDialog.getNbCpu();
	}
	
	
	/** @return  */
	public double getZCalibration() {
		return _nucleusPipelineBatchDialog.getZCalibration();
	}
	
	
	/** @return  */
	public double getXCalibration() {
		return _nucleusPipelineBatchDialog.getXCalibration();
	}
	
	
	/** @return  */
	public double getYCalibration() {
		return _nucleusPipelineBatchDialog.getYCalibration();
	}
	
	
	/** @return  */
	public String getUnit() {
		return _nucleusPipelineBatchDialog.getUnit();
	}
	
	
	/** @return  */
	public double getMinVolume() {
		return _nucleusPipelineBatchDialog.getMinVolume();
	}
	
	
	/** @return  */
	public double getMaxVolume() {
		return _nucleusPipelineBatchDialog.getMaxVolume();
	}
	
	
	/** @return  */
	public String getWorkDirectory() {
		return _nucleusPipelineBatchDialog.getWorkDirectory();
	}
	
	
	/** @return  */
	public boolean is2D3DAnalysis() {
		return _nucleusPipelineBatchDialog.is2D3DAnalysis();
	}
	
	
	/** @return  */
	public boolean is3DAnalysis() {
		return _nucleusPipelineBatchDialog.is3D();
	}
}
