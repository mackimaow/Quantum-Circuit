package utils.customCollections;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class Tree<T> implements Collection<T> {
	private Tree<T> parent;
	private T element;
	private LinkedList<Tree<T>> children = new LinkedList<>();
	
	private Tree(T element, Tree<T> parent) {
		this.element = element;
		this.parent = parent;
	}
	
	public Tree(T element) {
		this(element, null);
	}
	
	public T getElement() {
		return element;
	}
	
	public Tree<T> getParent() {
		return parent;
	}
	
	public Tree<T> getParent(int parentAbove) {
		Tree<T> current = parent;
		while(parentAbove != 0) {
			parentAbove--;
			current = current.parent;
		}
		return current;
	}
	
	@Override
	public int size() {
		int size = 0;
		Iterator<T> iterator = iterator();
		while(iterator.hasNext()) size++;
		return size;
	}

	@Override
	public boolean isEmpty() {
		return children.size() == 0;
	}

	@Override
	public boolean contains(Object o) {
		for(T element : this)
			if(element.equals(o)) return true;
		return false;
	}

	@Override
	public Iterator<T> iterator() {
		return new TreeIterator();
	}
	
	public Iterator<Tree<T>> nodeIterator() {
		return new TreeNodeIterator();
	}
	
	public LinkedList<Tree<T>> getChildren() {
		return children;
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
	public <E> E[] toArray(E[] array) {
		int size = size();
		E[] elements = (array.length >= size)? array : (E[]) Array.newInstance(array.getClass().getComponentType(), size);
		int i = 0;
        for(T element : this)
			elements[i++] = (E) element;
        if (array.length > size)
        	array[size] = null;
        return elements;
	}

	@Override
	public boolean add(T e) {
		children.addLast(new Tree<>(e, this));
		return true;
	}
	
	public void add(Tree<T> tree) {
		tree.parent = this;
		children.addLast(tree);
	}

	@Override
	public boolean remove(Object o) {
		Iterator<T> iterator = iterator();
		while(iterator.hasNext()) {
			T element = iterator.next();
			if(element.equals(o)) {
				iterator.remove();
				return true;
			}
		}
		return false;
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		for(Object o : c)
			if(!contains(o)) return false;
		return true;
	}

	@Override
	public boolean addAll(Collection<? extends T> c) {
		boolean changed = false;
		for(T element : c)
			changed |= add(element);
		return changed;
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		boolean changed = false;
		for(Object toRemove : c) {
			Iterator<T> iterator = iterator();
			while(iterator.hasNext()) {
				T element = iterator.next();
				if(element.equals(toRemove)) {
					iterator.remove();
					changed = true;
				}
			}
		}
		return changed;
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		boolean changed = false;
		Iterator<T> iterator = iterator();
		loop1 : while(iterator.hasNext()) {
			T element = iterator.next();
			for(Object toRetain : c)
				if(toRetain.equals(element))
					continue loop1;
			iterator.remove();
			changed = true;
		}
		return changed;
	}

	@Override
	public void clear() {
		children.clear();
	}
	
	private Tree<T> getInstance() {
		return this;
	}
	
	private class TreeNodeIterator implements Iterator<Tree<T>> {
		private Stack<Iterator<Tree<T>>> iteratorStack = new Stack<>();
		private boolean hasNext = true;
		private boolean nextHasChildren = false;
		
		private TreeNodeIterator() {
			LinkedList<Tree<T>> temp = new LinkedList<>();
			temp.add(getInstance());
			iteratorStack.push(temp.iterator());
			checkHasNext();
		}
		
		private Iterator<Tree<T>> peak() {
			return iteratorStack.peak();
		}
		
		private Iterator<Tree<T>> pop() {
			return iteratorStack.pop();
		}
		
		@Override
		public void remove() {
			peak().remove();
			if(nextHasChildren) {
				pop();
				if(!iteratorStack.isEmpty())
					checkHasNext();
				else
					hasNext = false;
			}
		}
		
		private void checkHasNext() {
			if(peak().hasNext()) return;
			do pop();
			while(!iteratorStack.isEmpty() && !peak().hasNext());
			hasNext = !iteratorStack.isEmpty();
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public Tree<T> next() {
			Tree<T> tree = peak().next();
			nextHasChildren = !tree.children.isEmpty();
			if(nextHasChildren)
				iteratorStack.push(tree.children.iterator());
			checkHasNext();
			return tree;
		}
	}
	
	
	private class TreeIterator implements Iterator<T> {

		private Stack<Iterator<Tree<T>>> iteratorStack = new Stack<>();
		private boolean hasNext = true;
		private boolean nextHasChildren = false;
		
		private TreeIterator() {
			LinkedList<Tree<T>> temp = new LinkedList<>();
			temp.add(getInstance());
			iteratorStack.push(temp.iterator());
			checkHasNext();
		}
		
		private Iterator<Tree<T>> peak() {
			return iteratorStack.peak();
		}
		
		private Iterator<Tree<T>> pop() {
			return iteratorStack.pop();
		}
		
		@Override
		public void remove() {
			peak().remove();
			if(nextHasChildren) {
				pop();
				if(!iteratorStack.isEmpty())
					checkHasNext();
				else
					hasNext = false;
			}
		}
		
		private void checkHasNext() {
			if(peak().hasNext()) return;
			do pop();
			while(!iteratorStack.isEmpty() && !peak().hasNext());
			hasNext = !iteratorStack.isEmpty();
		}
		
		@Override
		public boolean hasNext() {
			return hasNext;
		}

		@Override
		public T next() {
			Tree<T> tree = peak().next();
			nextHasChildren = !tree.children.isEmpty();
			if(nextHasChildren)
				iteratorStack.push(tree.children.iterator());
			checkHasNext();
			return tree.element;
		}
	}
	
}
