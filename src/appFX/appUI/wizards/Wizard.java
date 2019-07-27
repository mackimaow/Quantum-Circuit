package appFX.appUI.wizards;

import appFX.appUI.utils.SequencePane;
import appFX.appUI.utils.SequencePane.OnFinishListener;
import appFX.appUI.utils.SequencePaneElement;
import appFX.framework.AppStatus;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public abstract class Wizard<T> implements OnFinishListener<T> {
	private Stage dialog = null;
	private T element = null;
	private final int width, height;
	
	public Wizard(int width, int height) {
		this.width = width;
		this.height = height;
		this.dialog = new Stage();
	}
	
	public T openWizardAndGetElement(Object ... args) {
		SequencePane<T> sp = new SequencePane<T>();
		dialog.setScene(new Scene((Parent)sp.loadAsNode(), width, height));
		sp.start(getFirstSeqPaneElem(), this, args);
		Window w = AppStatus.get().getPrimaryStage();
		dialog.initOwner(w);
		dialog.initModality(Modality.APPLICATION_MODAL);
		dialog.setOnCloseRequest((event)-> {
			if(!onWizardCloseRequest()) event.consume();
		});
		dialog.showAndWait();
		dialog = new Stage();
		return element;
	}
	
	protected void close() {
		if(dialog != null)
			dialog.close();
	}
	
	public void sequenceFinish(T product) {
		element = product;
		close();
	};
	
	protected abstract boolean onWizardCloseRequest();
	protected abstract SequencePaneElement getFirstSeqPaneElem();
	
	protected void setTitle(String title) {
		dialog.setTitle(title);
	}
	
	protected Stage getStage() {
		return dialog;
	}
}
