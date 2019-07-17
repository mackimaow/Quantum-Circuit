package appFX.appUI.appViews.circuitBoardView.copyPasteTools;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

import appFX.appUI.appViews.circuitBoardView.copyPasteTools.GateSelectionBuffer.UnitSelection;

public class GateSelectionBuffer implements Iterable<UnitSelection> {
	private LinkedList<UnitSelection> selectionBuffer;
	
	public GateSelectionBuffer() {
		selectionBuffer = new LinkedList<>();
	}

	
	
	public void addSelection(GateSelectionBuffer toAdd) {
		for(UnitSelection selection : selectionBuffer) {
			
		}
	}
	
	public void removeSelection(GateSelectionBuffer toAdd) {
		
	}
	
	
	
	public void intersectSelection(GateSelectionBuffer toIntersect) {
		LinkedList<UnitSelection> newBuffer = new LinkedList<>();
		for(UnitSelection selection : toIntersect)
			intersectSelection(selection, newBuffer);
		selectionBuffer = newBuffer;
	}
	
	
	private void intersectSelection(UnitSelection toIntersect, LinkedList<UnitSelection> newBuffer) {
		ListIterator<UnitSelection> iterator = selectionBuffer.listIterator();
		while(iterator.hasNext()) {
			UnitSelection selection = iterator.next();
			UnitSelection intersect = selection.intersectSelection(toIntersect);
			if(intersect != null) {
				iterator.remove();
				newBuffer.add(intersect);
			}
		}
	}
	
	
	
	@Override
	public Iterator<UnitSelection> iterator() {
		return selectionBuffer.iterator();
	}
	
	
	public static class UnitSelection {
		private final boolean[] adjacentSelections = new boolean[] {false, false, false, false};
		private final int row, column;
		
		public UnitSelection(int row, int column) {
			this.row = row;
			this.column = column;
		}
		
		public boolean getAdjacentSelection(int direction) {
			return adjacentSelections[direction];
		}
		
		public int getRow() {
			return row;
		}
		
		public int getColumn() {
			return column;
		}
		
		public UnitSelection intersectSelection(UnitSelection other) {
			if(row != other.row || column != other.column)
				return null;
			for(int i = 0; i < adjacentSelections.length; i++)
				adjacentSelections[i] &= other.adjacentSelections[i];
			return this;
		}
	}
}
