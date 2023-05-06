package com.vish.game.tictaktoe.controller;

import com.vish.game.tictaktoe.model.GameStepDTO;
import com.vish.game.tictaktoe.model.UserRegisterDTO;
import com.vish.game.tictaktoe.service.GameService;
import com.vish.game.tictaktoe.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.annotation.SendToUser;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@AllArgsConstructor
public class GameWebSockerController {

    private final UserService userService;
    private final GameService gameService;

    @MessageMapping("/register")
    @SendToUser("/topic/response")
    public UserRegisterDTO register(final UserRegisterDTO userRegisterDTO, final Principal principal) {
        log.debug("Request to register from anonymous user {}, DTO : {}", principal.getName(), userRegisterDTO);
        userService.register(principal.getName(), userRegisterDTO.getUser());
        gameService.attemptToStartGame(principal.getName());
        return userRegisterDTO;
    }

    @MessageMapping("/step")
    @SendToUser("/topic/game-ws")
    public GameStepDTO step(final GameStepDTO gameStepDTO, final Principal principal) {
        log.debug("Request to step from user {}, DTO : {}", principal.getName(), gameStepDTO);
        gameService.step(gameStepDTO, principal);
        return gameStepDTO;
    }
}
