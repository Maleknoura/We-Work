package org.wora.we_work.controllers;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.wora.we_work.dto.equipement.EquipementRequestDTO;
import org.wora.we_work.dto.equipement.EquipementResponseDTO;
import org.wora.we_work.services.api.EquipementService;

@RestController
@RequestMapping("/api/equipments")
@RequiredArgsConstructor
@Slf4j
public class EquipementController {
    private final EquipementService equipementService;

    @PostMapping
    @PreAuthorize("hasAuthority('EQUIPEMENT_CREATE')")
    public ResponseEntity<EquipementResponseDTO> create(@Valid @RequestBody EquipementRequestDTO requestDTO) {
        return ResponseEntity.ok(equipementService.create(requestDTO));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPEMENT_UPDATE')")
    public ResponseEntity<EquipementResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody EquipementRequestDTO requestDTO) {
        return ResponseEntity.ok(equipementService.update(id, requestDTO));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPEMENT_DELETE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        equipementService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EQUIPEMENT_READ')")
    public ResponseEntity<EquipementResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(equipementService.getById(id));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('EQUIPEMENT_READ')")
    public ResponseEntity<Page<EquipementResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(equipementService.getAll(pageable));
    }

    @GetMapping("/espace/{espaceId}")
    @PreAuthorize("hasAuthority('EQUIPEMENT_READ')")
    public ResponseEntity<Page<EquipementResponseDTO>> getAllByEspace(
            @PathVariable Long espaceId,
            Pageable pageable) {
        return ResponseEntity.ok(equipementService.getAllByEspaceCoworking(espaceId, pageable));
    }
}





