package appFX.appUI.appViews.circuitBoardView.copyPasteTools;

import java.util.Iterator;
import java.util.LinkedList;

import utils.customCollections.LoopLinkedList;

public class GateSelectionBuffer implements Iterable<GateRectangularSelection> {
	private LinkedList<GateRectangularSelection> selectionBuffer;
	
	public GateSelectionBuffer() {
		selectionBuffer = new LinkedList<>();
	}
	
	public GateSelectionBuffer addSelection(GateRectangularSelection selection) {
		subtractSelection(selection);
		selectionBuffer.add(selection);
		return this;
	}
	
	public GateSelectionBuffer subtractSelection(GateRectangularSelection selection) {
		LinkedList<GateRectangularSelection> leftOverSelections = new LinkedList<>();
		
		Iterator<GateRectangularSelection> iterator = selectionBuffer.iterator();
		while(iterator.hasNext()) {
			GateRectangularSelection rectSelection = iterator.next();
			if(AddSubRectsFromSubtraction(leftOverSelections, rectSelection, selection))
				iterator.remove();
		}
		for(GateRectangularSelection leftOverSelection : leftOverSelections )
			selectionBuffer.add(leftOverSelection);
		return this;
	}
	
	public GateSelectionBuffer intersectSelection(GateRectangularSelection selection) {
		for(GateRectangularSelection rectSelection : selectionBuffer) {
			GateRectangularSelection intersection  = rectSelection.getIntersectSelection(selection);
			rectSelection.setSelection(intersection);
		}
		return this;
	}
	
	
	public GateSelectionBuffer inverseSelectionFrom(GateRectangularSelection selection) {
		GateSelectionBuffer buffer = new GateSelectionBuffer();
		buffer.addSelection(selection);
		for(GateRectangularSelection rect : this)
			buffer.subtractSelection(rect);
		selectionBuffer = buffer.selectionBuffer;
		
		return this;
	}
	
