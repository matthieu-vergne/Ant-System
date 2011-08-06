package sample.foodSearching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antcolony.world.IMarkableWorld;

public class World implements IMarkableWorld<Double, Coords, Ant> {
	public static final int MAX_MARK = 10;
	private final Double[][] marks;
	private final Integer[][] resources;
	private final Set<Anthill> anthills = new HashSet<Anthill>();
	private final Set<Coords> notAccessiblePositions = new HashSet<Coords>();

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

		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(10, 10), new Coords(14, 10))));
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(14, 10), new Coords(14, 30))));
	}

	public void addResources(Integer amount, Coords position) {
		if (!isAccessible(position)) {
			throw new IllegalArgumentException(position + " not accessible.");
		}

		resources[position.getX()][position.getY()] += amount;
	}

	public boolean isAccessible(Coords position) {
		if (position.getX() < 0 || position.getX() >= resources.length
				|| position.getY() < 0
				|| position.getY() >= resources[0].length) {
			return false;
		} else {
			for (Coords test : notAccessiblePositions) {
				if (test.equals(position)) {
					return false;
				}
			}
			return true;
		}
	}

	@Override
	public Coords[] getAccessiblePositionsAround(Coords center) {
		List<Coords> surroundings = new ArrayList<Coords>() {
			private static final long serialVersionUID = 1L;

			@Override
			public boolean add(Coords position) {
				if (!isAccessible(position)) {
					return false;
				} else {
					return super.add(position);
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
