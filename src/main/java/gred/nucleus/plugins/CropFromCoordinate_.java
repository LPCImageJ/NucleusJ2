package gred.nucleus.plugins;

import gred.nucleus.autocrop.CropFromCoordinates;
import gred.nucleus.dialogs.CropFromCoodinateDialog;
import ij.IJ;
import ij.plugin.PlugIn;
import loci.formats.FormatException;

import java.io.IOException;

public class CropFromCoordinate_ implements PlugIn {
	
	public static void cropFromCoordinates(String coordinateDir) throws IOException, FormatException, Exception {
		
		CropFromCoordinates test = new CropFromCoordinates(coordinateDir);
		test.runCropFromCoordinate();
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
				e.printStackTrace();
			}
		}
		
		if (cropFromCoodinateDialog.isStart()) {
			String file = cropFromCoodinateDialog.getLink();
			if (file == null || file.equals("")) {
				IJ.error("Input file or directory is missing");
			} else {
				try {
					IJ.log("Begin Autocrop from coordinate process ");
					
					cropFromCoordinates(file);
					
					IJ.log("\nAutocrop from coordinate process has ended successfully");
				} catch (Exception e) {
					IJ.log("\nAutocrop from coordinate process has failed");
					e.printStackTrace();
				}
			}
		}
	}
	
}
