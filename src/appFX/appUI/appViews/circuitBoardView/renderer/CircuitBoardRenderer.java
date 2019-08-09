package appFX.appUI.appViews.circuitBoardView.renderer;

import java.util.LinkedList;
import java.util.List;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.GateRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.GridRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.NotificationRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.QubitLineRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.QubitRegistersRenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.ToolActionRenderLayer;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.CircuitBoardModel.RowTypeList;
import graphicsWrapper.FocusData;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import utils.customCollections.CollectionUtils;
import utils.customCollections.immutableLists.ImmutableTree;

public class CircuitBoardRenderer {
	
	private static final double ZOOM_LOWER_BOUND = .1d;
	private static final double ZOOM_UPPER_BOUND = 4d;
	
	
	private StackPane stackPane;
	private LinkedList<RenderLayer> layers;
	public static final int GATE_RENDER_LAYER_INDEX = 1;
	public static final int GRID_RENDER_LAYER_INDEX = 2;
	public static final int TOOL_RENDER_LAYER_INDEX = 3;
	public static final int NOTIFICATION_RENDER_LAYER_INDEX = 4;
	
	private ImmutableTree<FocusData> gridData = null;
	private RowTypeList rowTypes = null;
	
	private double zoom = 1;
	private double horizontalGridFocused;
	private double verticalGridFocused;
	private double offGridX = 0d;
	private double offGridY = 0d;
	
	private double viewWidth = 600;
	private double viewHeight = 500;
	
	private double viewLeftOriginX = 0d;
	private double viewTopOriginY = 0d;
	
	private CircuitBoardModel circuitBoardModel;
	
	public CircuitBoardRenderer(CircuitBoardView circuitBoardView) {
		this.stackPane = new StackPane();
		this.layers = new LinkedList<>();
		circuitBoardModel = circuitBoardView.getCircuitBoardModel();
		horizontalGridFocused = (double) circuitBoardModel.getColumns() / 2d;
		verticalGridFocused = (double) circuitBoardModel.getRows() / 2d;
		addLayer(new QubitLineRenderLayer(viewWidth, viewHeight));
		addLayer(new GateRenderLayer(viewWidth, viewHeight, circuitBoardModel));
		addLayer(new GridRenderLayer(viewWidth, viewHeight));
		addLayer(new ToolActionRenderLayer(viewWidth, viewHeight, circuitBoardView));
		addLayer(new NotificationRenderLayer(viewWidth, viewHeight));
		addLayer(new QubitRegistersRenderLayer(viewWidth, viewHeight));
	}
	
	public synchronized void resizeWidth(double viewWidth) {
		this.viewWidth = viewWidth;
		for(RenderLayer layer : layers)
			layer.resize(viewWidth, viewHeight);
		render();
	}
	
	public synchronized void resizeHeight(double viewHeight) {
		this.viewHeight = viewHeight;
		for(RenderLayer layer : layers)
			layer.resize(viewWidth, viewHeight);
		render();
	}
	
	public Node getAsNode() {
		return stackPane;
	}
	
	public RenderLayer getLayer(int index) {
		return layers.get(index);
	}
	
	public synchronized void calculateBounds() {
		GateRenderLayer gateLayer = (GateRenderLayer) layers.get(GATE_RENDER_LAYER_INDEX);
		rowTypes = circuitBoardModel.getCopyOfRowTypeList();
		gridData = gateLayer.calculateBounds(null, rowTypes);
		
		for(RenderLayer layer : layers) {
			if(layer == gateLayer) continue;
			layer.calculateBounds(gridData, rowTypes);
		}
	}
	
	public synchronized void render() {
		double[] centeredPosition = getCenteredPosition();
		
		viewLeftOriginX = viewWidth / (2d * zoom) - centeredPosition[0];
		viewTopOriginY = viewHeight / (2d * zoom) - centeredPosition[1];
		
		for(RenderLayer layer : layers)
			layer.render(viewLeftOriginX, viewTopOriginY, zoom);
	}
	
	public synchronized void renderLayer(int index) {
		double[] centeredPosition = getCenteredPosition();
		
		viewLeftOriginX = viewWidth / (2d * zoom) - centeredPosition[0];
		viewTopOriginY = viewHeight / (2d * zoom) - centeredPosition[1];
		layers.get(index).render(viewLeftOriginX, viewTopOriginY, index);
	}
	
	public void setGridVisible(boolean visible) {
		layers.get(GRID_RENDER_LAYER_INDEX).setVisible(visible);
	}
	
	private void addLayer(RenderLayer layer) {
		layers.addLast(layer);
		layer.addToCircuitBoardRender(this);
	}
	
	public synchronized void scroll(double x, double y) {
		
		double[] centeredPosition = getCenteredPosition();
		
		x = centeredPosition[0] - x / zoom;
		y = centeredPosition[1] - y / zoom;
		
		double[] locationX = getXGridLocation(x);
		horizontalGridFocused = locationX[0];
		offGridX = locationX[1];

		double[] locationY = getYGridLocation(y);
		verticalGridFocused = locationY[0];
		offGridY = locationY[1];
		
		render();
	}
	
	public double[] getCenteredPosition() {
		double centerX = getXCoord(horizontalGridFocused, offGridX);
		double centerY = getYCoord(verticalGridFocused, offGridY);
		
		return new double[]{centerX, centerY};
	}
	
