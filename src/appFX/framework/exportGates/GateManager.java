package appFX.framework.exportGates;

import java.util.Hashtable;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.function.Supplier;
import java.util.stream.Stream;

import appFX.appUI.utils.AppAlerts;
import appFX.framework.AppCommand;
import appFX.framework.AppStatus;
import appFX.framework.MathDefinitions;
import appFX.framework.Project;
import appFX.framework.exportGates.RawExportableGateData.RawExportControl;
import appFX.framework.exportGates.RawExportableGateData.RawExportRegister;
import appFX.framework.gateModels.BasicGateModel;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.utils.InputDefinitions.ArgObject;
import appFX.framework.utils.InputDefinitions.GroupDefinition;
import appFX.framework.utils.InputDefinitions.MathObject;
import appFX.framework.utils.InputDefinitions.MatrixObject;
import appFX.framework.utils.InputDefinitions.ScalarObject;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Window;
import mathLib.Complex;
import mathLib.Matrix;
import mathLib.expression.Expression.EvaluateExpressionException;
import mathLib.expression.MathSet;
import mathLib.expression.Variable.ConcreteVariable;
import utils.customCollections.Queue;
import utils.customCollections.Stack;
import utils.customCollections.immutableLists.ImmutableArray;

public class GateManager {
	
	public static Stream<ExportedGate> exportGates(String circuitboardName) throws ExportException {
		ExportTree et = startScanAndGetExportStream(circuitboardName, MathDefinitions.GLOBAL_DEFINITIONS);
		return Stream.generate(new DefaultExportGatesSupplier(et)).takeWhile(x -> x != null);
	}
	
	public static Stream<ExportedGate> exportGates(Project p) throws ExportException {
		return exportGates(p.getTopLevelCircuitLocationString());
	}
	
	public static Stream<Exportable> exportGatesRecursively(Project p) throws ExportException {
		return exportGatesRecursively(p.getTopLevelCircuitLocationString());
	}
	
	public static Stream<Exportable> exportGatesRecursively(String circuitboardName) throws ExportException {
		return exportGatesRecursively(startScanAndGetExportStream(circuitboardName, MathDefinitions.GLOBAL_DEFINITIONS));
	}
	
	private static Stream<Exportable> exportGatesRecursively(ExportTree et) {
		return Stream.generate(new RecursiveExportGatesSupplier(et)).takeWhile(x -> x != null);
	}
	
	
	
	
	public static interface Exportable {
		public Stream<Exportable> exportIfCircuitBoard();
		public ExportedGate exportIfNotCircuitBoard();
		public boolean isCircuitBoard();
		public int getColumn();
		public int[] getRegisters();
		public int[] getUnderneathIdentityRegisters();
		public Control[] getQuantumControls();
		public Control[] getClassicalControls();
	}
	
	
	
	private static class ExportNotCircuit implements Exportable {
		private final int column;
		private final ExportLeaf n;
		
		private ExportNotCircuit(ExportLeaf n, int column) {
			this.column = column;
			this.n = n;
		}

		@Override
		public Stream<Exportable> exportIfCircuitBoard() {
			return null;
		}

		@Override
		public ExportedGate exportIfNotCircuitBoard() {
			return toExportedGate(n);
		}

		@Override
		public boolean isCircuitBoard() {
			return false;
		}

		@Override
		public int getColumn() {
			return column;
		}

		@Override
		public int[] getRegisters() {
			RawExportableGateData regs = n.rawData;
			Hashtable<Integer, RawExportRegister> registers = regs.getRegisters();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i).globalReg;
			
			return temp;
		}

		@Override
		public int[] getUnderneathIdentityRegisters() {
			RawExportableGateData regs = n.rawData;
			LinkedList<RawExportRegister> registers = regs.getUnderneathQuantumIdentityGates();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i).globalReg;
			
