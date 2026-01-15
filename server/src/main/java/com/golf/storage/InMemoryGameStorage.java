package com.golf.storage;

import com.golf.model.domain.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory storage for game instances
 */
@Component
public class InMemoryGameStorage {

    private static final Logger logger = LoggerFactory.getLogger(InMemoryGameStorage.class);

    private final Map<String, Game> games = new ConcurrentHashMap<>();

    /**
     * Save or update a game
     */
    public void save(Game game) {
        games.put(game.getGameId(), game);
        logger.debug("Saved game: {}", game.getGameId());
    }

    /**
     * Find a game by ID
     */
    public Optional<Game> findById(String gameId) {
        return Optional.ofNullable(games.get(gameId));
    }

    /**
     * Delete a game
     */
    public void delete(String gameId) {
        games.remove(gameId);
        logger.info("Deleted game: {}", gameId);
    }

    /**
     * Get count of active games
     */
    public int count() {
        return games.size();
    }

    /**
     * Clear all games (for testing)
     */
    public void clear() {
        games.clear();
        logger.warn("Cleared all games from storage");
    }
}
