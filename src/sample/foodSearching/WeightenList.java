package sample.foodSearching;

import java.util.HashMap;
import java.util.Map;

public class WeightenList<Item> {
	private Map<Item, Double> map = new HashMap<Item, Double>();

	public void add(Item item) {
		add(item, 1.0);
	}

	public void add(Item item, Double weight) {
		if (map.containsKey(item)) {
			weight += map.get(item);
		}
		set(item, weight);
	}

	public void set(Item item, Double weight) {
		map.put(item, weight);
	}

	public Double getTotalWeight() {
		Double total = 0.0;
		for (Double weight : map.values()) {
			total += weight;
		}
		return total;
	}

	public Item get(Double weight) {
		for (Map.Entry<Item, Double> entry : map.entrySet()) {
			weight -= entry.getValue();
			if (weight < 0) {
				return entry.getKey();
			}
		}
		return null;
	}

	public Double getWeight(Item item) {
		for (Map.Entry<Item, Double> entry : map.entrySet()) {
			if (item.equals(entry.getKey())) {
				return entry.getValue();
			}
		}
		return 0.0;
	}
	
}
