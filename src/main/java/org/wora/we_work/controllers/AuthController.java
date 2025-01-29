package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.wora.we_work.dto.*;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.wora.we_work.services.api.AuthService;

import javax.management.relation.RoleNotFoundException;

import static org.hibernate.query.sqm.tree.SqmNode.log;

@RestController
@Slf4j
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody RegisterRequestDTO request) throws RoleNotFoundException {
        AuthResponseDTO response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @PostMapping("/complete-profile/client")
    @PreAuthorize("hasRole('CLIENT')")
    public ResponseEntity<Void> completeClientProfile(@Valid @RequestBody ClientProfileDTO profileDTO) {
        authService.completeClientProfile(profileDTO);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/complete-profile/proprietaire")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<Void> completeProprietaireProfile(@Valid @RequestBody ProprietaireProfileDTO profileDTO) {
        authService.completeProprietaireProfile(profileDTO);
        return ResponseEntity.ok().build();
    }
}