package org.antcolony.anthill;

import org.antcolony.ant.IAnt;
import org.antcolony.world.IMarkableWorld;

public interface IAnthill<World extends IMarkableWorld<?, Position, Ant>, Ant extends IAnt<?, Position, ?, ?>, Position> {
	public World getWorld();

	public Position getPosition();

	public Ant createAnt();
}
