package appFX.framework.simulator;

import java.util.Hashtable;

import mathLib.Vector;
import mathLib.operators.OperatorSet;

public class PureState<T> extends QuantumState<T> {
	
	private Vector<T> internalVector;
	
	@SafeVarargs
	public PureState(T ... components){
		internalVector = new Vector<>(components);
	}

	public PureState(Vector<T> v) {
		internalVector = v;
	}

	@SafeVarargs
	public PureState(T elementToInferOperator, boolean isVertical, T ... components){		
		internalVector = new Vector<>(elementToInferOperator, isVertical, components);
	}
	
	@SafeVarargs
	public PureState(OperatorSet<T> operatorSet, boolean isVertical, T ... components){		
		internalVector = new Vector<>(operatorSet, isVertical, components);
	}
	
	@Override
	public MixedState<T> toMixedState() {
		return new MixedState<>(internalVector.mult(internalVector.conjugateTranspose()));
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
