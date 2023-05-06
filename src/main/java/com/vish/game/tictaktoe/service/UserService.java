package com.vish.game.tictaktoe.service;

import com.vish.game.tictaktoe.util.UserCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCache userCache;
    private int roomId = 1;

    public int getNextRoomId() {
        return ++roomId;
    }

    public void register(String userId, String userName) {
        userCache.update(userId, userName);
    }

    public String getName(String id) {
        return userCache.get(id);
    }

    public Optional<String> getUserFromWaitingPool() {
        return userCache.getUserFromWaitingPool();
    }

    public Map<String, String> getAllUsersInPool() {
        return userCache.getAllUsersInPool();
    }

    public void removeUserFromPool(String userId) {
        userCache.removeUserFromPool(userId);
    }

    public void extendWaitingPool(String userId) {
        userCache.extendWaitingPool(userId);
    }
}
