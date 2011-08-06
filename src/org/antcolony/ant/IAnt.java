package org.antcolony.ant;

import org.antcolony.world.IPosition;

public interface IAnt {
	public IPosition getPreviousPosition();

	public IPosition getCurrentPosition();

	public IPosition goToNextPosition();

	public void markCurrentPosition();
}
