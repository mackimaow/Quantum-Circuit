package appFX.framework.simulator.quickSim;

import java.util.stream.Stream;

import appFX.framework.Project;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.GateManager;
import appFX.framework.exportGates.GateManager.ExportCircuit;
import appFX.framework.exportGates.GateManager.ExportException;
import appFX.framework.exportGates.GateManager.ExportNotCircuit;
import appFX.framework.exportGates.GateManager.Exportable;
import appFX.framework.gateModels.GateModel.GateComputingType;
import utils.StringUtils;
import utils.customCollections.IterableUtils;
import utils.customCollections.Range;
import utils.customMaps.IndexMap;

public class QuickSim {
	
	public static void simulate(Project p) throws ExportException {
		System.out.println("\n\n\n>>>>>  Simulation Started:\n");
		Stream<Exportable> stream = GateManager.exportGates(p);
		
		ExportCircuit first = null;
		for (Exportable e : IterableUtils.convert(stream))
			first = (ExportCircuit) e;
		
		GateComputingType computingType = first.getComputingType();
		
		QuantumState qs;
		ClassicalState cs;
		ClassicalState cs2;
		IndexMap indexMap;
		State[] states;
		switch (computingType) {
		case QUANTUM:
			int numQubits = first.getNumPrimaryRegs();
			int numBits = first.getNumSecondaryRegs();
			qs = new QuantumState(numQubits);
			cs = new ClassicalState(numBits);
			states = new State[] {qs, cs};
			indexMap = new IndexMap(Range.mk(numQubits));
			break;
		case CLASSICAL:
			int numBits1 = first.getNumPrimaryRegs();
			int numBits2 = first.getNumSecondaryRegs();
			cs = new ClassicalState(numBits1);
			cs2 = new ClassicalState(numBits2);
			states = new State[] {cs, cs2};
			indexMap = new IndexMap(Range.mk(numBits1));
			break;
		default:
			throw new RuntimeException(computingType.name() + " is not supported.");
		}
		
		simulate(first.exportIfCircuitBoard(), states, new Control[0], indexMap);
	}
	
	@SuppressWarnings("unused")
	private static void debugSim(Stream<Exportable> stream) {
		debugSim(stream, 0);
	}
	
	private static void simulate(Stream<Exportable> stream, State[] states, Control[] qcs, IndexMap indexMap) {
		stream = stream.takeWhile(x -> x != null);
		stream = stream.filter(e -> !e.isIdentity());
		for (Exportable e : IterableUtils.convert(stream)) {
			
			if (e.isCircuitBoard()) {
				ExportCircuit ec = (ExportCircuit) e;
				Control[] cs = ec.getClassicalControls();
				simulate(e.exportIfCircuitBoard(), states, qcs, indexMap);
			} else {
				ExportNotCircuit enc = (ExportNotCircuit) e;
				
			}
		}
	}
	
	
	private static void debugSim(Stream<Exportable> stream, int timesNest) {
		char [] prefixList = new char[timesNest];
		for (int i : Range.mk(timesNest))
			prefixList[i] = '\t';
		String prefix = String.valueOf(prefixList);
		
		stream = stream.takeWhile(x -> x != null);
		stream = stream.filter(e -> !e.isIdentity());
		for (Exportable e : IterableUtils.convert(stream)) {
			String details = e.toString();
			details = StringUtils.prefixNewLine(details, prefix);
			System.out.println(details);
			
			if (e.isCircuitBoard())
				debugSim(e.exportIfCircuitBoard(), timesNest + 1);
		}
	}
}
