package sample.foodSearching;

public enum Direction {
	UNDEFINED, LEFT, RIGHT, UP, BOTTOM;

	public static Direction getDirection(Coords from, Coords to) {
		int dx = to.getX() - from.getX();
		int dy = to.getX() - from.getX();
		if (Math.abs(dx) > Math.abs(dy)) {
			return dx > 0 ? RIGHT : LEFT;
		}
		else {
			return dy > 0 ? BOTTOM : UP;
		}
	}
}
