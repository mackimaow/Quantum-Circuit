package appFX.framework.simulator;

import mathLib.Matrix;

public class KrausOperation<T> extends Operation<T> {
	final Matrix<T>[] krausList;
	
	public KrausOperation(Matrix<T>[] krausList, int[] registers) {
		super(registers);
		this.krausList = krausList;
	}
}
