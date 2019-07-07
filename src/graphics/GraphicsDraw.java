package graphics;

import java.util.ArrayList;
import java.util.Iterator;

import graphics.FocusBounds.AxisBound;
import graphics.FocusBounds.GridBound;
import graphics.FocusBounds.RimBound;
import graphics.GraphicsCalculator.FocusData;
import utils.customCollections.Stack;

class GraphicsDraw<ImageType, FontType> extends Graphics<ImageType, FontType> {
	private final GraphicalDrawTools<ImageType, FontType> drawTools;
	private final Iterator<FocusData> focusDataIterator;
	private Stack<FocusData> focusDataStack = new Stack<>();
	private double[] bounds;
	
	public static <ImageType, FontType> void graphicsDraw(CompiledGraphics<ImageType, FontType> compiledGraphics, GraphicalDrawTools<ImageType, FontType> drawTools) {
		GraphicsDraw<ImageType, FontType> graphicsDraw = new GraphicsDraw<>(drawTools, compiledGraphics.getFocusData().iterator());
		compiledGraphics.bluePrint.onDraw(graphicsDraw);
	}
	
	private GraphicsDraw(GraphicalDrawTools<ImageType, FontType> gc, Iterator<FocusData> focusData) {
		this.drawTools = gc;
		this.focusDataIterator = focusData;
		focusDataStack.push(focusData.next());
	}
	
	public FocusData getCurrent() {
		return focusDataStack.peak();
	}
	
	
	
	
	@Override
	public void changeInFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		FocusData parentData = getCurrent();
		FocusData childData = focusDataIterator.next();
		focusDataStack.push(childData);
		changeInAxisFocus(0, horizontalBounds, getHorizontalLayout(), parentData.widthColumnData, childData.widthColumnData);
		changeInAxisFocus(1, verticalBounds, getVerticalLayout(), parentData.heightRowData, childData.heightRowData);
	}
	
	private void changeInAxisFocus(int boundsIndexOffset, AxisBound axisBound, int layout, ArrayList<Double> parentGridData, ArrayList<Double> childGridData) {
		if(axisBound instanceof RimBound) {
			RimBound rimBound = (RimBound) axisBound;
			
			
		} else {
			GridBound gridBound = (GridBound) axisBound;
			bounds[boundsIndexOffset + 2] = bounds[boundsIndexOffset] + parentGridData.get(gridBound.highGrid - 1);
			double lowTranslate = gridBound.lowGrid == 0? 0 : parentGridData.get(gridBound.lowGrid - 1);
			bounds[boundsIndexOffset] += lowTranslate;
			
		}
	}

	@Override
	public void changeOutFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		
	}

	@Override
	protected GraphicalDrawTools<ImageType, FontType> getGraphicalDrawTools() {
		return drawTools;
	}

	@Override
	protected int[] checkAndTranslateBounds(int x, int y, int width, int height) {
		return null;
	}
}