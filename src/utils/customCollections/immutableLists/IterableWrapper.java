package utils.customCollections.immutableLists;

import java.util.Iterator;

public class IterableWrapper<T> implements Iterable<T> {
	
	private Iterator<T> iterator;
	
	public IterableWrapper(Iterator<T> iterator) {
		this.iterator = iterator;
	}

	@Override
	public Iterator<T> iterator() {
		return iterator;
	}
}
