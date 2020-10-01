package appFX.framework.simulator.quickSim;

import java.util.Map;

import appFX.framework.exportGates.ExportedGate;

public class QuantumState implements State {
	
	private StateMatrix stateMatrix;
	
	public QuantumState (int numQubits) {
		this.stateMatrix = new StateMatrix(numQubits);
	}
	
	@Override
	public void apply(ExportedGate eg, Map<Integer, Integer> map) {
		
	}
	
	@Override
	public int size() {
		return this.stateMatrix.getNumQubits();
	}
	
}
