package appFX.appUI.appViews.circuitBoardView.copyPasteTools;

public class GateRectangularSelection {
	private int rowStart, columnStart, rowEnd, columnEnd;
	
	public GateRectangularSelection(int row1, int column1, int row2, int column2) {
		if (row1 > row2) {
			this.rowStart = row2;
			this.rowEnd = row1;
		} else {
			this.rowStart = row1;
			this.rowEnd = row2;
		}
		if(column1 > column2) {
			this.columnStart = column2;
			this.columnEnd = column1;
		} else {
			this.columnStart = column1;
			this.columnEnd = column2; 
		}
	}
	
	
	public void setSelection(GateRectangularSelection selection) {
		rowStart = selection.rowStart;
		columnStart = selection.columnStart;
		rowEnd = selection.rowEnd;
		columnEnd = selection.columnEnd;
	}
	
	
	public boolean isCompletetlyEnclosedWithin(GateRectangularSelection selection) {
		return boundsOnAxisContained(rowStart, rowEnd, selection.rowStart, selection.rowEnd) &&
				boundsOnAxisContained(columnStart, columnEnd, selection.columnStart, selection.columnEnd);
	}
	
	public GateRectangularSelection getIntersectSelection(GateRectangularSelection selection) {
		if(checkIfIntersectWith(selection)) {
			if(isCompletetlyEnclosedWithin(selection))
				return this;
			if(selection.isCompletetlyEnclosedWithin(this))
				return selection;
			
			int rowStart 	= 0;
			int columnStart = 0;
			int rowEnd 		= 0;
			int columnEnd 	= 0;
			
			if(this.rowStart <= selection.rowStart)
				rowStart 	= selection.rowStart;
			else
				rowStart 	= this.rowStart;
			
			if(this.rowEnd < selection.rowEnd)
				rowEnd 		= this.rowEnd;
			else
				rowEnd		= selection.rowEnd;
			
			if(this.columnStart <= selection.columnStart)
				columnStart 	= selection.columnStart;
			else
				columnStart 	= this.columnStart;
			
			if(this.columnEnd < selection.columnEnd)
				columnEnd 		= this.columnEnd;
			else
				columnEnd		= selection.columnEnd;

			return new GateRectangularSelection(rowStart, columnStart, rowEnd, columnEnd);
			
		} else return null;
	}
	
	public boolean checkIfIntersectWith(GateRectangularSelection selection) {
		return intersectOnAxis(rowStart, rowEnd, selection.rowStart, selection.rowEnd) && 
				intersectOnAxis(columnStart, columnEnd, selection.columnStart, selection.columnEnd);
	}
	
	
	
	public int getRowStart() {
		return rowStart;
	}



	public int getColumnStart() {
		return columnStart;
	}



	public int getRowEnd() {
		return rowEnd;
	}

	public int getColumnEnd() {
		return columnEnd;
	}

	@Override
	public String toString() {
		return " [ t: " + rowStart + " r: " + columnEnd + " b: " + rowEnd + " l: " + columnStart + " ] ";
	}
	
	
	private static boolean intersectOnAxis(int axisStart1, int axisEnd1, int axisStart2, int axisEnd2) {
		return axisEnd1 >= axisStart2 && axisEnd2 >= axisStart1;
	}
	
	private static boolean boundsOnAxisContained(int axisStart1, int axisEnd1, int axisStart2, int axisEnd2) {
		return axisStart1 >= axisStart2  && axisEnd1 <= axisEnd2;
	}
}
