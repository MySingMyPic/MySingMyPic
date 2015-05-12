package com.ylsg365.pai.util;

/**
 * Created by lanzhihong on 2015/3/29.
 */
public class StringUtil {
    public static boolean isNull(String object) {
        if (object == null || object.trim().length() == 0 || "null".equals(object)) {
            return true;
        }
        return false;
    }
}
