package math.mathValue;

import java.io.Serializable;

import math.expression.MathValueTypeSet.MathType;

public interface MathValue extends Serializable {
	
	public static double EPSILON = .0000000000000001d;
	public static boolean ZERO_BASED = false;
	
	public MathType getType();
	public boolean isMatrix();
	public int getRows();
	public int getColumns();
	
	public default String typeString() {
		MathType type = getType();
		if(isMatrix())
			return type.name() + " Matrix"; 
		else
			return type.name();
	}
	
	public static int compare(MathValue first, MathValue second) {
		if(first.isMatrix() || second.isMatrix())
			throw makeInvalidOperation("Comparing", first, second);
		if(first.getType() == MathType.INTEGER) {
			MathInteger f = (MathInteger) first;
			if(second.getType() == MathType.INTEGER) {
				MathInteger s = (MathInteger) second;
				return f.compare(s);
			} else if(second.getType() == MathType.REAL){
				MathReal s = (MathReal) second;
				return f.compare(s);
			}
		} else if(first.getType() == MathType.REAL){
			MathReal f = (MathReal) first;
			if(second.getType() == MathType.INTEGER) {
				MathInteger s = (MathInteger) second;
				return f.compare(s);
			} else if(second.getType() == MathType.REAL){
				MathReal s = (MathReal) second;
				return f.compare(s);
			}
		}
		throw makeInvalidOperation("Comparing", first, second);
	}
	
	
	public default MathValue add(MathBoolean value) {
		throw makeInvalidOperation("Adding", this, value);
	}
	
	public default MathValue add(MathInteger value) {
		throw makeInvalidOperation("Adding", this, value);
	}
	
	public default MathValue add(MathReal value) {
		throw makeInvalidOperation("Adding", this, value);
	}
	
	public default MathValue add(MathComplex value) {
		throw makeInvalidOperation("Adding", this, value);
	}
	
	public default MathValue add(MathMatrix value) {
		throw makeInvalidOperation("Adding", this, value);
	}
	
	
	public default MathValue sub(MathBoolean value) {
		throw makeInvalidOperation("Subtracting", this, value);
	}
	
	public default MathValue sub(MathInteger value) {
		throw makeInvalidOperation("Subtracting", this, value);
	}
	
	public default MathValue sub(MathReal value) {
		throw makeInvalidOperation("Subtracting", this, value);
	}
	
	public default MathValue sub(MathComplex value) {
		throw makeInvalidOperation("Subtracting", this, value);
	}
	
	public default MathValue sub(MathMatrix value) {
		throw makeInvalidOperation("Subtracting", this, value);
	}
	
	
	
	public default MathValue mult(MathBoolean value) {
		throw makeInvalidOperation("Multipling", this, value);
	}
	
	public default MathValue mult(MathInteger value) {
		throw makeInvalidOperation("Multipling", this, value);
	}
	
	public default MathValue mult(MathReal value) {
		throw makeInvalidOperation("Multipling", this, value);
	}
	
	public default MathValue mult(MathComplex value) {
		throw makeInvalidOperation("Multipling", this, value);
	}
	
	public default MathValue mult(MathMatrix value) {
		throw makeInvalidOperation("Multipling", this, value);
	}
	
	
	
	public default MathValue div(MathBoolean value) {
		throw makeInvalidOperation("Dividing", this, value);
	}
	
	public default MathValue div(MathInteger value) {
		throw makeInvalidOperation("Dividing", this, value);
	}
	
	public default MathValue div(MathReal value) {
		throw makeInvalidOperation("Dividing", this, value);
	}
	
	public default MathValue div(MathComplex value) {
		throw makeInvalidOperation("Dividing", this, value);
	}
	
	public default MathValue div(MathMatrix value) {
		throw makeInvalidOperation("Dividing", this, value);
	}
	
	
	
	public default MathValue neg() {
		throw makeInvalidOperation("Negating", this);
	}
	
	
	
