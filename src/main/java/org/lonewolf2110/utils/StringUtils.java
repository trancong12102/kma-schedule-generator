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

    public static String reverseSemester(String source) {
        List<String> wordList = split(source, "_");

        if (wordList.size() == 3) {
            return wordList.get(2) + "_" + wordList.get(0) + "_" + wordList.get(1);
        }

        return source;
    }
}
