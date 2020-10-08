package mathLib.equation;

import java.io.Serializable;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
import utils.customCollections.Single;
import utils.customMaps.IndexMap;

public class BooleanEquation implements Serializable {
	private static final long serialVersionUID = -4303957002057417418L;
	private static final InputBitIndexListener DO_NOTHING = (e)->{};
	
	private final ParseTree tree;
	private final String latexString;
	private final int outputBit;
	
	public BooleanEquation(String booleanExpression) throws BooleanEquationParseException, LexemeNotRecognizedException, LexicalAnaylizerIOException {
		this(booleanExpression, DO_NOTHING);
	}
	
	public BooleanEquation(String booleanExpression, InputBitIndexListener inputBitIndexListener) throws BooleanEquationParseException, LexemeNotRecognizedException, LexicalAnaylizerIOException {
		Single<String> latexString = new Single<>();
		Single<Integer> outputBit = new Single<>();
		
		BooleanEquationParser expressionParser = new BooleanEquationParser(booleanExpression);
		tree = expressionParser.parse(new BooleanEquationRunnable() {
			@Override
			public void setOutputBitIndex(int bitIndex) {
				outputBit.setFirst(bitIndex);
			}
			
			@Override
			public void getLatex(String latex) {
				latexString.setFirst(latex);
			}
			
			@Override
			public void addInputBitIndex(int bitIndex) {
				inputBitIndexListener.addInputIndex(bitIndex);
			}
		});
		this.outputBit = outputBit.first();
		this.latexString = latexString.first();
	}
	
	public int getOutputBitIndex() {
		return outputBit;
	}
	
	public String getLatexString() {
		return latexString;
	}
	
	public boolean compute(final Function<Integer, Boolean> bitFetch, final BiConsumer<Integer, Boolean> bitSet, IndexMap indexMap) {
		ParseBranch root = (ParseBranch) tree.getRoot();
		return compute(root, bitFetch, bitSet, indexMap);
	}
	
	private static boolean compute(ParseNode node, final Function<Integer, Boolean> bitFetch, 
			final BiConsumer<Integer, Boolean> bitSet,
			final IndexMap indexMap) {
		ProductionSymbol sym =  node.getProductionSymbol();
		if(sym == BooleanEquationParser.NOT_NT ) {
			ParseBranch branch = (ParseBranch) node;
			return !compute(branch.getChildren().get(0), bitFetch, bitSet, indexMap);
		} else if( sym == BooleanEquationParser.AND_NT ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitFetch, bitSet, indexMap);
			boolean second = compute(branch.getChildren().get(1), bitFetch, bitSet, indexMap);
			return first && second;
		} else if(sym == BooleanEquationParser.OR_NT  ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitFetch, bitSet, indexMap);
			boolean second = compute(branch.getChildren().get(1), bitFetch, bitSet, indexMap);
			return first || second;
		} else if(sym == BooleanEquationParser.XOR_NT ) {
			ParseBranch branch = (ParseBranch) node;
			boolean first = compute(branch.getChildren().get(0), bitFetch, bitSet, indexMap);
			boolean second = compute(branch.getChildren().get(1), bitFetch, bitSet, indexMap);
			return first ^ second;
		} else if(sym == BooleanEquationParser.BIT_NT ) {
			BitLeaf bitLeaf = (BitLeaf) node;
			int index = indexMap.get(bitLeaf.getBitInt());
			return bitFetch.apply(index);
		} else if (sym == BooleanEquationParser.SET_NT) {
			ParseBranch branch = (ParseBranch) node;
			BitLeaf first = (BitLeaf) branch.getChildren().get(0);
			ParseNode expression = branch.getChildren().get(1);
			boolean value = compute(expression, bitFetch, bitSet, indexMap);
			int index = indexMap.get(first.getBitInt());
			bitSet.accept(index, value);
			return value;
		} else if(sym == BooleanEquationParser.BOOL_NT) {
			BoolLeaf leaf = (BoolLeaf) node;
			return leaf.getValue();
		} else {
			return false;
		}
	}
	
	public static interface InputBitIndexListener extends Serializable {
		public void addInputIndex(int inputIndex);
	}
}
