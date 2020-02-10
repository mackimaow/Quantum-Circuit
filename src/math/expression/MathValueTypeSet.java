package math.expression;

import java.io.Serializable;

import math.mathValue.MathMatrix;
import math.mathValue.MathValue;

public class MathValueTypeSet implements Serializable {
	private static final long serialVersionUID = -6920179254755072179L;

	public static final MathValueTypeSet UNDETERMINED 	= null;
	public static final MathValueTypeSet VARIABLE 		= new MathValueTypeSet(MathType.VARIABLE, 0, 0);
	public static final MathValueTypeSet BOOLEAN 		= new MathValueTypeSet(MathType.BOOLEAN, 0, 0);
	public static final MathValueTypeSet INTEGER 		= new MathValueTypeSet(MathType.INTEGER, 0, 0);
	public static final MathValueTypeSet REAL 			= new MathValueTypeSet(MathType.REAL, 0, 0);
	public static final MathValueTypeSet COMPLEX 		= new MathValueTypeSet(MathType.COMPLEX, 0, 0);
	
	public static MathValueTypeSet getScalarTypeSet(MathType typeSet) {
		switch(typeSet) {
		case BOOLEAN:
			return BOOLEAN;
		case COMPLEX:
			return COMPLEX;
		case INTEGER:
			return INTEGER;
		case REAL:
			return REAL;
		case VARIABLE:
			return VARIABLE;
		default:
			return null;
		}
	}
	
	public static enum MathType {
		VARIABLE, BOOLEAN, INTEGER, REAL, COMPLEX;
		
		public static MathType getEncasingTypeBetween(MathType first, MathType second) {
			switch(first) {
			case BOOLEAN:
				switch(second) {
				case BOOLEAN:
					return first;
				default:
					return null;
				}
			case INTEGER:
				switch(second) {
				case INTEGER:
				case REAL:
				case COMPLEX:
					return second;
				default:
					return null;
				}
			case REAL:
				switch(second) {
				case INTEGER:
					return first;
				case REAL:
				case COMPLEX:
					return second;
				default:
					return null;
				}
			case COMPLEX:
				switch(second) {
				case INTEGER:
				case REAL:
				case COMPLEX:
					return first;
				default:
					return null;
				}
			default:
				return null;
			}
		}
	}
	
	public final MathType valueType;
	public final int rows, columns;
	
	
	
	public static MathValueTypeSet getMathValueTypeSet(MathValue mathValue) {
		if(mathValue.isMatrix()) {
			MathMatrix mm = (MathMatrix) mathValue;
			MathType mvt = mm.v(0, 0).getType();
			for(int r = 0 ; r < mm.getRows(); r++) {
				for(int c = 0 ; c < mm.getColumns(); c++) {
					MathType mvtNext =  mm.v(r, c).getType();
					MathType result = MathType.getEncasingTypeBetween(mvt, mvtNext);
					if(result == null) throw new RuntimeException("Cannot have a matrix of " + mvt.name() + " and " + mvtNext.name());
					mvt = result;
				}
			}
			return new MathValueTypeSet(mvt, mm.getRows(), mm.getColumns());
		} else {
			return getScalarTypeSet(mathValue.getType());
		}
	}
	
	public MathValueTypeSet(MathType valueType, int rows, int columns) {
		this.valueType = valueType;
		this.rows = rows;
		this.columns = columns;
	} 
	
	public MathValueTypeSet duplicateContents() {
		if(!isMatrix())
			return this;
		return new MathValueTypeSet(valueType, rows, columns);
	}
	
	public boolean isMatrix() {
		return rows != 0 && columns != 0;
	}
	
	public boolean isScalar() {
		return !isMatrix();
	}
	
}
