package appFX.appUI.prompts;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.utils.AppFXMLComponent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;

public class PryRemovePrompt extends Prompt<Boolean[]>{
	public static final String REMOVE_TEXT = "Remove Selected";
	public static final String CANCEL_BUTTON_TEXT = "Cancel";


	private final CenterNode centerNode;
	private final boolean hasInputLink;
	private final boolean hasControl;
	private final boolean hasOutputLink;
	
	public PryRemovePrompt(boolean hasInputLink, boolean hasControl, boolean hasOutputLink) {
		super(300, 300, new String[] {REMOVE_TEXT, CANCEL_BUTTON_TEXT});
		this.hasInputLink = hasInputLink; 
		this.hasControl = hasControl;
		this.hasOutputLink = hasOutputLink;
		centerNode = new CenterNode();
	}

	@Override
	protected boolean onPromptCloseRequest() {
		return true;
	}

	@Override
	protected void onButtonPressed(String buttonName) {
		if(buttonName.equals(CANCEL_BUTTON_TEXT)) {
			promptFinish(null);
			return;
		}
		boolean removeGate = centerNode.removeGate.isSelected();
		boolean removeInputLink = centerNode.removeInputLink.isSelected() && hasInputLink;
		boolean removeControl = centerNode.removeControl.isSelected() && hasControl;
		boolean removeOutputLink = centerNode.removeOutputLink.isSelected() && hasOutputLink;
		promptFinish(new Boolean[] {removeGate, removeInputLink, removeControl, removeOutputLink});
	}

	@Override
	protected Node getCenterNode() {
		return centerNode.loadAsNode();
	}
	
	private class CenterNode extends AppFXMLComponent implements Initializable, EventHandler<ActionEvent> {
		@FXML private CheckBox removeGate, removeInputLink, removeControl, removeOutputLink;
		
		public CenterNode() {
			super("prompts/PryRemovePrompt.fxml");
		}

		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			if(!hasInputLink) {
				removeInputLink.setVisible(false);
				removeInputLink.setManaged(false);
			}
			if(!hasControl) {
				removeControl.setVisible(false);
				removeControl.setManaged(false);
			}
			if(!hasOutputLink) {
				removeOutputLink.setVisible(false);
				removeOutputLink.setManaged(false);
			}
			removeGate.setOnAction(this);
		}

		@Override
		public void handle(ActionEvent event) {
			boolean isChecked = removeGate.isSelected();
			removeInputLink.setDisable(isChecked);
			removeInputLink.setSelected(isChecked);
			removeControl.setDisable(isChecked);
			removeControl.setSelected(isChecked);
			removeOutputLink.setDisable(isChecked);
			removeOutputLink.setSelected(isChecked);
		}
	}
	
}
