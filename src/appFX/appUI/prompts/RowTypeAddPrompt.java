package appFX.appUI.prompts;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.utils.AppAlerts;
import appFX.appUI.utils.AppFXMLComponent;
import appFX.framework.gateModels.CircuitBoardModel.RowType;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import utils.customCollections.Pair;

public class RowTypeAddPrompt extends Prompt<Pair<RowType, Integer>> {
	private static final String ADD_ROW_BUTTON_TEXT = "Add Rows";
	private static final String CANCEL_BUTTON_TEXT = "Cancel";
	
	private final CenterNode centerNode;
	private final RowType[] rowTypes;
	private final int rowTypeSelect;
	
	public RowTypeAddPrompt(int rowTypeSelect, RowType ... rowTypes) {
		super(200, 300, new String[] {ADD_ROW_BUTTON_TEXT, CANCEL_BUTTON_TEXT});
		centerNode = new CenterNode();
		this.rowTypes = rowTypes;
		this.rowTypeSelect = rowTypeSelect;
		setTitle("Add Row");
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
		String amtString = centerNode.amount.getText().trim();
		int amt;
		try {
			amt = Integer.parseInt(amtString);
			if(amt < 1)
				throw new NumberFormatException();
		} catch (NumberFormatException e) {
			AppAlerts.showMessage(getStage(), "Invalid Amount", "Please enter a valid positive integer amount", AlertType.ERROR);
			return;
		}
		RowType type = centerNode.rowType.getValue();
		Pair<RowType, Integer> output = new Pair<>(type, amt);
		promptFinish(output);
	}

	@Override
	protected Node getCenterNode() {
		return centerNode.loadAsNode();
	}
	
	
	private class CenterNode extends AppFXMLComponent implements Initializable {
		@FXML private ComboBox<RowType> rowType;
		@FXML private TextField amount;
		
		private CenterNode() {
			super("prompts/RowTypeAddPrompt.fxml");
		}

		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			ObservableList<RowType> list = rowType.getItems();
			for(RowType type : rowTypes)
				list.add(type);
			rowType.getSelectionModel().clearAndSelect(rowTypeSelect);
		}
		
	}
}
