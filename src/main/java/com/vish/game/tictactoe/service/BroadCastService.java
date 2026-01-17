package com.vish.game.tictactoe.service;

import com.vish.game.tictactoe.config.WebSocketProperties;
import com.vish.game.tictactoe.model.GameBoard;
import com.vish.game.tictactoe.model.GameStepDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadCastService {

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties webSocketProps;

    @Async
    public void startGameAndBroadcast(Optional<GameStepDTO> gameStepOptional) {
        if(gameStepOptional.isPresent()) {
            GameStepDTO gameStep = gameStepOptional.get();

            // Use async delay to allow frontend to load components
            CompletableFuture.delayedExecutor(2, TimeUnit.SECONDS).execute(() -> {
                startGame(gameStep.getPlayerId(), gameStep);
                GameStepDTO opponentGameStep = GameStepDTO.builder(gameStep.getOpponentName(),
                        gameStep.getOpponentId(),
                        gameStep.getPlayerName(),
                        gameStep.getPlayerId()).board(gameStep.getBoard()).build();
                startGame(opponentGameStep.getPlayerId(), opponentGameStep);
            });
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
