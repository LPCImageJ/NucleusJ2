package gred.nucleus.core;

import gred.nucleus.autocrop.Box;
import gred.nucleus.utils.Gradient;
import gred.nucleus.utils.Histogram;
import gred.nucleus.utils.VoxelRecord;
import ij.*;
import ij.measure.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import Jama.EigenvalueDecomposition;
import Jama.Matrix;

/**
 * Class NucleusMeasure : allow the compute of severals 3Dparameters (shape, lenght) in
 * binary object
 * <p>
 * //TODO reercrire cette classe ya des choses que je fais 5 fois c'est inutil
 *
 * @author Tristan Dubos and Axel Poulet
 */

public class Measure3D {

    ImagePlus _imageSeg;
    ImagePlus _rawImage;


    double _xCal;
    double _yCal;
    double _zCal;

    HashMap< Double, Integer> _segmentedNucleusHisto =new HashMap <Double, Integer>();


    public Measure3D() {
    }


    public Measure3D(double xCal, double ycal, double zCal) {
        this._xCal = xCal;
        this._yCal = ycal;
        this._zCal = zCal;

    }

    public Measure3D(ImagePlus imageSeg, ImagePlus rawImage, double xCal, double ycal, double zCal) {
        this._rawImage = rawImage;
        this._imageSeg = imageSeg;
        this._xCal = xCal;
        this._yCal = ycal;
        this._zCal = zCal;
    }

    /**
     * Scan of image and if the voxel belong to theobject of interest, looking,
     * if in his neighborhood there are voxel value == 0 then it is a boundary voxel.
     * Adding the surface of the face of the voxel frontier, which are in contact
     * with the background of the image, to the surface total.
     *
     * @param label          label of the interest object
     * @return the surface
     */

