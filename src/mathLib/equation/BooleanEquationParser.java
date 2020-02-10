package mathLib.equation;

import java.util.Iterator;

import language.compiler.LexicalAnalyzer;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.Token;
import utils.customCollections.Pair;

public class BooleanEquationParser {
	public static final Token SPACE = new Token("\\s+");
	public static final Token BIT = new Token("b");
	public static final Token INTEGER = new Token("[0-9]+");
	public static final Token SET = new Token("=|<-");
	public static final Token NOT = new Token("([nN][oO][tT])|\\!|~");
	public static final Token AND = new Token("([aA][nN][dD])|\\*|\\&");
	public static final Token OR = new Token("[oO][rR]|\\+|\\|");
	public static final Token XOR = new Token("[xX][oO][rR]|\\^|\\(\\+\\)");
	public static final Token OPAR = new Token("\\(");
	public static final Token CPAR = new Token("\\)");
	public static final Token OBRA = new Token("\\[");
	public static final Token CBRA = new Token("\\]");
	

	public static final NonTerminal SET_NT = new NonTerminal("set");
	public static final NonTerminal NOT_NT = new NonTerminal("not");
	public static final NonTerminal AND_NT = new NonTerminal("and");
	public static final NonTerminal OR_NT  = new NonTerminal("or");
	public static final NonTerminal XOR_NT = new NonTerminal("xor");
	public static final NonTerminal BIT_NT	= new NonTerminal("bit");
	public static final NonTerminal BOOL_NT	= new NonTerminal("bool");
	
	private static final LexicalAnalyzer EXPRESSION_LEXER = new LexicalAnalyzer(SPACE, BIT, INTEGER, SET, NOT, AND, OR, XOR, OPAR, CPAR, OBRA, CBRA);
	
	private static final BooleanEquationRunnable DO_NOTHING = new BooleanEquationRunnable() {
		@Override public void setOutputBitIndex(int bitIndex) {}
		@Override public void getLatex(String latex) {}
		@Override public void addInputBitIndex(int bitIndex) {}
	};

	private final Iterator<Pair<Token, String>> iterator;
	private Token token = null;
	private String lexeme = null;
	private boolean placedBack = false;
	private String latexString;
	private ParseTree tree;
	private BooleanEquationRunnable runnable;
	
	public BooleanEquationParser(String string) {
		this.iterator = EXPRESSION_LEXER.getTokenStream(string).filter((o) -> o.first() != SPACE).iterator();
	}
	
	public ParseTree parse() throws BooleanEquationParseException {
		return parse(DO_NOTHING);
	}
	
	public ParseTree parse(BooleanEquationRunnable runnable) throws BooleanEquationParseException {
		this.runnable = runnable;
		ParseNode branch = parseSet();
		tree = new ParseTree(branch);
		runnable.getLatex(latexString);
		return tree;
	}
	
	private ParseNode parseSet() throws BooleanEquationParseException {
		latexString = "";
		BitLeaf variable = parseBit(true);
		runnable.setOutputBitIndex(variable.bitIndex);
		matchNext(SET);
		latexString += " = ";
		ParseNode expression = parseOr();
		if(hasNext())
			throw new BooleanEquationParseException();
		ParseBranch set = new ParseBranch(SET_NT);
		set.addChild(variable);
		set.addChild(expression);
		return set;
	}
	
	private ParseNode parseOr() throws BooleanEquationParseException {
		ParseNode parseAnd = parseAnd();
		ParseNode or = parseOrE(parseAnd);
		return or;
	}
	
	private ParseNode parseOrE(ParseNode pb) throws BooleanEquationParseException {
		if(!hasNext()) return pb;
		getNext();
		if(token == OR) {
			latexString += " + ";
			ParseNode right = parseAnd();
			ParseBranch or = new ParseBranch(OR_NT);
			or.addChild(pb);
			or.addChild(right);
			return parseOrE(or);
		} else if(token == XOR) {
			latexString += " \\oplus ";
			ParseNode right = parseAnd();
			ParseBranch xor = new ParseBranch(XOR_NT);
			xor.addChild(pb);
			xor.addChild(right);
			return parseOrE(xor);
		} else {
			placeBack();
			return pb;
		}
	}
	
