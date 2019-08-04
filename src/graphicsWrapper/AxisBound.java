package graphicsWrapper;

public class AxisBound {
	final int focusGroupAbove;
	
	private AxisBound(int focusGroupAbove) {
		this.focusGroupAbove = focusGroupAbove;
	}
	
	public static final class RimBound extends AxisBound {
		final double lowMargin;
		final double highMargin;
		final boolean stretchLow, stretchHigh;
		
		public RimBound(double lowMargin, double highMargin, boolean stretchLow, boolean stretchHigh) {
			this(lowMargin, highMargin, stretchLow, stretchHigh, 0);
		}
		
		public RimBound(double lowMargin, double highMargin, boolean stretchLow, boolean stretchHigh, int focusGroupAbove) {
			super(focusGroupAbove);
			this.lowMargin = lowMargin;
			this.highMargin = highMargin;
			this.stretchLow = stretchLow;
			this.stretchHigh = stretchHigh;
		}
	}
	
	public static final class GridBound extends AxisBound {
		final int lowGrid;
		final int highGrid;
		
		public GridBound(int lowGrid) {
			this(lowGrid, lowGrid + 1, 0);
		}
		
		public GridBound(int lowGridInclusive, int highGridExclusive, int focusGroupAbove) {
			super(focusGroupAbove);
			this.lowGrid = lowGridInclusive;
			this.highGrid = highGridExclusive;
		}
		
		public GridBound(int lowGridInclusive, int highGridExclusive) {
			this(lowGridInclusive, highGridExclusive, 0);
		}
	}
	
//	public static final class FractionalBound extends AxisBound {
//		final double fractionOfParent;
//		
//		public FractionalBound(double fractionOfParent, int focusGroupAbove) {
//			super(focusGroupAbove);
//			this.fractionOfParent = fractionOfParent;
//		}
//	}
	
}
