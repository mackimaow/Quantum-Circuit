package graphicsWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;

import graphicsWrapper.AxisBound.GridBound;
import graphicsWrapper.AxisBound.RimBound;
import utils.customCollections.immutableLists.ImmutableTree;

class GraphicsDraw<ImageType, FontType, ColorType> extends Graphics<ImageType, FontType, ColorType> {
	private final GraphicalDrawTools<ImageType, FontType, ColorType> drawTools;
	private ImmutableTree<FocusData> focusContainerTree;
	private final Iterator<ImmutableTree<FocusData>> focusDataIterator;
	private LinkedList<Double[]> boundsStack = new LinkedList<>();
	
	public static <ImageType, FontType, ColorType> void graphicsDraw(double startX, double startY, double scale, CompiledGraphics<ImageType, FontType, ColorType> compiledGraphics, GraphicalDrawTools<ImageType, FontType, ColorType> drawTools) {
		GraphicsDraw<ImageType, FontType, ColorType> graphicsDraw = new GraphicsDraw<>(startX, startY, scale, drawTools, compiledGraphics.getFocusData());
		compiledGraphics.bluePrint.onDraw(graphicsDraw, compiledGraphics.userArgs);
	}
	
	private GraphicsDraw(double startX, double startY, double scale, GraphicalDrawTools<ImageType, FontType, ColorType> gc, ImmutableTree<FocusData> focusDataTree) {
		super(scale);
		this.drawTools = gc;
		this.focusContainerTree = focusDataTree;
		this.focusDataIterator = focusContainerTree.getNodeIterator();
		FocusData first = focusDataIterator.next().getElement();
		Double[] bounds = {startX, startY, startX + first.getWidth(), startY + first.getHeight()};
		boundsStack.push(bounds);
	}
	
	
	@Override
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, InternalAxisBound internalWidthBound, InternalAxisBound internalHeightBound) {
		focusContainerTree = focusDataIterator.next();
		
		boundsStack.push(new Double[4]);
		changeInAxisFocus(0, horizontalBounds, getHorizontalLayout());
		changeInAxisFocus(1, verticalBounds, getVerticalLayout());
	}
	
	
	private void changeInAxisFocus(int boundsIndexOffset, AxisBound axisBound, byte layout) {
		FocusData fd = focusContainerTree.getElement();
		Double[] nextBounds = boundsStack.getFirst();
		Double[] parentBounds = boundsStack.get(axisBound.focusGroupAbove + 1);
		
		FocusData pfd = focusContainerTree.getParent(axisBound.focusGroupAbove).getElement();
		
		ArrayList<Double> parentGridData, childGridData;
		
		if(boundsIndexOffset == 0) {
			parentGridData = pfd.widthColumnData;
			childGridData = fd.widthColumnData;
		} else {
			parentGridData = pfd.heightRowData;
			childGridData = fd.heightRowData;
		}		
		
		if(axisBound instanceof RimBound) {
			RimBound rimBound = (RimBound) axisBound;
			double parentLength = parentGridData.get(parentGridData.size() - 1);
			double childLength = childGridData.get(childGridData.size() - 1);
			
			if(layout == CENTER_ALIGN) {
				if(rimBound.stretchLow) {
					nextBounds[boundsIndexOffset] = parentBounds[boundsIndexOffset] + rimBound.lowMargin;
					nextBounds[boundsIndexOffset + 2] = nextBounds[boundsIndexOffset] + childLength;
				} else if (rimBound.stretchHigh) {
					nextBounds[boundsIndexOffset + 2] = parentBounds[boundsIndexOffset + 2] - rimBound.highMargin;
					nextBounds[boundsIndexOffset] = nextBounds[boundsIndexOffset] - childLength;
				} else {
					double totalChildSpace = childLength + rimBound.lowMargin + rimBound.highMargin;
					nextBounds[boundsIndexOffset] = parentBounds[boundsIndexOffset] + (parentLength - totalChildSpace) / 2d + rimBound.lowMargin;
					nextBounds[boundsIndexOffset + 2] = nextBounds[boundsIndexOffset] + childLength;
				}
			} else if (layout == LOW_ALIGN) {
				nextBounds[boundsIndexOffset] = parentBounds[boundsIndexOffset] + rimBound.lowMargin;
				nextBounds[boundsIndexOffset + 2] = nextBounds[boundsIndexOffset] + childLength;
			} else if(layout == HIGH_ALIGN) {
				nextBounds[boundsIndexOffset + 2] = parentBounds[boundsIndexOffset + 2] - rimBound.highMargin;
				nextBounds[boundsIndexOffset] = nextBounds[boundsIndexOffset] - childLength;
			}
		} else {
			GridBound gridBound = (GridBound) axisBound;
			nextBounds[boundsIndexOffset + 2] = parentBounds[boundsIndexOffset] + parentGridData.get(gridBound.highGrid - 1);
			double lowTranslate = gridBound.lowGrid == 0? 0 : parentGridData.get(gridBound.lowGrid - 1);
			nextBounds[boundsIndexOffset] = parentBounds[boundsIndexOffset] + lowTranslate;
		}
	}

	@Override
	public void escapeFocus() {
		focusContainerTree = focusContainerTree.getParent();
		boundsStack.pop();
	}

	@Override
	protected GraphicalDrawTools<ImageType, FontType, ColorType> getGraphicalDrawTools() {
		return drawTools;
	}

	
	@Override
	protected double[] checkAndTransformBounds(double x, double y, double width, double height, double ... otherBounds) {
		Double[] currentBounds = boundsStack.getFirst();
		double[] translateBounds = new double[4 + otherBounds.length];
		translateAxis(0, currentBounds, translateBounds, getHorizontalLayout(), x, width, scale);
		translateAxis(1, currentBounds, translateBounds, getVerticalLayout(), y, height, scale);
		for(int i = 0; i < otherBounds.length; i++)
			translateBounds[i + 4] = otherBounds[i] * scale;
		return translateBounds;
	}
	private static void translateAxis(int boundsIndexOffset, Double[] currentBounds, double[] translateBounds, byte layout, double translate, double length, double scale) {
		double parentLength = currentBounds[boundsIndexOffset + 2] - currentBounds[boundsIndexOffset]; 
		if(length < 0)
			length = parentLength * (-length);
		if(layout == CENTER_ALIGN)
			translateBounds[boundsIndexOffset] = (int) Math.round( (translate + currentBounds[boundsIndexOffset] + (parentLength - length) / 2d) * scale );
		else if (layout == LOW_ALIGN)
			translateBounds[boundsIndexOffset] = (int) Math.round( (translate + currentBounds[boundsIndexOffset]) * scale );
		else if(layout == HIGH_ALIGN)
			translateBounds[boundsIndexOffset] = (int) Math.round( (currentBounds[boundsIndexOffset + 2] - translate - length) * scale );
		translateBounds[boundsIndexOffset + 2] = (int) Math.round( (length) * scale );
	}

	@Override
	public void setBoundsManaged(boolean managed) {}
	
	
	
	
}