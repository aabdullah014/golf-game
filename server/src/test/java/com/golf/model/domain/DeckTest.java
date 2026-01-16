package com.golf.model.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Deck class
 */
class DeckTest {

    @Test
    void testDeckCreation() {
        Deck deck = new Deck();

        assertEquals(52, deck.size());
        assertFalse(deck.isEmpty());
    }

    @Test
    void testShuffle() {
        Deck deck = new Deck();

        // Shuffle shouldn't change size
        deck.shuffle();
        assertEquals(52, deck.size());
    }

    @Test
    void testDraw() {
        Deck deck = new Deck();

        Card card = deck.draw();
        assertNotNull(card);
        assertEquals(51, deck.size());
    }

    @Test
    void testDrawUntilEmpty() {
        Deck deck = new Deck();

        // Draw all 52 cards
        for (int i = 0; i < 52; i++) {
            Card card = deck.draw();
            assertNotNull(card);
        }

        assertTrue(deck.isEmpty());
        assertEquals(0, deck.size());

        // Drawing from empty deck should return null
        Card emptyDraw = deck.draw();
        assertNull(emptyDraw);
    }

    @Test
    void testPeek() {
        Deck deck = new Deck();

        Card peeked = deck.peek();
        assertNotNull(peeked);

        // Peek shouldn't change size
        assertEquals(52, deck.size());

        // Drawing should give same card as peek
        Card drawn = deck.draw();
        assertEquals(peeked, drawn);
    }

    @Test
    void testPeekEmptyDeck() {
        Deck deck = new Deck();

        // Empty the deck
        while (!deck.isEmpty()) {
            deck.draw();
        }

        assertNull(deck.peek());
    }
}
