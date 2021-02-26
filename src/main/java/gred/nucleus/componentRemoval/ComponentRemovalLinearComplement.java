/*
 * Copyright (C) 2016 by Rémy Malgouyres
 * http://malgouyres.org
 * File: ComponentRemovalLinearComplement.java
 *
 * The program is distributed under the terms of the GNU General Public License
 *
 */

package gred.nucleus.componentRemoval;

import gred.nucleus.connectedComponent.ComponentInfo;
import gred.nucleus.utils.Voxel;


/**
 * This class is intended to implement the predicate on voxels and connected components to keep all components which are
 * outside a thick plane. The thick plane's equation is:
 * <p> z <= m_xCoefficient*x + m_yCoefficient*y + m_constantCoefficient
 * <p> or
 * <p> z > m_thickness + m_xCoefficient*x + m_yCoefficient*y + m_constantCoefficient
 *
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 */
public class ComponentRemovalLinearComplement implements ComponentRemovalPredicate {
	private double m_xCoefficient;
	private double m_yCoefficient;
	private double m_constantCoefficient;
	private double m_thickness;
	
	
	/**
	 * @param xCoefficient        first coefficient of the plane's equation
	 * @param yCoefficient        second coefficient of the plane's equation
	 * @param constantCoefficient third coefficient of the plane's equation
	 * @param thickness           thickness of the plane
	 */
	public ComponentRemovalLinearComplement(double xCoefficient,
	                                        double yCoefficient,
	                                        double constantCoefficient,
	                                        double thickness) {
		this.m_xCoefficient = xCoefficient;
		this.m_yCoefficient = yCoefficient;
		this.m_constantCoefficient = constantCoefficient;
		this.m_thickness = thickness;
	}
	
	
	/**
	 *
	 */
	@Override
	public boolean keepVoxelComponent(Voxel voxel, ComponentInfo componentInfo) {
		double zValue =
				this.m_xCoefficient * voxel.getX() + this.m_yCoefficient * voxel.getY() + this.m_constantCoefficient;
		return ((voxel.getZ() < zValue) || (voxel.getZ() >= zValue + this.m_thickness));
	}
	
}
