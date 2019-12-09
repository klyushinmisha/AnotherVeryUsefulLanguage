package ru.mirea.lang.Roman;

import java.util.regex.Pattern;

public enum RomanTokenType {
    TEN("X", 10),
    NINE("IX", 9),
    EIGHT("VIII", 8),
    SEVEN("VII", 7),
    SIX("VI", 6),
    FIVE("V", 5),
    FOUR("IV", 4),
    THREE("III", 3),
    TWO("II", 2),
    ONE("I", 1);

    final Pattern pattern;
    int value;

    RomanTokenType(String regexp, int decValue) {
        pattern = Pattern.compile(regexp);
        value = decValue;
    }

    static RomanTokenType[] getValues() {
        return RomanTokenType.values();
    }
}
