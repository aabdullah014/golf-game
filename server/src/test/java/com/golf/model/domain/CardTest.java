package com.golf.model.domain;

import com.golf.model.enums.Rank;
import com.golf.model.enums.Suit;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for Card class
 */
class CardTest {

    @Test
    void testCardCreation() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);

        assertEquals(Suit.HEARTS, card.getSuit());
        assertEquals(Rank.ACE, card.getRank());
        assertFalse(card.isFaceUp());
    }

    @Test
    void testRedKingValue() {
        // Red Kings should be -1
        Card redKingHearts = new Card(Suit.HEARTS, Rank.KING);
        assertEquals(-1, redKingHearts.getValue());

        Card redKingDiamonds = new Card(Suit.DIAMONDS, Rank.KING);
        assertEquals(-1, redKingDiamonds.getValue());
    }

    @Test
    void testBlackKingValue() {
        // Black Kings should be 13
        Card blackKingClubs = new Card(Suit.CLUBS, Rank.KING);
        assertEquals(13, blackKingClubs.getValue());

        Card blackKingSpades = new Card(Suit.SPADES, Rank.KING);
        assertEquals(13, blackKingSpades.getValue());
    }

    @Test
    void testNumberCardValues() {
        Card ace = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals(1, ace.getValue());

        Card five = new Card(Suit.CLUBS, Rank.FIVE);
        assertEquals(5, five.getValue());

        Card ten = new Card(Suit.SPADES, Rank.TEN);
        assertEquals(10, ten.getValue());
    }

    @Test
    void testActionCards() {
        assertTrue(new Card(Suit.HEARTS, Rank.SEVEN).isActionCard());
        assertTrue(new Card(Suit.HEARTS, Rank.EIGHT).isActionCard());
        assertTrue(new Card(Suit.HEARTS, Rank.NINE).isActionCard());
        assertTrue(new Card(Suit.HEARTS, Rank.TEN).isActionCard());
        assertTrue(new Card(Suit.HEARTS, Rank.JACK).isActionCard());
        assertTrue(new Card(Suit.HEARTS, Rank.QUEEN).isActionCard());

        assertFalse(new Card(Suit.HEARTS, Rank.ACE).isActionCard());
        assertFalse(new Card(Suit.HEARTS, Rank.TWO).isActionCard());
        assertFalse(new Card(Suit.HEARTS, Rank.KING).isActionCard());
    }

    @Test
    void testFlip() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertFalse(card.isFaceUp());

        card.flip();
        assertTrue(card.isFaceUp());

        card.flip();
        assertFalse(card.isFaceUp());
    }

    @Test
    void testSetFaceUp() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);

        card.setFaceUp(true);
        assertTrue(card.isFaceUp());

        card.setFaceUp(false);
        assertFalse(card.isFaceUp());
    }

    @Test
    void testToString() {
        Card card = new Card(Suit.HEARTS, Rank.ACE);
        assertEquals("A♥", card.toString());

        Card king = new Card(Suit.SPADES, Rank.KING);
        assertEquals("K♠", king.toString());
    }
}
