package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class GateAttributeTool extends ToolAction {
	
	private int rowSelStart, rowSelEnd, colSel;
	private SolderedGate currSG;
	private double mouseGridX = -1d;
	private double mouseGridY = -1d;
	
	
	public GateAttributeTool(CircuitBoardView cbv) {
		super(cbv);
	}
	
	public abstract void placeAttribute(CircuitBoardModel model, int rowGate, int rowAttribute, int column);
	public abstract void drawCursor(Graphics<Image, Font, Color> graphics, boolean isGateSelected);
	
	@Override
	public void buttonPressed(double column, double offGridX, double row, double offGridY) {
		if(column < 0d || row < 0d)
			return;
		
		CircuitBoardView cbv = getCircuitBoardView();
		int rowInt = (int) Math.floor(row);
		int columnInt = (int) Math.floor(column);
		SolderedPin sp = cbv.getCircuitBoardModel().getSolderedPinAt(rowInt, columnInt);
		SolderedGate sg = sp.getSolderedGate();
		
		if(currSG == null) {
			if(PresetGateType.isIdentity(sg.getGateModelLocationString()))
				return;
			
			int[] rowBounds = cbv.getCircuitBoardModel().getGateBodyBoundsFromSpace(rowInt, columnInt);
			rowSelStart = rowBounds[0];
			rowSelEnd = rowBounds[1] + 1;
			colSel = columnInt;
			
			currSG = sg;
		} else {
			if(colSel != columnInt) {
				reset();
				return;
			}
			if(sg == currSG && sp instanceof SolderedRegister)
				return;
			
			placeAttribute(cbv.getCircuitBoardModel(), rowSelStart, rowInt, columnInt);
			
			reset();
		}
		calculateAndRenderLayer();
	}
	
	@Override
	public void reset() {
		currSG = null;
		calculateAndRenderLayer();
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
		
		graphics.setLineWidth(1);
		
		if(currSG != null) {
			RenderLayer.setFocus(graphics, gridData, rowSelStart, rowSelEnd, colSel, colSel + 1); {
				graphics.setColor(Color.BLUE);
				graphics.customGraphicFunction((drawTool)-> {
					CustomFXGraphics gfx = (CustomFXGraphics) drawTool;
					gfx.setLineDashes(graphics.resize(5), graphics.resize(3));
				});
				graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.customGraphicFunction((drawTool)-> {
					CustomFXGraphics gfx = (CustomFXGraphics) drawTool;
					gfx.setLineDashes(0, 0);
				});
			} graphics.escapeFocus();
		}
		if(mouseGridX < 0d || mouseGridY < 0d)
			return;
		
		int column = (int) Math.floor(mouseGridX);
		int row = (int) Math.floor(mouseGridY);
		
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		RenderLayer.setFocus(graphics, gridData, row, column); {
			drawCursor(graphics, currSG != null && column == colSel);
		} graphics.escapeFocus();
	}
}