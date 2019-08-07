package utils.customCollections;


/**
 * 
 * Class instances are used to make methods able to output 2 values
 * instead of the usual 1.
 * 
 * @author Massimiliano Cutugno
 *
 * @param <T>
 * @param <E>
 */
public class Pair <T, E> extends Single<T> {
	private E second;
	
	public Pair(T first, E second) {
		super(first);
		setSecond(second);
	}
	
	public Pair () {
		this (null, null);
	}
	
	/**
	 * 
	 * @return second element
	 */
	public E second () {
		return second;
	}
	
	public void setSecond (E second) {
		this.second = second;
	}
	
	public void setBoth (T first, E second) {
		setFirst(first);
		this.second = second;
	}
}
