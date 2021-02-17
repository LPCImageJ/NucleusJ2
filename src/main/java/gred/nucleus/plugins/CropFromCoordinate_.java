package gred.nucleus.plugins;

import gred.nucleus.autocrop.CropFromCoordonnate;
import gred.nucleus.dialogs.CropFromCoodinateDialog;
import ij.IJ;
import ij.plugin.PlugIn;
import loci.formats.FormatException;

import java.io.IOException;

public class CropFromCoordinate_ implements PlugIn {
	
	public static void cropFromCoordinates(String coordonnateDir) throws IOException, FormatException, Exception {
		
		CropFromCoordonnate test = new CropFromCoordonnate(coordonnateDir);
		test.runCropFromCoordonnate();
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
