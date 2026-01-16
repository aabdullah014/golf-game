package com.golf.model.enums;

import lombok.Getter;

/**
 * Card ranks in a standard deck with Golf game values
 */
@Getter
public enum Rank {
    ACE("A", 1, false),
    TWO("2", 2, false),
    THREE("3", 3, false),
    FOUR("4", 4, false),
    FIVE("5", 5, false),
    SIX("6", 6, false),
    SEVEN("7", 7, true), // Action: peek at own card
    EIGHT("8", 8, true), // Action: peek at own card
    NINE("9", 9, true), // Action: peek at opponent's card
    TEN("10", 10, true), // Action: peek at opponent's card
    JACK("J", 11, true), // Action: blind swap
    QUEEN("Q", 12, true), // Action: look and swap
    KING("K", 13, false); // Special: -1 if red, 13 if black

    private final String symbol;
    private final int baseValue;
    private final boolean isActionCard;

    Rank(String symbol, int baseValue, boolean isActionCard) {
        this.symbol = symbol;
        this.baseValue = baseValue;
        this.isActionCard = isActionCard;
    }

    @Override
    public String toString() {
        return symbol;
    }
}
