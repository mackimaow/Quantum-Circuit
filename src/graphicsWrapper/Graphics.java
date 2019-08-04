package graphicsWrapper;

import java.awt.Color;

import org.scilab.forge.jlatexmath.TeXConstants;
import org.scilab.forge.jlatexmath.TeXFormula;
import org.scilab.forge.jlatexmath.TeXIcon;

public abstract class Graphics<ImageType, FontType, ColorType> {
	static final byte LOW_ALIGN = 1;
	static final byte HIGH_ALIGN = 3;
	public static final byte CENTER_ALIGN 	=  2;
	
	public static final byte LEFT_ALIGN 	= LOW_ALIGN;
	public static final byte TOP_ALIGN 		= LOW_ALIGN;
	public static final byte RIGHT_ALIGN 	= HIGH_ALIGN;
	public static final byte BOTTOM_ALIGN 	= HIGH_ALIGN;
	
	public static final byte FOCUS_WIDTH = -1;
	public static final byte FOCUS_HEIGHT = -1;
	
	
	private FontWrapper<FontType> fontWrapper;
	private FontType font;
	private double fontHeight;
	private byte horizontalLayout = LEFT_ALIGN;
	private byte verticalLayout = TOP_ALIGN;
	protected final double scale;
	
	public static <ImageType, FontType, ColorType> CompiledGraphics<ImageType, FontType, ColorType> compileGraphicalBluePrint(GraphicalBluePrint<ImageType, FontType, ColorType> bluePrint, Object ... userArgs) {
		return GraphicsCalculator.compileGraphics(bluePrint, userArgs);
	}
	
	public static <ImageType, FontType, ColorType> void graphicsDraw(double startX, double startY, double zoom, CompiledGraphics<ImageType, FontType, ColorType> compiledGraphics, GraphicalDrawTools<ImageType, FontType, ColorType> drawTools) {
		GraphicsDraw.graphicsDraw(startX, startY, zoom, compiledGraphics, drawTools);
	}
	
	public static <ImageType, FontType, ColorType> void graphicsDraw(CompiledGraphics<ImageType, FontType, ColorType> compiledGraphics, GraphicalDrawTools<ImageType, FontType, ColorType> drawTools) {
		graphicsDraw(0d, 0d, 1d, compiledGraphics, drawTools);
	}
	
	Graphics(double scale) {
		this.scale = scale;
		setLineWidth(1);
	};
	
	public static double getFocusWidth(double percentOfWidth) {
		return -1 * percentOfWidth;
	}
	
	public static double getFocusHeight(double percentOfHeight) {
		return -1 * percentOfHeight;
	}
	
	public void setLayout(byte horizontalLayout, byte verticalLayout) {
		this.horizontalLayout = horizontalLayout;
		this.verticalLayout = verticalLayout;
	}
	
	public void setHorizontalLayout(byte align) {
		horizontalLayout = align;
	}
	
	public void setVerticalyLayout(byte align) {
		verticalLayout = align;
	}
	
	public byte getHorizontalLayout() {
		return horizontalLayout;
	}
	
	public byte getVerticalLayout() {
		return verticalLayout;
	}
	
	public void customGraphicFunction(CustomGraphicFunction<ImageType, FontType, ColorType> function) {
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			function.perform(gc);
	}
	
	public void setColor(ColorType color) {
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.setColor(color);
	}	
	
	public void setFont(FontWrapper<FontType> fontWrapper, double fontHeight) {
		this.fontWrapper = fontWrapper;
		this.fontHeight = fontHeight * scale;
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		this.font = fontWrapper.getFontWithHeight(this.fontHeight);
		if(gc != null)
			gc.setFont(this.font);
	}
	
	public void setFontHeight(double fontHeight) {
		this.fontHeight = fontHeight * scale;
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		this.font = fontWrapper.getFontWithHeight(this.fontHeight);
		if(gc != null)
			gc.setFont(this.font);
	}
	
	public FontWrapper<FontType> getFontWrapper() {
		return fontWrapper;
	}
	
