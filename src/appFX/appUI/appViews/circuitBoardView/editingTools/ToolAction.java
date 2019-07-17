package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.ToolActionRenderLayer;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public abstract class ToolAction {
	private CircuitBoardView circuitBoardView;
	
	public ToolAction (CircuitBoardView circuitBoardView) {
		this.circuitBoardView = circuitBoardView;
	}
	
	public abstract void onToolStart(double column, double offGridX, double row, double offGridY);
	public abstract void buttonPressed(double column, double offGridX, double row, double offGridY);
	public abstract void mouseMoved(double column, double offGridX, double row, double offGridY);
	public abstract void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData);
	public abstract void reset();
	
	public ToolActionRenderLayer getToolActionRenderLayer() {
		return (ToolActionRenderLayer) circuitBoardView.getRenderer().getLayer(CircuitBoardRenderer.TOOL_RENDER_LAYER_INDEX);
	}
	
	public void calculateAndRenderLayer() {
		circuitBoardView.renderToolActionLayerWhenSelected();
	}
	
	public CircuitBoardView getCircuitBoardView() {
		return circuitBoardView;
	}
	
	public static boolean isMouseGridChanged(double oldMouseGrid, double newMouseGrid) {
		int preMouseInt = (int) Math.floor(oldMouseGrid);
		int newMouseInt = (int) Math.floor(newMouseGrid);
		
		if(preMouseInt == newMouseInt) {
			int preMouseRound = (int) Math.round(oldMouseGrid);
			int newMouseRound = (int) Math.round(newMouseGrid);
			if(preMouseRound == newMouseRound)
				return false;
		}
		
		return true;
	}
}