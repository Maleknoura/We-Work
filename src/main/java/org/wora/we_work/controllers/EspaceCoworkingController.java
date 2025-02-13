package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.services.api.EspaceCoworkingService;

import java.util.List;

@RestController
@RequestMapping("/api/espaces")
@RequiredArgsConstructor
public class EspaceCoworkingController {
    private final EspaceCoworkingService espaceCoworkingService;

    @PostMapping
    @PreAuthorize("hasAuthority('ESPACE_CREATE')")
    public ResponseEntity<EspaceCoworkingResponseDTO> create(@Valid @RequestBody EspaceCoworkingRequestDTO requestDTO) {
        return ResponseEntity.ok(espaceCoworkingService.create(requestDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ESPACE_UPDATE')")
    public ResponseEntity<EspaceCoworkingResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EspaceCoworkingRequestDTO requestDTO) {
        return ResponseEntity.ok(espaceCoworkingService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ESPACE_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        espaceCoworkingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('ESPACE_READ')")
    public ResponseEntity<EspaceCoworkingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(espaceCoworkingService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ESPACE_READ')")
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(espaceCoworkingService.getAll(pageable));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('ESPACE_READ')")
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getAllByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(espaceCoworkingService.getAllByProprietaire(userId, pageable));
    }
}
