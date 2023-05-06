package com.vish.game.tictaktoe.service;

import com.vish.game.tictaktoe.config.WebSocketProperties;
import com.vish.game.tictaktoe.model.GameBoard;
import com.vish.game.tictaktoe.model.GameStepDTO;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
@AllArgsConstructor
public class BroadCastService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties webSocketProps;

    public void startGameAndBroadcast(Optional<GameStepDTO> gameStepOptional) {
        if(gameStepOptional.isPresent()) {
            // Adding sleep to give time to front end to load components.
            // TODO add a queuing system
            try {
                Thread.sleep(2500);
            } catch (InterruptedException e) {
                log.error("Failed to sleep", e);
            }
            GameStepDTO gameStep = gameStepOptional.get();
            startGame(gameStep.getPlayerId(), gameStep);
            gameStep = GameStepDTO.builder(gameStep.getOpponentName(),
                    gameStep.getOpponentId(),
                    gameStep.getPlayerName(),
                    gameStep.getPlayerId()).board(gameStep.getBoard()).build();
            startGame(gameStep.getPlayerId(), gameStep);
        }
    }

    public GameStepDTO createDTOAndBroadcast(GameBoard board, String playerName, String playerId, String opponentName,
                                             String opponentId, Optional<String> winningPlayerIdOptional) {
        GameStepDTO playerGameState = GameStepDTO
                .builder(playerName, playerId, opponentName, opponentId)
                .board(board)
                .build();

        winningPlayerIdOptional.ifPresent(playerGameState::setWinningPlayer);
        updateUser(playerId, playerGameState);
        return playerGameState;
    }

    private void startGame(String userId, GameStepDTO gameStepDTO) {
        log.debug("Sending message to userID : {} to start the game {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, webSocketProps.getGameStartTopic(), gameStepDTO);
    }

    private void updateUser(String userId, GameStepDTO gameStepDTO) {
        log.debug("Sending message to userID : {} to step the game {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, webSocketProps.getGameStepTopic(), gameStepDTO);
    }
}
