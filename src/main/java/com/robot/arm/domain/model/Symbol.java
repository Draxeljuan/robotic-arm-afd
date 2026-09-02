package com.robot.arm.domain.model;

import java.util.Optional;

public enum Symbol {
    U('U'),
    D('D'),
    L('L'),
    R('R'),
    PLUS('+'),
    MINUS('-');

    private final char character;

    Symbol(char character) {
        this.character = character;
    }

    public char getCharacter() {
        return character;
    }

    public static Optional<Symbol> fromChar(char c) {
        for (Symbol s : values()) {
            if (s.character == c) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}