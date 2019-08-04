package appFX.framework.solderedGates;

public class SpacePin extends SolderedPin {
	private static final long serialVersionUID = -7897895869586517336L;
	
	public static enum OutputLinkType {
		CLASSICAL_LINK;
	}
	
	
	private final int linkInputReg;
	private final OutputLinkType outputPinLinkType;
	private final int outputReg;
	
	public SpacePin(SolderedGate gate, boolean isWithinBody, int linkStartReg, OutputLinkType outputLinkType, int outputReg) {
		super(gate, isWithinBody);
		this.outputPinLinkType = outputLinkType;
		this.outputReg = outputReg;
		this.linkInputReg = linkStartReg;
	}
	
	public SpacePin(SolderedGate gate, boolean isWithinBody) {
		this(gate, isWithinBody, -1, OutputLinkType.CLASSICAL_LINK, -1);
	}
	
	
	@Override
	public boolean isNotEmptySpace() {
		return isInputLinked() || isOutputLinked();
	}
	
	public boolean isOutputLinked() {
		return outputReg >= 0;
	}
	
	public boolean isInputLinked() {
		return linkInputReg >= 0;
	}
	
	public int getInputReg() {
		return linkInputReg;
	}
	
	public OutputLinkType getOutputLinkType() {
		return outputPinLinkType;
	}
	
	public int getOutputReg() {
		return outputReg;
	}
}
