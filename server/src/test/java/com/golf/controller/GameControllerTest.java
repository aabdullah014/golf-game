package com.golf.controller;

import com.golf.model.dto.CreateGameRequest;
import com.golf.model.dto.GameStateResponse;
import com.golf.model.dto.PlayerActionRequest;
import com.golf.service.GameLogicService;
import com.golf.service.GameService;
import com.golf.storage.InMemoryGameStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests for GameController
 */
@SpringBootTest
class GameControllerTest {

    @Autowired
    private GameController gameController;

    @Autowired
    private InMemoryGameStorage storage;

    @BeforeEach
    void setUp() {
        storage.clear();
    }

    @Test
    void testCreateGame() {
        CreateGameRequest request = new CreateGameRequest(2);

        ResponseEntity<GameStateResponse> response = gameController.createGame(request);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals(2, response.getBody().getPlayers().size());
        assertNotNull(response.getBody().getGameId());
    }

    @Test
    void testGetGame() {
        // Create a game first
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();

        // Get the game
        ResponseEntity<GameStateResponse> getResponse = gameController.getGame(gameId);

        assertEquals(HttpStatus.OK, getResponse.getStatusCode());
        assertEquals(gameId, getResponse.getBody().getGameId());
    }

    @Test
    void testDeleteGame() {
        // Create a game
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();

        // Delete the game
        ResponseEntity<Void> deleteResponse = gameController.deleteGame(gameId);

        assertEquals(HttpStatus.NO_CONTENT, deleteResponse.getStatusCode());
        assertEquals(0, storage.count());
    }

    @Test
    void testStartGame() {
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();

        ResponseEntity<GameStateResponse> startResponse = gameController.startGame(gameId);

        assertEquals(HttpStatus.OK, startResponse.getStatusCode());
        assertEquals("DRAW", startResponse.getBody().getPhase().toString());
    }

    @Test
    void testDrawFromDeck() {
        // Create and start game
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();
        gameController.startGame(gameId);

        // Draw from deck
        ResponseEntity<GameStateResponse> drawResponse = gameController.drawFromDeck(gameId);

        assertEquals(HttpStatus.OK, drawResponse.getStatusCode());
        assertEquals("AFTER_DRAW", drawResponse.getBody().getPhase().toString());
        assertNotNull(drawResponse.getBody().getDrawnCard());
    }

    @Test
    void testSwapCard() {
        // Create, start, and draw
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();
        gameController.startGame(gameId);
        gameController.drawFromDeck(gameId);

        // Swap card at index 0
        PlayerActionRequest swapRequest = new PlayerActionRequest(0, 0);
        ResponseEntity<GameStateResponse> swapResponse = gameController.swapCard(gameId, swapRequest);

        assertEquals(HttpStatus.OK, swapResponse.getStatusCode());
        assertNull(swapResponse.getBody().getDrawnCard());
    }

    @Test
    void testCallGolf() {
        // Create and start game
        CreateGameRequest createRequest = new CreateGameRequest(2);
        ResponseEntity<GameStateResponse> createResponse = gameController.createGame(createRequest);
        String gameId = createResponse.getBody().getGameId();
        gameController.startGame(gameId);

        // Call GOLF
        ResponseEntity<GameStateResponse> golfResponse = gameController.callGolf(gameId);

        assertEquals(HttpStatus.OK, golfResponse.getStatusCode());
        assertTrue(golfResponse.getBody().isGolfCalled());
        assertTrue(golfResponse.getBody().isFinalRound());
    }
}
