package gred.nucleus.plugins;

import gred.nucleus.autocrop.CropFromCoordinates;
import gred.nucleus.dialogs.CropFromCoodinateDialog;
import ij.IJ;
import ij.plugin.PlugIn;
import loci.formats.FormatException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandles;


public class CropFromCoordinates_ implements PlugIn {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	
	public static void cropFromCoordinates(String coordinateDir) throws IOException, FormatException {
		
		//CropFromCoordinates test = new CropFromCoordinates(coordinateDir);
		//test.runCropFromCoordinate();
	}
	
	
	@Override
	public void run(String arg) {
		
		if (IJ.versionLessThan("1.32c")) {
			return;
		}
		CropFromCoodinateDialog cropFromCoodinateDialog = new CropFromCoodinateDialog();
		while (cropFromCoodinateDialog.isShowing()) {
			try {
				Thread.sleep(1);
			} catch (InterruptedException e) {
				LOGGER.error("An error occurred.", e);
			}
		}
		
		if (cropFromCoodinateDialog.isStart()) {
			String file = cropFromCoodinateDialog.getLink();
			if (file == null || file.equals("")) {
				IJ.error("Input file or directory is missing");
			} else {
				try {
					LOGGER.info("Begin Autocrop from coordinate process ");
					
					cropFromCoordinates(file);
					
					LOGGER.info("Autocrop from coordinate process has ended successfully");
				} catch (Exception e) {
					LOGGER.info("Autocrop from coordinate process has failed");
					LOGGER.error("An error occurred.", e);
				}
			}
		}
	}
	
}
