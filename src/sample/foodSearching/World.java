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
import org.cellularautomaton.rule.RuleFactory;
import org.cellularautomaton.space.SpaceBuilder;
import org.cellularautomaton.state.AbstractStateFactory;

// TODO manage several types of mark and add a new anthill
public class World implements IMarkableWorld<Marker, Coords, Ant> {
	public static final Double MAX_MARK = 10 * Ant.MARK_AMOUNT;
	private Integer[][] resources;
	private final Set<Anthill> anthills = new HashSet<Anthill>();
	private final Set<Coords> notAccessiblePositions = new HashSet<Coords>();
	private CellularAutomaton<Marker> waveField;

	public World() {
		initWaveField();
		initResources();
		initObstacles();
	}

	private void initWaveField() {
		SpaceBuilder<Marker> builder = new SpaceBuilder<Marker>();
		builder.setStateFactory(new AbstractStateFactory<Marker>() {
			@Override
			public List<Marker> getPossibleStates() {
				return Arrays.asList(new Marker());
			}
		});
		builder.setRule(new IRule<Marker>() {

			@Override
			public Marker calculateNextStateOf(ICell<Marker> cell) {
				Marker state = cell.getCurrentState();
				for (ICell<Marker> neighbour : cell.getAllCellsAround()) {
					for (String id : neighbour.getCurrentState().getIDs()) {
						state.addMark(id, 0.0);
					}
				}
				for (String id : state.getIDs()) {
					Double expansionPower = Marker.getExpansionPowerFor(id);
					if (expansionPower > 0) {
						Double intensity = (1 - expansionPower)
								* state.getMark(id);
						for (ICell<Marker> neighbour : cell.getAllCellsAround()) {
							Double environment = neighbour.getCurrentState()
									.getMark(id);
							intensity += expansionPower * environment / 4;
						}
						state.addMark(id, intensity - state.getMark(id));
					}
				}
				return state;
			}
		});
		builder.createNewSpace();
		builder.addDimension(getWidth(), false);
		builder.addDimension(getHeight(), false);
		builder.finalizeSpace();

		waveField = new CellularAutomaton<Marker>(builder.getSpaceOfCell());
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

		for (ICell<Marker> cell : waveField.getSpace().getAllCells()) {
			for (Coords position : notAccessiblePositions) {
				int[] coords = cell.getCoords().getAll();
				if (position.getX() == coords[0]
						&& position.getY() == coords[1]) {
					cell.setRule(new RuleFactory<Marker>()
							.getStaticRuleInstance());
					cell.setCurrentState(new Marker());
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
	public Marker getMarkAt(Coords position) {
		return getWaveCellAt(position).getCurrentState();
	}

	@Override
	public void addMark(Marker mark, Coords position) {
		for (String id : mark.getIDs()) {
			getWaveCellAt(position).getCurrentState().addMark(id,
					mark.getMark(id));
		}
	}

	private ICell<Marker> getWaveCellAt(Coords position) {
		ICell<Marker> cell = waveField.getSpace().getOrigin()
				.getRelativeCell(position.getX(), position.getY());
		return cell;
	}

	@Override
	public void evaporateMarks() {
		// moved to the CA itself
		throw new IllegalStateException("Not needed method, remove its using.");
	}

	public void evolve() {
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
		ICell<Marker> cell = getWaveCellAt(position);
		cell.setRule(new RuleFactory<Marker>().getStaticRuleInstance());
		Marker state = new Marker();
		state.addMark(Anthill.WAVE_ID, Anthill.MAX_WAVE);
		cell.setCurrentState(state);
		return anthill;
	}

	public Anthill[] getAnthills() {
		return anthills.toArray(new Anthill[0]);
	}

	public Marker[][] getWaveField() {
		Marker[][] field = new Marker[getWidth()][getHeight()];
		for (ICell<Marker> cell : waveField.getSpace().getAllCells()) {
			int[] coords = cell.getCoords().getAll();
			field[coords[0]][coords[1]] = cell.getCurrentState();
		}
		return field;
	}

	public Boolean isMarkLimit(String id, Coords position) {
		Double mark = getMarkAt(position).getMark(id);
		if (mark == 0.0) {
			for (Coords neighbour : getAccessiblePositionsAround(position)) {
				Double mark2 = getMarkAt(neighbour).getMark(id);
				if (mark2 > 0.0) {
					return true;
				}
			}
		}
		return false;
	}
}
