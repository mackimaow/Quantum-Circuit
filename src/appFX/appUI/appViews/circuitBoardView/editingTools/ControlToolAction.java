package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import appFX.appUI.appViews.circuitBoardView.renderer.CustomFXGraphics;
import appFX.appUI.appViews.circuitBoardView.renderer.GateRenderer;
import appFX.appUI.appViews.circuitBoardView.renderer.renderLayers.RenderLayer;
import appFX.appUI.utils.AppAlerts;
import appFX.framework.AppStatus;
import appFX.framework.gateModels.CircuitBoardModel;
import appFX.framework.gateModels.PresetGateType;
import appFX.framework.solderedGates.SolderedGate;
import appFX.framework.solderedGates.SolderedPin;
import appFX.framework.solderedGates.SolderedRegister;
import graphicsWrapper.FocusData;
import graphicsWrapper.Graphics;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import utils.customCollections.Pair;

public class ControlToolAction extends GateAttributeTool {
	
	private static final Color CURSOR_COLOR = new Color(.7d, .7d, .7d, .5d);
	
	private final boolean controlType;
	
	public ControlToolAction(CircuitBoardView cbv, boolean controlType) {
		super(cbv);
		this.controlType = controlType;
	}

	@Override
	public void placeAttribute(CircuitBoardModel model, int rowGate, int rowAttribute, int column) {
		
		try {
			model.placeControl(rowAttribute, rowGate, column, controlType);
		} catch(IllegalArgumentException e) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), "Could not add control", e.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void drawCursor(Graphics<Image, Font, Color> graphics, boolean isGateSelected) {
		if(isGateSelected) {
			GateRenderer.renderControlInFocus(graphics, CustomFXGraphics.RENDER_PALETTE, controlType);
		} else {
			graphics.setColor(CURSOR_COLOR);
			graphics.fillRect(0, 0, Graphics.FOCUS_WIDTH, Graphics.FOCUS_HEIGHT);
		}
	}
}
