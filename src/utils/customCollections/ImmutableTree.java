package utils.customCollections;

import java.util.Iterator;

public class ImmutableTree<T> extends ImmutableCollection<T> {

	public ImmutableTree(Tree<T> tree) {
		super(tree);
	}
	
	public T getElement() {
		return getTree().getElement();
	}
	
	public ImmutableTree<T> getChild(int index) {
		return new ImmutableTree<>(getTree().getChildren().get(index));
	}
	
	public int getChildrenSize() {
		return getTree().getChildren().size();
	}
	
	public ChildrenIterator getChildrenIterator() {
		return new ChildrenIterator();
	}
	
	private Tree<T> getTree() {
		return (Tree<T>)collection;
	}
	
	private class ChildrenIterator implements Iterator<ImmutableTree<T>> {
		private Iterator<Tree<T>> iterator = getTree().getChildren().iterator();
		
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
