package org.antcolony.world;

import org.antcolony.ant.IAnt;

public interface IMarkableWorld<Mark> {
	public void addAnt(IAnt ant, IPosition position);

	public void getPositionsAround(IPosition center);

	public void getMarkAt(IPosition position);

	public void addMark(Mark mark, IPosition position);

	public void evaporateMarks();
}