	public double getXCoord(double gridLocation, double offGridX) {
		FocusData fd = gridData.getElement();
		double columnWidth = fd.getWidth();
		if(gridLocation >= fd.getColumnCount())
			return columnWidth - .5d;
		if(gridLocation < 0)
			if(offGridX < 0)
				return offGridX;
			else
				return offGridX + columnWidth;
		int gridFocusedX = (int) Math.floor(gridLocation);
		double gridFractionalWidth = gridLocation - gridFocusedX;
		return fd.getCummulativeWidth(gridFocusedX)  - (1 - gridFractionalWidth)  * fd.getColumnWidthAt(gridFocusedX);
	}
	
	public double getYCoord(double gridLocation, double offGridY) {
		FocusData fd = gridData.getElement();
		double rowHeight = fd.getHeight();
		if(gridLocation >= fd.getRowCount())
			return rowHeight - .5d;
		if(gridLocation < 0)
			if(offGridY < 0)
				return offGridY;
			else
				return offGridY + rowHeight;
		int gridFocusedY = (int) Math.floor(gridLocation);
		double gridFractionalHeight = gridLocation - gridFocusedY;
		return fd.getCummulativeHeight(gridFocusedY)  - (1 - gridFractionalHeight)  * fd.getRowHeightAt(gridFocusedY);
	}
	
	
	public double[] getXGridLocation(double xCord) {
		if(xCord < 0)
			return new double[] {-1d, xCord};
		FocusData fd = gridData.getElement();
		double totalWidth = fd.getWidth();
		if(xCord >= totalWidth)
			return new double[] {-1d, xCord - totalWidth};
		List<Double> heightData = fd.getCummulativeWidthData();
		int newIndex = CollectionUtils.binarySearch(heightData, xCord);
		double previousCummulativeWidth = newIndex == 0? 0d : fd.getCummulativeWidth(newIndex - 1); 
		double newOffset = (xCord - previousCummulativeWidth) / fd.getColumnWidthAt(newIndex);
		double grid = newIndex + newOffset;
		return new double[] {grid < fd.getColumnCount()? grid : -1d, 0};
	}
	
	public double[] getYGridLocation(double yCoord) {
		if(yCoord < 0)
			return new double[] {-1d, yCoord};
		FocusData fd = gridData.getElement();
		double totalHeight = fd.getHeight();
		if(yCoord >= totalHeight)
			return new double[] {-1d, yCoord - totalHeight};
		List<Double> widthData = fd.getCummulativeHeightData();
		int newIndex = CollectionUtils.binarySearch(widthData, yCoord);
		double previousCummulativeHeight = newIndex == 0? 0d : fd.getCummulativeHeight(newIndex - 1); 
		double newOffset = (yCoord - previousCummulativeHeight) / fd.getRowHeightAt(newIndex);
		double grid = newIndex + newOffset;
		return new double[] {grid < fd.getRowCount()? grid : -1d, 0};
	}
	
	
	public synchronized void setCentered() {
		horizontalGridFocused = -1;
		verticalGridFocused = -1;
	}
	
	public synchronized void scrollToGrid(double row, double column) {
		horizontalGridFocused = column;
		verticalGridFocused = row;
		render();
	}
	
	public double[] getMouseGridBounds(double mouseX, double mouseY) {
		double[] centerPosition = getCenteredPosition();
		centerPosition[0] = (mouseX - viewWidth / 2d) / zoom + centerPosition[0];
		centerPosition[1] = (mouseY - viewHeight / 2d) / zoom + centerPosition[1];
		
		double[] xGridLocation = getXGridLocation(centerPosition[0]);
		double[] yGridLocation = getYGridLocation(centerPosition[1]);
		
		return new double[] {xGridLocation[0], xGridLocation[1], yGridLocation[0], yGridLocation[1]};
	}
	
	public synchronized void zoom(double zoom) {
		if(zoom < ZOOM_LOWER_BOUND)
			zoom = ZOOM_LOWER_BOUND;
		else if(zoom > ZOOM_UPPER_BOUND)
			zoom = ZOOM_UPPER_BOUND;
		this.zoom = zoom;
		render();
	}
	
	public synchronized void zoom(double zoom, double mouseX, double mouseY) {
		if(zoom < ZOOM_LOWER_BOUND)
			zoom = ZOOM_LOWER_BOUND;
		else if(zoom > ZOOM_UPPER_BOUND)
			zoom = ZOOM_UPPER_BOUND;
		
		double[] centerPosition = getCenteredPosition();
		double localX = centerPosition[0] + (mouseX - viewWidth / 2d) / this.zoom;
		double localY = centerPosition[1] + (mouseY - viewHeight / 2d) / this.zoom;
		
		double newCenterPositionX = localX - (mouseX - viewWidth / 2d) / zoom;
		double newCenterPositionY = localY - (mouseY - viewHeight / 2d) / zoom;
		
		double[] locationX = getXGridLocation(newCenterPositionX);
		horizontalGridFocused = locationX[0];
		offGridX = locationX[1];
		

		double[] locationY = getYGridLocation(newCenterPositionY);
		verticalGridFocused = locationY[0];
		offGridY = locationY[1];
		
		this.zoom = zoom;
		
		render();
	}
	
	public double getZoom() {
		return zoom;
	}

	public ImmutableTree<FocusData> getGridData() {
		return gridData;
	}
	
	public RowTypeList getRowTypeList() {
		return rowTypes;
	}

	public double getViewWidth() {
		return viewWidth;
	}

	public double getViewHeight() {
		return viewHeight;
	}
	
}
