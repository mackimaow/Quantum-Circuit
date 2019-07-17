package utils.customCollections.immutableLists;

import java.util.Iterator;

import utils.customCollections.Tree;

public class ImmutableTree<T> extends ImmutableCollection<T> {

	public ImmutableTree(Tree<T> tree) {
		super(tree);
	}
	
	public T getElement() {
		return getTree().getElement();
	}
	
	public ImmutableTree<T> getParent() {
		return new ImmutableTree<>(getTree().getParent());
	}
	
	public ImmutableTree<T> getParent(int parentAbove) {
		return new ImmutableTree<>(getTree().getParent(parentAbove));
	}
	
	public ImmutableTree<T> getChild(int index) {
		return new ImmutableTree<>(getTree().getChildren().get(index));
	}
	
	public int getChildrenSize() {
		return getTree().getChildren().size();
	}
	
	public TreeIterator getChildrenIterator() {
		return new TreeIterator(getTree().getChildren().iterator());
	}
	
	public TreeIterator getNodeIterator() {
		return new TreeIterator(getTree().nodeIterator());
	}
	
	private Tree<T> getTree() {
		return (Tree<T>)collection;
	}
	
	private class TreeIterator implements Iterator<ImmutableTree<T>> {
		private final Iterator<Tree<T>> iterator;
		
		private TreeIterator(Iterator<Tree<T>> iterator) {
			this.iterator = iterator;
		}
		
		@Override
		public boolean hasNext() {
			return iterator.hasNext();
		}

		@Override
		public ImmutableTree<T> next() {
			return new ImmutableTree<>(iterator.next());
		}
		
	}
}
