package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class QubitRegistersRenderLayer extends RenderLayer {

	public QubitRegistersRenderLayer(double width, double height) {
		super(width, height);
	}
	
	@Override
	public synchronized void render(double startX, double startY, double zoom) {
		startX -= GateRenderLayer.GRID_SIZE;
		super.render(startX < 0? 0d : startX, startY, zoom);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		graphics.setBoundsManaged(false);
		ImmutableTree<FocusData> fd = (ImmutableTree<FocusData>) userArgs[0];
		FocusData gridData = fd.getElement();
		graphics.setFont(CustomFXGraphics.DEFAULT, 15);
		
		for(int r = 0; r < gridData.getRowCount(); r++) {
			setFocus(graphics, gridData, r); {
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				Color white = new Color(1d, 1d, 1d, .75d);
				graphics.setColor(white);
				graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				graphics.setColor(Color.BLACK);
				graphics.drawLatex("\\(\\vert\\Psi_{" + r + "}\\rangle\\)", 0, 0);
			} graphics.escapeFocus();
		}
	}
	
	public void setFocus(Graphics<Image, Font, Color> graphics, FocusData gridData, int row) {
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
		double topMargin = row == 0? 0d : gridData.getCummulativeHeight(row - 1);
		double height = gridData.getRowHeightAt(row);
		graphics.setFocus(new RimBound(0, 0, false, false), new RimBound(topMargin, 0, false, false), GateRenderLayer.GRID_SIZE, height);
	}
	
	
}
