package utils.customCollections;

import java.util.Iterator;
import java.util.function.BiFunction;

public class Range <T extends Number & Comparable<T>> implements Iterable<T> {
	private T start, end, incr;
	private BiFunction<T, T, T> add;
	
	private static <T> Object[] fillInArgs(T[] args) {
		int length = args.length;
		if (length <= 0 || length > 3) 
			throw new IllegalArgumentException("Range must have at least 1 and no more that 3 arguments.");
		Object[] argsP = {0, 0, 1};
		if (length == 1) {
			argsP[1] = args[0];
		} else if (length == 2) {
			argsP[0] = args[0];
			argsP[1] = args[1];
		} else {
			argsP = args;
		}
		return argsP;
	}
	
	@SuppressWarnings("unchecked")
	private static <T extends Number & Comparable<T>> Range<T> mk(BiFunction<T, T, T> add, T ... args) {
		Object[] argsP = fillInArgs(args);
		return new Range<T>((T)argsP[0], (T)argsP[1], (T)argsP[2], add);
	}
	
	public static Range<Long> mk(Long ... args) {
		return mk((x, y) -> x + y, args);
	}
	public static Range<Float> mk(Float ... args) {
		return mk((x, y) -> x + y, args);
	}
	public static Range<Integer> mk(Integer ... args) {
		return mk((x, y) -> x + y, args);
	}
	public static Range<Double> mk(Double ... args) {
		return mk((x, y) -> x + y, args);
	}
	
	
	private Range(T start, T end, T incr, BiFunction<T, T, T> add) {
		this.start = start;
		this.end = end;
		this.incr = incr;
		this.add = add;
	}
	
	@Override
	public Iterator<T> iterator() {
		return new RangeIterator();
	}
	
	private class RangeIterator implements Iterator<T> {
		private T current;
		
		public RangeIterator() {
			this.current = start;
		}
		
		@Override
		public boolean hasNext() {
			return current.compareTo(end) < 0;
		}
		
		@Override
		public T next() {
			T toReturn = current;
			current = add.apply(current, incr);
			return toReturn;
		}
	}
}
