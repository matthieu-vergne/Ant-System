package sample.foodSearching;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.antcolony.world.IMarkableWorld;
import org.cellularautomaton.CellularAutomaton;
import org.cellularautomaton.cell.ICell;
import org.cellularautomaton.rule.IRule;
import org.cellularautomaton.space.SpaceBuilder;

import sample.foodSearching.cellularautomaton.WaveFactory;

// TODO manage several types of mark and add a new anthill
public class World implements IMarkableWorld<Double, Coords, Ant> {
	public static final Double MAX_MARK = 2 * Ant.MARK_SENSIBILITY;
	private Double[][] marks;
	private Integer[][] resources;
	private final Set<Anthill> anthills = new HashSet<Anthill>();
	private final Set<Coords> notAccessiblePositions = new HashSet<Coords>();
	private CellularAutomaton<Double> waveField;

	public World() {
		initWaveField();
		initMarks();
		initResources();
		initObstacles();
	}

	private void initWaveField() {
		SpaceBuilder<Double> builder = new SpaceBuilder<Double>();
		builder.setStateFactory(new WaveFactory());
		builder.setRule(new IRule<Double>() {

			@Override
			public Double calculateNextStateOf(ICell<Double> cell) {
				Double intensity = 0.0;
				for (ICell<Double> neighbour : cell.getAllCellsAround()) {
					Double state = neighbour.getCurrentState();
					intensity += Math.pow(state, Anthill.WAVE_EXPANSION);
				}
				intensity /= 4;
				intensity = Math.pow(intensity, 1.0 / Anthill.WAVE_EXPANSION);
				return intensity;
			}
		});
		builder.createNewSpace();
		builder.addDimension(getWidth(), false);
		builder.addDimension(getHeight(), false);
		builder.finalizeSpace();

		waveField = new CellularAutomaton<Double>(builder.getSpaceOfCell());
		waveField.setDependencyConsidered(false);
	}

	private void initObstacles() {
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(5, 10), new Coords(8, 10))));
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(8, 10), new Coords(8, 20))));
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(3, 20), new Coords(8, 20))));
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(20, 0), new Coords(20, 20))));
		notAccessiblePositions.addAll(Arrays.asList(Coords.generateLine(
				new Coords(30, 30), new Coords(50, 35))));

		IRule<Double> notAccessibleRule = new IRule<Double>() {

			@Override
			public Double calculateNextStateOf(ICell<Double> cell) {
				return 0.0;
			}
		};
		for (ICell<Double> cell : waveField.getSpace().getAllCells()) {
			for (Coords position : notAccessiblePositions) {
				int[] coords = cell.getCoords().getAll();
				if (position.getX() == coords[0]
						&& position.getY() == coords[1]) {
					cell.setRule(notAccessibleRule);
				}
			}
		}
	}

	private final void initResources() {
		resources = new Integer[getWidth()][getHeight()];
		for (int x = 0; x < resources.length; x++) {
			for (int y = 0; y < resources[x].length; y++) {
				resources[x][y] = 0;
			}
		}
	}

	private void initMarks() {
		marks = new Double[getWidth()][getHeight()];
		for (int x = 0; x < marks.length; x++) {
			for (int y = 0; y < marks[x].length; y++) {
				marks[x][y] = 0.0;
			}
		}
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
				marks[x][y] = marks[x][y] * 0.999;
				if (marks[x][y] < Ant.MARK_SENSIBILITY) {
					marks[x][y] = 0.0;
				}
			}
		}
	}

	public void evolve() {
		evaporateMarks();
		waveField.doStep();
	}

	public int getWidth() {
		return 50;
	}

	public int getHeight() {
		return 50;
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
		ICell<Double> cell = waveField.getSpace().getOrigin()
				.getRelativeCell(position.getX(), position.getY());
		cell.setRule(new IRule<Double>() {

			@Override
			public Double calculateNextStateOf(ICell<Double> cell) {
				return Anthill.MAX_WAVE;
			}
		});
		return anthill;
	}

	public Anthill[] getAnthills() {
		return anthills.toArray(new Anthill[0]);
	}

	public Double[][] getWaveField() {
		Double[][] field = new Double[getWidth()][getHeight()];
		for (ICell<Double> cell : waveField.getSpace().getAllCells()) {
			int[] coords = cell.getCoords().getAll();
			field[coords[0]][coords[1]] = cell.getCurrentState();
		}
		return field;
	}

	public Double getWaveAt(Coords position) {
		return waveField.getSpace().getOrigin()
				.getRelativeCell(position.getX(), position.getY())
				.getCurrentState();
	}
}
