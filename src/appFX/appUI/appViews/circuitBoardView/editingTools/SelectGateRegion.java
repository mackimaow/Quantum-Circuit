package appFX.appUI.appViews.circuitBoardView.editingTools;

import appFX.appUI.appViews.circuitBoardView.CircuitBoardView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;
import utils.customCollections.Pair;

public class SelectGateRegion extends Region {
	
	private final int row, column, height;
	
	public SelectGateRegion (CircuitBoardView cbv, int row, int column) {
		Pair<Integer, Integer> bounds = cbv.getCircuitBoardModel().getSolderedGateBodyBounds(row, column);
		
		this.row = bounds.first();
		this.column = column;
		this.height = bounds.second() - bounds.first();
		
		GridPane.setConstraints(this, column + 1, this.row, 1, height);
	}
	
	public int getFirstRow() {
		return row;
	}
	
	public int getColumn() {
		return column;
	}
	
	public int getRowHeight() {
		return height;
	}
}
