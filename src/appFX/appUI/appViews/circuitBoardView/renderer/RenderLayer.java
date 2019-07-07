package appFX.appUI.appViews.circuitBoardView.renderer;

import graphics.Graphics;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;

public abstract class RenderLayer {
	private Canvas layer;
	
	public synchronized void render() {
		renderLayer();
	}
	
	public synchronized void resize(int width, int height) {
		layer.resize(width, height);
		renderLayer();
	}
	
	private void renderLayer() {
		GraphicsContext gc = layer.getGraphicsContext2D();
//		GraphicsDraw gd = new GraphicsDraw(gc, 0, 0, (int) layer.getWidth(), (int) layer.getHeight());
//		renderLayer(gd);
	}
	
	protected abstract void renderLayer(Graphics gc);
	
}

