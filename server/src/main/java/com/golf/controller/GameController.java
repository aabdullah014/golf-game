package com.golf.controller;

import com.golf.model.domain.Game;
import com.golf.model.dto.CreateGameRequest;
import com.golf.model.dto.GameStateResponse;
import com.golf.model.dto.PlayerActionRequest;
import com.golf.service.GameLogicService;
import com.golf.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for Golf card game operations
 */
@RestController
@RequestMapping("/api/games")
public class GameController {

    private static final Logger logger = LoggerFactory.getLogger(GameController.class);

    private final GameService gameService;
    private final GameLogicService gameLogicService;

    @Autowired
    public GameController(GameService gameService, GameLogicService gameLogicService) {
        this.gameService = gameService;
        this.gameLogicService = gameLogicService;
    }

    /**
     * Create a new game
     * POST /api/games
     */
    @PostMapping
    public ResponseEntity<GameStateResponse> createGame(@RequestBody CreateGameRequest request) {
        logger.info("POST /api/games - Creating game with {} players", request.getNumPlayers());

        Game game = gameService.createGame(request.getNumPlayers());
        GameStateResponse response = GameStateResponse.fromGame(game);

        logger.info("Created game: {}", game.getGameId());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    /**
     * Get game state
     * GET /api/games/{gameId}
     */
    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateResponse> getGame(@PathVariable String gameId) {
        logger.debug("GET /api/games/{}", gameId);

        Game game = gameService.getGame(gameId);
        GameStateResponse response = GameStateResponse.fromGame(game);

        return ResponseEntity.ok(response);
    }

    /**
     * Delete a game
     * DELETE /api/games/{gameId}
     */
    @DeleteMapping("/{gameId}")
    public ResponseEntity<Void> deleteGame(@PathVariable String gameId) {
        logger.info("DELETE /api/games/{}", gameId);

        gameService.deleteGame(gameId);

        return ResponseEntity.noContent().build();
    }

    /**
     * Start the game (transition from INITIAL_PEEK to DRAW)
     * POST /api/games/{gameId}/start
     */
    @PostMapping("/{gameId}/start")
    public ResponseEntity<GameStateResponse> startGame(@PathVariable String gameId) {
        logger.info("POST /api/games/{}/start", gameId);

        Game game = gameService.getGame(gameId);
        game.startGame();
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Draw card from deck
     * POST /api/games/{gameId}/draw-deck
     */
    @PostMapping("/{gameId}/draw-deck")
    public ResponseEntity<GameStateResponse> drawFromDeck(@PathVariable String gameId) {
        logger.info("POST /api/games/{}/draw-deck", gameId);

        Game game = gameService.getGame(gameId);
        gameLogicService.drawFromDeck(game);
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Draw card from discard pile
     * POST /api/games/{gameId}/draw-discard
     */
    @PostMapping("/{gameId}/draw-discard")
    public ResponseEntity<GameStateResponse> drawFromDiscard(@PathVariable String gameId) {
        logger.info("POST /api/games/{}/draw-discard", gameId);

        Game game = gameService.getGame(gameId);
        gameLogicService.drawFromDiscard(game);
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Swap drawn card with hand card
     * POST /api/games/{gameId}/swap
     * Body: { "cardIndex": 2 }
     */
    @PostMapping("/{gameId}/swap")
    public ResponseEntity<GameStateResponse> swapCard(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/swap - cardIndex: {}", gameId, request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.swapCard(game, request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Discard the drawn card
     * POST /api/games/{gameId}/discard-drawn
     */
    @PostMapping("/{gameId}/discard-drawn")
    public ResponseEntity<GameStateResponse> discardDrawn(@PathVariable String gameId) {
        logger.info("POST /api/games/{}/discard-drawn", gameId);

        Game game = gameService.getGame(gameId);
        gameLogicService.discardDrawnCard(game);
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Match a card with the discard pile
     * POST /api/games/{gameId}/match
     * Body: { "playerIndex": 0, "cardIndex": 1 }
     */
    @PostMapping("/{gameId}/match")
    public ResponseEntity<GameStateResponse> matchCard(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/match - player: {}, cardIndex: {}",
                gameId, request.getPlayerIndex(), request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.matchCard(game, request.getPlayerIndex(), request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Call GOLF
     * POST /api/games/{gameId}/call-golf
     */
    @PostMapping("/{gameId}/call-golf")
    public ResponseEntity<GameStateResponse> callGolf(@PathVariable String gameId) {
        logger.info("POST /api/games/{}/call-golf", gameId);

        Game game = gameService.getGame(gameId);
        gameLogicService.callGolf(game);
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Action: Peek at own card (7 or 8)
     * POST /api/games/{gameId}/action/peek-own
     * Body: { "cardIndex": 0 }
     */
    @PostMapping("/{gameId}/action/peek-own")
    public ResponseEntity<GameStateResponse> peekOwnCard(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/action/peek-own - cardIndex: {}",
                gameId, request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.peekOwnCard(game, request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Action: Peek at opponent's card (9 or 10)
     * POST /api/games/{gameId}/action/peek-opponent
     * Body: { "opponentIndex": 1, "cardIndex": 0 }
     */
    @PostMapping("/{gameId}/action/peek-opponent")
    public ResponseEntity<GameStateResponse> peekOpponentCard(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/action/peek-opponent - opponent: {}, cardIndex: {}",
                gameId, request.getOpponentIndex(), request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.peekOpponentCard(game, request.getOpponentIndex(), request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Action: Jack blind swap - step 1
     * POST /api/games/{gameId}/action/blind-swap-1
     * Body: { "cardIndex": 0 }
     */
    @PostMapping("/{gameId}/action/blind-swap")
    public ResponseEntity<GameStateResponse> blindSwap(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/action/blind-swap-1 - cardIndex: {}",
                gameId, request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.blindSwap(game, request.getCardIndex(), request.getOpponentIndex(), request.getOpponentCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Action: Queen peek
     * POST /api/games/{gameId}/action/queen-peek
     * Body: { "opponentIndex": 1, "cardIndex": 0 }
     */
    @PostMapping("/{gameId}/action/queen-peek")
    public ResponseEntity<GameStateResponse> queenPeek(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/action/queen-peek - opponent: {}, cardIndex: {}",
                gameId, request.getOpponentIndex(), request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.queenPeek(game, request.getOpponentIndex(), request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }

    /**
     * Action: Queen swap
     * POST /api/games/{gameId}/action/queen-swap
     * Body: { "playerIndex": 0, "cardIndex": 1 }
     */
    @PostMapping("/{gameId}/action/queen-swap")
    public ResponseEntity<GameStateResponse> queenSwap(
            @PathVariable String gameId,
            @RequestBody PlayerActionRequest request) {
        logger.info("POST /api/games/{}/action/queen-swap - player: {}, cardIndex: {}",
                gameId, request.getPlayerIndex(), request.getCardIndex());

        Game game = gameService.getGame(gameId);
        gameLogicService.queenSwap(game, request.getPlayerIndex(), request.getCardIndex());
        gameService.updateGame(game);

        return ResponseEntity.ok(GameStateResponse.fromGame(game));
    }
}
