package org.antcolony.ant;

import org.antcolony.anthill.IAnthill;
import org.antcolony.world.IMarkableWorld;

public interface IAnt<Mark, Position, World extends IMarkableWorld<Mark, Position, ?>, Anthill extends IAnthill<World, ?, Position>> {
	public World getWorld();

	public Anthill getAnthill();

	public Position getCurrentPosition();

	public void goToNextPosition();

	public void markCurrentPosition();

	public boolean isLookingForResource();

	public boolean isLookingForAnthill();

}
