package gred.nucleus.utils;


import ij.measure.Calibration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.invoke.MethodHandles;
import java.util.List;


/** @author Tristan Dubos and Axel Poulet */
public class ConvexHullDetection {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
	
	private static final double PI = Math.PI;
	
	private VoxelRecord p0       = new VoxelRecord();
	private String      axesName = "";
	/*
	 * 1) On calcule l'angle alpha comme précédemment :
	 * double acos = Math.acos(cosAlpha)
	 * 	double alpha;
	 * 	if (sinAlpha < 0)
	 * 		alpha = -acos;
	 * 	else
	 * 		alpha = acos;
	 * 2) L'angle alpha est l'angle entre la "tangente"  (vecteur entre v_{n-1} et v_n) à la frontière, calculée précédemment, et le vecteur vers les autres voxels.
	 * L'algo précédent doit marche pour une vrai enveloppe convexe (à mettre au point avant de coder la version avec seuillage sur la distance !).
	 * Pour une vrai enveloppe convexe, normalement, l'angle alpha doit toujours, pour tous les voxels, être entre 0 et pi (à vérifier !).
	 * Dans notre algo avec seuillage sur la distance, justement, on peut autoriser des angles alphas négatifs, car le polygone de sortie n'est pas forcément convexe.
	 * On doit alors ruser en considérant non pas l'angle entre la "tangente" à la frontière, calculée précédemment, et le vecteur vers les autres voxels, mais l'angle
	 * (ou plutôt son représentant pris entre -pi et pi) entre la "normale entrante", qui est la "tangente" (vecteur entre v_{n-1} et v_n) plus pi/2.
	 * Ça donne quelque chose comme :
	 *
	 * double angle = computeAngle(vectorTest,vectorCourant);
	 * double anglePlusPiSurDeux = angle - pi/2;
	 * if (anglePlusPiSurDeux < -pi)
	 *         anglePlusPiSurDeux += 2*pi;
	 *  if(anglePlusPiSurDeux <= angleMin )
	 *  angleMin = anglePlusPiSurDeux;
	 *
	 *    etc.
	 *   Par contre, pour voir si on a "fait le tour" (somme des angles dépasse 2*pi), on peut garder l'ancien angle...
	 * Pour calculer l'angle alpha, on utilise :
	 *  produit vectoriel (U^V)z = Ux*Vy-Uy*Vx
	 *  produit scalaire V.U= Vx*Ux+Vy*Uy
	 *  produit des normes = racineCarre(Vx*Vx+Vy*Vy)*racineCarre(Ux*Ux+Uy*Uy)
	 *  sin alpha = produit vectoriel / produit des normes
	 *  cos alpha = produit scalaire / produit des normes
	 *  Pour calculer l'angle alpha, on utilise
	 *  sin alpha = produit vectoriel / produit des normes
	 *  cos alpha = produit scalaire / produit des normes
	 *  On inverse avec arccos en tenant compte du signe du sinus
	 *  et on cherche la détermination + ou - 2*k*pi qui se trouve dans [0, 2*pi[
	 */
	
	
	/**
	 * Method to detect the ensemble of convex hull voxels with  Jarvis march approach see :
	 * <p>
	 * https://fr.wikipedia.org/wiki/Marche_de_Jarvis https://en.wikipedia.org/wiki/Gift_wrapping_algorithm
	 *
	 * @param distanceThreshold
	 * @param convexHull        List of voxels composing the convexe Hull   The input contain only the starting pixel
	 *                          (the most down rigth of the boundary voxels)
	 * @param image             Image contenant les voxel de la composante connexe
	 * @param lVoxelBoundary    Liste des voxels frontiere
	 * @param vectorTest        Voxel ??? test avec
	 * @param calibration       Calibration de l'image
	 *
	 * @return List of voxels composing the convexe Hull
	 */
	public List<VoxelRecord> findConvexHull(double[][] image,
	                                        List<VoxelRecord> convexHull,
	                                        List<VoxelRecord> lVoxelBoundary,
	                                        VoxelRecord vectorTest,
	                                        Calibration calibration,
	                                        double distanceThreshold) {
		LOGGER.trace("Finding convex hull.");
		double      anglesSum      = 0.0;
		int         compteur       = 0;
		VoxelRecord voxelTest      = p0;
		VoxelRecord voxelPrecedent = new VoxelRecord();
		double      xCal           = calibration.pixelWidth;
		double      yCal           = calibration.pixelHeight;
		double      zCal           = calibration.pixelDepth;
		while (anglesSum < 2 * PI + 1) {
			double      angleMin          = 0;
			double      maxLength         = 0;
			double      distance          = 0;
			double      angleMinPiSurDeux = 2 * PI;
			VoxelRecord voxelMin          = new VoxelRecord();
			int         iMin              = 0;
			if (compteur != 0) {
				vectorTest.setLocation(voxelTest.i - voxelPrecedent.i,
				                       voxelTest.j - voxelPrecedent.j,
				                       voxelTest.k - voxelPrecedent.k);
			}
			
			for (int i = 0; i < lVoxelBoundary.size(); i++) {
				//LOGGER.trace("Processing boundary voxel {}/{}", i, lVoxelBoundary.size());
				//LOGGER.trace("    anglesSum: {}, {} {} {}", anglesSum, lVoxelBoundary.get(i).i, lVoxelBoundary.get(i).j, lVoxelBoundary.get(i).k);
				if (voxelTest.compareCoordinatesTo(lVoxelBoundary.get(i)) == 1) {
					VoxelRecord vectorCourant = new VoxelRecord();
					vectorCourant.setLocation(lVoxelBoundary.get(i).i - voxelTest.i,
					                          lVoxelBoundary.get(i).j - voxelTest.j,
					                          lVoxelBoundary.get(i).k - voxelTest.k);
					switch (axesName) {
						case "xy":
							distance = Math.sqrt(vectorCourant.i * xCal * vectorCourant.i * xCal +
							                     vectorCourant.j * yCal * vectorCourant.j * yCal);
							break;
						case "xz":
							distance = Math.sqrt(vectorCourant.i * xCal * vectorCourant.i * xCal +
							                     vectorCourant.k * zCal * vectorCourant.k * zCal);
							break;
						case "yz":
							distance = Math.sqrt(vectorCourant.k * zCal * vectorCourant.k * zCal +
							                     vectorCourant.j * yCal * vectorCourant.j * yCal);
							break;
					}
					// IJ.log("distance " +distance+ " "+vectorCourant._i + " "+vectorCourant._k );
					// IJ.log("distance " + distance +"<="+ distanceThreshold);
					if (distance <= distanceThreshold) {
						double angle              = computeAngle(vectorTest, vectorCourant, calibration);
						double anglePlusPiSurDeux = angle - PI / 2;
						if (anglePlusPiSurDeux <= -PI) {
							anglePlusPiSurDeux += 2 * PI;
						}
						double threshold = angleThreshold(image, voxelTest, vectorTest, calibration, distanceThreshold);
						if (anglePlusPiSurDeux <= angleMinPiSurDeux) {
							if (anglePlusPiSurDeux < angleMinPiSurDeux) {
								maxLength = distance;
								angleMinPiSurDeux = anglePlusPiSurDeux;
								angleMin = angle;
								voxelMin = lVoxelBoundary.get(i);
								iMin = i;
							} else if (anglePlusPiSurDeux == angleMinPiSurDeux && distance > maxLength) {
								maxLength = distance;
								angleMinPiSurDeux = anglePlusPiSurDeux;
								angleMin = angle;
								voxelMin = lVoxelBoundary.get(i);
								iMin = i;
							}
						}
					}
				}
			}
			++compteur;
			voxelPrecedent = voxelTest;
			voxelTest = voxelMin;
			
			//System.out.println("le voxel min "+voxelMin._i+" "+voxelMin._k+" "+voxelMin._j+" ");
			//if(voxelMin._i > 0 &&voxelMin._k> 0 &&voxelMin._j> 0 ){
			lVoxelBoundary.remove(iMin);
			
			anglesSum += angleMin;
			if (voxelMin.compareCoordinatesTo(p0) == 0) {
				break;
			}
			
			if (anglesSum <= 2 * PI) {
				convexHull.add(voxelMin);
				//IJ.log("point num: "+compteur+" "+_p0._i+" "+_p0._j+" "+_p0._k+" angle: "+angleMinPiSurDeux+" distance: "+maxLength+" angle sum"+anglesSum);
			}
/*
 }
 ICI ISSUE 13 a regarder !!!!! l'image qui pose probleme se trouve ici :
 /media/tridubos/DATA1/DATA_ANALYSE/MANIP_MANU_KAKU/ANALYSE_OCTOBRE_2019/images_PROBLEMS
 else{
 return null;
 }
 
 */
		
		}
		return convexHull;
	}
	
	
	/**
	 * sweetsweet sun
	 *
	 * @param calibration
	 * @param vector1
	 * @param vector2
	 *
	 * @return
	 */
	double computeAngle(VoxelRecord vector1, VoxelRecord vector2, Calibration calibration) {
		double xcal = calibration.pixelWidth;
		double ycal = calibration.pixelHeight;
		double zcal = calibration.pixelDepth;
		double normeVector1 = Math.sqrt(vector1.i * xcal * vector1.i * xcal +
		                                vector1.j * ycal * vector1.j * ycal +
		                                vector1.k * zcal * vector1.k * zcal);
		double normeVector2 = Math.sqrt(vector2.i * xcal * vector2.i * xcal +
		                                vector2.j * ycal * vector2.j * ycal +
		                                vector2.k * zcal * vector2.k * zcal);
		double normesProduct = normeVector1 * normeVector2;
		double sinAlpha      = 0, cosAlpha = 0;
		
		switch (axesName) {
			case "xy":
				sinAlpha = ((vector1.i * xcal) * (vector2.j * ycal) - (vector1.j * ycal) * (vector2.i * xcal)) /
				           normesProduct;
				cosAlpha = ((vector1.i * xcal) * (vector2.i * xcal) + (vector1.j * ycal) * (vector2.j * ycal)) /
				           normesProduct;
				break;
			case "xz":
				sinAlpha = ((vector1.i * xcal) * (vector2.k * zcal) - (vector1.k * zcal) * (vector2.i * xcal)) /
				           normesProduct;
				cosAlpha = ((vector1.i * xcal) * (vector2.i * xcal) + (vector1.k * zcal) * (vector2.k * zcal)) /
				           normesProduct;
				break;
			case "yz":
				sinAlpha = ((vector1.j * ycal) * (vector2.k * zcal) - (vector1.k * zcal) * (vector2.j * ycal)) /
				           normesProduct;
				cosAlpha = ((vector1.j * ycal) * (vector2.j * ycal) + (vector1.k * zcal) * (vector2.k * zcal)) /
				           normesProduct;
				break;
		}
		if (cosAlpha > 1) {
			cosAlpha = 1;
		} else if (cosAlpha < -1) {
			cosAlpha = -1;
		}
		double acos = Math.acos(cosAlpha);
		double alpha;
		if (sinAlpha < 0) {
			alpha = -acos;
		} else {
			alpha = acos;
		}
		return alpha;
	}
	
	
	/**
	 * @param p
	 * @param q
	 * @param r
	 *
	 * @return
	 */
	int orientation(VoxelRecord p, VoxelRecord q, VoxelRecord r) {
		int turn = (int) ((q.i - p.i) * (r.j - p.j) - (r.i - p.i) * (q.j - p.j));
		return Integer.signum(turn);
	}
	
	
	/** @return  */
	public String getAxes() {
		return axesName;
	}
	
	
	/** @param axes  */
	public void setAxes(String axes) {
		axesName = axes;
	}
	
	
	/** @param voxelRecord  */
	public void setInitialVoxel(VoxelRecord voxelRecord) {
		p0 = voxelRecord;
	}

