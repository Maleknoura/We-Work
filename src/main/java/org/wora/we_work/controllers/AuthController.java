package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.wora.we_work.dto.user.AuthResponseDTO;
import org.wora.we_work.dto.user.LoginRequestDTO;
import org.wora.we_work.dto.user.RegisterRequestDTO;
import org.wora.we_work.services.api.AuthService;
import org.wora.we_work.services.api.UserService;

import javax.management.relation.RoleNotFoundException;


@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) throws RoleNotFoundException {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }
    @PutMapping("/{userId}/ban")
    public ResponseEntity<String> banUser(@PathVariable Long userId) {
        userService.banUser(userId);
        return ResponseEntity.ok("Utilisateur banni avec succès.");
    }

    @PutMapping("/{userId}/unban")
    public ResponseEntity<String> unbanUser(@PathVariable Long userId) {
        userService.unbanUser(userId);
        return ResponseEntity.ok("Utilisateur débanni avec succès.");
    }

    @GetMapping("/{username}/isBanned")
    public ResponseEntity<Boolean> isUserBanned(@PathVariable String username) {
        boolean banned = userService.isUserBanned(username);
        return ResponseEntity.ok(banned);
    }

}