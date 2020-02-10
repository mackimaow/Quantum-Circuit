package math.expression;

import java.io.BufferedReader;
import java.util.LinkedList;

import language.compiler.LexicalAnalyzer;
import language.compiler.ParseTree;
import language.compiler.ParseTree.ParseNode;
import language.compiler.ProductionSymbol.NonTerminal;
import language.compiler.Token;
import math.expression.MathParser.FunctionParseBranch;
import math.expression.MathParser.MathParseBranch;
import math.expression.MathParser.MathParseMathValue;
import math.expression.MathParser.MathParseNode;
import math.expression.MathParser.MathParseTree;
import math.expression.MathParser.MathParseVariable;
import math.expression.MathParser.MatrixParseBranch;
import math.expression.MathParser.ParseException;
import math.mathValue.MathInteger;
import math.mathValue.MathReal;
import math.mathValue.MathValue;

public class ExpressionParser {
	public static final Token IF  = new Token("[iI][fF]");
	public static final Token ELSE  = new Token("[eE][lL][sS][eE]|[oO][tT][hH][eE][rR][wW][iI][sS][eE]");
	public static final Token OCBRA  = new Token("\\{");
	public static final Token CCBRA  = new Token("\\}");
	public static final Token NOT = new Token("([nN][oO][tT])|\\!|~");
	public static final Token AND = new Token("([aA][nN][dD])|\\&");
	public static final Token OR = new Token("[oO][rR]|\\|");
	
	public static final Token LH = new Token("<");
	public static final Token GH = new Token(">");
	public static final Token EQ = new Token("=");
	public static final Token LH_EQ = new Token("<=");
	public static final Token GH_EQ = new Token(">=");
	public static final Token N_EQ = new Token("!=|~=|/=");
	
	public static final Token XOR = new Token("[xX][oO][rR]\\(\\+\\)");		// addition modulo 2 or exclusive OR (+)
	public static final Token NAME 		= new Token("\\\\?[a-zA-Z]\\w*");  		// used for variable and function names
	public static final Token INTEGER	= new Token("\\d+");					    // any positive integer
	public static final Token REAL		= new Token("\\d+\\.\\d*|\\d*\\.\\d+");	    // any positive decimal
	public static final Token OBRA 		= new Token("\\[");							// [
	public static final Token CBRA 		= new Token("\\]");							// ]
	public static final Token OPAR 		= new Token("\\(");							// (
	public static final Token CPAR 		= new Token("\\)");							// )
	public static final Token COM 		= new Token(",");							// ,
	public static final Token SCOM 		= new Token(";");							// ;
	public static final Token ADD 		= new Token("\\+");							// +
	public static final Token SUB_NEG 	= new Token("-");							// -
	public static final Token MULT 		= new Token("\\*");							// *
	public static final Token EXP 		= new Token("^");							// ^
	public static final Token DIV 		= new Token("/");							// /
	public static final Token SPACE 	= new Token("\\s+");						// any white space character
	public static final Token KRONECKER	= new Token("\\(x\\)");						// tensor product (x)
	
	
	// CONDITION_NT = EXPRESSION_NODE 	BOOLEAN_NODE 	ELSE_NODE
	// CONDITION_NT = ELSE_EXPRESSION_NODE
	public static final NonTerminal CONDITION_NT = new NonTerminal("Condition"); 
	
	
	
	
	// NOT_NT = EXPRESSION
	public static final NonTerminal NOT_NT = new NonTerminal("not");

	
	// AND_NT = LEFT_EXPRESSION 	RIGHT_EXPRESSION
	public static final NonTerminal AND_NT = new NonTerminal("and");

	// OR_NT = LEFT_EXPRESSION 		RIGHT_EXPRESSION
	public static final NonTerminal OR_NT  = new NonTerminal("or");

