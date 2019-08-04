package graphicsWrapper;
import java.util.ArrayList;
import java.util.LinkedList;

import graphicsWrapper.AxisBound.GridBound;
import graphicsWrapper.AxisBound.RimBound;
import utils.customCollections.Tree;

class GraphicsCalculator<ImageType, FontType, ColorType> extends Graphics<ImageType, FontType, ColorType> {
	
	private Tree<RawFocusData> focusContainer;
	private boolean managed = true;
	
	public static <ImageType, FontType, ColorType> CompiledGraphics<ImageType, FontType, ColorType> compileGraphics(GraphicalBluePrint<ImageType, FontType, ColorType> bluePrint, Object ... userArgs) {
		GraphicsCalculator<ImageType, FontType, ColorType> calculator = new GraphicsCalculator<>();
		bluePrint.onDraw(calculator, userArgs);
		
		Tree<RawFocusData> top = calculator.focusContainer;
		RawFocusData topElement = top.getElement();
		topElement.setCummulativeData();
		adjustForChildRimSpace(topElement.widthData, 0d);
		adjustForChildRimSpace(topElement.heightData, 0d);
		
		FocusData topFocusData = new FocusData(topElement.widthData.axisLengths, topElement.heightData.axisLengths);
		Tree<FocusData> focusDataTree = new Tree<FocusData>(topFocusData);

		LinkedList<Tree<RawFocusData>> treeChildren = top.getChildren();
		for(Tree<RawFocusData> childTree : treeChildren)
			focusDataTree.add(readjustBounds(childTree));
		
		return new CompiledGraphics<>(focusDataTree, bluePrint, userArgs);
	}
	
	private static void adjustForChildRimSpace(AxisData axisData, double amtToAdd) {
		if(axisData.isEmpty()) {
			axisData.add(axisData.childRimSpace + amtToAdd);
		} else {
			if(axisData.childRimSpace > axisData.getGridLength())
				amtToAdd += axisData.childRimSpace - axisData.getGridLength();
			if(amtToAdd  > 0d) {
				double amtToAddToEach = amtToAdd / axisData.size();
				axisData.expandAllLengths(amtToAddToEach);
			}
		}
	}
	
	private GraphicsCalculator() {
		super(1d);
		focusContainer = new Tree<>(new RawFocusData());
	}
	
	private static Tree<FocusData> readjustBounds(Tree<RawFocusData> childBounds) {
		RawFocusData child = childBounds.getElement();
		readjustAxisBound(true, childBounds);
		readjustAxisBound(false, childBounds);
		
		FocusData fd = new FocusData(child.widthData.axisLengths, child.heightData.axisLengths);
		Tree<FocusData> readjustedData = new Tree<>(fd);
		
		for(Tree<RawFocusData> childTree : childBounds.getChildren())
			readjustedData.add(readjustBounds(childTree));
		return readjustedData;
	}
	
	private static void readjustAxisBound(boolean isWidths, Tree<RawFocusData> childFocusDataTree) {
		RawFocusData rawFocusData = childFocusDataTree.getElement();
		
		AxisBound axisBound;
		AxisData childAxisData, parentAxisData;
		
		if(isWidths) {
			childAxisData = rawFocusData.widthData;
			axisBound = childAxisData.axisBound;
			RawFocusData parentData = childFocusDataTree.getParent(axisBound.focusGroupAbove).getElement();
			parentAxisData = parentData.widthData;
		} else {
			childAxisData = rawFocusData.heightData;
			axisBound = childAxisData.axisBound;
			RawFocusData parentData = childFocusDataTree.getParent(axisBound.focusGroupAbove).getElement();
			parentAxisData = parentData.heightData;
		}
		
		double unadjustedLength = childAxisData.getTotalLength();
		double adjustedLength = unadjustedLength;
		
		if(axisBound instanceof RimBound) {
			RimBound rimBound = (RimBound) axisBound;
			
			boolean stretchLow = rimBound.stretchLow;
			boolean stretchHigh = rimBound.stretchHigh;
			
			double lowMargin = rimBound.lowMargin;
			double highMargin = rimBound.highMargin;
			
			double parentLength = parentAxisData.getTotalLength();
			
			byte layout = childAxisData.layout;
			if(layout == CENTER_ALIGN) {
				double totalChildSpace = lowMargin + highMargin + unadjustedLength;
				double totalUnfilledChildSpace = parentLength - totalChildSpace;
				if(stretchLow) adjustedLength += totalUnfilledChildSpace / 2d;
				if(stretchHigh) adjustedLength += totalUnfilledChildSpace / 2d;
			} else if (layout == LOW_ALIGN && stretchHigh || layout == HIGH_ALIGN && stretchLow) {
				adjustedLength = parentLength - lowMargin - highMargin;
			}
			
		} else {
			GridBound gridBound = (GridBound) axisBound;
			
			int lowerBound = gridBound.lowGrid;
			int upperbound = gridBound.highGrid;
			
			adjustedLength = parentAxisData.getDistanceFromRange(lowerBound, upperbound);
		}
		
		adjustForChildRimSpace(childAxisData, adjustedLength - unadjustedLength);
	}
	

