package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.appUI.appViews.circuitBoardView.renderer.GateRenderer;
import appFX.framework.gateModels.CircuitBoardModel;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public final class GateRenderLayer extends RenderLayer {	
	private CircuitBoardModel circuitBoard;
	
	public GateRenderLayer(double width, double height, CircuitBoardModel circuitBoard) {
		super(width, height);
		this.circuitBoard = circuitBoard;
	}
	
	@Override
	public void onDraw(Graphics<Image, Font, Color> graphics, Object ... userArgs) {
		GateRenderer.drawCircuitBoard(graphics, CustomFXGraphics.RENDER_PALETTE, circuitBoard);
	}
}
