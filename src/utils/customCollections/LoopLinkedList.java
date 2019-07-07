package utils.customCollections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

public class LoopLinkedList<T> implements List<T> {
	
	private DoubleListNode<T> firstNode = null;
	private int size;
	private int windness;
	
	
	public LoopLinkedList() {
		size = 0;
		windness = 0;
	}

	public void windTo(int amt) {
		firstNode = getNodeAt(amt);
		windness += amt;
	}
	
	public void resetWindness() {
		windTo(-windness);
		windness = 0;
	}
	
	public int getWindness() {
		return windness;
	}
	
	private DoubleListNode<T> removeNode(DoubleListNode<T> nodeToDelete) {
		if(nodeToDelete == null || size == 1)
			return removeFirstNode();
	
		nodeToDelete.previous.next = nodeToDelete.next;
		nodeToDelete.next.previous = nodeToDelete.previous;
		size--;
		return nodeToDelete;
	}
	
	private DoubleListNode<T> addElementBefore(DoubleListNode<T> nodeToAddBefore, T element) {
		if(nodeToAddBefore == null)
			return setFirstNode(element);
		DoubleListNode<T> nodeToAdd = new DoubleListNode<T>(element, nodeToAddBefore.previous, nodeToAddBefore);
		nodeToAddBefore.previous.next = nodeToAdd;
		nodeToAddBefore.previous = nodeToAdd;
		size++;
		return nodeToAdd;
	}
	
	private DoubleListNode<T> getNodeAt(int index) {
		DoubleListNode<T> currentNode = firstNode;
		if(index < 0)
			index += size;
		int reducedIndex = size != 0? index % size : 0;
		if(reducedIndex <= size / 2) {
			for(int i = 0; i < reducedIndex; i++)
				currentNode = currentNode.next;
		} else {
			reducedIndex = reducedIndex - size;
			for(int i = 0; i > reducedIndex; i--)
				currentNode = currentNode.previous;
		}
		return currentNode;
	}
	
	private DoubleListNode<T> setFirstNode(T element) {
		DoubleListNode<T> temp = new DoubleListNode<T>(element, null, null);
		temp.next = temp;
		temp.previous = temp;
		firstNode = temp;
		size = 1;
		return temp;
	}
	
	private DoubleListNode<T> removeFirstNode() {
		DoubleListNode<T> temp = firstNode;
		firstNode = null;
		size = 0;
		return temp;
	}
	
	@Override
	public boolean add(T item) {
		addElementBefore(firstNode, item);
		return true;
	}


	@Override
	public boolean addAll(Collection<? extends T> collection) {
		for(T element : collection)
			add(element);
		return true;
	}


	@Override
	public void clear() {
		size = 0;
		firstNode = null;
	}


	@Override
	public boolean contains(Object item) {
		for(T element : this)
			if(element.equals(item))
				return true;
		return false;
	}


	@Override
	public boolean containsAll(Collection<?> collection) {
		for(Object element : collection)
			if(!contains(element)) return false;
		return true;
	}


	@Override
	public boolean isEmpty() {
		return size == 0;
	}


	@Override
	public Iterator<T> iterator() {
		return new ListItr();
	}
	
	
	private class ListItr implements ListIterator<T> {
		private DoubleListNode<T> previousNode = null;
		private DoubleListNode<T> nextNode = null;
		private DoubleListNode<T> lastNodeSeen = null;
		int nextIndex;
		
		private ListItr() {
			this.nextNode = firstNode;
			this.nextIndex = 0;
		}
		
		private ListItr(int index) {
			if(index < 0)
				index += size;
			index = index % size;
			this.nextNode = getNodeAt(index);
			if(this.nextNode != null)
				this.previousNode = this.nextNode.previous;
			this.nextIndex = index;
		}
		
		@Override
		public boolean hasNext() {
			return nextIndex != size;
		}

		@Override
		public T next() {
			if(!hasNext()) throw new NoSuchElementException();
			previousNode = nextNode;
			nextNode = previousNode.next;
			nextIndex++;
			lastNodeSeen = previousNode;
			return previousNode.element;
		}

		@Override
		public boolean hasPrevious() {
			return nextIndex == 0;
		}

