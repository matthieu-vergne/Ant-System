package sample.foodSearching;

import java.util.HashSet;
import java.util.Set;

import org.antcolony.anthill.IAnthill;

public class Anthill implements IAnthill<World, Ant, Coords> {
	public static final String WAVE_ID = "wave";
	public static final Double MAX_WAVE = 1e8;
	public static final Double WAVE_EXPANSION = 1.0;
	private final World world;
	private final Coords position;
	private final Set<Ant> ants = new HashSet<Ant>();
	private Integer resources = 0;

	public Anthill(World world, Coords position) {
		this.world = world;
		this.position = position;
	}

	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Coords getPosition() {
		return position;
	}

	@Override
	public Ant createAnt() {
		Ant ant = new Ant(this, position);
		ants.add(ant);
		return ant;
	}

	public void killAnt(Ant ant) {
		if (!ants.contains(ant)) {
			throw new IllegalArgumentException("Unknown ant.");
		}

		ants.remove(ant);
	}

	public Ant[] getAnts() {
		return ants.toArray(new Ant[0]);
	}

	public int getAntCounter() {
		return ants.size();
	}
	
	public void putResources() {
		resources++;
		System.out.println(resources);
	}
}
