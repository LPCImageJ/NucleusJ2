package gred.nucleus.plugins;
import java.io.File;
import gred.nucleus.dialogs.NucleusSegmentationBatchDialog;
import gred.nucleus.segmentation.SegmentationCalling;
import gred.nucleus.utils.FileList;
import ij.IJ;
import ij.ImagePlus;
import ij.plugin.PlugIn;
/**
 * 
 * Method to segment the nucleus on batch
 *  
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NucleusSegmentationBatchPlugin_ implements PlugIn {

	private NucleusSegmentationBatchDialog _nucleusSegmentationBatchDialog = new NucleusSegmentationBatchDialog();
	
	/**
	 * 
	 */
	public void run(String arg) {
		while( _nucleusSegmentationBatchDialog.isShowing()) {
			try {Thread.sleep(1);}
			catch (InterruptedException e) {e.printStackTrace();}
		}	
		if (_nucleusSegmentationBatchDialog.isStart()) {
			FileList fileList = new FileList ();
			File[] tRawImageFile = fileList.run(_nucleusSegmentationBatchDialog.getRawDataDirectory());
			if (tRawImageFile.length==0)
				IJ.showMessage("There are no image in "+_nucleusSegmentationBatchDialog.getRawDataDirectory());
			else {
				if(IJ.openImage(tRawImageFile[0].toString()).getType() == ImagePlus.GRAY32 ) {
			    	IJ.error("image format", "No images in gray scale 8bits or 16 bits in 3D");
			        return;
			    }
				SegmentationCalling otsuModif = new SegmentationCalling(_nucleusSegmentationBatchDialog.getRawDataDirectory(), _nucleusSegmentationBatchDialog.getWorkDirectory(),
						(short)_nucleusSegmentationBatchDialog.getMinVolume(), (short)_nucleusSegmentationBatchDialog.getMaxVolume());
                otsuModif.runOneImage(true);
			}
		}
	}

    /**
     *
     * @return
     */
	public int getNbCpu() {
	    return _nucleusSegmentationBatchDialog.getNbCpu();
	}

    /**
     *
     * @return
     */
	public double getZCalibration() {
	    return _nucleusSegmentationBatchDialog.getZCalibration();
	}

    /**
     *
     * @return
     */
	public double getXCalibration() {
	    return _nucleusSegmentationBatchDialog.getXCalibration();
	}

    /**
     *
     * @return
     */
	public double getYCalibration() {
	    return _nucleusSegmentationBatchDialog.getYCalibration();
	}

    /**
     *
     * @return
     */
	public String getUnit() {return _nucleusSegmentationBatchDialog.getUnit();}

    /**
     *
     * @return
     */
	public double getMinVolume() {
	    return _nucleusSegmentationBatchDialog.getMinVolume();
	}

    /**
     *
     * @return
     */
	public double getMaxVolume() {
	    return _nucleusSegmentationBatchDialog.getMaxVolume();
	}

    /**
     *
     * @return
     */
	public String getWorkDirectory() {
		return _nucleusSegmentationBatchDialog.getWorkDirectory();
	}
}