package ru.mirea.lang.Roman;

import java.util.List;

public class RomanParser {
    private final List<RomanToken> tokens;
    private int pos = 0;

    public RomanParser(List<RomanToken> tokens) {
        this.tokens = tokens;
    }

    public static int eval(List<RomanToken> tokens) {
        int low = 0;
        int high = 0;
        for (RomanToken t : tokens) {
            if (t.type == RomanTokenType.TEN) {
                high++;
            } else {
                low = t.type.value;
            }
        }
        return high * 10 + low;
    }

    public static int getDecimal(String text) {
        RomanLexer l = new RomanLexer(text);
        List<RomanToken> tokens = l.lex();

        RomanParser p = new RomanParser(tokens);

        return p.eval(tokens);
    }
}
