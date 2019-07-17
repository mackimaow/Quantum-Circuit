package graphicsWrapper;

public abstract class ImageWrapper<ImageType> {
	public final ImageType image;
	
	public ImageWrapper(ImageType image) {
		this.image = image;
	}
	
	public abstract double getWidth();
	public abstract double getHeight();
}
