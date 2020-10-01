package utils.customMaps;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import utils.customCollections.Range;
import utils.customCollections.Zip;

public class IndexMap extends AbstractMap<Integer, Integer> implements Iterable<Integer> {
	
	private ArrayList<Integer> components;
	
	public IndexMap (Iterable<Integer> iterable) {
		components = new ArrayList<>();
		iterable.forEach(components::add);
	}

	@Override
	public Set<Entry<Integer, Integer>> entrySet() {
		int size = size();
		HashSet<Entry<Integer, Integer>> set = new HashSet<>();
		for (Object[] z : Zip.mk(components, Range.mk(size)))
			set.add(new IndexEntry((int) z[0], (int) z[1]));
		return set;
	}
	
	public IndexMap map(Map<Integer, Integer> map) {
		int size = map.size();
		IndexMap indexMap = new IndexMap(this);
		for (Object[] z : Zip.mk(Range.mk(size), indexMap))
			put((int) z[0], map.get((int) z[1]));
		return indexMap;
	}
	
	@Override
	public Iterator<Integer> iterator() {
		return components.iterator();
	}
	
	@Override
	public int size() {
		return components.size();
	}
	
	@Override
	public Integer get(Object key) {
		int index = (int) key;
		return components.get(index);
	}
	
	@Override
	public Integer put(Integer key, Integer value) {
		Integer old = components.get(key);
		components.set(key, value);
		return old;
	}
	
	private class IndexEntry implements Map.Entry<Integer, Integer> {
		private int key, value;
		
		IndexEntry(int key, int value) {
			this.key = key;
			this.value = value;
		}
		
		@Override
		public Integer getKey() {
			return key;
		}

		@Override
		public Integer getValue() {
			return value;
		}

		@Override
		public Integer setValue(Integer value) {
			return put(key, value);
		}
		
	}
}
