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

@Slf4j
@Service
@RequiredArgsConstructor
public class BroadCastService {

    private static final int GAME_START_DELAY_MS = 2000;

    private final SimpMessagingTemplate messagingTemplate;
    private final WebSocketProperties webSocketProps;

    /**
     * Broadcasts game start to both players with a delay for frontend loading.
     * Uses virtual threads via @Async for non-blocking execution.
     */
    @Async
    public void startGameAndBroadcast(Optional<GameStepDTO> gameStepOptional) {
        gameStepOptional.ifPresent(gameStep -> {
            try {
                // Allow frontend to load components
                Thread.sleep(GAME_START_DELAY_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.warn("Game start delay interrupted", e);
                return;
            }

            // Notify first player
            startGame(gameStep.getPlayerId(), gameStep);

            // Notify opponent with swapped player/opponent
            GameStepDTO opponentGameStep = GameStepDTO.builder(
                    gameStep.getOpponentName(),
                    gameStep.getOpponentId(),
                    gameStep.getPlayerName(),
                    gameStep.getPlayerId()
            ).board(gameStep.getBoard()).build();

            startGame(opponentGameStep.getPlayerId(), opponentGameStep);
        });
    }

    public GameStepDTO createDTOAndBroadcast(GameBoard board, String playerName, String playerId,
                                             String opponentName, String opponentId,
                                             Optional<String> winningPlayerIdOptional) {
        GameStepDTO playerGameState = GameStepDTO
                .builder(playerName, playerId, opponentName, opponentId)
                .board(board)
                .build();

        winningPlayerIdOptional.ifPresent(playerGameState::setWinningPlayer);
        updateUser(playerId, playerGameState);
        return playerGameState;
    }

    private void startGame(String userId, GameStepDTO gameStepDTO) {
        log.debug("Sending game start to user: {} with game: {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, webSocketProps.getGameStartTopic(), gameStepDTO);
    }

    private void updateUser(String userId, GameStepDTO gameStepDTO) {
        log.debug("Sending game step to user: {} with game: {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, webSocketProps.getGameStepTopic(), gameStepDTO);
    }
}
