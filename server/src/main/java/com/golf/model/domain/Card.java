package com.golf.model.domain;

import com.golf.model.enums.Rank;
import com.golf.model.enums.Suit;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents a playing card in the Golf card game
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Card {

    private static final Logger logger = LoggerFactory.getLogger(Card.class);

    private Suit suit;
    private Rank rank;
    private boolean faceUp;

    public Card(Suit suit, Rank rank) {
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
        logger.debug("Created card: {}{}", rank, suit);
    }

    /**
     * Calculate the point value of this card for scoring
     * 
     * Special rules:
     * - Red King (Hearts/Diamonds) = -1
     * - Black King (Clubs/Spades) = 13
     * - Other cards = base value (A=1, 2-10=face, J=11, Q=12)
     */
    public int getValue() {
        if (rank == Rank.KING) {
            int value = suit.isRed() ? -1 : 13;
            logger.trace("King value: {} for {}{}", value, rank, suit);
            return value;
        }
        return rank.getBaseValue();
    }

    /**
     * Check if this card is an action card (7-Q)
     */
    public boolean isActionCard() {
        return rank.isActionCard();
    }

    /**
     * Flip the card face up or face down
     */
    public void flip() {
        this.faceUp = !this.faceUp;
        logger.debug("Flipped card {}{} to {}", rank, suit, faceUp ? "face up" : "face down");
    }

    /**
     * Set card face up state
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
        logger.trace("Set card {}{} to {}", rank, suit, faceUp ? "face up" : "face down");
    }

    @Override
    public String toString() {
        return rank.toString() + suit.toString();
    }
}
