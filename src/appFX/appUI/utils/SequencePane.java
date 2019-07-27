package appFX.appUI.utils;

import java.net.URL;
import java.util.ResourceBundle;

import appFX.appUI.utils.SequencePaneElement.SequencePaneFinish;
import appFX.appUI.utils.SequencePaneElement.SequencePaneNext;
import appFX.appUI.utils.SequencePaneElement.SequencePanePrevious;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import utils.customCollections.Stack;

public class SequencePane<T> extends AppFXMLComponent implements Initializable {
	
	@FXML
	private ScrollPane contentPane;
	@FXML
	private Button previousButton, nextButton, finishButton;
	
	private SequencePaneElement currentComponent;
	private OnFinishListener<T> finishListener;
	private Stack<SequencePaneElement> paneControlData;
	private boolean initialized = false;
	private boolean started = false;
	
	public SequencePane() {
		super("utils/SequencePane.fxml");
		paneControlData = new Stack<>();
	}
	
	public void start(SequencePaneElement firstComponent, OnFinishListener<T> finishListener, Object ... args) {
		if(initialized && !started) {
			started = true;
			this.finishListener = finishListener;
			setNextComponent(firstComponent);
			firstComponent.onOpen(args);
		}
	}
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		initialized = true;
		previousButton.setDisable(true);
		nextButton.setDisable(true);
		finishButton.setDisable(true);
	}
	
	private void setNextComponent(SequencePaneElement nextComponent) {
		currentComponent = nextComponent;
		previousButton.setDisable(!(currentComponent instanceof SequencePanePrevious));
		nextButton.setDisable(!(currentComponent instanceof SequencePaneNext));
		finishButton.setDisable(!(currentComponent instanceof SequencePaneFinish));
		contentPane.setContent(currentComponent.getNode());
	}
	
	
	@FXML
	private void onPreviousPressed(ActionEvent e) {
		if(!(currentComponent instanceof SequencePanePrevious)) return;
		SequencePanePrevious component = (SequencePanePrevious) currentComponent;
		if(component.checkPrevious()) {
			SequencePaneElement next = paneControlData.pop();
			setNextComponent(next);
		}
	}
	
	@FXML
	private void onNextPressed(ActionEvent e) {
		if(!(currentComponent instanceof SequencePaneNext)) return;
		SequencePaneNext component = (SequencePaneNext) currentComponent;
		if(component.checkNext()) {
			SequencePaneElement next = component.getNext();
			setNextComponent(next);
			paneControlData.push((SequencePaneElement)component);
			next.onOpen(component.giveNext());
		}
	}
	
	@SuppressWarnings("unchecked")
	@FXML private void onFinishedPressed(ActionEvent e) {
		if(!(currentComponent instanceof SequencePaneFinish)) return;
		SequencePaneFinish<T> component = (SequencePaneFinish<T>) currentComponent;
		if(component.checkFinish()) {
			previousButton.setDisable(true);
			nextButton.setDisable(true);
			finishButton.setDisable(true);
			finishListener.sequenceFinish(component.getFinish());
		}
	}
	
	public interface OnFinishListener<T> {
		public void sequenceFinish(T product);
	}
}
