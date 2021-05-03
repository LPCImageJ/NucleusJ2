package autocroptest;

import gred.nucleus.autocrop.Box;


public class CropResult {
	private int cropNumber;
	private int channel;
	private int xStart, yStart,	zStart;
	private int width, height, depth;
	
	private Box box;
	
	public CropResult(int cropNumber, int channel, int xStart, int yStart, int zStart, int width, int height, int depth) {
		this.cropNumber = cropNumber;
		this.channel = channel;
		this.xStart = xStart;
		this.yStart = yStart;
		this.zStart = zStart;
		this.width = width;
		this.height = height;
		this.depth = depth;
		
		box = new Box((short) xStart,
		              (short) (xStart+width),
		              (short) yStart,
		              (short) (yStart+height),
		              (short) zStart,
		              (short) (zStart+depth)
		);
	}
	
	public Box getBox() { return box; }
	public int getCropNumber() { return cropNumber; }
}
