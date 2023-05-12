package nettion.utils;

import java.util.*;

public class RandomUtils {
    public static Random random = new Random();
    public static String random(final int length, final String chars) {
        return random(length, chars.toCharArray());
    }

    public static String random(final int length, final char[] chars) {
        final StringBuilder stringBuilder = new StringBuilder();
        for(int i = 0; i < length; i++)
            stringBuilder.append(chars[new java.util.Random().nextInt(chars.length)]);
        return stringBuilder.toString();
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new java.util.Random().nextInt(endExclusive - startInclusive);
    }

    public static int nextInt(float startInclusive, float endExclusive) {
        return (int) (endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new java.util.Random().nextInt((int) (endExclusive - startInclusive)));
    }

    public static boolean nextBoolean() {
        return new java.util.Random().nextInt(100) >= 50;
    }
}
