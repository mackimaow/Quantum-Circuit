package math.mathValue;

import math.expression.MathValueTypeSet.MathType;

public class MathReal implements MathValue {
	private static final long serialVersionUID = -5859534084180809410L;
	
	final double number;
	
	public static MathValue toMathReal(double number) {
		long abs = (long) Math.abs(number);
		if(MathValue.fuzzyEquals(number, abs))
			return new MathInteger(abs);
		return new MathReal(number);
	}
	
	private MathReal(double number) {
		this.number = number;
	}
	
	public double getNumber() {
		return number;
	}
	
	public int compare(MathInteger value) {
		return Double.compare(number, value.number);
	}
	
	public int compare(MathReal value) {
		return Double.compare(number, value.number);
	}
	
	
	
	public MathValue add(MathInteger value) {
		return toMathReal(number + value.getNumber());
	}
	
	public MathValue add(MathReal value) {
		return toMathReal(number + value.getNumber());
	}
	
	public MathValue add(MathComplex value) {
		return value.add(this);
	}
	
	public MathValue add(MathMatrix value) {
		return value.add(this);
	}
	
	
	
	public MathValue sub(MathInteger value) {
		return toMathReal(number - value.getNumber());
	}
	
	public MathValue sub(MathReal value) {
		return toMathReal(number - value.getNumber());
	}
	
	public MathValue sub(MathComplex value) {
		return value.sub(this).neg();
	}
	
	public MathValue sub(MathMatrix value) {
		return value.add(this).neg();
	}
	
	
	
	public MathValue mult(MathInteger value) {
		return toMathReal(number * value.getNumber());
	}
	
	public MathValue mult(MathReal value) {
		return toMathReal(number * value.getNumber());
	}
	
	public MathValue mult(MathComplex value) {
		return value.mult(this);
	}
	
	public MathValue mult(MathMatrix value) {
		return value.mult(this);
	}
	
	
	
	public MathValue div(MathInteger value) {
		return toMathReal(number / value.getNumber());
	}
	
	public MathValue div(MathReal value) {
		return toMathReal(number / value.getNumber());
	}
	
	public MathValue div(MathComplex value) {
		double denom = value.a * value.a - value.b * value.b;
		return MathComplex.toMathComplex(value.a * number / denom, - value.b * number / denom);
	}
	
	
	
	public MathValue neg() {
		return toMathReal(-number);
	}
	
	
	
	public MathValue conj() {
		return toMathReal(number);
	}
	
	
	
	public MathValue pow(MathInteger value) {
		return toMathReal(Math.pow(number, value.getNumber()));
	}
	
	public MathValue pow(MathReal value) {
		if(number < 0) {
			double sqrt = Math.sqrt(number);
			double temp1 = 2 * value.getNumber() * Math.atan(sqrt);
			double magToPower = Math.pow(number, value.getNumber());
			return MathComplex.toMathComplex(magToPower * Math.cos(temp1), magToPower * Math.sin(temp1));
		} else {
			return MathReal.toMathReal(Math.pow(number, value.getNumber()));
		}
	}
	
	public MathValue pow(MathComplex value) {
		double temp1 = Math.pow(number, value.a);
		double temp2 = value.b * Math.log(number);
		return MathComplex.toMathComplex(temp1 * Math.cos(temp2), temp1 * Math.sin(temp2));
	}
	
	
	@Override
	public MathType getType() {
		return MathType.REAL;
	}

	@Override
	public boolean isMatrix() {
		return false;
	}

	@Override
	public int getRows() {
		return 0;
	}

	@Override
	public int getColumns() {
		return 0;
	}
}
