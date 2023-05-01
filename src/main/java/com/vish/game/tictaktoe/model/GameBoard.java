package com.vish.game.tictaktoe.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Data
@NoArgsConstructor
public class GameBoard {

    private Integer roomId;

    private List<TickSpace> tickSpaces;
    private String crossPlayerId;
    private String activePlayerId;

    private int markedTicks;

    public GameBoard(Integer roomId, String player1) {
        this.roomId = roomId;
        this.tickSpaces = IntStream.rangeClosed(0,8).boxed()
                .map(i -> new TickSpace(i, false, false))
                .collect(Collectors.toList());
        this.crossPlayerId = player1;
        this.activePlayerId = player1;
        this.markedTicks = 0;
    }
}
