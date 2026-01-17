package com.vish.game.tictactoe.model;

/**
 * Record for game starter configuration.
 * Immutable data carrier for initiating a game between two players.
 */
public record GameStarterDTO(
    String userId,
    String opponentId,
    String roomId
) {}
