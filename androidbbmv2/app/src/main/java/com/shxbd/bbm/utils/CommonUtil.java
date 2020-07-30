package com.shxbd.bbm.utils;

public class CommonUtil {

    public static String fliterChinese(String str) {

        return str.replaceAll("[^(a-zA-Z0-9)]", "");
    }
}
