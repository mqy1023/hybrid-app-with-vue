/*
 * Copyright (c) 2016, ZCJ All Rights Reserved.
 * Project Name: zcj_android-V0.21
 */
package androidtest.example.com.utils;


public class StringUtils {

	public static boolean isEmpty(CharSequence cs) {
		return (cs == null) || (cs.length() == 0);
	}

	public static boolean isNotEmpty(CharSequence cs) {
		return !isEmpty(cs);
	}


	/**
	 * 字符串是否为空
	 *
	 * @param s: 字符串
	 * @return: 是否为空
	 */
	public static boolean isSpace(String s) {
		return (s == null || s.trim().length() == 0);
	}
	/**
	 * 判断字符串是否为空
	 * @param text
	 * @return true null false !null
	 */
	public static boolean isNull(CharSequence text) {
		if (text == null || "".equals(text.toString().trim()) || "null".equals(text))
			return true;
		return false;
	}
}