	// XOR_BOOL_NT = LEFT_EXPRESSION	RIGHT_EXPRESSION
	public static final NonTerminal XOR_BOOL_NT = new NonTerminal("xor boolean");
	
	
	
	
	// LT_NT = LEFT_EXPRESSION 		RIGHT_EXPRESSION
	public static final NonTerminal LT_NT = new NonTerminal("less than");
	
	// GT_NT = LEFT_EXPRESSION		RIGHT_EXPRESSION
	public static final NonTerminal GT_NT = new NonTerminal("greater than");
	
	// EQ_NT = LEFT_EXPRESSION		RIGHT_EXPRESSION
	public static final NonTerminal EQ_NT = new NonTerminal("equal to");
	
	// LT_EQ_NT = LEFT_EXPRESSION	RIGHT_EXPRESSION
	public static final NonTerminal LT_EQ_NT = new NonTerminal("less than equal to");
	
	// GT_EQ_NT = LEFT_EXPRESSION	RIGHT_EXPRESSION
	public static final NonTerminal GT_EQ_NT = new NonTerminal("greater than equal to");
	
	// N_EQ_NT = LEFT_EXPRESSION	RIGHT_EXPRESSION
	public static final NonTerminal N_EQ_NT = new NonTerminal("not equal to");
	
	
	

	// ADD_NT = LEFT_EXPRESSION		RIGHT_EXPRESSION
	public static final NonTerminal ADD_NT	= new NonTerminal("add");
	
	// SUB_NT = LEFT_EXPRESSION		RIGHT_EXPRESSION
	public static final NonTerminal SUB_NT	= new NonTerminal("subtract");

	// XOR_INT_NT = LEFT_EXPRESSION		RIGHT_EXPRESSION
	public static final NonTerminal XOR_INT_NT = new NonTerminal("xor integer");

	// NEG_NT = EXPRESSION
	public static final NonTerminal NEG_NT	= new NonTerminal("negate");
	
	// MULT_NT = LEFT_EXPRESSION 	RIGHT_EXPRESSION
	public static final NonTerminal MULT_NT = new NonTerminal("multiply");
	
	// DIV_NT = LEFT_EXPRESSION 	RIGHT_EXPRESSION
	public static final NonTerminal DIV_NT = new NonTerminal("divide");
	
	// POW_NT = LEFT_EXPRESSION 	RIGHT_EXPRESSION
	public static final NonTerminal POW_NT = new NonTerminal("power");
	
	// KRONECKER_NT = LEFT_EXPRESSION 	RIGHT_EXPRESSION
	public static final NonTerminal KRONECKER_NT = new NonTerminal("kronecker");
	
	// INDEX_NT = VARIABLE 	RIGHT_EXPRESSION
	public static final NonTerminal INDEX_NT = new NonTerminal("index integer");
	
	// INDEX_MAT_NT = VARIABLE	 ROW_INDEX_EXPRESSION 	COLUMN_INDEX_EXPRESSION
	public static final NonTerminal INDEX_MAT_NT = new NonTerminal("index matrix");
	
	// MAT_NT = EXPRESSION 	... EXPRESSION
	public static final NonTerminal MAT_NT = new NonTerminal("matrix");
	
	// FUNCT_NT = ARG_EXPRESSION ... ARG_EXPRESSION
	public static final NonTerminal FUNCT_NT = new NonTerminal("function");

	
	
