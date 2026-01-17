package com.vish.game.tictactoe.handler;

import com.vish.game.tictactoe.util.UserCache;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionConnectedEvent;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Slf4j
@Component
@AllArgsConstructor
public class UserConnectionEventListener {

    private final UserCache userCache;

    @EventListener
    public void handleWebSocketConnectListener(final SessionConnectedEvent event) {
        log.debug("new user connected : {}", event.getUser().getName());
    }

    @EventListener
    public void handleWebSocketDisconnectListener(final SessionDisconnectEvent event) {
        final StompHeaderAccessor headerAccessor = StompHeaderAccessor.wrap(event.getMessage());
        String username = headerAccessor.getUser().getName();
        log.debug("user {} disconnected", username);
        userCache.delete(username);
    }
}
