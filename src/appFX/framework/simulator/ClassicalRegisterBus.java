package appFX.framework.simulator;

public class ClassicalRegisterBus {
	private final byte[] classicalRegs;
	private final int numBits;
	
	public ClassicalRegisterBus(int numBits) {
		int byteRegsSize = (int) Math.ceil(numBits / 8);
		classicalRegs = new byte[byteRegsSize];
		this.numBits = numBits;
		for(int i = 0; i < classicalRegs.length; i++)
			classicalRegs[i] = 0;
	}
	
	public int getSize() {
		return numBits;
	}
	
	public void set(int bit, boolean value) {
		int byteIndex = (int) Math.floor(bit / 8);
		int bitIndex = bit % 8;
		if(value)
			classicalRegs[byteIndex] |= 1 << bitIndex;
		else
			classicalRegs[byteIndex] &= ~(1 << bitIndex);
	}
	
	public void negate(int bit) {
		set(bit, !get(bit));
	}
	
	public void and(int bit, boolean value) {
		set(bit, get(bit) && value);
	}
	
	public void or(int bit, boolean value) {
		set(bit, get(bit) || value);
	}
	
	public void xor(int bit, boolean value) {
		set(bit, get(bit) ^ value);
	}
	
	public boolean get(int bit) {
		int byteIndex = (int) Math.floor(bit / 8);
		int bitIndex = bit % 8;
		byte elem = classicalRegs[byteIndex];
		int result = (elem >>> bitIndex) & 1;
		return result != 0;
	}
}
