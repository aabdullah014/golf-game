package com.golf.model.enums;

import lombok.Getter;

/**
 * Card suits in a standard deck
 */
@Getter
public enum Suit {
    HEARTS("♥", true),
    DIAMONDS("♦", true),
    CLUBS("♣", false),
    SPADES("♠", false);

    private final String symbol;
    private final boolean isRed;

    Suit(String symbol, boolean isRed) {
        this.symbol = symbol;
        this.isRed = isRed;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
