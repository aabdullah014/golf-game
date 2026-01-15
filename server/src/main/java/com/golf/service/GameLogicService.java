package com.golf.service;

import com.golf.exception.InvalidMoveException;
import com.golf.model.domain.Card;
import com.golf.model.domain.Game;
import com.golf.model.domain.Player;
import com.golf.model.enums.GamePhase;
import com.golf.model.enums.Rank;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Service containing all Golf card game logic and rules
 */
@Service
public class GameLogicService {

    private static final Logger logger = LoggerFactory.getLogger(GameLogicService.class);

    /**
     * Draw a card from the deck
     */
    public void drawFromDeck(Game game) {
        logger.info("Player {} drawing from deck in game {}",
                game.getCurrentPlayer().getName(), game.getGameId());

        validatePhase(game, GamePhase.DRAW, "Can only draw during DRAW phase");

        if (game.getDeck().isEmpty()) {
            throw new InvalidMoveException("Deck is empty");
        }

        Card card = game.getDeck().draw();
        card.setFaceUp(true);
        game.setDrawnCard(card);
        game.setPhase(GamePhase.AFTER_DRAW);

        logger.debug("Drew card: {}", card);
    }

    /**
     * Draw a card from the discard pile
     */
    public void drawFromDiscard(Game game) {
        logger.info("Player {} drawing from discard pile in game {}",
                game.getCurrentPlayer().getName(), game.getGameId());

        validatePhase(game, GamePhase.DRAW, "Can only draw during DRAW phase");

        if (game.getDiscardPile().isEmpty()) {
            throw new InvalidMoveException("Discard pile is empty");
        }

        Card card = game.getDiscardPile().remove(game.getDiscardPile().size() - 1);
        card.setFaceUp(true);
        game.setDrawnCard(card);
        game.setPhase(GamePhase.AFTER_DRAW);

        logger.debug("Drew card from discard: {}", card);
    }

    /**
     * Swap the drawn card with a card from the player's hand
     */
    public void swapCard(Game game, int cardIndex) {
        logger.info("Player {} swapping card at index {} in game {}",
                game.getCurrentPlayer().getName(), cardIndex, game.getGameId());

        validatePhase(game, GamePhase.AFTER_DRAW, "Can only swap during AFTER_DRAW phase");

        if (game.getDrawnCard() == null) {
            throw new InvalidMoveException("No drawn card to swap");
        }

        Player currentPlayer = game.getCurrentPlayer();

        if (cardIndex < 0 || cardIndex >= currentPlayer.getHandSize()) {
            throw new InvalidMoveException("Invalid card index: " + cardIndex);
        }

        // Remove card from hand and add to discard
        Card discardedCard = currentPlayer.removeCard(cardIndex);
        discardedCard.setFaceUp(true);
        game.getDiscardPile().add(discardedCard);

        // Add drawn card to hand (face down)
        Card drawnCard = game.getDrawnCard();
        drawnCard.setFaceUp(false);
        currentPlayer.getHand().add(cardIndex, drawnCard);

        game.setDrawnCard(null);

        logger.debug("Swapped card. Discarded: {}", discardedCard);

        // Check if discarded card is an action card
        if (discardedCard.isActionCard()) {
            logger.info("Discarded card {} is an action card", discardedCard);
            executeActionCard(game, discardedCard);
        } else {
            endTurn(game);
        }
    }

    /**
     * Discard the drawn card without swapping
     */
    public void discardDrawnCard(Game game) {
        logger.info("Player {} discarding drawn card in game {}",
                game.getCurrentPlayer().getName(), game.getGameId());

        validatePhase(game, GamePhase.AFTER_DRAW, "Can only discard during AFTER_DRAW phase");

        if (game.getDrawnCard() == null) {
            throw new InvalidMoveException("No drawn card to discard");
        }

        Card card = game.getDrawnCard();
        card.setFaceUp(true);
        game.getDiscardPile().add(card);
        game.setDrawnCard(null);

        logger.debug("Discarded drawn card: {}", card);

        // Check if it's an action card
        if (card.isActionCard()) {
            logger.info("Discarded card {} is an action card", card);
            executeActionCard(game, card);
        } else {
            endTurn(game);
        }
    }

