package ru.mirea.lang.Roman;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

public class RomanLexer {
    private final String src;
    private int pos = 0;
    private final List<RomanToken> tokens = new ArrayList<>();

    public RomanLexer(String src) {
        this.src = src;
    }

    private boolean nextToken() {
        if (pos >= src.length())
            return false;
        for (RomanTokenType tt : RomanTokenType.values()) {
            Matcher m = tt.pattern.matcher(src);
            m.region(pos, src.length());
            if (m.lookingAt()) {
                RomanToken t = new RomanToken(tt, pos);
                tokens.add(t);
                pos = m.end();
                return true;
            }
        }
        throw new RuntimeException("Неожиданный символ " + src.charAt(pos) + " в позиции " + pos);
    }

    public List<RomanToken> lex() {
        while (nextToken()) {
            // do nothing
        }
        return tokens;
    }

    public static void main(String[] args) {
        String text = "XXXIVIVV";
        RomanLexer l = new RomanLexer(text);
        List<RomanToken> tokens = l.lex();
        for (RomanToken t : tokens) {
            System.out.println(t.type.value);
        }
    }
}
