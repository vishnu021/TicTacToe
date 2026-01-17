package com.vish.game.tictactoe.service;

import com.vish.game.tictactoe.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import static com.vish.game.tictactoe.util.GameStateHelper.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class GameService {
    private final BroadCastService broadCastService;
    private final UserService userService;
    private final Map<String, GameBoard> activeGame = new ConcurrentHashMap<>();

    public Optional<GameStepDTO> startGame(GameStarterDTO gameStarterConfig) {
        String userId = gameStarterConfig.getUserId();
        String opponentId = gameStarterConfig.getOpponentId();
        String roomId = gameStarterConfig.getRoomId();
        log.info("Starting game of user ({}){} with opponent ({}){} ", userService.getName(userId), userId,
                userService.getName(opponentId), opponentId);

        String gameKey = generateKey(userId, opponentId);
        GameBoard board = getGameBoard(userId, opponentId, roomId, gameKey);

        return Optional.of(GameStepDTO.builder(userService.getName(userId),
                        userId,
                        userService.getName(opponentId),
                        opponentId)
                        .board(board)
                .build());
    }

    boolean isGameInProgress(GameBoard gameBoard) {
        return gameBoard.getTickSpaces().stream().anyMatch(TickSpace::isClicked);
    }

    private GameBoard getGameBoard(String userId, String opponentId, String roomId, String gameKey) {
        if(activeGame.containsKey(gameKey) && !isGameInProgress(activeGame.get(gameKey))) {
            log.debug("Game already existing for user {} with opponent {}", userId, opponentId);
            return activeGame.get(gameKey);
        } else {
            GameBoard board = new GameBoard(Integer.parseInt(roomId), userId);
            activeGame.put(gameKey, board);
            return board;
        }
    }

    public Optional<GameStepDTO> step(final GameStepDTO gameStepDTO,
                                      final Principal principal) {
        String playerName = gameStepDTO.getPlayerName();
        String playerId = gameStepDTO.getPlayerId();
        String opponentName = gameStepDTO.getOpponentName();
        String opponentId = gameStepDTO.getOpponentId();

        if (!isGameStateValid(gameStepDTO, principal)){
            return Optional.of(gameStepDTO);
        }

        String gameKey = generateKey(playerId, opponentId);
        GameBoard board = gameStepDTO.getBoard();
        board.setActivePlayerId(opponentId);

        activeGame.put(gameKey, board);
        GameStepDTO playerGameState = updateGameStateAndBroadcast(gameStepDTO, playerName, playerId, opponentName, opponentId, board);

        return Optional.of(playerGameState);
    }

    private GameStepDTO updateGameStateAndBroadcast(GameStepDTO gameStepDTO, String playerName, String playerId,
                                                    String opponentName, String opponentId, GameBoard board) {
        Optional<String> winningPlayerIdOptional = getWinningPlayerId(gameStepDTO);

        broadCastService.createDTOAndBroadcast(board, opponentName, opponentId, playerName, playerId,
                winningPlayerIdOptional);
        return broadCastService.createDTOAndBroadcast(board, playerName, playerId, opponentName,
                opponentId, winningPlayerIdOptional);
    }

    public void attemptToStartGame(String userId) {
        Optional<String> opponentIdOptional = userService.getUserFromWaitingPool();

        if(opponentIdOptional.isPresent()) {
            String opponentId = opponentIdOptional.get();
            userService.removeUserFromPool(opponentId);
            GameStarterDTO gameStarterDTO = new GameStarterDTO(userId, opponentId,
                    String.valueOf(userService.getNextRoomId()));
            Optional<GameStepDTO> gameStepOptional =  startGame(gameStarterDTO);
            broadCastService.startGameAndBroadcast(gameStepOptional);
        } else {
            userService.extendWaitingPool(userId);
        }
    }

    public Set<String> getAllActiveGames() {
        return activeGame.keySet();
    }
}