    /**
     * Match a card from hand with the top of discard pile
     */
    public void matchCard(Game game, int playerIndex, int cardIndex) {
        logger.info("Player {} attempting to match card at index {} in game {}",
                playerIndex, cardIndex, game.getGameId());

        validatePhase(game, GamePhase.DRAW, "Can only match during DRAW phase");

        if (game.getDiscardPile().isEmpty()) {
            throw new InvalidMoveException("Discard pile is empty, cannot match");
        }

        Player player = game.getPlayers().get(playerIndex);
        Card topDiscard = game.getDiscardPile().get(game.getDiscardPile().size() - 1);
        Card playerCard = player.getCard(cardIndex);

        if (playerCard == null) {
            throw new InvalidMoveException("Invalid card index");
        }

        // Check if ranks match
        if (playerCard.getRank() != topDiscard.getRank()) {
            logger.warn("Match failed: {} does not match {}", playerCard, topDiscard);
            throw new InvalidMoveException("Card ranks do not match");
        }

        // Valid match - remove from hand and add to discard
        Card matchedCard = player.removeCard(cardIndex);
        matchedCard.setFaceUp(true);
        game.getDiscardPile().add(matchedCard);

        logger.info("Successful match: {} matched with {}", matchedCard, topDiscard);
    }

    /**
     * Execute an action card
     */
    private void executeActionCard(Game game, Card actionCard) {
        game.setPhase(GamePhase.ACTION);
        game.setActionContext(new Game.ActionContext(actionCard));

        logger.info("Executing action card: {}", actionCard);

        // Action execution will be handled by subsequent API calls
        // based on the action card type
    }

