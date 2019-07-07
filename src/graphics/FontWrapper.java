package graphics;

public abstract class FontWrapper<FontType> {

	private static final double P1 = 15;
	private static final double P2 = 30;
	
	private String fontName;
	private double fontHeightSlope;
	private double fontHeightYIntercept;
	
	
	public FontWrapper(String fontName) {
		this.fontName = fontName;
		
		FontType font = getFont(fontName, P1);
		double s1 = getHeight(font);
		font = getFont(fontName, P2);
		double s2 =  getHeight(font);
		
		fontHeightSlope = (P2 - P1) / (s2 - s1);
		fontHeightYIntercept = P1 - fontHeightSlope * s1;
	}
	
	protected abstract FontType getFont(String fontName, double size);
	protected abstract double getHeight(FontType fontType);
	
	public abstract double getWidth(FontType fontType, String text);
	
	public FontType getFontWithHeight(double height) {
		return getFont(fontName, fontHeightSlope * height + fontHeightYIntercept);
	}
}
