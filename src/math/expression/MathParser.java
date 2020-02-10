package math.expression;

import java.io.BufferedReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import language.compiler.LexicalAnalyzer;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.Token;
import math.mathValue.MathValue;
import utils.customCollections.Pair;

public class MathParser {
	
	
	private final Iterator<Pair<Token, String>> iterator;
	private Token token = null;
	private String lexeme = null;
	private boolean placedBack = false;
	
	public MathParser(LexicalAnalyzer la, String toParse, Token ... tokensToFilter) {
		Stream<Pair<Token, String>> tokenStream = la.getTokenStream(toParse).filter((pair) -> {
			Token toCheck = pair.first();
			for(int i = 0; i < tokensToFilter.length; i++) {
				if(tokensToFilter[i] == toCheck)
					return false;
			}
			return true;
		});
		iterator = tokenStream.iterator();
	}
	
	public MathParser(LexicalAnalyzer la, BufferedReader br, Token ... tokensToFilter) {
		Stream<Pair<Token, String>> tokenStream = la.getTokenStream(br).filter((pair) -> {
			Token toCheck = pair.first();
			for(int i = 0; i < tokensToFilter.length; i++) {
				if(tokensToFilter[i] == toCheck)
					return false;
			}
			return true;
		});
		iterator = tokenStream.iterator();
	}
	
	public void matchNext(Token mustMatch) throws ParseException {
		if(!hasNextToken())
			throw new ParseException();
		loadNextToken();
		if(token != mustMatch)
			throw new ParseException();
	}
	
	public boolean hasNextToken() {
		return placedBack || iterator.hasNext();
	}
	
	public void loadNextToken() throws ParseException {
		if(placedBack) {
			placedBack = false;
		} else {
			if(!iterator.hasNext())
				throw new ParseException("Unexpected end");
			Pair<Token, String> pair = iterator.next();
			token  = pair.first();
			lexeme = pair.second();
		}
	}
	
	public void placeBack() {
		placedBack = true;
	}
	
	public String getLexeme() {
		return lexeme;
	}
	
	public Token getToken() {
		return token;
	}
	
	public abstract static interface MathParseNode {
		
		public void appendLatex(String latex);
		public void prependLatex(String latex);
		public String getLatex(MathScope matSet);
	}
	
	public static class MathParseTree extends ParseTree {
		private static final long serialVersionUID = 6306338717374639463L;

		public MathParseTree(MathParseNode root) {
			super((ParseNode) root);
		}
		
		public String getLatex(MathScope ms) {
			MathParseNode mpn = (MathParseNode) getRoot();
			return mpn.getLatex(ms);
		}
	}
	
	
	public static class MathParseBranch extends ParseBranch implements MathParseNode {
		private static final long serialVersionUID = -1009537845942716942L;
		protected String prepend = "", append = "";
		
		private MathParseBranch(NonTerminal nonTerminal, LinkedList<ParseNode> nodes) {
			super(nonTerminal, nodes);
		} 
		
		public MathParseBranch(NonTerminal nonTerminal) {
			super(nonTerminal);
		}
		
		public void addChild(MathParseNode mathParseNode) {
			super.addChild((ParseNode) mathParseNode);
		}
		
		@Override
		public void appendLatex(String latex) {
			append += latex;
		}

		@Override
		public void prependLatex(String latex) {
			prepend = latex + prepend;
		}

		@Override
		public String getLatex(MathScope matSet) {
			String childrenLatex = prepend;
			for(ParseNode pn : getChildren()) {
				MathParseNode pmn = (MathParseNode) pn;
				childrenLatex += pmn.getLatex(matSet);
			}
			return childrenLatex + append;
		}
	}
	
	public static class FunctionParseBranch extends MathParseBranch {
		private static final long serialVersionUID = -8968726286392876050L;
		private final String functionName;
		
		
		public FunctionParseBranch(String functionName) {
			super(ExpressionParser.FUNCT_NT);
			this.functionName = functionName;
		}
		
		@Override
		public String getLatex(MathScope matSet) {
			List<ParseNode> list = getChildren();
			int numParams = list.size();
			Function function = matSet.getFunction(functionName, numParams);
			String[] parameterLatex = new String[numParams];
			
			int i = 0;
			for(ParseNode pn : list) {
				MathParseNode pmn = (MathParseNode) pn;
				parameterLatex[i++] = pmn.getLatex(matSet);
			}
			
			return prepend + function.getLatex(parameterLatex) + append;			
		}
		
		public String getName() {
			return functionName;
		}
	}
	
	public static class MatrixParseBranch extends MathParseBranch {
		private static final long serialVersionUID = -8968726286392876050L;
		private final int rows, columns;
		
		public MatrixParseBranch(int rows, int columns, LinkedList<ParseNode> nodes) {
			super(ExpressionParser.MAT_NT, nodes);
			this.rows = rows;
			this.columns = columns;
		}
		
		public int getRows() {
			return rows;
		}
		
		public int getColumns() {
			return columns;
		}
	}
	
	public static class MathParseVariable extends ParseNode implements MathParseNode {
		private static final long serialVersionUID = 6704866327167092549L;
		public final String variableName;
		private String prepend = "", append = "";
		
		public MathParseVariable(String variableName) {
			super(ExpressionParser.NAME);
			this.variableName = variableName;
		}
		
		@Override
		public String getLatex(MathScope matSet) {
			Variable variable = matSet.getVariable(variableName);
			return prepend + variable.getLatex() + append;
		}

		@Override
		public void appendLatex(String latex) {
			append += latex;
		}


		@Override
		public void prependLatex(String latex) {
			prepend = prepend + latex;
		}


		@Override
		public boolean isLeaf() {
			return true;
		}
		
		public String getName() {
			return variableName;
		}
	}
	
	
	public static class MathParseMathValue extends ParseNode implements MathParseNode {
		private static final long serialVersionUID = 6167734696366235141L;
		
		private String latex;
		private MathValue mathValue;
		
		public MathParseMathValue(MathValue mathValue, String latex) {
			super(ExpressionParser.REAL);
			this.mathValue = mathValue;
			this.latex = latex;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
		
		public MathValue getValue() {
			return mathValue;
		}
		
		@Override
		public void appendLatex(String latex) {
			this.latex += latex;
		}

		@Override
		public void prependLatex(String latex) {
			this.latex = latex + this.latex;
		}

		@Override
		public String getLatex(MathScope matSet) {
			return latex;
		}
	}
	
	
	@SuppressWarnings("serial")
	public static class ParseException extends Exception {
		public ParseException() {
			this("");
		}
		
		public ParseException(String message) {
			super(message);
		}
	}
}
