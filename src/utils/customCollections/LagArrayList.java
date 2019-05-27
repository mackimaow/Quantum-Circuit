package utils.customCollections;

import java.util.ArrayList;
import java.util.Iterator;

public class LagArrayList <T> implements Iterable<T> {
	private ArrayList<T>  baseList = new ArrayList<>();
	private Queue<Triple<Boolean, T, Integer>> lagQueue = new Queue<>();
	private int lagged = 0;
	
	
	
	public synchronized void setLagged(boolean lag) {
		if(lagged != 0 || lag)
			lagged = lagged + (lag? 1 : -1);
		else return;
		
		if(lagged == 0) {
			for(Triple<Boolean,T,Integer> p : lagQueue) {
				if(p.first()) {
					if(p.third() == -1)
						baseList.add(p.second());
					else
						baseList.add(p.third(), p.second());
				} else {
					if(p.third() == -1)
						baseList.remove(p.first());
					else
						baseList.remove(p.third());
				}
			}
			lagQueue.clear();
		}
	}
	
	
	
	
	public T get(int index) {
		return baseList.get(index);
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	public synchronized boolean remove(Object o) {
		if(lagged > 0)
			lagQueue.enqueue(new Triple<>(false, (T) o, -1));
		else
			return baseList.remove(o);
		return false;
	}
	
	
	
	
	public synchronized T remove(int index) {
		if(lagged > 0)
			lagQueue.enqueue(new Triple<>(false, null, index));
		else
			return baseList.remove(index);
		return null;
	}
	
	
	
	
	public synchronized boolean add(T e) {
		if(lagged > 0)
			lagQueue.enqueue(new Triple<>(true, e, -1));
		else
			return baseList.add(e);
		return false;
	}
	
	
	
	
	public synchronized void add(int index, T element) {
		if(lagged > 0)
			lagQueue.enqueue(new Triple<>(true, element, index));
		else
			baseList.add(index, element);
	}
	
	
	
	
	public int size() {
		return baseList.size();
	}

	
	
	
	@Override
	public Iterator<T> iterator() {
		return new ListItr();
	}

	private class ListItr implements Iterator<T> {
		private Iterator<T> iter = baseList.iterator();
		
		@Override
		public boolean hasNext() {
			return iter.hasNext();
		}

		@Override
		public T next() {
			return iter.next();
		}
	}
	
	
}
