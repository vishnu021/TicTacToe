package com.vish.game.tictaktoe.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.Objects;

@Data
@Builder(builderMethodName = "optionalBuilder")
@AllArgsConstructor
public class GameStepDTO {
    private String playerName;
    private String playerId;
    private String opponentName;
    private String opponentId;
    private GameBoard board;
    private String winningPlayer;

    public static GameStepDTOBuilder builder(String playerName, String playerId, String opponentName, String opponentId) {
        return optionalBuilder()
                .playerName(playerName)
                .playerId(playerId)
                .opponentName(opponentName)
                .opponentId(opponentId);
    }

    public String getCrossPlayerId() {
        return board.getCrossPlayerId();
    }

    public String getCirclePlayerId() {
        if(Objects.equals(playerId, board.getCrossPlayerId())) {
            return opponentId;
        } else {
            return playerId;
        }
    }
}
