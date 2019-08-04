package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.MainScene;
import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.utils.AppToolManager;
import appFX.appUI.utils.AppToolManager.AppTool;
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
		
		if(toggle == toolManager.getToolButton(AppTool.SELECT_TOOL)) {
			return new SelectToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppTool.SOLDER_TOOL)) {
			return new SolderRegionToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppTool.PRY_TOOL)) {
			return new PryToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppTool.EDIT_TOOL)) {
		} else if(toggle == toolManager.getToolButton(AppTool.LINK_TOOL)) {
			return new LinkToolAction(cbv);
		} else if(toggle == toolManager.getToolButton(AppTool.CONTROL_TOOL)) {
			return new ControlToolAction(cbv, Control.CONTROL_TRUE);
		} else if(toggle == toolManager.getToolButton(AppTool.CONTROL_NOT_TOOL)) {
			return new ControlToolAction(cbv, Control.CONTROL_FALSE);
		} else if(toggle == toolManager.getToolButton(AppTool.ADD_COLUMN_TOOL)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.ADD, RowColumnToolAction.COLUMN);
		} else if(toggle == toolManager.getToolButton(AppTool.REMOVE_COLUMN_TOOL)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.REMOVE, RowColumnToolAction.COLUMN);
		} else if(toggle == toolManager.getToolButton(AppTool.ADD_ROW_TOOL)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.ADD, RowColumnToolAction.ROW);
		} else if(toggle == toolManager.getToolButton(AppTool.REMOVE_ROW_TOOL)) {
			return new RowColumnToolAction(cbv, RowColumnToolAction.REMOVE, RowColumnToolAction.ROW);
		}
		return doNothing;
	}

	public void resetCurrentTool() {
		currentToolAction.reset();
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
