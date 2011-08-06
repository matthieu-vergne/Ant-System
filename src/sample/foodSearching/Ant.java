package sample.foodSearching;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.antcolony.ant.IAnt;

public class Ant implements IAnt<Double, Coords, World, Anthill> {
	public static final Double MARK_SENSIBILITY = 1.0;
	private Anthill anthill;
	private Coords currentPosition;
	private boolean hasResource;

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

	@Override
	public Coords getCurrentPosition() {
		return currentPosition;
	}

	@Override
	public void goToNextPosition() {
		if (isLookingForResource()) {
			currentPosition = getNextInterestingPositionToFood();
			Integer availableResource = getWorld().getResourceAt(
					currentPosition);
			if (availableResource > 0) {
				getWorld().takeResourceAt(currentPosition);
				hasResource = true;
			}
		} else if (isLookingForAnthill()) {
			markCurrentPosition();
			currentPosition = getNextPositionToAnthill();
			if (getAnthill().getPosition().equals(currentPosition)) {
				hasResource = false;
			}
		}
	}

	private Coords getNextInterestingPositionToFood() {
		Coords[] positions = getWorld().getAccessiblePositionsAround(
				currentPosition);
		Double strongestMark = getStrongestMarkIn(positions);
		if (strongestMark > MARK_SENSIBILITY) {
			// consider only positions with similar marks
			Set<Coords> strongestPositions = new HashSet<Coords>();
			for (Coords position : positions) {
				if (isSimilarValue(MARK_SENSIBILITY, strongestMark, getWorld()
						.getMarkAt(position))) {
					strongestPositions.add(position);
				}
			}
			positions = strongestPositions.toArray(new Coords[0]);

			// consider only the farest position from the anthill
			final Coords anthillPosition = getAnthill().getPosition();
			Arrays.sort(positions, new Comparator<Coords>() {
				@Override
				public int compare(Coords c1, Coords c2) {
					Double d1 = Coords.distanceBetween(anthillPosition, c1);
					Double d2 = Coords.distanceBetween(anthillPosition, c2);
					return d2.compareTo(d1);
				}
			});
			return positions[0];
		} else {
			// take a random position
			return positions[new Random().nextInt(positions.length)];
		}
	}

	private Double getStrongestMarkIn(Coords... positions) {
		Double strongestMark = 0.0;
		for (Coords position : positions) {
			strongestMark = Math.max(strongestMark,
					getWorld().getMarkAt(position));
		}
		return strongestMark;
	}

	private static boolean isSimilarValue(final Double threshold,
			final Double m1, final Double m2) {
		if (m1 < threshold && m2 < threshold) {
			return true;
		} else {
			Double average = (m1 + m2) / 2;
			Double similarity = m1 / average;
			if (similarity > 1) {
				similarity = 1 / similarity;
			}
			return similarity > 0.5;
		}
	}

	private Coords getNextPositionToAnthill() {
		final Coords anthillPosition = getAnthill().getPosition();
		final Coords[] positions = getWorld().getAccessiblePositionsAround(
				getCurrentPosition());
		Arrays.sort(positions, new Comparator<Coords>() {
			@Override
			public int compare(Coords c1, Coords c2) {
				Double d1 = Coords.distanceBetween(anthillPosition, c1);
				Double d2 = Coords.distanceBetween(anthillPosition, c2);
				return d1.compareTo(d2);
			}
		});
		return positions[0];
	}

	@Override
	public void markCurrentPosition() {
		getWorld().addMark(2 * MARK_SENSIBILITY, getCurrentPosition());
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
