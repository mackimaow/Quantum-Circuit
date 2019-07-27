package appFX.appUI.utils;

import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class RearrangableMatrixDefinitionPaneWrapper extends RearrangableListPaneWrapper {
	private String formatString;
	
	public RearrangableMatrixDefinitionPaneWrapper (VBox container, String formatString) {
		super(container);
		this.formatString = formatString;
	}
	
	@Override
	protected Node createElement(Object... args) {
		String text = args.length == 0? "" : (String) args[0];
		MatrixDefinitionView mdv = new MatrixDefinitionView(size(), text);
		return mdv;
	}
	
	@Override
	protected Object[] getArgs(Node element) {
		MatrixDefinitionView mdv = (MatrixDefinitionView) element;
		String[] args = {mdv.getString()};
		return args;
	}
	
	@Override
	protected boolean whenElementIsRemoved(int index, Node n) {
		while(++index != size()) {
			MatrixDefinitionView kv = (MatrixDefinitionView) getElement(index);
			kv.setLatex(index - 1);
		}
		return true;
	}
	
	@Override
	protected boolean whenElementIsAdded(int index, Node n) {
		return true;
	}
	
	@Override
	protected boolean whenElementMoves(int indexFirst, int indexNext) {
		String selected = ((MatrixDefinitionView) getElement(indexFirst)).getString();
		
		int i = 0;
		for(; i < size(); i++) {
			if(i == indexNext || i == indexFirst)
				break;
		}
		
		if(i == indexFirst) {
			MatrixDefinitionView next = (MatrixDefinitionView) getElement(i);
			for(; i != indexNext; i++) {
				MatrixDefinitionView cur = next;
				next = (MatrixDefinitionView) getElement(i + 1);
				cur.setString(next.getString());
			}
			next.setString(selected);
		} else if(i == indexNext) {
			for(; i < size() && i <= indexFirst; i++) {
				MatrixDefinitionView cur = (MatrixDefinitionView) getElement(i);
				String temp = cur.getString();
				cur.setString(selected);
				selected = temp;
			}
		}
		
		return false;
	}

	@Override
	protected boolean whenCleared() {
		return true;
	}
	
	
	private class MatrixDefinitionView extends HBox {
		MatrixDefinitionView(int number, String input) {
			String s = formatString.replaceAll("%i", Integer.toString(number));
			LatexNode lv = new LatexNode(s, 20, "#00000000", "#000000");
			TextField inputText = new TextField(input);
			ObservableList<Node> children = getChildren();
			children.addAll(lv, inputText);
			
			setSpacing(5);
			setAlignment(Pos.CENTER_LEFT);
			HBox.setHgrow(inputText, Priority.ALWAYS);
		}
		
		void setLatex(int number) {
			getLatexNode().setLatex("$$ k_{" + number + "} = $$");
		}
		
		LatexNode getLatexNode() {
			return (LatexNode) getChildren().get(0);
		}
		
		String getString() {
			return ((TextField)getChildren().get(1)).getText();
		}
		
		void setString(String s) {
			((TextField)getChildren().get(1)).setText(s);
		}
	}
}