		@Override
		public T previous() {
			if(!hasPrevious()) throw new NoSuchElementException();
			nextNode = previousNode;
			previousNode = nextNode.previous;
			nextIndex--;
			nextNode = previousNode;
			return nextNode.element;
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
			if(lastNodeSeen == null) throw new NoSuchElementException();
			if(size == 1) {
				firstNode = null;
				previousNode = null;
				nextNode = null;
				nextIndex = 0;
			} else {
				removeNode(lastNodeSeen);
				if(nextNode == lastNodeSeen) {
					nextNode = nextNode.next;
				} else {
					previousNode = previousNode.previous;
					nextIndex--;
				}
			}
			lastNodeSeen = null;
		}

		@Override
		public void set(T e) {
			if(lastNodeSeen == null) throw new NoSuchElementException();
			lastNodeSeen.element = e;
		}

		@Override
		public void add(T e) {
			if(lastNodeSeen == null) throw new NoSuchElementException();
			addElementBefore(lastNodeSeen, e);
			nextIndex++;
			lastNodeSeen = null;
		}

	}

	@Override
	public boolean remove(Object item) {
		Iterator<T> iterator = iterator();
		while(iterator.hasNext()) {
			T element = iterator.next();
			if(element.equals(item)) {
				iterator.remove();
				size--;
			}
		}
		return true;
	}


	@Override
	public boolean removeAll(Collection<?> collection) {
		for(Object element : collection)
			remove(element);
		return true;
	}


	@Override
	public boolean retainAll(Collection<?> collection) {
		Iterator<T> iterator = iterator();
		while(iterator.hasNext()) {
			T element = iterator.next();
			boolean containsElement = false;
			for(Object otherElement : collection)
				containsElement |= element.equals(otherElement);
			if(!containsElement)
				iterator.remove();
		}
		return true;
	}


	@Override
	public int size() {
		return size;
	}


	@Override
	public Object[] toArray() {
		Object[] elements = new Object[size]; 
		int i = 0;
		for(T element : this)
			elements[i++] = element;
		return elements;
	}


	@SuppressWarnings("unchecked")
	@Override
	public <E> E[] toArray(E[] array) {
		E[] elements = (array.length >= size)? array : (E[]) Array.newInstance(array.getClass().getComponentType(), size);
		int i = 0;
        for(T element : this)
			elements[i++] = (E) element;
        if (array.length > size)
        	array[size] = null;
        return elements;
	}

	@Override
	public void add(int index, T item) {
		DoubleListNode<T> node = getNodeAt(index);
		addElementBefore(node, item);
	}

	@Override
	public boolean addAll(int index, Collection<? extends T> collection) {
		DoubleListNode<T> node = getNodeAt(index);
		for(T element : collection)
			addElementBefore(node, element);
		return true;
	}

	@Override
	public T get(int index) {
		if(size == 0)
			throw new IndexOutOfBoundsException("The list is empty");
		DoubleListNode<T> node = getNodeAt(index);
		return node.element;
	}

	@Override
	public int indexOf(Object item) {
		int index = 0;
		for(T element : this) {
			if(element.equals(item))
				return index;
			index++;
		}
		return -1;
	}

	@Override
	public int lastIndexOf(Object item) {
		ListIterator<T> listIterator = listIterator(size - 1);
		int index = size - 1;
		while(listIterator.hasPrevious()) {
			T element = listIterator.previous();
			if(element.equals(item))
				return index;
			index--;
		}
		return -1;
	}

	@Override
	public ListIterator<T> listIterator() {
		return new ListItr();
	}

	@Override
	public ListIterator<T> listIterator(int index) {
		return new ListItr(index);
	}

	@Override
	public T remove(int index) {
		DoubleListNode<T> node = getNodeAt(index);
		if(node == null) return null;
		return node.element;
	}

	@Override
	public T set(int index, T item) {
		DoubleListNode<T> node = getNodeAt(index);
		if(node == null)
			throw new IndexOutOfBoundsException("The list is empty");
		T prevElement = node.element;
		node.element = item;
		return prevElement;
	}

	@Override
	public LoopLinkedList<T> subList(int startIndex, int endIndex) {
		throw new UnsupportedOperationException();
	}
	
	@Override
	public String toString() {
		String s = " [ ";
		
		Iterator<T> iterator = this.iterator();
		
		if(size != 0) {
			s += iterator.next();
			while(iterator.hasNext())
				s += ", " + iterator.next();
		}
		
		return s + " ] ";
	}
}
