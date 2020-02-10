package appFX.framework.gateModels;

import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;

public class BasicGateModel extends SingleGateModel {
	private static final long serialVersionUID = -3974442774420594973L;
	
	public static final String GATE_MODEL_EXTENSION =  "gm";
	
	private final int numberOfRegisters;
    private final ClassicalGateDefinition classicalDefinition;
    private final QuantumGateDefinition quantumDefinition;
    
    public BasicGateModel(String location, String name, String symbol, String description, String[] parameters, ClassicalGateDefinition classicalDefinition, QuantumGateDefinition quantumDefinition) 
    		throws DefinitionEvaluatorException {
		super(location, name, symbol, description, getComputingType(classicalDefinition, quantumDefinition), initAndGetParameters(parameters, classicalDefinition, quantumDefinition));
		this.classicalDefinition = classicalDefinition;
		this.quantumDefinition = quantumDefinition;
		this.numberOfRegisters = getNumberOfRegisters(classicalDefinition, quantumDefinition);
	}
    
    private static GateComputingType getComputingType(ClassicalGateDefinition classicalDefinition, QuantumGateDefinition quantumDefinition) {
    	if(classicalDefinition != null) {
    		if(quantumDefinition != null)
    			return GateComputingType.CLASSICAL_AND_QUANTUM;
    		return GateComputingType.CLASSICAL;
    	}
    	return GateComputingType.QUANTUM;
    }
    
    private static String[] initAndGetParameters(String[] parameters, ClassicalGateDefinition classicalDefinition, QuantumGateDefinition quantumDefinition) throws DefinitionEvaluatorException {
    	if(classicalDefinition != null)
    		classicalDefinition.initialize(parameters);
    	if(quantumDefinition != null) {
    		quantumDefinition.initialize(parameters);
    		return QuantumGateDefinition.checkParameters(parameters, quantumDefinition);
    	}
    	return parameters;
    }
    
    private static int getNumberOfRegisters(ClassicalGateDefinition classicalDefinition, QuantumGateDefinition quantumDefinition) throws DefinitionEvaluatorException {
    	if(classicalDefinition != null) {
        	int classicalNum = classicalDefinition.getNumberOfRegisters();
    		if(quantumDefinition != null) {
    			int quantumNum = quantumDefinition.getNumberOfRegisters();
    			if(quantumNum < classicalNum)
    				throw new DefinitionEvaluatorException("The amount of classical registers exceeds the amount of quantum registers", 0);
    		}
    		return classicalNum;
    	}
    	return quantumDefinition.getNumberOfRegisters();
    }
    
    private BasicGateModel(String location, String name, String symbol, String description, String[] arguments, BasicGateModel old) {
    	super(location, name, symbol, description, old.getComputingType(), arguments);
    	this.numberOfRegisters = old.numberOfRegisters;
    	this.classicalDefinition = old.classicalDefinition;
    	this.quantumDefinition = old.quantumDefinition;
    }
	
	@Override
	public boolean isPreset() {
		return false;
	}
	
	@Override
	public int getNumberOfRegisters() {
		return numberOfRegisters;
	}
	
	
	public boolean isMultiQubitGate () {
		return getNumberOfRegisters() > 1;
	}
    
	public ClassicalGateDefinition getClassicalGateDefinition() {
		return classicalDefinition;
	}
	
	public QuantumGateDefinition getQuantumGateDefinition() {
		return quantumDefinition;
	}
    
    @SuppressWarnings("serial")
	public static class InvalidGateModelMatrixException extends RuntimeException {
		public InvalidGateModelMatrixException (String reason) {
			super (reason);
		}
	}

	@Override
	public String getExtString() {
		return GATE_MODEL_EXTENSION;
	}

	@Override
	public GateModel shallowCopyToNewName(String location, String name, String symbol, String description, GateComputingType computingType, String ... arguments) {
		return new BasicGateModel(location, name, symbol, description, arguments, this);
	}
	
	
}
