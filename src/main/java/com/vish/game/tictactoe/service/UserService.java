package com.vish.game.tictactoe.service;

import com.vish.game.tictactoe.util.UserCache;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserCache userCache;
    private final AtomicInteger roomId = new AtomicInteger(1);

    public int getNextRoomId() {
        return roomId.incrementAndGet();
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
