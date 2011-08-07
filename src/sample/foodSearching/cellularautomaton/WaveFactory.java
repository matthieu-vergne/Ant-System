package sample.foodSearching.cellularautomaton;

import java.util.Arrays;
import java.util.List;

import org.cellularautomaton.state.AbstractStateFactory;

public class WaveFactory extends AbstractStateFactory<Double> {

	@Override
	public List<Double> getPossibleStates() {
		return Arrays.asList(0.0, 1.0, 2.0, 3.0, 4.0, 5.0);
	}

}