	// PARA_NT = EXPRESSION
	public static final NonTerminal PARA_NT = new NonTerminal("parathesis");
	
	
	private static final LexicalAnalyzer EXPRESSION_LEXER = 
			new LexicalAnalyzer(IF, ELSE, OCBRA, CCBRA, NOT, AND, OR, LH, GH, EQ, LH_EQ, GH_EQ,
					N_EQ, XOR, NAME, INTEGER, REAL, OBRA, CBRA, OPAR, CPAR, COM, SCOM, ADD, SUB_NEG, MULT,
					EXP, DIV, SPACE, KRONECKER);
	
	
	public static ParseTree parse(String string) throws ParseException {
		MathParser mathParser = new MathParser(EXPRESSION_LEXER, string, SPACE);
		MathSemanticAnalyzer msa = new MathSemanticAnalyzer();
		MathParseNode mpn = e(mathParser, msa);
		return new MathParseTree(mpn);
	}
	
	
	public static ParseTree parse(BufferedReader br) throws ParseException {
		MathParser mathParser = new MathParser(EXPRESSION_LEXER, br, SPACE);
		MathSemanticAnalyzer msa = new MathSemanticAnalyzer();
		MathParseNode mpn = e(mathParser, msa);
		return new MathParseTree(mpn);
	}
	
	
	// e -> 	OCBRA b i
	// e -> 	ep
	protected static MathParseNode e(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == OCBRA) {
			MathParseNode expression = b(mp, msa);
			MathParseBranch condition = i(mp, msa, expression);
			condition.prependLatex("\\begin{cases}");
			condition.appendLatex("\\end{cases}");
			return condition;
		} else {
			mp.placeBack();
			return ep(mp, msa);
		}
	}
	

	// i -> 	IF b COM l
	protected static MathParseBranch i(MathParser mp, MathSemanticAnalyzer msa, MathParseNode expression) throws ParseException {
		MathParseBranch condition = new MathParseBranch(CONDITION_NT);
		mp.matchNext(IF);
		expression.appendLatex(" , & \\text{if} \\ ");
		MathParseNode bool = b(mp, msa);
		mp.matchNext(COM);
		bool.appendLatex(" \\\\ ");
		MathParseNode elseNode = l(mp, msa);
		condition.addChild(expression);
		condition.addChild(bool);
		condition.addChild(elseNode);
		return condition;
	}
	

	// l -> 	b ELSE
	// l ->		b i
	protected static MathParseNode l(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		MathParseNode expression = b(mp, msa);
		mp.loadNextToken();
		if(mp.getToken() == ELSE) {
			expression.appendLatex(" , & \\text{otherwise} ");
			return expression;
		} else {
			mp.placeBack();
			return i(mp, msa, expression);
		}
	}
	
	
	// b -> 	a bp
	protected static MathParseNode b(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		MathParseNode left = a(mp, msa);
		MathParseNode top = bp(mp, msa, left);
		return top;
	}
	
	// bp -> 	OR a
	// bp -> 	XOR a
	// bp -> 	
	protected static MathParseNode bp(MathParser mp, MathSemanticAnalyzer msa, MathParseNode left) throws ParseException {
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == OR) {
			left.appendLatex(" \\lor ");
			MathParseNode right = a(mp, msa);
			MathParseBranch or = new MathParseBranch(OR_NT);
			or.addChild(left);
			or.addChild(right);
			return bp(mp, msa, or);
		} else if(token == XOR) {
			left.appendLatex(" \\oplus ");
			MathParseNode right = a(mp, msa);
			MathParseBranch xor = new MathParseBranch(XOR_BOOL_NT);
			xor.addChild(left);
			xor.addChild(right);
			return bp(mp, msa, xor);
		} else {
			mp.placeBack();
			return left;
		}
	}
	
	// a -> 	c ap
	protected static MathParseNode a(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		MathParseNode left = c(mp, msa);
		MathParseNode top = ap(mp, msa, left);
		return top;
	}
	

	// ap -> 	MULT c
	// ap -> 	
	protected static MathParseNode ap(MathParser mp, MathSemanticAnalyzer msa, MathParseNode left) throws ParseException {
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == AND) {
			left.appendLatex(" \\land ");
			MathParseNode right = c(mp, msa);
			MathParseBranch and = new MathParseBranch(AND_NT);
			and.addChild(left);
			and.addChild(right);
			return ap(mp, msa, and);
		} else {
			mp.placeBack();
			return left;
		}
	}
	

	// c -> 	NOT c
	// c -> 	ep LH ep
	// c -> 	ep GH ep
	// c -> 	ep EQ ep
	// c -> 	ep LH_EQ ep
	// c -> 	ep GH_EQ ep
	// c -> 	ep N_EQ ep
	// c -> 	ep
	protected static MathParseNode c(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		mp.loadNextToken();
		Token token = mp.getToken();
		
		if(token == NOT) {
			MathParseBranch top = new MathParseBranch(NOT_NT);
			top.prependLatex(" \\overline{ ");
			top.appendLatex(" } ");
			top.addChild(c(mp, msa));
			return top;
		} else {
			mp.placeBack();
		}
		
		MathParseNode left = ep(mp, msa);
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		
		token = mp.getToken();
		
		if(token == LH) {
			left.appendLatex(" < ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(LT_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else if(token == GH) {
			left.appendLatex(" > ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(GT_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else if(token == EQ) {
			left.appendLatex(" = ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(EQ_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else if(token == LH_EQ) {
			left.appendLatex(" \\leq ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(LT_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else if(token == GH_EQ) {
			left.appendLatex(" \\geq ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(GT_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else if(token == N_EQ) {
			left.appendLatex(" \\neq ");
			MathParseNode right = ep(mp, msa);
			MathParseBranch top = new MathParseBranch(N_EQ_NT);
			top.addChild(left);
			top.addChild(right);
			return top;
			
		} else {
			mp.placeBack();
			return left;
		}
	}
	
	

	// ep -> 	t epp
	protected static MathParseNode ep(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		MathParseNode left = t(mp, msa);
		MathParseNode top = epp(mp, msa, left);
		return top;
	}
	

	// epp -> 	ADD t
	// epp -> 	SUB_NEG t
	// epp -> 	XOR t
	// epp -> 	
	protected static MathParseNode epp(MathParser mp, MathSemanticAnalyzer msa, MathParseNode left) throws ParseException {
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == ADD) {
			left.appendLatex(" + ");
			MathParseNode right = t(mp, msa);
			MathParseBranch top = new MathParseBranch(ADD_NT);
			top.addChild(left);
			top.addChild(right);
			return epp(mp, msa, top);
		} else if (token == SUB_NEG) {
			left.appendLatex(" - ");
			MathParseNode right = t(mp, msa);
			MathParseBranch top = new MathParseBranch(SUB_NT);
			top.addChild(left);
			top.addChild(right);
			return epp(mp, msa, top);
		} else if (token == XOR) {
			left.appendLatex(" \\oplus ");
			MathParseNode right = t(mp, msa);
			MathParseBranch top = new MathParseBranch(XOR_INT_NT);
			top.addChild(left);
			top.addChild(right);
			return epp(mp, msa, top);
		} else {
			mp.placeBack();
			return left;
		}
	}


	// t -> 	f tp
	protected static MathParseNode t(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		MathParseNode left = f(mp, msa);
		MathParseNode top = tp(mp, msa, left);
		return top;
	}


	// tp -> 	MULT f
	// tp -> 	DIV f
	// tp -> 	KRONECKER f
	// tp -> 	
	protected static MathParseNode tp(MathParser mp, MathSemanticAnalyzer msa, MathParseNode left) throws ParseException {
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == MULT) {
			left.appendLatex(" \\cdot ");
			MathParseNode right = f(mp, msa);
			MathParseBranch top = new MathParseBranch(MULT_NT);
			top.addChild(left);
			top.addChild(right);
			return tp(mp, msa, top);
		} else if (token == DIV) {
			left = removeParathesisIfPreset(left);
			left.prependLatex(" { ");
			left.appendLatex(" \\over ");
			MathParseNode right = f(mp, msa);
			right = removeParathesisIfPreset(right);
			right.appendLatex(" } ");
			MathParseBranch top = new MathParseBranch(DIV_NT);
			top.addChild(left);
			top.addChild(right);
			return tp(mp, msa, top);
		} else if (token == KRONECKER) {
			left.appendLatex(" \\otimes ");
			MathParseNode right = f(mp, msa);
			MathParseBranch top = new MathParseBranch(KRONECKER_NT);
			top.addChild(left);
			top.addChild(right);
			return tp(mp, msa, top);
		} else if (token == NAME || token == REAL || 
				token == OPAR || token == OBRA ) {
			mp.placeBack();
			left.appendLatex(" \\cdot ");
			MathParseNode right = f(mp, msa);
			MathParseBranch top = new MathParseBranch(MULT_NT);
			top.addChild(left);
			top.addChild(right);
			return tp(mp, msa, top);
		} else {
			mp.placeBack();
			return left;
		}
	}
	
	
	// f -> 	SUB_NEG f
	// f -> 	v fp
	protected static MathParseNode f(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		mp.loadNextToken();
		if(mp.getToken() == SUB_NEG) {
			MathParseNode middle = f(mp, msa);
			middle.prependLatex(" - ");
			MathParseBranch top = new MathParseBranch(NEG_NT);
			top.addChild(middle);
			return top;
		} else {
			mp.placeBack();
			MathParseNode left = v(mp, msa);
			MathParseNode top = fp(mp, msa, left);
			return top;
		}
	}
	
	
	// fp (la = SUB_NEG)	-> 	EXP f fp 
	// fp 					-> 	EXP v fp
	// fp 					-> 	
	protected static MathParseNode fp(MathParser mp, MathSemanticAnalyzer msa, MathParseNode left) throws ParseException {
		if(!mp.hasNextToken()) return left;
		mp.loadNextToken();
		if(mp.getToken() == EXP) {
			MathParseBranch top = new MathParseBranch(POW_NT);
			MathParseNode right;
			mp.loadNextToken();
			Token next = mp.getToken();
			mp.placeBack();
			right = next == SUB_NEG? f(mp, msa) : v(mp, msa);
			top.addChild(left);
			top.addChild(right);
			left.appendLatex(" ^ { ");
			right.appendLatex(" } ");
			return fp(mp, msa, top);
		} else {
			mp.placeBack();
			return left;
		}
	}
	
	

	// v 				-> 	OPAR b CPAR 
	// v (la = OBRA)	-> 	m
	// v 				->  REAL
	// v 				->  INTEGER
	// v (la = OCBRA)	->  NAME vi
	// v (la = OPAR)	->  NAME vf
	// v 				->  NAME
	protected static MathParseNode v(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == OPAR) {
			MathParseNode middle = b(mp, msa);
			mp.matchNext(CPAR);
			
			MathParseBranch mpb = new MathParseBranch(PARA_NT);
			mpb.appendLatex(" \\left( ");
			mpb.appendLatex(" \\right) ");
			mpb.addChild(middle);
			return mpb;
		} else if (token == OBRA) {
			mp.placeBack();
			return m(mp, msa);
		} else if(token == REAL) {
			double d = Double.parseDouble(mp.getLexeme());
			MathValue mv = MathReal.toMathReal(d);
			msa.setCurrentType(MathValueTypeSet.REAL);
			return new MathParseMathValue(mv, mp.getLexeme());
		} else if(token == INTEGER) {
			long d = Long.parseLong(mp.getLexeme());
			MathInteger mv = new MathInteger(d);
			msa.setCurrentType(MathValueTypeSet.INTEGER);
			return new MathParseMathValue(mv, mp.getLexeme());
		} else if(token == NAME) {
			String name = mp.getLexeme();
			if (!mp.hasNextToken()) {
				msa.setCurrentType(MathValueTypeSet.UNDETERMINED);
				return new MathParseVariable(name);
			} else {
				mp.loadNextToken();
				token = mp.getToken();
				mp.placeBack();
				if(token == OCBRA) {
					return vi(mp, msa, name);
				} else if (token == OPAR) {
					return vf(mp, msa, name);
				} else {
					msa.setCurrentType(MathValueTypeSet.UNDETERMINED);
					return new MathParseVariable(name);
				}
			}
		} else {
			throw new ParseException("The sequence " + mp.getLexeme() + " does not belong where a value is expected");
		}
	}

	

	// vf 	-> OPAR    b COM ... b     CPAR 	
	protected static MathParseNode vf (MathParser mp, MathSemanticAnalyzer msa, String functionName) throws ParseException  {
		mp.matchNext(OPAR);
		
		FunctionParseBranch functionParseBranch = new FunctionParseBranch(functionName);
		
		while(true) {
			MathParseNode node = b(mp, msa);
			functionParseBranch.addChild(node);
			
			mp.loadNextToken();
			Token next = mp.getToken();
			if(next != COM) {
				mp.placeBack();
				mp.matchNext(CPAR);
				break;
			}
		}
		msa.setCurrentType(MathValueTypeSet.UNDETERMINED);
		return functionParseBranch;
	}
	

	// vi 	-> OCBRA  b  CCBRA
	// vi 	-> OCBRA  b COM b  CCBRA
	protected static MathParseNode vi (MathParser mp, MathSemanticAnalyzer msa, String variableName) throws ParseException {
		mp.matchNext(OCBRA);
		
		MathParseBranch top;
		MathParseNode mpv = new MathParseVariable(variableName);
		mpv.appendLatex("_{ ");
		MathParseNode first = b(mp, msa);
		first.appendLatex(" , ");
		
		mp.loadNextToken();
		Token token = mp.getToken();
		if(token == COM) {
			top = new MathParseBranch(INDEX_MAT_NT);
			MathParseNode second = b(mp, msa);
			second.appendLatex(" } ");
			
			top.addChild(mpv);
			top.addChild(first);
			top.addChild(second);
		} else {
			mp.placeBack();
			
			top = new MathParseBranch(INDEX_NT);
			top.addChild(mpv);
			top.addChild(first);
		}
		mp.matchNext(CCBRA);

		msa.setCurrentType(MathValueTypeSet.UNDETERMINED);
		return top;
	}
	
	

	// m 	-> OBRA     b COM ... b SCOM ...      CBRA
	protected static MathParseNode m(MathParser mp, MathSemanticAnalyzer msa) throws ParseException {
		mp.matchNext(OBRA);
		
		LinkedList<ParseNode> contents = new LinkedList<>();
		
		final int UNKNOWN_AMT = -1;
		
		
		int columns = UNKNOWN_AMT;
		int rows = 0;
		int currentColumnIndex = 0;
		while(true) {
			MathParseNode node = b(mp, msa);
			if(msa.getCurrentType().isMatrix())
				throw new ParseException("Matrix cannot have a matrix element");
			contents.add((ParseNode)node);
			
			mp.loadNextToken();
			Token next = mp.getToken();
			if(next == COM) {
				currentColumnIndex++;
				
				node.appendLatex(" & ");
			} else if(next == SCOM) {
				if(columns != UNKNOWN_AMT && currentColumnIndex != columns) {
					throw new ParseException("Matrix cannot have different length columns");
				} else {
					if(currentColumnIndex == 0)
						throw new ParseException("Matrix cannot have 0 length columns");
				}
				
				columns = currentColumnIndex;
				currentColumnIndex = 0;
				rows++;
					
				node.appendLatex(" \\\\ ");
			} else {
				mp.placeBack();
				mp.matchNext(CBRA);
				break;
			}
		}
		rows++;
		
		MatrixParseBranch top = new MatrixParseBranch(rows, columns, contents);
		top.prependLatex(" { \\begin{bmatrix} ");
		top.appendLatex(" \\end{bmatrix} } ");
		msa.setCurrentType(rows, columns);
		
		return top;
	}
	
	private static MathParseNode removeParathesisIfPreset(MathParseNode node) {
		if(node instanceof MathParseBranch) {
			MathParseBranch mpb = (MathParseBranch) node;
			if(mpb.getProductionSymbol() == PARA_NT) {
				MathParseNode center = (MathParseNode) mpb.getChildren().get(0);
				return center;
			}
		}
		return node;
	}

}
