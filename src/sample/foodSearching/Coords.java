package sample.foodSearching;

import java.util.ArrayList;
import java.util.List;

public class Coords {
	private final int x;
	private final int y;

	public Coords(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Coords) {
			Coords coords = (Coords) obj;
			return getX() == coords.getX() && getY() == coords.getY();
		} else {
			return false;
		}
	}

	public static Double distanceBetween(Coords c1, Coords c2) {
		Integer dx = c2.getX() - c1.getX();
		Integer dy = c2.getY() - c1.getY();
		return Math.hypot(dx, dy);
	}

	public static Coords[] generateLine(Coords start, Coords end) {
		List<Coords> positions = new ArrayList<Coords>();
		while (!start.equals(end)) {
			positions.add(start);
			start = closestTo(end, new Coords(start.getX() + 1, start.getY()),
					new Coords(start.getX() - 1, start.getY()), new Coords(
							start.getX(), start.getY() + 1),
					new Coords(start.getX(), start.getY() - 1), new Coords(
							start.getX() + 1, start.getY() + 1), new Coords(
							start.getX() - 1, start.getY() - 1), new Coords(
							start.getX() - 1, start.getY() + 1), new Coords(
							start.getX() + 1, start.getY() - 1));
		}
		positions.add(end);
		return positions.toArray(new Coords[0]);
	}

	public static Coords closestTo(Coords reference, Coords... positions) {
		if (positions == null || positions.length == 0) {
			return null;
		} else {
			Coords closest = positions[0];
			for (Coords position : positions) {
				if (distanceBetween(reference, position) < distanceBetween(
						reference, closest)) {
					closest = position;
				}
			}
			return closest;
		}
	}

	@Override
	public String toString() {
		return "(" + x + "," + y + ")";
	}
}
