package com.vish.game.tictactoe.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameStarterDTO {
    private String userId;
    private String opponentId;
    private String roomId;
}