	private boolean AddSubRectsFromSubtraction(LinkedList<GateRectangularSelection> leftOverSelections,
			GateRectangularSelection totalSelection, GateRectangularSelection subtractSelection) {
		if(totalSelection.checkIfIntersectWith(subtractSelection)) {
			byte sidesEnclosed = 0;
			byte windNumber = 0;
			
			if(subtractSelection.getRowStart() <= totalSelection.getRowStart()) { // top
				sidesEnclosed++;
			}
			if(subtractSelection.getColumnEnd() >= totalSelection.getColumnEnd()) { // right
				windNumber += -1;
				sidesEnclosed++;
			}
			if(subtractSelection.getRowEnd() >= totalSelection.getRowEnd()) { // bottom
				windNumber += -2;
				sidesEnclosed++;
			}
			if(subtractSelection.getColumnStart() <= totalSelection.getColumnStart()) {	// left
				windNumber += 1;
				sidesEnclosed++;
			}
			
			if (sidesEnclosed == 0) {
				leftOverSelections.add(new GateRectangularSelection(
						totalSelection.getRowStart(), totalSelection.getColumnStart(), 
						totalSelection.getRowEnd(), subtractSelection.getColumnStart()));
				leftOverSelections.add(new GateRectangularSelection(
						totalSelection.getRowStart(), subtractSelection.getColumnStart(), 
						subtractSelection.getRowStart(), subtractSelection.getColumnEnd()));
				leftOverSelections.add(new GateRectangularSelection(
						totalSelection.getRowStart(), subtractSelection.getColumnEnd(),
						totalSelection.getRowEnd(), totalSelection.getColumnEnd()));
				leftOverSelections.add(new GateRectangularSelection(
						subtractSelection.getRowEnd(), subtractSelection.getColumnStart(), 
						totalSelection.getRowEnd(), subtractSelection.getColumnEnd()));
			} else if(sidesEnclosed != 4) {
				LoopLinkedList<Integer> totalRect = boundsToLoopList(totalSelection);
				LoopLinkedList<Integer> subRect = boundsToLoopList(subtractSelection);
				
				if (sidesEnclosed == 3) {
					LoopLinkedList<Integer> temp = new LoopLinkedList<>();
					
					totalRect.windTo(windNumber);
					subRect.windTo(windNumber);
					temp.windTo(windNumber);
					temp.add(subRect.get(2));
					temp.add(totalRect.get(1));
					temp.add(totalRect.get(2));
					temp.add(totalRect.get(3));
					leftOverSelections.add(loopListToBounds(temp));
				
				} else if(sidesEnclosed == 2) {
					if( windNumber % 2 == 0 ) {
						windNumber /= 2;
						
						for(int i = 0; i < 2; i++) {
							LoopLinkedList<Integer> temp = new LoopLinkedList<Integer>();
							totalRect.windTo(windNumber + 2*i);
							subRect.windTo(windNumber + 2*i);
							temp.windTo(windNumber + 2*i);
							temp.add(totalRect.get(0));
							temp.add(totalRect.get(1));
							temp.add(subRect.get(0));
							temp.add(totalRect.get(3));
							totalRect.resetWindness();
							subRect.resetWindness();
							leftOverSelections.add(loopListToBounds(temp));
						}
					} else {
						LoopLinkedList<Integer> temp1 = new LoopLinkedList<Integer>();
						LoopLinkedList<Integer> temp2 = new LoopLinkedList<Integer>();
						
						if(windNumber != -1)
							windNumber = (byte) (Math.abs(windNumber) - 1);
						else if (subtractSelection.getColumnEnd() <= totalSelection.getColumnEnd())
							windNumber *= -1;
						
						totalRect.windTo(-windNumber);
						subRect.windTo(-windNumber);
						temp1.windTo(-windNumber);
						temp2.windTo(-windNumber);
						temp1.add(subRect.get(2));
						temp1.add(subRect.get(1));
						temp1.add(totalRect.get(2));
						temp1.add(totalRect.get(3));

						temp2.add(totalRect.get(0));
						temp2.add(totalRect.get(1));
						temp2.add(totalRect.get(2));
						temp2.add(subRect.get(1));
						
						leftOverSelections.add(loopListToBounds(temp1));
						leftOverSelections.add(loopListToBounds(temp2));
					}
				} else {
					LoopLinkedList<Integer> temp1 = new LoopLinkedList<Integer>();
					LoopLinkedList<Integer> temp2 = new LoopLinkedList<Integer>();
					LoopLinkedList<Integer> temp3 = new LoopLinkedList<Integer>();
					
					totalRect.windTo(-windNumber);
					subRect.windTo(-windNumber);
					temp1.windTo(-windNumber);
					temp2.windTo(-windNumber);
					temp3.windTo(-windNumber);
					
					temp1.add(totalRect.get(0));
					temp1.add(subRect.get(3));
					temp1.add(totalRect.get(2));
					temp1.add(totalRect.get(3));
					
					temp2.add(subRect.get(2));
					temp2.add(subRect.get(1));
					temp2.add(totalRect.get(2));
					temp2.add(subRect.get(3));
					
					temp3.add(totalRect.get(0));
					temp3.add(totalRect.get(1));
					temp3.add(totalRect.get(2));
					temp3.add(subRect.get(1));
					
					leftOverSelections.add(loopListToBounds(temp1));
					leftOverSelections.add(loopListToBounds(temp2));
					leftOverSelections.add(loopListToBounds(temp3));
				}
			}
			
			return true;
		} else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		String s = "";
		for(GateRectangularSelection rect : selectionBuffer)
			s += rect + "\n";
		return s;
	}
	
	private GateRectangularSelection loopListToBounds(LoopLinkedList<Integer> list) {
		list.resetWindness();
		return new GateRectangularSelection(list.get(0), list.get(3), list.get(2), list.get(1));
	}
	
	
	private LoopLinkedList<Integer> boundsToLoopList(GateRectangularSelection selection) {
		LoopLinkedList<Integer> temp = new LoopLinkedList<>();
		temp.add(selection.getRowStart());
		temp.add(selection.getColumnEnd());
		temp.add(selection.getRowEnd());
		temp.add(selection.getColumnStart());
		return temp;
	}

	@Override
	public Iterator<GateRectangularSelection> iterator() {
		return selectionBuffer.iterator();
	}
}
