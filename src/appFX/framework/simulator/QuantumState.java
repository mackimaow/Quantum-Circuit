package appFX.framework.simulator;

import java.util.Hashtable;

public abstract class QuantumState<T> {
	
	public abstract MixedState<T> toMixedState();
	public abstract QuantumState<T> applyOperation(UnitaryOperation<T>[] operation);
	public abstract QuantumState<T> applyMeasurement(KrausOperation<T> operations);
	public abstract QuantumState<T> applyMeasurement(PovmOperation<T> operations);
	public abstract QuantumState<T> applyLookMeasurement(KrausOperation<T> operations, Hashtable<Integer, Integer> outputToClassicalGlobalRegs, ClassicalRegisterBus classicalBus);
	public abstract QuantumState<T> applyLookMeasurement(PovmOperation<T> operations, Hashtable<Integer, Integer> outputToClassicalGlobalRegs, ClassicalRegisterBus classicalBus);
}
