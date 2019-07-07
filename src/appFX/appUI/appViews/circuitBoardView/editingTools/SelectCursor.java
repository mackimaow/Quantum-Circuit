package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.AppToolManager;
import appFX.appUI.MainScene;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.copyPasteTools.ControlToolAction;
import appFX.framework.AppStatus;
import appFX.framework.exportGates.Control;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Toggle;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;

public class SelectCursor implements EventHandler<MouseEvent> {
	private static final ToolAction DO_NOTHING = new ToolAction(false) {
		
		public void reset() {}
		public void buttonPressed(int row, int column) {}
		public void mouseMoved(Region cursor, double x, double y, double boundX, double boundY) {}
		public boolean isCursorDisplayed() {
			return false;
		}
	};
	
	private ToolAction currentToolAction;
	private final ChangeListener<Toggle> toolChanged, gateModelChanged;
	private final CircuitBoardView cbv;
	private final Region cursorDisp;
	private int row, column;
	
	public SelectCursor(CircuitBoardView cbv) {
		this.cbv = cbv;
		this.cursorDisp = new Region();
		cursorDisp.setOnMouseMoved(this);
		MainScene ms = AppStatus.get().getMainScene();
		this.currentToolAction = getToolAction(ms.getToolManager().getSelectedTool());
		currentToolAction.initToolCursorRender(cursorDisp);
		setPosition(0, 0);
		
		cursorDisp.setOnMouseClicked(this);
		
		toolChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
			currentToolAction = getToolAction(newV);
			currentToolAction.initToolCursorRender(cursorDisp);
			setPosition(row, column);
		};
		
		gateModelChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
		};
	}
	
	private ToolAction getToolAction(Toggle toggle) {
		MainScene ms = AppStatus.get().getMainScene();
		AppToolManager toolManager = ms.getToolManager();
		
		if(toggle == toolManager.getToolButton(AppToolManager.SELECT_TOOL_BUTTON)) {
			return new SelectToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppToolManager.SOLDER_TOOL_BUTTON)) {
			return new SolderRegionToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppToolManager.EDIT_TOOL_BUTTON)) {
		} else if(toggle == toolManager.getToolButton(AppToolManager.CONTROL_TOOL_BUTTON)) {
			return new ControlToolAction(cbv, Control.CONTROL_TRUE);
		} else if(toggle == toolManager.getToolButton(AppToolManager.CONTROL_NOT_TOOL_BUTTON)) {
			return new ControlToolAction(cbv, Control.CONTROL_FALSE);
		} else if(toggle == toolManager.getToolButton(AppToolManager.ADD_COLUMN_TOOL_BUTTON)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.ADD, RowColumnToolAction.COLUMN);
		} else if(toggle == toolManager.getToolButton(AppToolManager.REMOVE_COLUMN_TOOL_BUTTON)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.REMOVE, RowColumnToolAction.COLUMN);
		} else if(toggle == toolManager.getToolButton(AppToolManager.ADD_ROW_TOOL_BUTTON)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.ADD, RowColumnToolAction.ROW);
		} else if(toggle == toolManager.getToolButton(AppToolManager.REMOVE_ROW_TOOL_BUTTON)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.REMOVE, RowColumnToolAction.ROW);
		}
		hideCursor();
		return DO_NOTHING;
	}

	public ChangeListener<Toggle> getToolChangedListener() {
		return toolChanged;
	}
	
	public ChangeListener<Toggle> getModelChangedListener() {
		return gateModelChanged;
	}
	
	public void showTool() {
		if(currentToolAction.isCursorDisplayed()) {
			cursorDisp.setManaged(true);
			cursorDisp.setVisible(true);
		}
	}
	
	public void addToNodeList(ObservableList<Node> nodes) {
		nodes.add(cursorDisp);
	}
	
	public void hideCursor() {
		cursorDisp.setManaged(false);
		cursorDisp.setVisible(false);
	}
	
	public int getRow() {
		return row;
	}
	
	public int getColumn () {
		return column;
	}
	
	public void setColumn(int column) {
		this.column = column;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}
	
	public void setRow(int row) {
		this.row = row;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}
	
	public void setPosition(int row, int column) {
		this.row = row;
		this.column = column;
		currentToolAction.updateCursorPosition(cursorDisp, row, column);
	}

	@Override
	public void handle(MouseEvent event) {
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			currentToolAction.buttonPressed(getRow(), getColumn());
		} else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			currentToolAction.mouseMoved(cursorDisp, event.getX(), event.getY(), cursorDisp.getWidth(), cursorDisp.getHeight());
		}
	}
	
	public ToolAction getCurrentTool() {
		return currentToolAction;
	}
}
