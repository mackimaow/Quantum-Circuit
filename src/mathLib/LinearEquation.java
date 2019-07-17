package mathLib;

public class LinearEquation {
	private final double slope;
	private final double yIntercept;
	
	public LinearEquation (double x1, double y1, double x2, double y2) {
		slope = (y1 - y2) / (x1 - x2);
		yIntercept = y1 - x1 * slope; 
	}
	
	public double getX(double y) {
		return (y - yIntercept) / slope; 
	}
	
	public double getY(double x) {
		return slope * x + yIntercept;
	}
	
	public double getSlope() {
		return slope;
	}
	
	public double getYIntercept() {
		return yIntercept;
	}
}
