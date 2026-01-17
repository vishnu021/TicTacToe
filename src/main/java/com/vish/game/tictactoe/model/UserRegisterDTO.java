package com.vish.game.tictactoe.model;

/**
 * Record for user registration data.
 * Immutable data carrier for user registration requests.
 */
public record UserRegisterDTO(
    String user,
    boolean createNewRoom
) {}
