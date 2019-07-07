package graphics;

public class FocusBounds {
	AxisBound horizontalBounds, verticalBounds;
	
	FocusBounds(AxisBound horizontalBounds, AxisBound verticalBounds) {
		this.horizontalBounds = horizontalBounds;
		this.verticalBounds = verticalBounds;
	}
	
	abstract class AxisBound {}
	
	public final class RimBound extends AxisBound {
		final int lowMargin;
		final int highMargin;
		final boolean stretchLow, stretchHigh;
		
		public RimBound(int lowMargin, int highMargin, boolean stretchLow, boolean stretchHigh) {
			this.lowMargin = lowMargin;
			this.highMargin = highMargin;
			this.stretchLow = stretchLow;
			this.stretchHigh = stretchHigh;
		}
	}
	
	public final class GridBound extends AxisBound {
		final int lowGrid;
		final int highGrid;
		
		public GridBound(int lowGrid, int highGrid) {
			this.lowGrid = lowGrid;
			this.highGrid = highGrid;
		}
	}
	
}
