package utils.customCollections;

import java.io.Serializable;

public class Single<T> implements Serializable {
	private static final long serialVersionUID = 6365695730648120660L;
	
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
