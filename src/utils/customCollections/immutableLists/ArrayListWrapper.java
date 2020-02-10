package utils.customCollections.immutableLists;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import utils.customCollections.Single;

public class ArrayListWrapper<T> implements List<T> {
	private final Object[] elements;
	
	public ArrayListWrapper(T[] elements) {
		this.elements = elements;
	}
	
	private ArrayListWrapper(Single<Object[]> elements) {
		this.elements = elements.first();
	}
	
	@Override
	public int size() {
		return elements.length;
	}

	@Override
	public boolean isEmpty() {
		return elements.length == 0;
	}

	@Override
	public boolean contains(Object o) {
		for(int i = 0; i < size(); i++)
			if(elements[i].equals(o)) return true;
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new ArrayIterator(0);
	}
	
	private class ArrayIterator implements ListIterator<T> {
		
		private int lastIndex = -1;
		private int nextIndex;
		
		public ArrayIterator(int index) {
			this.nextIndex = index;
		}
		
		@Override
		public boolean hasNext() {
			return nextIndex() < size();
		}

		@SuppressWarnings("unchecked")
		@Override
		public T next() {
			lastIndex = nextIndex();
			return (T) elements[nextIndex++];
		}

		@Override
		public boolean hasPrevious() {
			return previousIndex() >= 0;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T previous() {
			lastIndex = previousIndex();
			return (T) elements[(nextIndex--) - 1];
		}

		@Override
		public int nextIndex() {
			return nextIndex;
		}

		@Override
		public int previousIndex() {
			return nextIndex - 1;
		}

		@Override
		public void remove() {
	        throw new UnsupportedOperationException("remove");
		}

		@Override
		public void set(T e) {
			if(lastIndex == -1)
				throw new RuntimeException("set() was called before a call to next() or previous()");
			elements[lastIndex] = e;
		}

		@Override
		public void add(T e) {
	        throw new UnsupportedOperationException("add");
		}
		
		
	}

	@Override
	public Object[] toArray() {
		Object[] elements = new Object[size()]; 
		int i = 0;
		for(T element : this)
			elements[i++] = element;
		return elements;
	}

	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] a) {
		E[] elements = (a.length >= size())? a : (E[]) Array.newInstance(a.getClass().getComponentType(), size());
		int i = 0;
        for(T element : this)
			elements[i++] = (E) element;
        if (a.length > size())
        	a[size()] = null;
        return elements;
	}

	@Override
	public boolean add(T e) {
        throw new UnsupportedOperationException("add");
	}

	@Override
	public boolean remove(Object o) {
        throw new UnsupportedOperationException("remove");
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c)
			if(!contains(o)) return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
        throw new UnsupportedOperationException("addAll");
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> c) {
        throw new UnsupportedOperationException("addAll");
	}

	@Override
	public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("removeAll");
	}

	@Override
	public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("retainAll");
	}

	@Override
	public void clear() {
        throw new UnsupportedOperationException("clear");
	}

	@SuppressWarnings("unchecked")
	@Override
	public T get(int index) {
		return (T) elements[index];
	}

	@SuppressWarnings("unchecked")
	@Override
	public T set(int index, T element) {
		T prev = (T) elements[index];
		elements[index] = element;
		return prev;
	}

	@Override
	public void add(int index, T element) {
        throw new UnsupportedOperationException("add");
	}

	@Override
	public T remove(int index) {
        throw new UnsupportedOperationException("remove");
	}

	@Override
	public int indexOf(Object o) {
		for(int i = 0; i < size(); i++)
			if(elements[i].equals(o)) return i;
		return -1;
	}

	@Override
	public int lastIndexOf(Object o) {
		for(int i = size() - 1; i >= 0; i++)
			if(elements[i].equals(o)) return i;
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ArrayIterator(0);
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ArrayIterator(index);
	}

	@Override
	public ArrayListWrapper<T> subList(int fromIndex, int toIndex) {
		Object[] temp = new Object[toIndex - fromIndex];
		for(int i = fromIndex; i < toIndex; i++)
			temp[i - fromIndex] = elements[i];		
		return new ArrayListWrapper<T>(new Single<>(temp));
	}
 
}
