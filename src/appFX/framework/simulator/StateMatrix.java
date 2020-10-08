package appFX.framework.simulator;

import java.util.ArrayList;
import java.util.HashSet;

import appFX.framework.exportGates.Control;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.Vector;
import utils.customCollections.Range;
import utils.customMaps.IndexMap;

public class StateMatrix {
	private final int numQubits;
	private final double[] real;
	private final double[] imag;
	
	public StateMatrix (int numQubits) {
		this(numQubits, new double[1 << numQubits], new double[1 << numQubits]);
		real[0] = 1;
	}
	
	private StateMatrix (int numQubits, double[] real, double[] imag) {
		this.numQubits = numQubits;
		this.real = real;
		this.imag = imag;
	}
	
	public int getNumQubits() {
		return numQubits;
	}
	
	public double observableProbability(StateMatrix sm) {
		double prob = 0;
		for (int i : Range.mk(real.length)) {
			double rv1 = real[i];
			double iv1 = imag[i];
			double rv2 = sm.real[i];
			double iv2 = sm.imag[i];
			prob += rv1 * rv2 + iv1 * iv2;
		}
		return prob;
	}
	
	public double observableProbability() {
		double prob = 0;
		for (int i : Range.mk(real.length)) {
			double rv = real[i];
			double iv = imag[i];
			prob += rv * rv + iv * iv;
		}
		return prob;
	}
	
	public void normalize() {
		double magS = observableProbability();
		double sqrtMag = Math.sqrt(magS);
		if (sqrtMag == 0.0)
			return;
		for (int i : Range.mk(real.length)) {
			real[i] /= sqrtMag;
			imag[i] /= sqrtMag;
		}
	}
	
	public void mult(Matrix<Complex> matrix, IndexMap regIndexMap, Control[] qcs) {
		
		int[] indicators = makeIndexIndicators(qcs, regIndexMap);
		int zeroEntries = indicators[0];
		int oneEntries = indicators[1];
		int both = zeroEntries & oneEntries;
		int controlTrueEntries = oneEntries & ~zeroEntries;
		
		ArrayList<Integer> bothIndicies = new ArrayList<>();
		for (int i : Range.mk(numQubits)) { // TODO: find a better way to do this
			int mask = 1 << i;
			if ((both & mask) != 0)
				bothIndicies.add(mask);
		}

		double[][] comps = split(matrix);
		double[] realMat = comps[0];
		double[] imagMat = comps[1];
		
		if (bothIndicies.size() == 0) {
			mult(realMat, imagMat, regIndexMap, controlTrueEntries);
		} else {
			int size = bothIndicies.size();
			int timesThrough = 1 << size;
			for (int i : Range.mk(timesThrough)) { // TODO: Make these iterations parallel (GPU?)
				int mask = 0;
				for (int j : Range.mk(size)) // TODO: Find someway to bypass this for loop
					if ((i & (1 << j)) != 0)
						mask |= bothIndicies.get(j);
				mult(realMat, imagMat, regIndexMap, mask | controlTrueEntries);
			}
		}
	}
	
	private void mult(double[] realMat, double[] imagMat, IndexMap indexMap, int mask) {
		int size = 1 << indexMap.size();
		
		double[] realOutput 	= new double[size];
		double[] imagOutput 	= new double[size];
		double[] temp 			= new double[size];
		
		mult(real, realMat, realOutput, indexMap, mask);
		mult(imag, imagMat, temp, indexMap, mask);
		sub(realOutput, temp, realOutput);
		
		mult(real, imagMat, imagOutput, indexMap, mask);
		mult(imag, realMat, temp, indexMap, mask);
		add(imagOutput, temp, imagOutput);
		
		set(real, realOutput, indexMap, mask);
		set(imag, imagOutput, indexMap, mask);
	}
	
	private void mult(double[] vector, double[] mat, double[] output, IndexMap indexMap, int mask) {
		int size = 1 << indexMap.size();
		for (int r : Range.mk(size)) {
			double sum = 0;
			for (int c : Range.mk(size)) {
				int matrixIndex = r + c * size;
				int mappedIndex = mapIndexComponents(c, indexMap) | mask;
				sum += vector[mappedIndex] * mat[matrixIndex];
			}
			output[r] = sum;
		}
	}
	
	private void add(double[] first, double[] second, double[] output) {
		for (int i : Range.mk(first.length))
			output[i] = first[i] + second[i];
	}
	
	private void sub(double[] first, double[] second, double[] output) {
		for (int i : Range.mk(first.length))
			output[i] = first[i] - second[i];
	}
	
	private void set(double[] vector, double[] toSetVector, IndexMap indexMap, int mask) {
		for (int i : Range.mk(toSetVector.length)) {
			int mappedIndex = mapIndexComponents(i, indexMap) | mask;
			vector[mappedIndex] = toSetVector[i];
		}
	}
	
	// TODO: Speed this up (find some other way to do this)
	private int mapIndexComponents(int indexComps, IndexMap indexMap) {
		int size = indexMap.size();
		int output = 0;
		for (int i : Range.mk(size)) {
			int mask = 1 << (size - 1 - i);
			if ((indexComps & mask) == 0)
				continue;
			int mappedIndex = indexMap.get(i);
			output |= 1 << (numQubits - 1 - mappedIndex);
		}
		return output;
	}
	
	public StateMatrix copy() {
		double[] realCopy = new double[real.length];
		double[] imagCopy = new double[imag.length];
		for (int i : Range.mk(real.length)) {
			realCopy[i] = real[i];
			imagCopy[i] = imag[i];
		}
		return new StateMatrix(numQubits, realCopy, imagCopy);
	}
	
	private int[] makeIndexIndicators(Control[] qcs, IndexMap regIndexMap) {
		HashSet<Integer> mapIndicies = new HashSet<Integer>();
		regIndexMap.forEach(mapIndicies::add);
		
		int bothNeg = 0;
		for (int i : mapIndicies)
			bothNeg |= 1 << (numQubits - 1 - i);
		
		int negatedZeroEntries = bothNeg;
		int negatedOneEntries  = bothNeg;
		
		for (Control c : qcs) {
			int reg = c.getRegister();
			if (c.getControlStatus() == Control.CONTROL_TRUE)
				negatedZeroEntries |= 1 << (numQubits - 1 - reg);
			else
				negatedOneEntries  |= 1 << (numQubits - 1 - reg);
		}
		
		int mask = (1 << numQubits) - 1;
		return new int[] {mask & ~negatedZeroEntries, mask & ~negatedOneEntries};
	}
	
	private static double[][] split(Matrix<Complex> matrix) {
		int size = matrix.getRows();
		int numEntries = 1 << matrix.getRows();
		double[] real 	= new double[numEntries];
		double[] imag = new double[numEntries];
		for (int row : Range.mk(size)) {
			for (int column : Range.mk(size)) {
				Complex c = matrix.v(row, column);
				int index = row + column * size;
				real[index] = c.getReal();
				imag[index] = c.getImaginary();
			}
		}
		return new double[][] {real, imag};
	}
	
	@Override
	public String toString() {
		int size = real.length;
		Complex[] vectorComps = new Complex[size];
		for (int i : Range.mk(size))
			vectorComps[i] = new Complex(real[i], imag[i]);
		Vector<Complex> vector = new Vector<>(vectorComps);
		return vector.toString();
	}
}
