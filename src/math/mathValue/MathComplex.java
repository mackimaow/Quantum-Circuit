package math.mathValue;

import math.expression.MathValueTypeSet.MathType;

public class MathComplex implements MathValue {
	private static final long serialVersionUID = -9099395460757557732L;
	
	double a, b;
	
	public static MathValue toMathComplex(double real, double complex) {
		if(MathValue.fuzzyEquals(complex, 0))
			return MathReal.toMathReal(real);
		return new MathComplex(real, complex);
	}
	
    private MathComplex(double a, double b) {
        this.a = a;
        this.b = b;
    }
    
    public double getReal() {
    	return a;
    }
    
    public double getImaginary() {
    	return b;
    }
    
    
    
    public MathValue add(MathInteger value) {
    	return toMathComplex(a + value.getNumber(), b);
	}
	
	public MathValue add(MathReal value) {
    	return toMathComplex(a + value.getNumber(), b);
	}
	
	public MathValue add(MathComplex value) {
    	return toMathComplex(a + value.a, b + value.b);
	}
	
	public MathValue add(MathMatrix value) {
    	return value.add(this);
	}
	
	
	
	public MathValue sub(MathInteger value) {
    	return toMathComplex(a - value.getNumber(), b);
	}
	
	public MathValue sub(MathReal value) {
    	return toMathComplex(a - value.getNumber(), b);
	}
	
	public MathValue sub(MathComplex value) {
    	return toMathComplex(a - value.a, b - value.b);
	}
	
	public MathValue sub(MathMatrix value) {
    	return value.sub(this).neg();
	}
	
	
	
	public MathValue mult(MathInteger value) {
    	return toMathComplex(a * value.getNumber(), b * value.getNumber());
	}
	
	public MathValue mult(MathReal value) {
    	return toMathComplex(a * value.getNumber(), b * value.getNumber());
	}
	
	public MathValue mult(MathComplex value) {
		return toMathComplex(a * value.a - b * value.b, a * value.b + b * value.a);
	}
	
	public MathValue mult(MathMatrix value) {
    	return value.mult(this);
	}
	
	
	
	public MathValue div(MathInteger value) {
    	return toMathComplex(a / value.getNumber(), b / value.getNumber());
	}
	
	public MathValue div(MathReal value) {
    	return toMathComplex(a / value.getNumber(), b / value.getNumber());
	}
	
	public MathValue div(MathComplex value) {
		double denom = value.a * value.a - value.b * value.b;
		MathComplex mc = new MathComplex(value.a / denom, - value.b / denom);
		return mult(mc);
	}
	
	
	
	public MathValue neg() {
		return toMathComplex(-a, -b);
	}
	
	
	
	public MathValue conj() {
		return toMathComplex(a, -b);
	}
	
	
	
	public MathValue pow(MathInteger value) {
		double temp1 = value.getNumber() * Math.atan(b / a);
		double magToPower = Math.pow(a * a + b * b, value.getNumber() / 2d);
		return toMathComplex(magToPower * Math.cos(temp1), magToPower * Math.sin(temp1));
	}
	
	public MathValue pow(MathReal value) {
		double temp1 = value.getNumber() * Math.atan(b / a);
		double magToPower = Math.pow(a * a + b * b, value.getNumber() / 2d);
		return toMathComplex(magToPower * Math.cos(temp1), magToPower * Math.sin(temp1));
	}
	
	public MathValue pow(MathComplex value) {
		if(b == 0) {
			double temp1 = Math.pow(a, value.a);
			double temp2 = value.b * Math.log(a);
			return new MathComplex(temp1 * Math.cos(temp2), temp1 * Math.sin(temp2));
		} else if(value.b == 0){
			double temp1 = value.a * Math.atan(b / a);
			double magToPower = Math.pow(a * a + b * b, value.a / 2d);
			return new MathComplex(magToPower * Math.cos(temp1), magToPower * Math.sin(temp1));
		}
		throw new UnsupportedOperation("Cannot raise a Complex power to another Complex Number");
	}
	
	public MathValue abs() {
		return MathReal.toMathReal(Math.sqrt(a*a + b*b));
	}
	
	public MathValue inverse() {
		double denom = a * a - b * b;
		return toMathComplex(a / denom, - b / denom);
	}
	
    
    @Override
    public String toString(){
        if(b >= 0)
            return a + " + " + b + " i ";
        else
            return a + " - " + (-b) + " i ";
    }

	@Override
	public MathType getType() {
		return MathType.COMPLEX;
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
