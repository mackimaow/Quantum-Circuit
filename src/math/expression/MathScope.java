package math.expression;

import java.util.Hashtable;

import math.expression.Expression.EvaluateExpressionException;
import math.expression.Function.FunctionID;
import math.mathValue.MathValue;

public class MathScope {
	private final MathScope enclosingScope;
	private Hashtable<FunctionID, Function> functionSet = new Hashtable<>();
	private Hashtable <String, Variable> variableSet = new Hashtable<>();
	
	public MathScope () {
		this(null);
	}
	
	public MathScope (MathScope enclosingScope) {
		this.enclosingScope = enclosingScope;
	}
	
	public void addToSet(MathScope scope) {
		functionSet.putAll(scope.functionSet);
		variableSet.putAll(scope.variableSet);
	}
	
	public void addVariable(Variable v) {
		variableSet.put(v.getName(), v);
	}
	
	public void removeVariable(Variable v) {
		variableSet.remove(v.getName());
	}
	
	public void addFunctionDefinition (Function function) {
		functionSet.put(function.getID(), function);
	}
	
	public void removeFunctionDefinition(Function function) {
		functionSet.remove(function.getID());
	}
	
	public MathScope getEnclosingScope () {
		return enclosingScope;
	}
	
	public Variable getVariable (String name) {
		Variable variable = variableSet.get(name);
		if(variable == null && enclosingScope != null)
			variable = enclosingScope.getVariable(name);
		return variable;
	}
	
	public Function getFunction (String name, int numParams) {
		Function function = functionSet.get(new FunctionID(name, numParams));
		if(function == null && enclosingScope != null)
			function = enclosingScope.getFunction(name, numParams);
		return function;
	}
	
	public MathValue computeFunction(String name, Expression ... params) throws EvaluateExpressionException {
		Function function = functionSet.get(new FunctionID(name, params.length));
		if(function == null)
			if(enclosingScope != null)
				return enclosingScope.computeFunction(this, name, params);
			else throw new FunctionNotDefinedException(name, params.length);
		return function.compute(this, this, params);
	}
	
	private MathValue computeFunction(MathScope callingScope, String name, Expression ... params) throws EvaluateExpressionException {
		Function function = getFunction(name, params.length);
		if(function == null)
			throw new FunctionNotDefinedException(name, params.length);
		return function.compute(this, callingScope, params);
	}
	
	public MathValue computeVariable (String name) throws EvaluateExpressionException {
		Variable variable = getVariable(name);
		if(variable == null)
			throw new VariableNotDefinedException(name);
		return variable.getValue(this);
	}
	
	@SuppressWarnings("serial")
	public static class FunctionNotDefinedException extends RuntimeException {
		private String functionName;
		private int numParams;
		
		public FunctionNotDefinedException (String functionName, int numParams) {
			super("Function \"" + functionName + "\" with " + numParams + " parameters is not defined.");
			this.functionName = functionName;
			this.numParams = numParams;
		}
		
		public String getFunctionName() {
			return functionName;
		}
		
		public int getNumParams() {
			return numParams;
		}
		
	}
	
	@SuppressWarnings("serial")
	public static class VariableNotDefinedException extends RuntimeException {
		private String variableName;
		
		public VariableNotDefinedException (String variableName) {
			super("Variable \"" + variableName + "\" is not defined.");
			this.variableName = variableName;
		}
		
		public String getVariableName() {
			return variableName;
		}
	}
	
}
