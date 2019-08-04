package appFX.appUI.prompts;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.utils.AppFXMLComponent;
import appFX.framework.AppStatus;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class Prompt<T> {
	private Stage dialog = null;
	private T element = null;
	private final int width, height;
	private final String[] buttons;
	
	public Prompt(int width, int height, String ... buttons) {
		this.width = width;
		this.height = height;
		this.buttons = buttons;
		this.dialog = new Stage();
	}
	
	public T openPromptAndGetElement() {
		PromptView pv = new PromptView();
		dialog.setScene(new Scene((Parent)pv.loadAsNode(), width, height));
		Window w = AppStatus.get().getPrimaryStage();
		dialog.initOwner(w);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setOnCloseRequest((event)-> {
			if(!onPromptCloseRequest()) event.consume();
		});
		dialog.showAndWait();
		dialog = new Stage();
		return element;
	}
	
	protected void close() {
		if(dialog != null)
			dialog.close();
	}
	
	protected void promptFinish(T product) {
		element = product;
		close();
	}
	
	protected abstract boolean onPromptCloseRequest();
	protected abstract void onButtonPressed(String buttonName);
	protected abstract Node getCenterNode();
	
	
	protected void setTitle(String title) {
		dialog.setTitle(title);
	}
	
	protected Stage getStage() {
		return dialog;
	}
	
	private class PromptView extends AppFXMLComponent implements Initializable {
		@FXML private ScrollPane contentPane;
		@FXML private HBox buttonBox;
		
		public PromptView() {
			super("prompts/Prompt.fxml");
		}

		@Override
		public void initialize(URL arg0, ResourceBundle arg1) {
			ObservableList<Node> children = buttonBox.getChildren();
			for(String text : buttons) {
				Button button = new Button(text);
				button.setOnAction((event) -> onButtonPressed(text));
				HBox.setMargin(button, new Insets(0, 5, 0, 0));
				children.add(button);
			}
			contentPane.setContent(getCenterNode());
		}
	}
}
