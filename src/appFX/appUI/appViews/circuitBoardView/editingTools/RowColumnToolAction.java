package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.appUI.prompts.RowTypeAddPrompt;
import appFX.appUI.utils.AppAlerts;
import appFX.framework.AppStatus;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.CircuitBoardModel.RowType;
import appFX.framework.gateModels.GateModel.GateComputingType;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.Pair;

public class RowColumnToolAction extends ToolAction {
	
	public static final boolean ADD = true;
	public static final boolean REMOVE = false;
	public static final boolean ROW = true;
	public static final boolean COLUMN = false;
	
	private static final Color COLOR_ADD_FILL = new Color(0, .7d, 0, .4d);
	private static final Color COLOR_REMOVE_FILL = new Color(.7d, 0, 0, .4d);
	private static final Color COLOR_ADD_BORDER = new Color(0, .5d, 0, .7d);
	private static final Color COLOR_REMOVE_BORDER = new Color(.5d, 0, 0, .7d);
	
	private final boolean addRemove;
	private final boolean rowColumn;
	private double mouseGrid = -1;
	
	public RowColumnToolAction(CircuitBoardView cbv, boolean addRemove, boolean rowColumn) {
		super(cbv);
		this.addRemove = addRemove;
		this.rowColumn = rowColumn;
	}
	
	@Override
	public void buttonPressed(double column, double offGridX, double row, double offGridY) {
		if(column < 0 || row < 0)
			return;
		
		int rowInt = (int) Math.floor(row);
		int columnInt = (int) Math.floor(column);
		
		try {
			CircuitBoardView cbv = getCircuitBoardView();
			CircuitBoardModel cbm = cbv.getCircuitBoardModel();
			
			if(rowColumn == ROW) {
				if(addRemove == ADD) {
					RowType[] validTypes;
					int rowTypeSelect;
					GateComputingType type = cbm.getComputingType();
					if(type == GateComputingType.CLASSICAL) {
						validTypes = new RowType[] {RowType.SPACE, RowType.CLASSICAL};
						rowTypeSelect = 1;
					} else if(type == GateComputingType.QUANTUM) {
						validTypes = new RowType[] {RowType.SPACE, RowType.CLASSICAL, RowType.QUANTUM};
						rowTypeSelect = 2;
					} else {
						validTypes = new RowType[] {RowType.SPACE, RowType.CLASSICAL_AND_QUANTUM};
						rowTypeSelect = 1;
					}
						
					RowTypeAddPrompt prompt = new RowTypeAddPrompt(rowTypeSelect, validTypes);
					Pair<RowType, Integer> element = prompt.openPromptAndGetElement();
					
					if(element != null)
						cbm.addRows(rowInt + (row - rowInt < .5d? 0 : 1) , element.second(), element.first());
				} else {
					cbm.removeRows(rowInt, rowInt + 1);
				}
			} else {
				if(addRemove == ADD)
					cbm.addColumns(columnInt + (column - columnInt < .5d? 0 : 1), 1);
				else
					cbm.removeColumns(columnInt, columnInt + 1);
			}
		} catch(IllegalArgumentException iae) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), 
					"Could not " + (addRemove == ADD? "add":"remove") + " " + (rowColumn == ROW? "Row":"Column"),
					iae.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void reset() {
		mouseGrid = -1;
	}

	@Override
	public void mouseMoved(double column, double offGridX, double row, double offGridY) {
		double oldMouseGrid = mouseGrid;
		if(rowColumn == ROW)
			mouseGrid = column < 0 ? -1d : row;
		else
			mouseGrid = row < 0 ? -1d : column;
		if(isMouseGridChanged(oldMouseGrid, mouseGrid))
			calculateAndRenderLayer();
	}

	@Override
	public void onToolStart(double column, double offGridX, double row, double offGridY) {
		mouseMoved(column, offGridX, row, offGridY);
		calculateAndRenderLayer();
	}

	@Override
	public void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData) {
		if(mouseGrid < 0d)
			return;
		
		graphics.setLineWidth(3);
		if(rowColumn == ROW) {
			int row = (int) Math.floor(mouseGrid);
			RenderLayer.setFocus(graphics, gridData, row, row + 1, 0, gridData.getColumnCount()); {
				if(addRemove == ADD) {
					graphics.setColor(COLOR_ADD_FILL);
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setVerticalyLayout(mouseGrid - row < .5d ? Graphics.TOP_ALIGN : Graphics.BOTTOM_ALIGN);
					graphics.setColor(COLOR_ADD_BORDER);
					graphics.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
				} else {
					graphics.setColor(COLOR_REMOVE_FILL);
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setColor(COLOR_REMOVE_BORDER);
					graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				}
			} graphics.escapeFocus();
		} else {
			int column = (int) Math.floor(mouseGrid);
			RenderLayer.setFocus(graphics, gridData, 0, gridData.getRowCount(), column, column + 1); {
				if(addRemove == ADD) {
					graphics.setColor(COLOR_ADD_FILL);
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setHorizontalLayout(mouseGrid - column < .5d ? Graphics.LEFT_ALIGN : Graphics.RIGHT_ALIGN);
					graphics.setColor(COLOR_ADD_BORDER);
					graphics.drawLine(0, 0, 0, Graphics.FOCUS_HEIGHT, true);
				} else {
					graphics.setColor(COLOR_REMOVE_FILL);
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setColor(COLOR_REMOVE_BORDER);
					graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				}
			} graphics.escapeFocus();
		}
	}
	

}
