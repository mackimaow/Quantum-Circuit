package appFX.framework.simulator;

import java.util.Hashtable;

import mathLib.Matrix;

public class MixedState<T> extends QuantumState<T> {
	private Matrix<T> internalMatrix;
	
	public MixedState (Matrix<T> internalMatrix) {
		this.internalMatrix = internalMatrix;
	}
	
	@Override
	public MixedState<T> toMixedState() {
		return this;
	}

	@Override
	public QuantumState<T> applyOperation(UnitaryOperation<T>[] operation) {
		return null;
	}

	@Override
	public QuantumState<T> applyMeasurement(KrausOperation<T> operations) {
		return null;
	}

	@Override
	public QuantumState<T> applyMeasurement(PovmOperation<T> operations) {
		return null;
	}

	@Override
	public QuantumState<T> applyLookMeasurement(KrausOperation<T> operations,
			Hashtable<Integer, Integer> outputToClassicalGlobalRegs, ClassicalRegisterBus classicalBus) {
		return null;
	}

	@Override
	public QuantumState<T> applyLookMeasurement(PovmOperation<T> operations,
			Hashtable<Integer, Integer> outputToClassicalGlobalRegs, ClassicalRegisterBus classicalBus) {
		return null;
	}
}
