package appFX.framework.simulator.quickSim;

public class StateMatrix {
	private final int numQubits;
	
	public StateMatrix (int numQubits) {
		this.numQubits = numQubits;
	}
	
	public int getNumQubits() {
		return numQubits;
	}
}
