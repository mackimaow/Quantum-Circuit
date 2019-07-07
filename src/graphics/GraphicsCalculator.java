package graphics;
import java.util.ArrayList;
import java.util.LinkedList;

import graphics.FocusBounds.AxisBound;
import graphics.FocusBounds.GridBound;
import graphics.FocusBounds.RimBound;
import utils.customCollections.Stack;
import utils.customCollections.Tree;

class GraphicsCalculator<ImageType, FontType> extends Graphics<ImageType, FontType> {
	
	private Stack<Tree<FocusData>> focusContainers = new Stack<>();
	private Stack<Double> minimumWidths = new Stack<>();
	private Stack<Double> minimumHeights = new Stack<>();
	
	
	public static <ImageType, FontType> CompiledGraphics<ImageType, FontType> compileGraphics(GraphicalBluePrint<ImageType, FontType> bluePrint) {
		GraphicsCalculator<ImageType, FontType> calculator = new GraphicsCalculator<>();
		bluePrint.onDraw(calculator);
		
		Tree<FocusData> top = calculator.getData();
		FocusData topElement = top.getElement();
		LinkedList<Tree<FocusData>> treeChildren = top.getChildren();
		for(Tree<FocusData> childTree : treeChildren)
			readjustBounds(topElement, childTree);
		
		return new CompiledGraphics<>(top, bluePrint);
	}
	
	private GraphicsCalculator() {
		focusContainers.push(new Tree<>(new FocusData()));
		minimumWidths.push(0d);
		minimumHeights.push(0d);
	}
	
	private static void readjustBounds(FocusData parent, Tree<FocusData> bounds) {
		FocusData child = bounds.getElement();
		readjustAxisBound(parent.widthColumnData, child.widthColumnData);
		readjustAxisBound(parent.heightRowData, child.heightRowData);
			
		for(Tree<FocusData> childTree : bounds.getChildren())
			readjustBounds(child, childTree);
	}
	
	private static void readjustAxisBound(ArrayList<Double> parentAxisData, ArrayList<Double> childAxisData) {
		int last = (int) Math.round(popLast(childAxisData));
		
		double unadjustedLength = 0;
		double adjustedLength = 0;
		if(last < 0) {
			last *= -1;
			boolean stretchLow = (last & 0b10) != 0;
			boolean stretchHigh = (last & 0b100) != 0;
			
			int layout = (last & 0b10000) - (last & 0b1000); 
			int lowMargin = (int) Math.round(popLast(childAxisData));
			int highMargin = (int) Math.round(popLast(childAxisData));
			
			double parentLength = parentAxisData.get(parentAxisData.size() - 1);
			unadjustedLength = childAxisData.get(childAxisData.size() - 1);
			if(layout == CENTER_ALIGN) {
				double lowLength = stretchLow? parentLength / 2d - lowMargin : unadjustedLength / 2d;
				double highLength = stretchHigh? parentLength / 2d - highMargin : unadjustedLength / 2d;
				adjustedLength = lowLength + highLength;
			} else if (layout < 0 && stretchHigh || layout > 0 && stretchLow) {
				adjustedLength = parentLength - lowMargin - highMargin;
			}
			
		} else {
			int lowerBound = last;
			int upperbound = (int) Math.round(popLast(childAxisData));
			
			adjustedLength = parentAxisData.get(upperbound - 1) - (lowerBound == 0? 0:parentAxisData.get(lowerBound));
			unadjustedLength = childAxisData.get(childAxisData.size() - 1);
		}
		
		if(adjustedLength > unadjustedLength) {
			double amtToAdd = (adjustedLength - unadjustedLength) / childAxisData.size();
			for(int i = 0; i < childAxisData.size(); i++) {
				double newValue = ( i + 1 ) * amtToAdd + childAxisData.get(i);
				childAxisData.set(i, newValue); 
			}
		}
	}
	
	private static double popLast(ArrayList<Double> list) {
		double last = list.get(list.size() - 1); 
		list.remove(list.size() - 1);
		return last;
	}
	
	private Tree<FocusData> getData() {
		return focusContainers.peak();
	}
	
	private double getMinimumWidth() {
		return minimumWidths.peak();
	}
	
	private double getMinimumHeight() {
		return minimumHeights.peak();
	}
	
	private void setMinimWidth(double width) {
		minimumWidths.pop();
		minimumWidths.push(width);
	}
	
	private void setMinimumHeight(double height) {
		minimumHeights.pop();
		minimumHeights.push(height);
	}
	
