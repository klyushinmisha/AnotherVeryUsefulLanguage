package ru.mirea.lang.Roman;

public class RomanToken {

    public final RomanTokenType type;
    public final int pos;

    public RomanToken(RomanTokenType type, int pos) {
        this.type = type;
        this.pos = pos;
    }

    @Override
    public String toString() {
        return String.valueOf(type.value);
    }
}