			return temp;
		}
		
		
		@Override
		public Control[] getQuantumControls() {
			RawExportableGateData exportData = n.rawData;
			return getControls(exportData, false);
		}

		@Override
		public Control[] getClassicalControls() {
			RawExportableGateData exportData = n.rawData;
			return getControls(exportData, true);
		}
	}
	
	
	private static class ExportCircuit implements Exportable {
		private int column;
		private ExportTree tree;
		
		private ExportCircuit(ExportTree tree, int column) {
			this.column = column;
			this.tree = tree;
		}
		
		@Override
		public Stream<Exportable> exportIfCircuitBoard() {
			return exportGatesRecursively(tree);
		}

		@Override
		public ExportedGate exportIfNotCircuitBoard() {
			return null;
		}

		@Override
		public boolean isCircuitBoard() {
			return true;
		}

		@Override
		public int getColumn() {
			return column;
		}

		@Override
		public int[] getRegisters() {
			RawExportableGateData regs = tree.rawData;
			Hashtable<Integer, RawExportRegister> registers = regs.getRegisters();
			int[] temp = new int[registers.size()];
			
			for(int i = 0; i < registers.size(); i++)
				temp[i] = registers.get(i).globalReg;
			
			return temp;
		}
		
		@Override
		public int[] getUnderneathIdentityRegisters() {
			RawExportableGateData regs = tree.rawData;
			LinkedList<RawExportRegister> gates = regs.getUnderneathQuantumIdentityGates();
			int[] temp = new int[gates.size()];
			
			for(int i = 0; i < gates.size(); i++)
				temp[i] = gates.get(i).globalReg;
			
			return temp;
		}
		
		@Override
		public Control[] getQuantumControls() {
			RawExportableGateData exportData = tree.rawData;
			return getControls(exportData, false);
		}

		@Override
		public Control[] getClassicalControls() {
			RawExportableGateData exportData = tree.rawData;
			return getControls(exportData, true);
		}
		
	}
	
	
	private static Control[] getControls(RawExportableGateData exportData, boolean classicalControls) {
		LinkedList<RawExportControl> exportControls;
		if(classicalControls)
			exportControls = exportData.getClassicalControls();
		else 
			exportControls = exportData.getQuantumControls();
		Control[] temp = new Control[exportControls.size()];
		int i = 0;
		for(RawExportControl exportControl : exportControls)
			temp[i++] = new Control(exportControl.globalReg, exportControl.controlStatus);
		return temp;
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private static ExportedGate toExportedGate(ExportLeaf leaf) {
		
		GateModel gm = leaf.gm;
		Hashtable<String, Complex> argParamTable = leaf.parameters;
		BasicGateModel dg = (BasicGateModel) gm;
		ImmutableArray<MathObject> definitions = dg.getDefinitions();
		Matrix<Complex>[] matrixes = new Matrix[definitions.size()];
		
		for (int i = 0 ; i < matrixes.length; i++) {
			try {
				MathObject mo = definitions.get(i);
				if(mo.hasArguments())
					matrixes[i] = (Matrix<Complex>) ((ArgObject) mo).getDefinition().compute(leaf.runtimeVariables);
				else
					matrixes[i] = (Matrix<Complex>) ((MatrixObject) mo).getMatrix();
			} catch (EvaluateExpressionException e) {
				e.printStackTrace();
				throw new RuntimeException(e.getMessage());
			}
		}
		
		Hashtable<Integer, RawExportRegister> tempRegs = leaf.rawData.getRegisters();
		int[] registers = new int[tempRegs.size()];
		for(int i = 0; i < tempRegs.size(); i++)
			registers[i] = tempRegs.get(i).globalReg;

		Control[] classicalControls = getControls(leaf.rawData, true);
		Control[] quantumControls = getControls(leaf.rawData, false);
		return new ExportedGate(dg, argParamTable, registers, !leaf.rawData.isQuantum(), classicalControls, quantumControls, matrixes);
	}
	
	
	
	
	private static class DefaultExportGatesSupplier extends AbstractExportGatesSupplier {
		private DefaultExportGatesSupplier(ExportTree et) {
			super(et);
		}

		
		// TODO: add functionality for classical controls
		@Override
		public void doActionToNode(ExportTree previous, ExportNode next) {
			if(previous.rawData != null) {
				Hashtable<Integer, RawExportRegister> registers = previous.rawData.getRegisters();
				Hashtable<Integer, RawExportRegister> nextRegisters = next.rawData.getRegisters();
				
				for(int i = 0; i < nextRegisters.size(); i++)
					nextRegisters.put(i, registers.get(nextRegisters.get(i).globalReg));
				
				LinkedList<RawExportControl> quantumControls = next.rawData.getQuantumControls();
				ListIterator<RawExportControl> li = quantumControls.listIterator();
				
				while(li.hasNext()){
					RawExportControl previousControl = li.next();
					int previousReg = previousControl.globalReg;
					boolean previousStat = previousControl.controlStatus;
					li.set(new RawExportControl(registers.get(previousReg).globalReg, -1, previousStat));
				}
				quantumControls.addAll(previous.rawData.getQuantumControls());
			}
		}
		
		@Override
		public ExportedGate export(ExportLeaf leaf) {
			return toExportedGate(leaf);
		}
	}
	
	
	
	
	
	
	private static class RecursiveExportGatesSupplier implements Supplier<Exportable> {
		private ExportTree et;
		
		private RecursiveExportGatesSupplier(ExportTree et) {
			this.et = et;
		}
		
		@Override
		public Exportable get() {
			Queue<ExportNode> nodes = et.exportNodes;
			if(nodes.size() == 0) return null;
			
			ExportNode node = et.exportNodes.dequeue();
			if(node instanceof ExportTree)
				return new ExportCircuit((ExportTree) node, et.rawData.getColumn());
			else
				return new ExportNotCircuit((ExportLeaf) node, et.rawData.getColumn());
		}
		
	}
	
	
	
	
	
	
	
	
	
	private abstract static class AbstractExportGatesSupplier  implements Supplier<ExportedGate> {
		Stack<ExportTree> currentState = new Stack<>();
		
		private AbstractExportGatesSupplier(ExportTree et) {
			currentState.push(et);
		}
		
		@Override
		public ExportedGate get() {
			while(!currentState.isEmpty()) {
				ExportTree tree = currentState.peak();
				Queue<ExportNode> nodes = tree.exportNodes;
				
				if(nodes.isEmpty()) {
					currentState.pop();
					continue;
				}
				
				ExportNode n = nodes.dequeue();
				
				doActionToNode(tree, n);
				
				if(n instanceof ExportTree) {
					currentState.push((ExportTree) n);
					continue;
				} else {
					return export((ExportLeaf) n);
				}
			}
			return null;
		}
		public abstract void doActionToNode(ExportTree previous, ExportNode next);
		public abstract ExportedGate export(ExportLeaf leaf);
		
	}
	
	
	
	
		
	private static ExportTree startScanAndGetExportStream (String circuitboardName, MathSet runtimeVariables) throws ExportException {
		
		Project p = AppStatus.get().getFocusedProject();
		CircuitBoardModel cb = (CircuitBoardModel) p.getGateModel(circuitboardName);
		
		if(cb == null) throw new ExportException("Gate \"" + circuitboardName + "\" is not valid or does not exist", null, 0, 0, 0);
		
		Hashtable<Integer, Integer> registers = new Hashtable<>();
		for(int i = 0; i < cb.getRows(); i++)
			registers.put(i, i);
		
		return scanCB(p, cb, runtimeVariables, null);
	}
	
	
	
	
	
	
	
	
	private static ExportTree scanCB (Project p, CircuitBoardModel cb, MathSet runtimeVariables, RawExportableGateData data) throws ExportException {
		
		Queue<ExportNode> nodes = new Queue<>();
		
		for(RawExportableGateData rawData : cb) {
			SolderedGate sg = rawData.getSolderedGate();
			GateModel gm = p.getGateModel(sg.getGateModelLocationString());
			
			if(gm == null) throw new ExportException("Gate \"" + sg.getGateModelLocationString() + 
					"\" is not valid or does not exist in \"" + cb.getLocationString() + "\"", cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
			

			GroupDefinition        parameters = sg.getParameterSet();
			ImmutableArray<String> arguments = gm.getParameters();
			
			
			if(arguments.size() != parameters.getSize()) {
				throw new ExportException("Gate \"" + sg.getGateModelLocationString() + "\" in \"" 
											+ cb.getLocationString() + "\" are missing necessary arguments", cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
			}
			
			if(rawData.getRegisters().size() != gm.getNumberOfRegisters()) {
				throw new ExportException("Gate \"" + sg.getGateModelLocationString() + "\" in \"" 
						+ cb.getLocationString() + "\" is not the appropriate size", cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
			}
			
			int i = 0;
			Complex c;
			
			ExportNode n = null;
			MathSet ms = new MathSet(MathDefinitions.GLOBAL_DEFINITIONS);
			
			try {
				if(gm instanceof CircuitBoardModel) {
					for(MathObject mo : parameters.getMathDefinitions()) {
						if(mo.isMatrix())
							throw new ExportException("Gate \"" + sg.getGateModelLocationString() + "\" in \"" 
									+ cb.getLocationString() + "\" cannot not pass a matrix in parameter " + i, cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
						
						if(mo.hasArguments())
							c = (Complex) ((ArgObject) mo).getDefinition().compute(runtimeVariables);
						else
							c = (Complex) ((ScalarObject) mo).getScalar();
						
						ms.addVariable(new ConcreteVariable(arguments.get(i++), c));
					}
					n = scanCB(p, (CircuitBoardModel) gm, ms, rawData);
				} else {
					Hashtable<String, Complex> argParamTable = new Hashtable<>();
					for(MathObject mo : parameters.getMathDefinitions()) {
						if(mo.isMatrix())
							throw new ExportException("Gate \"" + sg.getGateModelLocationString() + "\" in \"" 
									+ cb.getLocationString() + "\" cannot not pass a matrix in parameter " + i, cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
						
						if(mo.hasArguments())
							c = (Complex) ((ArgObject) mo).getDefinition().compute(runtimeVariables);
						else
							c = (Complex) ((ScalarObject) mo).getScalar();
						
						argParamTable.put(arguments.get(i), c);
						ms.addVariable(new ConcreteVariable(arguments.get(i++), c));
					}
					n = new ExportLeaf(argParamTable, gm, ms, rawData);
				}
				
			} catch (EvaluateExpressionException e) {
				throw new ExportException("Gate \"" + sg.getGateModelLocationString() + "\" in \"" 
						+ cb.getLocationString() + "\" could not evaluate parameter " + i + " due to: " + e.getMessage(), cb.getLocationString(), rawData.getGateRowBodyStart(), rawData.getGateRowBodyEnd(), rawData.getColumn());
			}
			
			
			nodes.enqueue(n);
		}
		
		return new ExportTree(nodes, data);
	}
	
	
	
	
	
	
	
	@SuppressWarnings("serial")
	public static class ExportException extends Exception {
		public final int rowStart, rowEnd, column;
		public final String circuitBoardName;
		private ExportException (String message, String circuitBoardName, int rowStart, int rowEnd, int column) {
			super(message);
			this.circuitBoardName = circuitBoardName;
			this.rowStart = rowStart;
			this.rowEnd = rowEnd;
			this.column = column;
		}
		
		public void showExportErrorSource() {
			Window w = AppStatus.get().getPrimaryStage();
        	AppAlerts.showMessage(w, "Export Failed", getMessage(), AlertType.ERROR);
        	if(circuitBoardName != null)
        		AppCommand.doAction(AppCommand.OPEN_CIRCUIT_BOARD_AND_SHOW_ERROR, circuitBoardName, rowStart, rowEnd, column);
		}
		
		
	}
	
	
	
	
	
	
	
	private static class ExportTree extends ExportNode {
		
		final Queue<ExportNode> exportNodes;
		
		public ExportTree(Queue<ExportNode> exportStates, RawExportableGateData rawData) {
			super(rawData);
			this.exportNodes = exportStates;
		}
		
		
	}
	
	
	
	
	
	
	
	private abstract static class ExportNode {
		final RawExportableGateData rawData;
		
		public ExportNode (RawExportableGateData rawData) {
			this.rawData = rawData;
		}
	}
	
	
	
	
	
	
	private static class ExportLeaf extends ExportNode {
		final Hashtable<String, Complex> parameters;
		final GateModel gm;
		final MathSet runtimeVariables;
		
		private ExportLeaf(Hashtable<String, Complex> parameters, GateModel gm, MathSet runtimeVariables, RawExportableGateData rawData) {
			super(rawData);
			this.runtimeVariables = runtimeVariables;
			this.parameters = parameters;
			this.gm = gm;
		}
	}
	
	
}
