package com.spring.util;

/**
 * <p>Description:
 * </p>
 *
 * @author marvin
 * Created at 2023/5/20
 */
public class StringUtils {
	public static String toLowerCaseFirstChar(String inputString) {
		return inputString.substring(0, 1).toLowerCase() + inputString.substring(1);
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}
}
