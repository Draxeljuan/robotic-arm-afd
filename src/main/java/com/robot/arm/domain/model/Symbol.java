package com.robot.arm.domain.model;

import java.util.Optional;

/**
 * Alfabeto Σ = {U, D, L, R, +, -}.
 * U,D,L,R: movimientos cartesianos. +: tomar pieza. -: soltar pieza.
 */
public enum Symbol {
    UP('U'),
    DOWN('D'),
    LEFT('L'),
    RIGHT('R'),
    GRAB('+'),
    DROP('-');

    private final char character;

    Symbol(char character) {
        this.character = character;
    }

    public char getChar() {
        return character;
    }

    /**
     * Determina si un carácter pertenece a Σ. Si no pertenece, la cadena completa
     * será rechazada por el autómata ("la cadena no pertenece").
     */
    public static Optional<Symbol> fromChar(char c) {
        char upper = Character.toUpperCase(c);
        for (Symbol s : values()) {
            if (s.character == upper) {
                return Optional.of(s);
            }
        }
        return Optional.empty();
    }
}
