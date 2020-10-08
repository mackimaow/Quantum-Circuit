package appFX.framework.simulator;

import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.ClassicalGateDefinition;
import appFX.framework.gateModels.GateModel;
import mathLib.equation.BooleanEquation;
import mathLib.expression.MathSet;
import utils.customCollections.Range;
import utils.customCollections.immutableLists.ImmutableArray;
import utils.customMaps.IndexMap;

public class ClassicalState implements State {
	private final byte[] bytes;
	private final int numBits;
	
	public ClassicalState(int numBits) {
		int numBytes = (int) Math.ceil(numBits / 8.0);
		this.bytes = new byte[numBytes];
		this.numBits = numBits;
	}
	
	@Override
	public int apply(GateModel gm, MathSet mathSet, IndexMap map, Object ... args) {
		BasicGateModel bgm = (BasicGateModel) gm;
		ClassicalGateDefinition cgd = bgm.getClassicalGateDefinition();
		ImmutableArray<BooleanEquation> eqs = cgd.getDefinitions();
		for (BooleanEquation eq : eqs)
			eq.compute(this::get, this::set, map);
		return 0;
	}
	
	@Override
	public int size() {
		return numBits;
	}
	
	public boolean get(int bit) {
		if ( bit < 0 || bit > numBits )
			throw new IndexOutOfBoundsException(bit);
		
		int byteOffset = (int) Math.floor(bit / 8.0);
		int bitOffset = bit % 8;
		int bitMask = 1 << bitOffset;
		
		return (bytes[byteOffset] & bitMask) != 0;
	}
	
	public void set(int bit, int value) {
		set(bit, value != 0);
	}
	
	public void set(int bit, boolean value) {
		if ( bit < 0 || bit > numBits )
			throw new IndexOutOfBoundsException(bit);
		
		int byteOffset = (int) Math.floor(bit / 8.0);
		int bitOffset = bit % 8;
		int bitMask = 1 << bitOffset;
		
		if (value)
			bytes[byteOffset] |= (byte) bitMask;
		else
			bytes[byteOffset] &= (byte) ~bitMask;
	}
	
	@Override
	public String toString() {
		char[] comps = new char[numBits];
		for (int i : Range.mk(numBits-1, -1, -1))
			comps[i] = get(i) ? '1' : '0';
		return "[Classical] : \n" + new String(comps);
	}
}
