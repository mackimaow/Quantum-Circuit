package math.mathValue;

import math.expression.MathValueTypeSet.MathType;

public class MathBoolean implements MathValue {
	private static final long serialVersionUID = 8779678414226044488L;

	private final boolean value;
	
	public MathBoolean(boolean value) {
		this.value = value;
	}
	
	public boolean getValue() {
		return value;
	}
	
	
	public MathValue add(MathBoolean value) {
		return new MathBoolean(this.value || value.value);
	}
	
	public MathValue mult(MathBoolean value) {
		return new MathBoolean(this.value && value.value);
	}
	
	public MathValue neg() {
		return new MathBoolean(!value);
	}
		
	public MathValue xOr(MathBoolean value) {
		return new MathBoolean(this.value ^ value.value);
	}
	
	
	
	
	@Override
	public MathType getType() {
		return MathType.BOOLEAN;
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
