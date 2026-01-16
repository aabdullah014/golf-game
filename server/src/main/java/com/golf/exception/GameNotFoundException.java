package com.golf.exception;

/**
 * Exception thrown when a game is not found
 */
public class GameNotFoundException extends RuntimeException {
    public GameNotFoundException(String gameId) {
        super("Game not found: " + gameId);
    }
}
