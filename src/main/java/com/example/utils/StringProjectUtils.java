package com.example.utils;

import org.apache.commons.lang3.ArrayUtils;

public final class StringProjectUtils {
    public static String format(String str, Object... params) {
        if (str == null) {
            return null;
        } else if (ArrayUtils.isEmpty(params)) {
            return str;
        } else {
            for (int i = 0; i < params.length; i++) {
                String param = params[i] == null ? "null" : String.valueOf(params[i]);
                str = str.replaceFirst("\\{\\}", param);
            }
            return str;
        }
    }
}
