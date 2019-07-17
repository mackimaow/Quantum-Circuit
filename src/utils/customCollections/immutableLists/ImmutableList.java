package utils.customCollections.immutableLists;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class ImmutableList<T> implements List<T> {
	private List<T> list;

	public ImmutableList(List<T> list) {
		this.list = list;
	}
	
	@Override
	public int size() {
		return list.size();
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}

	@Override
	public Iterator<T> iterator() {
		return listIterator();
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <E> E[] toArray(E[] a) {
		return list.toArray(a);
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
		return list.containsAll(c);
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		return false;
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
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

	@Override
	public T get(int index) {
		return list.get(index);
	}

	@Override
	public T set(int index, T element) {
		return null;
	}

	@Override
	public void add(int index, T element) {
	}

	@Override
	public T remove(int index) {
		return null;
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<T> listIterator() {
		return listIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ImmutableListIterator(index);
	}
	
	@Override
	public List<T> subList(int fromIndex, int toIndex) {
		return new ImmutableList<T>(list.subList(fromIndex, toIndex));
	}

	private class ImmutableListIterator implements ListIterator<T> {
		private ListIterator<T> iterator;
		public ImmutableListIterator (int index){
			iterator = list.listIterator(index);
		}
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}
		@Override
		public T next() {
			return iterator.next();
		}
		@Override
		public boolean hasPrevious() {
			return iterator.hasPrevious();
		}
		@Override
		public T previous() {
			return iterator.previous();
		}
		@Override
		public int nextIndex() {
			return iterator.nextIndex();
		}
		@Override
		public int previousIndex() {
			return iterator.previousIndex();
		}
		@Override
		public void remove() {
		}
		@Override
		public void set(T e) {
		}
		@Override
		public void add(T e) {
		}
	}
	
}
