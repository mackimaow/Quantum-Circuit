package utils.customCollections;

public class Triple<A,B,C> extends Pair<A,B> {
	private static final long serialVersionUID = -5842585376585820649L;
	
	private C third;
	
	public Triple(A first, B second, C third) {
		super(first, second);
		this.third = third;
	}
	
	public Triple() {
		this(null, null, null);
	}
	
	public C third() {
		return third;
	}
	
	public void setThird(C third) {
		this.third = third;
	}
	
	public void setThree(A first, B second, C third) {
		setBoth(first, second);
		this.third = third;
	}
}
