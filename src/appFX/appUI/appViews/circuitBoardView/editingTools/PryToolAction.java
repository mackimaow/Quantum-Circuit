package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.appUI.prompts.Prompt;
import appFX.appUI.prompts.PryRemovePrompt;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.solderedGates.SolderedControlPin;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import appFX.framework.solderedGates.SpacePin;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class PryToolAction extends ToolAction {

	private double mouseGridX = -1d;
	private double mouseGridY = -1d;
	
	public PryToolAction(CircuitBoardView circuitBoardView) {
		super(circuitBoardView);
		
	}

	@Override
	public void onToolStart(double column, double offGridX, double row, double offGridY) {
		mouseGridX = column;
		mouseGridY = row;
		calculateAndRenderLayer();
	}

	@Override
	public void buttonPressed(double column, double offGridX, double row, double offGridY) {
		if(column < 0d || row < 0d)
			return;
		
		int selectedColumn = (int) Math.floor(column);
		int selectedRow = (int) Math.floor(row);
		
		CircuitBoardModel model = getCircuitBoardView().getCircuitBoardModel();
		SolderedPin sp = model.getSolderedPinAt(selectedRow, selectedColumn);
		
		if(sp.getSolderedGate().isIdentity())
			return;
		
		if(sp instanceof SolderedRegister) {
			model.removeGate(selectedRow, selectedColumn);
		} else {
			SpacePin spacePin = (SpacePin) sp; 
			boolean hasInputLink = spacePin.isInputLinked();
			boolean hasControl = spacePin instanceof SolderedControlPin;
			boolean hasOutputLink = spacePin.isOutputLinked();
			
			Prompt<Boolean[]> pryRemovePrompt = new PryRemovePrompt(hasInputLink, hasControl, hasOutputLink);
			
			Boolean[] userOutput = pryRemovePrompt.openPromptAndGetElement();
			
			if(userOutput == null)
				return;
			
			if(userOutput[0])
				model.removeGate(selectedRow, selectedColumn);
			else
				model.removeLinksAndControls(selectedRow, selectedColumn, userOutput[1], userOutput[2], userOutput[3]);
		}
		
		reset();
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
	public void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData) {
		graphics.setLineWidth(1);
		graphics.setFont(CustomFXGraphics.DEFAULT, 10);
		
		if(mouseGridX < 0d || mouseGridY < 0d)
			return;
		
		int column = (int) Math.floor(mouseGridX);
		int row = (int) Math.floor(mouseGridY);
		
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		RenderLayer.setFocus(graphics, gridData, row, column); {
			graphics.setColor(Color.RED);
			graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
			graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
			graphics.drawText("-", 0, -7);
		} graphics.escapeFocus();
	}

	@Override
	public void reset() {
		calculateAndRenderLayer();
	}

}
