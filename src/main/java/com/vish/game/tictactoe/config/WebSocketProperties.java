package com.vish.game.tictactoe.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("ws.tick")
public class WebSocketProperties {
    private String endpoint;
    private String destinationPrefix;
    private String appPrefix;
    private String gameStartTopic;
    private String gameStepTopic;
}
