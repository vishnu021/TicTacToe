package com.vish.game.tictaktoe.service;

import com.vish.game.tictaktoe.UserCache;
import com.vish.game.tictaktoe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {

    private final UserCache userCache;
    final private BroadCastService broadCastService;

    // TODO maintain match pool as well
    // TODO autoregister player not registered
    private final Map<String, GameBoard> activeGame = new HashMap<>();

    private final List<List<Integer>> winningIndices = List.of(List.of(0,1,2), List.of(3,4,5), List.of(6,7,8),
            List.of(0,3,6), List.of(1,4,7), List.of(2,5,8), List.of(0,4,8), List.of(2,4,6));

    public Optional<GameStepDTO> startGame(GameStarterDTO gameStarterConfig) {
        String userId = gameStarterConfig.getUserId();
        String opponentId = gameStarterConfig.getOpponentId();
        String roomId = gameStarterConfig.getRoomId();
        log.info("Starting game of user ({}){} with opponent ({}){} ", userCache.get(userId), userId,
                userCache.get(opponentId), opponentId);
        String gameKey = generateKey(userId, opponentId);

        GameBoard board;

        if(activeGame.containsKey(gameKey)) {
            log.info("game already existing for user {} with opponent {}", userId, opponentId);
            board = activeGame.get(gameKey);
        } else {
            board = new GameBoard(Integer.parseInt(roomId), userId);
            activeGame.put(gameKey, board);
        }
        return Optional.of(GameStepDTO.builder(userCache.get(userId),
                        userId,
                        userCache.get(opponentId),
                        opponentId)
                        .board(board)
                .build());
    }

    public Optional<GameStepDTO> step(final GameStepDTO gameStepDTO,
                                      final Principal principal) {
        //TODO: check for valid player and opponent id
        String playerName = gameStepDTO.getPlayerName();
        String playerId = gameStepDTO.getPlayerId();
        String opponentName = gameStepDTO.getOpponentName();
        String opponentId = gameStepDTO.getOpponentId();
        String activePlayer = gameStepDTO.getBoard().getActivePlayerId();

        if(!Objects.equals(principal.getName(), playerId)) {
            log.warn("Incorrect state of game board for user : {}", principal.getName());
            // TODO : correct the state of game or disconnect user
            return Optional.of(gameStepDTO);
        }

        if(!Objects.equals(playerId, activePlayer)) {
            log.warn("Got step request from non-active player : {}", playerId);
            // TODO : correct the state of game or disconnect user
            return Optional.of(gameStepDTO);
        }

        if(!isGameStepValid(gameStepDTO)) {
            log.warn("Game step is not valid for player : {}", playerId);
            // TODO : correct the state of game or disconnect user
            return Optional.of(gameStepDTO);
        }

        String gameKey = generateKey(playerId, opponentId);
        GameBoard board = gameStepDTO.getBoard();
        board.setActivePlayerId(opponentId);

        activeGame.put(gameKey, board);

        GameStepDTO playerGameState = GameStepDTO
                .builder(playerName, playerId, opponentName, opponentId)
                .board(board)
                .build();

        GameStepDTO opponentGameState = GameStepDTO
                .builder(opponentName, opponentId, playerName, playerId)
                .board(board)
                .build();

        if(isGameCompleted(gameStepDTO)) {
            log.info(" {} won the game", gameStepDTO.getWinningPlayer());
            playerGameState.setWinningPlayer(gameStepDTO.getWinningPlayer());
            opponentGameState.setWinningPlayer(gameStepDTO.getWinningPlayer());
        }

        broadCastService.updateUser(playerId, playerGameState);
        broadCastService.updateUser(opponentId, opponentGameState);

        return Optional.of(playerGameState);
    }

    private boolean isGameCompleted(GameStepDTO gameStepDTO) {
        List<TickSpace> tickSpaces = gameStepDTO.getBoard().getTickSpaces();

        List<Integer> crossedIds = tickSpaces.stream()
                .filter(tick -> tick.isClicked())
                .filter(tick -> tick.isCrossed())
                .map(tick -> tick.getId())
                .toList();

        if(isWinningPattern(crossedIds)) {
            gameStepDTO.setWinningPlayer(gameStepDTO.getCrossPlayerId());
            return true;
        }

        List<Integer> circleIds = tickSpaces.stream()
                .filter(tick -> tick.isClicked())
                .filter(tick -> !tick.isCrossed())
                .map(tick -> tick.getId())
                .toList();

        if(isWinningPattern(circleIds)) {
            gameStepDTO.setWinningPlayer(gameStepDTO.getCirclePlayerId());
            return true;
        }
        return false;
    }


    private boolean isWinningPattern(List<Integer> ids) {
        return winningIndices.stream().anyMatch(ids::containsAll);
    }

    // TODO
    private boolean isGameStepValid(GameStepDTO gameStepDTO) {
        log.debug("game step : {}", gameStepDTO);
        return true;
    }

    private String generateKey(String username, String opponentName) {
        if(username.compareTo(opponentName) >0 ) {
            return username + "|" + opponentName;
        } else {
            return opponentName + "|" + username;
        }
    }
}
