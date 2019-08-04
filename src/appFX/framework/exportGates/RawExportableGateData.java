package appFX.framework.exportGates;

import java.util.Hashtable;
import java.util.LinkedList;

import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SpacePin.OutputLinkType;

public class RawExportableGateData {
	private final SolderedGate sg;
	private final LinkedList<RawExportControl> quantumControls;
	private final LinkedList<RawExportControl> classicalControls;
	private final LinkedList<RawExportOutputLink> outputLinks;
	private final LinkedList<RawExportLink> inputLinks;
	private final LinkedList<RawExportRegister> underneathQuantumIdentityGates;
	private final Hashtable<Integer, RawExportRegister> registers;
	private final boolean classicalReg;
	private final int gateRowSpaceStart, gateRowSpaceEnd, gateRowBodyStart, gateRowBodyEnd, column;
	
	public RawExportableGateData(SolderedGate sg, LinkedList<RawExportControl> quantumControls,
		LinkedList<RawExportControl> classicalControls, LinkedList<RawExportOutputLink> outputLinks,
		LinkedList<RawExportLink> inputLinks, LinkedList<RawExportRegister> underneathQuantumIdentityGates,
		Hashtable<Integer, RawExportRegister> registers, boolean classicalReg, int gateRowSpaceStart, 
		int gateRowSpaceEnd, int gateRowBodyStart, int gateRowBodyEnd, int column) {
		
		this.sg = sg;
		this.quantumControls = quantumControls;
		this.classicalControls = classicalControls;
		this.outputLinks = outputLinks;
		this.inputLinks = inputLinks;
		this.underneathQuantumIdentityGates = underneathQuantumIdentityGates;
		this.registers = registers;
		this.classicalReg = classicalReg;
		this.gateRowSpaceStart = gateRowSpaceStart;
		this.gateRowSpaceEnd = gateRowSpaceEnd;
		this.gateRowBodyStart = gateRowBodyStart;
		this.gateRowBodyEnd = gateRowBodyEnd;
		this.column = column;
	}

	public SolderedGate getSolderedGate() {
		return sg;
	}

	public boolean isQuantum() {
		return !classicalReg;
	}
	
	public Hashtable<Integer, RawExportRegister> getRegisters() {
		return registers;
	}
	
	public LinkedList<RawExportOutputLink> getOutputLinks() {
		return outputLinks;
	}
	
	public LinkedList<RawExportLink> getInputLinks() {
		return inputLinks;
	}
	
	public LinkedList<RawExportControl> getClassicalControls() {
		return classicalControls;
	}
	
	public LinkedList<RawExportControl> getQuantumControls() {
		return quantumControls;
	}

	public LinkedList<RawExportRegister> getUnderneathQuantumIdentityGates () {
		return underneathQuantumIdentityGates;
	}
	
	public int[] getInputLinksRowBounds() {
		if(inputLinks.isEmpty())
			return null;
		RawExportLink first = inputLinks.getFirst();
		RawExportLink last = inputLinks.getLast();
		return new int[] {first.row, last.row};
	}
	
	public int[] getOutputLinksRowBounds() {
		if(outputLinks.isEmpty())
			return null;
		RawExportOutputLink first = outputLinks.getFirst();
		RawExportOutputLink last = outputLinks.getLast();
		return new int[] {first.row, last.row};
	}
	
	public int[] getControlRowBounds() {
		int lowerBound = Integer.MAX_VALUE;
		int upperBound = -1;
		if(!classicalControls.isEmpty()) {
			RawExportControl first = classicalControls.getFirst();
			RawExportControl last = classicalControls.getLast();
			lowerBound = first.row;
			upperBound = last.row;
		}
		if(!quantumControls.isEmpty()) {
			RawExportControl first = quantumControls.getFirst();
			RawExportControl last = quantumControls.getLast();
			int quantumLowerBound = first.row;
			int quantumUpperBound = last.row;
			
			lowerBound = quantumLowerBound < lowerBound? quantumLowerBound : lowerBound;
			upperBound = quantumUpperBound > upperBound? quantumUpperBound : upperBound;
		}
		return upperBound == -1? null : new int[] {lowerBound, upperBound};
	}
	
	public boolean hasInputLinks() {
		return !inputLinks.isEmpty();
	}
	
	public boolean hasOutputLinks() {
		return !outputLinks.isEmpty();
	}
	
	public int getGateRowSpaceStart() {
		return gateRowSpaceStart;
	}

	public int getGateRowSpaceEnd() {
		return gateRowSpaceEnd;
	}

	public int getGateRowBodyStart() {
		return gateRowBodyStart;
	}

	public int getGateRowBodyEnd() {
		return gateRowBodyEnd;
	}

	public int getColumn() {
		return column;
	}
	
	public static class RawExportRegister {
		public final int globalReg;
		public final int row;
		
		public RawExportRegister(int globaleReg, int row) {
			this.globalReg = globaleReg;
			this.row = row;
		}
	}
	
	public static class RawExportControl extends RawExportRegister {
		public final boolean controlStatus;
		
		public RawExportControl(int globaleReg, int row, boolean controlStatus) {
			super(globaleReg, row);
			this.controlStatus = controlStatus;
		}
	}
	
	public static class RawExportLink extends RawExportRegister {
		public final int localReg;
		
		public RawExportLink(int globaleReg, int row, int inputReg) {
			super(globaleReg, row);
			this.localReg = inputReg;
		}
	}
	
	public static class RawExportOutputLink extends RawExportLink {
		public final OutputLinkType linkType;
		
		public RawExportOutputLink(int globaleReg, int row, int outputReg, OutputLinkType linkType) {
			super(globaleReg, row, outputReg);
			this.linkType = linkType;
		}
	}
	
}
