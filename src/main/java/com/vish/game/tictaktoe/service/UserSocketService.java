package com.vish.game.tictaktoe.service;

import com.vish.game.tictaktoe.UserCache;
import com.vish.game.tictaktoe.model.GameStarterDTO;
import com.vish.game.tictaktoe.model.GameStepDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserSocketService {

    private final UserCache userCache;
    private final BroadCastService broadCastService;
    private final GameService gameService;
    int roomId = 1;

    public void register(String userId, String userName) {
        userCache.update(userId, userName);
        if(userCache.isWaitingPoolEmpty()) {
            userCache.extendWaitingPool(userId);
        } else {
            String opponentId = userCache.getUserFromWaitingPool().get();
            userCache.removeUserFromPool(opponentId);
            GameStarterDTO gameStarterDTO = new GameStarterDTO(userId, opponentId, String.valueOf(roomId++));
            Optional<GameStepDTO> gameStepOptional =  gameService.startGame(gameStarterDTO);
            broadCastService.startGame(gameStepOptional);
        }
    }
}
