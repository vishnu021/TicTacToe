package com.vish.game.tictaktoe.controller;

import com.vish.game.tictaktoe.model.GameStepDTO;
import com.vish.game.tictaktoe.model.UserRegisterDTO;
import com.vish.game.tictaktoe.service.GameService;
import com.vish.game.tictaktoe.service.UserSocketService;
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

    final UserSocketService userSocketService;
    final GameService gameService;

    @MessageMapping("/register")
    @SendToUser("/topic/response")
    public UserRegisterDTO register(final UserRegisterDTO userRegisterDTO,
                                                 final Principal principal) {

        log.info("Request to register from anonymous user {}, DTO : {}",
                principal.getName(), userRegisterDTO);
        userSocketService.register(principal.getName(), userRegisterDTO.getUser());
        return userRegisterDTO;
    }

    @MessageMapping("/step")
    @SendToUser("/topic/game-ws")
    public GameStepDTO step(final GameStepDTO gameStepDTO,
                            final Principal principal) {

        log.info("Request to step from user {}, DTO : {}",
                principal.getName(), gameStepDTO);
        // TODO check for valid id
        gameService.step(gameStepDTO, principal);
        return gameStepDTO;
    }

}
