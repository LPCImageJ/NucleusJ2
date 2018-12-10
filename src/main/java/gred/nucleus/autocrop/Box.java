package gred.nucleus.autocrop;

/**
*  Represents the information relative to a Box in the image space (e.g. a bounding box for an object in an image).
*  A box is represented by the minimal values (xmin, ymin, zmin) and the maximal values (xmax, ymax, zmax) for each coordinate.
*
 * @author Tristan Dubos and Axel Poulet
*
*/
public class Box {
	/** The coordinate x min of the Box */
	private short m_xMin;
	/** The coordinate x max of the Box*/
	private short m_xMax;
	/** The coordinate y min of the Box*/
	private short m_yMin;
	/** The coordinate y max of the Box*/
	private short m_yMax;
	/** The coordinate z min of the Box*/
	private short m_zMin;
	/** The coordinate z max of the Box*/
	private short m_zMax;
	
	/**
	 *   Constructor
	 *   
	 * @param xMin: coordinate x min of the Box
	 * @param xMax: coordinate x max of the Box
	 * @param yMin: coordinate y min of the Box
	 * @param yMax: coordinate y max of the Box
	 * @param zMin: coordinate z min of the Box
	 * @param zMax: coordinate z max of the Box
	 */
	public Box(short xMin,short xMax,short yMin,short yMax,short zMin,short zMax) {
		m_xMin = xMin;
		m_xMax = xMax;
		m_yMin = yMin;
		m_yMax = yMax;
		m_zMin = zMin;
		m_zMax = zMax;
	}
	
	/**
	 * Returns minimal value of the x coordinate in the box
	 * @return the m_xMin
	 */
	public short getXMin(){
		return m_xMin;
	}
	
	/** @param xMin the m_xMin to set*/
	public void setXMin(short xMin){
		this.m_xMin = xMin;
	}

	/**
	 * Returns maximal value of the x coordinate in the box
	 * @return the m_xMax
	 */
	public short getXMax(){
		return m_xMax;
	}

	/** @param xMax the m_xMax to set */
	public void setXMax(short xMax){ this.m_xMax = xMax;}

	/**
	 * Returns minimal value of the y coordinate in the box
	 * @return the m_yMin
	 */
	public short getYMin(){
		return m_yMin;
	}

	/** @param yMin the m_yMin to set*/
	public void setYMin(short yMin){
		this.m_yMin = yMin;
	}

	/**
	 * Returns maximal value of the y coordinate in the box
	 * @return the m_yMax
	 */
	public short getYMax(){
		return m_yMax;
	}

	/** @param yMax the m_yMax to set*/
	public void setYMax(short yMax) {
		this.m_yMax = yMax;
	}

	/**
	 * returns minimal value of the z coordinate in the box
	 * @return the m_zMin
	 */
	public short getZMin() {
		return m_zMin;
	}

	/** @param zMin the m_zMin to set */	
	public void setZMin(short zMin) {
		this.m_zMin = zMin;
	}

	/**
	 * Returns maximal value of the z coordinate in the box
	 * @return the m_zMax
	 */
	public short getZMax() {
		return m_zMax;
	}

	/** @param zMax the m_zMax to set*/
	public void setZMax(short zMax) {
		this.m_zMax = zMax;
	}
}
