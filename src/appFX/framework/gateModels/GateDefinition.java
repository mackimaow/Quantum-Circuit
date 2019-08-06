package appFX.framework.gateModels;

import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;
import utils.customCollections.immutableLists.ImmutableArray;

public abstract class GateDefinition {
	protected int numberOfRegisters;
	protected ImmutableArray<String> latex;
	protected ImmutableArray<String> userInput;
	private boolean initialize = false;
	
	public GateDefinition(ImmutableArray<String> latex, ImmutableArray<String>  userInput, int numberOfRegisters) {
		this.latex = latex;
		this.userInput = userInput;
		this.numberOfRegisters = numberOfRegisters;
	}
	
	public GateDefinition() {
		this(null, null, -1);
	}
	
	public void initialize(String[] parameters) throws DefinitionEvaluatorException {
		if(initialize) return;
		checkAndInitializeDefinition(parameters);
		initialize = true;
	}
	
	protected abstract void checkAndInitializeDefinition(String[] parameters) throws DefinitionEvaluatorException;
	
	public boolean isInitialized() {
		return initialize;
	}
	
	public ImmutableArray<String> getLatex() {
		return latex;
	}

	public ImmutableArray<String> getUserInput() {
		return userInput;
	}

	public int getNumberOfRegisters() {
		return numberOfRegisters;
	}
}
