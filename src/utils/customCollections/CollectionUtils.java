package utils.customCollections;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class CollectionUtils {
	
	/**
	 *
	 */
	public static <T extends Comparable<T>> ArrayList<Integer> sortedListIndexes(T[] unsortedList) {
		ArrayList<Integer> indexes = new ArrayList<>(unsortedList.length);
		
	    HashMap<Integer, T> indexMap = new HashMap<>();
	    int index = 0;
	    for (T element : unsortedList) {
	    	indexes.add(index);
	        indexMap.put(index++, element);
	    }
	    
	    Collections.sort(indexes, new Comparator<Integer>() {
	    	
	        public int compare(Integer left, Integer right) {

	            T leftIndex = indexMap.get(left);
	            T rightIndex = indexMap.get(right);
	            if (leftIndex == null)
	                return -1;
	            if (rightIndex == null)
	                return 1;

	            return leftIndex.compareTo(rightIndex);
	        }
	    });
	    
	    return indexes;
	}
	
	public static <T extends Comparable<T>> int binarySearch(List<T> list, T element) {
		return binarySearch(list, element, 0, list.size() - 1);
	}
	
	public static <T extends Comparable<T>> int binarySearch(List<T> list, T element, int lowerBound, int upperBound) {
		int indexRange = upperBound - lowerBound;
		if(indexRange < 2) {
			if(element.compareTo(list.get(lowerBound)) < 0)
				return lowerBound;
			else
				return lowerBound + 1;
		} else {
			int middleIndex = lowerBound + (int) Math.floor(indexRange / 2d);
			if(element.compareTo(list.get(middleIndex)) < 0)
				return binarySearch(list, element, lowerBound, middleIndex);
			else
				return binarySearch(list, element, middleIndex, upperBound);
		}
	}
}
