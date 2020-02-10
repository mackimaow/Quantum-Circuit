package appFX.framework.gateModels;

public abstract class SingleGateModel extends GateModel {
	private static final long serialVersionUID = -774517717036818830L;

	public SingleGateModel(String location, String name, String symbol, String description,
			GateComputingType gateComputingType, String[] parameters) {
		super(location, name, symbol, description, gateComputingType, parameters);
	}
	

	public boolean isCircuitBoard() {
		return false;
	}
	
}
