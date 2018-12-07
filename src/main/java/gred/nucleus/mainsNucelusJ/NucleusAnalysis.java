package gred.nucleus.mainsNucelusJ;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import gred.nucleus.core.Measure3D;
import ij.IJ;
import ij.ImagePlus;


/**
 * Several method to realise and create the outfile for the nuclear Analysis
 * 
 * @author Tristan Dubos and Axel Poulet
 *
 */

public class NucleusAnalysis {
	@SuppressWarnings("unused")
	private static class IOEception {  public IOEception() { } }
    private ImagePlus _imgRaw = new ImagePlus();
    private ImagePlus _imgSeg = new ImagePlus();
    private String _resu = "";

	public NucleusAnalysis (ImagePlus raw, ImagePlus seg){
	    this._imgRaw =  raw;
	    this._imgSeg = seg;
    }

	public NucleusAnalysis (){}

	public String nucleusParameter3D (){
        Measure3D measure3D = new Measure3D();
        double volume = measure3D.computeVolumeObject(this._imgSeg,255);
        double surfaceArea = measure3D.computeSurfaceObject(this._imgSeg,255);
        double bis = measure3D.computeComplexSurface(this._imgRaw, this._imgSeg);
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
                +measure3D.computeSphericity(volume, bis);
        return this._resu;
    }

    /**
     *
     * @param resu
     */
    public void setResu(String resu){
	    this._resu = resu;
    }
}