    public double computeSurfaceObject( double label) {
        ImageStack imageStackInput = this._imageSeg.getStack();
        double surfaceArea = 0, voxelValue, neighborVoxelValue;
        for (int k = 1; k < this._imageSeg.getStackSize(); ++k) {
            for (int i = 1; i < this._imageSeg.getWidth(); ++i) {
                for (int j = 1; j < this._imageSeg.getHeight(); ++j) {
                    voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue == label) {
                        for (int kk = k - 1; kk <= k + 1; kk += 2) {
                            neighborVoxelValue = imageStackInput.getVoxel(i, j, kk);
                            if (voxelValue != neighborVoxelValue)
                                surfaceArea = surfaceArea + this._xCal * this._yCal;
                        }
                        for (int ii = i - 1; ii <= i + 1; ii += 2) {
                            neighborVoxelValue = imageStackInput.getVoxel(ii, j, k);
                            if (voxelValue != neighborVoxelValue)
                                surfaceArea = surfaceArea + this._yCal * this._zCal;
                        }
                        for (int jj = j - 1; jj <= j + 1; jj += 2) {
                            neighborVoxelValue = imageStackInput.getVoxel(i, jj, k);
                            if (voxelValue != neighborVoxelValue)
                                surfaceArea = surfaceArea + this._xCal * this._zCal;
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
    public double[] computeVolumeofAllObjects(ImagePlus imagePlusInput) {

        Histogram histogram = new Histogram();
        histogram.run(imagePlusInput);
        double[] tlabel = histogram.getLabels();
        double[] tObjectVolume = new double[tlabel.length];
        HashMap<Double, Integer> hashHisto = histogram.getHistogram();
        for (int i = 0; i < tlabel.length; ++i) {
            int nbVoxel = hashHisto.get(tlabel[i]);
            tObjectVolume[i] = nbVoxel * this._xCal * this._yCal * this._zCal;
        }
        return tObjectVolume;
    }

    /**
     * Compute the volume of one object with this label
     *
     * @param label double label of the object of interest
     * @return double: the volume of the label of interest
     */
    public double computeVolumeObject2(double label) {
        Histogram histogram = new Histogram();
        histogram.run(this._imageSeg);
        HashMap<Double, Integer> hashMapHisto = histogram.getHistogram();
        return hashMapHisto.get(label) * this._xCal * this._yCal * this._zCal;

    }

    public double computeVolumeObject3(double label, ImagePlus imagePlusInput) {
        Histogram histogram = new Histogram();
        histogram.run(imagePlusInput);
        HashMap<Double, Integer> hashMapHisto = histogram.getHistogram();
        return hashMapHisto.get(label) * this._xCal * this._yCal * this._zCal;


    }

    /**
     * Compute the volume of one object with this label
     *
     * @param imagePlusInput ImagePLus of the segmented image
     * @param label          double label of the object of interest
     * @return double: the volume of the label of interest
     */
    public double computeVolumeObject(ImagePlus imagePlusInput, double label) {
        Histogram histogram = new Histogram();
        histogram.run(imagePlusInput);
        HashMap<Double, Integer> hashMapHisto = histogram.getHistogram();
        return hashMapHisto.get(label) * this._xCal * this._yCal * this._zCal;
    }

    /**
     * compute the equivalent spherical radius
     *
     * @param volume double of the volume of the object of interesr
     * @return double the equivalent spherical radius
     */
    public double equivalentSphericalRadius(double volume) {
        double radius = (3 * volume) / (4 * Math.PI);
        radius = Math.pow(radius, 1.0 / 3.0);
        return radius;
    }

    /**
     * compute the equivalent spherical radius with ImagePlus in input
     *
     * @param imagePlusBinary ImagePlus of the segmented image
     * @return double the equivalent spherical radius
     */
    public double equivalentSphericalRadius(ImagePlus imagePlusBinary) {
        double radius = (3 * computeVolumeObject(imagePlusBinary, 255)) / (4 * Math.PI);
        radius = Math.pow(radius, 1.0 / 3.0);
        return radius;
    }


    /**
     * Method which compute the sphericity :
     * 36Pi*Volume^2/Surface^3 = 1 if perfect sphere
     *
     * @param volume  double volume of the object
     * @param surface double surface of the object
     * @return double sphercity
     */
    public double computeSphericity(double volume, double surface) {
        return ((36 * Math.PI * (volume * volume)) / (surface * surface * surface));
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
     * @param label          double label of interest
     * @return double table containing the 3 eigen values
     */

    public double[] computeEigenValue3D( double label) {
        ImageStack imageStackInput = this._imageSeg.getImageStack();
        VoxelRecord barycenter = computeBarycenter3D(true, this._imageSeg, label);

        double xx = 0;
        double xy = 0;
        double xz = 0;
        double yy = 0;
        double yz = 0;
        double zz = 0;
        int compteur = 0;
        double voxelValue;
        for (int k = 0; k < this._imageSeg.getStackSize(); ++k) {
            double dz = ((this._zCal * (double) k) - barycenter.getK());
            for (int i = 0; i < this._imageSeg.getWidth(); ++i) {
                double dx = ((this._xCal * (double) i) - barycenter.getI());
                for (int j = 0; j < this._imageSeg.getHeight(); ++j) {
                    voxelValue = imageStackInput.getVoxel(i, j, k);
                    if (voxelValue == label) {
                        double dy = ((this._yCal * (double) j) - barycenter.getJ());
                        xx += dx * dx;
                        yy += dy * dy;
                        zz += dz * dz;
                        xy += dx * dy;
                        xz += dx * dz;
                        yz += dy * dz;
                        compteur++;
                    }
                }
            }
        }
        double[][] tValues = {{xx / compteur, xy / compteur, xz / compteur},
                {xy / compteur, yy / compteur, yz / compteur},
                {xz / compteur, yz / compteur, zz / compteur}};
        Matrix matrix = new Matrix(tValues);
        EigenvalueDecomposition eigenValueDecomposition = matrix.eig();
        return eigenValueDecomposition.getRealEigenvalues();
    }


    /**
     * Compute the flatness and the elongation of the object of interest
     *
     * @param label          double label of interest
     * @return double table containing in [0] flatness and in [1] elongation
     */
    public double[] computeFlatnessAndElongation( double label) {
        double[] shapeParameters = new double[2];
        double[] tEigenValues = computeEigenValue3D( label);
        shapeParameters[0] = Math.sqrt(tEigenValues[1] / tEigenValues[0]);
        shapeParameters[1] = Math.sqrt(tEigenValues[2] / tEigenValues[1]);
        return shapeParameters;
    }

    /**
     * Method which determines object barycenter
     *
     * @param unit           if true the coordinates of barycenter are in µm.
     * @param imagePlusInput ImagePlus of labelled image
     * @param label          double label of interest
     * @return VoxelRecord the barycenter of the object of interest
     */
    public VoxelRecord computeBarycenter3D(boolean unit, ImagePlus imagePlusInput, double label) {
        ImageStack imageStackInput = imagePlusInput.getImageStack();
        Calibration calibration = imagePlusInput.getCalibration();
        double xCalibration = calibration.pixelWidth;
        double yCalibration = calibration.pixelHeight;
        double zCalibration = calibration.pixelDepth;
        VoxelRecord voxelRecordBarycenter = new VoxelRecord();
        int count = 0;
        int sx = 0;
        int sy = 0;
        int sz = 0;
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
            voxelRecordBarycenter.Multiplie(this._xCal, this._yCal, this._zCal);
        return voxelRecordBarycenter;
    }

    /**
     * Method which compute the barycenter of each objects and return the result
     * in a table of VoxelRecord
     *
     * @param imagePlusInput ImagePlus of labelled image
     * @param unit           if true the coordinates of barycenter are in µm.
     * @return table of VoxelRecord for each object of the input image
     */
    public VoxelRecord[] computeObjectBarycenter(ImagePlus imagePlusInput, boolean unit) {
        Histogram histogram = new Histogram();
        histogram.run(imagePlusInput);
        double[] tlabel = histogram.getLabels();
        VoxelRecord[] tVoxelRecord = new VoxelRecord[tlabel.length];
        for (int i = 0; i < tlabel.length; ++i) {
            tVoxelRecord[i] = computeBarycenter3D(unit, imagePlusInput, tlabel[i]);
        }
        return tVoxelRecord;
    }

    /**
     * Intensity of chromocenters/ intensity of the nucleus
     *
     * @param imagePlusInput        ImagePlus raw image
     * @param imagePlusSegmented    binary ImagePlus
     * @param imagePlusChromocenter ImagePlus of the chromocemters
     * @return double Relative Heterochromatin Fraction compute on the Intensity ratio
     */
    public double computeIntensityRHF(ImagePlus imagePlusInput, ImagePlus imagePlusSegmented, ImagePlus imagePlusChromocenter) {
        double chromocenterIntensity = 0;
        double nucleusIntensity = 0;
        double voxelValueChromocenter;
        double voxelValueInput;
        double voxelValueSegmented;
        ImageStack imageStackChromocenter = imagePlusChromocenter.getStack();
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
     *
     * @param imagePlusSegmented    binary ImagePlus
     * @param imagePlusChomocenters ImagePLus of the chromocenters
     * @return double Relative Heterochromatin Fraction compute on the Volume ratio
     */

    public double computeVolumeRHF(ImagePlus imagePlusSegmented, ImagePlus imagePlusChomocenters) {
        double volumeCc = 0;
        double[] tVolumeChromocenter = computeVolumeofAllObjects(imagePlusChomocenters);
        for (int i = 0; i < tVolumeChromocenter.length; ++i)
            volumeCc += tVolumeChromocenter[i];
        double[] tVolumeSegmented = computeVolumeofAllObjects(imagePlusSegmented);
        return volumeCc / tVolumeSegmented[0];
    }

    /**
     * Detect the number of object on segmented imaeg  plop
     *
     * @param imagePlusInput Segmented image
     * @return int nb of object in the image
     */
    public int getNumberOfObject(ImagePlus imagePlusInput) {
        Histogram histogram = new Histogram();
        histogram.run(imagePlusInput);
        return histogram.getNbLabels();
    }

    public double computeComplexSurface2(ImagePlus imagePlusInput) {
        Gradient gradient = new Gradient(imagePlusInput);
        ArrayList<Double> tableUnitaire[][][] = gradient.getUnitaire();
        ImageStack imageStackSegmented = this._rawImage.getStack();
        double surfaceArea = 0, voxelValue, neighborVoxelValue;
        VoxelRecord voxelRecordIn = new VoxelRecord();
        VoxelRecord voxelRecordOut = new VoxelRecord();

        for (int k = 2; k < this._rawImage.getNSlices() - 2; ++k) {
            for (int i = 2; i < this._rawImage.getWidth() - 2; ++i) {
                for (int j = 2; j < this._rawImage.getHeight() - 2; ++j) {
                    voxelValue = imageStackSegmented.getVoxel(i, j, k);
                    if (voxelValue > 0) {
                        for (int kk = k - 1; kk <= k + 1; kk += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) j, (double) kk);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k],
                                        tableUnitaire[i][j][kk], voxelRecordIn, voxelRecordOut,
                                        ((this._xCal) * (this._yCal)));
                            }
                        }
                        for (int ii = i - 1; ii <= i + 1; ii += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) ii, (double) j, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[ii][j][k],
                                        voxelRecordIn, voxelRecordOut, ((this._yCal) * (this._zCal)));
                            }
                        }
                        for (int jj = j - 1; jj <= j + 1; jj += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) jj, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][jj][k],
                                        voxelRecordIn, voxelRecordOut, ((this._xCal) * (this._zCal)));
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

     * @return
     */
    public double computeComplexSurface() {
        Gradient gradient = new Gradient(this._rawImage);
        ArrayList<Double> tableUnitaire[][][] = gradient.getUnitaire();
        ImageStack imageStackSegmented = this._imageSeg.getStack();
        double surfaceArea = 0, voxelValue, neighborVoxelValue;
        VoxelRecord voxelRecordIn = new VoxelRecord();
        VoxelRecord voxelRecordOut = new VoxelRecord();

        for (int k = 2; k < this._imageSeg.getNSlices() - 2; ++k) {
            for (int i = 2; i < this._imageSeg.getWidth() - 2; ++i) {
                for (int j = 2; j < this._imageSeg.getHeight() - 2; ++j) {
                    voxelValue = imageStackSegmented.getVoxel(i, j, k);
                    if (voxelValue > 0) {
                        for (int kk = k - 1; kk <= k + 1; kk += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, j, kk);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) j, (double) kk);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k],
                                        tableUnitaire[i][j][kk], voxelRecordIn, voxelRecordOut,
                                        ((this._xCal) * (this._yCal)));
                            }
                        }
                        for (int ii = i - 1; ii <= i + 1; ii += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(ii, j, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) ii, (double) j, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[ii][j][k],
                                        voxelRecordIn, voxelRecordOut, ((this._yCal) * (this._zCal)));
                            }
                        }
                        for (int jj = j - 1; jj <= j + 1; jj += 2) {
                            neighborVoxelValue = imageStackSegmented.getVoxel(i, jj, k);
                            if (voxelValue != neighborVoxelValue) {
                                voxelRecordIn.setLocation((double) i, (double) j, (double) k);
                                voxelRecordOut.setLocation((double) i, (double) jj, (double) k);
                                surfaceArea = surfaceArea + computeSurfelContribution(tableUnitaire[i][j][k], tableUnitaire[i][jj][k],
                                        voxelRecordIn, voxelRecordOut, ((this._xCal) * (this._zCal)));
                            }
                        }
                    }
                }
            }
        }
        return surfaceArea;
    }


    /**
     * @param imagePlusSegmented
     * @param gradient
     * @return
     */
    public double computeComplexSurface(ImagePlus imagePlusSegmented, Gradient gradient) {
        ArrayList<Double> tableUnitaire[][][] = gradient.getUnitaire();
        ImageStack imageStackSegmented = imagePlusSegmented.getStack();
        double surfaceArea = 0, voxelValue, neighborVoxelValue;
        VoxelRecord voxelRecordIn = new VoxelRecord();
        VoxelRecord voxelRecordOut = new VoxelRecord();
        Calibration calibration = imagePlusSegmented.getCalibration();
        double xCalibration = calibration.pixelWidth;
        double yCalibration = calibration.pixelHeight;
        double zCalibration = calibration.pixelDepth;
        for (int k = 2; k < imagePlusSegmented.getNSlices() - 2; ++k) {
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
     * @param listUnitaireIn
     * @param listUnitaireOut
     * @param voxelRecordIn
     * @param voxelRecordOut
     * @param as
     * @return
     */
    private double computeSurfelContribution(ArrayList<Double> listUnitaireIn, ArrayList<Double> listUnitaireOut,
                                             VoxelRecord voxelRecordIn, VoxelRecord voxelRecordOut, double as) {
        double dx = voxelRecordIn._i - voxelRecordOut._i;
        double dy = voxelRecordIn._j - voxelRecordOut._j;
        double dz = voxelRecordIn._k - voxelRecordOut._k;
        double nx = (listUnitaireIn.get(0) + listUnitaireOut.get(0)) / 2;
        double ny = (listUnitaireIn.get(1) + listUnitaireOut.get(1)) / 2;
        double nz = (listUnitaireIn.get(2) + listUnitaireOut.get(2)) / 2;
        return Math.abs((dx * nx + dy * ny + dz * nz) * as);
    }

    /**
     * Compute an Hashmap describing the segmented object (from raw data).
     * Key = Voxels intensity
     * value = Number of voxels
     *
     * If voxels ==255 in seg image
     *      add Hashmap (Voxels intensity ,+1)
     *
     */

    private void histogramSegmentedNucleus() {
        ImageStack imageStackRaw = this._rawImage.getStack();
        ImageStack imageStackSeg = this._imageSeg.getStack();
        Histogram histogram = new Histogram();
        histogram.run(this._rawImage);
        for(int k = 0; k < this._rawImage.getStackSize(); ++k) {
            for (int i = 0; i < this._rawImage.getWidth(); ++i) {
                for (int j = 0; j < this._rawImage.getHeight(); ++j) {
                    double voxelValue = imageStackSeg.getVoxel(i, j, k);
                    if (voxelValue ==255) {
                        if(!this._segmentedNucleusHisto.containsKey(imageStackRaw.getVoxel(i, j, k)) ){
                            this._segmentedNucleusHisto.put(imageStackRaw.getVoxel(i, j, k),  1);
                        }
                        else{
                            this._segmentedNucleusHisto.put(imageStackRaw.getVoxel(i, j, k), this._segmentedNucleusHisto.get(imageStackRaw.getVoxel(i, j, k)) + 1);

                        }
                    }
                }
            }
        }

    }

    /**
     * Compute the mean intensity of the segmented object
     * by comparing voxels intensity in the raw image and
     * white/segmented voxels the segmented image.
     *
     * @return mean intensity of segmented object
     */

    private double meanIntensity (){
        int numberOfVoxel=0;
        double mean=0;
        for(Map.Entry<Double , Integer> histo2 :  this._segmentedNucleusHisto.entrySet()) {
            numberOfVoxel+=histo2.getValue();
            mean+=histo2.getKey()*histo2.getValue();

        }
        return mean/numberOfVoxel;
    }

    /**
     * Compute the standard deviation of the mean intensity
     * @see Measure3D#meanIntensity()
     * @return the standard deviation of the mean intensity of segmented object
     */

    private double standardDeviationIntensity (Double mean){
        int numberOfVoxel=0;
        double std=0;
        for(Map.Entry<Double , Integer> histo2 :  this._segmentedNucleusHisto.entrySet()) {
            numberOfVoxel+=histo2.getValue();
            std=Math.abs((histo2.getKey()*histo2.getValue())-(histo2.getValue()*mean));

        }
        return std/(numberOfVoxel-1);


    }

    /**
     * Find the maximum intensity voxel of segmented object
     * @return the maximum intensity voxel of segmented object
     */

    private double maxIntensity(){
        double maxIntensity=0;
        for (Map.Entry<Double , Integer> entry : this._segmentedNucleusHisto.entrySet())
        {
            if (maxIntensity ==0 || entry.getKey().compareTo(maxIntensity) > 0)
            {
                maxIntensity = entry.getKey();
            }
        }
        return maxIntensity;

    }

    /**
     * Find the minimum intensity voxel of segmented object
     * @return the minimum intensity voxel of segmented object
     */

    private double minIntensity(){
        double minIntensity=0;
        for (Map.Entry<Double , Integer> entry : this._segmentedNucleusHisto.entrySet())
        {
            if (minIntensity ==0 || entry.getKey().compareTo(minIntensity) < 0)
            {
                minIntensity = entry.getKey();
            }
        }
        return minIntensity;

    }

    /**
     * list of parameters compute in this method returned in tabulated format
     * @return list of parameters compute in this method returned in tabulated format
     */

    public String nucleusParameter3D() {
        String resu = "";
        histogramSegmentedNucleus();
        double volume = computeVolumeObject2(255);
        double surfaceArea = computeSurfaceObject( 255);
        double bis = computeComplexSurface();
        double[] tEigenValues = computeEigenValue3D(255);
        resu = this._rawImage.getTitle() + "\t"
                + computeVolumeObject2(255) + "\t"
                + computeFlatnessAndElongation( 255)[0] + "\t"
                + computeFlatnessAndElongation( 255)[1] + "\t"
                + computeSphericity(volume, surfaceArea) + "\t"
                + equivalentSphericalRadius(volume) + "\t"
                + surfaceArea + "\t"
                + bis + "\t"
                + computeSphericity(volume, bis)+ "\t"
                +meanIntensity()+ "\t"
                +standardDeviationIntensity(meanIntensity())+ "\t"
                +minIntensity()+ "\t"
                +maxIntensity();

        return resu;
    }
}