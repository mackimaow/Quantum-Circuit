package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.CircuitBoardRenderer;
import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.framework.gateModels.CircuitBoardModel.RowTypeList;
import graphicsWrapper.CompiledGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.GraphicalBluePrint;
import graphicsWrapper.Graphics;
import graphicsWrapper.AxisBound.RimBound;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public abstract class RenderLayer implements GraphicalBluePrint<Image, Font, Color> {
	private Canvas canvas;
	private CompiledGraphics<Image, Font, Color> compiledGraphics = null;
	private CustomFXGraphics customFXGraphics;
	
	public RenderLayer(double width, double height) {
		canvas = new Canvas(width, height);
		customFXGraphics = new CustomFXGraphics(canvas.getGraphicsContext2D());
	}
	
	public synchronized ImmutableTree<FocusData> calculateBounds(ImmutableTree<FocusData> gridFocusData, RowTypeList rowTypeList) {
		compiledGraphics = Graphics.compileGraphicalBluePrint(this, gridFocusData, rowTypeList);
		return compiledGraphics.getFocusData();
	}
	
	public synchronized void render(double startX, double startY, double zoom) {
		GraphicsContext gc = canvas.getGraphicsContext2D();
		gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
		Graphics.graphicsDraw(startX, startY, zoom, compiledGraphics, customFXGraphics);
	}
	
	public synchronized void resize(double width, double height) {
		canvas.setWidth(width);
		canvas.setHeight(height);
	}
	
	public void addToCircuitBoardRender(CircuitBoardRenderer circuitBoardRenderer) {
		StackPane stackPane = (StackPane) circuitBoardRenderer.getAsNode();
		stackPane.getChildren().add(canvas);
	}
	
	public void setVisible(boolean visible) {
		canvas.setVisible(visible);
	}
	
	public static void setFocus(Graphics<Image, Font, Color> graphics, FocusData gridData, int rowStartInclusive, int rowEndExclusive, int columnStartInclusive, int columnEndExclusive) {
		double xOffset = columnStartInclusive == 0? 0d : gridData.getCummulativeWidth(columnStartInclusive - 1);
		double yOffset = rowStartInclusive == 0? 0d : gridData.getCummulativeHeight(rowStartInclusive - 1);
		double width = gridData.getColumnWidthSum(columnStartInclusive, columnEndExclusive);
		double height = gridData.getRowHeightSum(rowStartInclusive, rowEndExclusive);
		
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
		graphics.setFocus(new RimBound(xOffset, 0d, false, false), new RimBound(yOffset, 0d, false, false), width, height);
	}
	
	public static void setFocus(Graphics<Image, Font, Color> graphics, FocusData gridData, int row, int column) {
		setFocus(graphics, gridData, row, row + 1, column, column + 1);
	}
}

