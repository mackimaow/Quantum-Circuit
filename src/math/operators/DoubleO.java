package math.operators;

//                        7
//                        ^  
public final class DoubleO implements OperatorSet<Double>{
	private static final long serialVersionUID = -2688808386681917690L;
	
	public static final DoubleO OPERATOR_SET = new DoubleO();
	
	private DoubleO() {}
	
	@Override
	public Double get1() {
		return 1d;
	}

	@Override
	public Double getn1() {
		return -1d;
	}

	@Override
	public Double get0() {
		return 0d;
	}

	@Override
	public Double[] mkZeroArray(int size) {
		Double[] comps = new Double[size];
		for(int i = 0; i < size; i++)
			comps[i] = 0d;
		return comps;
	}
	
	@Override
	public Double add(Double num1, Double num2) {
		return num1 + num2;
	}

	@Override
	public Double sub(Double num1, Double num2) {
		return num1 - num2;
	}

	@Override
	public Double mult(Double num1, Double num2) {
		return num1 * num2;
	}

	@Override
	public Double div(Double num1, Double num2) {
		return num1 / num2;
	}
	
	@Override
	public Double neg(Double num1) {
		return -num1;
	}

	@Override
	public Double pow(Double num1, Double num2) {
		return Math.pow(num1 , num2 );
	}

	@Override
	public Double sqrt(Double num) {
		return Math.pow(num , .5);
	}

	@Override
	public Double conjugate(Double num) {
		return num;
	}
	
}
