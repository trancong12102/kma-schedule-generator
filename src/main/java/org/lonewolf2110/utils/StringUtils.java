package org.lonewolf2110.utils;

import java.util.ArrayList;
import java.util.List;

public class StringUtils {
    public static List<String> split(String source, String separator) {
        List<String> stringList = new ArrayList<>();
        String src = source + separator;
        int idx;

        while ((idx = src.indexOf(separator)) != -1) {
            stringList.add(src.substring(0, idx));
            src = src.substring(idx + 1);
        }

        return stringList;
    }
}
