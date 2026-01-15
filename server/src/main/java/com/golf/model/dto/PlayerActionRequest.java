package com.golf.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Request for player actions requiring card index
 */
@Data
public class PlayerActionRequest {

    private int playerIndex;
    private int cardIndex;
    private Integer opponentIndex; // Optional for some actions
    private Integer opponentCardIndex; // Optional for some actions

    @JsonCreator
    public PlayerActionRequest(
            @JsonProperty("playerIndex") int playerIndex,
            @JsonProperty("cardIndex") int cardIndex,
            @JsonProperty("opponentIndex") Integer opponentIndex,
            @JsonProperty("opponentCardIndex") Integer opponentCardIndex) {
        this.playerIndex = playerIndex;
        this.cardIndex = cardIndex;
        this.opponentIndex = opponentIndex;
        this.opponentCardIndex = opponentCardIndex;
    }

    public PlayerActionRequest(int playerIndex, int cardIndex) {
        this.playerIndex = playerIndex;
        this.cardIndex = cardIndex;
    }
}