	@Override
	protected void changeInFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		Tree<FocusData> tree = new Tree<>(new FocusData());
		focusContainers.peak().add(tree);
		focusContainers.add(tree);
		minimumWidths.push(0d);
		minimumHeights.push(0d);
	}

	@Override
	protected void changeOutFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		FocusData focusDataChild = focusContainers.pop().getElement();
		FocusData focusDataParent = focusContainers.peak().getElement();
		updateLengthsAxisParent(focusDataParent.widthColumnData, focusDataChild.widthColumnData, minimumWidths, horizontalBounds, getHorizontalLayout(), focusDataChild.getWidth());
		updateLengthsAxisParent(focusDataParent.heightRowData, focusDataChild.heightRowData, minimumHeights, verticalBounds, getVerticalLayout(), focusDataChild.getHeight());
	}
	
	private static void updateLengthsAxisParent(ArrayList<Double> parentAxisData, ArrayList<Double> childAxisData, Stack<Double> lengths, AxisBound axisBounds, int layout, double childLength) {
		double minimumAxisLength = lengths.pop();
		if(childAxisData.isEmpty()) {
			childAxisData.add(minimumAxisLength);
		} else {
			double currentCumulativeLength = 0;
			for(int i = 0; i < childAxisData.size(); i++) {
				currentCumulativeLength += childAxisData.get(i);
				childAxisData.set(i, currentCumulativeLength);
			}
			if(minimumAxisLength > currentCumulativeLength) {
				double addToEach = (minimumAxisLength-currentCumulativeLength) / childAxisData.size();
				currentCumulativeLength = 0;
				for(int i = 0; i < childAxisData.size(); i++) {
					currentCumulativeLength =  childAxisData.get(i) + (i+1) * addToEach;
					childAxisData.set(i, currentCumulativeLength);
				}
			}
		}
		
		if(axisBounds instanceof RimBound) {
			RimBound rimBound = (RimBound) axisBounds;
			double totalChildLength = rimBound.lowMargin + childLength + rimBound.highMargin;
			Double currentLength = lengths.peak();
			if(totalChildLength > currentLength) {
				lengths.pop();
				lengths.push(totalChildLength);
			}
			childAxisData.add((double)rimBound.highMargin); // removed after readjustment
			childAxisData.add((double)rimBound.lowMargin); // removed after readjustment
			double rimStreched = -1 + (rimBound.stretchLow ? -0b10:0) + (rimBound.stretchHigh? -0b100:0) 
					+ (layout < 0? -0b1000:0) + (layout > 0? -0b10000:0);
			childAxisData.add(rimStreched); // removed after readjustment
		} else {
			GridBound gridBound = (GridBound) axisBounds;
			int axisStartInclusize = gridBound.lowGrid;
			int axisEndExclusize = gridBound.highGrid;
			double currentCumulativeLength = 0;
			
			while(parentAxisData.size() < axisEndExclusize)
				parentAxisData.add(0d);
			
			for(int i = axisStartInclusize; i < axisEndExclusize; i++)
				currentCumulativeLength += parentAxisData.get(i);
			
			if(childLength > currentCumulativeLength) {
				double addToEach = (childLength-currentCumulativeLength) / (axisEndExclusize - axisStartInclusize);
				for(int i = axisStartInclusize; i < axisEndExclusize; i++)
					parentAxisData.set(i, parentAxisData.get(i) + addToEach);
			}

			childAxisData.add((double)axisEndExclusize); // removed after readjustment
			childAxisData.add((double)axisStartInclusize); // removed after readjustment
		}
	}
	
	@Override
	protected GraphicalDrawTools<ImageType, FontType> getGraphicalDrawTools() {
		return null;
	}

	private int calcMinLength(int position, int length, int layout) {
		if(layout == CENTER_ALIGN)
			return length + 2 * Math.abs(position);
		return length + (position > 0 ? position : 0);
	}
	
	
	@Override
	protected int[] checkAndTranslateBounds(int x, int y, int width, int height) {
		int occupiedWidth = calcMinLength(x, width, getHorizontalLayout());
		int occupiedHeight = calcMinLength(y, height, getVerticalLayout());
		if(getMinimumWidth() < occupiedWidth)
			setMinimWidth(occupiedWidth);
		if(getMinimumHeight() < occupiedHeight)
			setMinimumHeight(occupiedHeight);
		return null;
	}
	
	public final static class FocusData {
		ArrayList<Double> widthColumnData = new ArrayList<>();
		ArrayList<Double> heightRowData = new ArrayList<>();
		
		public int getRowCount() {
			return heightRowData.size();
		}
		
		public int getColumnCount() {
			return widthColumnData.size();
		}
		
		public double getCummulativeWidth(int column) {
			return widthColumnData.get(column);
		}
		
		public double getCummulativeHeight(int row) {
			return heightRowData.get(row);
		}
		
		public double getColumnWidthAt(int column) {
			double previousSize = column == 0 ? 0 : widthColumnData.get(column - 1);
			return widthColumnData.get(column) - previousSize;
		}
		
		public double getRowHeightAt(int row) {
			double previousSize = row == 0 ? 0 : heightRowData.get(row - 1);
			return heightRowData.get(row) - previousSize;
		}
		
		public double getWidth() {
			return widthColumnData.get(widthColumnData.size());
		}
		
		public double getHeight() {
			return heightRowData.get(heightRowData.size());
		}
	}
}