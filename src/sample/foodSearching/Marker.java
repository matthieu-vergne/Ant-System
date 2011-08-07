package sample.foodSearching;

import java.util.HashMap;
import java.util.Map;

public class Marker {
	private static final Map<String, Double> maxMark = new HashMap<String, Double>();
	private static final Map<String, Double> minMark = new HashMap<String, Double>();
	private static final Map<String, Double> expansionPower = new HashMap<String, Double>();
	private final Map<String, Double> marks = new HashMap<String, Double>();

	public Marker() {
		initStatic();
	}

	private void initStatic() {
		minMark.put(Ant.MARK_ID, Ant.MARK_SENSIBILITY);
		maxMark.put(Ant.MARK_ID, World.MAX_MARK);
		expansionPower.put(Ant.MARK_ID, Ant.MARK_EXPANSION);

		minMark.put(Anthill.WAVE_ID, Ant.WAVE_SENSIBILITY);
		maxMark.put(Anthill.WAVE_ID, Anthill.MAX_WAVE);
		expansionPower.put(Anthill.WAVE_ID, Anthill.WAVE_EXPANSION);
	}

	public void addMark(String id, Double amount) {
		if (marks.containsKey(id)) {
			amount += marks.get(id);
		}
		amount = Math.min(amount, getMaxMarkFor(id));
		if (amount < getMinMarkFor(id)) {
			amount = 0.0;
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

	public String[] getIDs() {
		return marks.keySet().toArray(new String[0]);
	}

	public static Double getExpansionPowerFor(String id) {
		if (expansionPower.containsKey(id)) {
			return expansionPower.get(id);
		} else {
			return 0.0;
		}
	}

	public static Double getMinMarkFor(String id) {
		if (minMark.containsKey(id)) {
			return minMark.get(id);
		} else {
			return 0.0;
		}
	}

	public static Double getMaxMarkFor(String id) {
		if (maxMark.containsKey(id)) {
			return maxMark.get(id);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}
}
