/*****************************************************************************\
 Copyright (C) 2016 by Rémy Malgouyres                                    *
 http://malgouyres.org                                                    *
 File: ConnectedComponent2D.java                                          *
 *
 The program is distributed under the terms of the GNU General Public License *
 *
 \******************************************************************************/

package gred.nucleus.connectedComponent;


import ij.ImagePlus;
import ij.process.ImageProcessor;
import gred.nucleus.utils.Voxel;


import java.util.LinkedList;


/**
 * Class dedicated to connected components labeling in 3D images
 * 
 * @author Remy Malgouyres, Tristan Dubos and Axel Poulet
 * 
 * TODO : This class has not been tested and should probably be worked out before use.
 * 
 */
public class ConnectedComponent2D extends ConnectedComponent {

	/**
	 * Constructor from an ImageWrapper representing a binary image and the foreground color in this image
	 * @param inputImage input (probably binary) image, the components of which to compute.
	 * @param foregroundColor label of the 1's in the input image inputImage
	 */
	protected ConnectedComponent2D(ImagePlus inputImage, int foregroundColor) {
		super(inputImage, foregroundColor);
	}


	/**
	 * Performs a breadth first search of the connected component for labeling
	 * The method goes over all the voxels in the connected component of the object
	 * of the initial voxel.
	 * The method sets the fields of the ComponentInfo parameter to record the status of the component.
	 * 
	 * @param voxelShort initial voxel of the connected component
	 * @param currentLabel label to set for the voxels of the component
	 * @throws Exception if the FIFO's size exceeds the number of preallocated voxels
	 */
	protected void breadthFirstSearch(	Voxel voxelShort, short currentLabel, ComponentInfo componentInfo) throws Exception {

		// FIFO for the Breadth First Search algorithm
		// LinkedList is more efficient than ArrayList 
		// because poll() (alias Remove(0)) is constant time !!!
		LinkedList<Voxel> voxelFifo = new LinkedList<>();

		// add initial voxel to the FIFO
		voxelFifo.add(voxelShort);
		while (!voxelFifo.isEmpty()) {
			// Retrieve and remove the head of the FIFO
			Voxel polledVoxelShort = voxelFifo.poll();
			short iV = polledVoxelShort.getX();
			short jV = polledVoxelShort.getY();

			//			freeVoxel();

			// Determine the neighborhood taking into account the image's boundaries
			short imin, imax, jmin, jmax;
			if (iV-1 >= 0)
				imin = (short)(iV-1);
			else{
				imin = 0;
				componentInfo.setOnTheeBorder();
			}

			if (jV-1 >= 0)
				jmin = (short)(jV-1);
			else{
				jmin = 0;
				componentInfo.setOnTheeBorder();
			}

			if (iV+1 < this.m_inputImage.getWidth())
				imax = (short)(iV+1);
			else{
				imax = (short)(this.m_inputImage.getWidth()-1);
				componentInfo.setOnTheeBorder();
			}

			if (jV+1 < this.m_inputImage.getHeight())
				jmax = (short)(jV+1);
			else{
				jmax = (short)(this.m_inputImage.getHeight()-1);
				componentInfo.setOnTheeBorder();
			}

			ImageProcessor imgProc = m_inputImage.getProcessor();
			// For each neighbor :
			for (short ii = imin ; ii <= imax ; ii++) {
				for (short jj = jmin ; jj <= jmax; jj++) {
					// If the neighbor (different from VoxelRecordShort) is a 1 and not labeled
					if ((getLabel(ii, jj, 0) == 0) && (imgProc.get(ii, jj) == this.m_foregroundColor)) {
						// Set the voxel's label
						setLabel(ii, jj, 0, currentLabel); 
						componentInfo.incrementNumberOfPoints(); // increment component's cardinality					
						voxelFifo.add(new Voxel(ii, jj, (short)0)); // add to FIFO
					}
				}
			}
		}
	}

	/**
	 *  labels the connected components of the input image (attribute m_ip)
	 */ 
	@Override
	public void doLabelConnectedComponent() {
		short currentLabel = 0;
		ImageProcessor imgProc = m_inputImage.getProcessor();
		for (short i = 0; i < this.m_inputImage.getWidth(); i++) {
			for (short j = 0; j < this.m_inputImage.getHeight(); j++) {
				if (imgProc.getPixel(i, j) == this.m_foregroundColor && getLabel(i, j, 0) == 0) {
					currentLabel++;
					this.m_labels[i][j][0] = currentLabel;
					ComponentInfo componentInfo = new ComponentInfo( currentLabel, 1, new Voxel(i, j, (short) 0), false);
					try {
						breadthFirstSearch( new Voxel(i, j, (short)0), currentLabel, componentInfo);
					}catch (Exception e){
						e.printStackTrace();
						System.exit(0);
					}
					this.m_compInfo.add(componentInfo);
				}
			}
		}
	}

} // end of class