	/*Soit d notre seuil de distance et C(v, D) le carré de centre v et de rayon d (dans le plan considéré).
	Copier l'image dans le carré C(v, D) dans une petite image I_c
	inverser les zéros et les uns dans I_c
	Mettre v (où plutôt le voxel qui lui correspond qui doit être le centre de I_c) à 1.
	Etiqueter à 2 la composante connexe des 1 qui contient v dans I_c (faire un parcours breadthFirstSerach comme l'autre jour)
	Pour chaque voxel  w du bord de I_c qui est à 2, calculer 
	double angleEntreZeroEt2pi = computeAngle(vectorTest,w-v,calibration) + pi
	Calculer angleEntreZeroEt2piMax le maximum des angles obtenus.
	thresholdAngle = (angleEntreZeroEt2piMax).
	si (thresholdAngle >= pi)
	thresholdAngle -= 2pi */
	
	
	/**
	 * @param image
	 * @param voxelRecord
	 * @param vectorTest
	 * @param calibration
	 * @param distance
	 *
	 * @return
	 */
	private double angleThreshold(double[][] image,
	                              VoxelRecord voxelRecord,
	                              VoxelRecord vectorTest,
	                              Calibration calibration,
	                              double distance) {
		int nbPixelWidth  = (int) (distance / calibration.pixelWidth);
		int nbPixelHeight = (int) (distance / calibration.pixelHeight);
		int x             = (int) voxelRecord.i;
		int y             = (int) voxelRecord.j;
		if (axesName.equals("xz")) {
			y = (int) voxelRecord.k;
			nbPixelHeight = (int) (distance / calibration.pixelDepth);
		} else if (axesName.equals("yz")) {
			x = (int) voxelRecord.j;
			y = (int) voxelRecord.k;
			nbPixelWidth = (int) (distance / calibration.pixelHeight);
			nbPixelHeight = (int) (distance / calibration.pixelDepth);
		}
		if (nbPixelWidth == 0) {
			nbPixelWidth = 1;
		}
		
		if (nbPixelHeight == 0) {
			nbPixelHeight = 1;
		}
		//IJ.log(""+ getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber()+" largeur la distance  " +distance+ "\n  nbPixelWidth "+nbPixelWidth+ "\n nbPixelHeight "+nbPixelHeight+ "\n x "+x+ " y "+y+ " " + calibration.pixelDepth+ " "+calibration.pixelHeight);
		//double angleMax = 0;
		//if(nbPixelWidth>0 && nbPixelHeight>0 ) {
		//  IJ.log(""+ getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber()+" "+nbPixelWidth + " "+nbPixelHeight+" " +x + " " + y);
		List<VoxelRecord> listBoundaryVoxel = getListOfInterestVoxel(image, nbPixelWidth, nbPixelHeight, x, y);
		double            angleMax          = 0;
		for (VoxelRecord record : listBoundaryVoxel) {
			VoxelRecord vectorCourant = new VoxelRecord();
			vectorCourant.setLocation(record.i - nbPixelWidth,
			                          record.j - nbPixelHeight,
			                          0);
			if (axesName.equals("xz")) {
				vectorCourant.setLocation(record.i - nbPixelWidth,
				                          0,
				                          record.k - nbPixelHeight);
			} else if (axesName.equals("yz")) {
				vectorCourant.setLocation(0,
				                          record.j - nbPixelWidth,
				                          record.k - nbPixelHeight);
			}
			double angleEntreZeroEt2pi = computeAngle(vectorTest, vectorCourant, calibration) + PI;
			//IJ.log("AngleTest "+ angleEntreZeroEt2pi );
			if (angleEntreZeroEt2pi > angleMax) {
				angleMax = angleEntreZeroEt2pi;
			}
		}
		if (angleMax > PI) {
			angleMax -= 2 * PI;
		}
		return angleMax;
	}
	
	
	/**
	 * @param image
	 * @param nbPixelWidth
	 * @param nbPixelHeight
	 * @param iInterestVoxel
	 * @param jInterestVoxel
	 *
	 * @return
	 */
	private List<VoxelRecord> getListOfInterestVoxel(double[][] image,
	                                                 int nbPixelWidth,
	                                                 int nbPixelHeight,
	                                                 int iInterestVoxel,
	                                                 int jInterestVoxel) {
		double value     = image[iInterestVoxel][jInterestVoxel];
		int    minWidth  = iInterestVoxel - nbPixelWidth;
		int    maxWidth  = iInterestVoxel + nbPixelWidth;
		int    minHeight = jInterestVoxel - nbPixelHeight;
		int    maxHeight = jInterestVoxel + nbPixelHeight;
		if (minWidth < 0) {
			minWidth = 0;
		}
		if (maxWidth >= image.length) {
			maxWidth = image.length - 1;
		}
		if (minHeight < 0) {
			minHeight = 0;
		}
		if (maxHeight >= image[0].length) {
			maxHeight = image[0].length - 1;
		}
		double[][] iC = new double[nbPixelWidth * 2][nbPixelHeight * 2];
		/*
        IJ.log(" i "+ iInterestVoxel + " j "+jInterestVoxel+"\n");
		IJ.log("Image 1 "+image.length + " x "+image[0].length+"\n");
        IJ.log("minWidth "+minWidth + " maxWidth "+maxWidth+"\n");
        IJ.log("minHeight "+minHeight + " maxHeight "+maxHeight+"\n");
        */
		//IJ.log("HA "   +minWidth+" max "+maxWidth+" iInterestVoxel "+iInterestVoxel+" "+maxHeight+" ers "+iC.length + " nbPixelWidth "+nbPixelWidth +" nbPixelHeight " +nbPixelHeight);
		int k = 0;
		for (iInterestVoxel = minWidth; iInterestVoxel < maxWidth; ++iInterestVoxel) {
			int l = 0;
			for (jInterestVoxel = minHeight; jInterestVoxel < maxHeight; ++jInterestVoxel) {
				//IJ.log("HB "+ iInterestVoxel +"  "+ jInterestVoxel+ " "+image[iInterestVoxel][jInterestVoxel]+  " "+iC[k][l]);
				if (image[iInterestVoxel][jInterestVoxel] == value) {
					iC[k][l] = 0;
				} else {
					iC[k][l] = 1;
				}
				++l;
			}
			++k;
		}
		VoxelRecord voxelRecord = new VoxelRecord();
		voxelRecord.setLocation(nbPixelWidth, nbPixelHeight, 0);
		ConnectedComponents connectedComponents = new ConnectedComponents();
		connectedComponents.setImageTable(iC);
		//IJ.log(""+ getClass().getName()+" L-"+ new Exception().getStackTrace()[0].getLineNumber()+" image  "+iC.length+ " "+voxelRecord._i+ " "+voxelRecord._j + " " +image.length+ " "+image[0].length);
		connectedComponents.computeLabelOfOneObject(1, voxelRecord);
		return connectedComponents.getBoundaryVoxel(2);
	}
	
}