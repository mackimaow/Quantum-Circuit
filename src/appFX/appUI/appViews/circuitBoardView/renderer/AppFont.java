package appFX.appUI.appViews.circuitBoardView.renderer;

import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class AppFont {
	private static final double P1 = 15;
	private static final double P2 = 30;
	
	private String fontName;
	private double fontHeightSlope;
	private double fontHeightYIntercept;
	
	
	public AppFont(String fontName) {
		this.fontName = fontName;
		Font font = new Font(fontName, P1);
		
		Text t = new Text("");
		t.setFont(font);
		double s1 = t.getBoundsInLocal().getHeight();
		
		font = new Font(fontName, P2);
		t.setFont(font);
		double s2 = t.getBoundsInLocal().getHeight();
		fontHeightSlope = (P2 - P1) / (s2 - s1);
		fontHeightYIntercept = P1 - fontHeightSlope * s1;
	}
	
	public Font getFontWithHeight(double height) {
		return new Font(fontName, fontHeightSlope * height + fontHeightYIntercept);
	}
}
