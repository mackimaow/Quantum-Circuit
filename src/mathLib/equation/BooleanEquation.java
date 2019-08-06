package mathLib.equation;

import java.io.Serializable;

import language.compiler.LexicalAnalyzer.LexemeNotRecognizedException;
import language.compiler.LexicalAnalyzer.LexicalAnaylizerIOException;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol;
import mathLib.equation.BooleanEquationParser.BitLeaf;
import mathLib.equation.BooleanEquationParser.BoolLeaf;
import mathLib.equation.BooleanEquationParser.BooleanEquationParseException;
import mathLib.equation.BooleanEquationParser.BooleanEquationRunnable;

public class BooleanEquation implements Serializable, BooleanEquationRunnable {
	private static final long serialVersionUID = -4303957002057417418L;
	private static final InputBitIndexListener DO_NOTHING = (e)->{};
	
	private final ParseTree tree;
	private String latexString;
	private int outputBit = 0;
	private final InputBitIndexListener inputBitIndexListener;
	
	public BooleanEquation(String booleanExpression) throws BooleanEquationParseException, LexemeNotRecognizedException, LexicalAnaylizerIOException {
		this(booleanExpression, DO_NOTHING);
	}
	
	public BooleanEquation(String booleanExpression, InputBitIndexListener inputBitIndexListener) throws BooleanEquationParseException, LexemeNotRecognizedException, LexicalAnaylizerIOException {
		this.inputBitIndexListener = inputBitIndexListener;
		BooleanEquationParser expressionParser = new BooleanEquationParser(booleanExpression);
		tree = expressionParser.parse(this);
	}
	
	public int getOutputBitIndex() {
		return outputBit;
	}
	
	public String getLatexString() {
		return latexString;
	}
	
	@Override
	public void setOutputBitIndex(int bitIndex) {
		outputBit = bitIndex;
	}
	
	@Override
	public void addInputBitIndex(int bitIndex) {
		inputBitIndexListener.addInputIndex(bitIndex);
	}

	@Override
	public void getLatex(String latex) {
		latexString = latex;
	}
	
	public boolean compute(CalculateBitListener bitListener) {
		ParseBranch root = (ParseBranch) tree.getRoot();
		ParseNode expression = root.getChildren().get(1);
		return compute(expression, bitListener);
	}
	
	private static boolean compute(ParseNode node, CalculateBitListener bitListener) {
		ProductionSymbol sym =  node.getProductionSymbol();
		if(sym == BooleanEquationParser.NOT_NT ) {
			ParseBranch branch = (ParseBranch) node;
			return !compute(branch.getChildren().get(0), bitListener);
		} else if( sym == BooleanEquationParser.AND_NT ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitListener);
			boolean second = compute(branch.getChildren().get(1), bitListener);
			return first && second;
		} else if(sym == BooleanEquationParser.OR_NT  ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitListener);
			boolean second = compute(branch.getChildren().get(1), bitListener);
			return first || second;
		} else if(sym == BooleanEquationParser.XOR_NT ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitListener);
			boolean second = compute(branch.getChildren().get(1), bitListener);
			return first ^ second;
		} else if(sym == BooleanEquationParser.BIT_NT ) {
			BitLeaf bitLeaf = (BitLeaf) node;
			int bitIndex = bitLeaf.getBitInt();
			return bitListener.getClassicalBitValue(bitIndex);
		} else if(sym == BooleanEquationParser.BOOL_NT) {
			BoolLeaf leaf = (BoolLeaf) node;
			return leaf.getValue();
		} else {
			return false;
		}
	}
	
	public static interface InputBitIndexListener {
		public void addInputIndex(int inputIndex);
	}
	
	public static interface CalculateBitListener {
		public boolean getClassicalBitValue(int inputIndex);
	}
}
