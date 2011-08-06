package sample.foodSearching;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antcolony.world.IMarkableWorld;

public class World implements IMarkableWorld<Double, Coords, Ant> {
	public static final int MAX_MARK = 10;
	private final Double[][] marks;
	private final Integer[][] resources;
	private final Set<Anthill> anthills = new HashSet<Anthill>();

	public World() {
		marks = new Double[50][50];
		for (int x = 0; x < marks.length; x++) {
			for (int y = 0; y < marks[x].length; y++) {
				marks[x][y] = 0.0;
			}
		}

		resources = new Integer[50][50];
		for (int x = 0; x < resources.length; x++) {
			for (int y = 0; y < resources[x].length; y++) {
				resources[x][y] = 0;
			}
		}

		resources[10][10] = 500;
		resources[40][40] = 500;
	}

	@Override
	public Coords[] getAccessiblePositionsAround(Coords center) {
		List<Coords> surroundings = new ArrayList<Coords>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean add(Coords coords) {
				if (coords.getX() < 0 || coords.getX() >= resources.length
						|| coords.getY() < 0
						|| coords.getY() >= resources[0].length) {
					return false;
				} else {
					return super.add(coords);
				}
			}
		};
		surroundings.add(new Coords(center.getX() - 1, center.getY()));
		surroundings.add(new Coords(center.getX() + 1, center.getY()));
		surroundings.add(new Coords(center.getX(), center.getY() + 1));
		surroundings.add(new Coords(center.getX(), center.getY() - 1));
		return surroundings.toArray(new Coords[0]);
	}

	@Override
	public Double getMarkAt(Coords position) {
		return marks[position.getX()][position.getY()];
	}

	@Override
	public void addMark(Double mark, Coords position) {
		int x = position.getX();
		int y = position.getY();
		marks[x][y] = Math.min(marks[x][y] + mark, MAX_MARK);
	}

	@Override
	public void evaporateMarks() {
		for (int x = 0; x < marks.length; x++) {
			for (int y = 0; y < marks[x].length; y++) {
				marks[x][y] = marks[x][y] * 0.99;
			}
		}
	}

	public int getWidth() {
		return resources.length;
	}

	public int getHeight() {
		return resources[0].length;
	}

	public Integer getResourceAt(Coords position) {
		return resources[position.getX()][position.getY()];
	}

	public void takeResourceAt(Coords position) {
		resources[position.getX()][position.getY()] -= 1;
	}
	
	public Anthill createAnthill(Coords position) {
		Anthill anthill = new Anthill(this, position);
		anthills.add(anthill);
		return anthill;
	}
	
	public Anthill[] getAnthills() {
		return anthills.toArray(new Anthill[0]);
	}
}
