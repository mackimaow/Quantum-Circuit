package math.operators;

import java.io.Serializable;

import math.mathValue.MathComplex;
import math.mathValue.MathValue;

public interface OperatorSet<T> extends Serializable {
	
	@SuppressWarnings("unchecked")
	public static <T> OperatorSet<T> inferOperatorSet(T num) {
		if(num instanceof Double) {
			return (OperatorSet<T>) DoubleO.OPERATOR_SET;
		} else if(num instanceof Float) {
			return (OperatorSet<T>) FloatO.OPERATOR_SET;
		} else if(num instanceof Integer) {
			return (OperatorSet<T>) IntegerO.OPERATOR_SET;
		} else if(num instanceof MathValue) {
			return (OperatorSet<T>) MathValueOperatorSet.OPERATOR_SET;
		} else {
			throw new CannotInferOperatorSetException(num);
		}
	}
	
	@SuppressWarnings("serial")
	public static class CannotInferOperatorSetException extends RuntimeException {
		public <T> CannotInferOperatorSetException(T num) {
			super("Cannot infer type: " + num.getClass());
		}
	}
	
	public T add(T num1, T num2);
	public T sub(T num1, T num2);
	public T mult(T num1, T num2);
	public T div(T num1, T num2);
	public T neg(T num1);
	public T pow(T num1, T num2);
	public T sqrt(T num);
	public T conjugate(T num);
	
	public T get1();
	public T getn1();
	public T get0();
	
	public T[] mkZeroArray(int size);
}
