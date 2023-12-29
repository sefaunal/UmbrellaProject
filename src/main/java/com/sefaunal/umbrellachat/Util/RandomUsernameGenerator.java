package com.sefaunal.umbrellachat.Util;

import java.util.Random;

/**
 * @author github.com/sefaunal
 * @since 2023-12-29
 */
public class RandomUsernameGenerator {
    private static final String ALPHABET = "abcdefghijklmnopqrstuvwxyz";

    private static final String NUMBERS = "0123456789";

    public static String generateRandomUsername() {
        Random random = new Random();
        StringBuilder sb = new StringBuilder();
        sb.append("user-");
        for (int i = 0; i < 19; i++) {
            int index;
            if (i % 2 == 0) {
                index = random.nextInt(ALPHABET.length());
                sb.append(ALPHABET.charAt(index));
            } else {
                index = random.nextInt(NUMBERS.length());
                sb.append(NUMBERS.charAt(index));
            }
        }
        return sb.toString();
    }
}
