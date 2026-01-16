package com.golf.model.domain;

import com.golf.model.enums.GamePhase;
import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Represents a Golf card game instance with full state management
 */
@Data
public class Game {

    private static final Logger logger = LoggerFactory.getLogger(Game.class);

    private String gameId;
    private List<Player> players;
    private Deck deck;
    private List<Card> discardPile;
    private int currentPlayerIndex;
    private GamePhase phase;
    private Card drawnCard;
    private Card lastDiscardedCard;
    private ActionContext actionContext;
    private boolean golfCalled;
    private boolean finalRound;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    /**
     * Create a new game with specified number of players
     */
    public Game(int numPlayers) {
        this.gameId = UUID.randomUUID().toString();
        this.players = new ArrayList<>();
        this.deck = new Deck();
        this.discardPile = new ArrayList<>();
        this.currentPlayerIndex = 0;
        this.phase = GamePhase.INITIAL_PEEK;
        this.golfCalled = false;
        this.finalRound = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        logger.info("Created new game: {} with {} players", gameId, numPlayers);

        // Initialize players
        for (int i = 0; i < numPlayers; i++) {
            String playerId = UUID.randomUUID().toString();
            String playerName = "Player " + (i + 1);
            players.add(new Player(playerId, playerName));
        }
    }

    /**
     * Deal initial cards to all players (4 cards each)
     */
    public void dealInitialCards() {
        logger.info("Dealing initial cards for game {}", gameId);
        deck.shuffle();

        for (int i = 0; i < 4; i++) {
            for (Player player : players) {
                Card card = deck.draw();
                if (card != null) {
                    player.addCard(card);
                    if (i > 1) {
                        card.setFaceUp(true); // Bottom 2 cards face up
                    }
                }
            }
        }

        logger.info("Dealt 4 cards to each of {} players", players.size());
        for (Player player : players) {
            logger.info("{}'s top 2 cards: {} , {}, the bottom 2: {} , {}",
                    player.getName(),
                    player.getHand().get(0),
                    player.getHand().get(1),
                    player.getHand().get(2),
                    player.getHand().get(3));
        }
    }

    /**
     * Get the current player
     */
    public Player getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * Advance to the next player's turn
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
        phase = GamePhase.DRAW;
        updatedAt = LocalDateTime.now();

        logger.info("Advanced to player {}'s turn", getCurrentPlayer().getName());
    }

    /**
     * Start the game (transition from INITIAL_PEEK to DRAW)
     */
    public void startGame() {
        phase = GamePhase.DRAW;
        updatedAt = LocalDateTime.now();
        for (Player player : players) {
            player.getHand().get(2).setFaceUp(false);
            player.getHand().get(3).setFaceUp(false);
        }
        logger.info("Game {} started", gameId);
    }

    /**
     * Call GOLF to trigger the final round
     */
    public void callGolf() {
        this.golfCalled = true;
        this.finalRound = true;
        updatedAt = LocalDateTime.now();
        logger.info("GOLF called in game {} by player {}", gameId, getCurrentPlayer().getName());
    }

    /**
     * End the game and calculate final scores
     */
    public void endGame() {
        phase = GamePhase.GAME_OVER;

        // Flip all cards face up
        for (Player player : players) {
            for (Card card : player.getHand()) {
                card.setFaceUp(true);
            }
            player.calculateScore();
        }

        updatedAt = LocalDateTime.now();
        logger.info("Game {} ended", gameId);
        logFinalScores();
    }

    /**
     * Log final scores for all players
     */
    private void logFinalScores() {
        logger.info("=== Final Scores for Game {} ===", gameId);
        for (Player player : players) {
            logger.info("{}: {} points", player.getName(), player.getScore());
        }
    }

    /**
     * Get the winner (player with lowest score)
     */
    public Player getWinner() {
        return players.stream()
                .min((p1, p2) -> Integer.compare(p1.getScore(), p2.getScore()))
                .orElse(null);
    }

    /**
     * Check if final round is complete
     */
    public boolean isFinalRoundComplete() {
        return finalRound && currentPlayerIndex == players.size() - 1;
    }

    /**
     * Action context for multi-step action cards (Jack, Queen)
     */
    @Data
    public static class ActionContext {
        private Card actionCard;
        private int step; // Current step in multi-step action
        private List<CardSelection> selections; // Cards selected during action

        public ActionContext(Card actionCard) {
            this.actionCard = actionCard;
            this.step = 0;
            this.selections = new ArrayList<>();
        }

        @Data
        public static class CardSelection {
            private int playerIndex;
            private int cardIndex;
            private Card card;
        }
    }

    @Override
    public String toString() {
        return String.format("Game[%s] - Phase: %s, Player: %d/%d, Cards in deck: %d",
                gameId.substring(0, 8), phase, currentPlayerIndex + 1, players.size(), deck.size());
    }
}
