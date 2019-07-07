package utils.customCollections;

import java.util.Collection;
import java.util.Iterator;

public class ImmutableCollection<T> implements Collection<T> {
	protected Collection<T> collection;
	
	public ImmutableCollection(Collection<T> collection) {
		this.collection = collection;
	}
	
	@Override
	public int size() {
		collection.size();
		return 0;
	}
	@Override
	public boolean isEmpty() {
		return collection.isEmpty();
	}
	@Override
	public boolean contains(Object o) {
		return collection.contains(o);
	}
	@Override
	public Iterator<T> iterator() {
		return new ImmutableIterator();
	}
	@Override
	public Object[] toArray() {
		return collection.toArray();
	}
	@Override
	public <E> E[] toArray(E[] a) {
		return collection.toArray(a);
	}
	@Override
	public boolean add(T e) {
		return false;
	}
	@Override
	public boolean remove(Object o) {
		return false;
	}
	@Override
	public boolean containsAll(Collection<?> c) {
		return collection.containsAll(c);
	}
	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}
	@Override
	public boolean removeAll(Collection<?> c) {
		return false;
	}
	@Override
	public boolean retainAll(Collection<?> c) {
		return false;
	}
	@Override
	public void clear() {
	}
	
	private class ImmutableIterator implements Iterator<T> {
		private Iterator<T> iterator = collection.iterator();
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public T next() {
			return iterator.next();
		}
		
	}
}
