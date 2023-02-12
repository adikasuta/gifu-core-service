package com.gifu.coreservice.utils;

public class StringUtils {
    public static String toDigits(long num, int digits){
        return String.format("%0"+digits+"d", num);
    }
}
