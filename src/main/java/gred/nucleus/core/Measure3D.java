package gred.nucleus.core;

import gred.nucleus.utils.Gradient;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.*;
import ij.measure.*;
import java.util.ArrayList;
import java.util.HashMap;
import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class NucleusMeasure : allow the compute of severals 3Dparameters (shape, lenght) in
 * binary object
 *
 * //TODO reercrire cette classe ya des choses que je fais 5 fois c'est inutil
 *
 * @author Tristan Dubos and Axel Poulet
 */

public class Measure3D {

	ImagePlus _image;

	double _xCal;
	double _ycal;
	double _zcal;

	public Measure3D (){ }

	public Measure3D (ImagePlus img, double xCal,double ycal,double zCal){
		this._image=img;
		this._xCal=xCal;
		this._ycal=ycal;
		this._zcal=zCal;

	}

	/**
	 * Scan of image and if the voxel belong to theobject of interest, looking,
	 * if in his neighborhood there are voxel value == 0 then it is a boundary voxel.
	 * Adding the surface of the face of the voxel frontier, which are in contact
	 * with the background of the image, to the surface total.
	 *
	 * @param imagePlusInput segmented image
	 * @param label label of the interest object
	 * @return the surface
	 */

	public double computeSurfaceObject (ImagePlus imagePlusInput, double label){
		Calibration calibration= imagePlusInput.getCalibration();
		ImageStack imageStackInput = imagePlusInput.getStack();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double surfaceArea = 0,voxelValue, neighborVoxelValue;
		for (int k = 1; k < imagePlusInput.getStackSize(); ++k) {
			for (int i = 1; i < imagePlusInput.getWidth(); ++i) {
				for (int j = 1; j < imagePlusInput.getHeight(); ++j) {
					voxelValue = imageStackInput.getVoxel(i, j, k);
					if (voxelValue == label) {
						for (int kk = k - 1; kk <= k + 1; kk += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + xCalibration * yCalibration;
						}
						for (int ii = i - 1; ii <= i + 1; ii += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + yCalibration * zCalibration;
						}
						for (int jj = j - 1; jj <= j + 1; jj += 2) {
							neighborVoxelValue = imageStackInput.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue)
								surfaceArea = surfaceArea + xCalibration * zCalibration;
						}
					}
				}
			}
		}
		return surfaceArea;
	}


	/**
	 * This Method compute the volume of each segmented objects
	 * in imagePlus
	 * 
	 * @param imagePlusInput ImagePlus segmented image
	 * @return double table which contain the volume of each image object
	 */
	public double[] computeVolumeofAllObjects (ImagePlus imagePlusInput){
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		double []tlabel = histogram.getLabels();
		double [] tObjectVolume = new double[tlabel.length];
		HashMap<Double , Integer> hashHisto = histogram.getHistogram();
		for(int i=0; i < tlabel.length; ++i){
	        int nbVoxel = hashHisto.get(tlabel[i]); 
			tObjectVolume[i] = nbVoxel*xCalibration*yCalibration*zCalibration;
	    }
		return tObjectVolume;
	}

	/**
	 * Compute the volume of one object with this label
	 * @param label double label of the object of interest
	 * @return double: the volume of the label of interest
	 */
	public double computeVolumeObject2 ( double label){
		Histogram histogram = new Histogram ();
		histogram.run(this._image);
		HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
		return  hashMapHisto.get(label)*this._xCal*this._ycal*this._zcal;

	}

	public double computeVolumeObject3 ( double label,ImagePlus imagePlusInput){
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
		return  hashMapHisto.get(label)*this._xCal*this._ycal*this._zcal;


	}

	/**
	 * Compute the volume of one object with this label
	 * @param imagePlusInput ImagePLus of the segmented image
	 * @param label double label of the object of interest
	 * @return double: the volume of the label of interest
	 */
	public double computeVolumeObject (ImagePlus imagePlusInput, double label){
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		HashMap<Double , Integer> hashMapHisto = histogram.getHistogram();
		return  hashMapHisto.get(label) *xCalibration*yCalibration*zCalibration;

	}

    /**
     * compute the equivalent spherical radius
     * @param volume double of the volume of the object of interesr
     * @return double the equivalent spherical radius
     */
	public double equivalentSphericalRadius (double volume) {
		double radius =  (3 * volume) / (4 * Math.PI);
		radius = Math.pow(radius, 1.0/3.0);
		return radius;
	}
	
	/**
	 * compute the equivalent spherical radius with ImagePlus in input
     *
     * @param imagePlusBinary ImagePlus of the segmented image
     * @return  double the equivalent spherical radius
     */
	public double equivalentSphericalRadius (ImagePlus imagePlusBinary){
		double radius =  (3 * computeVolumeObject(imagePlusBinary,255)) / (4 * Math.PI);
		radius = Math.pow(radius, 1.0/3.0);
		return radius;
	}


	/**
	 * Method which compute the sphericity :
	 * 36Pi*Volume^2/Surface^3 = 1 if perfect sphere
	 * 
	 * @param volume double volume of the object
	 * @param surface double surface of the object
	 * @return double sphercity
	 */
	public double computeSphericity(double volume, double surface){
		return ((36 * Math.PI * (volume*volume)) / (surface*surface*surface));
	}
  
	/**
	 * Method which compute the eigen value of the matrix (differences between the
	 * coordinates of all points and the barycenter
	 * Obtaining a symmetric matrix :
	 * xx xy xz
	 * xy yy yz
	 * xz yz zz
	 * Compute the eigen value with the pakage JAMA
	 * 
	 * @param imagePlusInput  ImagePlus labelled
	 * @param label double label of interest
	 * @return double table containing the 3 eigen values
	 */

	public double [] computeEigenValue3D (ImagePlus imagePlusInput, double label){
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		VoxelRecord barycenter = computeBarycenter3D (true,imagePlusInput,label);
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		double xx = 0;
		double xy = 0;
		double xz = 0;
		double yy = 0;
		double yz = 0;
		double zz = 0;
		int compteur = 0;
		double voxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k){
			double dz = ((zCalibration * (double) k)-barycenter.getK());
			for (int i = 0; i < imagePlusInput.getWidth(); ++i){
				double dx = ((xCalibration * (double) i)-barycenter.getI());
				for (int j = 0; j < imagePlusInput.getHeight(); ++j){
					voxelValue = imageStackInput.getVoxel(i,j,k);
					if (voxelValue == label){
						double dy = ((yCalibration * (double) j)-barycenter.getJ());
						xx+= dx * dx;
						yy+= dy * dy;
						zz+= dz * dz;
						xy+= dx * dy;
						xz+= dx * dz;
						yz+= dy * dz;
						compteur++;
					}
				}
			}
		}
		double [][] tValues = {{xx / compteur, xy / compteur, xz / compteur},
                      {xy / compteur, yy / compteur, yz / compteur},
                      {xz / compteur, yz / compteur, zz / compteur}};
		Matrix matrix = new Matrix (tValues);
		EigenvalueDecomposition eigenValueDecomposition =  matrix.eig();
		return eigenValueDecomposition.getRealEigenvalues();
	}

  
	/**
     * Compute the flatness and the elongation of the object of interest
	 * @param imagePlusInput ImagePlus of labelled image
	 * @param label double label of interest
	 * @return double table containing in [0] flatness and in [1] elongation
	 */
	public double [] computeFlatnessAndElongation (ImagePlus imagePlusInput, double label){
		double [] shapeParameters = new double[2];
		double [] tEigenValues = computeEigenValue3D (imagePlusInput,label);
		shapeParameters [0] = Math.sqrt(tEigenValues[1] / tEigenValues[0]);
		shapeParameters [1] =  Math.sqrt (tEigenValues[2] / tEigenValues[1]);
		return shapeParameters;
	}  

	/**
	 * Method which determines object barycenter
	 * 
	 * @param unit if true the coordinates of barycenter are in µm.
	 * @param imagePlusInput ImagePlus of labelled image
	 * @param label double label of interest
	 * @return VoxelRecord the barycenter of the object of interest
	 */
	public VoxelRecord computeBarycenter3D (boolean unit,ImagePlus imagePlusInput, double label){
		ImageStack imageStackInput = imagePlusInput.getImageStack();
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		VoxelRecord voxelRecordBarycenter = new VoxelRecord ();
		int count = 0;
		int sx = 0;
		int sy = 0;
		int sz =0;
		double voxelValue;
		for (int k = 0; k < imagePlusInput.getStackSize(); ++k) {
            for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
                for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
                    voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue == label) {
                        sx += i;
                        sy += j;
                        sz += k;
                        ++count;
                    }
                }
            }
        }
		sx /= count;
		sy /= count;
		sz /= count;
		voxelRecordBarycenter.setLocation(sx, sy, sz);
		if (unit)
		    voxelRecordBarycenter.Multiplie(xCalibration, yCalibration,zCalibration);
		return voxelRecordBarycenter;
  	}
	
	/**
	 * Method which compute the barycenter of each objects and return the result
	 * in a table of VoxelRecord
	 *
	 * @param imagePlusInput ImagePlus of labelled image
	 * @param unit  if true the coordinates of barycenter are in µm.
	 * @return table of VoxelRecord for each object of the input image
	 */
	public VoxelRecord[] computeObjectBarycenter (ImagePlus imagePlusInput, boolean unit) {
		Histogram histogram = new Histogram();
		histogram.run(imagePlusInput);
		double []tlabel = histogram.getLabels();
		VoxelRecord [] tVoxelRecord = new VoxelRecord [ tlabel.length];
		for(int i = 0; i < tlabel.length; ++i) {
			tVoxelRecord[i] = computeBarycenter3D(unit, imagePlusInput,tlabel[i] );
		}
		return tVoxelRecord;
	}
	
	/**
	 * Intensity of chromocenters/ intensity of the nucleus
	 * 
	 * @param imagePlusInput ImagePlus raw image
	 * @param imagePlusSegmented binary ImagePlus
	 * @param imagePlusChromocenter ImagePlus of the chromocemters
	 * @return double Relative Heterochromatin Fraction compute on the Intensity ratio
	 */
	public double computeIntensityRHF (ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter ) {
	    double chromocenterIntensity = 0;
	    double nucleusIntensity = 0;
	    double voxelValueChromocenter;
	    double voxelValueInput;
	    double voxelValueSegmented;
	    ImageStack imageStackChromocenter =  imagePlusChromocenter.getStack();
	    ImageStack imageStackSegmented = imagePlusSegmented.getStack();
	    ImageStack imageStackInput = imagePlusInput.getStack();
	    for (int k = 0; k < imagePlusInput.getNSlices(); ++k) {
            for (int i = 0; i < imagePlusInput.getWidth(); ++i) {
                for (int j = 0; j < imagePlusInput.getHeight(); ++j) {
                    voxelValueSegmented = imageStackSegmented.getVoxel(i, j, k);
                    voxelValueInput = imageStackInput.getVoxel(i, j, k);
                    voxelValueChromocenter = imageStackChromocenter.getVoxel(i, j, k);

                    if (voxelValueSegmented > 0) {
                        if (voxelValueChromocenter > 0)
                            chromocenterIntensity += voxelValueInput;
                        nucleusIntensity += voxelValueInput;
                    }
                }
            }
        }
	    return chromocenterIntensity / nucleusIntensity;
	}

	  /**
	   * Method which compute the RHF (total chromocenters volume / nucleus volume)
	   * @return RHF
	   *
	   * @param imagePlusSegmented binary ImagePlus
	   * @param imagePlusChomocenters ImagePLus of the chromocenters
	   * @return double Relative Heterochromatin Fraction compute on the Volume ratio
	   */

	public double computeVolumeRHF (ImagePlus imagePlusSegmented, ImagePlus imagePlusChomocenters) {
	    double volumeCc = 0;
	    double [] tVolumeChromocenter = computeVolumeofAllObjects(imagePlusChomocenters);
	    for (int i = 0; i < tVolumeChromocenter.length; ++i) 
	    	volumeCc += tVolumeChromocenter[i];
	    double []tVolumeSegmented = computeVolumeofAllObjects(imagePlusSegmented);
	    return volumeCc / tVolumeSegmented[0];
	}
	  
	/**
	 * Detect the number of object on segmented imaeg  plop
	 * @param imagePlusInput Segmented image
	 * @return int nb of object in the image
	 */
	public int getNumberOfObject (ImagePlus imagePlusInput) {
	    Histogram histogram = new Histogram ();
		histogram.run(imagePlusInput);
		return histogram.getNbLabels();
	}

	public double computeComplexSurface2(ImagePlus imagePlusInput) {
		Gradient gradient = new Gradient(imagePlusInput);
		ArrayList <Double> tableUnitaire [][][] = gradient.getUnitaire();
		ImageStack imageStackSegmented = this._image.getStack();
		double surfaceArea = 0,voxelValue, neighborVoxelValue;
		VoxelRecord voxelRecordIn = new VoxelRecord();
		VoxelRecord voxelRecordOut= new VoxelRecord();

		for (int k = 2; k < this._image.getNSlices()-2; ++k) {
			for (int i = 2; i < this._image.getWidth() - 2; ++i) {
				for (int j = 2; j < this._image.getHeight() - 2; ++j) {
					voxelValue = imageStackSegmented.getVoxel(i, j, k);
					if (voxelValue > 0) {
						for (int kk = k - 1; kk <= k + 1; kk += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation((double) i, (double) j, (double) k);
								voxelRecordOut.setLocation((double) i, (double) j, (double) kk);
								surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k],
										tableUnitaire[i][j][kk], voxelRecordIn, voxelRecordOut,
										((this._xCal) * (this._ycal)));
							}
						}
						for (int ii = i - 1; ii <= i + 1; ii += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation((double) i, (double) j, (double) k);
								voxelRecordOut.setLocation((double) ii, (double) j, (double) k);
								surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[ii][j][k],
										voxelRecordIn, voxelRecordOut, ((this._ycal) * (this._zcal)));
							}
						}
						for (int jj = j - 1; jj <= j + 1; jj += 2) {
							neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
							if (voxelValue != neighborVoxelValue) {
								voxelRecordIn.setLocation((double) i, (double) j, (double) k);
								voxelRecordOut.setLocation((double) i, (double) jj, (double) k);
								surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][jj][k],
										voxelRecordIn, voxelRecordOut, ((this._xCal) * (this._zcal)));
							}
						}
					}
				}
			}
		}
		return surfaceArea;
	}

	/**
	 * //TODO garder qu'une seul methode poure calcuer la surface complexe
     *
	 * @param imagePlusInput
	 * @param imagePlusSegmented
	 * @return
	 */
	public double computeComplexSurface(ImagePlus imagePlusInput, ImagePlus imagePlusSegmented) {
	    Gradient gradient = new Gradient(imagePlusInput);
		ArrayList <Double> tableUnitaire [][][] = gradient.getUnitaire();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		double surfaceArea = 0,voxelValue, neighborVoxelValue;
		VoxelRecord voxelRecordIn = new VoxelRecord();
		VoxelRecord voxelRecordOut= new VoxelRecord();
		Calibration calibration= imagePlusInput.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		for (int k = 2; k < imagePlusSegmented.getNSlices()-2; ++k) {
            for (int i = 2; i < imagePlusSegmented.getWidth() - 2; ++i) {
                for (int j = 2; j < imagePlusSegmented.getHeight() - 2; ++j) {
                    voxelValue = imageStackSegmented.getVoxel(i, j, k);
                    if (voxelValue > 0) {
                        for (int kk = k - 1; kk <= k + 1; kk += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) j, (double) kk);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k],
										      tableUnitaire[i][j][kk], voxelRecordIn, voxelRecordOut,
										      ((xCalibration) * (yCalibration)));
                            }
                        }
                        for (int ii = i - 1; ii <= i + 1; ii += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) ii, (double) j, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[ii][j][k],
                                        voxelRecordIn, voxelRecordOut, ((yCalibration) * (zCalibration)));
                            }
                        }
                        for (int jj = j - 1; jj <= j + 1; jj += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) jj, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][jj][k],
                                        voxelRecordIn, voxelRecordOut, ((xCalibration) * (zCalibration)));
                            }
                        }
                    }
                }
            }
        }
		return surfaceArea;
	}


    /**
     *
     * @param imagePlusSegmented
     * @param gradient
     * @return
     */
	public double computeComplexSurface(ImagePlus imagePlusSegmented, Gradient gradient ) {
	    ArrayList <Double> tableUnitaire [][][] = gradient.getUnitaire();
		ImageStack imageStackSegmented = imagePlusSegmented.getStack();
		double surfaceArea = 0,voxelValue, neighborVoxelValue;
		VoxelRecord voxelRecordIn = new VoxelRecord();
		VoxelRecord voxelRecordOut= new VoxelRecord();
		Calibration calibration= imagePlusSegmented.getCalibration();
		double xCalibration = calibration.pixelWidth;
		double yCalibration = calibration.pixelHeight;
		double zCalibration = calibration.pixelDepth;
		for (int k = 2; k < imagePlusSegmented.getNSlices()-2; ++k) {
            for (int i = 2; i < imagePlusSegmented.getWidth() - 2; ++i) {
                for (int j = 2; j < imagePlusSegmented.getHeight() - 2; ++j) {
                    voxelValue = imageStackSegmented.getVoxel(i, j, k);
                    if (voxelValue > 0) {
                        for (int kk = k - 1; kk <= k + 1; kk += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) j, (double) kk);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][j][kk],
                                        voxelRecordIn, voxelRecordOut, ((xCalibration) * (yCalibration)));
                            }
                        }
                        for (int ii = i - 1; ii <= i + 1; ii += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) ii, (double) j, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[ii][j][k],
                                        voxelRecordIn, voxelRecordOut, ((yCalibration) * (zCalibration)));
                            }
                        }
                        for (int jj = j - 1; jj <= j + 1; jj += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) jj, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][jj][k],
                                        voxelRecordIn, voxelRecordOut, ((xCalibration) * (zCalibration)));
                            }
                        }
                    }
                }
            }
        }
		return surfaceArea;
	}

	/**
	 *
	 * @param listUnitaireIn
	 * @param listUnitaireOut
	 * @param voxelRecordIn
	 * @param voxelRecordOut
	 * @param as
	 * @return
	 */
	private double computeSurfelContribution (ArrayList <Double> listUnitaireIn, ArrayList <Double> listUnitaireOut,
			  VoxelRecord voxelRecordIn, VoxelRecord voxelRecordOut, double as ) {
	    double dx = voxelRecordIn._i - voxelRecordOut._i;
		double dy = voxelRecordIn._j - voxelRecordOut._j;
		double dz = voxelRecordIn._k - voxelRecordOut._k;
		double nx = (listUnitaireIn.get(0)+listUnitaireOut.get(0))/2;
		double ny = (listUnitaireIn.get(1)+listUnitaireOut.get(1))/2;
		double nz = (listUnitaireIn.get(2)+listUnitaireOut.get(2))/2;
		return Math.abs((dx*nx+dy*ny+dz*nz)*as);
	}
}