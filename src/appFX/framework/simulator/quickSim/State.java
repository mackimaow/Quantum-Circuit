package appFX.framework.simulator.quickSim;

import java.util.Map;

import appFX.framework.exportGates.ExportedGate;

public interface State {
	public void apply(ExportedGate eg, Map<Integer, Integer> map);
	public int size();
}
