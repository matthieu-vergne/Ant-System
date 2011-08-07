package sample.foodSearching;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.antcolony.ant.IAnt;

// TODO review the mark following : when too far, stop in the middle
public class Ant implements IAnt<Marker, Coords, World, Anthill> {
	public static final String MARK_ID = "mark";
	public static final Double MARK_EXPANSION = 0.05;
	public static final Double MARK_SENSIBILITY = 1.0;
	public static final double MARK_AMOUNT = 50 * MARK_SENSIBILITY;
	public static final Double WAVE_SENSIBILITY = 1.0;
	private final Anthill anthill;
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
		//return followStrongestMark();
		return followMoreOrLessStrongestMark();
	}

	private Coords followMoreOrLessStrongestMark() {
		Coords[] positions = getWorld().getAccessiblePositionsAround(
				currentPosition);
		WeightenList<Coords> weightenList = new WeightenList<Coords>();

		{
			for (Coords coords : positions) {
				Double mark = Anthill.MAX_WAVE
						- getWorld().getMarkAt(coords).getMark(Anthill.WAVE_ID);
				mark++;
				mark /= Anthill.MAX_WAVE;
				weightenList.add(coords, mark);
			}
		}

		{
			for (Coords coords : positions) {
				Double mark = 1 + getWorld().getMarkAt(coords).getMark(
						Ant.MARK_ID);
				if (mark > 1) {
					Double weight = weightenList.getWeight(coords);
					mark /= Math.pow(weight, 10);
					weightenList.add(coords, mark);
				}
				System.out.println(weightenList.getWeight(coords));
			}
		}

		return weightenList.get(Math.random() * weightenList.getTotalWeight());
	}

	private Coords followStrongestMark() {
		Coords[] positions = getWorld().getAccessiblePositionsAround(
				currentPosition);
		positions = filterTooFarPositions(positions);

		Double strongestMark = getStrongestMarkIn(positions);
		if (strongestMark > MARK_SENSIBILITY) {
			positions = filterDifferentlyMarkedPositions(positions,
					strongestMark);

			// consider only the farest position from the anthill
			Arrays.sort(positions, new Comparator<Coords>() {
				@Override
				public int compare(Coords c1, Coords c2) {
					Double d1 = getWorld().getMarkAt(c1).getMark(MARK_ID);
					Double d2 = getWorld().getMarkAt(c2).getMark(MARK_ID);
					return d1.compareTo(d2);
				}
			});
			return positions[0];
		} else {
			// take a random position
			if (positions.length > 0) {
				return positions[new Random().nextInt(positions.length)];
			} else {
				return currentPosition;
			}
		}
	}

	private Coords[] filterDifferentlyMarkedPositions(Coords[] positions,
			Double strongestMark) {
		Set<Coords> strongestPositions = new HashSet<Coords>();
		for (Coords position : positions) {
			if (isSimilarValue(MARK_SENSIBILITY, strongestMark, getWorld()
					.getMarkAt(position).getMark(MARK_ID))) {
				strongestPositions.add(position);
			}
		}
		positions = strongestPositions.toArray(new Coords[0]);
		return positions;
	}

	private Coords[] filterTooFarPositions(Coords[] positions) {
		Set<Coords> managedPositions = new HashSet<Coords>();
		for (Coords position : positions) {
			if (getWorld().getMarkAt(position).getMark(Anthill.WAVE_ID) > WAVE_SENSIBILITY) {
				managedPositions.add(position);
			}
		}
		positions = managedPositions.toArray(new Coords[0]);
		return positions;
	}

	private Double getStrongestMarkIn(Coords... positions) {
		Double strongestMark = 0.0;
		for (Coords position : positions) {
			strongestMark = Math.max(strongestMark,
					getWorld().getMarkAt(position).getMark(MARK_ID));
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
		Marker mark = new Marker();
		if (hasResource) {
			mark.addMark(MARK_ID, MARK_AMOUNT);
		} else {
			// mark.addMark(Anthill.WAVE_ID, Anthill.MAX_WAVE
			// / getAnthill().getAntCounter()/10);
		}
		getWorld().addMark(mark, getCurrentPosition());
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
