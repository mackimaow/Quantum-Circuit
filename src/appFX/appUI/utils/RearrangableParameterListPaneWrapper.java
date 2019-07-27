package appFX.appUI.utils;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

public class RearrangableParameterListPaneWrapper extends RearrangableListPaneWrapper {

	public RearrangableParameterListPaneWrapper(VBox p) {
		super(p);
	}

	@Override
	protected Node createElement(Object... args) {
		String text = args.length == 0? "" : (String) args[0];
		return new TextField(text);
	}

	@Override
	protected Object[] getArgs(Node element) {
		TextField tf = (TextField) element;
		String[] args = {tf.getText()};
		return args;
	}

	@Override
	protected boolean whenElementIsRemoved(int index, Node n) {
		return true;
	}

	@Override
	protected boolean whenElementIsAdded(int index, Node n) {
		return true;
	}

	@Override
	protected boolean whenElementMoves(int indexFirst, int indexNext) {
		return true;
	}

	@Override
	protected boolean whenCleared() {
		return true;
	}
	
	
	
}
