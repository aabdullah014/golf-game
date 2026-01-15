package com.golf.model.domain;

import com.golf.model.enums.Rank;
import com.golf.model.enums.Suit;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a deck of playing cards with shuffle and draw operations
 */
@Getter
public class Deck {

    private static final Logger logger = LoggerFactory.getLogger(Deck.class);

    private final List<Card> cards;

    /**
     * Create a standard 52-card deck
     */
    public Deck() {
        this.cards = new ArrayList<>();
        initializeDeck();
        logger.info("Created new deck with {} cards", cards.size());
    }

    /**
     * Initialize deck with all 52 cards
     */
    private void initializeDeck() {
        for (Suit suit : Suit.values()) {
            for (Rank rank : Rank.values()) {
                cards.add(new Card(suit, rank));
            }
        }
        logger.debug("Initialized deck with {} cards", cards.size());
    }

    /**
     * Shuffle the deck using Collections.shuffle
     */
    public void shuffle() {
        Collections.shuffle(cards);
        logger.info("Shuffled deck");
    }

    /**
     * Draw a card from the top of the deck
     * 
     * @return the drawn card, or null if deck is empty
     */
    public Card draw() {
        if (cards.isEmpty()) {
            logger.warn("Attempted to draw from empty deck");
            return null;
        }
        Card card = cards.remove(cards.size() - 1);
        logger.debug("Drew card: {} (remaining: {})", card, cards.size());
        return card;
    }

    /**
     * Get the number of cards remaining in the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Check if the deck is empty
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Peek at the top card without removing it
     */
    public Card peek() {
        if (cards.isEmpty()) {
            return null;
        }
        return cards.get(cards.size() - 1);
    }
}
