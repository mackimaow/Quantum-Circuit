package graphicsWrapper;

import utils.customCollections.Tree;
import utils.customCollections.immutableLists.ImmutableTree;

public class CompiledGraphics<ImageType, FontType, ColorType> {
	private final Tree<FocusData> focusData;
	final GraphicalBluePrint<ImageType, FontType, ColorType> bluePrint;
	final Object[] userArgs;
	
	CompiledGraphics(Tree<FocusData> focusData, GraphicalBluePrint<ImageType, FontType, ColorType> bluePrint, Object[] userArgs) {
		this.focusData = focusData;
		this.bluePrint = bluePrint;
		this.userArgs =  userArgs;
	}
	
	public ImmutableTree<FocusData> getFocusData() {
		return new ImmutableTree<>(focusData);
	}
}
