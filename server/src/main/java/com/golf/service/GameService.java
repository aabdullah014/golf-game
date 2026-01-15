package com.golf.service;

import com.golf.exception.GameNotFoundException;
import com.golf.model.domain.Game;
import com.golf.storage.InMemoryGameStorage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Service for managing game lifecycle
 */
@Service
public class GameService {

    private static final Logger logger = LoggerFactory.getLogger(GameService.class);

    private final InMemoryGameStorage storage;

    @Autowired
    public GameService(InMemoryGameStorage storage) {
        this.storage = storage;
    }

    /**
     * Create a new game with specified number of players
     */
    public Game createGame(int numPlayers) {
        logger.info("Creating new game with {} players", numPlayers);

        if (numPlayers < 2 || numPlayers > 6) {
            throw new IllegalArgumentException("Number of players must be between 2 and 6");
        }

        Game game = new Game(numPlayers);
        game.dealInitialCards();
        storage.save(game);

        logger.info("Created game: {} with {} players", game.getGameId(), numPlayers);
        return game;
    }

    /**
     * Get a game by ID
     */
    public Game getGame(String gameId) {
        return storage.findById(gameId)
                .orElseThrow(() -> {
                    logger.warn("Game not found: {}", gameId);
                    return new GameNotFoundException(gameId);
                });
    }

    /**
     * Delete a game
     */
    public void deleteGame(String gameId) {
        logger.info("Deleting game: {}", gameId);
        storage.delete(gameId);
    }

    /**
     * Update game state after modifications
     */
    public void updateGame(Game game) {
        storage.save(game);
        logger.trace("Updated game: {}", game.getGameId());
    }
}
