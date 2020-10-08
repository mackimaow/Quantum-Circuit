package appFX.framework.simulator;

import java.util.ArrayList;
import java.util.stream.Stream;

import appFX.framework.Project;
import appFX.framework.exportGates.Control;
import appFX.framework.exportGates.GateManager;
import appFX.framework.exportGates.GateManager.ExportCircuit;
import appFX.framework.exportGates.GateManager.ExportException;
import appFX.framework.exportGates.GateManager.ExportNotCircuit;
import appFX.framework.exportGates.GateManager.Exportable;
import appFX.framework.exportGates.RawExportableGateData.RawExportLink;
import appFX.framework.exportGates.RawExportableGateData.RawExportOutputLink;
import appFX.framework.gateModels.GateModel.GateComputingType;
import utils.StringUtils;
import utils.customCollections.IterableUtils;
import utils.customCollections.Range;
import utils.customMaps.IndexMap;

public class Simulator {
	
	public static State[] simulate(Project p) throws ExportException {
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
		return states;
	}
	
	@SuppressWarnings("unused")
	private static void debugSim(Stream<Exportable> stream) {
		debugSim(stream, 0);
	}
	
	private static void simulate(Stream<Exportable> stream, State[] states, Control[] qcs, IndexMap parentToGlobalMap) {
		stream = stream.takeWhile(x -> x != null);
		stream = stream.filter(e -> !e.isIdentity());
		for (Exportable e : IterableUtils.convert(stream)) {
			boolean runGate = checkClassicalControls(states, e);
			
			if (!runGate)
				continue;
			
			// get local map
			ArrayList<Integer> regs = new ArrayList<>();
			for (int i : e.getRegisters()) 
				regs.add(i);
			IndexMap childToParentMap = new IndexMap(regs);
			
			if (e.isCircuitBoard()) {
				ExportCircuit ec = (ExportCircuit) e;
				
				State[] nextStates = null;
				if (ec.getComputingType() == GateComputingType.QUANTUM) {
					State qs = states[0];
					int numBits = ec.getNumSecondaryRegs();
					ClassicalState cs = new ClassicalState(numBits);
					nextStates = new State[] {qs, cs};
					
					ClassicalState global = (ClassicalState) states[1];
					for (RawExportLink rel : ec.getInputLinks()) {
						int globalReg = rel.globalReg;
						boolean value = global.get(globalReg);
						int localReg = rel.localReg;
						cs.set(localReg, value);
					}

					Control[] qcsNext = bindQuantumControls(qcs, ec.getQuantumControls(), parentToGlobalMap);
					IndexMap childToGlobal = childToParentMap.map(parentToGlobalMap);
					simulate(e.exportIfCircuitBoard(), nextStates, qcsNext, childToGlobal);
					
					for (RawExportOutputLink rel : ec.getOutputLinks()) {
						int localReg = rel.localReg;
						boolean value = cs.get(localReg);
						int globalReg = rel.globalReg;
						switch(rel.linkType) {
						case CLASSICAL_LINK:
							global.set(globalReg, value);
							break;
						default:
							break;
						}
					}
					
				} else {
					State qs = states[0];
					int numBits = ec.getNumPrimaryRegs();
					State cs = new ClassicalState(numBits);
					nextStates = new State[] {qs, cs};
					
					simulate(e.exportIfCircuitBoard(), nextStates, null, null);
				}
				
			} else {
				ExportNotCircuit enc = (ExportNotCircuit) e;
				if (enc.getComputingType() == GateComputingType.QUANTUM) {
					QuantumState qs = (QuantumState) states[0];
					Control[] qcsNext = bindQuantumControls(qcs, e.getQuantumControls(), parentToGlobalMap);
					IndexMap childToGlobal = childToParentMap.map(parentToGlobalMap);
					int measuredValue = qs.apply(enc.getGateModel(), enc.getMathSet(), childToGlobal, (Object) qcsNext);

					ClassicalState cs = (ClassicalState) states[1];
					for (RawExportOutputLink rel : enc.getOutputLinks()) {
						int localReg = rel.localReg;
						boolean value = ((measuredValue >>> localReg) & 1) == 1;
						int globalReg = rel.globalReg;
						switch(rel.linkType) {
						case CLASSICAL_LINK:
							cs.set(globalReg, value);
							break;
						default:
							break;
						}
					}
				} else if (enc.getComputingType() == GateComputingType.CLASSICAL)  {
					ClassicalState cs = null;
					if (states[0] instanceof QuantumState)
						cs = (ClassicalState) states[1];
					else
						cs = (ClassicalState) states[0];
					
					cs.apply(enc.getGateModel(), enc.getMathSet(), childToParentMap);
				}
			}
		}
	}
	
	private static Control[] bindQuantumControls (Control[] global, Control[] local, IndexMap indexMap) {
		int totalSize = global.length + local.length;
		Control[] qcsNext = new Control[totalSize];
		int i = 0;
		for (Control qc : global)
			qcsNext[i++] = qc;
		for (Control qc : local) {
			int reg = qc.getRegister();
			boolean status = qc.getControlStatus();
			int mappedReg = indexMap.get(reg);
			qcsNext[i++] = new Control(mappedReg, status);
		}
		return qcsNext;
	}
	
	private static boolean checkClassicalControls(State[] states, Exportable e) {
		Control[] qcs = e.getQuantumControls();
		Control[] ccs = e.getClassicalControls();
		
		if (states[0] instanceof QuantumState) {
			ClassicalState cs = (ClassicalState) states[1];
			for (Control c : ccs) {
				int i = c.getRegister();
				boolean toCheck = c.getControlStatus() == Control.CONTROL_TRUE;
				boolean val = cs.get(i);
				if (val != toCheck)
					return false;
			}
		} else {
			if (qcs.length != 0) {
				ClassicalState cs = (ClassicalState) states[0];
				for (Control c : qcs) {
					int i = c.getRegister();
					boolean toCheck = c.getControlStatus() == Control.CONTROL_TRUE;
					boolean val = cs.get(i);
					if (val != toCheck)
						return false;
				}
			}
			
			int stateIndex = states[1].size() != 0 ? 1 : 0;
			ClassicalState cs = (ClassicalState) states[stateIndex];
			
			for (Control c : ccs) {
				int i = c.getRegister();
				boolean toCheck = c.getControlStatus() == Control.CONTROL_TRUE;
				boolean val = cs.get(i);
				if (val != toCheck)
					return false;
			}
			
		}
		return true;
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
