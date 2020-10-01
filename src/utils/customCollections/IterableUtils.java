package utils.customCollections;

import java.util.stream.Stream;

import utils.customCollections.immutableLists.IterableWrapper;

public class IterableUtils {
	public static <T> Iterable<T> convert(Stream<T> stream) {
		return new IterableWrapper<T>(stream.iterator());
	}
}
