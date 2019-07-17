package graphicsWrapper;

public interface CustomGraphicFunction<ImageType, FontType, ColorType> {
	public void perform(GraphicalDrawTools<ImageType, FontType, ColorType> gdt);
}
