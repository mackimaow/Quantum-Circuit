package utils.customCollections;

class DoubleListNode <T> {
	T element;
	DoubleListNode<T> next;
	DoubleListNode<T> previous;
	
	DoubleListNode(T element, DoubleListNode<T> previous, DoubleListNode<T> next) {
		this.element = element;
		this.next = next;
		this.previous = previous;
	}
}
