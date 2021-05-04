package autocroptest;

import org.apache.calcite.util.Pair;

import java.util.Collections;
import java.util.List;
import java.util.Map;


public class AutocropResult {
	private int cropNb;
	private List<CropResult> coordinates;
	
	public int getCropNb() {
		return cropNb;
	}
	
	public void setCropNb(int cropNb) {
		this.cropNb = cropNb;
	}
	
	public void setCoordinates(List<CropResult> coordinates){
		this.coordinates = coordinates;
	}
	
	public List<CropResult> getCoordinates(){
		return Collections.unmodifiableList(coordinates);
	}
	
}
