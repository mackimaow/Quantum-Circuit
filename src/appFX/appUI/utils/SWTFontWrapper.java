package appFX.appUI.utils;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

import graphicsWrapper.FontWrapper;

public class SWTFontWrapper extends FontWrapper<Font> {
	
	public SWTFontWrapper(String fontName) {
		super(fontName);
	}
	
	public SWTFontWrapper(Font font) {
		super(font.getFontName());
	}

	@Override
	protected Font getFont(String fontName, double size) {
		return new Font(fontName, Font.PLAIN, (int) Math.round(size));
	}

	@Override
	protected double getHeight(Font font) {
		FontMetrics fm = getFontMetrics(font);
		return fm.getHeight();
	}

	@Override
	protected double[] getAscentDescent(Font font, double height) {
		FontMetrics fm = getFontMetrics(font);
		return new double[] {fm.getAscent(), fm.getDescent()};
	}

	@Override
	public double getWidth(Font font, String text) {
		FontMetrics fm = getFontMetrics(font);
		return fm.stringWidth(text);
	}
	
	public static FontMetrics getFontMetrics(Font font) {
		BufferedImage bimg = new BufferedImage(1, 1, BufferedImage.TYPE_BYTE_BINARY);
		Graphics g = bimg.getGraphics();
		return g.getFontMetrics(font);
	}
	
}
