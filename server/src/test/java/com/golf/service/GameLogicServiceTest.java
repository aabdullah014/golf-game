package com.golf.service;

import com.golf.exception.InvalidMoveException;
import com.golf.model.domain.Card;
import com.golf.model.domain.Game;
import com.golf.model.enums.GamePhase;
import com.golf.model.enums.Rank;
import com.golf.model.enums.Suit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for GameLogicService
 */
class GameLogicServiceTest {

    private GameLogicService gameLogicService;
    private Game game;

    @BeforeEach
    void setUp() {
        gameLogicService = new GameLogicService();
        game = new Game(2);
        game.dealInitialCards();
        game.startGame(); // Move to DRAW phase
    }

    @Test
    void testDrawFromDeck() {
        assertEquals(GamePhase.DRAW, game.getPhase());

        int initialDeckSize = game.getDeck().size();
        gameLogicService.drawFromDeck(game);

        assertNotNull(game.getDrawnCard());
        assertTrue(game.getDrawnCard().isFaceUp());
        assertEquals(GamePhase.AFTER_DRAW, game.getPhase());
        assertEquals(initialDeckSize - 1, game.getDeck().size());
    }

    @Test
    void testDrawFromDeckWrongPhase() {
        game.setPhase(GamePhase.AFTER_DRAW);

        assertThrows(InvalidMoveException.class, () -> {
            gameLogicService.drawFromDeck(game);
        });
    }

    @Test
    void testDrawFromDiscard() {
        // Add a card to discard pile
        Card discardCard = new Card(Suit.HEARTS, Rank.ACE);
        game.getDiscardPile().add(discardCard);

        gameLogicService.drawFromDiscard(game);

        assertNotNull(game.getDrawnCard());
        assertEquals(discardCard, game.getDrawnCard());
        assertEquals(GamePhase.AFTER_DRAW, game.getPhase());
        assertTrue(game.getDiscardPile().isEmpty());
    }

    @Test
    void testDrawFromEmptyDiscard() {
        assertTrue(game.getDiscardPile().isEmpty());

        assertThrows(InvalidMoveException.class, () -> {
            gameLogicService.drawFromDiscard(game);
        });
    }

    @Test
    void testSwapDrawnCard() {
        // Draw a card first
        gameLogicService.drawFromDeck(game);
        Card drawnCard = game.getDrawnCard();

        int initialHandSize = game.getCurrentPlayer().getHandSize();

        // Swap with first card in hand
        gameLogicService.swapDrawnCard(game, 0);

        assertNull(game.getDrawnCard());
        assertEquals(initialHandSize, game.getCurrentPlayer().getHandSize());
        assertFalse(game.getDiscardPile().isEmpty());
    }

    @Test
    void testSwapDrawnCardInvalidIndex() {
        gameLogicService.drawFromDeck(game);

        assertThrows(InvalidMoveException.class, () -> {
            gameLogicService.swapDrawnCard(game, 10); // Invalid index
        });
    }

    @Test
    void testDiscardDrawnCard() {
        gameLogicService.drawFromDeck(game);
        Card drawnCard = game.getDrawnCard();

        gameLogicService.discardDrawnCard(game);

        assertNull(game.getDrawnCard());
        assertFalse(game.getDiscardPile().isEmpty());
        assertEquals(drawnCard, game.getDiscardPile().get(game.getDiscardPile().size() - 1));
    }

    @Test
    void testMatchCard() {
        // Set up a matching scenario
        Card matchCard = new Card(Suit.HEARTS, Rank.FIVE);
        game.getDiscardPile().add(matchCard);

        Card playerCard = new Card(Suit.CLUBS, Rank.FIVE); // Same rank
        game.getCurrentPlayer().getHand().set(0, playerCard);

        int initialHandSize = game.getCurrentPlayer().getHandSize();

        gameLogicService.matchCard(game, 0, 0, 0);

        assertEquals(initialHandSize - 1, game.getCurrentPlayer().getHandSize());
        assertEquals(2, game.getDiscardPile().size());
    }

    @Test
    void testMatchCardWrongRank() {
        Card discardCard = new Card(Suit.HEARTS, Rank.FIVE);
        game.getDiscardPile().add(discardCard);

        Card playerCard = new Card(Suit.CLUBS, Rank.THREE); // Different rank
        game.getCurrentPlayer().getHand().set(0, playerCard);


        int initialHandSize = game.getCurrentPlayer().getHandSize();

        gameLogicService.matchCard(game, 0, 0, 0);

        assertEquals(initialHandSize + 1, game.getCurrentPlayer().getHandSize());
        assertEquals(1, game.getDiscardPile().size());
    }

    @Test
    void testMatchCardWrongRankOtherPlayer() {
        Card discardCard = new Card(Suit.HEARTS, Rank.FIVE);
        game.getDiscardPile().add(discardCard);

        Card playerCard = new Card(Suit.CLUBS, Rank.THREE); // Different rank
        game.getCurrentPlayer().getHand().set(0, playerCard);


        int initialHandSize = game.getCurrentPlayer().getHandSize();

        gameLogicService.matchCard(game, 0, 1, 0);

        assertEquals(initialHandSize + 2, game.getCurrentPlayer().getHandSize());
        assertEquals(1, game.getDiscardPile().size());

        assertEquals(initialHandSize - 1, game.getPlayers().get(1).getHandSize());
    }

    @Test
    void testCallGolf() {
        assertFalse(game.isGolfCalled());
        assertFalse(game.isFinalRound());

        gameLogicService.callGolf(game);

        assertTrue(game.isGolfCalled());
        assertTrue(game.isFinalRound());
    }

    @Test
    void testCallGolfWrongPhase() {
        game.setPhase(GamePhase.AFTER_DRAW);

        assertThrows(InvalidMoveException.class, () -> {
            gameLogicService.callGolf(game);
        });
    }

    @Test
    void testPeekOwnCard() {
        // Swap a card with action card (7)
        Card actionCard = new Card(Suit.HEARTS, Rank.SEVEN);
        game.setDrawnCard(actionCard);
        game.setPhase(GamePhase.AFTER_DRAW);

        gameLogicService.discardDrawnCard(game);

        // Should be in ACTION phase now
        assertEquals(GamePhase.ACTION, game.getPhase());

        // Peek at a card
        gameLogicService.peekOwnCard(game, 1);

        assertTrue(game.getPlayers().get(0).hasPeekedAt(1));
    }
}
