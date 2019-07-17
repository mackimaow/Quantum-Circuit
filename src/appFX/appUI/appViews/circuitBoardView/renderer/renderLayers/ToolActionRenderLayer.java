package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.editingTools.ToolActionManager;
import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class ToolActionRenderLayer extends RenderLayer {
	
	private final CircuitBoardView cbv;
	private FocusData gridData;
	
	public ToolActionRenderLayer(double width, double height, CircuitBoardView cbr) {
		super(width, height);
		this.cbv = cbr;
	}
	
	public synchronized void render() {
		CircuitBoardRenderer renderer = cbv.getRenderer();
		
		double[] centeredPosition = renderer.getCenteredPosition();
		
		double viewLeftOriginX = renderer.getViewWidth() / (2d * renderer.getZoom()) - centeredPosition[0];
		double viewTopOriginY = renderer.getViewHeight() / (2d * renderer.getZoom()) - centeredPosition[1];
		
		render(viewLeftOriginX, viewTopOriginY, renderer.getZoom());
	}
	
	public synchronized void calculateBounds() {
		CircuitBoardRenderer renderer = cbv.getRenderer();
		calculateBounds(renderer.getGridData());
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		ImmutableTree<FocusData> focusDataTree = (ImmutableTree<FocusData>) userArgs[0];
		
		gridData = focusDataTree.getElement();
		
		graphics.setBoundsManaged(false);
		ToolActionManager toolActionManager = cbv.getToolActionManager();
		if(toolActionManager.isStarted())
			toolActionManager.getCurrentTool().renderOnLayer(graphics, gridData);
	}
	
}
