package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import graphicsWrapper.AxisBound.RimBound;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class QubitLineRenderLayer extends RenderLayer {

	public QubitLineRenderLayer(double width, double height) {
		super(width, height);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		graphics.setBoundsManaged(false);
		graphics.setColor(Color.BLACK);
		
		ImmutableTree<FocusData> fd = (ImmutableTree<FocusData>) userArgs[0];
		FocusData gridData = fd.getElement();
		
		for(int i = 0; i < gridData.getRowCount(); i++) {
			setFocus(graphics, gridData, i); {
				graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
				graphics.drawLine(0, 0, Graphics.FOCUS_WIDTH, 0, true);
			} graphics.escapeFocus();
		}
	}
	
	private static void setFocus(Graphics<Image, Font, Color> graphics, FocusData gridData, int row) {
		double lowerMargin = row == 0? 0 : gridData.getCummulativeHeight(row - 1);
		double width = gridData.getWidth();
		double height = gridData.getRowHeightAt(row);
		graphics.setLayout(Graphics.LEFT_ALIGN, Graphics.TOP_ALIGN);
		graphics.setFocus(new RimBound(0, 0, true, true), new RimBound(lowerMargin, 0, false, false), width, height);
	}

}