	private ParseNode parseAnd() throws BooleanEquationParseException {
		ParseNode parseFactor = parseFactor(false);
		ParseNode and = parseAndE(parseFactor);
		return and;
	}
	
	private ParseNode parseAndE(ParseNode pb) throws BooleanEquationParseException {
		if(!hasNext()) return pb;
		getNext();
		if(token == AND) {
			latexString += " ";
			ParseNode right = parseFactor(false);
			ParseBranch and = new ParseBranch(AND_NT);
			and.addChild(pb);
			and.addChild(right);
			return parseAndE(and);
		} else if(token != OR && token != XOR && token != CPAR) {
			latexString += " ";
			placeBack();
			ParseNode right = parseAnd();
			ParseBranch and = new ParseBranch(AND_NT);
			and.addChild(pb);
			and.addChild(right);
			return parseAndE(and);
		} else {
			placeBack();
			return pb;
		}
	}
	
	private ParseNode parseFactor(boolean outerIsNot) throws BooleanEquationParseException {
		getNext();
		if(token == NOT) {
			latexString += " \\overline{";
			ParseNode center = parseFactor(true);
			ParseBranch negate = new ParseBranch(NOT_NT);
			negate.addChild(center);
			latexString += "} ";
			return center;
		} else if(token == OPAR) {
			if(!outerIsNot)
				latexString += " ( ";
			ParseNode center = parseOr();
			matchNext(CPAR);
			if(!outerIsNot)
				latexString += " ) ";
			return center;
		} else if(token == INTEGER){
			latexString += lexeme;
			int i = Integer.parseInt(lexeme);
			return new BoolLeaf(i);
		} else if(token == BIT) {
			placeBack();
			BitLeaf bit = parseBit(false);
			runnable.addInputBitIndex(bit.bitIndex);
			return bit;
		} else {
			throw new BooleanEquationParseException();
		}
	}
	
	private BitLeaf parseBit(boolean output) throws BooleanEquationParseException {
		matchNext(BIT);
		matchNext(OBRA);
		matchNext(INTEGER);
		int i = Integer.parseInt(lexeme);
		matchNext(CBRA);
		latexString += " b" + (output? "'" : "") + "_{" + i + "} ";
		return new BitLeaf(i);
	}
	
	public void matchNext(Token mustMatch) throws BooleanEquationParseException {
		if(!hasNext())
			throw new BooleanEquationParseException();
		getNext();
		if(token != mustMatch)
			throw new BooleanEquationParseException();
	}
	
	public boolean hasNext() {
		return placedBack || iterator.hasNext();
	}
	
	public void getNext() {
		if(placedBack) {
			placedBack = false;
		} else {
			Pair<Token, String> pair = iterator.next();
			token  = pair.first();
			lexeme = pair.second();
		}
	}
	
	public void placeBack() {
		placedBack = true;
	}
	
	public static class BoolLeaf extends ParseNode {
		private static final long serialVersionUID = -5563007617843843787L;
		
		private final Boolean value;
		
		public BoolLeaf(int value) throws BooleanEquationParseException {
			super(BOOL_NT);
			if(value < 0 || value > 1)
				throw new BooleanEquationParseException();
			this.value = value == 1;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
		
		public Boolean getValue() {
			return value;
		}
	}
	
	public static class BitLeaf extends ParseNode {
		private static final long serialVersionUID = -873351171194083982L;
		
		private final int bitIndex;
		
		public BitLeaf(int bitIndex) {
			super(BIT_NT);
			this.bitIndex = bitIndex;
		}

		@Override
		public boolean isLeaf() {
			return true;
		}
		
		public int getBitInt() {
			return bitIndex;
		}
	}
	
	@SuppressWarnings("serial")
	public static class BooleanEquationParseException extends Exception {
		public BooleanEquationParseException(String message) {
			super(message);
		}
		public BooleanEquationParseException() {
			this("Could not parse boolean expression");
		}
	}
	
	public static interface BooleanEquationRunnable {
		void setOutputBitIndex(int bitIndex);
		void addInputBitIndex(int bitIndex);
		void getLatex(String latex);
	}
}
