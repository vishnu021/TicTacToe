package com.vish.game.tictactoe.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

@Slf4j
@Component
public class UserCache {

    private final Cache<String, String> userCache;
    private final Set<String> waitingPool = new HashSet<>();

    public UserCache(@Value("${game.user.maxCount}") long maximumActiveUsers,
                     @Value("${game.user.sessionDuration}") long userSessionDuration) {
        this.userCache = CacheBuilder.newBuilder()
                .maximumSize(maximumActiveUsers)
                .recordStats()
                .expireAfterWrite(Duration.ofSeconds(userSessionDuration))
                .build();
    }

    public boolean isWaitingPoolEmpty() {
        return waitingPool.size()==0;
    }

    public void extendWaitingPool(String userId) {
        waitingPool.add(userId);
    }
    public void removeUserFromPool(String userId) {
        waitingPool.remove(userId);
    }

    public Optional<String> getUserFromWaitingPool() {
        if(!isWaitingPoolEmpty()) {
            return waitingPool.stream().findAny();
        }
        return Optional.empty();
    }

    public Map<String, String> getAllUsersInPool() {
        return waitingPool.stream().collect(Collectors.toMap(x -> x, this::get));
    }

    public void update(String userId, String username) {
        userCache.put(userId, username);
    }

    public String get(String userId) {
        return userCache.getIfPresent(userId);
    }

    public void delete(String userId) {
        try {
            userCache.invalidate(userId);
            waitingPool.remove(userId);
        } catch (Exception e){
            log.error("Error deleting {}", userId,e);
        }
    }

    public boolean find(String userId) {
        return userCache.getIfPresent(userId) != null;
    }
}
