package appFX.framework.simulator.quickSim;

import java.util.Map;

import appFX.framework.exportGates.ExportedGate;

public class ClassicalState implements State {
	private final byte[] bytes;
	private final int numBits;
	
	public ClassicalState(int numBits) {
		int numBytes = (int) Math.ceil(numBits / 8.0);
		this.bytes = new byte[numBytes];
		this.numBits = numBits;
	}
	
	@Override
	public void apply(ExportedGate eg, Map<Integer, Integer> map) {
		
	}
	
	@Override
	public int size() {
		return numBits;
	}
	
	public boolean get(int bit) {
		if ( bit < 0 || bit > numBits )
			throw new IndexOutOfBoundsException(bit);
		
		int byteOffset = (int) Math.floor(numBits / 8.0);
		int bitOffset = numBits % 8;
		int bitMask = 1 << bitOffset;
		
		return (bytes[byteOffset] & bitMask) != 0;
	}
	
	public void set(int bit, int value) {
		set(bit, value != 0);
	}
	
	public void set(int bit, boolean value) {
		if ( bit < 0 || bit > numBits )
			throw new IndexOutOfBoundsException(bit);
		
		int byteOffset = (int) Math.floor(numBits / 8.0);
		int bitOffset = numBits % 8;
		int bitMask = 1 << bitOffset;
		
		if (value)
			bytes[byteOffset] |= (byte) bitMask;
		else
			bytes[byteOffset] &= (byte) ~bitMask;
	}
}
