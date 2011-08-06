package org.antcolony.world;

import org.antcolony.ant.IAnt;

public interface IMarkableWorld<Mark, Position, Ant extends IAnt<Mark, Position, ?, ?>> {
	public Position[] getAccessiblePositionsAround(Position center);

	public Mark getMarkAt(Position position);

	public void addMark(Mark mark, Position position);

	public void evaporateMarks();
}
