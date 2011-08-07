package sample.foodSearching;

import java.util.Arrays;
import java.util.Comparator;

import org.antcolony.ant.IAnt;

// TODO review the mark following : when too far, stop in the middle
public class Ant implements IAnt<Marker, Coords, World, Anthill> {
	public static final String MARK_ID = "mark";
	public static final Double MARK_EXPANSION = 0.05;
	public static final Double MARK_SENSIBILITY = 1.0;
	public static final double MARK_AMOUNT = 10 * MARK_SENSIBILITY;
	public static final Double WAVE_SENSIBILITY = 1.0;
	private final Anthill anthill;
	private Coords currentPosition;
	private boolean hasResource;
	private Direction direction = Direction.UNDEFINED;

	public Ant(Anthill anthill, Coords position) {
		this.anthill = anthill;
		this.currentPosition = position;
	}

	@Override
	public World getWorld() {
		return getAnthill().getWorld();
	}

	@Override
	public Anthill getAnthill() {
		return anthill;
	}

	public Double getAnthillDistance() {
		return Coords.distanceBetween(getAnthill().getPosition(),
				getCurrentPosition());
	}

	@Override
	public Coords getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public void goToNextPosition() {
		markCurrentPosition();
		if (isLookingForResource()) {
			currentPosition = getNextInterestingPositionToFood();
			Integer availableResource = getWorld().getResourceAt(
					currentPosition);
			if (availableResource > 0) {
				getWorld().takeResourceAt(currentPosition);
				hasResource = true;
			}
		} else if (isLookingForAnthill()) {
			currentPosition = getNextPositionToAnthill();
			if (getAnthillDistance() == 0) {
				hasResource = false;
			}
		}
	}

	private Coords getNextInterestingPositionToFood() {
		Coords[] positions = getWorld().getAccessiblePositionsAround(
				currentPosition);
		WeightenList<Coords> weightenList = new WeightenList<Coords>();
		for (Coords coords : positions) {
			Double weight = 1.0;
			Double mark = getWorld().getMarkAt(coords).getMark(MARK_ID);
			if (mark > 0) {
				weight = mark * suitDirection(coords);
			}
			weightenList.add(coords, weight);
		}

		Coords coords = weightenList.get(Math.random()
				* weightenList.getTotalWeight());
		direction = Direction.getDirection(getCurrentPosition(), coords);
		return coords;
	}

	private Double suitDirection(Coords coords) {
		Double suit = 0.0;
		if (direction == Direction.BOTTOM) {
			suit += (double) (coords.getX() - getCurrentPosition().getX() + 1) / 2;
		} else if (direction == Direction.UP) {
			suit += (double) (getCurrentPosition().getX() - coords.getX() + 1) / 2;
		} else if (direction == Direction.RIGHT) {
			suit += (double) (coords.getY() - getCurrentPosition().getY() + 1) / 2;
		} else if (direction == Direction.LEFT) {
			suit += (double) (getCurrentPosition().getY() - coords.getY() + 1) / 2;
		} else if (direction == Direction.UNDEFINED) {
			suit += 1;
		}
		return suit;
	}

	private Coords getNextPositionToAnthill() {
		final Coords[] positions = getWorld().getAccessiblePositionsAround(
				getCurrentPosition());
		Arrays.sort(positions, new Comparator<Coords>() {
			@Override
			public int compare(Coords c1, Coords c2) {
				Double w1 = getWorld().getMarkAt(c1).getMark(Anthill.WAVE_ID);
				Double w2 = getWorld().getMarkAt(c2).getMark(Anthill.WAVE_ID);
				return w2.compareTo(w1);
			}
		});
		return positions[0];
	}

	@Override
	public void markCurrentPosition() {
		if (hasResource) {
			Marker mark = new Marker();
			mark.addMark(MARK_ID, MARK_AMOUNT);
			getWorld().addMark(mark, getCurrentPosition());
		}
	}

	@Override
	public boolean isLookingForAnthill() {
		return hasResource;
	}

	@Override
	public boolean isLookingForResource() {
		return !hasResource;
	}
}
