package appFX.appUI.appViews.circuitBoardView.copyPasteTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.editingTools.SelectGateRegion;
import appFX.appUI.appViews.circuitBoardView.editingTools.ToolAction;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class ControlToolAction extends ToolAction {
	private final boolean controlType;
	private final CircuitBoardView cbv;
	private int rowSel, colSel;
	private SelectGateRegion region = null;
	private SolderedGate currSG;
	
	public ControlToolAction(CircuitBoardView cbv, boolean controlType) {
		super(true);
		this.cbv = cbv;
		this.controlType = controlType;
	}
	
	@Override
	public void buttonPressed(int row, int column) {
		SolderedPin sp = cbv.getCircuitBoardModel().getSolderPinAt(row, column);
		SolderedGate sg = sp.getSolderedGate();
		if(currSG == null) {
			if(PresetGateType.isIdentity(sg.getGateModelFormalName()))
				return;
			
			currSG = sg;
			rowSel = row;
			colSel = column;
			
			region = new SelectGateRegion (cbv, row, column);
			region.setStyle("-fx-border-color: blue;\n"
	                + "-fx-border-width: 2;\n"
	                + "-fx-border-style: dashed;\n");
			
			ObservableList<Node> nodes = cbv.getCircuitBoardUIPane().getChildren();
			nodes.add(nodes.size() - 1, region);
		} else {
			if(colSel != column) {
				reset();
				return;
			}
			if(sg == currSG && sp instanceof SolderedRegister)
				return;
			
			cbv.getCircuitBoardModel().placeControl(row, rowSel, column, controlType);
			
			reset();
		}
	}
	
	@Override
	public void reset() {
		rowSel = -1;
		colSel = -1;
		if(region != null)
			cbv.getCircuitBoardUIPane().getChildren().remove(region);
		region = null;
		currSG = null;
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}

	@Override
	public void mouseMoved(Region cursor, double x, double y, double boundX, double boundY) {}
}
