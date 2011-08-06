package sample.foodSearching;

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
}
