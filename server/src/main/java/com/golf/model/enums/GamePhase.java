package com.golf.model.enums;

/**
 * Game phases for state machine management
 */
public enum GamePhase {
    INITIAL_PEEK, // Players peek at bottom two cards
    DRAW, // Player must draw from deck or discard
    AFTER_DRAW, // Player must swap or discard drawn card
    ACTION, // Executing action card (may require multiple steps)
    GAME_OVER // Game has ended
}
