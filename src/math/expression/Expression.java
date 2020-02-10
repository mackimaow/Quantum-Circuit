package math.expression;

import java.io.Serializable;
import java.util.Iterator;

import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseBranch;
import language.compiler.ParseTree.ParseLeaf;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.ProductionSymbol.SymbolType;
import math.expression.MathParser.FunctionParseBranch;
import math.expression.MathParser.MathParseMathValue;
import math.expression.MathParser.MathParseTree;
import math.expression.MathParser.MathParseVariable;
import math.expression.MathParser.MatrixParseBranch;
import math.expression.MathParser.ParseException;
import math.expression.MathValueTypeSet.MathType;
import math.mathValue.MathBoolean;
import math.mathValue.MathMatrix;
import math.mathValue.MathValue;
import math.operators.MathValueOperatorSet;

public class Expression implements Serializable {
	private static final long serialVersionUID = 6406307424607474858L;
	
	private ParseTree tree;
	private MathValueTypeSet mathValueType;
	
	
	/**
	 * returns the variable's name that this expression represents. 
	 * If this expression doesn't contains a single variable name,
	 * then an {@link IllegalArgumentException} is thrown
	 * @param expression the variable name
	 * @return the variable's name that this expression represents
	 */
	public static String getVariableFrom(Expression expression) {
		ParseNode pn;
		ParseBranch pb;
		pn = expression.tree.getRoot();
		while(!pn.isLeaf()) {
			pb = (ParseBranch) pn;
			if(pb.getChildren().size() == 1)
				pn = pb.getChildren().getFirst();
			else throw new IllegalArgumentException("Expression must only contain one variable");
		}
		if(pn.getProductionSymbol() == ExpressionParser.NAME) {
			return ((ParseLeaf) pn).getValue();
		} else throw new IllegalArgumentException("Expression must only contain one variable");
	}
	
	
	public Expression (String expression) throws ParseException {
		this(ExpressionParser.parse(expression));
	}
	
	
	public Expression (ParseTree tree) {
		this.tree = tree;
	}
	
	
	
	public String treeString () {
		return tree.toString();
	}
	
	
	public MathValueTypeSet getTypeSet() {
		return mathValueType;
	}
	
	
	
	public ParseTree getTree() {
		return tree;
	}
	
	public MathValue compute(MathScope mathScope) throws EvaluateExpressionException {
		try {
			return evaulateNode(tree.getRoot(), mathScope);
		} catch (EvaluateExpressionException e) {
			throw e;
		} catch (Exception e) {
			throw new EvaluateExpressionException(e.getMessage());
		}
	}
	
	public String toLatex(MathScope scope) {
		MathParseTree mTree = (MathParseTree) tree;
		return mTree.getLatex(scope);
	}
	
