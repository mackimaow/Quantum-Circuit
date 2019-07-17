package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SelectRenderLayer extends RenderLayer {

	private final CircuitBoardRenderer circuitBoardRenderer;
	
	public SelectRenderLayer(int width, int height, CircuitBoardRenderer circuitBoardRenderer) {
		super(width, height);
		this.circuitBoardRenderer = circuitBoardRenderer;
	}
	
	@Override
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		
	}

}
