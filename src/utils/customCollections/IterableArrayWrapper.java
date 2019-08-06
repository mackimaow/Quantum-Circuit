package utils.customCollections;

import java.util.Iterator;

public class IterableArrayWrapper<T> implements Iterable<T> {
	private final T[] array;
	
	public IterableArrayWrapper(T[] array) {
		this.array = array;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator();
	}
	
	
	private class ArrayIterator implements Iterator<T> {
		private int nextIndex = 0;
		@Override
		public boolean hasNext() {
			return nextIndex < array.length;
		}

		@Override
		public T next() {
			return array[nextIndex++];
		}
	}
}
