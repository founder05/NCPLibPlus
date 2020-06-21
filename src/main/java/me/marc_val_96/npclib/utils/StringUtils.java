package me.marc_val_96.npclib.utils;

import java.security.SecureRandom;
import java.util.Random;

public class StringUtils {

    private static final Random RANDOM = new SecureRandom();
    private static final char[] CHARS =
        "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static String getRandomString() {
        return generateNumbers(CHARS.length);
    }

    private static String generateNumbers(int maxIndex) {
        StringBuilder sb = new StringBuilder(9);
        for (int i = 0; i < 9; ++i) {
            sb.append(CHARS[RANDOM.nextInt(maxIndex)]);
        }
        return sb.toString();
    }

}
