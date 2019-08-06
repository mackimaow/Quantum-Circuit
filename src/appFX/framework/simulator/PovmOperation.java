package appFX.framework.simulator;

import mathLib.Matrix;

public class PovmOperation<T> extends Operation<T> {
	final Matrix<T>[] povmList;
	
	public PovmOperation(Matrix<T>[] povmList, int[] registers) {
		super(registers);
		this.povmList = povmList;
	}
}
