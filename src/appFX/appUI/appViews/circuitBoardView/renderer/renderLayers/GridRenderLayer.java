package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import graphicsWrapper.AxisBound.RimBound;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class GridRenderLayer extends RenderLayer {

	public GridRenderLayer(double width, double height) {
		super(width, height);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		graphics.setBoundsManaged(false);
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);

		graphics.customGraphicFunction((drawTool)-> {
			CustomFXGraphics gfx = (CustomFXGraphics) drawTool;
			gfx.setLineDashes(graphics.resize(5), graphics.resize(3));
		});
		
		graphics.setColor(Color.LIGHTGREY);
		
		ImmutableTree<FocusData> fd = (ImmutableTree<FocusData>) userArgs[0];
		FocusData gridData = fd.getElement();
		
		for(int r = 0; r < gridData.getRowCount(); r++) {
			for(int c = 0; c < gridData.getColumnCount(); c++) {
				setFocus(graphics, gridData, r, c); {
					graphics.drawRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
				} graphics.escapeFocus();
			}
		}
	}
}
