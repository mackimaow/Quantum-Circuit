package math.expression;

import java.util.Iterator;
import java.util.List;
import java.util.Objects;

import math.expression.Expression.EvaluateExpressionException;
import math.expression.MathParser.ParseException;
import math.expression.Variable.ConcreteVariable;
import math.mathValue.MathValue;
import utils.customCollections.Pair;
import utils.customCollections.immutableLists.ArrayListWrapper;
import utils.customCollections.immutableLists.ImmutableArray;

public abstract class Function {
	
	private final FunctionID id;
	private final FunctionLatexFormat functionLatexFormat;
	private final Pair<String, MathValueTypeSet>[] params;
	private final MathValueTypeSet outputType;
	
	@SafeVarargs
	public Function(String name, FunctionLatexFormat functionLatexFormat, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params) {
		this(new FunctionID(name, params.length), functionLatexFormat, outputType, params);
	}
	
	@SafeVarargs
	public Function(FunctionID functionID, FunctionLatexFormat functionLatexFormat, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params){
		this.id = functionID;
		this.functionLatexFormat = functionLatexFormat;
		this.params = params;
		this.outputType = outputType;
	}
	
	public String getName () {
		return id.name;
	}
	
	public int numArgs() {
		return params.length;
	}
	
	FunctionID getID() {
		return id;
	}
	
	public String getLatex(String ... latexParameters) {
		return getLatex(new ArrayListWrapper<>(latexParameters));
	}
	
	public String getLatex(List<String> latexParametersList) {
		if(functionLatexFormat == FunctionLatexFormat.NONE) {
			String latex = " \\mathit{ " + id.name + " } ";
			latex += " \\left( ";
			
			if(!latexParametersList.isEmpty()) {
				latex += latexParametersList.get(0);
				for(int i = 1; i < latexParametersList.size(); i++)
					latex += " , " + latexParametersList.get(i);
			}
			
			latex += " \\right) ";
			
			return latex;
		} else {
			String latex = "";
			
			for(Object obj : functionLatexFormat) {
				if(FunctionLatexFormat.isParam(obj)) {
					int index = FunctionLatexFormat.getParamNumber(obj);
					latex += latexParametersList.get(index);
				} else {
					latex += obj.toString();
				}
			}
			
			return latex;
		}
	}
	
	public ImmutableArray<Pair<String, MathValueTypeSet>> getParams() {
		return new ImmutableArray<Pair<String, MathValueTypeSet>> (params);
	}
	
	public MathValueTypeSet getOutputType() {
		return outputType;
	}
	
	public FunctionLatexFormat getLatexFormat() {
		return functionLatexFormat;
	}
	
	public abstract MathValue compute(MathScope setDefinedBody, MathScope callingScope, Expression ... expressions)  throws EvaluateExpressionException;
	
	public static class ExpressionDefinedFunction extends Function {
		private final Expression definition;
		
		
		@SafeVarargs
		public ExpressionDefinedFunction(String expression, String name, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params) throws ParseException {
			this(expression, name, FunctionLatexFormat.NONE, outputType, params);
		}
		
		@SafeVarargs
		public ExpressionDefinedFunction(String expression, String name, FunctionLatexFormat format, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params) throws ParseException {
			super(name, format, outputType, params);
			this.definition = new Expression(expression);
		}
		
		
		@Override
		public MathValue compute(MathScope bodyScope, MathScope callingScope, Expression ... expressions) throws EvaluateExpressionException {
			MathScope paramDefinitons = new MathScope(bodyScope);
			int i = 0;
			for(Pair<String, MathValueTypeSet> param : getParams())
				paramDefinitons.addVariable(new ConcreteVariable(param.first(), param.second(), expressions[i++].compute(callingScope)));
			return definition.compute(paramDefinitons);
		}
	}
	
	public static class ConcreteFunction extends Function {
		private final FunctionDefinition definition;
		
		@SafeVarargs
		public ConcreteFunction (String name, FunctionDefinition definition, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params) {
			this(name, FunctionLatexFormat.NONE, definition, outputType, params);
		}
		
		@SafeVarargs
		public ConcreteFunction (String name, FunctionLatexFormat format, FunctionDefinition definition, MathValueTypeSet outputType, Pair<String, MathValueTypeSet> ... params) {
			super(name, format, outputType, params);
			this.definition = definition;
		}
		
		public MathValue compute(MathScope bodyScope, MathScope callingScope, Expression ... expressions) throws EvaluateExpressionException {
			return definition.compute(bodyScope, callingScope, expressions);
		}
	}
	
	public static interface FunctionDefinition {
		
		public MathValue compute(MathScope bodyScope, MathScope callingScope, Expression ... expressions) throws EvaluateExpressionException;
		
	}
	
	
	
	public static class FunctionID {
		private final String name;
		private final int numArgs;
		
		public FunctionID (String name, int numParams) {
			this.name = name;
			this.numArgs = numParams;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null || !(obj instanceof FunctionID))
				return false;
			FunctionID fh = (FunctionID) obj;
			return name.equals(fh.name) && numArgs == fh.numArgs;
		}
		
		@Override
		public int hashCode() {
			return Objects.hash(name, numArgs);
		}
	}
	
	
	
	
	public static class FunctionLatexFormat implements Iterable<Object>{
		public static final FunctionLatexFormat NONE = null;
		
		private final Object[] formatArray;
		
		public static Integer insertParam(int index) {
			return index;
		}
		
		public static boolean isParam(Object formatPart) {
			return formatPart instanceof Integer;
		}
		
		public static int getParamNumber(Object formatPart) {
			if(!isParam(formatPart))
				throw new IllegalArgumentException("String must be an param");
			return (Integer) formatPart;
		}
		
		public FunctionLatexFormat (Object ... formatArray) {
			this.formatArray = formatArray;
		}

		@Override
		public Iterator<Object> iterator() {
			return new LatexFormatIterator();
		}
		
		public int size() {
			return formatArray.length;
		}
		
		public class LatexFormatIterator implements Iterator<Object> {
			private int index = -1;
			
			@Override
			public boolean hasNext() {
				return index + 1 < size();
			}
			
			@Override
			public Object next() {
				return formatArray[++index];
			}
			
		}
	}
}
