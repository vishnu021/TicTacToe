package com.vish.game.tictactoe.model;

/**
 * Record representing a single cell in the Tic-Tac-Toe board.
 * Immutable - create new instances for state changes.
 */
public record TickSpace(
    int id,
    boolean clicked,
    boolean crossed
) {}
