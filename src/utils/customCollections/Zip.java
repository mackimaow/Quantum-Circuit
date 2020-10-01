package utils.customCollections;

import java.util.Iterator;

public class Zip implements Iterable<Object[]> {
	
	private Iterable<?>[] iterables;
	
	public static Zip mk(Iterable<?> ... iterables) {
		return new Zip(iterables);
	}
	
	private Zip(Iterable<?> ... iterables) {
		this.iterables = iterables;
	}
	
	@Override
	public Iterator<Object[]> iterator() {
		return new ZipIterator();
	}
	
	private class ZipIterator implements Iterator<Object[]>{
		Object[] iterators;
		
		ZipIterator() {
			int length = iterables.length;
			iterators = new Object[length];
			for (int i : Range.mk(length))
				iterators[i] = iterables[i].iterator();
		}

		@Override
		public boolean hasNext() {
			for (Object o : iterators)
				if (! ((Iterator<?>) o).hasNext())		
					return false;
			return true;
		}

		@Override
		public Object[] next() {
			int length = iterators.length;
			Object[] output = new Object[length];
			for (int i : Range.mk(length)) {
				Iterator<?> iter = (Iterator<?>) iterators[i];
				output[i] = iter.next();
			}
			return output;
		}
		
		
	}
}