	@Override
	public void setBoundsManaged(boolean managed) {
		this.managed = managed;	
	}
	
	@Override
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, InternalAxisBound internalWidthBound, InternalAxisBound internalHeightBound) {
		AxisData widthData, heightData;
		
		if(internalWidthBound.uniformGridLengths)
			widthData = new UniformAxisData(horizontalBounds, getHorizontalLayout(), internalWidthBound.minimumLength);
		else
			widthData = new AxisData(horizontalBounds, getHorizontalLayout(), internalWidthBound.minimumLength);
		if(internalHeightBound.uniformGridLengths)
			heightData = new UniformAxisData(verticalBounds, getVerticalLayout(), internalHeightBound.minimumLength);
		else
			heightData = new AxisData(verticalBounds, getVerticalLayout(), internalHeightBound.minimumLength);
		Tree<RawFocusData> nextContainer = new Tree<>(new RawFocusData(widthData, heightData));
		focusContainer.add(nextContainer);
		focusContainer = nextContainer;
	}

	@Override
	public void escapeFocus() {
		focusContainer.getElement().setCummulativeData();
		updateLengthsAxisParent(true, focusContainer);
		updateLengthsAxisParent(false, focusContainer);
		focusContainer = focusContainer.getParent();
	}
	
	
	private static void updateLengthsAxisParent(boolean isWidths, Tree<RawFocusData> rawFocusDataTree) {
		RawFocusData rawFocusData = rawFocusDataTree.getElement();
		

		AxisBound axisBound;
		AxisData axisData, parentAxisData;
		
		if(isWidths) {
			axisData = rawFocusData.widthData;
			axisBound = axisData.axisBound;
			RawFocusData parentData = rawFocusDataTree.getParent(axisBound.focusGroupAbove).getElement();
			parentAxisData = parentData.widthData;
		} else {
			axisData = rawFocusData.heightData;
			axisBound = axisData.axisBound;
			RawFocusData parentData = rawFocusDataTree.getParent(axisBound.focusGroupAbove).getElement();
			parentAxisData = parentData.heightData;
		}
		
		double childLength = axisData.getTotalLength();
		
		if(axisBound instanceof RimBound) {
			RimBound rimBound = (RimBound) axisBound;

			double 	totalChildLength = rimBound.lowMargin + childLength + rimBound.highMargin;
			
			Double currentLength = parentAxisData.childRimSpace;
			if(totalChildLength > currentLength)
				parentAxisData.childRimSpace = totalChildLength;
		} else {
			GridBound gridBound = (GridBound) axisBound;
			
			int axisStartInclusize = gridBound.lowGrid;
			int axisEndExclusize = gridBound.highGrid;
			
			parentAxisData.allocateSpace(axisEndExclusize);
			
			double currentCumulativeLength = parentAxisData.getDistanceFromRange(axisStartInclusize, axisEndExclusize);
			
			if(childLength > currentCumulativeLength) {
				double addToEach = (childLength-currentCumulativeLength) / (axisEndExclusize - axisStartInclusize);
				parentAxisData.expandLengths(axisStartInclusize, axisEndExclusize, addToEach);
			}
		}
	}
	
	@Override
	protected GraphicalDrawTools<ImageType, FontType, ColorType> getGraphicalDrawTools() {
		return null;
	}

	private double calcMinLength(double position, double length, byte layout) {
		if(layout == CENTER_ALIGN)
			return length + 2d * Math.abs(position);
		return length + (position > 0d ? position : 0d);
	}
	
	
	@Override
	protected double[] checkAndTransformBounds(double x, double y, double width, double height, double ... otherBounds) {
		if(!managed) return null;
		if(width > 0) {
			double occupiedWidth = calcMinLength(x, width, getHorizontalLayout());
			if(getMinimumWidth() < occupiedWidth)
				setMinimumWidth(occupiedWidth);
		}
		if(height > 0)  {
			double occupiedHeight = calcMinLength(y, height, getVerticalLayout());
			if(getMinimumHeight() < occupiedHeight)
				setMinimumHeight(occupiedHeight);
		}
		return null;
	}

	private double getMinimumWidth() {
		return focusContainer.getElement().widthData.childRimSpace;
	}
	
	private void setMinimumWidth(double occupiedWidth) {
		focusContainer.getElement().widthData.childRimSpace = occupiedWidth;
	}

	private double getMinimumHeight() {
		return focusContainer.getElement().heightData.childRimSpace;
	}
	
	private void setMinimumHeight(double occupiedHeight) {
		focusContainer.getElement().heightData.childRimSpace = occupiedHeight;
	}
	
	private static class RawFocusData {
		AxisData widthData;
		AxisData heightData;
		
		RawFocusData() {
			this(new AxisData(null, CENTER_ALIGN, 0), new AxisData(null, CENTER_ALIGN, 0));
		}
		
		RawFocusData(AxisData widthData, AxisData heigthData) {
			this.widthData = widthData;
			this.heightData = heigthData;
		}
		
		void setCummulativeData() {
			widthData = new CummulativeAxisData(widthData);
			heightData = new CummulativeAxisData(heightData);
		}
	}
	
	private static class AxisData {
		final ArrayList<Double> axisLengths;
		final AxisBound axisBound;
		final byte layout;
		double childRimSpace;
		
		AxisData(AxisBound axisBound, byte layout, double childRimSpace) {
			this(new ArrayList<>(), axisBound, layout, childRimSpace);
		}
		
		AxisData(ArrayList<Double> axisLengths, AxisBound axisBound, byte layout, double childRimSpace) {
			this.axisLengths = axisLengths;
			this.axisBound = axisBound;
			this.layout = layout;
			this.childRimSpace = childRimSpace;
		}
		
		boolean isEmpty() {
			return axisLengths.isEmpty();
		}
		
		int size() {
			return axisLengths.size();
		}
		
		void expandAllLengths(double amtToEach) {
			for(int i = 0; i < axisLengths.size(); i++)
				axisLengths.set(i, axisLengths.get(i) + amtToEach);
		}
		
		void expandLengths(int firstIndexInclusive, int lastIndexExclusive, double amtToEach) {
			for(int i = firstIndexInclusive; i < lastIndexExclusive; i++)
				axisLengths.set(i, axisLengths.get(i) + amtToEach);
		}
		
		void allocateSpace(int axisEndExclusize) {
			if(axisEndExclusize > axisLengths.size())
				for(int i = axisLengths.size(); i < axisEndExclusize; i++)
					axisLengths.add(0d);
		}
		
		double getDistanceFromRange(int firstIndexInclusive, int lastIndexExclusive) {
			double sum = 0;
			for(int i = firstIndexInclusive; i < lastIndexExclusive; i++)
				sum += axisLengths.get(i);
			return sum;
		}
		
		double get(int index) {
			return axisLengths.get(index);
		}
		
		void add(double length) {
			axisLengths.add(length);
		}
		
		double getGridLength() {
			double sum = 0;
			for(int i = 0; i < size(); i++)
				sum += get(i);
			return sum;
		}
		
		double getTotalLength() {
			double sum = getGridLength();
			return sum > childRimSpace ? sum : childRimSpace;
		}
		
	}
	
	private static class UniformAxisData extends AxisData {
		int size;
		
		UniformAxisData(AxisBound axisBound, byte layout, double childRimSpace) {
			super(new ArrayList<>(), axisBound, layout, childRimSpace);
		}
		
		boolean isEmpty() {
			return size == 0;
		}
		
		int size() {
			return size;
		}
		
		void expandAllLengths(double amtToEach) {
			if(!isEmpty())
				axisLengths.set(0, axisLengths.get(0) + amtToEach);
		}
		
		void expandLengths(int firstIndexInclusive, int lastIndexExclusive, double amtToEach) {
			if(!isEmpty())
				axisLengths.set(0, axisLengths.get(0) + amtToEach);
		}
		
		void allocateSpace(int axisEndExclusize) {
			if(size == 0 && axisEndExclusize > 0)
				axisLengths.add(0d);
			size = axisEndExclusize;
		}
		
		double getDistanceFromRange(int firstIndexInclusive, int lastIndexExclusive) {
			return (lastIndexExclusive - firstIndexInclusive - 1) * axisLengths.get(0);
		}
		
		double get(int index) {
			return axisLengths.get(0);
		}
		
		void add(double length) {
			if(isEmpty())
				axisLengths.add(length);
			else if(axisLengths.get(0) < length)
				axisLengths.set(0, length);
			size++;
		}
		
		double getGridLength() {
			return axisLengths.get(0) * (size - 1);
		}
		
		double getTotalLength() {
			double sum = getGridLength();
			return sum > childRimSpace ? sum : childRimSpace;
		}
		
	}
	
	private static class CummulativeAxisData extends AxisData {
		
		CummulativeAxisData(AxisData axisData) {
			super(axisData.axisLengths, axisData.axisBound, axisData.layout, axisData.childRimSpace);
			if(axisData instanceof UniformAxisData) {
				if(!axisData.isEmpty()) {
					double singleLength = axisData.axisLengths.get(0);
					for(int i = 1; i < axisData.size(); i++)
						axisLengths.add(i, singleLength * (i + 1));
				}
			} else {
				double sum = 0d;
				for(int i = 0; i < axisLengths.size(); i++) {
					sum += axisLengths.get(i);
					axisLengths.set(i, sum);
				}
			}
		}
		
		@Override
		void expandAllLengths(double amt) {
			for(int i = 0; i < axisLengths.size(); i++)
				axisLengths.set(i, axisLengths.get(i) + (i+1) * amt);
		}
		
		@Override
		void allocateSpace(int axisEndExclusize) {
			int sizeBeforeAlloc = axisLengths.size();
			double currentWidth = sizeBeforeAlloc == 0? 0 : axisLengths.get(sizeBeforeAlloc - 1);
			if(axisEndExclusize > sizeBeforeAlloc)
				for(int i = sizeBeforeAlloc; i < axisEndExclusize; i++)
					axisLengths.add(currentWidth);
		}
		
		
		@Override
		double getDistanceFromRange(int firstIndexInclusive, int lastIndexExclusive) {
			if(firstIndexInclusive == 0)
				return axisLengths.get(lastIndexExclusive - 1);
			return axisLengths.get(lastIndexExclusive - 1) - axisLengths.get(firstIndexInclusive - 1);
		}
		
		@Override
		double get(int index) {
			if(index == 0)
				return axisLengths.get(0);
			else
				return axisLengths.get(index) - axisLengths.get(index - 1);
		}
		
		@Override
		void add(double length) {
			if(axisLengths.size() == 0)
				axisLengths.add(length);
			else
				axisLengths.add(axisLengths.get(axisLengths.size() - 1) + length);
		}
		
		
		@Override
		double getGridLength() {
			if(axisLengths.size() == 0) return 0d;
			return axisLengths.get(axisLengths.size() - 1);
		}
		
	}
	
}