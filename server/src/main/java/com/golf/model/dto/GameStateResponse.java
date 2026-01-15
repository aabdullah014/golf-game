package com.golf.model.dto;

import com.golf.model.domain.Card;
import com.golf.model.domain.Game;
import com.golf.model.domain.Player;
import com.golf.model.enums.GamePhase;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Response containing complete game state
 */
@Data
public class GameStateResponse {

    private String gameId;
    private List<PlayerState> players;
    private int deckSize;
    private CardInfo topDiscard;
    private CardInfo drawnCard;
    private int currentPlayerIndex;
    private GamePhase phase;
    private boolean golfCalled;
    private boolean finalRound;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static GameStateResponse fromGame(Game game) {
        GameStateResponse response = new GameStateResponse();
        response.setGameId(game.getGameId());
        response.setPlayers(game.getPlayers().stream()
                .map(PlayerState::fromPlayer)
                .collect(Collectors.toList()));
        response.setDeckSize(game.getDeck().size());

        if (!game.getDiscardPile().isEmpty()) {
            Card topCard = game.getDiscardPile().get(game.getDiscardPile().size() - 1);
            response.setTopDiscard(CardInfo.fromCard(topCard));
        }

        if (game.getDrawnCard() != null) {
            response.setDrawnCard(CardInfo.fromCard(game.getDrawnCard()));
        }

        response.setCurrentPlayerIndex(game.getCurrentPlayerIndex());
        response.setPhase(game.getPhase());
        response.setGolfCalled(game.isGolfCalled());
        response.setFinalRound(game.isFinalRound());
        response.setCreatedAt(game.getCreatedAt());
        response.setUpdatedAt(game.getUpdatedAt());

        return response;
    }

    @Data
    public static class PlayerState {
        private String id;
        private String name;
        private List<CardInfo> hand;
        private int score;
        private int handSize;

        public static PlayerState fromPlayer(Player player) {
            PlayerState state = new PlayerState();
            state.setId(player.getId());
            state.setName(player.getName());
            state.setHand(player.getHand().stream()
                    .map(CardInfo::fromCard)
                    .collect(Collectors.toList()));
            state.setScore(player.getScore());
            state.setHandSize(player.getHandSize());
            return state;
        }
    }

    @Data
    public static class CardInfo {
        private String suit;
        private String rank;
        private boolean faceUp;
        private int value;

        public static CardInfo fromCard(Card card) {
            CardInfo info = new CardInfo();
            info.setSuit(card.getSuit().toString());
            info.setRank(card.getRank().toString());
            info.setFaceUp(card.isFaceUp());
            info.setValue(card.getValue());
            return info;
        }
    }
}
