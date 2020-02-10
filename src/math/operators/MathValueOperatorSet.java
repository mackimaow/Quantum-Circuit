package math.operators;

import math.mathValue.MathBoolean;
import math.mathValue.MathComplex;
import math.mathValue.MathInteger;
import math.mathValue.MathMatrix;
import math.mathValue.MathReal;
import math.mathValue.MathValue;

public final class MathValueOperatorSet implements OperatorSet<MathValue> 	{
	private static final long serialVersionUID = 4283965380630269968L;
	
	public static final MathValueOperatorSet OPERATOR_SET = new MathValueOperatorSet();
	
	private MathValueOperatorSet() {}
	
	@Override
	public MathValue add(MathValue num1, MathValue num2) {
		return map(num2, num1::add, num1::add, num1::add, num1::add, num1::add);
	}

	@Override
	public MathValue sub(MathValue num1, MathValue num2) {
		return map(num2, num1::sub, num1::sub, num1::sub, num1::sub, num1::sub);
	}

	@Override
	public MathValue mult(MathValue num1, MathValue num2) {
		return map(num2, num1::mult, num1::mult, num1::mult, num1::mult, num1::mult);
	}

	@Override
	public MathValue neg(MathValue num) {
		return num.neg();
	}
	
	@Override
	public MathValue div(MathValue num1, MathValue num2) {
		return map(num2, num1::div, num1::div, num1::div, num1::div, num1::div);
	}

	@Override
	public MathValue pow(MathValue num1, MathValue num2) {
		return map(num2, num1::pow, num1::pow, num1::pow, num1::pow, num1::pow);
	}
	
	public MathValue xOr(MathValue num1, MathValue num2) {
		return map(num2, num1::xOr, num1::xOr, num1::xOr, num1::xOr, num1::xOr);
	}
	
	public MathValue kronecker(MathValue num1, MathValue num2) {
		return num1.kroneckerValue(num2);
	}
	
	public MathValue index(MathValue num1, MathValue num2) {
		return num1.indexValue(num2);
	}
	
	public MathValue index(MathValue num1, MathValue row, MathValue column) {
		return num1.indexValue(row, column);
	}
	
	@Override
	public MathValue sqrt(MathValue num) {
		return pow(num, MathReal.toMathReal(.5d));
	}

	@Override
	public MathValue conjugate(MathValue num) {
		return num.conj();
	}
	
	public MathValue negate(MathValue num) {
		return num.neg();
	}
	
	@Override
	public MathValue get1() {
		return new MathInteger(1);
	}

	@Override
	public MathValue getn1() {
		return new MathInteger(-1);
	}

	@Override
	public MathValue get0() {
		return new MathInteger(0);
	}
	
	private static MathValue map (MathValue second, MathBooleanMap bool, MathIntegerMap integer, MathRealMap real, MathComplexMap complex, MathMatrixMap matrix) {
		if(second.isMatrix())
			return matrix.map((MathMatrix) second);
		switch(second.getType()) {
		case BOOLEAN:
			return bool.map((MathBoolean) second);
		case INTEGER:
			return integer.map((MathInteger) second);
		case REAL:
			return real.map((MathReal) second);
		case COMPLEX:
			return complex.map((MathComplex) second);
		default:
			break;
		}
		return null;
	}
	
	private static interface MathBooleanMap {
		public MathValue map(MathBoolean value);
	}
	
	private static interface MathIntegerMap {
		public MathValue map(MathInteger value);
	}
	
	private static interface MathRealMap {
		public MathValue map(MathReal value);
	}
	
	private static interface MathComplexMap {
		public MathValue map(MathComplex value);
	}
	
	private static interface MathMatrixMap {
		public MathValue map(MathMatrix value);
	}
	
	
	@Override
	public MathValue[] mkZeroArray(int size) {
		MathValue[] zeroArray = new MathValue[size];
		for(int i = 0; i < size; i++)
			zeroArray[i] = get0();
		return zeroArray;
	}

}
