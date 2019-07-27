package appFX.appUI.appViews.circuitBoardView.renderer;

import graphicsWrapper.FontWrapper;

public interface RenderPalette<ImageType, FontType, ColorType> {
	
	public ColorType getBlack();
	public ColorType getRed();
	public ColorType getWhite();
	public FontWrapper<FontType> getDefault();
	public default ColorType getColor(int red, int green, int blue, int alpha) {
		return getColor(red / 255d, green / 255d, blue / 255d, alpha / 255d);
	}
	public ColorType getColor(double red, double green, double blue, double alpha);
	
}
