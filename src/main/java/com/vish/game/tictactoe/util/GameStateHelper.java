package com.vish.game.tictactoe.util;

import com.vish.game.tictactoe.model.GameStepDTO;
import com.vish.game.tictactoe.model.TickSpace;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class GameStateHelper {
    private final static List<List<Integer>> winningIndices = List.of(List.of(0, 1, 2), List.of(3, 4, 5), List.of(6, 7, 8),
            List.of(0, 3, 6), List.of(1, 4, 7), List.of(2, 5, 8), List.of(0, 4, 8), List.of(2, 4, 6));

    public static Optional<String> getWinningPlayerId(GameStepDTO gameStepDTO) {
        List<TickSpace> tickSpaces = gameStepDTO.getBoard().getTickSpaces();

        List<Integer> crossedIds = getTickIds(tickSpaces, TickSpace::isCrossed);
        if(isWinningPattern(crossedIds)) {
            return Optional.of(gameStepDTO.getCrossPlayerId());
        }

        List<Integer> circleIds =  getTickIds(tickSpaces, tick -> !tick.isCrossed());
        if(isWinningPattern(circleIds)) {
            return Optional.of(gameStepDTO.getCirclePlayerId());
        }

        return Optional.empty();
    }

    public static String generateKey(String username, String opponentName) {
        if(username.compareTo(opponentName) >0 ) {
            return username + "|" + opponentName;
        } else {
            return opponentName + "|" + username;
        }
    }

    //TODO: check for valid opponent id
    public static boolean isGameStateValid(GameStepDTO gameStepDTO, Principal principal) {
        String playerId = gameStepDTO.getPlayerId();
        String activePlayer = gameStepDTO.getBoard().getActivePlayerId();

        if(!Objects.equals(principal.getName(), playerId)) {
            log.warn("Incorrect state of game board for user : {}", principal.getName());
            // TODO : correct the state of game or disconnect user
            return false;
        }

        if(!Objects.equals(playerId, activePlayer)) {
            log.warn("Got step request from non-active player : {}", playerId);
            // TODO : correct the state of game or disconnect user
            return false;
        }

        if(!isGameStepValid(gameStepDTO)) {
            log.warn("Game step is not valid for player : {}", playerId);
            // TODO : correct the state of game or disconnect user
            return false;
        }
        return true;
    }

    private static boolean isGameStepValid(GameStepDTO gameStepDTO) {
        log.debug("checking game step for dto : {}", gameStepDTO);
        List<TickSpace> tickSpaces = gameStepDTO.getBoard().getTickSpaces();

        // Validate board has exactly 9 spaces
        if (tickSpaces == null || tickSpaces.size() != 9) {
            log.warn("Invalid board size: {}", tickSpaces != null ? tickSpaces.size() : "null");
            return false;
        }

        // Count clicked spaces to ensure valid game progression
        long clickedCount = tickSpaces.stream().filter(TickSpace::isClicked).count();
        long crossCount = tickSpaces.stream().filter(t -> t.isClicked() && t.isCrossed()).count();
        long circleCount = clickedCount - crossCount;

        // Cross and circle counts should differ by at most 1
        if (Math.abs(crossCount - circleCount) > 1) {
            log.warn("Invalid move count - crosses: {}, circles: {}", crossCount, circleCount);
            return false;
        }

        return true;
    }

    private static List<Integer> getTickIds(final List<TickSpace> tickSpaces, Predicate<TickSpace> tickType) {
        return tickSpaces.stream()
                .filter(TickSpace::isClicked)
                .filter(tickType)
                .map(TickSpace::getId)
                .toList();
    }

    private static boolean isWinningPattern(List<Integer> ids) {
        return winningIndices.stream().anyMatch(ids::containsAll);
    }
}
