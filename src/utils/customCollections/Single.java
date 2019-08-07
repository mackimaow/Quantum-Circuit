package utils.customCollections;

public class Single<T> {
	private T first;
	
	public Single(T first) {
		this.first = first;
	}
	
	public Single () {
		this (null);
	}
	
	/**
	 * 
	 * @return first element
	 */
	public T first () {
		return first;
	}
	
	public void setFirst (T first) {
		this.first = first;
	}
}