	private MathValue evaulateNode(ParseNode parseNode, MathScope scope) throws EvaluateExpressionException {
		ProductionSymbol ps = parseNode.getProductionSymbol();
		
		if( ps == ExpressionParser.CONDITION_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode expressionNode = iter.next();
			ParseNode conditionNode = iter.next();
			ParseNode otherwiseNode = iter.next();
			
			MathValue mv = evaulateNode(conditionNode, scope);
			if(mv.isMatrix() || mv.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv.typeString());
			MathBoolean mb = (MathBoolean) mv;
			boolean condition = mb.getValue();
			if(condition)
				return evaulateNode(expressionNode, scope);
			return evaulateNode(otherwiseNode, scope);
		} else if ( ps == ExpressionParser.NOT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode expressionNode = iter.next();
			MathValue mv = evaulateNode(expressionNode, scope);
			if(mv.isMatrix() || mv.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv.typeString());
			return mv.neg();
			
		} else if ( ps == ExpressionParser.AND_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv2.isMatrix() || mv2.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			return mv1.mult((MathBoolean) mv2);
			
		} else if ( ps == ExpressionParser.OR_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv2.isMatrix() || mv2.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			return mv1.add((MathBoolean) mv2);
			
		} else if ( ps == ExpressionParser.XOR_BOOL_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv2.isMatrix() || mv2.getType() != MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			return mv1.xOr((MathBoolean) mv2);
			
		} else if ( ps == ExpressionParser.LT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp < 0);
			
		} else if ( ps == ExpressionParser.GT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp > 0);
			
		} else if ( ps == ExpressionParser.EQ_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp == 0);
			
		} else if ( ps == ExpressionParser.LT_EQ_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp <= 0);
			
		} else if ( ps == ExpressionParser.GT_EQ_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp >= 0);
			
		} else if ( ps == ExpressionParser.N_EQ_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv1.typeString());
			MathValue mv2 = evaulateNode(rightNode, scope);
			if(mv1.isMatrix() || mv1.getType() == MathType.COMPLEX || mv1.getType() == MathType.BOOLEAN)
				throw new EvaluateExpressionException("Cannot evaulate conditional with type: " + mv2.typeString());
			
			int comp = MathValue.compare(mv1, mv2);
			return new MathBoolean(comp != 0);
			
		} else if ( ps == ExpressionParser.ADD_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.add(mv1, mv2);
			
		} else if ( ps == ExpressionParser.SUB_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.sub(mv1, mv2);
			
		} else if ( ps == ExpressionParser.XOR_INT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.xOr(mv1, mv2);
			
		} else if ( ps == ExpressionParser.NEG_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.neg(mv1);
			
		} else if ( ps == ExpressionParser.MULT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.mult(mv1, mv2);
			
		} else if ( ps == ExpressionParser.DIV_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.div(mv1, mv2);
			
		} else if ( ps == ExpressionParser.POW_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.pow(mv1, mv2);
			
		} else if ( ps == ExpressionParser.KRONECKER_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.kronecker(mv1, mv2);
			
		} else if ( ps == ExpressionParser.INDEX_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode rightNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(rightNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.index(mv1, mv2);
			
		} else if ( ps == ExpressionParser.INDEX_MAT_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode leftNode = iter.next();
			ParseNode firstNode = iter.next();
			ParseNode seondNode = iter.next();
			
			MathValue mv1 = evaulateNode(leftNode, scope);
			MathValue mv2 = evaulateNode(firstNode, scope);
			MathValue mv3 = evaulateNode(seondNode, scope);
			
			return MathValueOperatorSet.OPERATOR_SET.index(mv1, mv2, mv3);
			
		} else if ( ps == ExpressionParser.MAT_NT ) {
			MatrixParseBranch mpb = (MatrixParseBranch) parseNode;
			Iterator<ParseNode> iter = mpb.iterator();
			int rows = mpb.getRows();
			int columns = mpb.getColumns();
			
			
			MathValue[] elements = new MathValue[rows * columns];
			for(int r = 0; r < rows; r++) {
				for(int c = 0; c < columns; c++) {
					ParseNode elemNode = iter.next();
					elements[r * columns + c] =  evaulateNode(elemNode, scope);
				}
			}
			
			MathMatrix mm = new MathMatrix(rows, columns, elements);
			return mm;
			
		} else if ( ps == ExpressionParser.FUNCT_NT ) {
			FunctionParseBranch fpb = (FunctionParseBranch) parseNode;
			String name = fpb.getName();
			int numArgs = fpb.getSize();
			Expression[] args = new Expression[numArgs];
			int i = 0;
			for(ParseNode pn : fpb)
				args[i++] = new Expression(new ParseTree(pn));
			
			return scope.computeFunction(name, args);
			
		} else if ( ps == ExpressionParser.PARA_NT ) {
			ParseBranch pb = (ParseBranch) parseNode;
			Iterator<ParseNode> iter = pb.iterator();
			ParseNode first = iter.next();
			
			MathValue mv = evaulateNode(first, scope);
			
			return mv;
			
		} else if(ps == ExpressionParser.NAME) {
			MathParseVariable mpv = (MathParseVariable) parseNode;
			String name = mpv.getName();
			return scope.computeVariable(name);
			
		} else if(ps == ExpressionParser.REAL ) {
			MathParseMathValue mpv = (MathParseMathValue) parseNode;
			return mpv.getValue();
		} else {
			if(ps.getType() == SymbolType.NON_TERMINAL) {
				NonTerminal nt = (NonTerminal) ps;
				throw new EvaluateExpressionException("The given Non-Terminal \" " + nt.getName() +"\"can not be defined to be evaluted");
			}
			throw new EvaluateExpressionException("A given Non-Terminal can not be defined to be evaluted");
		}
	}
	
	
	@SuppressWarnings("serial")
	public static class EvaluateExpressionException extends Exception {
		public EvaluateExpressionException (String message) {
			super(message);
		}
	}
	
	
	
}
