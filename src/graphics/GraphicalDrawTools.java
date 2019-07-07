package graphics;

public interface GraphicalDrawTools<ImageType, FontType> {
	public default void setFont(FontType font) {
		throw new UnsupportedOperationException("setFont");
	}
	public default void drawRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException("drawRect");
	}
	
	public default void fillRect(int x, int y, int width, int height) {
		throw new UnsupportedOperationException("fillRect");
	}
	
	public default void drawOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException("drawOval");
	}
	
	public default void fillOval(int x, int y, int width, int height) {
		throw new UnsupportedOperationException("fillOval");
	}
	
	public default void drawImage(ImageType image, int x, int y) {
		throw new UnsupportedOperationException("drawImage");
	}
	
	public default void drawText(String text, int x, int y) {
		throw new UnsupportedOperationException("drawText");
	}
	
}
