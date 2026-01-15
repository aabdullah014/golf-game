package com.golf.model.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

/**
 * Request to create a new game
 */
@Data
public class CreateGameRequest {

    private int numPlayers;

    @JsonCreator
    public CreateGameRequest(@JsonProperty("numPlayers") int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
