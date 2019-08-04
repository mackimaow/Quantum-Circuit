package appFX.appUI.appViews.circuitBoardView.editingTools;

import java.util.Hashtable;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.appUI.appViews.gateChooser.AbstractGateChooserView;
import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.ParameterPrompt;
import appFX.framework.AppStatus;
import appFX.framework.gateModels.GateModel;
import appFX.framework.utils.InputDefinitions.DefinitionEvaluatorException;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableArray;

public class SolderRegionToolAction  extends ToolAction {
	private Hashtable<Integer, Integer> globalToLocalRegs = new Hashtable<>();
	private int[] regs = null;
	private int currentReg = -1;
	private int nextReg = -1;
	private int selectedColumn = -1;
	private double mouseGridX = -1;
	private double mouseGridY = -1;
	
	
	public SolderRegionToolAction(CircuitBoardView cbv) {
		super(cbv);
	}

	@Override
	public void buttonPressed(double column, double offGridX, double row, double offGridY) {
		if(column < 0 || row < 0)
			return;
		
		if(selectedColumn != (int) Math.floor(column))
			reset();
		
		selectedColumn = (int) Math.floor(column);
		int selectedRow = (int) Math.floor(row);
		
		CircuitBoardView cbv = getCircuitBoardView();
		
		GateModel gm = getSelectedModel();
		
		if(gm != null) { 
			
			
			if(regs == null) {
				regs = new int[gm.getNumberOfRegisters()];
				nextReg = 1;
				currentReg = 0;
			}
			
			Integer alreadyUsedLocalReg = globalToLocalRegs.get(selectedRow);
			regs[currentReg] = selectedRow;
			globalToLocalRegs.put(selectedRow, currentReg);
			
			if(alreadyUsedLocalReg == null) {
				currentReg = nextReg++;
			} else {
				currentReg = alreadyUsedLocalReg;
			}
			
			if(nextReg - 1 == regs.length) {
				ImmutableArray<String> args = gm.getParameters();
				if(args.size() > 0) {
					ParameterPrompt pp = new ParameterPrompt(cbv.getProject(), cbv.getCircuitBoardModel(), gm.getLocationString(), regs, selectedColumn);
					pp.showAndWait();
				} else {
					try {
						cbv.getCircuitBoardModel().placeGate(gm.getLocationString(), selectedColumn, regs);
					} catch (DefinitionEvaluatorException e) {
						AppAlerts.showMessage(AppStatus.get().getPrimaryStage(),
								"Parameter(s) is invalid", e.getMessage(), AlertType.ERROR);
					} catch(IllegalArgumentException e2) {
						AppAlerts.showMessage(AppStatus.get().getPrimaryStage(),
								"Illegal placement of gate", e2.getMessage(), AlertType.ERROR);
					}
				}
				reset();
			}
			calculateAndRenderLayer();
		}
	}


	@Override
	public void reset() {
		regs = null;
		currentReg = -1;
		selectedColumn = -1;
		nextReg = -1;
		
		globalToLocalRegs.clear();
		calculateAndRenderLayer();
	}
	
	public GateModel getSelectedModel() {
		return AbstractGateChooserView.getSelected();
	}
	

	@Override
	public void mouseMoved(double column, double offGridX, double row, double offGridY) {
		double oldMouseGridX = mouseGridX;
		double oldMouseGridY = mouseGridY;
		mouseGridX = column;
		mouseGridY = row;
		if(isMouseGridChanged(oldMouseGridX, mouseGridX) || isMouseGridChanged(oldMouseGridY, mouseGridY))
			calculateAndRenderLayer();
	}

	@Override
	public void onToolStart(double column, double offGridX, double row, double offGridY) {
		mouseGridX = column;
		mouseGridY = row;
		calculateAndRenderLayer();
	}

	@Override
	public void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData) {
		graphics.setFont(CustomFXGraphics.DEFAULT, 10);
		
		graphics.setLineWidth(1);
		
		if(regs != null) {
			Color lightGrey = new Color(.7d, .7d, .7d, .4d);
			graphics.setColor(lightGrey);
			if(selectedColumn != 0) {
				RenderLayer.setFocus(graphics, gridData, 0, gridData.getRowCount(), 0, selectedColumn); {
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				} graphics.escapeFocus();
			}
			if(selectedColumn != gridData.getColumnCount() - 1) {
				RenderLayer.setFocus(graphics, gridData, 0, gridData.getRowCount(), selectedColumn + 1, gridData.getColumnCount()); {
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				} graphics.escapeFocus();
			}
			Color lightGreen = new Color(0, .7d, 0, .4d);
			for(int localReg = 0; localReg < nextReg; localReg++) {
				if(localReg == currentReg) continue;
				int globalReg = regs[localReg];
				RenderLayer.setFocus(graphics, gridData, globalReg, selectedColumn); {
					graphics.setColor(lightGreen);
					graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
					graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
					graphics.setColor(Color.DARKGREEN);
					graphics.drawText(Integer.toString(localReg), 0, 0);
				} graphics.escapeFocus();
			}
			if(mouseGridX >= 0 && mouseGridY >= 0) {
				int col = (int) Math.floor(mouseGridX);
				int row = (int) Math.floor(mouseGridY);
				if(col == selectedColumn) {
					RenderLayer.setFocus(graphics, gridData, row, col); {
						graphics.setColor(Color.DARKGRAY);
						graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
						graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
						graphics.setColor(Color.BLACK);
						graphics.drawText(Integer.toString(currentReg), 0, 0);
					} graphics.escapeFocus();
				}
			}
		} else {
			if(mouseGridX >= 0 && mouseGridY >= 0) {
				int col = (int) Math.floor(mouseGridX);
				int row = (int) Math.floor(mouseGridY);
				
				RenderLayer.setFocus(graphics, gridData, row, col); {
					GateModel gm = getSelectedModel();
					if(gm == null) {
						graphics.setColor(Color.GRAY);
						graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
						graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
						graphics.drawText("?", 0, -7);
					} else {
						graphics.setColor(Color.GREEN);
						graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
						graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
						graphics.drawText("+", 0, -7);
					}
				} graphics.escapeFocus();
			}
		}
	}

}