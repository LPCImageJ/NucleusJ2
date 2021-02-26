package gred.nucleus.myGradient;

import ij.ImagePlus;
import ij.gui.GenericDialog;
import ij.plugin.PlugIn;
import imagescience.feature.Hessian;
import imagescience.image.Aspects;
import imagescience.image.FloatImage;
import imagescience.image.Image;

import java.awt.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Vector;


public class FJ_Hessian implements PlugIn, WindowListener {
	
	private static boolean largest  = true;
	private static boolean middle   = false;
	private static boolean smallest = true;
	
	private static boolean absolute = true;
	
	private static String    scale = "1.0";
	private static Point     pos   = new Point(-1, -1);
	@SuppressWarnings("unused")
	private        ImagePlus imp   = null;
	
	
	public void run(String arg) {
		
		if (!FJ.libCheck()) return;
		final ImagePlus imp = FJ.imageplus();
		if (imp == null) return;
		
		FJ.log(FJ.name() + " " + FJ.version() + ": Hessian");
		
		GenericDialog gd = new GenericDialog(FJ.name() + ": Hessian");
		gd.addCheckbox(" Largest eigenvalue of Hessian tensor     ", largest);
		gd.addCheckbox(" Middle eigenvalue of Hessian tensor     ", middle);
		gd.addCheckbox(" Smallest eigenvalue of Hessian tensor     ", smallest);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(5, 0, 0, 0));
		gd.addCheckbox(" Absolute eigenvalue comparison     ", absolute);
		gd.addPanel(new Panel(), GridBagConstraints.EAST, new Insets(5, 0, 0, 0));
		gd.addStringField("                Smoothing scale:", scale);
		
		if (pos.x >= 0 && pos.y >= 0) {
			gd.centerDialog(false);
			gd.setLocation(pos);
		} else {
			gd.centerDialog(true);
		}
		gd.addWindowListener(this);
		gd.showDialog();
		
		if (gd.wasCanceled()) return;
		
		largest = gd.getNextBoolean();
		middle = gd.getNextBoolean();
		smallest = gd.getNextBoolean();
		absolute = gd.getNextBoolean();
		scale = gd.getNextString();
		
		(new FJHessian()).run(imp, largest, middle, smallest, absolute, scale);
	}
	
	
	public void windowActivated(final WindowEvent e) {
	}
	
	
	public void windowClosed(final WindowEvent e) {
		
		pos.x = e.getWindow().getX();
		pos.y = e.getWindow().getY();
	}
	
	
	public void windowClosing(final WindowEvent e) {
	}
	
	
	public void windowDeactivated(final WindowEvent e) {
	}
	
	
	public void windowDeiconified(final WindowEvent e) {
	}
	
	
	public void windowIconified(final WindowEvent e) {
	}
	
	
	public void windowOpened(final WindowEvent e) {
	}
	
}

class FJHessian {
	
	void run(
			final ImagePlus imp,
			final boolean largest,
			final boolean middle,
			final boolean smallest,
			final boolean absolute,
			final String scale
	        ) {
		
		try {
			double scaleVal;
			try {
				scaleVal = Double.parseDouble(scale);
			} catch (Exception e) {
				throw new IllegalArgumentException("Invalid smoothing scale value");
			}
			
			final Image   img     = Image.wrap(imp);
			final Aspects aspects = img.aspects();
			if (!FJ_Options.isotropic) img.aspects(new Aspects());
			final Hessian hess = new Hessian();
			hess.messenger.log(FJ_Options.log);
			hess.messenger.status(FJ_Options.pgs);
			hess.progressor.display(FJ_Options.pgs);
			
			final Vector<Image> eigenImages = hess.run(new FloatImage(img), scaleVal, absolute);
			
			final int nImages = eigenImages.size();
			for (Image eigenImage : eigenImages) eigenImage.aspects(aspects);
			if (nImages == 2) {
				if (largest) FJ.show(eigenImages.get(0), imp);
				if (smallest) FJ.show(eigenImages.get(1), imp);
			} else if (nImages == 3) {
				if (largest) FJ.show(eigenImages.get(0), imp);
				if (middle) FJ.show(eigenImages.get(1), imp);
				if (smallest) FJ.show(eigenImages.get(2), imp);
			}
			
			FJ.close(imp);
			
		} catch (OutOfMemoryError e) {
			FJ.error("Not enough memory for this operation");
			
		} catch (IllegalArgumentException | IllegalStateException e) {
			FJ.error(e.getMessage());
			
		} catch (Throwable e) {
			FJ.error("An unidentified error occurred while running the plugin");
			
		}
	}
	
}
