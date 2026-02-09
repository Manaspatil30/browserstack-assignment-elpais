package com.manas.utils;

import java.util.*;
import java.util.regex.Pattern;

public class TextAnalyzer {

    // Remove punctuation/symbols so "War:" and "war" count as the same word.
    private static final Pattern NON_WORD = Pattern.compile("[^a-zA-Z\\s]");

    public static Map<String, Integer> repeatedWordsMoreThan2(List<String> headers) {
        Map<String, Integer> freq = new HashMap<>();

        for (String h : headers) {
            if (h == null) continue;

            String cleaned = NON_WORD.matcher(h.toLowerCase()).replaceAll(" ");

            //Use \\s+ to avoid empty tokens when there are multiple spaces
            for (String w : cleaned.split("\\s+")){
                if (w.isBlank()) continue;
                freq.put(w, freq.getOrDefault(w,0) + 1);
            }
        }

        // keep output sorted by most frequent words, makes results easier to read.
        Map<String, Integer> result = new LinkedHashMap<>();
        freq.entrySet().stream()
                .filter(e -> e.getValue() > 2)
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .forEach(e -> result.put(e.getKey(), e.getValue()));
        return result;
    }
}
