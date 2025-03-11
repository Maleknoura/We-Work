package org.wora.we_work.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingRequestDTO;
import org.wora.we_work.dto.espaceCoworking.EspaceCoworkingResponseDTO;
import org.wora.we_work.entities.EspaceCoworking;
import org.wora.we_work.services.api.EspaceCoworkingService;
import org.wora.we_work.services.impl.CloudinaryService;

import java.util.List;


@RestController
@RequestMapping("/api/espaces")
@RequiredArgsConstructor
public class EspaceCoworkingController {
    private final EspaceCoworkingService espaceCoworkingService;
    private final CloudinaryService cloudinaryService;

    @GetMapping
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getAll(Pageable pageable) {
        return ResponseEntity.ok(espaceCoworkingService.getAll(pageable));
    }
    @PostMapping
    @Secured("ESPACE_CREATE")
    public ResponseEntity<EspaceCoworkingResponseDTO> create(
            @RequestPart("espace") @Valid EspaceCoworkingRequestDTO requestDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        if (files != null && !files.isEmpty()) {
            List<String> imageUrls = cloudinaryService.uploadMultipleFiles(files);
            requestDTO = new EspaceCoworkingRequestDTO(
                    requestDTO.nom(),
                    requestDTO.adresse(),
                    requestDTO.description(),
                    requestDTO.prixParJour(),
                    requestDTO.capacite(),
                    imageUrls
            );
        }

        EspaceCoworkingResponseDTO responseDTO = espaceCoworkingService.create(requestDTO);
        return ResponseEntity.ok(responseDTO);
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
    public ResponseEntity<EspaceCoworkingResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(espaceCoworkingService.getById(id));
    }


    @GetMapping("/user/{userId}")
    public ResponseEntity<Page<EspaceCoworkingResponseDTO>> getAllByUser(
            @PathVariable Long userId,
            Pageable pageable) {
        return ResponseEntity.ok(espaceCoworkingService.getAllByProprietaire(userId, pageable));
    }


}
