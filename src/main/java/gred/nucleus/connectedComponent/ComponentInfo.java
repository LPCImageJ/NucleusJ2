/*****************************************************************************\
 Copyright (C) 2016 by Rémy Malgouyres                                    *
 http://malgouyres.org                                                    *
 File: ComponentInfo.java                                                 *
 *
 The program is distributed under the terms of the GNU General Public License *
 *
 \******************************************************************************/

package gred.nucleus.connectedComponent;

import gred.nucleus.utils.Voxel;

/**
 * Represents the informations relative to a connected component in a binary image.
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */

public class ComponentInfo {
	
	/**
	 * Label (ID) of the connected component (i.e. color in the labels image array)
	 */
	private int m_label;
	
	/**
	 * Cardinality of the connected component. Can be zero if the connected component has been filtered out (e.g.
	 * threshold size or border components exclusion)
	 */
	private int m_numberOfPoints;
	
	/**
	 * Voxel representative of the component (one voxel in the component) Currently, this representative has minimal
	 * depth Z TODO extend usage using a comparison predicate possibly other that comparing depth.
	 */
	private Voxel m_voxelRepresentant;
	
	/**
	 * Flag indicating whether the connected component touches the edge of the image. (allows filtering out connected
	 * component which touch the edge of the image.)
	 */
	private boolean m_componentOnTheBorder;
	
	/**
	 * Constructor
	 *
	 * @param label                label of the connected component (i.e. color in the labels image array)
	 * @param numberOfPoints       (initial) cardinality of the connected component.
	 * @param voxelRepresentant    (initial) voxel representative of the component (one voxel in the component)
	 * @param componentOnTheBorder Flag (inital) indicating whether the connected component touches the edge of the
	 *                             image.
	 */
	public ComponentInfo(int label, int numberOfPoints, Voxel voxelRepresentant, boolean componentOnTheBorder) {
		this.m_label = label;
		this.m_numberOfPoints = numberOfPoints;
		this.m_voxelRepresentant = voxelRepresentant;
		this.m_componentOnTheBorder = componentOnTheBorder;
	}
	
	/**
	 * Getter.
	 *
	 * @return the label of the component
	 */
	public int getLabel() {
		return this.m_label;
	}
	
	/**
	 * Setter  for the label of the component
	 *
	 * @param label the label to use
	 */
	public void setLabel(int label) {
		this.m_label = label;
	}
	
	/**
	 * Getter
	 *
	 * @return the cardinality of the component
	 */
	public int getnumberOfPoints() {
		return this.m_numberOfPoints;
	}
	
	/**
	 * Increments the cardinality
	 */
	public void incrementNumberOfPoints() {
		this.m_numberOfPoints++;
	}
	
	/**
	 * Setter
	 *
	 * @param numberOfPoints the cardinality to set
	 */
	public void setNumberOfPoints(int numberOfPoints) {
		this.m_numberOfPoints = numberOfPoints;
	}
	
	/**
	 * Getter
	 *
	 * @return returns the component's flag indicating whether the component is on the border.
	 */
	public boolean isOnTheeBorder() {
		return this.m_componentOnTheBorder;
	}
	
	/**
	 * Getter
	 *
	 * @return the voxel representative of the component (one voxel in the component)
	 */
	public Voxel getRepresentant() {
		return this.m_voxelRepresentant;
	}
	
	/**
	 * Sets to true the flag indicating whether the component is on the border.
	 */
	public void setOnTheeBorder() {
		this.m_componentOnTheBorder = true;
	}
	
	
	/**
	 * @return a human readable string representation of this instance
	 *
	 * @see Object#toString()
	 */
	@Override
	public String toString() {
		return "Component label : " + this.m_label + ", Number of points : " + this.m_numberOfPoints;
	}
	
} // end of class ComponentInfo

