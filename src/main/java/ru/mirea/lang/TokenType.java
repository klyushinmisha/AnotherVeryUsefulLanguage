package ru.mirea.lang;

import java.util.regex.Pattern;

public enum TokenType {
    NUMBER("[X]{0,}(I|II|III|IV|V|VI|VII|VIII|IX|X){1,}"),
    ASSIGN(":="),
    WHILE("while"),
    DONE("done"),
    DO("do"),
    INC("\\+\\+"),
    DEC("\\-\\-"),
    SEMICOLON(";"),
    LS("<"),
    EQ("="),
    GR(">"),
    PRINT("print"),
    SPACE("[ \t\r\n]+"),
    ID("[a-z_][a-z_0-9]*");

    final Pattern pattern;

    TokenType(String regexp) {
        pattern = Pattern.compile(regexp);
    }
}
