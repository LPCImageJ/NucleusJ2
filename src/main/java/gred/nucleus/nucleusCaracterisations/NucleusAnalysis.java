package gred.nucleus.nucleusCaracterisations;

import gred.nucleus.core.Measure3D;
import ij.IJ;
import ij.ImagePlus;


/**
 * Analysis of nuclear segmented image, method call Measure3D class, to copute the 3D parameters of interest
 * @author Tristan Dubos and Axel Poulet
 *
 */
public class NucleusAnalysis {
    /**ImagePlus raw image*/
    private ImagePlus _imgRaw = new ImagePlus();
    /**ImagePlus segmented image*/
    private ImagePlus _imgSeg = new ImagePlus();
    /** String stocking the the parameters 3D results*/
    private String _resu = "";

    /**
     * Constructor
     * @param raw ImagePlus raw
     * @param seg ImagePlus segmented
     */
	public NucleusAnalysis (ImagePlus raw, ImagePlus seg) {
        this._imgRaw = raw;
        this._imgSeg = seg;
    }

    /**
     * Compute the different 3D parameter and stock them in a String
     * NucleusFileName
     * Volume
     * Flatness
     * Elongation
     * Sphericity => //TODO can be removed after test if the correction is OK
     * Esr
     * SurfaceArea => //TODO can be removed after test if the correction is OK
     * SurfaceAreaCorrected => with surfel method
     * SphericityCorrected => compute sphericty with SurfaceAreaCorrected
     * @return String results of the 3D parameter
     */
	public String nucleusParameter3D (){
        Measure3D measure3D = new Measure3D();
        double volume = measure3D.computeVolumeObject(this._imgSeg,255);
        double surfaceArea = measure3D.computeSurfaceObject(this._imgSeg,255);
        double bis = measure3D.computeComplexSurface(this._imgRaw, this._imgSeg);
        double [] tEigenValues = measure3D.computeEigenValue3D (this._imgSeg,255);
        IJ.log(" "+tEigenValues[0]            + " " +tEigenValues[1]  + " "              +tEigenValues[2]);
        if(this._resu.equals(""))
            this._resu = "NucleusFileName\tVolume\tFlatness\tElongation\tSphericity\tEsr\tSurfaceArea\tSurfaceAreaCorrected\tSphericityCorrected";
        this._resu = this._resu+"\n"+this._imgSeg.getTitle()+"\t"
                +measure3D.computeVolumeObject(this._imgSeg,255)+"\t"
                +measure3D.computeFlatnessAndElongation(this._imgSeg,255)[0]+"\t"
                +measure3D.computeFlatnessAndElongation(this._imgSeg,255)[1]+"\t"
                +measure3D.computeSphericity(volume, surfaceArea)+"\t"
                +measure3D.equivalentSphericalRadius(volume)+"\t"
                +surfaceArea+"\t"
                +bis+"\t"
                +measure3D.computeSphericity(volume, bis)
                ;
        return this._resu;
    }

    /**
     * Setter of _resu.
     * @param resu String
     */
    public void setResu(String resu){
	    this._resu = resu;
    }
}