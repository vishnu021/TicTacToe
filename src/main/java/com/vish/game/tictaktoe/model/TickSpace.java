package com.vish.game.tictaktoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TickSpace {
    private int id;
    private boolean clicked;
    private boolean crossed;
}
