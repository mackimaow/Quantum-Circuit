package appFX.framework.exportGates;

public class Control {
	public static final boolean CONTROL_TRUE = true;
	public static final boolean CONTROL_FALSE = false;
	
	private final int register;
	private final boolean controlStatus;
	
	public Control (int register, boolean controlStatus) {
		this.register = register;
		this.controlStatus = controlStatus;
	}

	public int getRegister() {
		return register;
	}

	public boolean getControlStatus() {
		return controlStatus;
	}
	
	@Override
	public String toString() {
		if(controlStatus == CONTROL_TRUE) {
			return "T" + register;
		} else if (controlStatus == CONTROL_FALSE) {
			return "F" + register;
		} else {
			throw new RuntimeException("[Error] Control status is NULL");
		}
	}
	
}
