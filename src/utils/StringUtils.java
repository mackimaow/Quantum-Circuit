package utils;

public class StringUtils {
	public static String prefixNewLine(String target, String prefix) {
		return prefix + target.replaceAll("\n", '\n' + prefix);
	}
}
