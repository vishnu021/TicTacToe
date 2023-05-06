package com.vish.game.tictaktoe.controller;

import com.vish.game.tictaktoe.service.GameService;
import com.vish.game.tictaktoe.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/status")
public class StatusController {

    private final UserService userService;
    private final GameService gameService;

    @GetMapping("/user-pool")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity getUsersInPool() {
        return ResponseEntity.ok(userService.getAllUsersInPool());
    }

    @GetMapping("/active-games")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseEntity getActiveGames() {
        return ResponseEntity.ok(gameService.getAllActiveGames());
    }
}
