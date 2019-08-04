package appFX.framework.solderedGates;

public class SolderedControlPin extends SpacePin {
	private static final long serialVersionUID = 292673113796147747L;
	
	private boolean controlStatus;
	
	public SolderedControlPin(SolderedGate gate, boolean isWithinBody, boolean controlStatus, int inputReg, OutputLinkType outputLinkType, int outputReg) {
		super(gate, isWithinBody, inputReg, outputLinkType, outputReg);
		this.controlStatus = controlStatus;
	}
	
	public SolderedControlPin(SolderedGate gate, boolean isWithinBody, boolean controlStatus) {
		super(gate, isWithinBody);
		this.controlStatus = controlStatus;
	}
	
	@Override
	public boolean isNotEmptySpace() {
		return true;
	}
	
	public boolean getControlStatus() {
		return controlStatus;
	}
}
