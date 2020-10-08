package appFX.framework.simulator;

import appFX.framework.exportGates.Control;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.QuantumGateDefinition;
import appFX.framework.utils.InputDefinitions.ArgObject;
import appFX.framework.utils.InputDefinitions.MathObject;
import appFX.framework.utils.InputDefinitions.MatrixObject;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.Expression.EvaluateExpressionException;
import mathLib.expression.MathSet;
import utils.customCollections.immutableLists.ImmutableArray;
import utils.customMaps.IndexMap;

public class QuantumState implements State {
	
	private StateMatrix stateMatrix;
	
	public QuantumState (int numQubits) {
		this.stateMatrix = new StateMatrix(numQubits);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public int apply(GateModel gm, MathSet mathSet, IndexMap map, Object ... args) {
		BasicGateModel bgm = (BasicGateModel) gm;
		QuantumGateDefinition qgd = bgm.getQuantumGateDefinition();
		ImmutableArray<MathObject> definitions = qgd.getDefinitions();
		Matrix<Complex>[] matrixes = new Matrix[definitions.size()];
		
		for (int i = 0 ; i < matrixes.length; i++) {
			try {
				MathObject mo = definitions.get(i);
				if(mo.hasArguments())
					matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(mathSet);
				else
					matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
			} catch (EvaluateExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		
		Control[] qcs = (Control[]) args[0];
		switch (qgd.getQuantumGateType()) {
		case HAMILTONIAN:
			throw new UnsupportedOperationException("QuaCC does not support Hamiltonian Simulation.");
			
		case KRAUS_OPERATORS:
			double predictor = Math.random();
			double probSum = 0;
			int i = 0;
			for (Matrix<Complex> mat : matrixes) {
				StateMatrix sm = this.stateMatrix.copy();
				sm.mult(mat, map, qcs);
				
				double prob = sm.observableProbability();
				probSum += prob;
				
				if (predictor < probSum) {
					sm.mult(mat.adjugate(), map, qcs);
					sm.normalize();
					this.stateMatrix = sm;
					break;
				}
				i ++;
			}
			return i;
			
		case POVM:
			predictor = Math.random();
			probSum = 0;
			for (Matrix<Complex> mat : matrixes) {
				StateMatrix sm = this.stateMatrix.copy();
				sm.mult(mat, map, qcs);
				
				double prob = sm.observableProbability(this.stateMatrix);
				probSum += prob;
				
				if (predictor < probSum) {
					sm.normalize();
					this.stateMatrix = sm;
					break;
				}
			}
			break;
			
		case UNIVERSAL:
			this.stateMatrix.mult(matrixes[0], map, qcs);
			break;
			
		default:
			break;
		
		}
		
		return -1;
	}
	
	@Override
	public int size() {
		return this.stateMatrix.getNumQubits();
	}
	
	@Override
	public String toString() {
		return "[Quantum] : \n" + stateMatrix.toString();
	}
	
}
