package com.susu.processclient;

public class StringUtils {

    public static String truncateTo64KConservative(String input) {
        if (input == null) return null;

        // 保守估计每个字符最多占4字节(UTF-8最坏情况)
        int maxChars = 20000; // 65535/4 ≈ 16383

        if (input.length() <= maxChars) {
            return input;
        }

        return input.substring(0, maxChars);
    }
}
