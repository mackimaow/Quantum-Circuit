package graphicsWrapper;

import mathLib.LinearEquation;

public abstract class FontWrapper<FontType> {

	private static final double P1 = 15;
	private static final double P2 = 30;
	
	private String fontName;
	private final LinearEquation fontHeightEquation;
	private final double fontAscentHeightRatio;
	private final double fontDescentHeightRatio;
	
	public FontWrapper(String fontName) {
		this.fontName = fontName;
		
		FontType font = getFont(fontName, P1);
		double s1 = getHeight(font);
		font = getFont(fontName, P2);
		double s2 =  getHeight(font);
		
		double[] ascentDescent = getAscentDescent(font, s2);
		fontAscentHeightRatio = ascentDescent[0] / s2;
		fontDescentHeightRatio = ascentDescent[1] / s2;
		fontHeightEquation = new LinearEquation(P1, s1, P2, s2);
	}
	
	protected abstract FontType getFont(String fontName, double size);
	protected abstract double getHeight(FontType fontType);
	protected abstract double[] getAscentDescent(FontType fontType, double height);
	
	public abstract double getWidth(FontType fontType, String text);
	
	public double getAscentLineHeight(double height) {
		return height * fontAscentHeightRatio;
	}
	
	public double getDescentLineHeight(double height) {
		return height * fontDescentHeightRatio;
	}
	
	public FontType getFontWithHeight(double height) {
		return getFont(fontName, fontHeightEquation.getY(height));
	}
}
