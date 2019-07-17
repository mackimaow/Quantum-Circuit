package appFX.appUI.appViews.circuitBoardView.renderer;

import graphicsWrapper.FontWrapper;
import javafx.geometry.Bounds;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class FXFontWrapper extends FontWrapper<Font> {
	
	public FXFontWrapper(Font font) {
		super(font.getName());
	}
	
	public FXFontWrapper(String fontName) {
		super(fontName);
	}

	@Override
	protected Font getFont(String fontName, double size) {
		Font font = new Font(fontName, size);
		return font;
	}

	@Override
	protected double getHeight(Font fontType) {
		Text temp = new Text();
		temp.setFont(fontType);
		return temp.getLayoutBounds().getHeight();
	}

	@Override
	public double getWidth(Font fontType, String text) {
		Text temp = new Text(text);
		temp.setFont(fontType);
		return temp.getLayoutBounds().getWidth();
	}

	@Override
	protected double[] getAscentDescent(Font fontType, double height) {
		Text t = new Text("");
		t.setFont(fontType);
		Bounds b = t.getLayoutBounds();
		return new double[] {-b.getMinY(), b.getMaxY()};
	}

}
