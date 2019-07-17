package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.MainScene;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.utils.AppToolManager;
import appFX.framework.AppStatus;
import appFX.framework.exportGates.Control;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.scene.control.Toggle;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class ToolActionManager implements EventHandler<MouseEvent> {
	

	private final ToolAction doNothing;
	private ToolAction currentToolAction;
	private final ChangeListener<Toggle> toolChanged, gateModelChanged;
	private final CircuitBoardView cbv;
	private double[] mouseBounds;
	private boolean isStarted = false;
	
	public ToolActionManager(CircuitBoardView cbv) {
		this.cbv = cbv;
		
		doNothing = new ToolAction(cbv) {
			public void onToolStart(double column, double offGridX, double row, double offGridY) {
				calculateAndRenderLayer();
			}
			public void buttonPressed(double column, double offGridX, double row, double offGridY) {}
			public void mouseMoved(double column, double offGridX, double row, double offGridY) {}
			public void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData) {}
			public void reset() {}
		};
		
		toolChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
			currentToolAction = getToolAction(newV);
			currentToolAction.onToolStart(mouseBounds[0], mouseBounds[1], mouseBounds[2], mouseBounds[3]);
		};
		
		gateModelChanged = (o, oldV, newV) -> {
			currentToolAction.reset();
		};
	}
	
	public void startManager() {
		MainScene ms = AppStatus.get().getMainScene();
		this.mouseBounds = new double[] {-1d, 0d, -1d, 0d};
		this.currentToolAction = getToolAction(ms.getToolManager().getSelectedTool());
		currentToolAction.onToolStart(mouseBounds[0], mouseBounds[1], mouseBounds[2], mouseBounds[3]);
		this.isStarted = true;
	}
	
	public boolean isStarted() {
		return isStarted;
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
		return doNothing;
	}

	public ChangeListener<Toggle> getToolChangedListener() {
		return toolChanged;
	}
	
	public ChangeListener<Toggle> getModelChangedListener() {
		return gateModelChanged;
	}

	@Override
	public void handle(MouseEvent event) {
		CircuitBoardRenderer renderer = cbv.getRenderer();
		mouseBounds = renderer.getMouseGridBounds(event.getX(), event.getY());
		if (event.getEventType() == MouseEvent.MOUSE_CLICKED) {
			currentToolAction.buttonPressed(mouseBounds[0], mouseBounds[1], mouseBounds[2], mouseBounds[3]);
		} else if (event.getEventType() == MouseEvent.MOUSE_MOVED) {
			currentToolAction.mouseMoved(mouseBounds[0], mouseBounds[1], mouseBounds[2], mouseBounds[3]);
		}
	}
	
	public ToolAction getCurrentTool() {
		return currentToolAction;
	}
}
