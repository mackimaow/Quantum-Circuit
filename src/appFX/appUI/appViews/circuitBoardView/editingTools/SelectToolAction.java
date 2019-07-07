package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.framework.AppStatus;
import appFX.framework.Project;
import appFX.framework.gateModels.GateModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedPin;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.layout.Region;

public class SelectToolAction extends ToolAction {
	
	private final CircuitBoardView cbv;
	private SelectGateRegion selectRegion = null;
	
	public SelectToolAction(CircuitBoardView cbv) {
		super(true);
		this.cbv = cbv;
	}
	
	@Override
	public void initToolCursorRender(Region cursor) {
		cursor.setStyle("-fx-background-color: #00000000;");
	}
	
	@Override
	public void updateCursorPosition(Region cursor, int row, int column) {
		synchronized (this) {
			ObservableList<Node> children = cbv.getCircuitBoardUIPane().getChildren();
			if (selectRegion != null)
				children.remove(selectRegion);
			SolderedPin sp = cbv.getCircuitBoardModel().getSolderPinAt(row, column);
			sp.getSolderedGate();
			
			AppStatus status = AppStatus.get();
			Project project = status.getFocusedProject();
			GateModel gm = project.getGateModel(sp.getSolderedGate().getGateModelFormalName());
			if(gm != PresetGateType.IDENTITY.getModel()) {
				selectRegion = new SelectGateRegion (cbv, row, column);
				selectRegion.setStyle("-fx-background-color: #88888844;");
				children.add(selectRegion);
			}
		}
	}
	
	@Override
	public void buttonPressed(int row, int column) {
		
	}

	@Override
	public void mouseMoved(Region cursor, double x, double y, double boundX, double boundY) {
		
	}

	@Override
	public void reset() {
		synchronized (this) {
			if (selectRegion != null) {
				ObservableList<Node> children = cbv.getCircuitBoardUIPane().getChildren();
				children.remove(selectRegion);
			}
		}
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}

}
