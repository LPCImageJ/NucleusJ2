package gred.nucleus.utils;

import gred.nucleus.core.Measure3D;
import gred.nucleus.segmentation.SegmentationParameters;
import ij.ImagePlus;
import ij.ImageStack;
import ij.gui.PolygonRoi;
import ij.gui.Roi;
import ij.measure.Calibration;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;


/**
 * Running gift wrapping for each axis combined
 *
 * @author Tristan Dubos and Axel Poulet
 */
public class ConvexHullImageMaker {
	
	/**
	 *
	 */
	private final VoxelRecord p0 = new VoxelRecord();
	/**
	 *
	 */
	ArrayList<Double> listLabel;
	/**
	 *
	 */
	private String                 axesName = "";
	/**
	 *
	 */
	private Calibration            calibration;
	private SegmentationParameters segmentationParameters;
	
	/* Constructor
	 * @see VoxelRecord
	 * @see ConvexHullImageMaker#setAxes(String) calibration Calibration of the current image analysed
	 * @param listLabel list of voxels of the connected component of the current stack analysed (initialised by giveTable method)
	 */
	
	
	/**
	 * running gift wrapping on the image input
	 *
	 * @return segmented image in axes concerned corrected by gift wrapping
	 *
	 * @see gred.nucleus.core.ConvexHullSegmentation
	 */
	public ImagePlus giftWrapping(ImagePlus imagePlusBinary, SegmentationParameters segmentationParameters) {
		this.segmentationParameters = segmentationParameters;
		calibration = imagePlusBinary.getCalibration();
		ImageStack imageStackInput = imagePlusBinary.getStack();
		Measure3D measure3d = new Measure3D(this.segmentationParameters.getXCal(),
		                                    this.segmentationParameters.getYCal(),
		                                    this.segmentationParameters.getZCal());
		
		// Calcul du rayon : PQ 1/2 du rayon
		double      equivalentSphericalRadius = (measure3d.equivalentSphericalRadius(imagePlusBinary) / 2);
		VoxelRecord tVoxelRecord              = measure3d.computeBarycenter3D(false, imagePlusBinary, 255.0);
		ImagePlus   imagePlusCorrected        = new ImagePlus();
		ImagePlus   imagePlusBlack            = new ImagePlus();
		int         indice;
		int         width;
		int         height;
		if (axesName.equals("xy")) {
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getHeight();
			indice = imagePlusBinary.getNSlices();
		} else if (axesName.equals("xz")) {
			width = imagePlusBinary.getWidth();
			height = imagePlusBinary.getNSlices();
			indice = imagePlusBinary.getHeight();
		} else {
			width = imagePlusBinary.getHeight();
			height = imagePlusBinary.getNSlices();
			indice = imagePlusBinary.getWidth();
		}
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		imagePlusBlack.setImage(bufferedImage);
		ImageStack imageStackOutput = new ImageStack(width, height);
		for (int k = 0; k < indice; ++k) {
			ImagePlus  ip    = imagePlusBlack.duplicate();
			double[][] image = giveTable(imagePlusBinary, width, height, k);
			if (listLabel.size() == 1) {
				ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoundary(image, listLabel.get(0), k);
				if (lVoxelBoundary.size() > 5) {
					ip = imageMaker(image, lVoxelBoundary, width, height, equivalentSphericalRadius);
					
				} else {
					ip = imagePlusBlack.duplicate();
					
				}
			} else if (listLabel.size() > 1) {
				ImageStack imageStackIp = ip.getImageStack();
				for (Double aDouble : listLabel) {
					ArrayList<VoxelRecord> lVoxelBoundary = detectVoxelBoundary(image, aDouble, k);
					if (lVoxelBoundary.size() > 5) {
						//TODO THINKING ON AN OTHER WAY TO DEFINE equivalentSphericalRadius PARAMETER
						ImageStack imageTempStack =
								imageMaker(image, lVoxelBoundary, width, height, equivalentSphericalRadius).getStack();
						for (int l = 0; l < width; ++l) {
							for (int m = 0; m < height; ++m) {
								if (imageTempStack.getVoxel(l, m, 0) > 0) {
									imageStackIp.setVoxel(l, m, 0, 255);
								}
							}
						}
					}
				}
			} else {
				ip = imagePlusBlack.duplicate();
			}
			imageStackOutput.addSlice(ip.getProcessor());
		}
		imagePlusCorrected.setStack(imageStackOutput);
		return imagePlusCorrected;
	}
	
	
	/**
	 * Determine l'ensemble des pixels "frontieres" et le plus extreme
	 *
	 * @param image
	 * @param label
	 * @param indice
	 *
	 * @return
	 */
	ArrayList<VoxelRecord> detectVoxelBoundary(double[][] image, double label, int indice) {
		ArrayList<VoxelRecord> lVoxelBoundary = new ArrayList<>();
		p0.setLocation(0, 0, 0);
		//parcours de l'ensemble des pixel de l'image 2D
		for (int i = 1; i < image.length; ++i) {
			for (int j = 1; j < image[i].length; ++j) {
				if (image[i][j] == label) {
					if (image[i - 1][j] == 0 || image[i + 1][j] == 0 || image[i][j - 1] == 0 || image[i][j + 1] == 0) {
						VoxelRecord voxelTest = new VoxelRecord();
						if (axesName.equals("xy")) {
							voxelTest.setLocation(i, j, indice);
						} else if (axesName.equals("xz")) {
							voxelTest.setLocation(i, indice, j);
						} else {
							voxelTest.setLocation(indice, i, j);
						}
						lVoxelBoundary.add(voxelTest);
						if (axesName.equals("xy")) {
							if (j > p0.j) {
								p0.setLocation(i, j, indice);
							} else if (j == p0.j) {
								if (i > p0.i) {
									p0.setLocation(i, j, indice);
								}
							}
						} else if (axesName.equals("xz")) {
							if (j > p0.k) {
								p0.setLocation(i, indice, j);
							} else if (j == p0.k) {
								if (i > p0.i) {
									p0.setLocation(i, indice, j);
								}
							}
						} else {
							if (j > p0.k) {
								p0.setLocation(indice, i, j);
							} else if (j == p0.k) {
								if (i > p0.j) {
									p0.setLocation(indice, i, j);
								}
							}
						}
					}
				}
			}
		}
		return lVoxelBoundary;
	}
	
	
	/**
	 * @param lVoxelBoundary
	 * @param width
	 * @param height
	 * @param equivalentSphericalRadius
	 *
	 * @return
	 */
	public ImagePlus imageMaker(double[][] image,
	                            ArrayList<VoxelRecord> lVoxelBoundary,
	                            int width,
	                            int height,
	                            double equivalentSphericalRadius) {
		ArrayList<VoxelRecord> convexHull = new ArrayList<>();
		convexHull.add(p0);
		VoxelRecord vectorTest = new VoxelRecord();
		if (axesName.equals("xy") || axesName.equals("xz")) {
			vectorTest.setLocation(-10, 0, 0);
		} else if (axesName.equals("yz")) {
			vectorTest.setLocation(0, -10, 0);
		}
		
		ConvexHullDetection convexHullDetection = new ConvexHullDetection();
		convexHullDetection.setInitialVoxel(p0);
		convexHullDetection.setAxes(axesName);
		convexHull = convexHullDetection.findConvexeHull(image,
		                                                 convexHull,
		                                                 lVoxelBoundary,
		                                                 vectorTest,
		                                                 calibration,
		                                                 equivalentSphericalRadius);
		return makePolygon(convexHull, width, height);
	}
	
	
	/**
	 * @param convexHull
	 * @param width
	 * @param height
	 *
	 * @return
	 */
	public ImagePlus makePolygon(List<VoxelRecord> convexHull, int width, int height) {
		ImagePlus     ip            = new ImagePlus();
		BufferedImage bufferedImage = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		int[]         tableWidth    = new int[convexHull.size() + 1];
		int[]         tableHeight   = new int[convexHull.size() + 1];
		for (int i = 0; i < convexHull.size(); ++i) {
			switch (axesName) {
				case "xy":
					tableWidth[i] = (int) convexHull.get(i).i;
					tableHeight[i] = (int) convexHull.get(i).j;
					break;
				case "xz":
					tableWidth[i] = (int) convexHull.get(i).i;
					tableHeight[i] = (int) convexHull.get(i).k;
					break;
				case "yz":
					tableWidth[i] = (int) convexHull.get(i).j;
					tableHeight[i] = (int) convexHull.get(i).k;
					break;
			}
		}
		
		switch (axesName) {
			case "xy":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).i;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).j;
				break;
			case "xz":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).i;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).k;
				break;
			case "yz":
				tableWidth[convexHull.size()] = (int) convexHull.get(0).j;
				tableHeight[convexHull.size()] = (int) convexHull.get(0).k;
				break;
		}
		/*
		Polygon p = new Polygon(tableWidth, tableHeight,tableWidth.length );
		Graphics2D g2d = bufferedImage.createGraphics();
		g2d.drawPolygon(p);
		g2d.fillPolygon(p);
		g2d.setColor(Color.WHITE);
		g2d.dispose();
		*/
		
		ip.setImage(bufferedImage);
		ip.getProcessor().setValue(255);
		ip.getProcessor().fill(new PolygonRoi(tableWidth, tableHeight, tableWidth.length, Roi.POLYGON));
		return ip;
	}
	
	
	/**
	 * @param imagePlusInput stack
	 * @param width
	 * @param height
	 * @param indice         Number of the stack
	 *
	 * @return
	 */
	double[][] giveTable(ImagePlus imagePlusInput, int width, int height, int indice) {
		ImageStack imageStackInput = imagePlusInput.getStack();
		double[][] image           = new double[width][height];
		for (int i = 0; i < width; ++i) {
			for (int j = 0; j < height; ++j) {
				if (axesName.equals("xy")) {
					image[i][j] = imageStackInput.getVoxel(i, j, indice);
				} else if (axesName.equals("xz")) {
					image[i][j] = imageStackInput.getVoxel(i, indice, j);
				} else {
					image[i][j] = imageStackInput.getVoxel(indice, i, j);
				}
			}
		}
		ConnectedComponents connectedComponents = new ConnectedComponents();
		connectedComponents.setImageTable(image);
		listLabel = connectedComponents.getListLabel(255);
		image = connectedComponents.getImageTable();
		return image;
	}
	
	
	/**
	 * Return current combined axis  analysing
	 *
	 * @return current combined axis  analysing
	 */
	public String getAxes() {
		return axesName;
	}
	
	
	/**
	 * Set the current combined axis  analysing
	 *
	 * @param axes Current combined axis analysing
	 */
	public void setAxes(String axes) {
		axesName = axes;
	}
	
}