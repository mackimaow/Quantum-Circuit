package appFX.appUI.appViews.circuitBoardView;

import appFX.appUI.AppAlerts;
import appFX.framework.AppStatus;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Region;

public class RowColumnToolAction extends ToolAction {
	
	public static final boolean ADD = true;
	public static final boolean REMOVE = false;
	public static final boolean ROW = true;
	public static final boolean COLUMN = false;
	private static final boolean TOP_OR_LEFT = true;
	private static final boolean BOTTOM_OR_RIGHT = false;
	
	private static final String formatString = "-fx-background-color: #%s;-fx-border-color: #%s;-fx-border-width: %d %d %d %d;";
	
	
	private CircuitBoardView cbv;
	private final boolean addRemove;
	private final boolean rowColumn;
	private Boolean cursorSide = null;
	
	public RowColumnToolAction(CircuitBoardView cbv, boolean addRemove, boolean rowColumn) {
		super(true);
		this.cbv = cbv;
		this.addRemove = addRemove;
		this.rowColumn = rowColumn;
	}
	
	@Override
	public void initToolCursorRender(Region cursor) {
		if (addRemove == ADD)
			setStyle(cursor, 0, 0, 0, 0);
		else
			setStyle(cursor, 3, 3, 3, 3);
	}
	
	@Override
	public void updateCursorPosition(Region cursor, int row, int column) {
		if (addRemove == ADD)
			setStyle(cursor, 0, 0, 0, 0);
		
		if(rowColumn == ROW)
			GridPane.setConstraints(cursor, 1, row, GridPane.REMAINING, 1);
		else
			GridPane.setConstraints(cursor, column + 1, 0, 1, GridPane.REMAINING);
	}
	
	@Override
	public void buttonPressed(int row, int column) {
		try {
			if(rowColumn == ROW) {
				if(addRemove == ADD) {
					if (cursorSide != null) {
						cbv.getCircuitBoardModel().addRows(row + (cursorSide == BOTTOM_OR_RIGHT? 1 : 0) , 1);
					}
				} else {
					cbv.getCircuitBoardModel().removeRows(row, row + 1);
				}
			} else {
				if(addRemove == ADD) {
					if (cursorSide != null) {
						cbv.getCircuitBoardModel().addColumns(column + (cursorSide == BOTTOM_OR_RIGHT? 1: 0), 1);
					}
				} else {
					cbv.getCircuitBoardModel().removeColumns(column, column + 1);
				}
			}
		} catch(IllegalArgumentException iae) {
			AppAlerts.showMessage(AppStatus.get().getPrimaryStage(), 
					"Could not " + (addRemove == ADD? "add":"remove") + " " + (rowColumn == ROW? "Row":"Column"),
					iae.getMessage(), AlertType.ERROR);
		}
	}

	@Override
	public void reset() {
		
	}

	@Override
	public boolean isCursorDisplayed() {
		return true;
	}

	@Override
	public void mouseMoved(Region cursor, double x, double y, double boundX, double boundY) {
		if(addRemove == ADD) {
			if (rowColumn == ROW) {
				if (y < boundY / 2) {
					cursorSide = TOP_OR_LEFT;
					setStyle(cursor, 0, 3, 0, 0);
				} else {
					cursorSide = BOTTOM_OR_RIGHT;
					setStyle(cursor, 0, 0, 0, 3);
				}
			} else {
				if (x < boundX / 2) {
					cursorSide = TOP_OR_LEFT;
					setStyle(cursor, 3, 0, 0, 0);
				} else {
					cursorSide = BOTTOM_OR_RIGHT;
					setStyle(cursor, 0, 0, 3, 0);
				}
			}
		}
	}
	private void setStyle(Region cursor, int leftBorder, int topBorder, int rightBorder, int bottomBorder) {
		String bkColor = addRemove == ADD? "00FF0011" : "FF000077";
		String bdColor = addRemove == ADD? "00FF00" : "FF0000";
		String style = String.format(formatString, bkColor, bdColor, topBorder, rightBorder, bottomBorder, leftBorder);
		cursor.setStyle(style);
	}

}
