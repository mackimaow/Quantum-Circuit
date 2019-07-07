package appFX.appUI.appViews.circuitBoardView.renderer;

import java.util.LinkedList;

import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import mathLib.Vector;

public class CircuitBoardRenderer {
	
	private static final double ZOOM_LOWER_BOUND = .1d;
	private static final double ZOOM_UPPER_BOUND = 2d;
	
	private static final double INITIAL_X = 0;
	private static final double INITIAL_Y = 0;
	
	private StackPane stackPane;
	private LinkedList<Integer> widths = new LinkedList<Integer>();
	private LinkedList<Integer> heights = new LinkedList<Integer>();
	private LinkedList<RenderLayer> layers;
	private int gateRenderLayerIndex = -1;
	private double zoom = 1;
	private Vector<Double> scrollVector = new Vector<>(INITIAL_X, INITIAL_Y, 1d);
	
	
	public CircuitBoardRenderer() {
		this.stackPane = new StackPane();
	}
	
	public Node getAsNode() {
		return stackPane;
	}
	
	public synchronized void render() {
		renderAll();
	}
	
	private synchronized void renderAll() {
		if(gateRenderLayerIndex == -1)
			return;
		
		GateRenderLayer gateLayer = (GateRenderLayer) layers.get(gateRenderLayerIndex);
		gateLayer.calculateGateBounds(widths, heights);
		
		for(RenderLayer layer : layers)
			layer.render();
	}
	
	public synchronized void addLayer(RenderLayer layer) {
		if(layer instanceof GateRenderLayer) {
			if(gateRenderLayerIndex != -1)
				throw new RuntimeException("This layer type \"" + GateRenderLayer.class.getName() + "\" can be only added once");
			gateRenderLayerIndex = layers.size();
		}
		layers.addLast(layer);
	}
	
	public synchronized void scroll(double x, double y) {
		
		renderAll();
	}
	
	public synchronized void zoom(double zoomFactor) {
		
		renderAll();
	}
	
	public double getZoom() {
		return zoom;
	}
	
	public double getScrollX() {
		return scrollVector.v(0);
	}
	
	public double getScrollY() {
		return scrollVector.v(1);
	}
}
