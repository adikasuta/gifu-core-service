package com.gifu.coreservice.utils;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class StringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 8;

    private static final Random random = new Random();
    public static String toDigits(long num, int digits){
        return String.format("%0"+digits+"d", num);
    }

    public static <T> String toStringSeparatedWith(List<T> list, String separator){
        return list.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));
    }
    public static String generateReferralCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }

    public static String generateRandomNumericString(int length) {
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(random.nextInt(10));
        }
        return sb.toString();
    }
}