	public void setLineWidth(double lineWidth) {
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.setLineWidth(lineWidth * scale);
	}
	
	public void drawLine(double x, double y, double width, double height, boolean handedness) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawLine(newBounds[0], newBounds[1], newBounds[2], newBounds[3], handedness);
	}
	
	public void drawArc(double x, double y, double width, double height, double startAngle, double endAngle) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawArc(newBounds[0], newBounds[1], newBounds[2], newBounds[3], startAngle, endAngle);
	}
	
	public void drawRect(double x, double y, double width, double height) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawRect(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void fillRect(double x, double y, double width, double height) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.fillRect(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void drawOval(double x, double y, double width, double height) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawOval(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void fillOval(double x, double y, double width, double height) {
		double[] newBounds = checkAndTransformBounds(x, y, width, height);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.fillOval(newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void drawImage(ImageWrapper<ImageType> imageWrapper, double x, double y) {
		double[] newBounds = checkAndTransformBounds(x, y, imageWrapper.getWidth(), imageWrapper.getHeight());
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawImage(imageWrapper.image, newBounds[0], newBounds[1], newBounds[2], newBounds[3]);
	}
	
	public void drawText(String text, double x, double y) {
		double width = fontWrapper.getWidth(font, text);
		double[] newBounds = checkAndTransformBounds(x, y, width / scale, fontHeight / scale);
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null) {
			double ascentLine = fontWrapper.getAscentLineHeight(fontHeight);
			double descentLine = fontWrapper.getDescentLineHeight(fontHeight);
			gc.drawText(text, ascentLine, descentLine, newBounds[0], newBounds[1]);
		}
	}
	
	private Color getColor() {
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc == null) return Color.BLACK;
		int[] rgba = gc.toRGBA(gc.getColor());
		return new Color(rgba[0], rgba[1], rgba[2], rgba[3]);
	}
	
	public void drawLatex(String latexString, double x, double y) {
		TeXFormula tf = new TeXFormula(latexString);
		TeXIcon ti = tf.createTeXIcon(TeXConstants.STYLE_DISPLAY, Math.round(fontHeight), 0, getColor());
		double[] newBounds = checkAndTransformBounds(x, y, ti.getIconWidth() / scale, ti.getIconHeight() / scale);
		
		GraphicalDrawTools<ImageType, FontType, ColorType> gc = getGraphicalDrawTools();
		if(gc != null)
			gc.drawLatex(ti, newBounds[0], newBounds[1]);
	}
	
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, double minimumWidth, double minimumHeight) {
		setFocus(horizontalBounds, verticalBounds, new InternalAxisBound(minimumWidth, false), new InternalAxisBound(minimumHeight, false));
	}
	
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, boolean uniformGridWidths, boolean uniformGridHeights) {
		setFocus(horizontalBounds, verticalBounds, new InternalAxisBound(0, uniformGridWidths), new InternalAxisBound(0, uniformGridHeights));
	}
	
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, double minimumWidth, double minimumHeight, boolean uniformGridWidths, boolean uniformGridHeights) {
		setFocus(horizontalBounds, verticalBounds, new InternalAxisBound(minimumWidth, uniformGridWidths), new InternalAxisBound(minimumHeight, uniformGridHeights));
	}
	
	public void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds) {
		setFocus(horizontalBounds, verticalBounds, new InternalAxisBound(0, false), new InternalAxisBound(0, false));
	}
	
	public double resize(double d) {
		return d * scale;
	}
	
	public abstract void setBoundsManaged(boolean managed);
	public abstract void setFocus(AxisBound horizontalBounds, AxisBound verticalBounds, InternalAxisBound internalWidthBound, InternalAxisBound internalHeightBound);
	public abstract void escapeFocus();
	protected abstract GraphicalDrawTools<ImageType, FontType, ColorType> getGraphicalDrawTools();
	protected abstract double[] checkAndTransformBounds(double x, double y, double width, double height, double ... otherBounds);
}
