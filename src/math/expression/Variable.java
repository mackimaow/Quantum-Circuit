package math.expression;

import math.expression.Expression.EvaluateExpressionException;
import math.mathValue.MathValue;

public abstract class Variable {
	public static final String NONE = null;
	
	private final String name;
	private final MathValueTypeSet type;
	private final String latexFormat;
	
	public Variable (String name, MathValueTypeSet type, String latexFormat) {
		this.name = name;
		this.type = type;
		this.latexFormat = latexFormat;
	}
	
	public String getName() {
		return name;
	}
	
	public MathValueTypeSet getType() {
		return type;
	}
	
	public String getLatex() {
		if(latexFormat == NONE)
			return name;
		return latexFormat;
	}
	
	public abstract MathValue getValue(MathScope group) throws EvaluateExpressionException;
	public abstract MathValueTypeSet getTypeSet();
	
	
	public static class ExpressionDefinedVariable extends Variable {
		private Expression definition;
		
		public ExpressionDefinedVariable(String name, MathValueTypeSet type, String latexFormat, Expression definition) {
			super(name, type, latexFormat);
			this.definition = definition;
		}
		
		public ExpressionDefinedVariable(String name, MathValueTypeSet type, Expression definition) {
			this(name, type, NONE, definition);
		}
		
		@Override
		public MathValue getValue(MathScope bodyScope) throws EvaluateExpressionException {
			return definition.compute(bodyScope);
		}

		@Override
		public MathValueTypeSet getTypeSet() {
			return definition.getTypeSet();
		}
	}
	
	public static class ConcreteVariable extends Variable {
		private MathValue value;
		private MathValueTypeSet typeSet;
		
		public ConcreteVariable(String name, MathValueTypeSet type, String latexFormat, MathValue value) {
			super(name, type, latexFormat);
			this.value = value;
			typeSet = MathValueTypeSet.getMathValueTypeSet(value);
		}
		
		public ConcreteVariable(String name, MathValueTypeSet type, MathValue value) {
			this(name, type, NONE, value);
		}

		@Override
		public MathValue getValue(MathScope bodyScope) {
			return value;
		}

		@Override
		public MathValueTypeSet getTypeSet() {
			return typeSet;
		}
		
	}
}
