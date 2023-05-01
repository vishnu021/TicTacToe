package com.vish.game.tictaktoe.service;

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

    public void startGame(Optional<GameStepDTO> gameStepOptional) {
        if(gameStepOptional.isPresent()) {
            GameStepDTO gameStep = gameStepOptional.get();
            startGame(gameStep.getPlayerId(), gameStep);
            gameStep = GameStepDTO.builder(gameStep.getOpponentName(),
                    gameStep.getOpponentId(),
                    gameStep.getPlayerName(),
                    gameStep.getPlayerId()).board(gameStep.getBoard()).build();
            startGame(gameStep.getPlayerId(), gameStep);
        }
    }

    public void startGame(String userId, GameStepDTO gameStepDTO) {
        log.info("Sending message to userID : {} to start the game {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, "/topic/game-start", gameStepDTO);
    }

    public void updateUser(String userId, GameStepDTO gameStepDTO) {
        log.info("Sending message to userID : {} to step the game {}", userId, gameStepDTO);
        messagingTemplate.convertAndSendToUser(userId, "/topic/game-ws", gameStepDTO);
    }
}
