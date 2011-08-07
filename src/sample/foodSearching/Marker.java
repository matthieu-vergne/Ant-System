package sample.foodSearching;

import java.util.HashMap;
import java.util.Map;

public class Marker {
	private final Map<String, Double> marks = new HashMap<String, Double>();
	private final Map<String, Boolean> isEvaporating = new HashMap<String, Boolean>();

	public void addMark(String id, Double amount) {
		if (marks.containsKey(id)) {
			amount += marks.get(id);
		}
		marks.put(id, amount);
	}

	public Double getMark(String id) {
		if (marks.containsKey(id)) {
			return marks.get(id);
		} else {
			return 0.0;
		}
	}
	
	public Boolean isEvaporating(String id) {
		if (isEvaporating.containsKey(id)) {
			return isEvaporating.get(id);
		} else {
			return false;
		}
	}
	
	public void evaporate() {
		for(String id : marks.keySet()) {
			if (isEvaporating(id)) {
				marks.put(id, marks.get(id)*0.999);
			}
		}
	}
	
	public String[] getIDs() {
		return marks.keySet().toArray(new String[0]);
	}
}
