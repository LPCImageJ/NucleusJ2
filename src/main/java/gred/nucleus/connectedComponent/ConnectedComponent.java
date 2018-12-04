/******************************************************************************\
*     Copyright (C) 2016 by Rémy Malgouyres                                    * 
*     http://malgouyres.org                                                    * 
*     File: ConnectedComponent.java                                            * 
*                                                                              * 
* The program is distributed under the terms of the GNU General Public License * 
*                                                                              * 
\******************************************************************************/ 

package gred.nucleus.connectedComponent;

import gred.nucleus.utils.Voxel;

import java.util.ArrayList;

import ij.ImagePlus;
import ij.ImageStack;
import ij.measure.Calibration;
import gred.nucleus.componentRemoval.ComponentRemovalNone;
import gred.nucleus.componentRemoval.ComponentRemovalPredicate;
import gred.nucleus.connectedComponent.ComponentInfo;


/**
 *
 * Allows connected components labeling in a binary image.
 * This abstract class has derived classes to label components in 2D or 3D images.
 * Some filtering criteria for filtering the connected components are supported :
 * <ul>
 * 	<li>Minimal volume for the components (taking into account the calibration)</li>
 * 	<li>Remove connected components that touch the boundary of the image.</li>
 * </ul>
 * An option in the filtering method allows to modify the input image to set random grey levels on each component.
 
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public abstract class ConnectedComponent {
	/**
	 * Input Image. 
	 * The image can be modified by the filtering in the filterComponents() method
	 */
	protected ImagePlus m_inputImage;

	/**
	 * Color of the foreground in the input binary image
	 */
	protected int m_foregroundColor;

	/**
	 * Components Information (cardinality, label, voxel representative, etc.)
	 */
	protected ArrayList<ComponentInfo> m_compInfo;
	
	/**
	 * Array containing the label of each voxel
	 */
	protected int[][][] m_labels;
	
	/**
	 * Volume of a voxel (used for component size thresholding)
	 */
	protected double m_voxelVolume;

	/**
	 * Initializes the fields of this instance (to be called in derived classes constructors)
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image ip
	 */
	protected ConnectedComponent(ImagePlus inputImage, int foregroundColor) {
		this.m_inputImage = inputImage;
		Calibration cal = m_inputImage.getCalibration();
		this.m_voxelVolume =  cal.pixelDepth*cal.pixelWidth*cal.pixelHeight;

		//System.out.println("vol vx"+m_voxelVolume);
		this.m_foregroundColor = foregroundColor;
		this.m_labels = new int[this.m_inputImage.getWidth()][this.m_inputImage.getHeight()][this.m_inputImage.getNSlices()];
		for (int i=0 ; i<this.m_inputImage.getWidth() ; i++) {
			for (int j=0 ; j<this.m_inputImage.getHeight() ; j++) {
				for (int k=0 ; k<this.m_inputImage.getNSlices() ; k++) {
					this.m_labels[i][j][k] = 0;
				}
			}
		}
		this.m_compInfo = new ArrayList<ComponentInfo>();    
	}

	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D).
	 *  the connected components are not labeled. Please call doConnectedComponent().
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @return an instance of a concrete derived class for ConnectedComponent
	 */
	public static ConnectedComponent getConnectedComponent(ImagePlus inputImage, int foregroundColor) {
		if (inputImage.getNSlices() <= 1)
			return new ConnectedComponent2D(inputImage, foregroundColor);

		return new ConnectedComponent3D(inputImage, foregroundColor);
	}
	
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D) and labels the components.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdVoxelVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws Exception in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */
	public static ConnectedComponent getLabelledConnectedComponent( ImagePlus inputImage,  int foregroundColor,
			boolean removeBorderComponent, double thresholdVoxelVolume,	boolean setRandomColors) throws Exception {
		ConnectedComponent cc;
		if (inputImage.getNSlices() <= 1)
			cc = new ConnectedComponent2D(inputImage, foregroundColor);
		else
			cc = new ConnectedComponent3D(inputImage, foregroundColor);

		cc.doLabelConnectedComponent();
		cc.filterComponents(removeBorderComponent, thresholdVoxelVolume, setRandomColors);
		return cc;
	}
	
	/**
	 * Constructs a ConnectedComponent derived class instance with relevant dimension (2D or 3D) and labels the components.
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 * </ul>
	 * @param inputImage : input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdVoxelVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param removalPredicate a predicate according to which components should be filtered out
	 * @param keepPredicate true if we should keep the components with a voxel satisfying removalPredicate, and false if we should remove the components with a voxel satisfying removalPredicate 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 * @return an instance of a concrete derived class for ConnectedComponent
	 * @throws Exception  in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */
	public static ConnectedComponent getLabelledConnectedComponent(ImagePlus inputImage, int foregroundColor,
			boolean removeBorderComponent, double thresholdVoxelVolume,	ComponentRemovalPredicate removalPredicate,
			boolean keepPredicate,	boolean setRandomColors ) throws Exception {
		ConnectedComponent cc;
		if (inputImage.getNSlices() <= 1)
			cc = new ConnectedComponent2D(inputImage, foregroundColor);
		else
			cc = new ConnectedComponent3D(inputImage, foregroundColor);
		cc.doLabelConnectedComponent();
		cc.filterComponents(removeBorderComponent, thresholdVoxelVolume, removalPredicate, keepPredicate, setRandomColors);
		return cc;
	}

	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 * @param x first coordinate of the pixel
	 * @param y second coordinate of the pixel
	 * @return the label of the input voxel (0 if not in any connected component)
	 */
	public int getLabel(int x, int y) {
		return this.m_labels[x][y][0];
	}

	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 * @return the label of the input voxel (0 if not in any connected component)
	 */
	public int getLabel(int x, int y, int z) {
		return this.m_labels[x][y][z];
	}

	/**
	 * retrieves the label of a voxel (after calling doComponents)
	 * @param x first coordinate of the voxel
	 * @param y second coordinate of the voxel
	 * @param z third coordinate of the voxel
	 * @param label the label of the input voxel (0 if not in any connected component)
	 */
	protected void setLabel(int x, int y, int z, int label) {
		this.m_labels[x][y][z] = label;
	}

	/**
	 * Retrieves the informations about a component for its label
	 * may return null if the component does not exist or has been erased because below volume threshold or on the border.
	 * @param label the component label
	 * @return the ComponentInfo instance of the component with the considered label. returns null if the component info is undefined
	 */
	public ComponentInfo getComponentInfo(short label) {
		try {
			ComponentInfo ci = this.m_compInfo.get(label-1);
			if (ci.getnumberOfPoints() ==0)
				return null;
			return ci;
		}catch (Exception e){
			return null;
		}
	}

	/**
	 * Retrieve a collection of voxels, with one voxel in each (possibly filtered) connected component.
	 * @return the array of voxel representatives of components
	 */
	public ArrayList<Voxel> getVoxelRepresentants() {
		ArrayList<Voxel> tabVoxels = new ArrayList<Voxel>();
		for (ComponentInfo ci : this.m_compInfo) {
			if (ci.getnumberOfPoints() > 0)
				tabVoxels.add(ci.getRepresentant());
		}
		return tabVoxels;
	}

	/**
	 * labels the connected components of the input image (attribute this.m_ip)
	 * @throws Exception in case the number of connected components exceeds the Short.MAX_VALUE (32767)
	 */ 	
	abstract void doLabelConnectedComponent() throws Exception;


	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 */
	protected void filterComponents( boolean removeBorderComponent, double thresholdComponentVolume, boolean setRandomColors) {
		filterComponents( removeBorderComponent, thresholdComponentVolume, new ComponentRemovalNone(), true, setRandomColors);
	}

	/**
	 * Filters the image components according to two criteria:
	 * <ul>
	 *    <li>Possibly remove the components which are on the edge of the image</li>
	 *    <li>Possibly remove the components with size bellow some threshold</li>
	 *    <li>Possibly keep (or remove) the components with a voxel satisfying a predicate</li>
	 * </ul>
	 * @param removeBorderComponent true if the components which are on the edge of the image should be removed by filtering
	 * @param thresholdComponentVolume minimal volume for filtering (taking into account the calibration) for the components. 0 if no minimal volume is required 
	 * @param removalPredicate a predicate according to which components should be filtered out
	 * @param keepPredicate true if we should keep only the components with at least one voxel satisfying removalPredicate, and false if we should remove the components with at least one voxel satisfying removalPredicate 
	 * @param setRandomColors true if the colors of the original image should be set according to the components labels.
	 */
	protected void filterComponents( boolean removeBorderComponent, double thresholdComponentVolume,
			ComponentRemovalPredicate removalPredicate, boolean keepPredicate, boolean setRandomColors) {

	    ArrayList<Boolean> existsVoxelSatisfyingPredicate = new ArrayList<Boolean>();
		for (int i = 0 ; i < this.m_compInfo.size() ; ++i)
			existsVoxelSatisfyingPredicate.add(Boolean.valueOf(false));

				// Check the predicate
		Voxel voxelToTest = new Voxel();
		for (voxelToTest.setX((short)0); voxelToTest.getX() < this.m_inputImage.getWidth(); voxelToTest.incrementCoord(0)) {
			for (voxelToTest.setY((short)0); voxelToTest.getY() < this.m_inputImage.getHeight(); voxelToTest.incrementCoord(1)) {
				for (voxelToTest.setZ((short)0); voxelToTest.getZ() < this.m_inputImage.getNSlices(); voxelToTest.incrementCoord(2))	{
					// get the voxel's label
					int label = getLabel(voxelToTest.getX(), voxelToTest.getY(), voxelToTest.getZ());
					if (label > 0) { // if not a background voxel
						ComponentInfo ci = this.m_compInfo.get(label-1);
						// test the predicate
						if(removalPredicate.keepVoxelComponent(voxelToTest, ci))
							existsVoxelSatisfyingPredicate.set(label-1, Boolean.valueOf(true));
					}
				}
			}
		}
		
		// if the keep predicate is true for at least one voxel
		// and we should remove
		// the components with a voxel satisfying removalPredicate 
		// or
		// if the keep predicate is false for all the voxels
		// and we should keep only
		// the components with a voxel satisfying removalPredicate 
		for (int i = 0 ; i < this.m_compInfo.size() ; ++i) {
			if (((!existsVoxelSatisfyingPredicate.get(i).booleanValue()) && keepPredicate) || (existsVoxelSatisfyingPredicate.get(i).booleanValue() && !keepPredicate))
				// remove the component
				this.m_compInfo.get(i).setNumberOfPoints(0);
		}

		int thresholdNVoxel = (int)(thresholdComponentVolume/this.m_voxelVolume);
		ArrayList<Integer> newLabels = new ArrayList<Integer>(this.m_compInfo.size());
		ArrayList<ComponentInfo> newTabComponents = new ArrayList<ComponentInfo>();
		short componentsCount = 0;
		// For each label
		for (int label=1 ; label<=this.m_compInfo.size() ; label++) {
			ComponentInfo ci = this.m_compInfo.get(label-1);
			// If the component survives the filtering criteria
			if (ci != null && ci.getnumberOfPoints() > 0 &&	ci.getnumberOfPoints() >= thresholdNVoxel && ((!removeBorderComponent) || !ci.isOnTheeBorder())) {
				componentsCount++;
				// old label/new label correspondence
				newLabels.add(new Integer(componentsCount));
				// register the component in the final array
				newTabComponents.add(ci);
			}
			else{
				ci.setNumberOfPoints(0);
				newLabels.add(new Integer(0));
			}
		}
		ArrayList<Double> componentsColors = new ArrayList<Double>(newTabComponents.size());
		for (int i=0 ; i<newTabComponents.size() ; i++)
			componentsColors.add(new Double(100 + Math.random()*(255 - 100)));
		ImageStack imgP = m_inputImage.getStack();
		for (int i = 0; i < this.m_inputImage.getWidth(); ++i ) {
			for (int j = 0; j < this.m_inputImage.getHeight(); ++j) {
				for (int k = 0; k < this.m_inputImage.getNSlices(); ++k) {
					int label = getLabel(i, j, k);
					// if not a background voxel and component not removed
					if (label > 0 && newLabels.get(label-1) > 0){
						int newLabel = newLabels.get(label-1); // get new label from old label
						ComponentInfo ci = newTabComponents.get(newLabel-1);
						ci.setLabel(newLabel); // Set new label for the component
						setLabel(i, j, k, newLabel); // Set new label for the voxel
						// Possibly change the color on the whole component
						if (setRandomColors)
							imgP.setVoxel(i, j, k, componentsColors.get(newLabel-1).intValue());
					}
					else{
						setLabel(i, j, k, 0); // Set label to 0 (remove the voxel)
						imgP.setVoxel(i, j, k, 0);
					}
				}
			}
		}
		this.m_compInfo = newTabComponents;
	}

	
	/**
	 * @return a human readable string representation of this instance
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Connected components of the image "+ this.m_inputImage.getTitle()+"\n");
		for (ComponentInfo compInfo : this.m_compInfo)
			builder.append(compInfo + "\n");
		return builder.toString();
	}
	
	/**
	 * retrieves the number of components (after calling doComponents)
	 * @return the number of components detected.
	 */
	
	public int getNumberOfComponents() {
		return this.m_compInfo.size();
	}
} // end of class 