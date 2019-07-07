package graphics;

import graphics.FocusBounds.AxisBound;
import utils.customCollections.Stack;

public abstract class Graphics<ImageType, FontType> {
	public static final byte LEFT_ALIGN 	= -1;
	public static final byte TOP_ALIGN 		= -1;
	public static final byte RIGHT_ALIGN 	=  1;
	public static final byte BOTTOM_RIGHT 	=  1;
	public static final byte CENTER_ALIGN 	=  0;
	
	
	Stack<FocusBounds> localFocus = new Stack<>();
	private FontWrapper<FontType> customFont;
	private FontType font;
	private double fontHeight;
	private int horizontalLayout = LEFT_ALIGN;
	private int verticalLayout = TOP_ALIGN;
	
	public static <ImageType, FontType> CompiledGraphics<ImageType, FontType> compileGraphicalBluePrint(GraphicalBluePrint<ImageType, FontType> bluePrint) {
		return GraphicsCalculator.compileGraphics(bluePrint);
	}
	
	Graphics() {};
	
	public void setHorizontalLayout(int align) {
		horizontalLayout = align;
	}
	
	public void setVerticalyLayout(int align) {
		verticalLayout = align;
	}
	
	public int getHorizontalLayout() {
		return horizontalLayout;
	}
	
	public int getVerticalLayout() {
		return verticalLayout;
	}
	
	public void setAppFont(FontWrapper<FontType> appFont, double fontHeight) {
		this.customFont = appFont;
		this.fontHeight = fontHeight;
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		this.font = customFont.getFontWithHeight(fontHeight);
		if(gc != null)
			gc.setFont(this.font);
	}
	
	public FontWrapper<FontType> getAppFont() {
		return customFont;
	}
	
	public void drawRect(int x, int y, int width, int height) {
		int[] newBounds = checkAndTranslateBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawRect(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void fillRect(int x, int y, int width, int height) {
		int[] newBounds = checkAndTranslateBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.fillRect(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void drawOval(int x, int y, int width, int height) {
		int[] newBounds = checkAndTranslateBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawOval(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void fillOval(int x, int y, int width, int height) {
		int[] newBounds = checkAndTranslateBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.fillOval(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void drawImage(ImageWrapper<ImageType> imageWrapper, int x, int y) {
		int[] newBounds = checkAndTranslateBounds(x, y, (int) imageWrapper.getWidth(), (int) imageWrapper.getHeight());
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawImage(imageWrapper.image, newBounds[0], newBounds[1]);
	}
	
	public void drawText(String text, int x, int y) {
		double width = customFont.getWidth(font, text);
		int[] newBounds = checkAndTranslateBounds(x, y, (int) width, (int) fontHeight);
		GraphicalDrawTools<ImageType, FontType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawText(text, newBounds[0], newBounds[1]);
	}
	
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		FocusBounds focusBounds = new FocusBounds(horizontalBounds, verticalBounds);
		localFocus.add(focusBounds);
		changeInFocus(horizontalBounds, verticalBounds);
	}
	
	public void escapeFocus() {
		FocusBounds focusBounds = localFocus.pop();
		changeOutFocus(focusBounds.horizontalBounds, focusBounds.verticalBounds);
	}
	
	protected abstract GraphicalDrawTools<ImageType, FontType> getGraphicalDrawTools();
	protected abstract int[] checkAndTranslateBounds(int x, int y, int width, int height);
	protected abstract void changeInFocus(AxisBound horizontalBounds, AxisBound verticalBounds);
	protected abstract void changeOutFocus(AxisBound horizontalBounds, AxisBound verticalBounds);
}
