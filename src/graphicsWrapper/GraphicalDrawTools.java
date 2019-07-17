package graphicsWrapper;

import org.scilab.forge.jlatexmath.TeXIcon;

public interface GraphicalDrawTools<ImageType, FontType, ColorType> {
	
	public void setLineWidth(double lineWidth);
	public void drawLine(double x, double y, double width, double height, boolean handedness);
	public void drawArc(double x, double y, double width, double height, double startAngle, double endAngle);
	public void setFont(FontType font);
	public ColorType getColor();
	public void setColor(ColorType color);
	public int[] toRGBA(ColorType color);
	public void drawRect(double x, double y, double width, double height);
	public void fillRect(double x, double y, double width, double height);
	public void drawOval(double x, double y, double width, double height);
	public void fillOval(double x, double y, double width, double height);
	public void drawImage(ImageType image, double x, double y, double height, double width);
	public void drawText(String text, double ascentLineHeight, double decendLineHeight, double x, double y);
	public void drawLatex(TeXIcon icon, double x, double y);
}
