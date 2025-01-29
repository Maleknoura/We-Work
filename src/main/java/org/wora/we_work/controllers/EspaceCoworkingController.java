package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.services.api.EspaceCoworkingService;

import java.util.List;

@RestController
@RequestMapping("/api/espace-coworking")
@RequiredArgsConstructor
@Validated
public class EspaceCoworkingController {
    private final EspaceCoworkingService espaceCoworkingService;

    @PostMapping
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<EspaceCoworkingResponseDTO> createEspace(
            @Valid @RequestBody EspaceCoworkingRequestDTO requestDTO) {
        EspaceCoworkingResponseDTO createdEspace = espaceCoworkingService.create(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(createdEspace);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<EspaceCoworkingResponseDTO> updateEspace(
            @PathVariable Long id,
            @Valid @RequestBody EspaceCoworkingRequestDTO requestDTO) {
        EspaceCoworkingResponseDTO updatedEspace = espaceCoworkingService.update(id, requestDTO);
        return ResponseEntity.ok(updatedEspace);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('PROPRIETAIRE')")
    public ResponseEntity<Void> deleteEspace(@PathVariable Long id) {
        espaceCoworkingService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<EspaceCoworkingResponseDTO> getEspaceById(@PathVariable Long id) {
        EspaceCoworkingResponseDTO espace = espaceCoworkingService.getById(id);
        return ResponseEntity.ok(espace);
    }

    @GetMapping
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getAllEspaces(
            @PageableDefault(size = 20, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<EspaceCoworkingResponseDTO> espaces = espaceCoworkingService.getAll(pageable);
        return ResponseEntity.ok(espaces);
    }

    @GetMapping("/proprietaire/{proprietaireId}")
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getEspacesByProprietaire(
            @PathVariable Long proprietaireId,
            @PageableDefault(size = 20, sort = "dateCreation", direction = Sort.Direction.DESC) Pageable pageable) {
        Page<EspaceCoworkingResponseDTO> espaces = espaceCoworkingService.getAllByProprietaire(proprietaireId, pageable);
        return ResponseEntity.ok(espaces);
    }
}