	public default MathValue conj() {
		throw makeInvalidOperation("Conjugating", this);
	}
	
	
	
	public default MathValue pow(MathBoolean value) {
		throw makeInvalidOperation("Raising", this, value);
	}
	
	public default MathValue pow(MathInteger value) {
		throw makeInvalidOperation("Raising", this, value);
	}
	
	public default MathValue pow(MathReal value) {
		throw makeInvalidOperation("Raising", this, value);
	}
	
	public default MathValue pow(MathComplex value) {
		throw makeInvalidOperation("Raising", this, value);
	}
	
	public default MathValue pow(MathMatrix value) {
		throw makeInvalidOperation("Raising", this, value);
	}
	
	
	
	public default MathValue xOr(MathBoolean value) {
		throw makeInvalidOperation("XOR-ing", this, value);
	}
	
	public default MathValue xOr(MathInteger value) {
		throw makeInvalidOperation("XOR-ing", this, value);
	}
	
	public default MathValue xOr(MathReal value) {
		throw makeInvalidOperation("XOR-ing", this, value);
	}
	
	public default MathValue xOr(MathComplex value) {
		throw makeInvalidOperation("XOR-ing", this, value);
	}
	
	public default MathValue xOr(MathMatrix value) {
		throw makeInvalidOperation("XOR-ing", this, value);
	}
	
	
	
	
	public default MathValue kronecker(MathMatrix value) {
		throw makeInvalidOperation("Kroneckering", this, value);
	}
	
	public default MathValue kroneckerValue(MathValue value) {
		if(value.isMatrix())
			return kronecker((MathMatrix) value);
		throw makeInvalidOperation("Kroneckering", this, value);
	}
	
	
	
	public default MathValue index(MathInteger value) {
		throw makeInvalidOperation("Indexing", this, value);
	}
	
	public default MathValue indexValue(MathValue value) {
		if(!value.isMatrix() && value.getType() == MathType.INTEGER)
			return index((MathInteger) value);
		throw makeInvalidOperation("Indexing", this, value);
	}
	
	
	
	public default MathValue index(MathInteger row, MathInteger column) {
		throw makeInvalidOperation("Indexing", this, row);
	}

	public default MathValue indexValue(MathValue row, MathValue column) {
		if(row.isMatrix() || row.getType() != MathType.INTEGER)
			throw makeInvalidOperation("Indexing", this, row);
		if(row.isMatrix() || column.getType() != MathType.INTEGER)
			throw makeInvalidOperation("Indexing", this, column);
		return index((MathInteger) row, (MathInteger) column);
	}
	
	
	
	
	static InvalidOperation makeInvalidOperation(String operation, MathValue value) {
		return new InvalidOperation(operation + " a " + value.typeString() + " is not allowed");
	}
	
	static InvalidOperation makeInvalidOperation(String operation, MathValue left, MathValue right) {
		return new InvalidOperation(operation + " a " + left.typeString()  + " by a " + right.typeString() + " is not allowed");
	}
	
	static InvalidOperation makeUnsupportedOperation(String operation, MathValue value) {
		return new InvalidOperation(operation + " a " + value.typeString() + " is not supported");
	}
	
	static UnsupportedOperation makeUnsupportedOperation(String operation, MathValue left, MathValue right) {
		return new UnsupportedOperation(operation + " a " + left.typeString()  + " by a " + right.typeString() + " is not supported");
	}
	
	@SuppressWarnings("serial")
	public static class UnsupportedOperation extends RuntimeException {
		public UnsupportedOperation(String message) {
			super(message);
		}
	}
	
	@SuppressWarnings("serial")
	public static class InvalidOperation extends RuntimeException {
		public InvalidOperation(String message) {
			super(message);
		}
	}
	
	public static boolean fuzzyEquals(double d1, double d2) {
		if(Math.abs(d1 - d2) <= EPSILON) return true;
		return false;
	}
	
}
