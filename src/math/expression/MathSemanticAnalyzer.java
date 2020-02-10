package math.expression;

import math.expression.MathValueTypeSet.MathType;

public class MathSemanticAnalyzer {
	
	private MathValueTypeSet currentType;
	
	public MathValueTypeSet getCurrentType() {
		return currentType;
	}
	
	public void setCurrentType(int rows, int columns) {
		setCurrentType(new MathValueTypeSet(MathType.COMPLEX, rows, columns));
	}
	
	public void setCurrentType(MathValueTypeSet currentType) {
		this.currentType = currentType;
	}
	
	// TODO: Complete Math Semantic Analyzer
	
//	public static boolean add(MathValueTypeSet first, MathValueTypeSet second) {
//		if(first.isMatrix()) {
//			if(second.isMatrix()) {
//				if(first.) {
//					
//				}
//				
//			}
//		} else {
//			
//		}
//		
//	}
//	
//	public static boolean mult(MathValueTypeSet first, MathValueTypeSet second) {
//		
//	}
//	
//	public static boolean div() {
//		
//	}
	
}
