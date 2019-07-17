package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

public class SelectToolAction extends ToolAction {
	
	public SelectToolAction(CircuitBoardView cbv) {
		super(cbv);
	}
	
	
	@Override
	public void buttonPressed(double column, double offGridX, double row, double offGridY) {
		
	}

	@Override
	public void mouseMoved(double column, double offGridX, double row, double offGridY) {
		
	}

	@Override
	public void reset() {
		
	}


	@Override
	public void onToolStart(double column, double offGridX, double row, double offGridY) {
		
	}


	@Override
	public void renderOnLayer(Graphics<Image, Font, Color> graphics, FocusData gridData) {
		
	}

}
