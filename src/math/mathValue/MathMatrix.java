package math.mathValue;

import math.Matrix;
import math.expression.MathValueTypeSet.MathType;
import math.operators.MathValueOperatorSet;

public class MathMatrix extends Matrix<MathValue> implements MathValue {
	private static final long serialVersionUID = -3964105436927690517L;
	
	@SafeVarargs
	public MathMatrix(int rows, int columns, MathValue ... components){
		super(MathValueOperatorSet.OPERATOR_SET, rows, columns, components);
	}

	private MathMatrix(MathValue[] comps, int rows, int columns) {
		super(comps, rows, columns, MathValueOperatorSet.OPERATOR_SET);
	}
	
	public MathMatrix(int rows, int columns){
		this(rows, columns, MathValueOperatorSet.OPERATOR_SET.mkZeroArray(rows * columns));
	}
	
	
	public MathValue add(MathInteger value) {
		return toMathMatrix(super.add(value));
	}
	
	public MathValue add(MathReal value) {
		return toMathMatrix(super.add(value));
	}
	
	public MathValue add(MathComplex value) {
		return toMathMatrix(super.add(value));
	}
	
	public MathValue add(MathMatrix value) {
		if(getRows() != value.getRows()) {
			throw new InvalidOperation("Cannot add a matrix with " + getRows()
				+ " rows by a matrix with " + value.getRows() + " rows");
		}
		if(getColumns() != value.getColumns()) {
			throw new InvalidOperation("Cannot add a matrix with " + getColumns()
				+ " columns by a matrix with " + value.getColumns() + " columns");
		}
		return toMathMatrix(super.add((Matrix<MathValue>) value));
	}
	
	
	
	public MathValue sub(MathInteger value) {
		return toMathMatrix(super.sub(value));
	}
	
	public MathValue sub(MathReal value) {
		return toMathMatrix(super.sub(value));
	}
	
	public MathValue sub(MathComplex value) {
		return toMathMatrix(super.sub(value));
	}
	
	public MathValue sub(MathMatrix value) {
		if(getRows() != value.getRows()) {
			throw new InvalidOperation("Cannot subtract a matrix with " + getRows()
				+ " rows by a matrix with " + value.getRows() + " rows");
		}
		if(getColumns() != value.getColumns()) {
			throw new InvalidOperation("Cannot subtract a matrix with " + getColumns()
				+ " columns by a matrix with " + value.getColumns() + " columns");
		}
		return toMathMatrix(super.sub((Matrix<MathValue>) value));
	}
	
	
	
	public MathValue mult(MathInteger value) {
		return toMathMatrix(super.mult(value));
	}
	
	public MathValue mult(MathReal value) {
		return toMathMatrix(super.mult(value));
	}
	
	public MathValue mult(MathComplex value) {
		return toMathMatrix(super.mult(value));
	}
	
	public MathValue mult(MathMatrix value) {
		if(getColumns() != value.getRows()) {
			throw new InvalidOperation("Cannot multiply a matrix with " + getColumns()
				+ " columns by a matrix with " + value.getRows() + " rows");
		}
		return toMathMatrix(super.mult((Matrix<MathValue>) value));
	}
	
	
	
	public MathValue div(MathInteger value) {
		return toMathMatrix(super.div(value));
	}
	
	public MathValue div(MathReal value) {
		return toMathMatrix(super.div(value));
	}
	
	public MathValue div(MathComplex value) {
		return toMathMatrix(super.div(value));
	}
	
	
	
	public MathValue neg() {
		return toMathMatrix(super.negate());
	}
	
	
	
	public MathValue conj() {
		return toMathMatrix(super.conjugate());
	}
	
	
	
	public MathValue xOr(MathBoolean value) {
		MathMatrix temp = new MathMatrix(rows, columns);
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(     v(r, c).xOr(value)   , r, c);
		return temp;
	}
	
	public MathValue xOr(MathInteger value) {
		MathMatrix temp = new MathMatrix(rows, columns);
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(     v(r, c).xOr(value)   , r, c);
		return temp;
	}
	
	public MathValue xOr(MathMatrix value) {
		if(getRows() != value.getRows()) {
			throw new InvalidOperation("Cannot xor a matrix with " + getRows()
				+ " rows by a matrix with " + value.getRows() + " rows");
		}
		if(getColumns() != value.getColumns()) {
			throw new InvalidOperation("Cannot xor a matrix with " + getColumns()
				+ " columns by a matrix with " + value.getColumns() + " columns");
		}
		
		
		MathValueOperatorSet o = MathValueOperatorSet.OPERATOR_SET;
		
		MathMatrix temp = new MathMatrix(rows, columns);
		for(int r = 0; r < rows; r++)
			for(int c = 0; c < columns; c++)
				temp.r(     o.xOr(v(r, c), value.v(r, c))   , r, c);
		return temp;
	}
	
	
	public MathValue kronecker(MathMatrix value) {
		return toMathMatrix(super.kronecker(value));
	}
	
	@Override
	public MathValue index(MathInteger row, MathInteger column) {
		if(getRows() != row.getNumber()) {
			throw new InvalidOperation("Cannot index a matrix with " + getRows()
				+ " rows at row index " + row.getNumber());
		}
		if(getColumns() != column.getNumber()) {
			throw new InvalidOperation("Cannot xor a matrix with " + getColumns()
				+ " columns at column index " + column.getNumber());
		}
		return v((int)row.getNumber(), (int)column.getNumber());
	}
	
	
	
	private static MathMatrix toMathMatrix(Matrix<MathValue> matrix) {
		return new MathMatrix(matrix.getComps(), matrix.getRows(), matrix.getColumns());
	}
	
	
	@Override
	public MathType getType() {
		return MathType.COMPLEX;
	}

	@Override
	public boolean isMatrix() {
		return true;
	}
}
