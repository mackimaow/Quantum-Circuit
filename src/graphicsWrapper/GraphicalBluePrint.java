package graphicsWrapper;

public interface GraphicalBluePrint<ImageType, FontType, ColorType> {
	public void onDraw(Graphics<ImageType, FontType, ColorType> graphics, Object ... userArgs);
}
