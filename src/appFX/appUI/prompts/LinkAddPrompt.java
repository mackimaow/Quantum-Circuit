package appFX.appUI.prompts;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.AppFXMLComponent;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utils.customCollections.Pair;

public class LinkAddPrompt extends Prompt<Pair<Boolean, Integer>>{
	public static final String ADD_LINK_TEXT = "Add Link";
	public static final String CANCEL_BUTTON_TEXT = "Cancel";
	public static final String INPUT_TYPE = "Input";
	public static final String OUTPUT_TYPE = "Output";
	
	private final CenterNode centerNode;
	private final boolean hasInput;
	
	public LinkAddPrompt(boolean hasInput) {
		super(200, 300, new String[] {ADD_LINK_TEXT, CANCEL_BUTTON_TEXT});
		centerNode = new CenterNode();
		setTitle("Add Classical Link");
		this.hasInput = hasInput;
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
		
		String amtString = centerNode.register.getText().trim();
		int reg;
		try {
			reg = Integer.parseInt(amtString);
			if(reg < 0)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			AppAlerts.showMessage(getStage(), "Invalid Register", "Please enter a valid classical register from the chosen gate", AlertType.ERROR);
			return;
		}
		String type = centerNode.inputType.getValue();
		Pair<Boolean, Integer> output = new Pair<>(type.equals(INPUT_TYPE), reg);
		promptFinish(output);
	}

	@Override
	protected Node getCenterNode() {
		return centerNode.loadAsNode();
	}
	
	
	private class CenterNode extends AppFXMLComponent implements Initializable {
		@FXML private ComboBox<String> inputType;
		@FXML private TextField register;
		
		public CenterNode() {
			super("prompts/LinkAddPrompt.fxml");
		}

		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			ObservableList<String> list = inputType.getItems();
			if(hasInput)
				list.add(INPUT_TYPE);
			list.add(OUTPUT_TYPE);
			inputType.getSelectionModel().clearAndSelect(0);
		}
	}
}
