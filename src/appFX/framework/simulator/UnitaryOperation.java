package appFX.framework.simulator;

import mathLib.Matrix;

public class UnitaryOperation<T> extends Operation<T> {
	Matrix<T> unitaryMatrix;
	
	public UnitaryOperation (int[] registers, Matrix<T> unitaryMatrix) {
		super(registers);
		this.unitaryMatrix = unitaryMatrix;
	}
}
