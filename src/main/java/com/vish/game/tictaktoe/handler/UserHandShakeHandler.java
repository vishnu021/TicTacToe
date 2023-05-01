package com.vish.game.tictaktoe.handler;

import com.sun.security.auth.UserPrincipal;
import com.vish.game.tictaktoe.UserCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.support.DefaultHandshakeHandler;

import java.security.Principal;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@AllArgsConstructor
public class UserHandShakeHandler extends DefaultHandshakeHandler {

    private final UserCache userCache;

    @Override
    protected Principal determineUser(final ServerHttpRequest request,
                                      final WebSocketHandler wsHandler,
                                      final Map<String, Object> attributes) {
        final String userId = UUID.randomUUID().toString();
        log.info("creating new userId for anonymous user : {}", userId);
        userCache.update(userId, "");
        return new UserPrincipal(userId);
    }
}
