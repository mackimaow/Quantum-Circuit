package appFX.framework.gateModels;

import java.io.Serializable;

import appFX.framework.utils.InputDefinitions;
import appFX.framework.utils.InputDefinitions.ArgDefinition;
import appFX.framework.utils.InputDefinitions.CheckDefinitionRunnable;
import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;
import appFX.framework.utils.InputDefinitions.GroupDefinition;
import appFX.framework.utils.InputDefinitions.MathObject;
import appFX.framework.utils.InputDefinitions.MatrixDefinition;
import appFX.framework.utils.InputDefinitions.ScalarDefinition;
import mathLib.Complex;
import mathLib.Matrix;
import utils.customCollections.immutableLists.ImmutableArray;

public class QuantumGateDefinition extends GateDefinition implements Serializable {
	private static final long serialVersionUID = -4804676651867190072L;

	
	
	public static enum QuantumGateType {
		UNIVERSAL("Universal"), POVM("POVM"), HAMILTONIAN("Hamiltonian"), KRAUS_OPERATORS("Kraus Operators");
		
		private String name;
		
		private QuantumGateType(String name) {
			this.name = name;
		}
		
		@Override
		public String toString() {
			return name;
		}
		
		public static QuantumGateType getByModelName(String s) {
			for(QuantumGateType bmt : QuantumGateType.values())
				if(bmt.name.equalsIgnoreCase(s)) return bmt;
			return null;
		}
	}
	
	private ImmutableArray<MathObject> definitions;
    private QuantumGateType gateType;
	
	public QuantumGateDefinition(QuantumGateType gateType, String ... userInputMatrixDefinitions) {
		this.gateType = gateType;
		this.userInput = new ImmutableArray<>(userInputMatrixDefinitions);
	}
    
    @Override
	protected void checkAndInitializeDefinition(String[] parameters) throws DefinitionEvaluatorException {
    	if(userInput.size() < 0)
    		throw new DefinitionEvaluatorException("This gate has no definitions. There must be at least one definition", -1);
    	RegularGateChecker rgc = new RegularGateChecker();
		GroupDefinition definitions = InputDefinitions.evaluateInput(rgc, parameters, userInput.size(), userInput);
		this.latex = definitions.getLatexRepresentations();
		this.definitions = definitions.getMathDefinitions();
		this.numberOfRegisters = rgc.getNumberRegisters();
	}
    
    public boolean isMeasurement() {
    	switch(gateType) {
		case KRAUS_OPERATORS:
		case POVM:
			return definitions.size() > 1;
		default:
			return false;
    	}
    }
    
    public QuantumGateType getQuantumGateType() {
    	return gateType;
    }
    
    public ImmutableArray<MathObject> getDefinitions() {
    	return definitions;
    }
    
    
    public static String[] checkParameters(String[] parameters, QuantumGateDefinition quantumGateDefinition) {
    	QuantumGateType gateType = quantumGateDefinition.gateType;
    	int definitionSize = quantumGateDefinition.userInput.size();
    	switch(gateType) {
    	case UNIVERSAL:
    		if(definitionSize != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
    		for(String arg : parameters)
				if(arg.equals("U"))
					throw new RuntimeException("Variable \"U\" cannot bed used as a parameter");
    		return parameters;
    	case HAMILTONIAN:
    		if(definitionSize != 1)
				throw new RuntimeException("There should be only one matrix to define this gate model");
    		String[] tempParams = new String[parameters.length + 1];
    		tempParams[0] = "t";
    		int i = 1;
    		for(String param : parameters) {
    			if(param.equals("H") || param.equals("t"))
					throw new RuntimeException("Variable \"" + param + "\" cannot be used as a parameter");
    			tempParams[i++] = param;
    		}
    		return tempParams;
    	case POVM:
    		if(definitionSize < 1)
				throw new RuntimeException("There should be at least one matrix to define this gate model");
    		for(String arg : parameters)
				if(arg.matches("F_\\d")) 
					throw new RuntimeException("Variable " + arg + " cannot bed used as a parameter");
    		return parameters;
    	case KRAUS_OPERATORS:
    		if(definitionSize < 1)
				throw new RuntimeException("There should be at least one matrix to define this gate model");
    		for(String arg : parameters)
				if(arg.matches("K_\\d")) 
					throw new RuntimeException("Variable " + arg + " cannot bed used as a parameter");
    		return parameters;
    	}
    	return null;
    }
    
    private static void checkSize(int rows, int columns, int definitionIndex) throws DefinitionEvaluatorException {
		if(rows != columns) {
			throw new DefinitionEvaluatorException("Number of rows must be equal to number of Columns", definitionIndex);
		} else {
			int size = rows;
			if((size & (size - 1)) != 0 || size < 2)
				throw new DefinitionEvaluatorException("The matrix size must greater than 1 and be some power of 2", definitionIndex);
		}
	}
	
    private static int getNumberOfRegisters (int matrixSize) {
		int i = 0;
		while(((matrixSize >> (++i)) | 1) != 1);
		return i;
	}
	
	private static void checkSize(Matrix<Complex> mat , int definitionIndex) throws DefinitionEvaluatorException{
		checkSize(mat.getRows(), mat.getColumns(), definitionIndex);
	}
	
	private static int getNumberOfRegisters (Matrix<Complex> matrix) {
		return getNumberOfRegisters(matrix.getRows());
	}
	
    public static class RegularGateChecker implements CheckDefinitionRunnable {
		
		private Integer numRegAll = null;
		private Integer numReg = null;
		
		@Override
		public void checkScalarDefinition(ScalarDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
		}

		@Override
		public void checkMatrixDefinition(MatrixDefinition definition, int definitionIndex)   throws DefinitionEvaluatorException{
			checkSize(definition.getMatrix(), definitionIndex);
			
			numReg = getNumberOfRegisters(definition.getMatrix());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}

		@Override
		public void checkArgDefinition(ArgDefinition definition, int definitionIndex)  throws DefinitionEvaluatorException {
			if(!definition.isMatrix())
				throw new DefinitionEvaluatorException("Definition should not be scalar", definitionIndex);
			
			checkSize(definition.getRows(), definition.getColumns(), definitionIndex);
			
			
			numReg = getNumberOfRegisters(definition.getRows());
			
			if(numRegAll == null)
				numRegAll = numReg;
			else if (numRegAll != numReg)
				throw new DefinitionEvaluatorException("Each matrix must be the same size", definitionIndex);
		}
		
		public int getNumberRegisters() {
			return numRegAll;
		}
    }
}
