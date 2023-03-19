package com.gifu.coreservice.utils;

import java.util.Random;

public class StringUtils {

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int LENGTH = 8;

    private static final Random random = new Random();
    public static String toDigits(long num, int digits){
        return String.format("%0"+digits+"d", num);
    }

    public static String generateReferralCode() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
        }
        return sb.toString();
    }
}
