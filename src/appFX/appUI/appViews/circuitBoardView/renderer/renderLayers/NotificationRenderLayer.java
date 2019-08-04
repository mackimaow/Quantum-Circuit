package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.framework.gateModels.CircuitBoardModel.RowTypeList;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class NotificationRenderLayer extends RenderLayer {
	private boolean drawError = false;
	private int rowStart, rowEnd, column;
	
	public NotificationRenderLayer(double width, double height) {
		super(width, height);
	}
	
	public synchronized void calculateDrawErrorBounds(int rowStart, int rowEnd, int column, ImmutableTree<FocusData> gridFocusData, RowTypeList rowTypeList) {
		drawError = true;
		this.rowStart = rowStart;
		this.rowEnd = rowEnd;
		this.column = column;
		super.calculateBounds(gridFocusData, rowTypeList);
	}
	
	@Override
	public synchronized ImmutableTree<FocusData> calculateBounds(ImmutableTree<FocusData> gridFocusData, RowTypeList rowTypeList) {
		drawError = false;
		return super.calculateBounds(gridFocusData, rowTypeList);
	}
	
	@Override
	public synchronized void render(double startX, double startY, double zoom) {
		super.render(startX, startY, zoom);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		if(!drawError) return;
		ImmutableTree<FocusData> fd = (ImmutableTree<FocusData>) userArgs[0];
		FocusData gridData = fd.getElement();
		
		int numRows = gridData.getRowCount();
		int numCols = gridData.getColumnCount();
		
		if(rowStart >= numRows || rowEnd >= numRows || column >= numCols) return;
		
		Color color = new Color(.8d, 0, 0, .6d);
		graphics.setColor(color);
		RenderLayer.setFocus(graphics, gridData, rowStart, rowEnd + 1, column, column + 1); {
			graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		} graphics.escapeFocus();
		
	}
	
}
