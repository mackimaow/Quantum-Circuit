package appFX.appUI.appViews.circuitBoardView.copyPasteTools;

import appFX.framework.gateModels.CircuitBoardModel;

public class GateCopyBuffer {
	private GateSelectionBuffer buffer;
	
	public GateCopyBuffer(CircuitBoardModel circuitBoard, GateSelectionBuffer buffer) {
		this.buffer = buffer;
	}
	
	
	public void applyPaste(CircuitBoardModel circuitBoardModel, int row, int column) {
		
	}
}
