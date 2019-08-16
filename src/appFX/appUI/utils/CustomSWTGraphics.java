package appFX.appUI.utils;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.image.BufferedImage;

import javax.swing.JLabel;

import org.scilab.forge.jlatexmath.TeXIcon;

import graphicsWrapper.FontWrapper;
import graphicsWrapper.GraphicalDrawTools;

public class CustomSWTGraphics implements GraphicalDrawTools<Image, Font, Color> {
	public static RenderPalette<Image, Font, Color> RENDER_PALETTE = new RenderPalette<Image, Font, Color>() {
		
		@Override
		public Color getWhite() {
			return Color.WHITE;
		}
		
		@Override
		public Color getRed() {
			return Color.RED;
		}
		
		@Override
		public FontWrapper<Font> getDefault() {
			return new SWTFontWrapper(new JLabel().getFont());
		}
		
		@Override
		public Color getColor(double red, double green, double blue, double alpha) {
			return new Color((float)red, (float)green, (float)blue, (float)alpha);
		}
		
		@Override
		public Color getBlack() {
			return Color.BLACK;
		}
	};
	
	private final Graphics2D graphics2D;
	
	public CustomSWTGraphics(Graphics2D graphics2D) {
		this.graphics2D = graphics2D;
	}
	
	private static int toInt(double value) {
		return (int) Math.round(value);
	}
	
	@Override
	public void setLineWidth(double lineWidth) {
		Stroke s = new BasicStroke(Math.round(lineWidth), BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND);
		graphics2D.setStroke(s);
	}

	@Override
	public void drawLine(double x, double y, double width, double height, boolean handedness) {
		if(handedness)
			graphics2D.drawLine(toInt(x), toInt(y), toInt(x + width), toInt(y + height));
		else
			graphics2D.drawLine(toInt(x), toInt(y + height), toInt(x + width), toInt(y));
	}

	@Override
	public void drawArc(double x, double y, double width, double height, double startAngle, double endAngle) {
		graphics2D.drawArc(toInt(x), toInt(y), toInt(width), toInt(height), toInt(startAngle), toInt(endAngle-startAngle));
	}

	@Override
	public void setFont(Font font) {
		graphics2D.setFont(font);
	}

	@Override
	public Color getColor() {
		return graphics2D.getColor();
	}

	@Override
	public void setColor(Color color) {
		graphics2D.setColor(color);
	}

	@Override
	public int[] toRGBA(Color color) {
		return new int[] {color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()};
	}

	@Override
	public void drawRect(double x, double y, double width, double height) {
		graphics2D.drawRect(toInt(x), toInt(y), toInt(width), toInt(height));
	}

	@Override
	public void fillRect(double x, double y, double width, double height) {
		graphics2D.fillRect(toInt(x), toInt(y), toInt(width), toInt(height));
	}

	@Override
	public void drawOval(double x, double y, double width, double height) {
		graphics2D.drawOval(toInt(x), toInt(y), toInt(width), toInt(height));
	}

	@Override
	public void fillOval(double x, double y, double width, double height) {
		graphics2D.fillOval(toInt(x), toInt(y), toInt(width), toInt(height));
	}

	@Override
	public void drawImage(Image image, double x, double y, double height, double width) {
		graphics2D.drawImage(image, toInt(x), toInt(y), toInt(width), toInt(height), null);
	}

	@Override
	public void drawText(String text, double ascentLineHeight, double decendLineHeight, double x, double y) {
		graphics2D.drawString(text, toInt(x), toInt(y + ascentLineHeight));
	}

	@Override
	public void drawLatex(TeXIcon icon, double x, double y) {
		BufferedImage bimg = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(), BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D g2d = (Graphics2D) bimg.getGraphics();
        JLabel label = new JLabel();
        icon.paintIcon(label, g2d, 0, 0);
        graphics2D.drawImage(bimg, toInt(x), toInt(y), bimg.getWidth(), bimg.getHeight(), null);
	}
	
}
