package appFX.appUI.appViews.circuitBoardView.renderer;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXIcon;

import graphicsWrapper.GraphicalDrawTools;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.text.Font;

public class CustomFXGraphics implements GraphicalDrawTools<Image, Font, Color> {
	public static final FXFontWrapper DEFAULT = new FXFontWrapper(Font.getDefault());
	
	private GraphicsContext gc;
	
	public CustomFXGraphics(GraphicsContext gc) {
		this.gc = gc;
	}
	
	public void setLineDashes(double number, double offset) {
		gc.setLineDashes(number);
		gc.setLineDashOffset(offset);
	}
	
	public void setLineCap (StrokeLineCap slg) {
		gc.setLineCap(slg);
	}
	
	public void setLineJoin(StrokeLineJoin slj) {
		gc.setLineJoin(slj);
	}
	
	@Override
	public void setLineWidth(double lineWidth) {
		gc.setLineWidth(lineWidth);
	}
	
	@Override
	public void drawLine(double x, double y, double width, double height, boolean handedness) {
		if(handedness)
			gc.strokeLine(x, y, x + width, y + height);
		else
			gc.strokeLine(x, y + height, x + width, y);
	}
	
	@Override
	public void drawArc(double x, double y, double width, double height, double startAngle, double endAngle) {
		gc.strokeArc(x, y, width, height, startAngle, endAngle - startAngle, ArcType.OPEN);	
	}
	
	@Override
	public void setColor(Color color) {
		gc.setFill(color);
		gc.setStroke(color);
	}
	
	@Override
	public void drawImage(Image image, double x, double y, double width, double height) {
		gc.drawImage(image, x, y, width, height);
	}
	
	@Override
	public void fillOval(double x, double y, double width, double height) {
		gc.fillOval(x, y, width, height);
	}
	
	@Override
	public void drawOval(double x, double y, double width, double height) {
		gc.strokeOval(x, y, width, height);
	}
	
	@Override
	public void drawRect(double x, double y, double width, double height) {
		gc.strokeRect(x, y, width, height);
	}
	
	@Override
	public void fillRect(double x, double y, double width, double height) {
		gc.fillRect(x, y, width, height);
	}
	
	@Override
	public void drawText(String text, double ascentLine, double descentLine, double x, double y) {
		gc.fillText(text, x, y + ascentLine);
	}
	
	@Override
	public void setFont(Font font) {
		gc.setFont(font);
	}

	@Override
	public Color getColor() {
		return (Color) gc.getFill();
	}

	@Override
	public int[] toRGBA(Color color) {
		int[] rgba = new int[4];
		rgba[0] = (int) Math.round(color.getRed() * 255);
		rgba[1] = (int) Math.round(color.getBlue() * 255);
		rgba[2] = (int) Math.round(color.getGreen() * 255);
		rgba[3] = (int) Math.round(color.getOpacity() * 255);
		return rgba;
	}

	@Override
	public void drawLatex(TeXIcon icon, double x, double y) {
		BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();
        JLabel label = new JLabel();
        icon.paintIcon(label, g2d, 0, 0);
        Image image = SwingFXUtils.toFXImage(bimg, null);
        drawImage(image, x, y, bimg.getWidth(), bimg.getHeight());
	}
}