    /**
     * Handle action card step - peek at own card (7 or 8)
     */
    public void peekOwnCard(Game game, int cardIndex) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.SEVEN, Rank.EIGHT);
        
        Player currentPlayer = game.getCurrentPlayer();
        Card card = currentPlayer.getCard(cardIndex);
        
        if (card == null) {
            throw new InvalidMoveException("Invalid card index");
        }
        
        currentPlayer.markCardAsPeeked(cardIndex);
        logger.info("Player {} peeked at own card: {} at index {}", currentPlayer.getName(), card, cardIndex);
        
        // End action
        game.setActionContext(null);
        endTurn(game);
    }

    /**
     * Handle action card step - peek at opponent's card (9 or 10)
     */
    public void peekOpponentCard(Game game, int opponentIndex, int cardIndex) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.NINE, Rank.TEN);

        if (opponentIndex == game.getCurrentPlayerIndex()) {
            throw new InvalidMoveException("Cannot peek at own cards with this action");
        }

        Player opponent = game.getPlayers().get(opponentIndex);
        Card card = opponent.getCard(cardIndex);

        if (card == null) {
            throw new InvalidMoveException("Invalid card index");
        }

        // Current player sees the card (but doesn't mark it as peeked for opponent)
        logger.info("Player {} peeked at opponent {}'s card: {}",
                game.getCurrentPlayer().getName(), opponent.getName(), card);

        // End action
        game.setActionContext(null);
        endTurn(game);
    }

    /**
     * Handle Jack blind swap - step 1: select own card
     */
    public void blindSwap(Game game, int ownCardIndex, int opponentIndex, int opponentCardIndex) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.JACK);

        Game.ActionContext context = game.getActionContext();
        if (context.getStep() != 0) {
            throw new InvalidMoveException("Invalid action step");
        }

        Player currentPlayer = game.getCurrentPlayer();
        Card card = currentPlayer.getCard(ownCardIndex);

        if (card == null) {
            throw new InvalidMoveException("Invalid card index");
        }

        // Store selection
        Game.ActionContext.CardSelection selection = new Game.ActionContext.CardSelection();
        selection.setPlayerIndex(game.getCurrentPlayerIndex());
        selection.setCardIndex(ownCardIndex);
        selection.setCard(card);
        context.getSelections().add(selection);
        context.setStep(1);

        logger.debug("Jack blind swap - selected own card at index {}", ownCardIndex);

        if (context.getStep() != 1) {
            throw new InvalidMoveException("Invalid action step");
        }

        if (opponentIndex == game.getCurrentPlayerIndex()) {
            throw new InvalidMoveException("Cannot swap with own cards");
        }

        // Get first selection
        Game.ActionContext.CardSelection firstSelection = context.getSelections().get(0);
        Player player1 = game.getPlayers().get(firstSelection.getPlayerIndex());
        Player player2 = game.getPlayers().get(opponentIndex);

        // Perform blind swap
        Card card1 = player1.getHand().get(firstSelection.getCardIndex());
        Card card2 = player2.getHand().get(opponentCardIndex);

        player1.getHand().set(firstSelection.getCardIndex(), card2);
        player2.getHand().set(opponentCardIndex, card1);

        logger.info("Blind swap executed between {} and {}",
                player1.getName(), player2.getName());

        // End action
        game.setActionContext(null);
        endTurn(game);
    }

    /**
     * Handle Queen look and swap - step 1: peek at opponent's card
     */
    public void queenPeek(Game game, int opponentIndex, int cardIndex) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.QUEEN);

        if (opponentIndex == game.getCurrentPlayerIndex()) {
            throw new InvalidMoveException("Cannot peek at own cards with Queen");
        }

        Game.ActionContext context = game.getActionContext();
        Player opponent = game.getPlayers().get(opponentIndex);
        Card card = opponent.getCard(cardIndex);

        if (card == null) {
            throw new InvalidMoveException("Invalid card index");
        }

        // Store the peeked card
        Game.ActionContext.CardSelection selection = new Game.ActionContext.CardSelection();
        selection.setPlayerIndex(opponentIndex);
        selection.setCardIndex(cardIndex);
        selection.setCard(card);
        context.getSelections().add(selection);
        context.setStep(1);

        logger.info("Queen peek at opponent {}'s card: {}", opponent.getName(), card);
    }

    /**
     * Handle Queen swap - step 2: optionally swap with any card
     */
    public void queenSwap(Game game, int targetPlayerIndex, int targetCardIndex) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.QUEEN);

        Game.ActionContext context = game.getActionContext();
        if (context.getStep() != 1) {
            throw new InvalidMoveException("Must peek first");
        }

        // Get peeked card info
        Game.ActionContext.CardSelection peekedSelection = context.getSelections().get(0);
        Player peekedPlayer = game.getPlayers().get(peekedSelection.getPlayerIndex());
        Player targetPlayer = game.getPlayers().get(targetPlayerIndex);

        // Swap the peeked card with the target card
        Card peekedCard = peekedPlayer.getHand().get(peekedSelection.getCardIndex());
        Card targetCard = targetPlayer.getHand().get(targetCardIndex);

        peekedPlayer.getHand().set(peekedSelection.getCardIndex(), targetCard);
        targetPlayer.getHand().set(targetCardIndex, peekedCard);

        logger.info("Queen swap executed between {} and {}",
                peekedPlayer.getName(), targetPlayer.getName());

        // End action
        game.setActionContext(null);
        endTurn(game);
    }

    /**
     * Skip Queen swap (don't swap after peeking)
     */
    public void queenSkipSwap(Game game) {
        validatePhase(game, GamePhase.ACTION, "Not in ACTION phase");
        validateActionCard(game, Rank.QUEEN);

        logger.info("Player {} skipped Queen swap", game.getCurrentPlayer().getName());

        game.setActionContext(null);
        endTurn(game);
    }

    /**
     * Call GOLF to start the final round
     */
    public void callGolf(Game game) {
        logger.info("Player {} calling GOLF in game {}",
                game.getCurrentPlayer().getName(), game.getGameId());

        validatePhase(game, GamePhase.DRAW, "Can only call GOLF at start of turn");

        game.callGolf();
        endTurn(game);
    }

    /**
     * End the current turn and advance to next player
     */
    private void endTurn(Game game) {
        logger.debug("Ending turn for player {}", game.getCurrentPlayer().getName());

        // Check if final round is complete
        if (game.isFinalRoundComplete()) {
            game.endGame();
            return;
        }

        game.nextTurn();
    }

    /**
     * Validate the game is in the expected phase
     */
    private void validatePhase(Game game, GamePhase expected, String message) {
        if (game.getPhase() != expected) {
            throw new InvalidMoveException(message + " (current phase: " + game.getPhase() + ")");
        }
    }

    /**
     * Validate the action card is one of the expected ranks
     */
    private void validateActionCard(Game game, Rank... expectedRanks) {
        if (game.getActionContext() == null) {
            throw new InvalidMoveException("No active action");
        }

        Rank actionRank = game.getActionContext().getActionCard().getRank();
        for (Rank expected : expectedRanks) {
            if (actionRank == expected) {
                return;
            }
        }

        throw new InvalidMoveException("Invalid action for card: " + actionRank);
    }
}
