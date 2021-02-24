package gred.nucleus.myGradient;

import ij.ImagePlus;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;
import imagescience.utility.Progressor;


/**
 * Modification of plugin featureJ to integrate of this work,
 * <p>
 * => Use to imagescience.jar library
 *
 * @author poulet axel
 */
public class MyGradient {
	
	private static boolean compute  = true;
	private static boolean suppress = false;
	private static String  scale    = "1.0";
	private static String  lower    = "";
	private static String  higher   = "";
	ImagePlus _imagePlus;
	ImagePlus _imagePlusBinary;
	private boolean mask;
	
	public MyGradient(ImagePlus imp, ImagePlus imagePlusBinary) {
		_imagePlus = imp;
		_imagePlusBinary = imagePlusBinary;
		mask = true;
	}
	
	
	public MyGradient(ImagePlus imp) {
		_imagePlus = imp;
		mask = false;
	}
	
	@SuppressWarnings("unused")
	public ImagePlus run() {
		ImagePlus newImagePlus = new ImagePlus();
		try {
			double  scaleVal;
			double  lowVal        = 0;
			double  highVal       = 0;
			boolean lowThreshold  = true;
			boolean highThreshold = true;
			try {
				scaleVal = Double.parseDouble(scale);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid smoothing scale value");
			}
			try {
				if (lower.equals("")) {
					lowThreshold = false;
				} else {
					lowVal = Double.parseDouble(lower);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid lower threshold value");
			}
			try {
				if (higher.equals("")) {
					highThreshold = false;
				} else {
					highVal = Double.parseDouble(higher);
				}
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid higher threshold value");
			}
			final int   threshMode = (lowThreshold ? 10 : 0) + (highThreshold ? 1 : 0);
			final Image image      = Image.wrap(_imagePlus);
			Image       newImage   = new FloatImage(image);
			double[]    pls        = {0, 1};
			int         pl         = 0;
			if ((compute || suppress) && threshMode > 0) {
				pls = new double[]{0, 0.9, 1};
			}
			final Progressor progressor = new Progressor();
			progressor.display(FJ_Options.pgs);
			if (compute || suppress) {
				final Aspects aspects = newImage.aspects();
				if (!FJ_Options.isotropic) newImage.aspects(new Aspects());
				final MyEdges myEdges = new MyEdges();
				if (mask) myEdges.setMask(_imagePlusBinary);
				progressor.range(pls[pl], pls[++pl]);
				myEdges.progressor.parent(progressor);
				myEdges.messenger.log(FJ_Options.log);
				myEdges.messenger.status(FJ_Options.pgs);
				newImage = myEdges.run(newImage, scaleVal, suppress);
				newImage.aspects(aspects);
			}
			newImagePlus = newImage.imageplus();
			_imagePlus.setCalibration(newImagePlus.getCalibration());
			final double[] min_max = newImage.extrema();
			final double   min     = min_max[0];
			final double   max     = min_max[1];
			newImagePlus.setDisplayRange(min, max);
		} catch (OutOfMemoryError e) {
			FJ.error("Not enough memory for this operation");
		} catch (IllegalArgumentException | IllegalStateException e) {
			FJ.error(e.getMessage());
		}
		//catch (Throwable e) {	FJ.error("An unidentified error occurred while running the plugin");	}
		return newImagePlus;
	}
}
