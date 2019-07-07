package graphics;

import graphics.GraphicsCalculator.FocusData;
import utils.customCollections.ImmutableTree;
import utils.customCollections.Tree;

public class CompiledGraphics<ImageType, FontType> {
	private final Tree<FocusData> focusData;
	final GraphicalBluePrint<ImageType, FontType> bluePrint;
	
	CompiledGraphics(Tree<FocusData> focusData, GraphicalBluePrint<ImageType, FontType> bluePrint) {
		this.focusData = focusData;
		this.bluePrint = bluePrint;
	}
	
	public ImmutableTree<FocusData> getFocusData() {
		return new ImmutableTree<>(focusData);
	}
}
