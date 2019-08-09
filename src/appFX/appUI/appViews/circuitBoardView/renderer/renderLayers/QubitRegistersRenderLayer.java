package appFX.appUI.appViews.circuitBoardView.renderer.renderLayers;

import appFX.appUI.appViews.circuitBoardView.renderer.GateRenderer;
import appFX.appUI.utils.CustomFXGraphics;
import appFX.framework.gateModels.CircuitBoardModel.RowTypeList;
import graphicsWrapper.CompiledGraphics;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.immutableLists.ImmutableTree;

public class QubitRegistersRenderLayer extends RenderLayer {
	private ImmutableTree<FocusData> focusData = null;
	
	public QubitRegistersRenderLayer(double width, double height) {
		super(width, height);
	}
	
	@Override
	public synchronized ImmutableTree<FocusData> calculateBounds(ImmutableTree<FocusData> gridFocusData,
			RowTypeList rowTypeList) {
		focusData = super.calculateBounds(gridFocusData, rowTypeList);
		return focusData;
	}
	
	@Override
	public synchronized void render(double startX, double startY, double zoom) {
		startX -= focusData.getElement().getWidth();
		super.render(startX < 0? 0d : startX, startY, zoom);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void onDraw(Graphics<Image, Font, Color> graphics, Object... userArgs) {
		ImmutableTree<FocusData> fd = (ImmutableTree<FocusData>) userArgs[0];
		RowTypeList rowTypeList = (RowTypeList) userArgs[1];
		FocusData gridData = fd.getElement();
		GateRenderer.renderQubitRegs(graphics, CustomFXGraphics.RENDER_PALETTE, gridData, rowTypeList, true);
	}
	
	
}
