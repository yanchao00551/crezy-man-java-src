package com.crezyman.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils {
	private static Pattern humpPattern = Pattern.compile("[A-Z]");
	private static Pattern linePattern = Pattern.compile("_(\\w)");

	public static String captureName(String name) {
		name = name.substring(0, 1).toUpperCase() + name.substring(1);
		return name;

	}
	public static boolean isAcronym(String word) {
		for (int i = 0; i < word.length(); i++) {
			char c = word.charAt(i);

			char c1 = word.charAt(0);
			char c2 = word.charAt(1);

			if (!Character.isLowerCase(c2) && Character.isLowerCase(c1)) {
				return true;
			}

			if (Character.isLowerCase(c1) && Character.isLowerCase(c2) && !Character.isLowerCase(c)) {
				return false;
			}

			if (!Character.isLowerCase(c)) {
				return true;
			}
		}
		return false;
	}

	public static String lineToHump(String str) {
		str = str.toLowerCase();
		Matcher matcher = linePattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, matcher.group(1).toUpperCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}

	public static String[] lineToHumpArr(String[] str) {
		String[] rst = new String[str.length];
		int i = 0;
		for (String s : str) {
			rst[i] = lineToHump(s);
			i++;
		}
		return rst;
	}

	public static String humpToLine(String str) {
		return str.replaceAll("[A-Z]", "_$0").toLowerCase();
	}

	public static String humpToLine2(String str) {
		Matcher matcher = humpPattern.matcher(str);
		StringBuffer sb = new StringBuffer();
		while (matcher.find()) {
			matcher.appendReplacement(sb, "_" + matcher.group(0).toLowerCase());
		}
		matcher.appendTail(sb);
		return sb.toString();
	}
}
