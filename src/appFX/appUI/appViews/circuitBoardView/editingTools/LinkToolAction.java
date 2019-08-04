package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.GateRenderer;
import appFX.appUI.prompts.LinkAddPrompt;
import appFX.appUI.utils.AppAlerts;
import appFX.framework.AppStatus;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.GateModel;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SpacePin.OutputLinkType;
import graphicsWrapper.Graphics;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.Pair;

public class LinkToolAction extends GateAttributeTool {

	private static final Color CURSOR_COLOR = new Color(.7d, .7d, .7d, .5d);
	
	public LinkToolAction(CircuitBoardView cbv) {
		super(cbv);
	}

	@Override
	public void placeAttribute(CircuitBoardModel model, int rowGate, int rowAttribute, int column) {
		SolderedPin sp = model.getSolderedPinAt(rowGate, column);
		SolderedGate sg = sp.getSolderedGate();
		String locationString = sg.getGateModelLocationString();
		GateModel gm = AppStatus.get().getFocusedProject().getGateModel(locationString);
		if(gm == null) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), "Could not add Link", "Gate defined at: \"" + locationString + "\" does not exist.", AlertType.ERROR);
			return;
		}
		
		boolean hasInput = false;
		if(gm instanceof CircuitBoardModel)
			hasInput = true;
		
		LinkAddPrompt linkPrompt = new LinkAddPrompt(hasInput);
		Pair<Boolean, Integer> pair = linkPrompt.openPromptAndGetElement();
		
		try {
			if(pair != null) {
				if(pair.first())
					model.placeInputLink(rowAttribute, rowGate, column, pair.second());
				else
					model.placeOutputLink(rowAttribute, rowGate, column, OutputLinkType.CLASSICAL_LINK, pair.second());
			}
		} catch(IllegalArgumentException e) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), "Could not add Link", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void drawCursor(Graphics<Image, Font, Color> graphics, boolean isGateSelected) {
		graphics.setLayout(Graphics.CENTER_ALIGN, Graphics.CENTER_ALIGN);
		if(isGateSelected) {
			graphics.setColor(Color.WHITE);
			graphics.fillRect(0, 0, GateRenderer.GRID_SIZE / 5, GateRenderer.GRID_SIZE / 5);
			graphics.setColor(Color.BLACK);
			graphics.drawRect(0, 0,  GateRenderer.GRID_SIZE / 5, GateRenderer.GRID_SIZE / 5);
		} else {
			graphics.setColor(CURSOR_COLOR);
			graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		}
	}
	
}
