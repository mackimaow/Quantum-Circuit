package appFX.appUI.utils;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public abstract class SequencePaneElement extends AppFXMLComponent implements Initializable {
	
	private PaneFieldDataList fieldData;
	private Node node = null;
	
	public SequencePaneElement(String fxmlFilename) {
		super(fxmlFilename);
		this.fieldData = new PaneFieldDataList();
	}
	
	public static boolean checkTextFieldError(Stage stage, TextField textField, boolean hasError, String errorTitle, String errorMessage) {
		if(hasError) {
			if(textField != null)
				textField.setStyle("-fx-background-color: #ff000033");
			AppAlerts.showMessage(stage, errorTitle, errorMessage, AlertType.ERROR);
			return true;
		} else {
			if(textField != null)
				textField.setStyle("");
			return false;
		}
	}
	
	public static boolean checkPaneError(Stage stage, Pane pane, boolean hasError, String errorTitle, String errorMessage) {
		if(hasError) {
			pane.setStyle("-fx-border-width: 3; -fx-border-color: #ff000033");
			AppAlerts.showMessage(stage, errorTitle, errorMessage, AlertType.ERROR);
			return true;
		} else {
			pane.setStyle("");
			return false;
		}
	}
	
	public static Integer getInteger(String integerString) {
		try {
			return Integer.parseInt(integerString);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	public static Double getDouble(String doubleString) {
		try {
			return Double.parseDouble(doubleString);
		} catch(NumberFormatException e) {
			return null;
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initialize();
		setStartingFieldData(fieldData);
	}
	
	void onOpen(Object ... args) {
		fieldData.put(args);
	}
	
	Node getNode() {
		if(node == null)
			node = loadAsNode();
		return node;
	}
	
	public PaneFieldDataList getFieldData() {
		return fieldData;
	}
	
	public abstract void setStartingFieldData(PaneFieldDataList fieldData);
	public abstract void initialize();
	
	
	public interface SequencePaneNext {
		public abstract boolean hasNext();
		public abstract Object[] giveNext();
		public abstract boolean checkNext();
		public abstract SequencePaneElement getNext();
	}
	
	public interface SequencePanePrevious {
		public abstract boolean hasPrevious();
		public abstract boolean checkPrevious();
	}
	
	public interface SequencePaneFinish<T> {
		public abstract boolean hasFinish();
		public abstract boolean checkFinish();
		public abstract T getFinish();
	}
	
	public class PaneFieldDataList {
		private ArrayList<Object> fields = new ArrayList<>();
		
		public void add(Object field) {
			add(field, null);
		}
		
		private void put(Object ... args) {
			if(args == null) return;
			for(int i = 0; i < args.length; i++)
				put(i, args[i]);
		}
		
		private <T> void put(int index, T defaultValue) {
			Object field = fields.get(index);
			putValueInField(field, defaultValue);
		}
		
		@SuppressWarnings("unchecked")
		private <T, K extends T> void putValueInField(Object field, K defaultValue) {
			if(defaultValue == null) return;
			if(field instanceof TextField) {
				((TextField) field).setText(defaultValue.toString());
			} else if (field instanceof TextArea) {
				((TextArea) field).setText(defaultValue.toString());
			} else if (field instanceof ComboBox) {
				ComboBox<T> comboBox = (ComboBox<T>) field;			
				comboBox.getSelectionModel().select(defaultValue);
			} else if (field instanceof RearrangableListPaneWrapper) {
				Object[] elementArgs = (Object[]) defaultValue;
				putValueInPaneWrapper((RearrangableListPaneWrapper) field, elementArgs);
			}
		}
		
		private void putValueInPaneWrapper(RearrangableListPaneWrapper field, Object[] args) {
			if(args instanceof Object[][]) {
				for(int i = 0; i < args.length; i++)
					field.addElementToEnd((Object[])args[i]);
			} else {
				for(int i = 0; i < args.length; i++)
					field.addElementToEnd(args[i]);
			}
		}
		
		public <K> void add(Object field, K defaultValue) {
			int index = fields.indexOf(field);
			if(index == -1)
				fields.add(field);
			putValueInField(field, defaultValue);
		}
	}
}
