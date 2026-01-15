package com.golf.model.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Represents a player in the Golf card game
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Player {

    private static final Logger logger = LoggerFactory.getLogger(Player.class);

    private String id;
    private String name;
    private List<Card> hand;
    private int score;
    private Set<Integer> peekedCardIndices; // Indices of cards the player has seen

    public Player(String id, String name) {
        this.id = id;
        this.name = name;
        this.hand = new ArrayList<>();
        this.score = 0;
        this.peekedCardIndices = new HashSet<>();
        logger.info("Created player: {} (ID: {})", name, id);
    }

    /**
     * Add a card to the player's hand
     */
    public void addCard(Card card) {
        hand.add(card);
        logger.debug("Player {} received card: {}", name, card);
    }

    /**
     * Remove a card at the specified index
     */
    public Card removeCard(int index) {
        if (index < 0 || index >= hand.size()) {
            logger.warn("Invalid card index: {} for player {}", index, name);
            return null;
        }
        Card removed = hand.remove(index);
        logger.debug("Player {} discarded card: {}", name, removed);
        return removed;
    }

    /**
     * Get a card at the specified index
     */
    public Card getCard(int index) {
        if (index < 0 || index >= hand.size()) {
            logger.warn("Invalid card index: {} for player {}", index, name);
            return null;
        }
        return hand.get(index);
    }

    /**
     * Calculate current score based on hand
     */
    public int calculateScore() {
        int total = hand.stream()
                .mapToInt(Card::getValue)
                .sum();
        this.score = total;
        logger.debug("Player {} score calculated: {}", name, score);
        return score;
    }

    /**
     * Mark a card as peeked
     */
    public void markCardAsPeeked(int index) {
        peekedCardIndices.add(index);
        logger.trace("Player {} peeked at card index {}", name, index);
    }

    /**
     * Check if player has peeked at a specific card
     */
    public boolean hasPeekedAt(int index) {
        return peekedCardIndices.contains(index);
    }

    /**
     * Get number of cards in hand
     */
    public int getHandSize() {
        return hand.size();
    }

    @Override
    public String toString() {
        return name + " (" + id + ") - " + hand.size() + " cards, score: " + score;
    }
}
