package com.vish.game.tictaktoe.config;

import com.vish.game.tictaktoe.handler.UserHandShakeHandler;
import lombok.AllArgsConstructor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@AllArgsConstructor
@EnableWebSocketMessageBroker
@EnableConfigurationProperties(WebSocketProperties.class)
public class MessageBrokerConfig implements WebSocketMessageBrokerConfigurer {

    private final UserHandShakeHandler handShakeHandler;
    private final WebSocketProperties webSocketProps;

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint(webSocketProps.getEndpoint())
                .setAllowedOriginPatterns("*")
                .setHandshakeHandler(handShakeHandler)
                .withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        registry.enableSimpleBroker(webSocketProps.getDestinationPrefix());
        registry.setApplicationDestinationPrefixes(webSocketProps.getAppPrefix());
    }
}
